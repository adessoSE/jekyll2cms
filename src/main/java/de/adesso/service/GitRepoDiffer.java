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
import java.util.ArrayList;
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

            // search latest commit with changes done by a contributor who is not GIT_AUTHOR_NAME
            // this commit is the last commit which is not done by GIT_AUTHOR_NAME
            LOGGER.info("Searching latest commit not done by " + configService.getGIT_AUTHOR_NAME() + "...");
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            RevCommit revCommitNew = StreamSupport.stream(new Git(repo).log().all().call().spliterator(), false)
                    .filter(commit -> !commit.getAuthorIdent().getName().equals(configService.getGIT_AUTHOR_NAME()) && checkOnPost(commit))
                    .findFirst().orElse(null);

            // if there is no such commit, exit
            if (revCommitNew == null) {
                LOGGER.error("No commits found.");
                LOGGER.error("Exiting jekyll2cms.");
                System.exit(30);
            }

            ObjectId newTree = revCommitNew.getTree().getId();
            newTreeIter.reset(reader, newTree);

            List<DiffEntry> entries = new ArrayList<>();
            // get all diffs between the last commit containing a change in a post and its parent
            DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
            df.setRepository(repo);

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            for (RevCommit revCommitOld : revCommitNew.getParents()) {
                ObjectId oldTree = revCommitOld.getTree().getId();
                oldTreeIter.reset(reader, oldTree);
                entries.addAll(df.scan(oldTreeIter, newTreeIter));
            }

            if (entries.isEmpty()) {
                LOGGER.info("No updates found in between " + newTree.toString() + " and its parents.");
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
