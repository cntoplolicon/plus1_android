package swj.swj.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import swj.swj.R;

/**
 * Created by cntoplolicon on 9/11/15.
 */
public class JsonErrorListener implements Response.ErrorListener {
    private static final String ERROR_TAG = RestClient.class.getCanonicalName();

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
            Toast.makeText(context, context.getResources().getString(R.string.network_error),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
