package com.oneplusapp.common;

import com.oneplusapp.model.Post;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiewei on 10/10/15.
 */
public class BookmarkService {

    private Callback callback;

    private static Map<Integer, Post> bookmarkedPosts = new HashMap<>();

    private static BookmarkService instance = new BookmarkService();

    public static BookmarkService getInstance() {
        return instance;
    }

    private BookmarkService() {
    }

    public void addBookmark(Post post) {
        if (bookmarkedPosts.put(post.getId(), post) == null && callback != null) {
            callback.onBookmarkChanged();
        }
    }

    public void removeBookmark(Post post) {
        if (bookmarkedPosts.remove(post.getId()) != null && callback != null) {
            callback.onBookmarkChanged();
        }
    }

    public boolean isBookmarked(Post post) {
        return bookmarkedPosts.containsKey(post.getId());
    }

    public void updateBookmarkCache(Post[] posts) {
        Map<Integer, Post> serverBookmarkedPosts = new HashMap<>();
        for (Post p : posts) {
            serverBookmarkedPosts.put(p.getId(), p);
        }
        if (!serverBookmarkedPosts.keySet().equals(bookmarkedPosts.keySet())) {
            bookmarkedPosts = serverBookmarkedPosts;
            if (callback != null) {
                callback.onBookmarkChanged();
            }
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onBookmarkChanged();
    }
}