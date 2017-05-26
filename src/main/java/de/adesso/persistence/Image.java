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

    @Id
    @GeneratedValue
    private Long id;
    private String url;
    private String format;

    @ManyToOne
    private Post post;

    // needed by JPA
    private Image() {
    }

    public Image(String url, String format) {
        this.url = url;
        this.format = format;
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
