package swj.swj.model;

/**
 * Created by cntoplolicon on 9/23/15.
 */
public class Post {
    private int id;
    private PostPage[] postPages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PostPage[] getPostPages() {
        return postPages;
    }

    public void setPostPages(PostPage[] postPages) {
        this.postPages = postPages;
    }
}
