package de.adesso.service;

import org.eclipse.jgit.api.Git;
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

    private final GitRepoPusher repoPusher;
    private final MarkdownTransformer markdownTransformer;
    private final FileTransfer imageTransfer;
    private final JekyllService jekyllService;
    
    private Git localGit;

    @Autowired
    public GitRepoDiffer(GitRepoPusher repoPusher, MarkdownTransformer markdownTransformer, FileTransfer imageTransfer, JekyllService jekyllService) {
        this.repoPusher = repoPusher;
        this.markdownTransformer = markdownTransformer;
        this.imageTransfer = imageTransfer;
        this.jekyllService = jekyllService;
    }

    /**
     * Gets the information of the newest commit
     */
    public void  getCommitInformation() {
            Repository repo;
            try (Git git = new Git(repo = LocalRepoCreater.getLocalGit().getRepository())) {
                //Getting The Commit Information of the Remote Repository
                RevWalk walker = new RevWalk(repo);
                RevCommit commit = walker.parseCommit(repo.resolve("HEAD"));
                Date commitTime = commit.getAuthorIdent().getWhen();
                String commiterName = commit.getAuthorIdent().getName();
                String commitEmail = commit.getAuthorIdent().getEmailAddress();
                String commitID = repo.resolve("HEAD").getName();

                // compare local and remote branch
                this.checkForUpdates(git, commiterName, commitEmail, commitTime, commitID);
            } catch (Exception e) {
                
            }
            finally {
                localGit.close();    
            }
    } 
    

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
    private void checkForUpdates(Git git, String name, String email, Date timestamp, String commitID) {
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

            // check if there is a diff between the local commit and the remote commit
            // if there are no updates, return
            // if there are updates
            //   trigger build process
            //   if build success
            //     copy all generated xml based on the diff information between local and remote
            //     copy all images
            //     commit and push to remote repo
            
            // check if uptades found
            if(commitJSON.get("Name").equals(name) && commitJSON.get("Email").equals(email)
                    && commitJSON.get("Date").equals(timestamp.toString())&& commitJSON.get("CommitID").equals(commitID)){
                LOGGER.info("No updates found.");
            }
            else {
                LOGGER.info("Updates found.");
                imageTransfer.deleteImages(new File(LOCAL_DEST_IMAGE + "/Cropped_Resized"));
                // if build process success, copy generated xml and images from _site to dest folder



                // Step 2
                jekyllService.startJekyllCI();
                // after jekyll build
                // copy xml from _site to assets, if files were deleted, it tries to delete the folder if it is empty
                markdownTransformer.copyGeneratedXmlFiles(entries);
                LOGGER.info("Copy Images from devblog/_site/assets/images folder to devblog/assets/images");
                // copy all images from _site/assets to assets
                imageTransfer.moveGeneratedImages(new File(LOCAL_SITE_IMAGE), new File(LOCAL_DEST_IMAGE));
                // push changes on repo



                // Step 3
                repoPusher.pushRepo(entries);
            }


            // TODO infos for commit message move logic to in repoPusher
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
            // TODO change exit code later
            System.exit(11);
        } catch (ParseException e) {
            e.printStackTrace();
            // TODO change exit code later
            System.exit(12);
        }
    }
}
