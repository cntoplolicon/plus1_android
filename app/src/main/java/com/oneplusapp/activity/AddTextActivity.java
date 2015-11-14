package com.oneplusapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.oneplusapp.R;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.ResetViewClickable;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.view.ConfirmAlertDialog;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by syb on 2015/9/18.
 */
public class AddTextActivity extends BaseActivity {
    private static Promise<JSONObject, VolleyError, Void> promise;

    @Bind(R.id.et_text)
    EditText editText;
    @Bind(R.id.tv_publish)
    TextView tvPublish;

    public static Promise<JSONObject, VolleyError, Void> getPromise() {
        return promise;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        ConfirmAlertDialog confirmAlertDialog = new ConfirmAlertDialog(AddTextActivity.this);
        confirmAlertDialog.show();
        confirmAlertDialog.getConfirmTextView().setText(R.string.quit_publishing);
        confirmAlertDialog.getConfirmTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.tv_publish)
    public void submit() {
        String text = editText.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.post_text_required, Toast.LENGTH_LONG).show();
            return;
        }
        promise = RestClient.getInstance().newPost(new String[]{text}, new AbstractContentBody[]{null}, new Integer[]{null}, new Integer[]{null}).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        Toast.makeText(getApplicationContext(), R.string.post_success, Toast.LENGTH_LONG).show();
                    }
                }).fail(
                new JsonErrorListener(getApplicationContext(), null) {
                    @Override
                    public void onFail(VolleyError error) {
                        super.onFail(error);
                        Log.e(PublishActivity.class.getName(), "failed uploading posts", error);
                        Toast.makeText(getApplicationContext(), R.string.post_failure, Toast.LENGTH_LONG).show();
                    }
                }).always(new ResetViewClickable<JSONObject, VolleyError>(tvPublish));
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("publish_class", AddTextActivity.class);
        startActivity(intent);
        finish();
    }
}
