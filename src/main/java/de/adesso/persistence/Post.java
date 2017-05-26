package de.adesso.persistence;

import javax.persistence.*;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String teaser;

    @OneToMany(mappedBy = "post")
    private List<Image> images;

    // needed by JPA
    private Post() {
    }

    public Post(String content, String teaser, List<Image> images) {
        this.content = content;
        this.teaser = teaser;
        this.images = images;
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
        String imagesString = "";
        for (Image image : images) {
            imagesString += image.toString() + ", ";
        }
        String retVal = "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", teaser='" + teaser + '\'' +
                "Images{" + imagesString + "}" +
                '}';
        return retVal;
    }
}
