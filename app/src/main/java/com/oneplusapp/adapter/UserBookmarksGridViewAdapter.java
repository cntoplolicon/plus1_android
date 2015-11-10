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
 * Created by cntoplolicon on 10/7/15.
 */
public class UserBookmarksGridViewAdapter extends PostsGridViewAdapter {

    public UserBookmarksGridViewAdapter(Context context) {
        super(context);
        loadBookmarks();
    }

    public void loadBookmarks() {
        loading = true;
        RestClient.getInstance().getUserBookmarks().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        Post[] posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                        updateAll(posts);
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