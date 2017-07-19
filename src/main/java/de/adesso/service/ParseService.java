package de.adesso.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adesso.persistence.Author;
import de.adesso.persistence.PostMetaData;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This services parses md files to pojo.
 */
@Service
public class ParseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseService.class);

    @Value("${repository.local.authorsfile}")
    private String AUTHORS_YAML_FILE;
    /**
     * Parses the meta data (header) of a jekyll markdown file into a PostMetaData object.
     *
     * @param mdFile the post from which you want to extract meta information from
     * @return parsed PostMetaData
     */
    public PostMetaData getMetaInformationFromPost(File mdFile) {

        // String representation of the MD file content
        String mdFileContent = getMdFileContent(mdFile);

        // Header of the mdFile, which is the YAML syntax
        String mdHeader = getMdHeader(mdFileContent);

        return getPostMetaData(mdHeader);
    }

    /**
     * Extracts the string content from a markdown file.
     *
     * @param mdFile File object
     * @return content of file as String
     */
    private String getMdFileContent(File mdFile) {
        String method = "getMdFileContent";

        String mdFileData = "";
        try {
            mdFileData = IOUtils.toString(new FileInputStream(mdFile), "utf-8");
        } catch (IOException ioe) {
            LOGGER.error("In method {}: Error while trying to get the content of a markdown file. Error message: {} ", method, ioe.getMessage());
        }
        return mdFileData;
    }

    /**
     * Extracts the header from the markdown file.
     *
     * @param mdFileData data of markdown file
     * @return header data as String
     */
    private String getMdHeader(String mdFileData) {
        String mdHeader = "";
        final String headerIndicator = "---";
        if(mdFileData.startsWith(headerIndicator)) {
            mdHeader = mdFileData.split(headerIndicator, 4)[1];
        }
        return mdHeader;
    }

    /**
     * Creates an object from a String that has a YAML syntax.
     *
     * @param mdHeader String from which the PostMetaData object should be created
     * @return parsed PostMetaData
     */
    private PostMetaData getPostMetaData(String mdHeader) {
        AuthorsYamlService ays = new AuthorsYamlService();
        String method = "getPostMetaData";

        PostMetaData postMetaData = null;
        ObjectMapper mapper = null;

        try {
            // map Header to PostMetaData object
            mapper = new ObjectMapper(new YAMLFactory());
            postMetaData = mapper.readValue(mdHeader.getBytes(), PostMetaData.class);

            // mapper = new ObjectMapper(new YAMLFactory());
            JsonNode root = mapper.readTree(mdHeader);

            // Get author node
            String authorName = root.findValue("author").asText();

            Author author = ays.findAuthorInAuthorsYamlFile(AUTHORS_YAML_FILE, authorName);
            postMetaData.getAuthors().add(author);

            // postMetaData = mapper.readValue(mdHeader.getBytes(), PostMetaData.class);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            LOGGER.info("Parsed meta information: {}-{}", sdf.format(postMetaData.getDate()), postMetaData.getTitle() );

        } catch (IOException ioe) {

            LOGGER.error("In method {}: Error while mapping meta information. Error message: {} ", method, ioe.getMessage());
        }
        return postMetaData;
    }

}
