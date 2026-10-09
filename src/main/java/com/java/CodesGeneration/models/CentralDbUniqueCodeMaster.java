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
@Table(name = "central_db_unique_code_master",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_master_uid_code", columnNames = {"uid_code"}),
                @UniqueConstraint(name = "uk_master_random_number", columnNames = {"random_number"}),
                @UniqueConstraint(name = "uk_master_serial_uid_code_type", columnNames = {"serial_number", "uid_code_type"})
        })
public class CentralDbUniqueCodeMaster {

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

    @Column(name = "lot_number")
    private Integer lotNumber;

    @Column(name = "plant_number")
    private String plantNumber;

    @Column(name = "plant_name")
    private String plantName;

    @Column(name = "random_number")
    private String randomNumber;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "uid_code")
    private String uidCode;

    @Column(name = "used")
    private Boolean used;

    @Column(name = "used_date")
    private Timestamp usedDate;

    @Column(name = "year")
    private Integer year;

    @Column(name = "year_of_usage")
    private Integer yearOfUsage;

    @Column(name = "unique_code_lots_id")
    private Long uniqueCodeLotsId;

    @Column(name = "uid_code_type")
    private String uidCodeType;
}
