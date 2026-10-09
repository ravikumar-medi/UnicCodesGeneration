package com.java.CodesGeneration.Service;

import com.java.CodesGeneration.Manager.CompanyDetailsManager;
import com.java.CodesGeneration.Manager.UserMasterManager;
import com.java.CodesGeneration.models.CompanyDetails;
import com.java.CodesGeneration.models.UserMaster;
import com.java.CodesGeneration.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeedService implements ApplicationRunner {

    @Autowired
    private UserMasterManager userMasterManager;

    @Autowired
    private CompanyDetailsManager companyDetailsManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedUser();
        seedCompanyDetails();
    }

    private void seedUser() {
        String userName = "ravikumarmedi";
        String password = "Ravi@J";

        if (userMasterManager.findByUserName(userName).isEmpty()) {
            UserMaster user = new UserMaster();
            user.setUserName(userName);
            user.setPassword(password);
            user.setActive(true);
            user.setCreatedOn(DateUtils.getCurrentSystemTimestamp());
            user.setStatus("ACTIVE");
            userMasterManager.save(user);
        }
    }

    private void seedCompanyDetails() {
        String companyId = "914";

        if (companyDetailsManager.findByCompanyId(companyId).isEmpty()) {
            CompanyDetails company = new CompanyDetails();
            company.setActive(true);
            company.setCompanyId(companyId);
            company.setCompanyName("Vylor Agrisciense India Pvt Ltd");
            company.setCreatedOn(DateUtils.getCurrentSystemTimestamp());
            company.setLineId("L1");
            company.setLineName("Line-1");
            company.setPlantId("P8");
            company.setPlantName("Plant-P8");
            company.setSecureKey("SECURE-914");
            company.setStatus("ACTIVE");
            companyDetailsManager.save(company);
        }
    }
}
