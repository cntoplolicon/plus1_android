package swj.swj.common;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import swj.swj.BuildConfig;
import swj.swj.model.User;

/**
 * Created by cntoplolicon on 9/10/15.
 */
public class RestClient {

    private static final String DEBUG_SERVER_URL = "http://192.168.1.122:9393";
    private static final String RELEASE_SERVER_URL = "http://liuxingapp:3000";
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
        private DeferredObject<T, VolleyError, ?> deferredObject;

        private PromiseListener(DeferredObject<T, VolleyError, ?> deferredObject) {
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

    public Promise<JSONObject, VolleyError, Void> newSecurityCode4Account(String username) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/account"), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newSecurityCode4Password(String username) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/password"), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> verifySecurityCode(String username, String securityCode) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("security_code", securityCode);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/verify"), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> signUp(String username, String nickname,
                                                         String password, int gender,
                                                         AbstractContentBody avatar) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("avatar", avatar);
        params.put("gender", gender);

        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.POST,
                getResourceUrl("/users"), params.entrySet(), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> signIn(String username, String password) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/sign_in"), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> signOut() {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.get("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/sign_out"), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> updateUserAttributes(Map<String, Object> attributes) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        params.putAll(attributes);
        String userId = params.get("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.PUT,
                getResourceUrl("/users/" + userId), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> updateUserAvatar(Map<String, Object> attributes) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        params.putAll(attributes);
        String userId = params.get("user_id").toString();
        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.PUT,
                getResourceUrl("/users/" + userId), params.entrySet(), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }


    public Promise<JSONObject, VolleyError, Void> resetPassword(String username, String password) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.PUT,
                getResourceUrl("/users/password"), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newPost(String[] texts, AbstractContentBody[] images, Integer[] imageWidths, Integer[] imageHeights) {
        if (texts.length != images.length) {
            throw new IllegalArgumentException("texts & images lengths unequal");
        }

        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
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
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getActiveInfections() {
        DeferredObject<JSONArray, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/users/" + userId + "/infections/active", params), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newPostView(int infectionId, int result) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();

        if (!POST_VIEWS_ENABLED) {
            return deferredObject.promise();
        }

        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);
        Map<String, Object> params = createUserParams();
        params.put("result", result);
        String userId = params.remove("user_id").toString();

        String path = "/users/" + userId + "/infections/" + infectionId + "/post_view";
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl(path), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getUserPosts(int authorId) {
        DeferredObject<JSONArray, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/users/" + authorId + "/posts", params), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> createBookmark(int postId) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        params.put("post_id", postId);
        String userId = params.remove("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/bookmarks"), params, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> removeBookmark(int postId) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE,
                encodeUrlParams("/users/" + userId + "/bookmarks/" + postId, params), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getUserBookmarks() {
        DeferredObject<JSONArray, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/users/" + userId + "/bookmarks", params), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> getAppInfo() {
        ThrowableDeferredObject<JSONObject, VolleyError, Void> deferredObject = new ThrowableDeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getResourceUrl("/app_info"), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }


    public Promise<JSONArray, VolleyError, Void> getPostComments(int postId) {
        DeferredObject<JSONArray, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> params = createUserParams();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/posts/" + postId + "/comments", params), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newComment(String content, int replyToId, int postId) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> userParams = createUserParams();
        if (replyToId > 0) {
            userParams.put("reply_to", replyToId);
        }
        userParams.put("content", content);

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/posts/" + postId + "/comments"), userParams, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> getPost(int postId) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> userParams = createUserParams();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                encodeUrlParams("/posts/" + postId, userParams), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newFeedback(String contact, String content) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONObject> listener = new PromiseListener<>(deferredObject);

        Map<String, Object> userParmas = createUserParams();
        userParmas.put("content", content);
        if (contact != null && !contact.isEmpty()) {
            userParmas.put("contact", contact);
        }
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/feedbacks"), userParmas, listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

    public Promise<JSONArray, VolleyError, Void> getRecommendPosts() {
        DeferredObject<JSONArray, VolleyError, Void> deferredObject = new DeferredObject<>();
        PromiseListener<JSONArray> listener = new PromiseListener<>(deferredObject);
        Map<String, Object> params = createUserParams();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                encodeUrlParams("/recommendations", params), listener, listener);
        requestQueue.add(request);

        return deferredObject.promise();
    }

}
