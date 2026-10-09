package com.java.CodesGeneration.Service;

import com.java.CodesGeneration.Manager.RequestResponseLogManager;
import com.java.CodesGeneration.models.RequestResponseLog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class RequestResponseLogService {

    private static final Logger LOG = LogManager.getLogger(RequestResponseLogService.class);

    private final RequestResponseLogManager repository;

    // ✅ Constructor Injection (Recommended)
    public RequestResponseLogService(RequestResponseLogManager repository) {
        this.repository = repository;
    }

    public void saveRequestResponse(
            String requestAPI,
            String request,
            String response,
            Timestamp requestedOn,
            Timestamp responseOn,
            String responseStatus) {

        try {
            RequestResponseLog log = new RequestResponseLog();

            log.setRequestAPI(requestAPI);
            log.setRequest(request);
            log.setResponse(response);

            // ✅ Handle null timestamps safely
            log.setRequestedOn(requestedOn != null ? requestedOn : Timestamp.from(Instant.now()));
            log.setResponseOn(responseOn != null ? responseOn : Timestamp.from(Instant.now()));

            log.setResponseStatus(responseStatus);

            repository.save(log);

            LOG.info("Request-Response log saved successfully for API: {}", requestAPI);

        } catch (Exception e) {
            LOG.error("Failed to save request-response log for API: {}", requestAPI, e);
        }
    }
}