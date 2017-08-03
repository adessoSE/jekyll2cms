package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;

import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This service helps managing repositories with the help of JGit.
 */
@Service
public class RepoService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepoService.class);

	@Value("${repository.local.path}")
	private String LOCAL_REPO_PATH;
	@Value("${repository.remote.url}")
	private String REMOTE_REPO_URL;

	@Value("${jekyll.path.posts}")
	private String JEKYLL_POSTS_PATH;

	/* contains old HEAD of repository */
	private ObjectId oldHead;
	
	/* HEAD of repository */
	private static final String HEAD = "HEAD^{tree}";

	@Autowired
	private JekyllService jekyllService;

	private Git localGit;

	/**
	 * Clones the remote repository (defined in application.properties:
	 * repository.remote.url) to the local file system (repository.local.path) -
	 * only if the local repository does not already exist.
	 */
	public boolean cloneRemoteRepo() {
		String method = "cloneRemoteRepo";
		try {
			if (!localRepositoryExists()) {
				localGit = Git.cloneRepository().setURI(REMOTE_REPO_URL).setDirectory(new File(LOCAL_REPO_PATH)).call();
				LOGGER.info("Repository cloned successfully");
			} else {
				LOGGER.warn("Remote repository is already cloned into local repository");
				localGit = openLocalGit();
			}
		} catch (Exception e) {
			LOGGER.error("In method " + method + ": Error while cloning remote git respository", e);
			return false;
		}

		return true;
	}

	/**
	 * Method checks if remote repository was updated. Before the git-pull command
	 * is executed in method pullRemoteRepo(), the state of the existing local
	 * repository will be stored to variable 'oldHead'. After the git-pull command
	 * was executed, this method will be called which compares the state of the old
	 * repository with the state of the repository after executing the git-pull
	 * command. Changed files will be logged
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
				LOGGER.info("No updates found.");
			} else {
				LOGGER.info("Updates found.");
				this.triggerBuildProcess();
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

	public void triggerBuildProcess() {
		LOGGER.info(
				"Start jekyll build process and generate XML files from jekyll builts and push them to remote repository");
		if (jekyllService.startJekyllCI()) {
			// TODO start git-push from this point
		}
	}

	/**
	 * pulls the remote git repository to receive changes.
	 */
	public void pullRemoteRepo() {
		String method = "pullRemoteRepo";
		LOGGER.info("Trying to pull remote repository...");
		if (localGit != null) {
			Repository repository = localGit.getRepository();
			try (Git git = new Git(repository)) {

				this.oldHead = repository.resolve(RepoService.HEAD);

				PullResult pullResult = git.pull().setStrategy(MergeStrategy.THEIRS).call();
				LOGGER.info("Fetch result: " + pullResult.getFetchResult().getMessages());
				LOGGER.info("Merge result: " + pullResult.getMergeResult().toString());
				LOGGER.info("Merge status: " + pullResult.getMergeResult().getMergeStatus());
				this.checkForUpdates(git);
			} catch (Exception e) {
				LOGGER.error("In method " + method + ": Error while pulling remote git repository.", e);
			}

			localGit.close();
		} else {
			LOGGER.warn("Repository not cloned yet");
		}

	}

	/**
	 * Checks if the local repository exists and creates it matching the configuration in the builder.
	 * @return true, if repository exists and could be built successful
	 */
	private boolean localRepositoryExists() {
		String method = "localRepositoryExists";
		try {
			FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
			repositoryBuilder.setGitDir(new File(LOCAL_REPO_PATH + ".git"));
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

	/**
	 * Opens the local jekyll repository.
	 *
	 * @return Git
	 */
	private Git openLocalGit() {
		try {
			return Git.open(new File(LOCAL_REPO_PATH + ".git"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
