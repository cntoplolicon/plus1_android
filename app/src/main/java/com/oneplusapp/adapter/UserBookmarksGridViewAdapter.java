package com.oneplusapp.adapter;

import android.content.Context;

import com.android.volley.VolleyError;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.Post;
import com.oneplusapp.model.User;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONArray;

public class UserBookmarksGridViewAdapter extends PostsGridViewAdapter {

    private static Post[] bookmarksCache = new Post[]{};

    static {
        User.registerUserChangedCallback(new ClearCacheCallback());
    }

    public UserBookmarksGridViewAdapter(Context context) {
        super(context);
        addAll(bookmarksCache);
    }

    public void loadBookmarks() {
        if (loading) {
            return;
        }
        loading = true;
        notifyLoadingStatusChanged();
        RestClient.getInstance().getUserBookmarks().done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        bookmarksCache = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                        updateAll(bookmarksCache);
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

    private static class ClearCacheCallback implements User.UserChangedCallback {
        @Override
        public void onUserChanged(User oldUser, User newUser) {
            bookmarksCache = new Post[]{};
            RestClient.getInstance().cancelRequests(RestClient.TAG_BOOKMARKS);
        }
    }
}