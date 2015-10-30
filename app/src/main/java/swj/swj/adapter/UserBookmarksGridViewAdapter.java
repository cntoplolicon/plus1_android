package swj.swj.adapter;

import android.content.Context;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import swj.swj.common.BookmarkService;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Post;

/**
 * Created by cntoplolicon on 10/7/15.
 */
public class UserBookmarksGridViewAdapter extends PostsGridViewAdapter {

    public UserBookmarksGridViewAdapter(Context context) {
        super(context);
        updateAll(BookmarkService.getInstance().getBookmarkedPosts());
        RestClient.getInstance().getUserBookmarks().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        Post[] posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                        addAll(posts);
                        notifyDataSetChanged();
                        BookmarkService.getInstance().updateBookmarkCache(posts);
                    }
                }).fail(new JsonErrorListener(context, null));
    }

}