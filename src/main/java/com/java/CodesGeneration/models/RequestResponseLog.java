package com.java.CodesGeneration.models;

import lombok.Data;                    
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;  
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data                                  
@NoArgsConstructor                    
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "codegeneration_request_response_log")
public class RequestResponseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "id")
    private Long id;

    @Column(name = "requested_user_id")
    private String requestedUserId;

    @Column(name = "requested_user_name")
    private String requestedUserName;

    @Column(name = "requested_on")
    private Timestamp requestedOn;

    @Column(name = "request_api", length = 4000)   
    private String requestAPI;

    @Column(name = "request", length = 4000)
    private String request;

    @Column(name = "response_on")
    private Timestamp responseOn;

    @Column(name = "response", length = 4000)
    private String response;

    @Column(name = "response_status")
    private String responseStatus;
}