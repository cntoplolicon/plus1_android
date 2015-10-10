package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

/**
 * Created by syb on 2015/9/18.
 */
public class AddTextActivity extends Activity {
    @Bind(R.id.et_text)
    EditText editText;

    public static boolean isSend;
    public static boolean isLoadState;
    private TextView tvPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        ButterKnife.bind(this);
        tvPublish = (TextView) findViewById(R.id.tv_publish);
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        isSend = tvPublish.isClickable();
        Log.e("sendText", isSend + "");
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.post_text_required, Toast.LENGTH_LONG).show();
            return;
        }
        RestClient.getInstance().newPost(new String[]{text}, new AbstractContentBody[]{null}).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        Toast.makeText(getApplicationContext(), R.string.post_success, Toast.LENGTH_LONG).show();
                        isLoadState = true;
                        Log.e("isLoadState",isLoadState+"");
                    }
                }).fail(
                new JsonErrorListener(getApplicationContext(), null) {
                    @Override
                    public void onFail(VolleyError error) {
                        super.onFail(error);
                        Log.e(PublishActivity.class.getName(), "failed uploading posts", error);
                        Toast.makeText(getApplicationContext(), R.string.post_failure, Toast.LENGTH_LONG).show();
                        isLoadState = true;
                        Log.e("isLoadState",isLoadState+"");
                    }
                });
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
