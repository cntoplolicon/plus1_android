package com.oneplusapp.model;

import org.joda.time.DateTime;

/**
 * Created by cntoplolicon on 9/23/15.
 */
public class Post {

    public static final int DELETED_BY_AUTHOR = 1;
    public static final int DELETED_BY_ADMIN = 2;

    private int id;
    private int spreadsCount;
    private int viewsCount;
    private int commentsCount;
    private boolean bookmarked;
    private Integer deletedBy;
    private User user;
    private PostPage[] postPages;
    private Comment[] comments;
    private DateTime createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSpreadsCount() {
        return spreadsCount;
    }

    public void setSpreadsCount(int spreadsCount) {
        this.spreadsCount = spreadsCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public Integer getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Integer deletedBy) {
        this.deletedBy = deletedBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PostPage[] getPostPages() {
        return postPages;
    }

    public void setPostPages(PostPage[] postPages) {
        this.postPages = postPages;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }
}
