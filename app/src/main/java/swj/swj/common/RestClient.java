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
    private static final boolean POST_VIEWS_ENABLED = true;

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

    public Promise<JSONObject, VolleyError, Void> newSecurityCode4Account(String username) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/account"), params,
                new ResolvePromiseListener<>(deferredObject),
                new RejectPromiseErrorListener(deferredObject));
        requestQueue.add(request);
        return deferredObject.promise();
    }

    public Promise<JSONObject, VolleyError, Void> newSecurityCode4Password(String username) {
        DeferredObject<JSONObject, VolleyError, Void> deferredObject = new DeferredObject<>();
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/password"), params,
                new ResolvePromiseListener<>(deferredObject),
                new RejectPromiseErrorListener(deferredObject));
        requestQueue.add(request);
        return deferredObject.promise();
    }

    public void verifySecurityCode(String username, String securityCode,
                                   Listener<JSONObject> onSucess, ErrorListener onError) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("security_code", securityCode);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/verify"), params, onSucess, onError);
        requestQueue.add(request);
    }

    public void signUp(String username, String nickname, String password, int gender, AbstractContentBody avatar,
                       Listener<JSONObject> onSuccess, ErrorListener onError) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("avatar", avatar);
        params.put("gender", gender);

        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.POST,
                getResourceUrl("/users"), params.entrySet(), onSuccess, onError);
        requestQueue.add(request);
    }

    public void signIn(String username, String password, Listener<JSONObject> onSucess, ErrorListener onError) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/sign_in"), params, onSucess, onError);
        requestQueue.add(request);
    }

    public void signOut(Listener<JSONObject> onSuccess, ErrorListener onError) {
        Map<String, Object> params = createUserParams();
        String userId = params.get("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/sign_out"), params, onSuccess, onError);
        requestQueue.add(request);
    }

    public void updateUserAttributes(Map<String, Object> attributes,
                                     Listener<JSONObject> onSucess, ErrorListener onError) {
        Map<String, Object> params = createUserParams();
        params.putAll(attributes);
        String userId = params.get("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.PUT,
                getResourceUrl("/users/" + userId), params, onSucess, onError);
        requestQueue.add(request);
    }

    public void updateUserAvatar(Map<String, Object> attributes,
                                 Listener<JSONObject> onSucess, ErrorListener onError) {
        Map<String, Object> params = createUserParams();
        params.putAll(attributes);
        String userId = params.get("user_id").toString();
        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.PUT,
                getResourceUrl("/users/" + userId), params.entrySet(), onSucess, onError);
        requestQueue.add(request);
    }


    public void resetPassword(String username, String password,
                              Listener<JSONObject> onSucess, ErrorListener onError) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.PUT,
                getResourceUrl("/users/password"), params, onSucess, onError);
        requestQueue.add(request);
    }

    public void newPost(String[] texts, AbstractContentBody[] images,
                        Listener<JSONObject> onSuccess, ErrorListener onError) {
        if (texts.length != images.length) {
            throw new IllegalArgumentException("texts & images lengths unequal");
        }

        List<Map.Entry<String, Object>> params = new LinkedList<>();
        Map<String, Object> userParams = createUserParams();
        params.addAll(userParams.entrySet());

        for (int i = 0; i < texts.length; i++) {
            params.add(new AbstractMap.SimpleEntry<String, Object>("post_pages[][text]", texts[i]));
            params.add(new AbstractMap.SimpleEntry<String, Object>("post_pages[][image]", images[i]));
        }

        String userId = userParams.get("user_id").toString();
        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/posts"), params, onSuccess, onError);
        requestQueue.add(request);
    }

    public void getActiveInfections(Listener<JSONArray> onSuccess, ErrorListener onError) {
        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                buildUrlForGetRequests("/users/" + userId + "/infections/active", params), onSuccess, onError);
        requestQueue.add(request);
    }

    public void newPostView(int infectionId, int result,
                            Listener<JSONObject> onSuccess, ErrorListener onError) {
        if (!POST_VIEWS_ENABLED) {
            return;
        }

        Map<String, Object> params = createUserParams();
        params.put("result", result);
        String userId = params.remove("user_id").toString();

        String path = "/users/" + userId + "/infections/" + infectionId + "/post_view";
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl(path), params, onSuccess, onError);
        requestQueue.add(request);
    }

    public void getUserPosts(int authorId, Listener<JSONArray> onSuccess, ErrorListener onError) {
        Map<String, Object> params = createUserParams();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                buildUrlForGetRequests("/users/" + authorId + "/posts", params), onSuccess, onError);
        requestQueue.add(request);
    }

    public void createBookmark(int postId, Listener<JSONObject> onSuccess, ErrorListener onError) {
        Map<String, Object> params = createUserParams();
        params.put("post_id", postId);
        String userId = params.remove("user_id").toString();
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/users/" + userId + "/bookmarks"), params, onSuccess, onError);
        requestQueue.add(request);
    }

    public void getUserBookmarks(Listener<JSONArray> onSuccess, ErrorListener onError) {
        Map<String, Object> params = createUserParams();
        String userId = params.remove("user_id").toString();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                buildUrlForGetRequests("/users/" + userId + "/bookmarks", params), onSuccess, onError);
        requestQueue.add(request);
    }

    private String buildUrlForGetRequests(String path, Map<String, Object> params) {
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

    public void loadImageServerUrl(Listener<JSONArray> onSuccess, ErrorListener onError) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getResourceUrl("/image_hosts"), onSuccess, onError);
        requestQueue.add(request);
    }

    private class ResolvePromiseListener<T> implements Listener<T> {
        private DeferredObject<T, ?, ?> deferredObject;

        private ResolvePromiseListener(DeferredObject<T, ?, ?> deferredObject) {
            this.deferredObject = deferredObject;
        }

        @Override
        public void onResponse(T response) {
            deferredObject.resolve(response);
        }
    }

    private class RejectPromiseErrorListener implements ErrorListener {
        private DeferredObject<?, VolleyError, ?> deferredObject;

        private RejectPromiseErrorListener(DeferredObject<?, VolleyError, ?> deferredObject) {
            this.deferredObject = deferredObject;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            deferredObject.reject(error);
        }
    }
}
