package de.adesso.service;

import de.adesso.persistence.Image;
import de.adesso.persistence.Post;
import de.adesso.persistence.PostMetaData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class extracts the HTML data of a blog post and maps the information to Post and Image objects.
 */
@Service
public class PostParseService {

    /* The path where the generated jekyll files live. */
    @Value("${repository.local.htmlposts.path}")
    private String LOCAL_HTML_POSTS_PATH;

    @Value("${repository.local.path}")
    private String LOCAL_REPO_PATH;

    @Autowired
    private ParseService parseService;

    /**
     * Searches HTML files within the given file path an generate posts from them.
     *
     * @return List - List of posts
     */
    public List<Post> getAllHtmlPosts() {
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
                            post.setTeaserXml(extractPostContentFirstParagraph(htmlContent));
                            post.generateHashValue(htmlContent);
                            posts.add(post);
                        }
                    });
            System.out.println("Successfully parsed all HTML post files to post objects!");
        } catch (IOException e) {
            System.err.println("There was an error reading files: " + e.getMessage());
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
        File[] metadataFiles = new File(LOCAL_REPO_PATH + "/_posts").listFiles(File::isFile);

        PostMetaData postMetaData = null;
        for (File htmlFile : htmlFiles) {
            String htmlFilePath = htmlFile.getAbsolutePath();

            if (isHtmlFile(htmlFilePath)) {
                String htmlFileNameNoExt = cutOffFileExtension(htmlFile.getName());
                String parentFileName = htmlFile.getParentFile().getName();
                String htmlContent = extractHtmlPostContent(new File(htmlFilePath));

                for (File metadataFile : metadataFiles) {
                    String metadataFileNameNoExt = cutOffFileExtension(metadataFile.getName());
                    if (metadataFileNameNoExt.equals(parentFileName + "-" + htmlFileNameNoExt)
                            && htmlContent.equals(post.getContent())) {
                        postMetaData = parseService.getMetaInformationFromPost(metadataFile);
                        postMetaData.setPost(post);
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
     * cut off the extension of file names.
     *
     * @param fileName - The file name of interest.
     * @return String
     */
    private String cutOffFileExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * Extracts the HTML post content of the HTML file.
     *
     * @param htmlFile The HTML post file
     * @return String - Post content as HTML code
     */
    private String extractHtmlPostContent(File htmlFile) {
        Document doc = null;
        try {
            doc = Jsoup.parse(htmlFile, "UTF-8");
        } catch (IOException e) {
            System.err.println(e.getMessage());
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
}