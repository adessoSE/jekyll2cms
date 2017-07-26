package de.adesso.service;

import de.adesso.persistence.Image;
import de.adesso.persistence.Post;
import de.adesso.persistence.PostMetaData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class extracts the HTML data of a blog post and maps the information to Post and Image objects.
 */
@Service
public class PostParseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostParseService.class);

    /* The path where the generated jekyll files live. */
    @Value("${repository.local.htmlposts.path}")
    private String LOCAL_HTML_POSTS_PATH;

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Autowired
    private ParseService parseService;

    @Autowired
    private RepoService repoService;

    /**
     * Searches HTML files within the given file path an generate posts from them.
     *
     * @return List - List of posts
     */
    public List<Post> getAllHtmlPosts() {
        String method = "getAllHtmlPosts";
        ArrayList<Post> posts = new ArrayList<>();
        try (Stream<Path> htmlFiles = Files.walk(Paths.get(LOCAL_HTML_POSTS_PATH))) {
            htmlFiles
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String filePath = path.toString();
                        if (isHtmlFile(filePath)) {
                            // get the contents of the post
                            String htmlContent = extractHtmlPostContent(new File(filePath));
                            Post post = new Post(htmlContent);
                            post.setTeaserHtml(extractPostContentFirstParagraph(htmlContent));
                            posts.add(post);
                        }
                    });
            LOGGER.info("Successfully parsed all HTML post files to post objects!");
        } catch (IOException e) {
            LOGGER.error("In method {}: There was an error reading files: {}", method, e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * Finds the corresponding metadata file of the given post.
     *
     * @param post - the post object for which the metadata file is searched.
     * @return PostMetaData
     */
    public PostMetaData findCorrespondingMetadataFile(Post post) {

        List<File> htmlFiles = extractAllHtmlFilesFromDirectory();
        File[] metadataFiles = new File(LOCAL_REPO_PATH + "_posts").listFiles(File::isFile);

        PostMetaData postMetaData = null;
        for (File htmlFile : htmlFiles) {
            String htmlFilePath = htmlFile.getAbsolutePath();

            if (isHtmlFile(htmlFilePath)) {
                String htmlFileNameNoExt = cutOffFileExtension(htmlFile.getName());
                String parentFileName = htmlFile.getParentFile().getName();
                String htmlContent = extractHtmlPostContent(new File(htmlFilePath));

                for (File metadataFile : metadataFiles) {
                    String FileName = metadataFile.getName();
                    String parent = metadataFile.getParent();
                    // generates: _posts/filename.markdown
                    String metadataRepositoryFilePath = String.format("%s/%s", parent.substring(parent.lastIndexOf('\\') + 1), FileName);
                    String metadataFileNameNoExt = cutOffFileExtension(metadataFile.getName());
                    if (metadataFileNameNoExt.equals(parentFileName + "-" + htmlFileNameNoExt)
                            && htmlContent.equals(post.getContent())) {
                        postMetaData = parseService.getMetaInformationFromPost(metadataFile);
                        postMetaData.setPost(post);
                        postMetaData.setRepositoryFilePath(metadataRepositoryFilePath);
                        postMetaData.setFirstCommitDate(retrieveCommitTime(postMetaData.getRepositoryFilePath(), CommitOrder.FIRST));
                        postMetaData.setLastCommitDate(retrieveCommitTime(postMetaData.getRepositoryFilePath(), CommitOrder.LAST));
                        return postMetaData;
                    }
                }

            }
        }

        return postMetaData;
    }

    /**
     * Extracts the image information of the post and puts them into an Image object.
     *
     * @param post - The corresponding post of the images
     * @return List - List of Image objects
     */
    public List<Image> extractImages(Post post) {
        List<Image> imageList = new ArrayList<>();
        Document doc = Jsoup.parse(post.getContent());
        Elements images = doc.select("img");
        images.forEach(imageElement -> {
            String imageUrl = imageElement.attr("src");
            String imageFormat = imageUrl.substring(imageUrl.lastIndexOf('.') + 1).toUpperCase();
            Image image = new Image(imageUrl, imageFormat, post);
            imageList.add(image);
        });
        return imageList;
    }

    /**
     * TODO Optimization for case where file does not have extension
     * cut off the extension of file names.
     *
     * @param fileName - The file name of interest.
     * @return String
     */
    private String cutOffFileExtension(String fileName) {
        String fileNameWithoutExtension = "";
        if (fileName.contains(".")) {
            fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            fileNameWithoutExtension = fileName;
        }
        return fileNameWithoutExtension;
    }

    /**
     * Extracts the HTML post content of the HTML file.
     *
     * @param htmlFile The HTML post file
     * @return String - Post content as HTML code
     */
    private String extractHtmlPostContent(File htmlFile) {
        String method = "extractHtmlPostContent";
        Document doc = null;
        try {
            doc = Jsoup.parse(htmlFile, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("In method {}: There was an error reading files. Error message: {}", method, e.getMessage());
            e.printStackTrace();
        }
        return doc.html();
    }

    /**
     * gets the first paragraph of the post content. It will be used as the teaser/preview portion of the post.
     *
     * @param htmlPostContent The HTML code of the post content
     * @return String - The first paragraph as HTML code
     */
    private String extractPostContentFirstParagraph(String htmlPostContent) {
        Document doc = Jsoup.parse(htmlPostContent);
        Element paragraph = doc
                .getElementsByClass("post-content").first()
                .getElementsByTag("p").first();
        return paragraph.outerHtml();
    }

    private boolean isHtmlFile(String filePath) {
        return filePath.endsWith(".htm") || filePath.endsWith(".html");
    }

    private List<File> extractAllHtmlFilesFromDirectory() {
        File[] htmlFileFolders = new File(LOCAL_HTML_POSTS_PATH).listFiles(File::isDirectory);
        List<File> htmlFiles = new ArrayList<>();
        for (File folder : htmlFileFolders) {
            for (File htmlFile : folder.listFiles(File::isFile)) {
                htmlFiles.add(htmlFile);
            }
        }
        return htmlFiles;
    }

    /**
     * retrieves date of the first commit of the provided file
     *
     * @param repoFilePath - provided file
     * @return Date
     */
    private Date retrieveCommitTime(String repoFilePath, CommitOrder order) {
        Date commitTime = null;
        Map<String, List<Date>> commitTimes = repoService.retrieveCommitTimesOfPostFiles();
        Set<String> filePaths = commitTimes.keySet();
        if (filePaths.contains(repoFilePath)) {
            switch (order) {
                case FIRST:
                    commitTime = commitTimes.get(repoFilePath).get(0);
                    break;
                case LAST:
                    int last = commitTimes.get(repoFilePath).size() - 1;
                    commitTime = commitTimes.get(repoFilePath).get(last);
                    break;
                default:
                    commitTime = commitTimes.get(repoFilePath).get(0);
            }
        }
        return commitTime;
    }

    /**
     * Configures which commit should be retrieved with the method retrieveCommitTime(String, CommitOrder).
     */
    enum CommitOrder {
        // First commit of file (for created date)
        FIRST,
        // Last commit of file (for modified date)
        LAST
    }
}