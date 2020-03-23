package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

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
    public void startJekyllCI() {
    	// create command builder
		ProcessBuilder builder = new ProcessBuilder();
		// set command:
		//   execute in shell
		//   allow file access of repo dir for user jekyll
		//   call jekyll build
		builder.command("sh", "-c", "chown -R jekyll /srv/jekyll/repo && jekyll build --incremental");
		LOGGER.info("Builder working dir: " + configService.getLOCAL_REPO_PATH());
		// set dir where to execute command
		builder.directory(new File(configService.getLOCAL_REPO_PATH()));

		Process process = null;
		try {
			LOGGER.info("execute command: sh -c chown -R jekyll /srv/jekyll/repo && jekyll build --incremental");
			process = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// pass every out line from process to sysout
		StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
		Executors.newSingleThreadExecutor().submit(streamGobbler);
		int exitCode = 0;
		try {
			exitCode = process.waitFor();
			LOGGER.info("jekyll build finished with exit code: " + exitCode);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (exitCode != 0) {
			System.exit(20);
		}
    }
}
