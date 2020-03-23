package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class GitRepoCloner {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepoCloner.class);

    private final ConfigService configService;

    @Autowired
    public GitRepoCloner(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Clones the remote repository (defined in environment variables:
     * repository.remote.url) to the local file system (repository.local.path)
     */
    public void cloneRemoteRepo() {
        try {
            LocalRepoCreater.setLocalGit(Git.cloneRepository()
                    .setURI(configService.getREMOTE_REPO_URL())
                    .setDirectory(new File(configService.getLOCAL_REPO_PATH()))
                    .call());
            LOGGER.info("Repository cloned successfully");
        } catch (Exception e) {
            LOGGER.error("In cloneRemoteRepo: Error while cloning remote git respository", e);
            LOGGER.error("Exiting Jekyll2cms...");
            System.exit(10);
        }
    }
}