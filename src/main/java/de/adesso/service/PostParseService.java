package de.adesso.service;


import de.adesso.persistence.Image;
import de.adesso.persistence.Post;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
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

    /**
     * extracts all necessary information to create post objects and write them into the database.
     * @return List - List of post objects
     */
    public List<Post> getAllHtmlPosts() {
        return this.listHtmlPostFiles(LOCAL_HTML_POSTS_PATH);
    }

    /**
     * Searches and lists HTML files within the given file path.
     *
     * @param rootPath The root path that is used for searching the file tree.
     * @return List - List of posts
     */
    private List<Post> listHtmlPostFiles(String rootPath) {
        final Post[] post = {null};
        ArrayList<Post> posts = new ArrayList<>();
        try (Stream<Path> stream = Files.walk(Paths.get(rootPath))) {
            stream
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String filePath = path.toString();
                        if (this.isHtmlFile(filePath)) {
                            // get the contents of the post
                            String htmlContent = getHtmlPostContent(new File(filePath));
                            post[0] = new Post(htmlContent, "", null);
                            post[0].setTeaser(this.getPostContentFirstParagraph(htmlContent));
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
     * Extracts the HTML post content of the HTML file.
     *
     * @param htmlFile The HTML post file
     * @return String - Post content as HTML code
     */
    private String getHtmlPostContent(File htmlFile) {
        String htmlContent = "";
        Document doc = null;
        try {
            doc = Jsoup.parse(htmlFile, "UTF-8");
            Elements articles = doc.getElementsByTag("article");
            for (Element e1 : articles) {
                htmlContent = e1.outerHtml();
            }
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
    private String getPostContentFirstParagraph(String htmlPostContent) {
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
    public List<Image> getImages(Post post) {
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