package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class GitRepoCloner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownTransformer.class);

    private Environment environment;

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Value("${repository.remote.url}")
    private String REMOTE_REPO_URL = environment.getProperty("REPOSITORY_REMOTE_URL");

    public GitRepoCloner(@Autowired Environment environment){
        this.environment = environment;
    }

    /**
     * Clones the remote repository (defined in application.properties:
     * repository.remote.url) to the local file system (repository.local.path) -
     * only if the local repository does not already exist.
     */
    public boolean cloneRemoteRepo() {

        String method = "cloneRemoteRepo";
        try {
            if (!localRepositoryExists()) {
                LocalRepoCreater.setLocalGit(Git.cloneRepository().setURI(REMOTE_REPO_URL).setDirectory(new File(LOCAL_REPO_PATH)).call());
                LOGGER.info("Repository cloned successfully");
            } else {
                LOGGER.warn("Remote repository is already cloned into local repository");
                LocalRepoCreater.setLocalGit(Git.open(new File(LOCAL_REPO_PATH+".git")));
            }
        } catch (Exception e) {
            LOGGER.error("In method " + method + ": Error while cloning remote git respository", e);
            return false;
        }

        return true;
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
}