package swj.swj.common;

import java.util.HashMap;
import java.util.Map;

import swj.swj.model.Post;

/**
 * Created by jiewei on 10/10/15.
 */
public class BookmarkService {

    private static Map<Integer, Post> bookmarkedPosts = new HashMap<>();

    private static BookmarkService instance = new BookmarkService();

    public static BookmarkService getInstance() {
        return instance;
    }

    private BookmarkService() {
    }

    public void addBookmark(Post post) {
        bookmarkedPosts.put(post.getId(), post);
    }

    public void removeBookmark(Post post) {
        bookmarkedPosts.remove(post.getId());
    }

    public boolean isBookmarked(Post post) {
        return bookmarkedPosts.containsKey(post.getId());
    }

    public void updateBookmarkCache(Post[] posts) {
        bookmarkedPosts.clear();
        for (Post p : posts) {
            bookmarkedPosts.put(p.getId(), p);
        }
    }

    public Post[] getBookmarkedPosts() {
        return BookmarkService.bookmarkedPosts.values().toArray(new Post[]{});
    }
}
