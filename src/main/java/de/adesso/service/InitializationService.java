package de.adesso.service;

import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Service
public class InitializationService {

	private MarkdownTransformer markdownTransformer;
	private GitRepoCloner repoCloner;
	private GitRepoPusher repoPusher;
	private ConfigService configService;
	private GitRepoDiffer repoDiffer;
	private FileTransfer fileTransfer;
	private JekyllService jekyllService;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationService.class);

	@Autowired
	public InitializationService(MarkdownTransformer markdownTransformer, GitRepoCloner gitRepoCloner,
								 GitRepoPusher gitRepoPusher, ConfigService configService, GitRepoDiffer repoDiffer, FileTransfer fileTransfer, JekyllService jekyllService){
		this.markdownTransformer = markdownTransformer;
		this.repoCloner = gitRepoCloner;
		this.repoPusher = gitRepoPusher;
		this.configService = configService;
		this.repoDiffer = repoDiffer;
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
			// Step 0: Check config
			configService.checkConfiguration();
			// Step 1: Clone repo
			repoCloner.cloneRemoteRepo();
			// Step 2: Transform repo using jekyll
			List<DiffEntry> entries = repoDiffer.checkForUpdates(); //TODO Refactor inner methods

			fileTransfer.deleteImages(new File(configService.getLOCAL_DEST_IMAGE() + "/Cropped_Resized"));
			jekyllService.startJekyllBuildProcess();
			markdownTransformer.copyGeneratedXmlFiles(entries);
			fileTransfer.moveGeneratedImages(new File(configService.getLOCAL_SITE_IMAGE()), new File(configService.getLOCAL_DEST_IMAGE()));
			markdownTransformer.copyAllGeneratedXmlFiles();

			// Step 3: Push changes
			repoPusher.pushRepo(entries);

			// TODO: Step 4: Send Notifications (optional)
//			emailService.sendSimpleEmail("Jekyll2cms startet", "Jekyll2cms for: " +
//				REPOSITORY_REMOTE_URL + " has been successfully started.");
		} catch(Exception e) {
			LOGGER.error("UNDEFINED EXCEPTION");
			e.printStackTrace();
			System.exit(1000);
		}
		LOGGER.info("Jekyll2cms successfull!");
		System.exit(0);
	}
}
