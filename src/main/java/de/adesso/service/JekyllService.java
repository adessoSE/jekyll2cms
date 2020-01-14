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
import java.util.concurrent.Executors;

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

    private final static String JEKYLL_OPTION_BUILD = "build";
    private final static String JEKYLL_OPTION_INCR = "--incremental";

    private void printJekyllStatus(int exitValue, String outputResult) {
        if (exitValue == 0) {
            LOGGER.info("Jekyll watcher started with output: \n {}", outputResult);
            LOGGER.info("Jekyll waiting for updates.");
        } else {
            LOGGER.error("Jekyll build was not successful.");
        }
    }


    /**
     * Starts the jekyll build process (jekyll build --incremental)
     *
     * @return true, if jekyll build was successful
     */
    public boolean startJekyllCI() {


		boolean isWindows = System.getProperty("os.name")
				.toLowerCase().startsWith("windows");

		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			builder.command("cmd.exe", "/c", "jekyll", "build", "--incremental");
		} else {
			builder.command("sh", "-c", "jekyll", "build", "--incremental");
		}
		LOGGER.info("Builder working dir: " + LOCAL_REPO_PATH);
		builder.directory(new File(LOCAL_REPO_PATH));
		Process process = null;
		try {
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StreamGobbler streamGobbler =
				new StreamGobbler(process.getInputStream(), System.out::println);
		Executors.newSingleThreadExecutor().submit(streamGobbler);
		int exitCode = 0;
		try {
			exitCode = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assert exitCode == 0;

        /*LOGGER.info("startJekyllCI()");
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

        int exitValue = -1;
        ByteArrayOutputStream jekyllBuildOutput = new ByteArrayOutputStream();

        String line;
        CommandLine cmdLine;
        if (isWindows) {
            line = "cmd.exe";
            cmdLine = CommandLine.parse(line);
            cmdLine.addArgument("/c");
            cmdLine.addArgument(JEKYLL_PATH);
            cmdLine.addArgument(JEKYLL_OPTION_BUILD);
            cmdLine.addArgument(JEKYLL_OPTION_INCR);
        } else {
            line = JEKYLL_PATH;
            cmdLine = CommandLine.parse(line);
            cmdLine.addArgument(JEKYLL_OPTION_BUILD);
            cmdLine.addArgument(JEKYLL_OPTION_INCR);
        }
        //cmdLine.addArgument(JEKYLL_OPTION_INCR);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(LOCAL_REPO_PATH));
        PumpStreamHandler streamHandler = new PumpStreamHandler(jekyllBuildOutput);
        executor.setStreamHandler(streamHandler);

        try {
            LOGGER.info("Starting jekyll build with command: " + cmdLine.toString());
            exitValue = executor.execute(cmdLine);
            LOGGER.info("Jekyll build command executed");
        } catch (IOException e) {
            LOGGER.error("Error while executing jekyll build. Error message: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
        printJekyllStatus(exitValue, jekyllBuildOutput.toString());*/
        return true;
    }
}
