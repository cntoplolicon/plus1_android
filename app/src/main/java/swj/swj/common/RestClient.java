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

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import swj.swj.BuildConfig;

/**
 * Created by cntoplolicon on 9/10/15.
 */
public class RestClient {

    private static final String DEBUG_SERVER_URL = "http://10.0.2.2:9393";
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

    public void signUp(String username, String nickname, String password, int gender, File avatar,
                       Listener<JSONObject> onSuccess, ErrorListener onError) {
        String userId = "sdjfklsdfjl";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("username", username);
        params.put("nickname", nickname);
        params.put("password", password);
        params.put("avatar", avatar);
        params.put("gender", gender);

        JsonObjectMultipartRequest request = new JsonObjectMultipartRequest(Request.Method.PUT,
                getResourceUrl("/user/" + userId), params, onSuccess, onError);
        requestQueue.add(request);
    }
}
