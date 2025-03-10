package com.oneplusapp.common;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oneplusapp.BuildConfig;
import com.oneplusapp.model.User;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.jdeferred.Promise;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RestClient {

    public static final String TAG_USER_POSTS = "user_posts";
    public static final String TAG_BOOKMARKS = "bookmarks";

    private static final String DEBUG_SERVER_URL = "http://192.168.1.122:9393";
    private static final String RELEASE_SERVER_URL = "https://oneplusapp.com";
    private static final RetryPolicy DEFAULT_RETRY_POLICY = new DefaultRetryPolicy(30 * 1000, // 30 sec
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private static final boolean POST_VIEWS_ENABLED = false;

    private static RestClient instance;

    private RequestQueue requestQueue;

    public static synchronized void initialize(Context context) {
        if (instance != null) {
            return;
        }
        instance = new RestClient(context);
    }

    private RestClient(Context context) {
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        BasicNetwork network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    public static RestClient getInstance() {
        return instance;
    }

    private String getResourceUrl(String path) {
        String serverUrl = BuildConfig.DEBUG ? DEBUG_SERVER_URL : RELEASE_SERVER_URL;
        return serverUrl + path;
    }

    private String encodeUrlParams(String path, Map<String, Object> params) {
        Uri.Builder uri = Uri.parse(getResourceUrl(path)).buildUpon();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            uri.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return uri.build().toString();
    }

    private Map<String, Object> createUserParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", User.current.getId());
        params.put("access_token", User.current.getAccessToken());
        return params;
    }

    private static class PromiseListener<T> implements Listener<T>, ErrorListener {
        private ThrowableDeferredObject<T, VolleyError, ?> deferredObject;

        private PromiseListener(ThrowableDeferredObject<T, VolleyError, ?> deferredObject) {
            this.deferredObject = deferredObject;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            deferredObject.reject(error);
        }

        @Override
        public void onResponse(T error) {
            deferredObject.resolve(error);
        }
    }

    public void cancelRequests(Object tag) {
        requestQueue.cancelAll(tag);
    }


    public String buildAgreementUrl() {
        return getResourceUrl("/agreement/index.html");
    }

    public Promise<JSONObject, VolleyError, Void> getAppRelease() {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getResourceUrl("/app_release/android"), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newSecurityCode4Account(String username) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/account"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newSecurityCode4Password(String username) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/password"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> verifySecurityCode(String username, String securityCode) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("security_code", securityCode);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/verify"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> signUp(String username, String nickname,
                                                         String password, int gender,
                                                         AbstractContentBody avatar) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("avatar", avatar);
        params.put("gender", gender);

        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.POST,
                getResourceUrl("/users"), params.entrySet(), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> signIn(String username, String password) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/sign_in"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> signInViaOauth(Map<String, Object> signInInfo) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/sign_in/oauth"), signInInfo, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> signOut() {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.get("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/sign_out"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> updateUserAttributes(Map<String, Object> attributes) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        params.putAll(attributes);
        String userId = params.get("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.PUT,
                getResourceUrl("/users/" + userId), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> updateUserAvatar(Map<String, Object> attributes) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        params.putAll(attributes);
        String userId = params.get("user_id").toString();
        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.PUT,
                getResourceUrl("/users/" + userId), params.entrySet(), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }


    public Promise<JSONObject, VolleyError, Void> resetPassword(String username, String password) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.PUT,
                getResourceUrl("/users/password"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newPost(String[] texts, AbstractContentBody[] images, Integer[] imageWidths, Integer[] imageHeights) {
        if (texts.length != images.length) {
            throw new IllegalArgumentException("texts & images lengths unequal");
        }

        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        List<Map.Entry<String, Object>> params = new LinkedList<>();
        Map<String, Object> userParams = createUserParams();
        params.addAll(userParams.entrySet());

        for (int i = 0; i < texts.length; i++) {
            params.add(new AbstractMap.SimpleEntry<String, Object>("post_pages[][text]", texts[i]));
            params.add(new AbstractMap.SimpleEntry<String, Object>("post_pages[][image]", images[i]));
            params.add(new AbstractMap.SimpleEntry<String, Object>("post_pages[][image_width]", imageWidths[i]));
            params.add(new AbstractMap.SimpleEntry<String, Object>("post_pages[][image_height]", imageHeights[i]));
        }

        String userId = userParams.get("user_id").toString();
        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/posts"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> deletePost(int postId) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                encodeUrlParams("/users/" + userId + "/posts/" + postId, params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> reportPost(int postId) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        params.put("post_id", postId);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/complains"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getActiveInfections() {
        ThrowableDeferredObject<JSONArray, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/users/" + userId + "/infections/active", params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newPostView(int infectionId, int result) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();

        if (BuildConfig.DEBUG && !POST_VIEWS_ENABLED) {
            return deferredObject.promise();
        }

        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);
        Map<String, Object> params = createUserParams();
        params.put("result", result);
        String userId = params.remove("user_id").toString();

        String path = "/users/" + userId + "/infections/" + infectionId + "/post_view";
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl(path), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getUserPosts(int authorId) {
        ThrowableDeferredObject<JSONArray, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/users/" + authorId + "/posts", params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        request.setTag(TAG_USER_POSTS);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> createBookmark(int postId) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        params.put("post_id", postId);
        String userId = params.remove("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/bookmarks"), params, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> removeBookmark(int postId) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                encodeUrlParams("/users/" + userId + "/bookmarks/" + postId, params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getUserBookmarks() {
        ThrowableDeferredObject<JSONArray, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/users/" + userId + "/bookmarks", params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        request.setTag(TAG_BOOKMARKS);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newComment(String content, int replyToId, int postId) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> userParams = createUserParams();
        if (replyToId > 0) {
            userParams.put("reply_to", replyToId);
        }
        userParams.put("content", content);

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/posts/" + postId + "/comments"), userParams, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> removeComment(int postId, int commentId) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                encodeUrlParams("/posts/" + postId + "/comments/" + commentId, params), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> getPost(int postId) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> userParams = createUserParams();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                encodeUrlParams("/posts/" + postId, userParams), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> updateAccountInfo(int userId, String accessToken, Map<String, Object> info) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> userParams = new HashMap<>(info);
        userParams.put("access_token", accessToken);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/account_info"), userParams, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newFeedback(String contact, String content) {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> userParams = createUserParams();
        userParams.put("content", content);
        if (contact != null && !contact.isEmpty()) {
            userParams.put("contact", contact);
        }
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/feedbacks"), userParams, listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getRecommendedPosts() {
        ThrowableDeferredObject<JSONArray, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);
        Map<String, Object> params = createUserParams();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/recommendations", params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getNotificationUsersInfo(Collection<Integer> userIds) {
        ThrowableDeferredObject<JSONArray, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);
        Map<String, Object> params = createUserParams();

        StringBuilder sbUserIds = new StringBuilder();
        for (Integer userId : userIds) {
            sbUserIds.append(userId);
            sbUserIds.append(";");
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, encodeUrlParams("/users/" + sbUserIds, params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> getLatestEvent() {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getResourceUrl("/events/latest"), listener, listener);
        // used in splash screen so timeout must be short
        RetryPolicy retryPolicy = new DefaultRetryPolicy(5 * 1000, // 5 sec
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getAllEvents() {
        ThrowableDeferredObject<JSONArray, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                getResourceUrl("/events"), listener, listener);

        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);
        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getEventRecommendedPosts(int eventId) {
        ThrowableDeferredObject<JSONArray, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);
        Map<String, Object> params = createUserParams();

        params.put("event_id", eventId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/events/" + eventId + "/recommendations", params), listener, listener);
        request.setRetryPolicy(DEFAULT_RETRY_POLICY);
        requestQueue.add(request);

        return deferredObject.promise();
    }
}
