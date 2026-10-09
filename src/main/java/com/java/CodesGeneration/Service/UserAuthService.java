package com.java.CodesGeneration.Service;

import com.java.CodesGeneration.Manager.UserMasterManager;
import com.java.CodesGeneration.models.UserMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthService {

    @Autowired
    private UserMasterManager userMasterManager;

    public boolean validateLogin(String userName, String password) {
        if (userName == null || password == null) {
            return false;
        }

        Optional<UserMaster> userOpt = userMasterManager.findByUserName(userName.trim());
        if (userOpt.isEmpty()) {
            return false;
        }

        UserMaster user = userOpt.get();
        if (Boolean.FALSE.equals(user.getActive())) {
            return false;
        }

        return user.getPassword() != null && user.getPassword().equals(password.trim());
    }
}
