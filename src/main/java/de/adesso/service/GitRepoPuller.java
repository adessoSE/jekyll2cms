package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GitRepoPuller {

    private final GitRepoDiffer repoDiffer;

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownTransformer.class);

    private Git localGit;

    @Autowired
    public GitRepoPuller(GitRepoDiffer repoDiffer) {
        this.repoDiffer = repoDiffer;
    }

    /**
     * pulls the remote git repository to receive changes.
     */
    public void pullRemoteRepo() {
        String method = "pullRemoteRepo";
        LOGGER.info("Trying to pull remote repository...");
        localGit = LocalRepoCreater.getLocalGit();
        if (localGit != null) {
            Repository repository = localGit.getRepository();
            try (Git git = new Git(repository)) {

				/*
				 * Getting The Commit Information of the Remote Repository
				 */
                RevWalk walker = new RevWalk(repository);
                RevCommit commit = walker.parseCommit(repository.resolve("HEAD"));
                Date commitTime = commit.getAuthorIdent().getWhen();
                String commiterName = commit.getAuthorIdent().getName();
                String commitEmail = commit.getAuthorIdent().getEmailAddress();
                String commitID = repository.resolve("HEAD").getName();

                // get everything from the remote branch
                PullResult pullResult = git.pull().setStrategy(MergeStrategy.THEIRS).call();
                LOGGER.info("Fetch result: " + pullResult.getFetchResult().getMessages());
                LOGGER.info("Merge result: " + pullResult.getMergeResult().toString());
                LOGGER.info("Merge status: " + pullResult.getMergeResult().getMergeStatus());
                // compare local and remote branch
                repoDiffer.checkForUpdates(git, commiterName, commitEmail, commitTime, commitID);
            } catch (Exception e) {
                LOGGER.error("In method " + method + ": Error while pulling remote git repository.", e);
            }
            localGit.close();
        } else {
            LOGGER.warn("Repository not cloned yet");
        }
    }
}
