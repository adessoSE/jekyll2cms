package de.adesso.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    // TODO: include all variables

    @Value("#{environment.REPOSITORY_REMOTE_URL}")
    private String REPOSITORY_REMOTE_URL;

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Value("#{environment.REPOSITORY_REMOTE_URL}")
    private String REMOTE_REPO_URL;

    public void checkConfiguration() {
        // TODO: implement checks
        // TODO: implement validations
    }

    public String getREPOSITORY_REMOTE_URL() {
        return REPOSITORY_REMOTE_URL;
    }

    public String getLOCAL_REPO_PATH() {
        return LOCAL_REPO_PATH;
    }

    public String getREMOTE_REPO_URL() {
        return REMOTE_REPO_URL;
    }
}
