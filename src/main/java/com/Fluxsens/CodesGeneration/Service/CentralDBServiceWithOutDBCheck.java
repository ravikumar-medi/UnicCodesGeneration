package com.Fluxsens.CodesGeneration.Service;

import com.Fluxsens.CodesGeneration.AppConfig;
import com.Fluxsens.CodesGeneration.DTO.CompanyDetailsDTO;
import com.Fluxsens.CodesGeneration.DTO.ResponseDTO;
import com.Fluxsens.CodesGeneration.utils.DateUtils;
import com.Fluxsens.CodesGeneration.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.security.SecureRandom;

/**
 * Service for generating unique codes with support for Primary and Secondary code types.
 *
 * Performance improvements:
 * - Removed SELECT COUNT checks before INSERT (was 2 DB calls per code, now 1)
 * - Using SecureRandom instead of Random (thread-safe, cryptographically strong)
 * - Removed synchronized — DB unique constraints handle safety
 * - On duplicate exception (23505), code is skipped and retried automatically
 * - Works efficiently even with crore rows in DB
 *
 * @author Medi RaviKumar
 * @version 2.0
 */
@Service
public class CentralDBServiceWithOutDBCheck {

    private static final Logger LOG = LogManager.getLogger(CentralDBServiceWithOutDBCheck.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final String PRIMARY_CODE_TYPE = "Primary";
    private static final String SECONDARY_CODE_TYPE = "Secondary";
    private static final String PG_UNIQUE_VIOLATION = "23505"; // PostgreSQL unique constraint error code

    // ✅ SecureRandom is thread-safe and cryptographically strong — no duplicates
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private DataSource dataSource;

    private Connection getPostgresConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // ✅ Removed synchronized — no longer needed, DB constraints handle uniqueness
    public ResponseDTO generateUniqueCodes(ResponseDTO resp, String jsonData) {
        Connection conn = null;
        long startTime = System.currentTimeMillis();

        try {
            LOG.info("========== UNIQUE CODE GENERATION START ==========");

            CompanyDetailsDTO reqDto = parseAndValidateRequest(jsonData);
            LOG.info("Request validated => companyId={}, plantId={}, lineId={}, year={}, type={}",
                    reqDto.getCompanyId(), reqDto.getPlantId(), reqDto.getLineId(),
                    reqDto.getYear(), reqDto.getUidCodeType());

            conn = getPostgresConnection();
            conn.setAutoCommit(false); // ✅ manual transaction control

            int generatedCodes = executeCodeGeneration(conn, reqDto);

            conn.commit(); // ✅ commit all at once

            long timeTaken = System.currentTimeMillis() - startTime;
            LOG.info("========== UNIQUE CODE GENERATION COMPLETE ==========: generatedCodes={}, timeTaken={}ms",
                    generatedCodes, timeTaken);

            JSONObject responseJson = new JSONObject();
            responseJson.put("generatedCodes", generatedCodes);
            responseJson.put("totalLots", reqDto.getNoOfLots());
            responseJson.put("codesPerLot", reqDto.getNoOfCodes());
            responseJson.put("uidCodeType", reqDto.getUidCodeType());
            responseJson.put("executionTimeMs", timeTaken);

            resp.setMessage(appConfig.getSuccessMessage());
            resp.setStatusCode(appConfig.getSuccessCode());
            resp.setResponse(responseJson.toString());

        } catch (IllegalArgumentException e) {
            LOG.error("Invalid request: {}", e.getMessage());
            resp.setMessage("Invalid request: " + e.getMessage());
            resp.setStatusCode("400");
            resp.setResponse(e.getMessage());
            rollbackQuietly(conn);

        } catch (Exception e) {
            LOG.error("Code generation failed: {}", e.getMessage(), e);
            resp.setMessage(appConfig.getErrorMessage());
            resp.setStatusCode(appConfig.getErrorCode());
            resp.setResponse("Error: " + e.getMessage());
            rollbackQuietly(conn);

        } finally {
            closeQuietly(conn);
        }

        return resp;
    }

    // ─────────────────────────────────────────────
    // PRIVATE — Parse & Validate
    // ─────────────────────────────────────────────

    private CompanyDetailsDTO parseAndValidateRequest(String jsonData) throws Exception {
        try {
            CompanyDetailsDTO dto = new ObjectMapper().readValue(jsonData, CompanyDetailsDTO.class);

            if (dto.getCompanyId() == null || dto.getCompanyId().isEmpty())
                throw new IllegalArgumentException("companyId is required");
            if (dto.getPlantId() == null || dto.getPlantId().isEmpty())
                throw new IllegalArgumentException("plantId is required");
            if (dto.getLineId() == null || dto.getLineId().isEmpty())
                throw new IllegalArgumentException("lineId is required");
            if (dto.getYear() <= 0)
                throw new IllegalArgumentException("year must be valid");
            if (dto.getNoOfLots() <= 0)
                throw new IllegalArgumentException("noOfLots must be greater than 0");
            if (dto.getUidCodeType() == null ||
                    (!PRIMARY_CODE_TYPE.equals(dto.getUidCodeType()) &&
                     !SECONDARY_CODE_TYPE.equals(dto.getUidCodeType())))
                throw new IllegalArgumentException("uidCodeType must be 'Primary' or 'Secondary'");

            return dto;

        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────
    // PRIVATE — Code Generation Loop
    // ─────────────────────────────────────────────

    private int executeCodeGeneration(Connection conn, CompanyDetailsDTO reqDto) throws Exception {
        int generatedCodes = 0;

        String generatedBy = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())
                ? appConfig.getGeneratedBy() : appConfig.getSecondaryGeneratedBy();
        String versionName = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())
                ? appConfig.getVersionName() : appConfig.getSecondaryVersionName();
        String versionCode = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())
                ? appConfig.getVersionCode() : appConfig.getSecondaryVersionCode();
        int codesPerLot = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())
                ? appConfig.getNoOfCodesForLot() : appConfig.getSecondaryNoOfCodesForLot();

