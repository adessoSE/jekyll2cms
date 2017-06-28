package de.adesso.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * This service helps managing all jekyll related commands.
 */
@Service
public class JekyllService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JekyllService.class);

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Value("${jekyll.path}")
    private String JEKYLL_PATH;

    /**
     * This method executes "jekyll build" command in the local repository.
     */
    public void runJekyllBuild() {
        String method = "runJekyllBuild";
        String line = JEKYLL_PATH + " build";

        ByteArrayOutputStream jekyllBuildOutput = new ByteArrayOutputStream();
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(LOCAL_REPO_PATH));
        PumpStreamHandler streamHandler = new PumpStreamHandler(jekyllBuildOutput);
        executor.setStreamHandler(streamHandler);

        try {
            int exitValue = executor.execute(cmdLine);
            printJekyllBuildStatus(exitValue, jekyllBuildOutput.toString());
        } catch (IOException e) {
            LOGGER.error("In method {}: Error while executing jekyll build. Error message: {}", method, e.getMessage());
        }

    }

    private void printJekyllBuildStatus(int exitValue, String outputResult) {
        if (exitValue == 0) {
            LOGGER.info("Jekyll build output: \n {}", outputResult);
            LOGGER.info("Jekyll build was successful.");
        } else {
            LOGGER.error("Jekyll build was not successful.");
        }
    }
}
