package de.adesso.persistence;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Author {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @JsonProperty("git_username")
    private String gitUsername;
    @JsonProperty("picture_url")
    private String pictureUrl;
    private String bio;
    private String github;
    private String email;


    @ManyToMany(mappedBy = "authors")
    private Set<PostMetaData> posts;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGitUsername() {
        return gitUsername;
    }

    public void setGitUsername(String gitUsername) {
        this.gitUsername = gitUsername;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<PostMetaData> getPosts() {
        return posts;
    }

    public void setPosts(Set<PostMetaData> posts) {
        this.posts = posts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        if (!id.equals(author.id)) return false;
        return name.equals(author.name);
    }

    @Override
    public int hashCode() {
        int result = 23;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", github='" + github + '\'' +
                ", email='" + email + '\'' +
                ", posts=" + posts +
                '}';
    }
}
