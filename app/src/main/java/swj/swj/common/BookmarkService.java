package swj.swj.common;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by jiewei on 10/10/15.
 */
public class BookmarkService {

    public static HashSet<Integer> bookmarkedPostsIds = new HashSet<>();

    private static BookmarkService instance = new BookmarkService();

    public static BookmarkService getInstance() {
        return instance;
    }

    private BookmarkService() {
    }

    public void addBookmark(int postId) {
        bookmarkedPostsIds.add(postId);
    }

    public void removeBookmark(int postId) {
        bookmarkedPostsIds.remove(postId);
    }

    public boolean isBookmarked(int postId) {
        return bookmarkedPostsIds.contains(postId);
    }

    public void updateBookmarks(Collection<Integer> postIds) {
        bookmarkedPostsIds.clear();
        bookmarkedPostsIds.addAll(postIds);
    }

}