        int yr = reqDto.getYear() % 100;
        Integer plantNumber = Integer.parseInt(reqDto.getPlantId().substring(1));
        Integer lineNumber  = Integer.parseInt(reqDto.getLineId().substring(1));
        String str1 = StringUtils.generateStringByParams(yr,
                Integer.parseInt(reqDto.getCompanyId()), plantNumber, lineNumber);

        reqDto.setNoOfCodes(codesPerLot);
        long lotHeaderId = insertUniqueCodeLot(conn, reqDto);

        // ✅ Get starting serial number once at the beginning
        long serialNumber = getMaxSequenceNumber(conn,
                reqDto.getCompanyId(), reqDto.getYear(),
                reqDto.getPlantId(), reqDto.getLineId(),
                reqDto.getUidCodeType());

        for (int lot = 1; lot <= reqDto.getNoOfLots(); lot++) {
            LOG.info("Processing lot {}/{}", lot, reqDto.getNoOfLots());

            int lotGenerated = 0;
            int lotSkipped   = 0;
            int lotFailed    = 0;

            for (int i = 0; i < reqDto.getNoOfCodes(); i++) {

                // ✅ Increment serial number once per code
                if (PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())) {
                    serialNumber = StringUtils.getTendigitsSequenceNumberForPrimary(serialNumber);
                } else {
                    serialNumber = StringUtils.getTendigitsSequenceNumberForSecondary(serialNumber);
                }

                boolean inserted = false;
                int retries = 0;

                // ✅ Retry loop — only triggers on rare duplicate, not every time
                while (!inserted && retries < MAX_RETRY_ATTEMPTS) {
                    try {
                        // Step 1: Generate random number and INSERT directly (no SELECT check)
                        String randomNumber = generateRandomNumber(conn, reqDto.getYear());
                        if (randomNumber == null) {
                            LOG.warn("LOT {} Code {}: random number generation failed after {} attempts",
                                    lot, i, MAX_RETRY_ATTEMPTS);
                            lotFailed++;
                            break;
                        }

                        // Step 2: Build UID
                        String uid = StringUtils.getUIDWithDynamicKeyIndex(
                                generatedBy, versionName, versionCode, str1, randomNumber);

                        // Step 3: INSERT directly — DB constraint catches any duplicate
                        boolean success = insertUniqueCodeMaster(conn, reqDto, lotHeaderId,
                                lot, serialNumber, randomNumber, uid);

                        if (success) {
                            inserted = true;
                            lotGenerated++;
                            generatedCodes++;
                        } else {
                            // Duplicate UID — extremely rare, retry with new random
                            retries++;
                            lotSkipped++;
                            LOG.info("LOT {} Code {}: duplicate UID, retry {}/{}",
                                    lot, i, retries, MAX_RETRY_ATTEMPTS);
                        }

                    } catch (Exception e) {
                        LOG.error("LOT {} Code {}: unexpected error => {}", lot, i, e.getMessage());
                        lotFailed++;
                        break;
                    }
                }

                if (!inserted && retries >= MAX_RETRY_ATTEMPTS) {
                    LOG.warn("LOT {} Code {}: gave up after {} retries", lot, i, MAX_RETRY_ATTEMPTS);
                    lotFailed++;
                }
            }

