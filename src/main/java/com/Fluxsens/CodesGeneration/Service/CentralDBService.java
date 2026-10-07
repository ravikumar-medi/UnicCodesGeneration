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
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for generating unique codes with support for Primary and Secondary code types.
 */
@Service
public class CentralDBService {

    private static final Logger LOG = LogManager.getLogger(CentralDBService.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final String PRIMARY_CODE_TYPE = "Primary";
    private static final String SECONDARY_CODE_TYPE = "Secondary";
    
    public static final AtomicInteger totalCodesGenerated = new AtomicInteger(0);
    public static volatile String currentJobStatus = "IDLE";
    public static volatile boolean generationInProgress = false;
    public static volatile String currentPlant = "";   // ADD
    public static volatile String currentLine  = "";

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private DataSource dataSource;

    private Connection getPostgresConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Transactional
    public synchronized ResponseDTO generateUniqueCodes(ResponseDTO resp, String jsonData) {
        Connection conn = null;
        long startTime = System.currentTimeMillis();
        
        if (generationInProgress) {
            resp.setMessage("Code generation already in progress. Please wait.");
            resp.setStatusCode("400");
            resp.setResponse("BUSY");
            return resp;
        }
        generationInProgress = true;
        currentPlant = "";   
        currentLine  = ""; 

        try {
            LOG.info("========== UNIQUE CODE GENERATION START ==========");
            CompanyDetailsDTO reqDto = parseAndValidateRequest(jsonData);
            currentPlant = reqDto.getPlantId();   
            currentLine  = reqDto.getLineId();   
            LOG.info("Request validated => companyId={}, plantId={}, lineId={}, year={}, type={}",reqDto.getCompanyId(), reqDto.getPlantId(), reqDto.getLineId(),reqDto.getYear(), reqDto.getUidCodeType());
            conn = getPostgresConnection();
            int generatedCodes = executeCodeGeneration(conn, reqDto);
            long timeTaken = System.currentTimeMillis() - startTime;
            LOG.info("========== UNIQUE CODE GENERATION COMPLETE ==========: generatedCodes={}, timeTaken={}ms", generatedCodes, timeTaken);
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
        } catch (Exception e) {
            LOG.error("Code generation failed: {}", e.getMessage(), e);
            resp.setMessage(appConfig.getErrorMessage());
            resp.setStatusCode(appConfig.getErrorCode());
            resp.setResponse("Error: " + e.getMessage());
        } finally {
            generationInProgress = false; 
            currentPlant = "";   // ADD
            currentLine  = "";
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOG.error("Failed to close connection: {}", e.getMessage());
                }
            }
        }

