package de.adesso.service;

import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This service helps managing repositories with the help of JGit.
 */
@Service
public class MarkdownTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownTransformer.class);

    private final ConfigService configService;

    private FileTransfer fileTransfer;

    @Autowired
    public MarkdownTransformer(FileTransfer fileTransfer, ConfigService configService) {
        this.fileTransfer = fileTransfer;
        this.configService = configService;
    }

    /**
     * After a change in a markdown post was detected, the jekyll-build process
     * generates html and xml files to the corresponding markdown file. The xml
     * output has to be copied to an intended folder
     *
     * @param entries List with all changed files
     */
    protected void copyGeneratedXmlFiles(List<DiffEntry> entries) {

        entries.forEach((entry) -> {
            /*
			 * Assumption: every-blog- post-file with ending "markdown" has the following
			 * structure: _posts/2017-08-01-new-post-for-netlify-test.markdown
			 */
            String filePath;

            if (entry.getChangeType() == DiffEntry.ChangeType.DELETE) {
                filePath = entry.getOldPath();
            } else {
                filePath = entry.getNewPath();
            }

            /*
			 * RegEx to separate the filepath into
			 * the folders, filedate, filename and the markdown suffix
			 */
            String regex = "(((/.+/)|())(((\\d+-){3})(([^/\\.]+))))";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(filePath);

            if(filePath.startsWith("_posts") && matcher.find()){
                String folderStructure = matcher.group(2);
                String fullFileName = matcher.group(5);
                String fileName = matcher.group(8);
                String fileDate = matcher.group(5).substring(0,10);

                String xmlFileName = fileName + ".xml";

				/*
				 * The Jekyll xml built is located at
				 * "/_site/blog-posts/2017-08-01/new-post-title/2017-08-01-new-post-title.xml
				 */
                File source = new File(String.format("%s%s/%s/%s/%s", configService.getLOCAL_HTML_POSTS(), folderStructure, fileDate, fileName, xmlFileName));
                File dest = new File(String.format("%s%s/%s/%s.xml", configService.getFIRSTSPIRIT_XML_PATH(), folderStructure, fileDate, fullFileName));
                Path dirPath = new File(String.format("%s%s/%s", configService.getFIRSTSPIRIT_XML_PATH(), folderStructure, fileDate)).toPath();

                if (entry.getChangeType() == DiffEntry.ChangeType.DELETE) {
                    try {
                        Files.delete(dest.toPath());
						/* Checking if the directory of the File is empty
						 * Then delete it
						 */
                        if (!Files.newDirectoryStream(dirPath).iterator().hasNext()) {
                            Files.delete(dirPath);
                        }
                    } catch (IOException e) {
                        LOGGER.error("An error occurred while deleting files.", e);
                        LOGGER.error("Exiting jekyll2cms.");
                        System.exit(391);
                    }
                } else {
                    fileTransfer.copyFile(source, dest);
                }
            }
        });
        fileTransfer.deleteXmlFromSiteFolder();
    }
}
