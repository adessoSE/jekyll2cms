package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitRepoPusher {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepoPusher.class);

    private final ConfigService configService;

    @Autowired
    public GitRepoPusher(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Pushes all files that changed locally
     * And check for deleted Data
     */
    public void pushRepo() {
        Git localGit = LocalRepoCreater.getLocalGit();
        if (localGit != null) {
            try {
                LOGGER.info("Pushing XML files to repository...");

                Status status = localGit.status().call();

                if (status.isClean() || (status.getUntracked().size() == 1 && status.getUntracked().contains("Gemfile.lock"))) {
                    LOGGER.info("No new files were generated, so no files to push.");
                    LOGGER.info("Stopping jekyll2cms.");
                    System.exit(0);
                }

                // add everything in ./assets, gemfile.lock and _site folder are ignored due to .gitignore
                localGit.add().addFilepattern("assets").setUpdate(false).call();

                // set commit message with all added and deleted files
                status = localGit.status().call();
                System.out.println(status.getAdded().size());
                StringBuilder commitMessageBuilder = new StringBuilder();
                status.getAdded().forEach(file -> commitMessageBuilder.append("ADD ").append(file).append("\n"));
                status.getRemoved().forEach(file -> commitMessageBuilder.append("DELETE ").append(file).append("\n"));

                localGit.commit()
                        .setAll(true)
                        .setMessage(commitMessageBuilder.toString())
                        .setAuthor(configService.getGIT_AUTHOR_NAME(), configService.getGIT_AUTHOR_MAIL())
                        .call();
                CredentialsProvider cp = new UsernamePasswordCredentialsProvider(configService.getGIT_AUTHOR_NAME(), configService.getGIT_AUTHOR_PASSWORD());
                localGit.push().setForce(false).setCredentialsProvider(cp).call();
                LOGGER.info("XML files pushed successfully.");
            } catch (GitAPIException e) {
                LOGGER.error("An error occurred while pushing files to remote repository.", e);
                LOGGER.error("Exiting jekyll2cms.");
                System.exit(40);
            }
        }
    }
}
