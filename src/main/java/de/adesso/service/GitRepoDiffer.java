package de.adesso.service;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class GitRepoDiffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownTransformer.class);

    private final ConfigService configService;

    //Not initialised, because repo needs to be cloned first, so there is no bean
    private Repository repo;

    @Autowired
    public GitRepoDiffer(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Gets the information of the newest commit
     */
    private String getCommitInformation() {
        repo = LocalRepoCreater.getLocalGit().getRepository();
        String commitID = "";
        try {
            //Getting The Commit Information of the Remote Repository
            RevWalk walker = new RevWalk(repo);
            RevCommit commit = walker.parseCommit(repo.resolve("HEAD"));
            commitID = repo.resolve("HEAD").getName();

        } catch (Exception e) {

        } finally {
            LocalRepoCreater.getLocalGit().close();
        }
        return commitID;
    }


    /**
     * Method checks if remote repository was updated. Before the git-pull command
     * is executed in method pullRemoteRepo(), the state of the existing local
     * repository will be stored to variable 'oldHead'. After the git-pull command
     * was executed, this method will be called which compares the state of the old
     * repository with the state of the repository after executing the git-pull
     * command. Changed files will be logged
     *
     * @return
     */
    public List<DiffEntry> checkForUpdates() {
        String commitID = getCommitInformation();

        LOGGER.info("Checking for Updates");
        JSONParser parser = new JSONParser();
        try {
            /*
             * Getting the Commit Information from the local
             * JSON File to compare this Information with
             * the Commit Information of the pull
             */
            Object object = parser.parse(new FileReader(configService.getJSON_PATH()));
            JSONObject commitJSON = (JSONObject) object;

            ObjectReader reader = repo.newObjectReader();
            RevWalk revWalk = new RevWalk(repo);

            /*
             * Getting the old Head of the Repository
             * Takes the Commit ID which is saved in the JSON-File
             * And transfers it to a tree Element
             */
            RevCommit revCommitOld = revWalk.parseCommit(ObjectId.fromString(commitJSON.get("CommitID").toString()).toObjectId());
            ObjectId oldTree = revCommitOld.getTree().getId();
            CanonicalTreeParser oldHeadIter = new CanonicalTreeParser(null, reader, oldTree);

            /*
             * Getting the old Head of the Repository
             * Takes the Commit ID from the last pull
             * And transfers it to a tree Element
             */
            RevCommit revCommitNew = revWalk.parseCommit(ObjectId.fromString(commitID).toObjectId());
            ObjectId newTree = revCommitNew.getTree().getId();
            CanonicalTreeParser newHeadIter = new CanonicalTreeParser(null, reader, newTree);

            DiffFormatter df = new DiffFormatter(new ByteArrayOutputStream());
            df.setRepository(repo);
            List<DiffEntry> entries = df.scan(oldHeadIter, newHeadIter);

            // check if there is a diff between the local commit and the remote commit
            // if there are no updates, return
            // if there are updates
            //   trigger build process
            //   if build success
            //     copy all generated xml based on the diff information between local and remote
            //     copy all images
            //     commit and push to remote repo

            // check if updades found
            if (commitJSON.get("CommitID").equals(commitID)) {
                LOGGER.info("No updates found. \n" +
                        "Jekyll2cms successfull");
                System.exit(0);
            } else {
                LOGGER.info("Updates found.");
                return entries;
            }


            // TODO infos for commit message move logic to in repoPusher
            for (DiffEntry entry : entries) {
                //Checking for deleted Files to get the old Path
                if (entry.getChangeType() == DiffEntry.ChangeType.DELETE) {
                    LOGGER.info("The file " + entry.getOldPath() + " was deleted!");
                } else {
                    LOGGER.info("The file " + entry.getNewPath() + " was updated!!");
                }
            }
            df.close();

        } catch (IOException e) {
            LOGGER.error("Error while checking for updated files");
            e.printStackTrace();
            // TODO change exit code later
            System.exit(11);
        } catch (ParseException e) {
            e.printStackTrace();
            // TODO change exit code later
            System.exit(12);
        }
        return null;
    }
}
