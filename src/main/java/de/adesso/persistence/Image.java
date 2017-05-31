package de.adesso.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * This class represents an image entity.
 */
@Entity
public class Image {

    /* unique ID of image entity*/
    @Id
    @GeneratedValue
    private Long id;
    /* URL of the image */
    private String url;
    /* image format */
    private String format;

    /* The post that belongs to this image */
    @ManyToOne
    private Post post;

    /** needed by JPA */
    private Image() {
    }

    /**
     * Constructor
     * @param url
     * @param format
     */
    public Image(String url, String format) {
        this.url = url;
        this.format = format;
    }

    /**
     * Contructor
     * @param url
     * @param format
     * @param post
     */
    public Image(String url, String format, Post post) {
        this.url = url;
        this.format = format;
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", format='" + format + '\'' +
                ", post=" + post +
                '}';
    }
}
