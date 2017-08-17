package de.adesso.service;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;

import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

	@Value("${repository.local.htmlposts.path}")
	private String LOCAL_HTML_POSTS;

	@Value("${repository.local.firstspirit-xml.path}")
	private String FIRSTSPIRIT_XML_PATH;

	@Value("${repository.local.user.name}")
	private String GIT_AUTHOR_NAME;

	@Value("${repository.local.user.mail}")
	private String GIT_AUTHOR_MAIL;

	@Value("${repository.local.user.password}")
	private String GIT_AUTHOR_PASSWORD;

	/* contains old HEAD of repository */
	private ObjectId oldHead;

	/* HEAD of repository */
	private static final String HEAD = "HEAD^{tree}";

	private final String GIT_COMMIT_MESSAGE = "New First Spirit XML files added automatically by jekyll2cms";

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
				this.copyGeneratedXmlFiles(entries);
				this.pushRepo(entries);
			}
			for (DiffEntry entry : entries) {
				//Checking for deleted Files to get the old Path
				if(entry.getChangeType() == DiffEntry.ChangeType.DELETE) {
					LOGGER.info("The file " + entry.getOldPath() + " was deleted!");
				}
				else {
					LOGGER.info("The file " + entry.getNewPath() + " was updated!!");
				}

			}
			df.close();
		} catch (IOException e) {
			LOGGER.error("Error while checking for updated files");
			e.printStackTrace();
		}
	}

	/**
	 * After a change in a markdown post was detected, the jekyll-build process
	 * generates html and xml files to the corresponding markdown file. The xml
	 * output has to be copied to an intended folder
	 * 
	 * @param entries
	 *            List with all changed files
	 */
	private void copyGeneratedXmlFiles(List<DiffEntry> entries) {

		entries.forEach((entry) -> {

			String [] splitFilePath;
			/*
			 * Assumption: every-blog- post-file with ending "markdown" has the following
			 * structure: _posts/2017-08-01-new-post-for-netlify-test.markdown
			 */

			/*
			 * Before getting the file Name
			 * Evaluation if the data is an deleted Data
			 */
			if(entry.getChangeType() == DiffEntry.ChangeType.DELETE){

				LOGGER.info("Found deleted Post!");

				/*
				 * separate "_posts" or other folders for example the folders for the categorie
				 * from "2017-08-01-new-post-for-netlify-test.markdown" in
				 * file path
				 */
				splitFilePath = entry.getOldPath().split("/");
				LOGGER.info("The File to be deleted: "+entry.getOldPath());
			}
			else {
				splitFilePath = entry.getNewPath().split("/");
			}

			/*
			 * only if changed file is in folder "_posts", then a post defined in markdown
			 * was created or updated; other files are ignored
			 */
			if (splitFilePath[0].equals("_posts")) {

				String[] splitFileName = splitFilePath[1].split("-");

				/*
				 * Get date "2017-08-01" from file name
				 * "2017-08-01-new-post-for-netlify-test.markdown"
				 */
				String fileDate = splitFileName[0] + "-" + splitFileName[1] + "-" + splitFileName[2];

				/*
				 * The part of the file name which is not the date is the file name of the
				 * jekyll xml built. The following method call extracts the file name
				 * "new-post-for-netlify-test.xml" from
				 * "2017-08-01-new-post-for-netlify-test.markdown"
				 */
				String fileName = this.reconstructFileName(splitFileName);
				String xmlFileName = fileName + ".xml";

				/*
				 * The Jeykyll xml built is located at
				 * "/_site/blog-posts/2017-08-01/new-post-for-netlify-test/new-post-for-netlify-
				 * test.xml
				 */
				File source = new File(LOCAL_HTML_POSTS + "/" + fileDate + "/" + fileName + "/" + xmlFileName);
				File dest = new File(FIRSTSPIRIT_XML_PATH + "/" + fileDate + "/" + fileDate + "-" + xmlFileName);
				Path dirPath = new File(FIRSTSPIRIT_XML_PATH + "/" + fileDate).toPath();

				if(entry.getChangeType()== DiffEntry.ChangeType.DELETE)
				{
					try {
						Files.delete(dest.toPath());
						/* Checking if the directory of the File is empty
						 * Then delete it
						 */
						if(!Files.newDirectoryStream(dirPath).iterator().hasNext()){
							Files.delete(dirPath);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
					this.copyFile(source, dest);
				}
			}
		});
	}

	/**
	 * Copy all XML files that were generated by jekyll.
	 * 
	 * This method is useful after starting and initializing the application
	 */
	public void copyAllGeneratedXmlFiles() {
		Collection<File> allFiles = new ArrayList<File>();
		File file = new File(LOCAL_HTML_POSTS);
		scanDirectory(file, allFiles);
		/*
		 * Filter: take only XML-files - other files will be ignored
		 */
		allFiles.stream().filter(File::isFile).filter((f) -> {
			return FilenameUtils.getExtension(f.getAbsolutePath()).equals("xml");
		}).forEach((f) -> {
			String fileDate = FilenameUtils.getBaseName(new File(f.getParent()).getParent());
			File dest = new File(
					/*
					 * XML File located at
					 * "_site/blog-posts/2016-05-12/welcome-to-jekyll/welcome-to-jekyll.xml" and is
					 * desired to be copied to
					 * "assets/first-spirit-xml/2016-05-12-welcome-to-jekyll"
					 */
					FIRSTSPIRIT_XML_PATH + "/" + fileDate + "/" + fileDate + "-"
							+ FilenameUtils.getBaseName(f.getAbsolutePath() + ".xml"));
			this.copyFile(f, dest);
		});

	}

	/**
	 * Auxiliary-method for opyAllGeneratedXmlFiles() - a file directory will be
	 * scanned and all files are collected
	 * 
	 * @param file
	 *            root directory - scan starts here
	 * @param all
	 *            Collection where results are added to
	 */
	private void scanDirectory(File file, Collection<File> all) {
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				all.add(child);
				scanDirectory(child, all);
			}
		}
	}

	/**
	 * Copy a file
	 * 
	 * @param source
	 *            Source of the file
	 * @param dest
	 *            Destination of the file
	 */
	private void copyFile(File source, File dest) {
		try {
			if (source.lastModified() != dest.lastModified()) {
				LOGGER.info("Copy file from " + source.getAbsolutePath() + " to " + dest.getAbsolutePath());
				if (!dest.exists()) {
					dest.getParentFile().mkdir();
				}
				Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING,
						StandardCopyOption.COPY_ATTRIBUTES);
				//Deletes the source XML-File from the
				//_site/blog-posts/YYYY-MM-DD/... folder
				Files.delete(source.toPath());
			}
		} catch (IOException e) {
			LOGGER.error("An error occured while copying generated XML files to destinantion");
		}
	}

	/**
	 * Reconstructs the file name from an array.
	 * 
	 * Example: Array looks like: {"new","post","for","netlify","test.markdown"}.
	 * This method will "return new-post-for-netlify-test"
	 * 
	 * @param splitFileName
	 *            Array with the divided file name
	 * @return reconstructed file name
	 */
	private String reconstructFileName(String[] splitFileName) {
		String fileName = "";
		for (int i = 3; i < splitFileName.length; i++) {

			if (i < splitFileName.length - 1) {
				fileName += splitFileName[i];
				fileName += "-";
			}
			if (i == splitFileName.length - 1) {
				fileName += splitFileName[i].split("\\.")[0];
			}
		}
		return fileName;
	}

	/**
	 * Starts the jekyll build process
	 */
	public void triggerBuildProcess() {
		LOGGER.info(
				"Start jekyll build process and generate XML files from jekyll builts and push them to remote repository");
		if (!jekyllService.startJekyllCI()) {
			LOGGER.error("An error occured while building the sources with jekyll");
		}
	}

	/**
	 * Pushes all files that changed locally
	 * And check for deleted Data
	 */
	public void pushRepo(List<DiffEntry> entries) {
		/*
		 * Assumption: the XML-posts will be pushed into the same repository where the
		 * markdown-posts were pushed, too. If another repository is intended for the
		 * First-Spirit-XML files, another implementation (other remote repository etc.)
		 * is necessary
		 */
		if (localGit != null) {
			try {
				LOGGER.info("Pushing XML files to repository");
				localGit.add().addFilepattern(".").setUpdate(false).call();
				//Iterates through entries to find deleted File
				if (entries.iterator().next().getChangeType() == DiffEntry.ChangeType.DELETE){
					localGit.add().addFilepattern("-A").setUpdate(false).call();
				}
				localGit.commit().setAll(true).setMessage(GIT_COMMIT_MESSAGE)
						.setAuthor(GIT_AUTHOR_NAME, GIT_AUTHOR_MAIL).call();
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(GIT_AUTHOR_NAME, GIT_AUTHOR_PASSWORD);
				localGit.push().setForce(true).setCredentialsProvider(cp).call();
				LOGGER.info("Pushing XML files was successful");
			} catch (GitAPIException e) {
				LOGGER.error("An error occured while pushing files to remote repository");
			}
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
	 * Checks if the local repository exists and creates it matching the
	 * configuration in the builder.
	 * 
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
