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

/**
 * Created by jiewei on 9/14/15.
 */
public class UserPostsGridViewAdapter extends PostsGridViewAdapter {

    private static Post[] currentUserPosts = new Post[]{};

    static {
        User.registerUserChangedCallback(new ClearCacheCallback());
    }

    private int userId;

    public UserPostsGridViewAdapter(Context context, int userId) {
        super(context);
        this.userId = userId;
        if (userId == User.current.getId()) {
            addAll(currentUserPosts);
        }
    }

    public void loadPosts() {
        if (loading) {
            return;
        }
        loading = true;
        notifyLoadingStatusChanged();
        RestClient.getInstance().getUserPosts(userId).done(
                new DoneCallback<JSONArray>() {
                    @Override
                    public void onDone(JSONArray response) {
                        Post[] posts = CommonMethods.createDefaultGson().fromJson(response.toString(), Post[].class);
                        if (userId == User.current.getId()) {
                            currentUserPosts = posts;
                        }
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

    private static class ClearCacheCallback implements User.UserChangedCallback {
        @Override
        public void onUserChanged(User oldUser, User newUser) {
            currentUserPosts = new Post[]{};
            RestClient.getInstance().cancelRequests(RestClient.TAG_USER_POSTS);
        }
    }
}
