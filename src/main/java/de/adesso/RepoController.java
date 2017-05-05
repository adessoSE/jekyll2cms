package de.adesso;

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

    @Value("${repository.local.path}")
    private String localRepoPath;

    @Value("${repository.remote.url}")
    private String remoteRepoUrl;

    private Repository localRepo;
    private Git git;

    public void initLocalRepo() {
        try {
            localRepo = new FileRepository(localRepoPath + "/.git");
        } catch (IOException e) {
            e.printStackTrace();
        }
        git = new Git(localRepo);
    }

    public void cloneRemoteRepo() {
        try {
            Git.cloneRepository()
                    .setURI(remoteRepoUrl)
                    .setDirectory(new File(localRepoPath))
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