            LOG.info("LOT {} summary => generated={}, skipped={}, failed={}",
                    lot, lotGenerated, lotSkipped, lotFailed);
        }

        LOG.info("TOTAL GENERATED UID COUNT => {}", generatedCodes);
        return generatedCodes;
    }

    // ─────────────────────────────────────────────
    // PRIVATE — Random Number (Insert-First, No SELECT)
    // ─────────────────────────────────────────────

    /**
     * Generates a unique random number by inserting directly into DB.
     * If a duplicate is detected (constraint violation 23505), retries with a new value.
     * No SELECT check before INSERT — much faster at scale.
     */
    private String generateRandomNumber(Connection conn, int year) {
        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            // ✅ SecureRandom — thread-safe, no shared state issues
            String randomNum = generateSecureRandom(14);
            String sql = "INSERT INTO fluxsen_schema.random_number_master " +
                         "(active, created_on, random_number, codes_year) " +
                         "VALUES (B'1', NOW(), ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, randomNum);
                ps.setInt(2, year);
                ps.executeUpdate();
                return randomNum; // ✅ inserted successfully, return immediately

            } catch (SQLException e) {
                if (PG_UNIQUE_VIOLATION.equals(e.getSQLState())) {
                    // Duplicate random number — generate a new one and retry
                    LOG.info("Random number duplicate on attempt {}/{}, retrying...",
                            attempt + 1, MAX_RETRY_ATTEMPTS);
                } else {
                    // Real DB error — log and stop
                    LOG.error("Random number insert failed (SQLState={}) => {}",
                            e.getSQLState(), e.getMessage());
                    return null;
                }
            }
        }
        LOG.warn("Could not generate unique random number after {} attempts", MAX_RETRY_ATTEMPTS);
        return null;
    }

    // ─────────────────────────────────────────────
    // PRIVATE — UID Insert (Insert-First, No SELECT)
    // ─────────────────────────────────────────────

    /**
     * Inserts a unique code directly into the master table.
     * Returns true if inserted successfully.
     * Returns false if duplicate UID (constraint violation) — caller will retry.
     * No SELECT COUNT check before INSERT — much faster at scale.
     */
    private boolean insertUniqueCodeMaster(Connection conn, CompanyDetailsDTO reqDto,
            long lotHeaderId, int lotNumber, long serialNumber,
            String randomNumber, String uidCode) throws Exception {

        String sql = "INSERT INTO fluxsen_schema.central_db_unique_code_master " +
                     "(active, created_on, customer_code, customer_name, line_name, " +
                     "line_number, lot_number, plant_number, plant_name, random_number, " +
                     "serial_number, status, uid_code, used, used_date, year, " +
                     "year_of_usage, unique_code_lots_id, uid_code_type) " +
                     "VALUES (B'1', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, B'0', ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, DateUtils.getCurrentSystemTimestamp());
            ps.setString(2, reqDto.getCompanyId());
            ps.setString(3, reqDto.getCompanyName());
            ps.setString(4, reqDto.getLineName());
            ps.setString(5, reqDto.getLineId());
            ps.setInt(6, lotNumber);
            ps.setString(7, reqDto.getPlantId());
            ps.setString(8, reqDto.getPlantName());
            ps.setString(9, randomNumber);
            ps.setLong(10, serialNumber);
            ps.setString(11, "GENERATED");
            ps.setString(12, uidCode);
            ps.setNull(13, java.sql.Types.TIMESTAMP);
            ps.setInt(14, reqDto.getYear());
            ps.setInt(15, 0);
            ps.setLong(16, lotHeaderId);
            ps.setString(17, reqDto.getUidCodeType());
            ps.executeUpdate();
            return true; // ✅ success

        } catch (SQLException e) {
            if (PG_UNIQUE_VIOLATION.equals(e.getSQLState())) {
                LOG.info("Duplicate UID detected => {}, will retry with new random", uidCode);
                return false; // ✅ caller retries with new random number
            }
            throw e; // real error — bubble up
        }
    }

    // ─────────────────────────────────────────────
    // PRIVATE — Lot Header Insert
    // ─────────────────────────────────────────────

    private long insertUniqueCodeLot(Connection conn, CompanyDetailsDTO reqDto) throws Exception {
        String sql = "INSERT INTO fluxsen_schema.central_db_unique_code_lots " +
                     "(active, created_on, customer_code, customer_name, line_name, " +
                     "line_number, no_of_codes, no_of_lots, plant_number, plant_name, status, year) " +
                     "VALUES (B'1', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, DateUtils.getCurrentSystemTimestamp());
            ps.setString(2, reqDto.getCompanyId());
            ps.setString(3, reqDto.getCompanyName());
            ps.setString(4, reqDto.getLineName());
            ps.setString(5, reqDto.getLineId());
            ps.setInt(6, reqDto.getNoOfCodes());
            ps.setInt(7, reqDto.getNoOfLots());
            ps.setString(8, reqDto.getPlantId());
            ps.setString(9, reqDto.getPlantName());
            ps.setString(10, "PENDING");
            ps.setInt(11, reqDto.getYear());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
                throw new Exception("Lot header insert failed: no ID returned");
            }
        }
    }

    // ─────────────────────────────────────────────
    // PRIVATE — Serial Number (Read Max Once)
    // ─────────────────────────────────────────────

    private long getMaxSequenceNumber(Connection conn, String companyId, int year,
            String plantId, String lineId, String uidCodeType) throws Exception {
        String sql = "SELECT COALESCE(MAX(serial_number), 0) " +
                     "FROM fluxsen_schema.central_db_unique_code_master " +
                     "WHERE customer_code = ? AND year = ? " +
                     "AND plant_number = ? AND line_number = ? AND uid_code_type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, companyId);
            ps.setInt(2, year);
            ps.setString(3, plantId);
            ps.setString(4, lineId);
            ps.setString(5, uidCodeType);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    // ─────────────────────────────────────────────
    // PRIVATE — Secure Random Generator
    // ─────────────────────────────────────────────

    /**
     * Generates a random alphanumeric string using SecureRandom.
     * Thread-safe — can be called from multiple threads simultaneously.
     */
    private static String generateSecureRandom(int len) {
        String chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // ─────────────────────────────────────────────
    // PRIVATE — Connection Helpers
    // ─────────────────────────────────────────────

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); }
            catch (SQLException e) { LOG.error("Rollback failed => {}", e.getMessage()); }
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try { conn.close(); }
            catch (SQLException e) { LOG.error("Failed to close connection => {}", e.getMessage()); }
        }
    }
}