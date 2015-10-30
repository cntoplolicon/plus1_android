package swj.swj.adapter;

import android.content.Context;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Post;

/**
 * Created by jiewei on 9/14/15.
 */
public class UserPostsGridViewAdapter extends PostsGridViewAdapter {

    public UserPostsGridViewAdapter(Context context, int userId) {
        super(context);
        RestClient.getInstance().getUserPosts(userId).done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        updateAll(CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class));
                        notifyDataSetChanged();
                    }
                }).fail(new JsonErrorListener(context, null));
    }
}
