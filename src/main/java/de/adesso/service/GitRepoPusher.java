package de.adesso.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class GitRepoPusher {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepoPusher.class);

    private final String GIT_COMMIT_MESSAGE = "New First Spirit XML files added automatically by jekyll2cms";

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
		/*
		 * Assumption: the XML-posts will be pushed into the same repository where the
		 * markdown-posts were pushed, too. If another repository is intended for the
		 * First-Spirit-XML files, another implementation (other remote repository etc.)
		 * is necessary
		 */
        Git localGit = LocalRepoCreater.getLocalGit();
        JSONObject commitInfo = new JSONObject();

        if (localGit != null) {
            try {
                LOGGER.info("Pushing XML files to repository");
                localGit.add().addFilepattern(".").setUpdate(false).call();
                //Iterates through entries to find deleted File
                if (entries.iterator().next().getChangeType() == DiffEntry.ChangeType.DELETE){
                    localGit.add().addFilepattern("-A").setUpdate(false).call();
                }

                // TODO: set message with new infos
                PersonIdent personIdent = localGit.commit().setAll(true).setMessage(GIT_COMMIT_MESSAGE)
                        .setAuthor(configService.getGIT_AUTHOR_NAME(), configService.getGIT_AUTHOR_MAIL()).call().getAuthorIdent();

                /*
                 * Taking the Information from the Commit of the Update
                 * and saving it into a the local Commit Information
                 * JSON File
                 * The Remote Repo has the JSON File of the Commit
                 * before this Commit
                 */
                // set information for json file
                commitInfo.put("Name", personIdent.getName());
                commitInfo.put("Email", personIdent.getEmailAddress());
                commitInfo.put("Date", personIdent.getWhen().toString());
                commitInfo.put("CommitID", localGit.getRepository().resolve("HEAD").getName());

                // write json file
                FileWriter jsonFile = new FileWriter(configService.getJSON_PATH());
                jsonFile.write(commitInfo.toJSONString());
                jsonFile.flush();
                jsonFile.close();

                CredentialsProvider cp = new UsernamePasswordCredentialsProvider(configService.getGIT_AUTHOR_NAME(), configService.getGIT_AUTHOR_PASSWORD());
                localGit.push().setForce(true).setCredentialsProvider(cp).call();
                LOGGER.info("Pushing XML files was successful");
            } catch (GitAPIException e) {
                LOGGER.error("An error occured while pushing files to remote repository");
                e.printStackTrace();
                System.exit(30);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(31);
            }
        }
    }
}
