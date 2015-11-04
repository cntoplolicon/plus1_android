package com.oneplusapp.adapter;

import android.content.Context;

import com.android.volley.VolleyError;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONArray;

import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Post;

/**
 * Created by jiewei on 9/14/15.
 */
public class UserPostsGridViewAdapter extends PostsGridViewAdapter {

    public UserPostsGridViewAdapter(Context context, int userId) {
        super(context);
        loading = true;
        RestClient.getInstance().getUserPosts(userId).done(
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
