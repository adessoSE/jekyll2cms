package de.adesso.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adesso.components.PostMetaInformation;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This services parses md files to pojo.
 */
@Service
public class ParseService {

    private final static Logger LOGGER = Logger.getLogger(ParseService.class);

    // regular expression, used to identify the header of the markdown file
    private final String REGEX_MDFILE = "(-{3}((.|\\n|\\r)*)-{3})((.|\\n|\\r)*)";

    /**
     * Parses the meta data (header) of a jekyll markdown file into a PostMetaInformation object.
     *
     * @param mdFile the post from which you want to extract meta information from
     * @return parsed PostMetaInformation
     */
    public PostMetaInformation parseFile(File mdFile) {
        LOGGER.info("> Starting to parse markdown File.");

        // String representation of the MD file content
        String mdFileContent = getMdFileContent(mdFile);

        // Header of the mdFile, which is the YAML syntax
        String mdHeader = getMdHeader(mdFileContent, REGEX_MDFILE);

        return getPostMetaInformation(mdHeader);
    }

    /**
     * Extracts the string content from a markdown file.
     *
     * @param mdFile File object
     * @return content of file as String
     */
    private String getMdFileContent(File mdFile) {
        String mdFileData = "";
        try {
            mdFileData = IOUtils.toString(new FileInputStream(mdFile), "utf-8");
        } catch (IOException ioe) {
            LOGGER.error("Error while trying to get the content of markdown file. ", ioe);
        }
        return mdFileData;
    }

    /**
     * Extracts the header from the markdown file.
     *
     * @param mdFileData data of markdown file
     * @param regex      used regular expression to find the header
     * @return header data as String
     */
    private String getMdHeader(String mdFileData, String regex) {
        String mdHeader = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mdFileData);
        if (matcher.matches()) {
            mdHeader = matcher.group(1);
        }
        return mdHeader;
    }

    /**
     * Creates an object from a String that has a YAML syntax.
     *
     * @param mdHeader String from which the PostMetaInformation object should be created
     * @return parsed PostMetaInformation
     */
    private PostMetaInformation getPostMetaInformation(String mdHeader) {

        PostMetaInformation postMetaInformation = null;
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            postMetaInformation = mapper.readValue(mdHeader.getBytes(), PostMetaInformation.class);
            LOGGER.info("Parsed meta information: " + postMetaInformation);

        } catch (IOException ioe) {
            LOGGER.error("Error while mapping meta information. ", ioe);
        }
        return postMetaInformation;
    }

}
