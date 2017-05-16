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

@Service
public class ParseService {

    // regular expression, used to identify the header of the markdown file
    private final String REGEX_MDFILE = "(-{3}((.|\\n|\\r)*)-{3})((.|\\n|\\r)*)";

    private final static Logger LOGGER = Logger.getLogger(ParseService.class);

    /**
     * parses the meta data (header) of a jekyll markdown file into a PostMetaInformation object.
     *
     * @param fileName file name of the markdown file
     * @return PostMetaInformation object
     */
    public PostMetaInformation parseFile(String fileName) {
        LOGGER.info("> Starting to parse markdown File.: ");
        // markdown file
        File mdFile = new File(fileName);

        // String representation of the MD file content
        String mdFileContent = getMdFileContent(mdFile);

        // Header of the mdFile, which is the YAML syntax
        String mdHeader = getMdHeader(mdFileContent, REGEX_MDFILE);

        return getPostMetaInformation(mdHeader);
    }

    /**
     * extracts the string content from a markdown file.
     *
     * @param mdFile File object
     * @return content of file as String
     */
    private String getMdFileContent(File mdFile) {
        LOGGER.info("> Starting to get markdown file content: ");
        String mdFileData = "";
        try {
            mdFileData = IOUtils.toString(new FileInputStream(mdFile), "utf-8");
        } catch (IOException ioe) {
            LOGGER.error("Error while trying to get the content of markdown file. ", ioe);
        }
        return mdFileData;
    }

    /**
     * extracts the header from the markdown file.
     *
     * @param mdFileData data of markdown file
     * @param regex      used regular expression to find the header
     * @return header data as String
     */
    private String getMdHeader(String mdFileData, String regex) {
        LOGGER.info("> Starting to get markdown header: ");
        String mdHeader = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mdFileData);
        if (matcher.matches()) {
            mdHeader = matcher.group(1);
        }
        return mdHeader;
    }

    /**
     * creates an object from a String that has a YAML syntax.
     *
     * @param mdHeader String from which the PostMetaInformation object should be created
     * @return PostMetaInformation object
     * @throws IOException catches IOException
     */
    private PostMetaInformation getPostMetaInformation(String mdHeader) {
        LOGGER.info("> Starting to get post meta information from markdown header: ");
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
