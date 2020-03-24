package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void pushRepo(List<DiffEntry> entries) {
        Git localGit = LocalRepoCreater.getLocalGit();
        if (localGit != null) {
            try {
                LOGGER.info("Pushing XML files to repository");

                if (localGit.status().call().isClean()) {
                    LOGGER.info("No new files were generated. Exiting jekyll2cms...");
                    System.exit(0);
                }

                localGit.add().addFilepattern(".").setUpdate(false).call();
                // iterates through entries to find deleted File
                if (entries.iterator().next().getChangeType() == DiffEntry.ChangeType.DELETE){
                    localGit.add().addFilepattern("-A").setUpdate(false).call();
                }

                StringBuilder commitMessageBuilder = new StringBuilder();
                // set commit message with all added and deleted files
                entries.forEach(entry -> {
                    String path = entry.getChangeType() == DiffEntry.ChangeType.DELETE ? entry.getOldPath() : entry.getNewPath();
                    String regex = "(((/.+/)|())(((\\d+-){3})(([^/\\.]+))))";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(path);

                    if(path.startsWith("_posts") && matcher.find()) {
                        String fileName = matcher.group(8);
                        String fileDate = matcher.group(5).substring(0, 10);
                        commitMessageBuilder.append(entry.getChangeType()).append(": ").append(fileName).append(", ").append(fileDate);
                    }
                });

                localGit.commit()
                        .setAll(true)
                        .setMessage(commitMessageBuilder.toString())
                        .setAuthor(configService.getGIT_AUTHOR_NAME(), configService.getGIT_AUTHOR_MAIL())
                        .call();
                CredentialsProvider cp = new UsernamePasswordCredentialsProvider(configService.getGIT_AUTHOR_NAME(), configService.getGIT_AUTHOR_PASSWORD());
                localGit.push().setForce(true).setCredentialsProvider(cp).call();
                LOGGER.info("Pushing XML files was successful");
            } catch (GitAPIException e) {
                LOGGER.error("An error occured while pushing files to remote repository");
                e.printStackTrace();
                System.exit(30);
            }
        }
    }
}
