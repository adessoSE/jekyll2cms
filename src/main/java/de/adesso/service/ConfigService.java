package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    //REMOTE REPO
    @Value("#{environment.REPOSITORY_REMOTE_URL ?: null}")
    private String REPOSITORY_REMOTE_URL;

    //GITHUB CREDENTIALS
    @Value("#{environment.REPOSITORY_LOCAL_USER_NAME ?: null}")
    private String GIT_AUTHOR_NAME;

    @Value("#{environment.REPOSITORY_LOCAL_USER_MAIL ?: null}")
    private String GIT_AUTHOR_MAIL;

    @Value("#{environment.REPOSITORY_LOCAL_USER_PASSWORD ?: null}")
    private String GIT_AUTHOR_PASSWORD;

    @Value("${repository.local.JSON.path}")
    private String JSON_PATH;

    @Value("${repository.local.image.path}")
    private String LOCAL_SITE_IMAGE;

    @Value("${repository.local.image.destination.path}")
    private String LOCAL_DEST_IMAGE;

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Value("${repository.local.htmlposts.path}")
    private String LOCAL_HTML_POSTS;

    @Value("${repository.local.firstspirit-xml.path}")
    private String FIRSTSPIRIT_XML_PATH;

    public void checkConfiguration() {
        /*
            REPO URL
         */
        if (REPOSITORY_REMOTE_URL == null) {
            LOGGER.error("ERROR: Environment variable not provided: REPOSITORY_REMOTE_URL \n" +
                    "Please provide something in the format: https://github.com/myAccount/devblog");
            System.exit(1);
        } else {
            LOGGER.info("SUCCESS: Environemnt variable provided: REPOSITORY_REMOTE_URL");
        }

        /*
            Github-Names
         */
        if (GIT_AUTHOR_NAME == null) {
            LOGGER.error("ERROR: Environment variable not provided: GIT_AUTHOR_NAME \n" +
                    "Please provide some Github-Username");

            System.exit(1);
        } else {
            LOGGER.info("SUCCESS: Environemnt variable provided: GIT_AUTHOR_NAME");
        }

        if (GIT_AUTHOR_MAIL == null) {
            LOGGER.error("ERROR: Environment variable not provided: GIT_AUTHOR_MAIL \n" +
                    "Please provide some correspronding Email to the Github Username");
            System.exit(1);
        } else {
            LOGGER.info("SUCCESS: Environemnt variable provided: GIT_AUTHOR_MAIL");
        }

        if (GIT_AUTHOR_PASSWORD == null) {
            LOGGER.error("ERROR: Environment variable not provided: GIT_AUTHOR_PASSWORD \n" +
                    "Please provide some correspronding password to the Github Username");
            System.exit(1);
        } else {
            LOGGER.info("SUCCESS: Environemnt variable provided: GIT_AUTHOR_PASSWORD");
        }
    }

    public String getLOCAL_REPO_PATH() {
        return LOCAL_REPO_PATH;
    }

    public String getREPOSITORY_REMOTE_URL() {
        return REPOSITORY_REMOTE_URL;
    }


    public String getLOCAL_HTML_POSTS() {
        return LOCAL_HTML_POSTS;
    }

    public String getLOCAL_SITE_IMAGE() {
        return LOCAL_SITE_IMAGE;
    }

    public String getLOCAL_DEST_IMAGE() {
        return LOCAL_DEST_IMAGE;
    }

    public String getFIRSTSPIRIT_XML_PATH() {
        return FIRSTSPIRIT_XML_PATH;
    }

    public String getJSON_PATH() {
        return JSON_PATH;
    }

    public String getGIT_AUTHOR_NAME() {
        return GIT_AUTHOR_NAME;
    }

    public String getGIT_AUTHOR_MAIL() {
        return GIT_AUTHOR_MAIL;
    }

    public String getGIT_AUTHOR_PASSWORD() {
        return GIT_AUTHOR_PASSWORD;
    }
}
