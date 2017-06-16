package de.adesso.persistence;

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
    @Column(columnDefinition = "TEXT")
    private String content;

    /* The teaser text of the post */
    @Column(columnDefinition = "TEXT")
    private String teaser;

    /* List of the images included in this post */
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Image> images;

    // needed by JPA
    private Post() {
    }

    public Post(String content, String teaser, List<Image> images) {
        this.content = content;
        this.teaser = teaser;
        this.images = images;
    }

    public Post(String content) {
        this.content = content;
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

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", teaser='" + teaser + '\'' +
                '}';
    }
}