        return resp;
    }

    private CompanyDetailsDTO parseAndValidateRequest(String jsonData) throws Exception {
        try {
            CompanyDetailsDTO dto = new ObjectMapper().readValue(jsonData, CompanyDetailsDTO.class);
        

            if (dto.getCompanyId() == null || dto.getCompanyId().isEmpty()) {
                throw new IllegalArgumentException("companyId is required");
            }
            if (dto.getPlantId() == null || dto.getPlantId().isEmpty()) {
                throw new IllegalArgumentException("plantId is required");
            }
            if (dto.getLineId() == null || dto.getLineId().isEmpty()) {
                throw new IllegalArgumentException("lineId is required");
            }
            int currentYear = java.time.Year.now().getValue();
            if (dto.getYear() < currentYear) {
                throw new IllegalArgumentException("Cannot generate codes for past year: " + dto.getYear() +". Current year is: " + currentYear );
            }
            if (dto.getYear() > currentYear + 1) {
                throw new IllegalArgumentException("Cannot generate codes more than 1 year in advance. " +"Requested: " + dto.getYear() );
            }
            if (dto.getNoOfLots() <= 0) {
                throw new IllegalArgumentException("noOfLots must be greater than 0");
            }
            if (dto.getNoOfLots() <= 0) {
                throw new IllegalArgumentException("noOfLots must be greater than 0");
            }
            if (dto.getUidCodeType() == null || (!PRIMARY_CODE_TYPE.equals(dto.getUidCodeType()) && !SECONDARY_CODE_TYPE.equals(dto.getUidCodeType()))) {
                throw new IllegalArgumentException("uidCodeType must be 'Primary' or 'Secondary'");
            }
            
          

            return dto;
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
        }
    }


    private int executeCodeGeneration(Connection conn, CompanyDetailsDTO reqDto) throws Exception {
        int generatedCodes = 0;

        generationInProgress = true;
        totalCodesGenerated.set(0);       
        currentJobStatus = "IN_PROGRESS";

        String generatedBy = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())? appConfig.getGeneratedBy() : appConfig.getSecondaryGeneratedBy();
        String versionName = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())? appConfig.getVersionName() : appConfig.getSecondaryVersionName();
        String versionCode = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())? appConfig.getVersionCode() : appConfig.getSecondaryVersionCode();
        int codesPerLot = PRIMARY_CODE_TYPE.equals(reqDto.getUidCodeType())? appConfig.getNoOfCodesForLot() : appConfig.getSecondaryNoOfCodesForLot();

        int yr = reqDto.getYear() % 100;
        Integer plantNumber = Integer.parseInt(reqDto.getPlantId().substring(1));
        Integer lineNumber  = Integer.parseInt(reqDto.getLineId().substring(1));
        String str1 = StringUtils.generateStringByParams(yr, Integer.parseInt(reqDto.getCompanyId()), plantNumber, lineNumber);

        reqDto.setNoOfCodes(codesPerLot);
        long lotHeaderId = insertUniqueCodeLot(conn, reqDto);

        // Fetch serialNumber ONCE before all lots (was inside lot loop before)
        long serialNumber = getMaxSequenceNumber(conn, reqDto.getCompanyId(), reqDto.getYear(),reqDto.getPlantId(), reqDto.getLineId(), reqDto.getUidCodeType());

        // Seed if DB empty/all bad data → start from YY00000001 If DB has valid data → continue from next number
        if (serialNumber == 0) {
            String yrStr = String.valueOf(reqDto.getYear()).substring(2); // "2026" → "26"
            serialNumber = Long.parseLong(yrStr + "00000001");            // → 2600000001
            LOG.info("Fresh start — seeded serialNumber => {}", serialNumber);
        } else {
            serialNumber++; // continue from next after last inserted
            LOG.info("Continuing from DB — next serialNumber => {}", serialNumber);
        }

        LOG.info("Starting serial number => {}", serialNumber);

        for (int lot = 1; lot <= reqDto.getNoOfLots(); lot++) {
            LOG.info("Processing lot {}/{}", lot, reqDto.getNoOfLots());

            int lotGenerated = 0;
            int lotSkipped   = 0;
            int lotFailed    = 0;

            for (int i = 0; i < reqDto.getNoOfCodes(); i++) {
                try {
                    // Single buildSerialString() for both PRIMARY and SECONDARY  format → "P1L1-2600000001"
                    String serialNumberStr = StringUtils.buildSerialString(serialNumber, reqDto.getPlantId(), reqDto.getLineId());
                    LOG.info("LOT {} Code {}: serial => {}", lot, i, serialNumberStr);
                    String randomNumber = generateRandomNumber(conn, reqDto.getYear());
                    if (randomNumber == null) {
                        LOG.info("LOT {} Code {}: random number generation failed", lot, i);
                        lotFailed++;
                        serialNumber++; //Increment on every path to avoid reuse
                        continue;
                    }
                    String uid = StringUtils.getUIDWithDynamicKeyIndex(generatedBy, versionName, versionCode, str1, randomNumber);
                    LOG.info("LOT {} Code {}: UID => {}", lot, i, uid);
                    if (getCountByUID(conn, uid) > 0) {
                        LOG.info("LOT {} Code {}: duplicate UID skipped => {}", lot, i, uid);
                        lotSkipped++;
                        serialNumber++; // Increment on every path to avoid reuse
                        continue;
                    }
                    //Passing serialNumberStr (formatted String)not raw long serialNumber
                    insertUniqueCodeMaster(conn, reqDto, lotHeaderId, lot, serialNumberStr, randomNumber, uid);
                    lotGenerated++;
                    generatedCodes++;
                    CentralDBService.totalCodesGenerated.incrementAndGet();
                    serialNumber++; //Increment after successful insert
                } catch (Exception e) {
                    LOG.info("LOT {} Code {}: insertion failed => {}", lot, i, e.getMessage());
                    lotFailed++;
                    serialNumber++; // Increment on every path to avoid reuse
                }
            }
            LOG.info("LOT {} summary => generated={}, skipped={}, failed={}",lot, lotGenerated, lotSkipped, lotFailed);
        }
        CentralDBService.currentJobStatus = "COMPLETED";
        LOG.info("TOTAL GENERATED UID COUNT => {}", generatedCodes);
        return generatedCodes;
    }


	//Proper numeric MAX using SPLIT_PART + CAST  Filters out NULL, '0', no-dash rows safely using NULLIF
	public long getMaxSequenceNumber(Connection conn, String companyId, int year, String plantId, String lineId,String uidCodeType) throws Exception {

		String sql = "SELECT COALESCE(MAX(CAST(NULLIF(SPLIT_PART(serial_number, '-', 2), '') AS BIGINT)), 0) "
				+ "FROM vylortracetrack_schema.central_db_unique_code_master "
				+ "WHERE customer_code = ? AND year = ? AND plant_number = ? "
				+ "AND line_number = ? AND uid_code_type = ? " + "AND serial_number IS NOT NULL " + // skip NULL
				"AND serial_number <> '0' " + // skip '0'
				"AND serial_number LIKE '%-%'"; // must have dash → valid format

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, companyId);
			ps.setInt(2, year);
			ps.setString(3, plantId);
			ps.setString(4, lineId);
			ps.setString(5, uidCodeType);
			try (ResultSet rs = ps.executeQuery()) {
				long result = rs.next() ? rs.getLong(1) : 0L;
				LOG.info("DB max serial number => {}", result);
				return result;
			}
		}
	}

    public synchronized String generateRandomNumber(Connection conn, int year) {
        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                String randomNum = StringUtils.generateRandomStringWithLength(14);
                if (getCountByRandomNumber(conn, randomNum) == 0) {
                    String sql = "INSERT INTO vylortracetrack_schema.random_number_master (active, created_on, random_number, codes_year) VALUES (B'1', NOW(), ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, randomNum);
                        ps.setInt(2, year);
                        ps.executeUpdate();
                    }
                    return randomNum;
                }
            } catch (Exception e) {
                LOG.error("Random number attempt {} failed => {}", attempt + 1, e.getMessage());
            }
        }
        return null;
    }

    public long getCountByRandomNumber(Connection conn, String randomNumber) throws Exception {
        String sql = "SELECT COUNT(*) FROM vylortracetrack_schema.random_number_master WHERE random_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, randomNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    public long getCountByUID(Connection conn, String uid) throws Exception {
        String sql = "SELECT COUNT(*) FROM vylortracetrack_schema.central_db_unique_code_master WHERE uid_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0L;
            }
        }
    }

    private long insertUniqueCodeLot(Connection conn, CompanyDetailsDTO reqDto) throws Exception {
        String sql = "INSERT INTO vylortracetrack_schema.central_db_unique_code_lots (active, created_on, customer_code, customer_name, line_name, line_number, no_of_codes, no_of_lots, plant_number, plant_name, status, year) VALUES (B'1', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
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
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new Exception("Lot header insert failed: no ID returned");
            }
        }
    }

    private void insertUniqueCodeMaster(Connection conn, CompanyDetailsDTO reqDto, long lotHeaderId, int lotNumber, String serialNumberStr, String randomNumber, String uidCode) throws Exception {
        String sql = "INSERT INTO vylortracetrack_schema.central_db_unique_code_master (active, created_on, customer_code, customer_name, line_name, line_number, lot_number, plant_number, plant_name, random_number, serial_number, status, uid_code, used, used_date, year, year_of_usage, unique_code_lots_id, uid_code_type) VALUES (B'1', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, B'0', ?, ?, ?, ?, ?)";
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
            ps.setString(10, serialNumberStr);
            ps.setString(11, "GENERATED");
            ps.setString(12, uidCode);
            ps.setNull(13, java.sql.Types.TIMESTAMP);
            ps.setInt(14, reqDto.getYear());
            ps.setInt(15, 0);
            ps.setLong(16, lotHeaderId);
            ps.setString(17, reqDto.getUidCodeType());
            ps.executeUpdate();
        }
    }
}
