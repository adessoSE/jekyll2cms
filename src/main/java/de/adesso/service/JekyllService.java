package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * This service helps managing all jekyll related commands.
 */
@Service
public class JekyllService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JekyllService.class);

    private final ConfigService configService;

    @Autowired
    public JekyllService(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Starts the jekyll build process (jekyll build --incremental)
     */
    public void startJekyllBuildProcess() {
        LOGGER.info("Starting jekyll build...");
        // create command builder
        ProcessBuilder builder = new ProcessBuilder();
        // set command:
        //   execute in shell
        //   allow file access of repo dir for user jekyll
        //   call jekyll build
        builder.command("sh", "-c", "chown -R jekyll /srv/jekyll/repo && jekyll build");
        // set dir where to execute command
        builder.directory(new File(configService.getLOCAL_REPO_PATH()));

        try {
            LOGGER.info("execute command: sh -c chown -R jekyll /srv/jekyll/repo && jekyll build");
            Process process = builder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LOGGER.info("jekyll build finished with exit code: " + exitCode);
            } else {
                LOGGER.error("jekyll build finished with exit code: " + exitCode);
                LOGGER.error("Exiting jekyll2cms.");
                System.exit(30);
            }
        } catch (IOException e) {
            LOGGER.error("Error during building command: ", e);
            LOGGER.error("Exiting jekyll2cms.");
            System.exit(31);
        } catch (InterruptedException e) {
            LOGGER.error("Error during jekyll build execution: ", e);
            LOGGER.error("Exiting jekyll2cms.");
            System.exit(32);
        }
    }
}
