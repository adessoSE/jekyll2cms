package de.adesso;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
public class RepoController {
    private String localPath, remotePath;
    private Repository localRepoPath;
    private Git git;

    public void initRepo() {
        localPath = "downloads/jekyll";
        remotePath = "https://github.com/daklassen/daklassen.github.io";
        try {
            localRepoPath = new FileRepository(localPath + "/.git");
        } catch (IOException e) {
            e.printStackTrace();
        }
        git = new Git(localRepoPath);
    }

    public void cloneRemoteRepo() {
        try {
            Git.cloneRepository()
                    .setURI(remotePath)
                    .setDirectory(new File(localPath))
                    .call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
