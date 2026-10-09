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
@Table(
    name = "company_details",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_company_details_company_id", columnNames = {"company_id"})
    }
)
public class CompanyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "line_id")
    private String lineId;

    @Column(name = "line_name")
    private String lineName;

    @Column(name = "plant_id")
    private String plantId;

    @Column(name = "plant_name")
    private String plantName;

    @Column(name = "secure_key")
    private String secureKey;

    @Column(name = "status")
    private String status;
}
