package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class InitializationService {

	@Value("#{environment.REPOSITORY_REMOTE_URL}")
	private String REPOSITORY_REMOTE_URL;

	@Value("${jekyll2cms.start.notification}")
	private String JEKYLL2CMS_START_NOTIFICATION;

	private MarkdownTransformer markdownTransformer;
	private GitRepoCloner repoCloner;
	private GitRepoPuller repoPuller;
	private GitRepoPusher repoPusher;
	private EmailService emailService;
	
	private final static long pollInterval = 20000;

	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

	public InitializationService(@Autowired MarkdownTransformer markdownTransformer,
								 @Autowired GitRepoCloner gitRepoCloner,
								 @Autowired GitRepoPuller gitRepoPuller,
								 @Autowired GitRepoPusher gitRepoPusher,
								 @Autowired EmailService emailService){
		this.markdownTransformer = markdownTransformer;
		this.repoCloner = gitRepoCloner;
		this.repoPuller = gitRepoPuller;
		this.repoPusher = gitRepoPusher;
		this.emailService = emailService;
	}

	/**
	 * Initializes jekyll2cms lifecycle.
	 *
	 * @return true, if initialization was successful. Return false otherwise.
	 */
	public boolean init() {
		try {
			if (repoCloner.cloneRemoteRepo()) {
				repoPuller.pullRemoteRepo();
				repoPusher.triggerBuildProcess();
				markdownTransformer.copyAllGeneratedXmlFiles(); //GitRepoDiffer.copyAllGeneratedXmlFiles
				if(JEKYLL2CMS_START_NOTIFICATION.equals("yes")) {
					emailService.sendSimpleEmail("Jekyll2cms startet", "Jekyll2cms for: " +
							REPOSITORY_REMOTE_URL
							+ " has been successfully started.");
				}
				return true;
			}
			return false;
		} catch (Exception e) {
			LOGGER.error("Jekyll2cm couldn't be initialized successfully.", e);
			return false;
		}
	}

	/**
	 * Triggers the git-pull command to check if there are any updates on the remote
	 * repository. The fixed rate value in the annotation defines the frequency in
	 * ms, when to check for an update (i.e. 10000ms = 10s ==> every 10 seconds the
	 * repository will be pulled (fetch+merge)
	 */
	@Scheduled(fixedDelay = pollInterval) // 3600000 = 1h (value in milliseconds)
	public void pullRemoteRepo() {
		this.repoPuller.pullRemoteRepo();
	}

}
