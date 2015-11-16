package com.oneplusapp.common;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cntoplolicon on 9/11/15.
 */
public class JsonObjectFormRequest extends JsonObjectRequest {

    private static final String PARAMS_ENCODING = "UTF-8";

    private Map<String, Object> params;

    public JsonObjectFormRequest(int method, String url, Map<String, Object> params,
                                 Response.Listener<JSONObject> onSuccess,
                                 Response.ErrorListener onError) {
        super(method, url, onSuccess, onError);
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> strParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                strParams.put(entry.getKey(), null);
            } else {
                strParams.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return strParams;
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    @Override
    protected String getParamsEncoding() {
        return PARAMS_ENCODING;
    }

    @Override
    public byte[] getBody() {
        Map<String, String> formParams = getParams();
        if (formParams != null && formParams.size() > 0) {
            return encodeFormParams(formParams, getParamsEncoding());
        }
        return null;
    }

    private byte[] encodeFormParams(Map<String, String> formParams, String encoding) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                stringBuilder.append(URLEncoder.encode(entry.getKey(), encoding));
                stringBuilder.append('=');
                stringBuilder.append(URLEncoder.encode(entry.getValue(), encoding));
                stringBuilder.append('&');
            }
            return stringBuilder.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("unsupported encoding: " + encoding, e);
        }
    }
}
