package de.adesso.persistence;


import javax.persistence.*;
import java.util.Set;

@Entity
public class Author {
    @Id
    @GeneratedValue
    private Long id;

    private String firstName;
    private String lastName;
    private String gitUsername;
    private String pictureUrl;
    private String bio;
    private String githubUrl;
    private String emailAddress;

    @ManyToMany(mappedBy = "authors")
    private Set<PostMetaData> posts;

    public Author() {
    }

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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
        if (firstName != null ? !firstName.equals(author.firstName) : author.firstName != null) return false;
        if (lastName != null ? !lastName.equals(author.lastName) : author.lastName != null) return false;
        return gitUsername.equals(author.gitUsername);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + gitUsername.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Author{\n" +
                "id=" + id +
                ", \nfirstName='" + firstName + '\'' +
                ", \nlastName='" + lastName + '\'' +
                ", \ngitUsername='" + gitUsername + '\'' +
                ", \npictureUrl='" + pictureUrl + '\'' +
                ", \nbio='" + bio + '\'' +
                ", \ngithubUrl='" + githubUrl + '\'' +
                ", \nemailAddress='" + emailAddress + '\'' +
                '}';
    }
}
