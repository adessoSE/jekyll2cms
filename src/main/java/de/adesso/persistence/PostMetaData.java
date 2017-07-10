package de.adesso.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * This class creates an Object containing the meta information (header) of the blog post
 * that was created using markdown language of jekyll.
 */
public class PostMetaData {

    private Long id;

    private String title;

    private String layout;

    private String categories;

    // TODO: check correct date format or change to a date object
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private Date date;

    // TODO: check correct date format or change to a date object
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;

    private String author;

    private String tags;

    private String subline;

    private Post post;

    public PostMetaData() {
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

    public String getSubline() {
        return subline;
    }

    public void setSubline(String subline) {
        this.subline = subline;
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