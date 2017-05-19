package de.adesso.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * This service helps managing all jekyll related commands.
 */
@Service
public class JekyllService {

    private final static Logger LOGGER = Logger.getLogger(JekyllService.class);

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Value("${jekyll.path}")
    private String JEKYLL_PATH;

    /**
     * This method executes "jekyll build" command in the local repository.
     */
    public void runJekyllBuild() {
        String line = JEKYLL_PATH + " build";
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(LOCAL_REPO_PATH));

        try {
            int exitValue = executor.execute(cmdLine);
            printJekyllBuildStatus(exitValue);
        } catch (IOException e) {
            LOGGER.error("Error while executing jekyll build.", e);
        }

    }

    private void printJekyllBuildStatus(int exitValue) {
        if (exitValue == 0) {
            System.out.println();
            System.out.println("Jekyll build was successful");
        } else {
            System.err.println();
            System.err.println("ERROR: Jekyll build was not successful");
        }
    }
}
