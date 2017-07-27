package de.adesso.persistence;

/**
 * This class creates a post object with the given fields.
 */
public class Post {

    /* The content of the post */
    private String content;

    /* The teaserHtml text of the post */
    private String teaserHtml;

    /* List of the images included in this post */
//    private List<Image> images;

    private PostMetaData postMetaData;

    public Post() {
    }

    /**
     * constructor
     * Creates also an MD5Hash hashing type.
     */
    public Post(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeaserHtml() {
        return teaserHtml;
    }

    public void setTeaserHtml(String teaserHtml) {
        this.teaserHtml = teaserHtml;
    }

/*    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }*/

    public PostMetaData getPostMetaData() {
        return postMetaData;
    }

    public void setPostMetaData(PostMetaData postMetaData) {
        this.postMetaData = postMetaData;
    }

    @Override
    public String toString() {
        return "Post{" +
                ", content='" + content + '\'' +
                ", teaserHtml='" + teaserHtml + '\'' +
                '}';
    }
}
