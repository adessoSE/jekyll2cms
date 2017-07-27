package de.adesso.persistence;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class Author {

    private Long id;

    private String name;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("git_username")
    private String gitUsername;

    @JsonProperty("picture_url")
    private String pictureUrl;

    private String bio;

    private String github;

    private String email;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
    public String toString() {
        return "Author{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gitUsername='" + gitUsername + '\'' +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", github='" + github + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
