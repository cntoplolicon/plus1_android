package swj.swj.model;

import org.joda.time.DateTime;

/**
 * Created by silentgod on 15-10-13.
 */
public class Comment {
    private int id;
    private int postId;
    private int replyToId;
    private String content;
    private User user;
    private DateTime createdAt;
    private DateTime updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(int replyToId) {
        this.replyToId = replyToId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DateTime getCreatedAt(){
        return createdAt;
    }

    public DateTime getUpdatedAt(){
        return updatedAt;
    }
}
