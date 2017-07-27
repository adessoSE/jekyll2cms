package de.adesso.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class InitializationService {

    @Autowired
    private RepoService repositoryService;

    private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Value("${jekyll.path}")
    private String JEKYLL_PATH;
    private final static String JEKYLL_OPTION_BUILD = "build";
    private final static String JEKYLL_OTPION_WATCH = "--watch";
    private final static String JEKYLL_OPTION_INCR = "--incremental";

    /**
     * Initializes jekyll2cms lifecycle.
     *
     * @return true, if initialization was successful. Return false otherwise.
     */
    public boolean init() {
        // TODO: exception handling to define
        try {
            this.repositoryService.cloneRemoteRepo();
            this.repositoryService.triggerXMLgenerator();
            new Thread() {
                @Override
                public void run() {
                    startJekyllCI();
                }
            }.start();
            return true;
        } catch (Exception e) {
            LOGGER.error("Jekyll2cm couldn't be initialized successfully.", e);
            return false;
        }
    }

    private void printJekyllStatus(int exitValue, String outputResult) {
        if (exitValue == 0) {
            LOGGER.info("Jekyll watcher started with output: \n {}", outputResult);
            LOGGER.info("Jekyll waiting for updates.");
        } else {
            LOGGER.error("Jekyll build was not successful.");
        }
    }

    private void startJekyllCI() {
        int exitValue = -1;
        String line = JEKYLL_PATH;
        ByteArrayOutputStream jekyllBuildOutput = new ByteArrayOutputStream();
        CommandLine cmdLine = CommandLine.parse(line);
        cmdLine.addArgument(JEKYLL_OPTION_BUILD);
        cmdLine.addArgument(JEKYLL_OTPION_WATCH);
        cmdLine.addArgument(JEKYLL_OPTION_INCR);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(LOCAL_REPO_PATH));
        PumpStreamHandler streamHandler = new PumpStreamHandler(jekyllBuildOutput);
        executor.setStreamHandler(streamHandler);
        try {
            exitValue = executor.execute(cmdLine);
            System.out.println("Started jekyll watching");
        } catch (IOException e) {
            LOGGER.error("Error while executing jekyll build. Error message: {}", e.getMessage());
            e.printStackTrace();
        }
        printJekyllStatus(exitValue, jekyllBuildOutput.toString());
    }
}
