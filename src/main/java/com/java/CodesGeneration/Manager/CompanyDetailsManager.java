package com.java.CodesGeneration.Manager;

import com.java.CodesGeneration.models.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyDetailsManager extends JpaRepository<CompanyDetails, Long> {
    Optional<CompanyDetails> findByCompanyId(String companyId);
}
