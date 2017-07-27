package de.adesso.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * This class creates an Object containing the meta information (header) of the blog post
 * that was created using markdown language of jekyll.
 */
public class PostMetaData {

    private String title;

    private String layout;

    private String categories;

    @JsonIgnore
    private Date firstCommitDate;

    @JsonIgnore
    private Date lastCommitDate;

    // TODO: check correct date format or change to a date object
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    // TODO: check correct date format or change to a date object
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;

    private String tags;

    private String subline;

    /* e.g. _posts/yyyy-MM-dd-Title.markdown */
    @JsonIgnore
    private String repositoryFilePath;

    private Author author;

    private Post post;

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getRepositoryFilePath() {
        return repositoryFilePath;
    }

    public void setRepositoryFilePath(String repositoryFilePath) {
        this.repositoryFilePath = repositoryFilePath;
    }

    public Date getFirstCommitDate() {
        return firstCommitDate;
    }

    public void setFirstCommitDate(Date firstCommitDate) {
        this.firstCommitDate = firstCommitDate;
    }

    public Date getLastCommitDate() {
        return lastCommitDate;
    }

    public void setLastCommitDate(Date lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    @Override
    public String toString() {
        return "PostMetaData{" +
                "title='" + title + '\'' +
                ", layout='" + layout + '\'' +
                ", categories='" + categories + '\'' +
                ", date=" + date +
                ", modifiedDate=" + modifiedDate +
                ", tags='" + tags + '\'' +
                ", subline='" + subline + '\'' +
                ", author='" + author + '\'' +
                ", post=" + post + '\'' +
                ", repositoryFilePath=" + (repositoryFilePath == null ? "Not set yet." : repositoryFilePath) + '\'' +
                ", firstCommitDate=" + (firstCommitDate == null ? "Not set yet." : firstCommitDate) + '\'' +
                ", lastCommitDate=" + (lastCommitDate == null ? "Not set yet." : lastCommitDate) +
                '}';
    }
}