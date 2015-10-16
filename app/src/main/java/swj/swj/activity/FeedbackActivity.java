package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.CommonDialog;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

public class FeedbackActivity extends Activity {

    @Bind(R.id.et_contact)
    EditText etContact;
    @Bind(R.id.et_content)
    EditText etContent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (!inputValidation()) {
            return;
        }
        RestClient.getInstance().newFeedback(etContact.getText().toString(), etContent.getText().toString())
                .done(new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject result) {
                        Toast.makeText(FeedbackActivity.this, R.string.feedback_success, Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).fail(new JsonErrorListener(getApplicationContext(), null));
    }

    private boolean inputValidation() {
        if (etContent.getText().toString().isEmpty()) {
            CommonDialog.showDialog(this, R.string.feedback_content_required);
            return false;
        }
        return true;
    }
}
