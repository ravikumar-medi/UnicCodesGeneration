package com.java.CodesGeneration.Manager;

import com.java.CodesGeneration.models.RandomNumberMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomNumberMasterManager extends JpaRepository<RandomNumberMaster, Long> {

    boolean existsByRandomNumber(String randomNumber);

    java.util.List<RandomNumberMaster> findByCodesYear(Integer codesYear);
}
