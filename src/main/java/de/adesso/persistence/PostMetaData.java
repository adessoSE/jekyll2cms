package de.adesso.persistence;

import javax.persistence.*;
import java.util.Arrays;

/**
 * This class creates an Object containing the meta information (header) of the blog post
 * that was created using markdown language of jekyll.
 */
@Entity
@Table(name = "POST_META_DATA")
public class PostMetaData {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    private String layout;

    private String slug;

    //@Column(columnDefinition = "VARCHAR2(200)")
    private String categories;

    // TODO: check correct date format or change to a date object
    private String date;

    // TODO: check correct date format or change to a date object
    private String modifiedDate;

    private String author;

    //@Column(columnDefinition = "VARCHAR2(200)")
    private String[] tags;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_ID")
    private Post post;

    /**
     * Needed private constructor for JPA
     */
    private PostMetaData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "PostMetaData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", categories=" + categories +
                ", date=" + date +
                ", modifiedDate=" + modifiedDate +
                ", author='" + author + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PostMetaData that = (PostMetaData) o;

        if (!id.equals(that.id)) return false;
        return title.equals(that.title);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }
}
