package de.adesso.persistence;

import javax.persistence.*;

/**
 * This class represents an image entity.
 */
@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "VARCHAR2(2000)")
    private String url;

    private String format;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Post post;

    private Image() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        return id.equals(image.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
