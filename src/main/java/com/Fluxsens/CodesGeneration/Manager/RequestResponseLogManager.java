package com.Fluxsens.CodesGeneration.Manager;

import com.Fluxsens.CodesGeneration.models.RequestResponseLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestResponseLogManager extends JpaRepository<RequestResponseLog, Long> {


}