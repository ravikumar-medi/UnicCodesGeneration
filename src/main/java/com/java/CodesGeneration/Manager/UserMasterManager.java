package com.java.CodesGeneration.Manager;

import com.java.CodesGeneration.models.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMasterManager extends JpaRepository<UserMaster, Long> {
    Optional<UserMaster> findByUserName(String userName);
}
