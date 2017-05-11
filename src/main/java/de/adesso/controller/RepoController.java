package de.adesso.controller;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
public class RepoController {

    private final static Logger LOGGER = Logger.getLogger(RepoController.class);

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;
    @Value("${repository.remote.url}")
    private String REMOTE_REPO_URL;

    private Repository localRepo;
    private Git git;

    public void initLocalRepo() {
        LOGGER.info("Starting: Git init local repository");

        try {
            localRepo = new FileRepository(LOCAL_REPO_PATH + "/.git");
        } catch (IOException e) {
            LOGGER.error("Error while initialising local git repository", e);
        }
        git = new Git(localRepo);
    }

    public void cloneRemoteRepo() {
        LOGGER.info("> Starting: Git clone remote repository");
        try {
            Git.cloneRepository()
                    .setURI(REMOTE_REPO_URL)
                    .setDirectory(new File(LOCAL_REPO_PATH))
                    .call();
        } catch (GitAPIException e) {
            LOGGER.error("Error while cloning remote git respository", e);
        }
    }
}
