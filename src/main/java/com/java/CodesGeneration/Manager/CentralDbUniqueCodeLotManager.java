package com.java.CodesGeneration.Manager;

import com.java.CodesGeneration.models.CentralDbUniqueCodeLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentralDbUniqueCodeLotManager extends JpaRepository<CentralDbUniqueCodeLot, Long> {

    java.util.List<CentralDbUniqueCodeLot> findByCustomerCodeAndYear(String customerCode, Integer year);
}
