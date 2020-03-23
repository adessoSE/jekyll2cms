package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InitializationService {

	@Value("${jekyll2cms.start.notification}")
	private String JEKYLL2CMS_START_NOTIFICATION;

	private MarkdownTransformer markdownTransformer;
	private GitRepoCloner repoCloner;
	private GitRepoPusher repoPusher;
	private ConfigService configService;
	private EmailService emailService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

	@Autowired
	public InitializationService(MarkdownTransformer markdownTransformer, GitRepoCloner gitRepoCloner,
								 GitRepoPusher gitRepoPusher, ConfigService configService, EmailService emailService){
		this.markdownTransformer = markdownTransformer;
		this.repoCloner = gitRepoCloner;
		this.repoPusher = gitRepoPusher;
		this.configService = configService;
		this.emailService = emailService;
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

			// TODO: Step 0: Check config
			configService.checkConfiguration();
			// Step 1: Clone repo
			repoCloner.cloneRemoteRepo();
			// TODO: Step 2: Transform repo using jekyll
			// TODO: Step 3: Push changes
			// TODO: Step 4: Send Notifications (optional)
//			emailService.sendSimpleEmail("Jekyll2cms startet", "Jekyll2cms for: " +
//				REPOSITORY_REMOTE_URL + " has been successfully started.");
		} catch(Exception e) {

		}



//		try {
//			if (repoCloner.cloneRemoteRepo()) {
//				repoPuller.pullRemoteRepo();
//				repoPusher.triggerBuildProcess();
//				markdownTransformer.copyAllGeneratedXmlFiles(); //GitRepoDiffer.copyAllGeneratedXmlFiles
//				System.exit(0);
//			}
//			System.exit(1);
//		} catch (Exception e) {
//			LOGGER.error("Jekyll2cm couldn't be initialized successfully.", e);
//			System.exit(1);
//		}
	}
}
