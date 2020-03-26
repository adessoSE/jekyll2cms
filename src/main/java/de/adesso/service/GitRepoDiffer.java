package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.tree.Tree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class GitRepoDiffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownTransformer.class);

    private final ConfigService configService;

    @Autowired
    public GitRepoDiffer(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Method checks if remote repository was updated. Before the git-pull command
     * is executed in method pullRemoteRepo(), the state of the existing local
     * repository will be stored to variable 'oldHead'. After the git-pull command
     * was executed, this method will be called which compares the state of the old
     * repository with the state of the repository after executing the git-pull
     * command. Changed files will be logged
     */
    public List<DiffEntry> checkForUpdates() {
        LOGGER.info("Checking for updates...");
        try {
            Repository repo = LocalRepoCreater.getLocalGit().getRepository();
            ObjectReader reader = repo.newObjectReader();

            // get current head (latest commit) from remote
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = repo.resolve("HEAD^{tree}");
            newTreeIter.reset(reader, newTree);

            // search latest commit with changes done by a contributor who is not GIT_AUTHOR_NAME
            // this commit is the last commit which is not done by GIT_AUTHOR_NAME
            LOGGER.info("Searching latest commit not done by " + configService.getGIT_AUTHOR_NAME() + "...");
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            RevCommit revCommitOld = StreamSupport.stream(new Git(repo).log().all().call().spliterator(), false)
                    .filter(commit -> !commit.getAuthorIdent().getName().equals(configService.getGIT_AUTHOR_NAME()) && checkOnPost(commit))
                    .findFirst().orElse(null);

//            for (RevCommit commit : new Git(repo).log().call()) {
//                if (!commit.getAuthorIdent().getName().equals(configService.getGIT_AUTHOR_NAME())) {
//
//                }
//            }

            // 1. head ist von jekyll2cms
            // 2. head ist von user change in md
            // 3. head ist von user other changes



            // if there is no such commit, exit
            if (revCommitOld == null) {
                LOGGER.error("No commits found.");
                LOGGER.error("Exiting jekyll2cms.");
                System.exit(30);
            }

            // if the found commit is the latest commit, the latest commit and the second latest commit are used for generating diffs
            ObjectId oldTree = revCommitOld.getTree().getId();
            if (oldTree.equals(newTree)) {
                LOGGER.info("Latest commit is not done by " + configService.getGIT_AUTHOR_NAME()
                        + ". Generating diffs with latest and second latest commit...");
                oldTree = repo.resolve("HEAD~1^{tree}");
            }
            oldTreeIter.reset(reader, oldTree);

            // get all diffs which were added by the author
            // ignore all changes from GIT_AUTHOR_NAME
            DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream()); // use NullOutputStream.INSTANCE if you don't need the diff output
            df.setRepository(repo);
            List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);

            if (entries.isEmpty()) {
                LOGGER.info("No updates found in between " + newTree.toString() + " and " + oldTree.toString() + ".");
                LOGGER.info("Stopping jekyll2cms.");
                System.exit(0);
            } else {
                LOGGER.info("Updates found.");
                return entries;
            }
            df.close();

        } catch (IOException e) {
            LOGGER.error("Error while checking for updated files.", e);
            LOGGER.error("Exiting jekyll2cms.");
            System.exit(31);
        } catch (GitAPIException e) {
            LOGGER.error("Error getting commits.", e);
            LOGGER.error("Exiting jekyll2cms.");
            System.exit(32);
        }
        return null;
    }

    private boolean checkOnPost(RevCommit commit) {
        try (DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream())) {
            System.out.println(commit.getShortMessage());
            Repository repo = LocalRepoCreater.getLocalGit().getRepository();
            df.setRepository(repo);
            ObjectReader reader = repo.newObjectReader();

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = commit.getTree().getId();
            newTreeIter.reset(reader, newTree);

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = commit.getParent(0).getTree().getId();
            oldTreeIter.reset(reader, oldTree);
            List<DiffEntry> entries = df.scan(oldTreeIter, newTreeIter);
            for (DiffEntry entry : entries) {
                String path = entry.getChangeType().equals(DiffEntry.ChangeType.DELETE) ? entry.getOldPath() : entry.getNewPath();
                System.out.println(path);
                if (path.startsWith("_post")) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
