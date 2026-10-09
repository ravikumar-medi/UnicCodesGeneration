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
@Table(name = "random_number_master",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_random_number_master_random_number", columnNames = {"random_number"})
        })
public class RandomNumberMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "random_number")
    private String randomNumber;

    @Column(name = "codes_year")
    private Integer codesYear;
}
