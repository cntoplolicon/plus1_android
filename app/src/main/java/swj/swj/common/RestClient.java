package swj.swj.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import swj.swj.BuildConfig;

/**
 * Created by cntoplolicon on 9/10/15.
 */
public class RestClient {

    private static final String ERROR_TAG = RestClient.class.getCanonicalName();

    private static final String DEBUG_SERVER_URL = "http://10.0.2.2:9393";
    private static final String RELEASE_SERVER_URL = "http://liuxingapp:3000";

    private static RestClient instance;

    private RequestQueue requestQueue;
    private Context context;

    private RestClient(Context context) {
        this.context = context;

        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024);
        BasicNetwork network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    public synchronized static RestClient getInstance(Context context) {
        if (instance == null) {
            instance = new RestClient(context);
        }
        return instance;
    }

    private String getResourceUrl(String path) {
        String serverUrl = BuildConfig.DEBUG ? DEBUG_SERVER_URL : RELEASE_SERVER_URL;
        return serverUrl + path;
    }

    public void newTempUser(Response.Listener<JSONObject> successCallback,
                                      Response.Listener<JSONObject> failureCallback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getResourceUrl("/users"),
                successCallback, new JsonErrorListener(context, failureCallback));
        requestQueue.add(request);
    }

    private static class JsonErrorListener implements Response.ErrorListener {
        private Response.Listener<JSONObject> callback;
        private Context context;

        public JsonErrorListener(Context context, Response.Listener<JSONObject> callback) {
            this.callback = callback;
            this.context = context;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            boolean jsonResponseHandled = false;
            if (error.networkResponse != null &&
                    error.networkResponse.headers.get("Content-Type").equals("application/json")) {
                try {
                    String stringResponse = new String(error.networkResponse.data, "UTF-8");
                    JSONObject jsonResponse = new JSONObject(stringResponse);
                    JSONObject fieldErrors = jsonResponse.getJSONObject("errors");
                    if (fieldErrors != null && callback != null) {
                        callback.onResponse(fieldErrors);
                        jsonResponseHandled = true;
                    }
                } catch (JSONException e) {
                    Log.e(ERROR_TAG, "fail passing json response", e);
                } catch (UnsupportedEncodingException e) {
                    Log.e(ERROR_TAG, "incorrect string encoding", e);
                }
            }

            if (!jsonResponseHandled) {
                Log.e(ERROR_TAG, "network error", error);
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
