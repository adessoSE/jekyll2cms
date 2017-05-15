package de.adesso.service;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class RepoService {

    private final static Logger LOGGER = Logger.getLogger(RepoService.class);

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;
    @Value("${repository.remote.url}")
    private String REMOTE_REPO_URL;

    private Git localGit;

    public void cloneRemoteRepo() {
        LOGGER.info("> Starting: Git clone remote repository");
        try {
            if (!localRepositoryExists()) {
                localGit = Git.cloneRepository()
                        .setURI(REMOTE_REPO_URL)
                        .setDirectory(new File(LOCAL_REPO_PATH))
                        .call();
            } else {
                LOGGER.warn("Remote repository is already cloned into local repository");
            }
        } catch (Exception e) {
            LOGGER.error("Error while cloning remote git respository", e);
        }
    }

    private boolean localRepositoryExists() {
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.setGitDir(new File(LOCAL_REPO_PATH + "/.git"));
            repositoryBuilder.setMustExist(true);
            repositoryBuilder.build();
        } catch (RepositoryNotFoundException e) {
            return false;
        } catch (IOException e) {
            LOGGER.error("Error while accessing file", e);
        }
        return true;
    }
}
