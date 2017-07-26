package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This service helps managing repositories with the help of JGit.
 */
@Service
@EnableScheduling
public class RepoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepoService.class);

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;
    @Value("${repository.remote.url}")
    private String REMOTE_REPO_URL;

    @Value("${jekyll.path.posts}")
    private String JEKYLL_POSTS_PATH;

    /* contains old HEAD of repository */
    private ObjectId oldHead;

    @Autowired
    private XmlParseService xmlParseService;

    /* local Git */
    private Git localGit;

    /* HEAD of repository */
    private static final String HEAD = "HEAD^{tree}";

    /* Map that maps post file names to a list of commit dates */
    private Map<String, List<Date>> postsMappedToListOfCommitTimes;

    /**
     * Clones the remote repository (see in application.properties:
     * repository.remote.url) to a local repository (repository.local.path) if the
     * local repository is not already existing.
     */
    public void cloneRemoteRepo() {
        String method = "cloneRemoteRepo";
        try {
            if (!localRepositoryExists()) {
                localGit = Git.cloneRepository().setURI(REMOTE_REPO_URL).setDirectory(new File(LOCAL_REPO_PATH)).call();
                LOGGER.info("Repository cloned successfully");
            } else {
                LOGGER.warn("Remote repository is already cloned into local repository");
                localGit = openLocalGit();
                pullRemoteRepo();
            }
        } catch (Exception e) {
            LOGGER.error("In method " + method + ": Error while cloning remote git respository", e);
        }
    }

    /**
     * Method checks if remote repository was updated. Before the git-pull command
     * is executed in method pullRemoteRepo(), the existing local repository will be
     * stored to variable 'oldHead'. After the git-pull command was executed, this
     * method will be called which compares the state of the old repository with the
     * state of the repository after executing the git-pull command. Changed files
     * will be logged
     *
     * @param git
     */
    private void checkForUpdates(Git git) {
        LOGGER.info("Checking for Updates");
        try {
            ObjectReader reader = git.getRepository().newObjectReader();
            CanonicalTreeParser oldHeadIter = new CanonicalTreeParser();
            oldHeadIter.reset(reader, oldHead);
            CanonicalTreeParser newHeadIter = new CanonicalTreeParser();
            ObjectId newTree = git.getRepository().resolve(RepoService.HEAD);
            newHeadIter.reset(reader, newTree);
            DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
            df.setRepository(git.getRepository());
            List<DiffEntry> entries = df.scan(oldHeadIter, newHeadIter);
            if (entries == null || entries.size() == 0) {
                LOGGER.info("No updates found.");
            } else {
                this.triggerXMLgenerator();
            }
            for (DiffEntry entry : entries) {
                LOGGER.info("The file " + entry.getNewPath() + " was updated!!");
            }
            df.close();
        } catch (IOException e) {
            LOGGER.error("Error while checking for updated files");
            e.printStackTrace();
        }
    }

    public void triggerXMLgenerator() {
        LOGGER.info("Generate XML files from jekyll builts and push them to remote repository");
        xmlParseService.generateXmlFiles();
    }

    /**
     * pulls the remote git repository to receive changes.
     */
    @Scheduled(fixedRate = 10000) // 3600000 = 1h (value in milliseconds)
    public void pullRemoteRepo() {
        String method = "pullRemoteRepo";
        LOGGER.info("Trying to pull remote repository...");
        if (localGit != null) {
            Repository repository = localGit.getRepository();
            try (Git git = new Git(repository)) {
                this.oldHead = repository.resolve(RepoService.HEAD);
                git.pull().call();
                this.checkForUpdates(git);
                localGit.close();
            } catch (Exception e) {
                LOGGER.error("In method " + method + ": Error while pulling remote git repository.", e);
            }
        }

    }

    private boolean localRepositoryExists() {
        String method = "localRepositoryExists";
        try {
            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repositoryBuilder.setGitDir(new File(LOCAL_REPO_PATH + ".git"));
            repositoryBuilder.setMustExist(true);
            repositoryBuilder.build();
        } catch (RepositoryNotFoundException e) {
            LOGGER.error("In method {}: Could not find repository: Error message: {}", method, e.getMessage());
            return false;
        } catch (IOException e) {
            LOGGER.error("In method {}: Error while accessing file: Error message: {}", method, e.getMessage());
        }
        return true;
    }

    /**
     * Opens the local jekyll repository.
     *
     * @return Git
     */
    private Git openLocalGit() {
        try {
            return Git.open(new File(LOCAL_REPO_PATH + ".git"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Maps post files (markdown format) from the local repository to a list of commit dates.
     *
     * @return Map
     */
    public Map<String, List<Date>> retrieveCommitTimesOfPostFiles() {
        localGit = openLocalGit();
        Repository repository = localGit.getRepository();
        ObjectId commitId = null;
        try (RevWalk walk = new RevWalk(repository)) {
            commitId = repository.resolve(Constants.HEAD);
            RevCommit commit = walk.parseCommit(commitId);
            RevTree tree = commit.getTree();

            // now use a TreeWalk to iterate over all files in the Tree recursively
            // you can set Filters to narrow down the results if needed
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(false);
                treeWalk.setFilter(PathFilter.create(JEKYLL_POSTS_PATH));
                while (treeWalk.next()) {
                    if (treeWalk.isSubtree()) {
                        treeWalk.enterSubtree();
                    } else {
                        String filePath = treeWalk.getPathString();
                        retrieveCommitTimesOfPostFile(filePath, repository);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return postsMappedToListOfCommitTimes;
    }

    /**
     * Retrieves the commit times of the given file from the given repository.
     *
     * @param relativeFilePath - e.g. _posts/file.markdown
     * @param repository       - Repository where the jekyll site lives.
     */
    public void retrieveCommitTimesOfPostFile(String relativeFilePath, Repository repository) {
        if (postsMappedToListOfCommitTimes == null) {
            postsMappedToListOfCommitTimes = new HashMap<>();
        }
        if (postsMappedToListOfCommitTimes.get(relativeFilePath) == null) {
            postsMappedToListOfCommitTimes.put(relativeFilePath, new ArrayList<>());
        }

        try (Git git = new Git(repository)) {
            Iterable<RevCommit> logs = git.log()
                    .addPath(relativeFilePath).call();
            for (RevCommit rev : logs) {
                // alternative: rev.getCommitterIdent().getWhen(). Is of type Date and returns same result.
                Date d = new Date(rev.getCommitTime() * 1000L);
                postsMappedToListOfCommitTimes.get(relativeFilePath).add(d);
            }
            Collections.sort(postsMappedToListOfCommitTimes.get(relativeFilePath));
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }
}
