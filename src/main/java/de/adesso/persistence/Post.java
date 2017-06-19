package de.adesso.persistence;

import de.adesso.util.HashingType;
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
    @Column(columnDefinition = "TEXT")
    private String content;

    /* The teaser text of the post */
    @Column(columnDefinition = "TEXT")
    private String teaser;

    /* hash value of post content */
    @Column(columnDefinition = "VARCHAR2(64)")
    private String hashValue;

    /* List of the images included in this post */
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Image> images;

    @Transient
    private HashingType hashingType;

    // needed by JPA
    private Post() {
    }

    /** constructor
     * Creates also an MD5Hash hashing type.
     * */
    public Post(String content) {
        this.content = content;
        this.hashingType = new MD5Hash();
    }

    /**
     * Generates a hash value from the given string.
     * @param content
     */
    public void generateHashValue(String content) {
        this.hashValue = this.hashingType.generateHashValue(content);
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

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public HashingType getHashingType() {
        return hashingType;
    }

    public void setHashingType(HashingType hashingType) {
        this.hashingType = hashingType;
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
                ", teaser='" + teaser + '\'' +
                '}';
    }
}
