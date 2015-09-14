package swj.swj.common;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by cntoplolicon on 9/12/15.
 */
public class JsonObjectMultipartRequest extends JsonObjectRequest {

    private HttpEntity entity;

    public JsonObjectMultipartRequest(int method, String url, Map<String, Object> params,
                                      Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        super(method, url, onSuccess, onError);
        buildMultipartEntity(params);
    }

    private void buildMultipartEntity(Map<String, Object> params) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setCharset(Charset.forName("UTF-8"));
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object objValue = entry.getValue();
            if (objValue instanceof AbstractContentBody) {
                AbstractContentBody fileBody = (AbstractContentBody) objValue;
                builder.addPart(entry.getKey(), fileBody);
            } else if (objValue != null) {
                builder.addTextBody(entry.getKey(), objValue.toString());
            }
        }
        entity = builder.build();
    }

    @Override
    public String getBodyContentType() {
        Log.d("Content-Type", entity.getContentType().getValue());
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            entity.writeTo(os);
            return os.toByteArray();
        } catch (IOException e) {
            Log.e(JsonObjectMultipartRequest.class.getName(), "failed building multipart body file", e);
            return null;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.w(JsonObjectMultipartRequest.class.getName(), "failed closing stream", e);
                }
            }
        }
    }
}
