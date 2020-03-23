package de.adesso.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Value("#{environment.SPRING_MAIL_RECEIPIENT}")
    private String RECEIPIENT;

    @Value("#{environment.REPOSITORY_REMOTE_URL}")
    private String REPOSITORY_REMOTE_URL;

    @Value("#{environment.REPOSITORY_LOCAL_USER_NAME}")
    private String GIT_AUTHOR_NAME;

    @Value("#{environment.REPOSITORY_LOCAL_USER_MAIL}")
    private String GIT_AUTHOR_MAIL;

    @Value("#{environment.REPOSITORY_LOCAL_USER_PASSWORD}")
    private String GIT_AUTHOR_PASSWORD;

    @Value("#{environment.SPRING_MAIL_USERNAME}")
    private String SPRING_MAIL_USERNAME;

    @Value("#{environment.SPRING_MAIL_PASSWORD}")
    private String SPRING_MAIL_PASSWORD;

    @Value("${repository.local.JSON.path}")
    private String JSON_PATH;

    @Value("${repository.local.image.path}")
    private String LOCAL_SITE_IMAGE;

    @Value("${repository.local.image.destination.path}")
    private String LOCAL_DEST_IMAGE;

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Value("${jekyll2cms.start.notification}")
    private String JEKYLL2CMS_START_NOTIFICATION;

    @Value("${repository.local.htmlposts.path}")
    private String LOCAL_HTML_POSTS;

    @Value("${repository.local.firstspirit-xml.path}")
    private String FIRSTSPIRIT_XML_PATH;


    public void checkConfiguration() {
        // TODO: implement checks
        // TODO: implement validations
    }


    public String getLOCAL_REPO_PATH() {
        return LOCAL_REPO_PATH;
    }

    public String getREPOSITORY_REMOTE_URL() {
        return REPOSITORY_REMOTE_URL;
    }

    public String getJEKYLL2CMS_START_NOTIFICATION() {
        return JEKYLL2CMS_START_NOTIFICATION;
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

    public String getRECEIPIENT() {
        return RECEIPIENT;
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

    public String getSPRING_MAIL_USERNAME() {
        return SPRING_MAIL_USERNAME;
    }

    public String getSPRING_MAIL_PASSWORD() {
        return SPRING_MAIL_PASSWORD;
    }
}
