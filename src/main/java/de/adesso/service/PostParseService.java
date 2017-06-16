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
import java.util.stream.Collectors;
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
     * extracts all necessary information to create post objects and write them into the database.
     *
     * @return List - List of post objects
     */
    public List<Post> getAllHtmlPosts() {
        return this.listAllHtmlPostFiles(LOCAL_HTML_POSTS_PATH);
    }

    /**
     * Searches and lists HTML files within the given file path.
     *
     * @param rootPath The root path that is used for searching the file tree.
     * @return List - List of posts
     */
    private List<Post> listAllHtmlPostFiles(String rootPath) {
        final Post[] post = {null};
        ArrayList<Post> posts = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(Paths.get(rootPath))) {
            stream
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String filePath = path.toString();
                        if (this.isHtmlFile(filePath)) {
                            // get the contents of the post
                            String htmlContent = extractHtmlPostContent(new File(filePath));
                            post[0] = new Post(htmlContent);
                            post[0].setTeaser(this.extractPostContentFirstParagraph(htmlContent));
                            posts.add(post[0]);
                            System.out.println("Parsed HTML post file to post object!");
                        }
                    });
            System.out.println("Parsed all HTML post files to post objects!");
        } catch (IOException e) {
            System.err.println("There was an error reading files: " + e.getMessage());
            e.printStackTrace();
        }
        return posts;
    }

    /**
     * @return all unrendered post files in markdown format.
     */
    protected List<File> listAllMetadataFiles() {
        ArrayList<File> posts = new ArrayList<>();
        File postsFolder = new File(LOCAL_REPO_PATH + "/_posts");
        for (File fileEntry : postsFolder.listFiles()) {
            posts.add(fileEntry);
        }
        return posts;
    }

    /**
     * Finds the corresponding metadata file of the given post.
     *
     * @param post - the post object for which the metadata file is searched.
     * @return PostMetaData
     */
    protected PostMetaData findCorrespondingMetadataFile(Post post) {
        PostMetaData postMetaData = null;

        List<Path> htmlFiles = null;
        try (Stream<Path> stream = Files.walk(Paths.get(LOCAL_HTML_POSTS_PATH))) {
            htmlFiles = stream.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<File> metadataFiles = listAllMetadataFiles();
        // TODO: wenn beide Listen ungleich null, sonst throw exception
        for (Path htmlFile : htmlFiles) {
            String htmlFilePath = htmlFile.toString();

            if (this.isHtmlFile(htmlFilePath)) {
                String htmlFileNameNoExt = this.cutOffFileExtension(htmlFile.getFileName().toString());
                String parentFileName = htmlFile.getParent().getFileName().toString();
                String htmlContent = extractHtmlPostContent(new File(htmlFilePath));

                for (File metadataFile : metadataFiles) {
                    String metadataFileNameNoExt = this.cutOffFileExtension(metadataFile.getName());
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
        String htmlContent = "";
        Document doc = null;
        try {
            doc = Jsoup.parse(htmlFile, "UTF-8");
            htmlContent = doc.html();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return htmlContent;
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
        System.out.println("Parsed image information of the post to image object!");
        return imageList;
    }

    /**
     * Checks if given file path is an HTML or HTM file
     *
     * @param filePath path of the file
     * @return boolean
     */
    private boolean isHtmlFile(String filePath) {
        return filePath.substring(filePath.lastIndexOf('.') + 1).equalsIgnoreCase("HTML")
                || filePath.substring(filePath.lastIndexOf('.') + 1).equalsIgnoreCase("HTM");
    }
}