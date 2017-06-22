package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

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

    private Git localGit;

    /**
     * Clones the remote repository (see in application.properties: repository.remote.url)
     * to a local repository (repository.local.path) if the local repository is not already existing.
     */
    public void cloneRemoteRepo() {
        String method = "cloneRemoteRepo";
        try {
            if (!localRepositoryExists()) {
                localGit = Git.cloneRepository()
                        .setURI(REMOTE_REPO_URL)
                        .setDirectory(new File(LOCAL_REPO_PATH))
                        .call();
                LOGGER.info("Repository cloned successfully");
            } else {
                LOGGER.warn("Remote repository is already cloned into local repository");
                localGit = Git.open(new File(LOCAL_REPO_PATH + "/.git"));
                pullRemoteRepo();
            }
        } catch (Exception e) {
            LOGGER.error("In method " + method +": Error while cloning remote git respository", e);
        }
    }

    /**
     * pulls the remote git repository to receive changes.
     */
    public void pullRemoteRepo() {
        String method = "pullRemoteRepo";
        LOGGER.info("Trying to pull remote repository...");
        try (Git git = new Git(localGit.getRepository())) {
            git.pull()
                    .call();
            localGit.close();
        } catch (Exception e) {
            LOGGER.error("In method "+ method +": Error while pulling remote git repository.", e);
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
