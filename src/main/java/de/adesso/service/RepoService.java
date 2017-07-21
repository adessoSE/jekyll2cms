package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This service helps managing repositories with the help of JGit.
 */
@Service
@EnableScheduling
public class RepoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepoService.class);

	@Value("${repository.local.path}")
	private String LOCAL_REPO_PATH;
	@Value("${repository.remote.url}")
	private String REMOTE_REPO_URL;

	private ObjectId oldHead;

	private Git localGit;

	private static final String HEAD = "HEAD^{tree}";

	/**
	 * Clones the remote repository (see in application.properties:
	 * repository.remote.url) to a local repository (repository.local.path) if the
	 * local repository is not already existing.
	 */
	public void cloneRemoteRepo() {
		String method = "cloneRemoteRepo";
		try {
			if (!localRepositoryExists()) {
				localGit = Git.cloneRepository().setURI(REMOTE_REPO_URL).setDirectory(new File(LOCAL_REPO_PATH)).call();
				LOGGER.info("Repository cloned successfully");
			} else {
				LOGGER.warn("Remote repository is already cloned into local repository");
				localGit = Git.open(new File(LOCAL_REPO_PATH + "/.git"));
				pullRemoteRepo();
			}
		} catch (Exception e) {
			LOGGER.error("In method " + method + ": Error while cloning remote git respository", e);
		}
	}

	/**
	 * Method checks if remote repository was updated. Before the git-pull command
	 * is executed in method pullRemoteRepo(), the existing local repository will be
	 * stored to variable 'oldHead'. After the git-pull command was executed, this
	 * method will be called which compares the state of the old repository with the
	 * state of the repository after executing the git-pull command. Changed files
	 * will be logged
	 * 
	 * @param git
	 */
	private void checkForUpdates(Git git) {
		LOGGER.info("Checking for Updates");
		try {
			ObjectReader reader = git.getRepository().newObjectReader();
			CanonicalTreeParser oldHeadIter = new CanonicalTreeParser();
			oldHeadIter.reset(reader, oldHead);
			CanonicalTreeParser newHeadIter = new CanonicalTreeParser();
			ObjectId newTree = git.getRepository().resolve(RepoService.HEAD);
			newHeadIter.reset(reader, newTree);
			DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
			df.setRepository(git.getRepository());
			List<DiffEntry> entries = df.scan(oldHeadIter, newHeadIter);
			if (entries == null || entries.size() == 0) {
				System.out.println("No updates found");
			} else {
				this.triggerXMLgenerator();
			}
			for (DiffEntry entry : entries) {
				LOGGER.info("The file " + entry.getNewPath() + " was updated!!");
			}
			df.close();
		} catch (IOException e) {
			LOGGER.error("Error while checking for updated files");
			e.printStackTrace();
		}
	}

	private void triggerXMLgenerator() {
		System.out.println("Generate XML files from jekyll builts and push them to remote repository");
		//TODO: trigger next steps of the defined application lifecycle
	}

	/**
	 * pulls the remote git repository to receive changes.
	 */
	@Scheduled(fixedRate = 10000) // 3600000 = 1h (value in milliseconds)
	public void pullRemoteRepo() {
		String method = "pullRemoteRepo";
		LOGGER.info("Trying to pull remote repository...");
		if (localGit != null) {
			Repository repository = localGit.getRepository();
			try (Git git = new Git(repository)) {
				this.oldHead = repository.resolve(RepoService.HEAD);
				git.pull().call();
				this.checkForUpdates(git);
				localGit.close();
			} catch (Exception e) {
				LOGGER.error("In method " + method + ": Error while pulling remote git repository.", e);
			}
		}

	}

	private boolean localRepositoryExists() {
		String method = "localRepositoryExists";
		try {
			FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
			repositoryBuilder.setGitDir(new File(LOCAL_REPO_PATH + "/.git"));
			repositoryBuilder.setMustExist(true);
			repositoryBuilder.build();
		} catch (RepositoryNotFoundException e) {
			LOGGER.error("In method {}: Could not find repository: Error message: {}", method, e.getMessage());
			return false;
		} catch (IOException e) {
			LOGGER.error("In method {}: Error while accessing file: Error message: {}", method, e.getMessage());
		}
		return true;
	}
}
