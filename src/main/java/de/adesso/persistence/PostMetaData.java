package de.adesso.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class creates an Object containing the meta information (header) of the blog post
 * that was created using markdown language of jekyll.
 */
@Entity
public class PostMetaData {

    @Id
    @GeneratedValue
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

    private String tags;

    private String subline;

    private String languageMultiKeyword;

    private String contentTypeMultiKeyword;

    private String mimeTypeMultiKeyword;

    /* hash value of post content */
    private String hashValue;

    @ManyToMany
    @JoinTable(
            name = "post_author_relation",
            joinColumns = {@JoinColumn(name = "post_meta_data_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")}
    )
    @JsonIgnore
    private Set<Author> authors;

    /* existence is important for parsing post headers yaml format, but is not used */
    @JsonIgnore
    @Transient
    private String author;

    @OneToOne
    @JoinColumn(name = "post_id", unique = true)
    private Post post;

    public PostMetaData() {
        authors = new HashSet<>();
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

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public String getLanguageMultiKeyword() {
        return languageMultiKeyword;
    }

    public void setLanguageMultiKeyword(String languageMultiKeyword) {
        this.languageMultiKeyword = languageMultiKeyword;
    }

    public String getContentTypeMultiKeyword() {
        return contentTypeMultiKeyword;
    }

    public void setContentTypeMultiKeyword(String contentTypeMultiKeyword) {
        this.contentTypeMultiKeyword = contentTypeMultiKeyword;
    }

    public String getMimeTypeMultiKeyword() {
        return mimeTypeMultiKeyword;
    }

    public void setMimeTypeMultiKeyword(String mimeTypeMultiKeyword) {
        this.mimeTypeMultiKeyword = mimeTypeMultiKeyword;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String printAuthorsAsString() {
        StringBuilder author = new StringBuilder();
        if(authors != null) {
            final Iterator<Author> itr = authors.iterator();

            while(itr.hasNext()) {
                Author a = itr.next();
                author.append(a.toString());
                if(itr.hasNext()) {
                    author.append(",");
                }
            }
        }
        return author.toString();
    }

    @Override
    public String toString() {
        return "PostMetaData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", layout='" + layout + '\'' +
                ", categories='" + categories + '\'' +
                ", date=" + date +
                ", modifiedDate=" + modifiedDate +
                ", authors=" + printAuthorsAsString() +
                ", tags='" + tags + '\'' +
                ", subline='" + subline + '\'' +
                ", languageMultiKeyword='" + languageMultiKeyword + '\'' +
                ", contentTypeMultiKeyword='" + contentTypeMultiKeyword + '\'' +
                ", mimeTypeMultiKeyword='" + mimeTypeMultiKeyword + '\'' +
                ", post=" + post +
                '}';
    }
}