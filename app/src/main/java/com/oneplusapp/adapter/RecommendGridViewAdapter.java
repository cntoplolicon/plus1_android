package com.oneplusapp.adapter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Post;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONArray;

/**
 * Created by jiewei on 10/21/15.
 */
public class RecommendGridViewAdapter extends PostsGridViewAdapter {

    private static Post[] userBookmarksCache = new Post[]{};

    public RecommendGridViewAdapter(Context context) {
        super(context);
        addAll(userBookmarksCache);
        loadRecommendations();
    }

    public void loadRecommendations() {
        if (loading) {
            return;
        }
        loading = true;
        notifyLoadingStatusChanged();
        RestClient.getInstance().getRecommendedPosts().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        userBookmarksCache = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                        updateAll(userBookmarksCache);
                        notifyDataSetChanged();
                    }
                }).fail(new JsonErrorListener(getContext(), null))
                .always(new AlwaysCallback<JSONArray, VolleyError>() {
                    @Override
                    public void onAlways(Promise.State state, JSONArray resolved, VolleyError rejected) {
                        loading = false;
                        notifyLoadingStatusChanged();
                    }
                });
    }
}