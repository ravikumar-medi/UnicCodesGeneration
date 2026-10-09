package com.java.CodesGeneration.Manager;

import com.java.CodesGeneration.models.CentralDbUniqueCodeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentralDbUniqueCodeMasterManager extends JpaRepository<CentralDbUniqueCodeMaster, Long> {

    boolean existsByUidCode(String uidCode);

    java.util.List<CentralDbUniqueCodeMaster> findByCustomerCodeAndYearAndPlantNumberAndLineNumberAndUidCodeType(
            String customerCode,
            Integer year,
            String plantNumber,
            String lineNumber,
            String uidCodeType
    );
}
