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
    name = "user_master",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_master_user_name", columnNames = {"user_name"})
    }
)
public class UserMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "status")
    private String status = "ACTIVE";
}
