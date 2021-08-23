package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigService.class);

    @Value("#{environment.REPOSITORY_REMOTE_URL ?: null}")
    private String REPOSITORY_REMOTE_URL;

    @Value("#{environment.REPOSITORY_LOCAL_USER_NAME ?: null}")
    private String GIT_AUTHOR_NAME;

    @Value("#{environment.REPOSITORY_LOCAL_USER_MAIL ?: null}")
    private String GIT_AUTHOR_MAIL;

    @Value("#{environment.REPOSITORY_LOCAL_USER_TOKEN ?: null}")
    private String GIT_AUTHOR_TOKEN;

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
        checkRemoteRepoUrl();
        checkAuthorName();
        checkAuthorMail();
        checkAuthorToken();
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

    public String getGIT_AUTHOR_NAME() {
        return GIT_AUTHOR_NAME;
    }

    public String getGIT_AUTHOR_MAIL() {
        return GIT_AUTHOR_MAIL;
    }

    public String getGIT_AUTHOR_TOKEN() {
        return GIT_AUTHOR_TOKEN;
    }

    private void checkRemoteRepoUrl() {
        if (REPOSITORY_REMOTE_URL == null) {
            logAndExitVariableNotFound("REPOSITORY_REMOTE_URL", "Please provide something in the format: https://github.com/myAccount/devblog.", 10);
        } else {
            LOGGER.info("Environment variable provided: REPOSITORY_REMOTE_URL");
        }
    }

    private void checkAuthorName() {
        if (GIT_AUTHOR_NAME == null) {
            logAndExitVariableNotFound("GIT_AUTHOR_NAME", "Please provide a Github username.", 11);
        } else {
            LOGGER.info("Environment variable provided: GIT_AUTHOR_NAME");
        }
    }

    private void checkAuthorMail() {
        if (GIT_AUTHOR_MAIL == null) {
            logAndExitVariableNotFound("GIT_AUTHOR_MAIL", "Please provide the email of the specified GitHub user.", 12);
        } else {
            LOGGER.info("Environment variable provided: GIT_AUTHOR_MAIL");
        }

    }

    private void checkAuthorToken() {
        if (GIT_AUTHOR_TOKEN == null) {
            logAndExitVariableNotFound("GIT_AUTHOR_TOKEN", "Please provide the provided GitHub user's access token.", 14);
        } else {
            LOGGER.info("Environment variable provided: GIT_AUTHOR_TOKEN");
        }
    }

    private void logAndExitVariableNotFound(String variable, String description, int exitCode) {
        LOGGER.error("Environment variable not provided: " + variable + ". " + description);
        LOGGER.error("Exiting jekyll2cms.");
        System.exit(exitCode);
    }
}
