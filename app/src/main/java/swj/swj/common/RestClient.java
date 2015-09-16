package swj.swj.common;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import swj.swj.BuildConfig;
import swj.swj.model.User;

/**
 * Created by cntoplolicon on 9/10/15.
 */
public class RestClient {

    private static final String DEBUG_SERVER_URL = "http://192.168.1.144:9393";
    private static final String RELEASE_SERVER_URL = "http://liuxingapp:3000";

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

    public void newTempUser(Listener<JSONObject> onSuccess, ErrorListener onError) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                getResourceUrl("/users"), onSuccess, onError);
        requestQueue.add(request);
    }

    public void newSecurityCode4Account(String username, Listener<JSONObject> onSuccess,
                                        ErrorListener onError) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/account"), params, onSuccess, onError);
        requestQueue.add(request);
    }

    public void verifySecurityCode(String username, String securityCode,
                                   Listener<JSONObject> onSucess, ErrorListener onError) {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("security_code", securityCode);
        JsonObjectFormRequest request = new JsonObjectFormRequest(Request.Method.POST,
                getResourceUrl("/security_codes/verify"), params, onSucess, onError);
        requestQueue.add(request);
    }

    public void signUp(boolean createNewUser, String username, String nickname, String password, int gender, AbstractContentBody avatar,
                       Listener<JSONObject> onSuccess, ErrorListener onError) {
        Map<String, Object> params = createMultipartParams4User();
        params.put("username", username);
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("avatar", avatar);
        params.put("gender", gender);

        String userId = createNewUser ? "new" : params.get("user_id").toString();
        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.PUT,
                getResourceUrl("/users/" + userId), params, onSuccess, onError);
        requestQueue.add(request);
    }

    private Map<String, Object> createMultipartParams4User() {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", String.valueOf(User.current.getId()));
        params.put("access_token", User.current.getAccessToken());
        return params;
    }
}
