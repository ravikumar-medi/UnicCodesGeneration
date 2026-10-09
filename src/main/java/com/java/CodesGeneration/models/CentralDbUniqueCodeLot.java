package com.java.CodesGeneration.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "central_db_unique_code_lots")
public class CentralDbUniqueCodeLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "line_name")
    private String lineName;

    @Column(name = "line_number")
    private String lineNumber;

    @Column(name = "no_of_codes")
    private Integer noOfCodes;

    @Column(name = "no_of_lots")
    private Integer noOfLots;

    @Column(name = "plant_number")
    private String plantNumber;

    @Column(name = "plant_name")
    private String plantName;

    @Column(name = "status")
    private String status;

    @Column(name = "uid_code_type")
    private String uidCodeType;

    @Column(name = "year")
    private Integer year;
}
