package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.RestClient;

/**
 * Created by syb on 2015/9/18.
 */
public class AddTextActivity extends Activity {

    @Bind(R.id.et_text)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.iv_use_back)
    public void useBack() {
        finish();
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        String text = editText.getText().toString();

        RestClient.getInstance().newPost(new String[]{text}, new AbstractContentBody[]{null},
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), R.string.post_success, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(AddTextActivity.class.getName(), "failed uploading posts", error);
                        Toast.makeText(getApplicationContext(), R.string.post_failure, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
