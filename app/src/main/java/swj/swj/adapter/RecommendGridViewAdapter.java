package swj.swj.adapter;

import android.content.Context;

import org.jdeferred.DoneCallback;
import org.json.JSONArray;

import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.Post;

/**
 * Created by jiewei on 10/21/15.
 */
public class RecommendGridViewAdapter extends PostsGridViewAdapter {
    public RecommendGridViewAdapter(Context context) {
        super(context);
        RestClient.getInstance().getRecommendPosts().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                        notifyDataSetChanged();
                    }
                }).fail(new JsonErrorListener(context, null));
    }
}