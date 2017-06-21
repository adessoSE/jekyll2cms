package de.adesso.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * This class creates an Object containing the meta information (header) of the blog post
 * that was created using markdown language of jekyll.
 */
@Entity
@Table(name = "POST_META_DATA")
public class PostMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String title;

    @Column(columnDefinition = "VARCHAR(50)")
    private String layout;

    @Column(columnDefinition = "VARCHAR(150)")
    private String categories;

    // TODO: check correct date format or change to a date object
    @Column(name = "POSTED_DATE", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Date date;

    // TODO: check correct date format or change to a date object
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;

    @Column(columnDefinition = "VARCHAR(100)", nullable = false)
    private String author;

    @Column(columnDefinition = "VARCHAR(150)")
    private String tags;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_ID")
    private Post post;

    // needed by JPA
    private PostMetaData() {
    }

    public PostMetaData(String title, String layout, String categories,
                        Date date, Date modifiedDate, String author, String tags,
                        Post post) {
        this.title = title;
        this.layout = layout;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
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