package de.adesso.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * This class creates an Object containing the meta information (header) of the blog post
 * that was created using markdown language of jekyll.
 */
@Entity
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

    private String tags;

    private String subline;

    private String language_multi_keyword;

    private String content_type_multi_keyword;

    private String mime_type_multi_keyword;

    /* hash value of post content */
    private String hashValue;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(
            name = "post_author_relation",
            joinColumns = {@JoinColumn(name = "post_meta_data_id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id")}
    )
    private Set<Author> authors;

    @OneToOne
    @JoinColumn(name = "post_id", unique = true)
    private Post post;

    public PostMetaData() {
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

    public String getLanguage_multi_keyword() {
        return language_multi_keyword;
    }

    public void setLanguage_multi_keyword(String language_multi_keyword) {
        this.language_multi_keyword = language_multi_keyword;
    }

    public String getContent_type_multi_keyword() {
        return content_type_multi_keyword;
    }

    public void setContent_type_multi_keyword(String content_type_multi_keyword) {
        this.content_type_multi_keyword = content_type_multi_keyword;
    }

    public String getMime_type_multi_keyword() {
        return mime_type_multi_keyword;
    }

    public void setMime_type_multi_keyword(String mime_type_multi_keyword) {
        this.mime_type_multi_keyword = mime_type_multi_keyword;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
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
                ", language_multi_keyword='" + language_multi_keyword + '\'' +
                ", content_type_multi_keyword='" + content_type_multi_keyword + '\'' +
                ", mime_type_multi_keyword='" + mime_type_multi_keyword + '\'' +
                ", post=" + post +
                '}';
    }
}