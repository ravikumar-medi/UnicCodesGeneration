package com.java.CodesGeneration.Controller;

import com.java.CodesGeneration.DTO.ResponseDTO;
import com.java.CodesGeneration.Service.CentralDBService;
import com.java.CodesGeneration.Service.RequestResponseLogService;
import com.java.CodesGeneration.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * REST Controller for generating unique codes
 * Supports both Primary and Secondary code types via single API endpoint
 * 
 * @author  RaviKumar Medi
 * @version 1.0
 */
@RestController
@RequestMapping("/generate")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UniqueCodeController {
    
    private static final Logger LOG = LogManager.getLogger(UniqueCodeController.class);

    @Autowired
    private CentralDBService centralDBService;

    @Autowired
    private RequestResponseLogService requestResponseLogService;

    /**
     * Generate unique codes for Primary or Secondary UID type
     * 
     * Request JSON Example:
     * {
     *     "companyId": "914",
     *     "companyName": "Vylor Agrisciense India Pvt Ltd",
     *     "plantId": "P8",
     *     "plantName": "Plant-P8",
     *     "lineId": "L1",
     *     "lineName": "Line-1",
     *     "year": "2026",
     *     "noOfLots": "5",
     *     "uidCodeType": "Primary"
     * }
     * 
     * @param request HTTP request
     * @param jsonData Request body as JSON string
     * @return ResponseEntity with generated codes information
     */
    @PostMapping("/generateUniqueCodes")
    public ResponseEntity<ResponseDTO> generateUniqueCodes(HttpServletRequest request,@RequestBody String jsonData) {
        LOG.info("<========== generateUniqueCodes API Called ==========>");
        ResponseDTO resp = new ResponseDTO();
        Timestamp reqTimestamp = DateUtils.getCurrentSystemTimestamp();
        try {
            LOG.debug("Request Body => {}", jsonData);
            
            resp = centralDBService.generateUniqueCodes(resp, jsonData);
            Timestamp respTimestamp = DateUtils.getCurrentSystemTimestamp();
            requestResponseLogService.saveRequestResponse("/generateUniqueCodes",jsonData,resp.getResponse() != null ? resp.getResponse().toString() : null,reqTimestamp,respTimestamp,resp.getStatusCode());
            LOG.info(">========== generateUniqueCodes API Completed ==========>");
            
            // Return appropriate HTTP status based on response code
            int statusCode = Integer.parseInt(resp.getStatusCode());
            if (statusCode == 200) {
                return ResponseEntity.ok(resp);
            } else if (statusCode == 400) {
                return ResponseEntity.badRequest().body(resp);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
            }
            
        } catch (Exception e) {
            LOG.error("Unexpected error in generateUniqueCodes => {}", e.getMessage(), e);
            resp.setMessage("Internal server error: " + e.getMessage());
            resp.setStatusCode("500");
            resp.setResponse("Error: " + e.getMessage());
            // Log error response
            try {
                requestResponseLogService.saveRequestResponse( "/generateUniqueCodes",jsonData, resp.getResponse() != null ? resp.getResponse().toString() : null,reqTimestamp, DateUtils.getCurrentSystemTimestamp(),"500");
            } catch (Exception logError) {
                LOG.error("Failed to log error response => {}", logError.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    @GetMapping("/checkProgress")
    public ResponseEntity<Map<String, Object>> checkProgress() {
        LOG.info(">========== checkProgress==========>");
        return ResponseEntity.ok(Map.of(
            "status",               CentralDBService.currentJobStatus,
            "totalCodesGenerated",  CentralDBService.totalCodesGenerated.get(),
            "inProgress",           CentralDBService.generationInProgress,
            "currentPlant",         CentralDBService.currentPlant,
            "currentLine",          CentralDBService.currentLine
        ));
    }

    /**
     * Health check endpoint
     * 
     * @return Status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
    	LOG.info(">========== Health==========>");
        return ResponseEntity.ok("Codes Generation API is running");
    }
    
    
    
}