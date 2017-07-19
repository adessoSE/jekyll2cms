package de.adesso.persistence;

import de.adesso.util.MD5Hash;

import javax.persistence.*;
import java.util.List;

/**
 * This class creates a post object with the given fields.
 */
@Entity
public class Post {

    /* unique ID of the post */
    @Id
    @GeneratedValue
    private Long id;

    /* The content of the post */
    private String content;

    /* The teaserHtml text of the post */
    private String teaserHtml;

    /* hash value of post content */
    private String hashValue;

    /* List of the images included in this post */
    @Transient
    private List<Image> images;

    @OneToOne(mappedBy = "post")
    private PostMetaData postMetaData;

    public Post() {
    }

    /**
     * constructor
     * Creates also an MD5Hash hashing type.
     */
    public Post(String content) {
        this.content = content;
    }

    /**
     * Generates a hash value from the given string.
     *
     * @param content
     */
    public void generateHashValue(String content) {
        this.hashValue = MD5Hash.generateHashValue(content);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeaserHtml() {
        return teaserHtml;
    }

    public void setTeaserHtml(String teaserHtml) {
        this.teaserHtml = teaserHtml;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", teaserHtml='" + teaserHtml + '\'' +
                '}';
    }
}
