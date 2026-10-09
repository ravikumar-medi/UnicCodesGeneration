package com.java.CodesGeneration.Service;

import com.java.CodesGeneration.AppConfig;
import com.java.CodesGeneration.DTO.CompanyDetailsDTO;
import com.java.CodesGeneration.DTO.ResponseDTO;
import com.java.CodesGeneration.Manager.CentralDbUniqueCodeLotManager;
import com.java.CodesGeneration.Manager.CentralDbUniqueCodeMasterManager;
import com.java.CodesGeneration.Manager.RandomNumberMasterManager;
import com.java.CodesGeneration.models.CentralDbUniqueCodeLot;
import com.java.CodesGeneration.models.CentralDbUniqueCodeMaster;
import com.java.CodesGeneration.models.RandomNumberMaster;
import com.java.CodesGeneration.utils.DateUtils;
import com.java.CodesGeneration.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    private RandomNumberMasterManager randomNumberMasterManager;

    @Autowired
    private CentralDbUniqueCodeLotManager centralDbUniqueCodeLotManager;

    @Autowired
    private CentralDbUniqueCodeMasterManager centralDbUniqueCodeMasterManager;

    @Transactional
    public synchronized ResponseDTO generateUniqueCodes(ResponseDTO resp, String jsonData) {
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
            int generatedCodes = executeCodeGeneration(reqDto);
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
            currentPlant = "";
            currentLine  = "";
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
            if (dto.getUidCodeType() == null || (!PRIMARY_CODE_TYPE.equals(dto.getUidCodeType()) && !SECONDARY_CODE_TYPE.equals(dto.getUidCodeType()))) {
                throw new IllegalArgumentException("uidCodeType must be 'Primary' or 'Secondary'");
            }
            
          

            return dto;
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage());
        }
    }


    private int executeCodeGeneration(CompanyDetailsDTO reqDto) throws Exception {
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
        long lotHeaderId = insertUniqueCodeLot(reqDto);

        long serialNumber = getMaxSequenceNumber(reqDto.getCompanyId(), reqDto.getYear(), reqDto.getPlantId(), reqDto.getLineId(), reqDto.getUidCodeType());

        if (serialNumber == 0) {
            String yrStr = String.valueOf(reqDto.getYear()).substring(2);
            serialNumber = Long.parseLong(yrStr + "00000001");
            LOG.info("Fresh start — seeded serialNumber => {}", serialNumber);
        } else {
            serialNumber++;
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
                    String serialNumberStr = StringUtils.buildSerialString(serialNumber, reqDto.getPlantId(), reqDto.getLineId());
                    LOG.info("LOT {} Code {}: serial => {}", lot, i, serialNumberStr);
                    String randomNumber = generateRandomNumber(reqDto.getYear());
                    if (randomNumber == null) {
                        LOG.info("LOT {} Code {}: random number generation failed", lot, i);
                        lotFailed++;
                        serialNumber++;
                        continue;
                    }
                    String uid = StringUtils.getUIDWithDynamicKeyIndex(generatedBy, versionName, versionCode, str1, randomNumber);
                    LOG.info("LOT {} Code {}: UID => {}", lot, i, uid);
                    if (getCountByUID(uid) > 0) {
                        LOG.info("LOT {} Code {}: duplicate UID skipped => {}", lot, i, uid);
                        lotSkipped++;
                        serialNumber++;
                        continue;
                    }
                    insertUniqueCodeMaster(reqDto, lotHeaderId, lot, serialNumberStr, randomNumber, uid);
                    lotGenerated++;
                    generatedCodes++;
                    CentralDBService.totalCodesGenerated.incrementAndGet();
                    serialNumber++;
                } catch (Exception e) {
                    LOG.info("LOT {} Code {}: insertion failed => {}", lot, i, e.getMessage());
                    lotFailed++;
                    serialNumber++;
                }
            }
            LOG.info("LOT {} summary => generated={}, skipped={}, failed={}",lot, lotGenerated, lotSkipped, lotFailed);
        }
        CentralDBService.currentJobStatus = "COMPLETED";
        LOG.info("TOTAL GENERATED UID COUNT => {}", generatedCodes);
        return generatedCodes;
    }


	public long getMaxSequenceNumber(String companyId, int year, String plantId, String lineId, String uidCodeType) {
		List<CentralDbUniqueCodeMaster> rows = centralDbUniqueCodeMasterManager.findByCustomerCodeAndYearAndPlantNumberAndLineNumberAndUidCodeType(
				companyId,
				year,
				plantId,
				lineId,
				uidCodeType
		);

		long maxSerial = 0L;
		for (CentralDbUniqueCodeMaster row : rows) {
			String serialNumber = row.getSerialNumber();
			if (serialNumber == null || serialNumber.isBlank() || !serialNumber.contains("-")) {
				continue;
			}
			String rawNumber = serialNumber.substring(serialNumber.lastIndexOf('-') + 1);
			if (rawNumber == null || rawNumber.isBlank()) {
				continue;
			}
			try {
				long value = Long.parseLong(rawNumber);
				if (value > maxSerial) {
					maxSerial = value;
				}
			} catch (NumberFormatException ignored) {
				LOG.debug("Ignoring non-numeric serial suffix: {}", serialNumber);
			}
		}

		LOG.info("DB max serial number => {}", maxSerial);
		return maxSerial;
	}

    public synchronized String generateRandomNumber(int year) {
        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                String randomNum = StringUtils.generateRandomStringWithLength(14);
                if (!randomNumberMasterManager.existsByRandomNumber(randomNum)) {
                    RandomNumberMaster entity = new RandomNumberMaster();
                    entity.setActive(true);
                    entity.setCreatedOn(DateUtils.getCurrentSystemTimestamp());
                    entity.setRandomNumber(randomNum);
                    entity.setCodesYear(year);
                    randomNumberMasterManager.save(entity);
                    return randomNum;
                }
            } catch (Exception e) {
                LOG.error("Random number attempt {} failed => {}", attempt + 1, e.getMessage());
            }
        }
        return null;
    }

    public long getCountByUID(String uid) throws Exception {
        return centralDbUniqueCodeMasterManager.existsByUidCode(uid) ? 1L : 0L;
    }

    private long insertUniqueCodeLot(CompanyDetailsDTO reqDto) throws Exception {
        CentralDbUniqueCodeLot lot = new CentralDbUniqueCodeLot();
        lot.setActive(true);
        lot.setCreatedOn(DateUtils.getCurrentSystemTimestamp());
        lot.setCustomerCode(reqDto.getCompanyId());
        lot.setCustomerName(reqDto.getCompanyName());
        lot.setLineName(reqDto.getLineName());
        lot.setLineNumber(reqDto.getLineId());
        lot.setNoOfCodes(reqDto.getNoOfCodes());
        lot.setNoOfLots(reqDto.getNoOfLots());
        lot.setPlantNumber(reqDto.getPlantId());
        lot.setPlantName(reqDto.getPlantName());
        lot.setStatus("PENDING");
        lot.setUidCodeType(reqDto.getUidCodeType());
        lot.setYear(reqDto.getYear());

        CentralDbUniqueCodeLot savedLot = centralDbUniqueCodeLotManager.save(lot);
        if (savedLot == null || savedLot.getId() == null) {
            throw new Exception("Lot header insert failed: no ID returned");
        }
        return savedLot.getId();
    }

    private void insertUniqueCodeMaster(CompanyDetailsDTO reqDto, long lotHeaderId, int lotNumber, String serialNumberStr, String randomNumber, String uidCode) throws Exception {
        CentralDbUniqueCodeMaster entity = new CentralDbUniqueCodeMaster();
        entity.setActive(true);
        entity.setCreatedOn(DateUtils.getCurrentSystemTimestamp());
        entity.setCustomerCode(reqDto.getCompanyId());
        entity.setCustomerName(reqDto.getCompanyName());
        entity.setLineName(reqDto.getLineName());
        entity.setLineNumber(reqDto.getLineId());
        entity.setLotNumber(lotNumber);
        entity.setPlantNumber(reqDto.getPlantId());
        entity.setPlantName(reqDto.getPlantName());
        entity.setRandomNumber(randomNumber);
        entity.setSerialNumber(serialNumberStr);
        entity.setStatus("GENERATED");
        entity.setUidCode(uidCode);
        entity.setUsed(false);
        entity.setUsedDate(null);
        entity.setYear(reqDto.getYear());
        entity.setYearOfUsage(0);
        entity.setUniqueCodeLotsId(lotHeaderId);
        entity.setUidCodeType(reqDto.getUidCodeType());

        centralDbUniqueCodeMasterManager.save(entity);
    }
}
