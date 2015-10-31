package swj.swj.adapter;

import android.content.Context;

import com.android.volley.VolleyError;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
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
        loading = true;
        RestClient.getInstance().getRecommendPosts().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        updateAll(CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class));
                        notifyDataSetChanged();
                    }
                }).fail(new JsonErrorListener(context, null))
                .always(new AlwaysCallback<JSONArray, VolleyError>() {
                    @Override
                    public void onAlways(Promise.State state, JSONArray resolved, VolleyError rejected) {
                        loading = false;
                        notifyLoadingStatusChanged();
                    }
                });
    }

}