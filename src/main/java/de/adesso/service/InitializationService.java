package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class InitializationService {

	private MarkdownTransformer markdownTransformer;
	private GitRepoCloner repoCloner;
	private GitRepoPusher repoPusher;
	private ConfigService configService;
	private FileTransfer fileTransfer;
	private JekyllService jekyllService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

	@Autowired
	public InitializationService(MarkdownTransformer markdownTransformer, GitRepoCloner gitRepoCloner,
								 GitRepoPusher gitRepoPusher, ConfigService configService, FileTransfer fileTransfer, JekyllService jekyllService){
		this.markdownTransformer = markdownTransformer;
		this.repoCloner = gitRepoCloner;
		this.repoPusher = gitRepoPusher;
		this.configService = configService;
		this.fileTransfer = fileTransfer;
		this.jekyllService = jekyllService;
	}

	/**
	 * Init the Jekyll2cms process.
	 * Step 0: Check config
	 * Step 1: Clone repo
 	 * Step 2: Transform repo using jekyll
	 * Step 3: Push changes
	 * Step 4: Send Notifications (optional)
	 */
	@PostConstruct
	public void init() {
		try {
			// Step 1: Check config
			configService.checkConfiguration();

			// Step 2: Clone repo
			repoCloner.cloneRemoteRepo();

			// Step 3: Transform repo using jekyll
			jekyllService.startJekyllBuildProcess();
			markdownTransformer.copyGeneratedXmlFiles();
			fileTransfer.moveGeneratedImages(new File(configService.getLOCAL_SITE_IMAGE()), new File(configService.getLOCAL_DEST_IMAGE()));

			// Step 4: Push changes
			repoPusher.pushRepo();

		} catch(Exception e) {
			LOGGER.error("UNDEFINED EXCEPTION", e);
			LOGGER.error("Exiting jekyll2cms.");
			System.exit(1);
		}

		LOGGER.info("Execution of jekyll2cms successful.");
		LOGGER.info("Stopping jekyll2cms.");
		System.exit(0);
	}
}
