package de.adesso.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class JekyllService {

    private final static Logger LOGGER = Logger.getLogger(JekyllService.class);

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    public void runJekyllBuild() {
        LOGGER.info("> Starting: Jekyll build");

        String line = "C:/tools/ruby23/bin/jekyll.bat build";
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(LOCAL_REPO_PATH));

        try {
            int exitValue = executor.execute(cmdLine);
            LOGGER.info("Jekyll build exited with value: " + exitValue);
        } catch (IOException e) {
            LOGGER.error("Error while executing jekyll build.", e);
        }

    }
}
