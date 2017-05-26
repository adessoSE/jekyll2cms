package de.adesso.persistence;

import javax.persistence.*;

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

    private String categories;

    // TODO: check correct date format or change to a date object
    @Column(nullable = false)
    private String date;

    // TODO: check correct date format or change to a date object
    @Column(nullable = false)
    private String modifiedDate;

    @Column(nullable = false)
    private String author;

    private String[] tags;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_ID")
    private Post post;

    // needed by JPA
    private PostMetaData() {
    }

    public PostMetaData(String title, String layout, String slug, String categories,
                        String date, String modifiedDate, String author, String[] tags,
                        Post post) {
        this.title = title;
        this.layout = layout;
        this.slug = slug;
        this.categories = categories;
        this.date = date;
        this.modifiedDate = modifiedDate;
        this.author = author;
        this.tags = tags;
        this.post = post;
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

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
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
                ", tags=" + tags +
                '}';
    }
}
