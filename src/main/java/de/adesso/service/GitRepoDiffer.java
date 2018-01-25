package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class GitRepoDiffer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownTransformer.class);

    @Value("${repository.local.JSON.path}")
    private String JSON_PATH;

    @Value("${repository.local.image.path}")
    private String LOCAL_SITE_IMAGE;

    @Value("${repository.local.image.destination.path}")
    private String LOCAL_DEST_IMAGE;

    @Autowired
    private GitRepoPusher repoPusher;

    @Autowired
    private MarkdownTransformer markdownTransformer;

    @Autowired
    private FileTransfer imageTransfer;

    /**
     * Method checks if remote repository was updated. Before the git-pull command
     * is executed in method pullRemoteRepo(), the state of the existing local
     * repository will be stored to variable 'oldHead'. After the git-pull command
     * was executed, this method will be called which compares the state of the old
     * repository with the state of the repository after executing the git-pull
     * command. Changed files will be logged
     *
     * @param git
     */
    public void checkForUpdates(Git git, String name, String email, Date timestamp, String commitID) {
        LOGGER.info("Checking for Updates");
        JSONParser parser = new JSONParser();
        try {
			/*
			 * Getting the Commit Information from the local
			 * JSON File to compare this Information with
			 * the Commit Information of the pull
			 */
            Object object = parser.parse(new FileReader(JSON_PATH));
            JSONObject commitJSON = (JSONObject) object;

            ObjectReader reader = git.getRepository().newObjectReader();
            RevWalk revWalk = new RevWalk(git.getRepository());

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
            df.setRepository(git.getRepository());
            List<DiffEntry> entries = df.scan(oldHeadIter, newHeadIter);

            if(commitJSON.get("Name").equals(name) && commitJSON.get("Email").equals(email)
                    && commitJSON.get("Date").equals(timestamp.toString())&& commitJSON.get("CommitID").equals(commitID)){
                LOGGER.info("No updates found.");
            }
            else{
                LOGGER.info("Updates found.");
                imageTransfer.deleteImages(new File(LOCAL_DEST_IMAGE + "/Cropped_Resized"));
                if(repoPusher.triggerBuildProcess())
                {
                    markdownTransformer.copyGeneratedXmlFiles(entries);
                    LOGGER.info("Copy Images from devblog/_site/assets/images folder to devblog/assets/images");
                    imageTransfer.moveGeneratedImages(new File(LOCAL_SITE_IMAGE), new File(LOCAL_DEST_IMAGE));
                    repoPusher.pushRepo(entries);
                }
                else {
                    LOGGER.info("No Updates, so no push.");
                }
            }

            for (DiffEntry entry : entries) {
                //Checking for deleted Files to get the old Path
                if(entry.getChangeType() == DiffEntry.ChangeType.DELETE) {
                    LOGGER.info("The file " + entry.getOldPath() + " was deleted!");
                }
                else {
                    LOGGER.info("The file " + entry.getNewPath() + " was updated!!");
                }
            }
            df.close();
        } catch (IOException e) {
            LOGGER.error("Error while checking for updated files");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
