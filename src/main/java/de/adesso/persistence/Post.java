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

    // Many posts belong to one author
    /*@ManyToOne
    private Author author;*/

    // needed by JPA
    private Post() {
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
        int imageCounter = 0;
        for (Image image : images) {

            if (imageCounter < images.size()) {
                imagesString += image.toString() + ", ";
            }

            /*else {

            }*/
        }

        String retVal = "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", teaser='" + teaser + '\'' +
                "Images{" + imagesString + "}" +
                '}';
        return retVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
