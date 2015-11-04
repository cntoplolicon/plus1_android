package swj.swj.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.jdeferred.FailCallback;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import swj.swj.R;

/**
 * Created by cntoplolicon on 9/11/15.
 */
public class JsonErrorListener implements FailCallback<VolleyError> {
    private static final String ERROR_TAG = RestClient.class.getCanonicalName();

    private Response.Listener<JSONObject> callback;
    private Context context;

    public JsonErrorListener(Context context, Response.Listener<JSONObject> callback) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    public void onFail(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.statusCode == HttpURLConnection.HTTP_FORBIDDEN) {
            CommonMethods.clientSideSignOut(context);
            Toast.makeText(context, R.string.account_sign_in_error, Toast.LENGTH_LONG).show();
            return;
        }

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
            int resourceId = R.string.unknown_error;
            if (error instanceof NetworkError) {
                resourceId = R.string.network_error;
            } else if (error instanceof ServerError) {
                resourceId = R.string.server_error;
            } else if (error instanceof TimeoutError) {
                resourceId = R.string.timeout_error;
            }
            Toast.makeText(context, resourceId, Toast.LENGTH_SHORT).show();

        }
    }
}
