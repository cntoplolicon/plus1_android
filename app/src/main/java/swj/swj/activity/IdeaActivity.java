package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;

public class IdeaActivity extends Activity {

    @Bind(R.id.et_biography)
    EditText biographyInput;
    @Bind(R.id.et_qq_phone)
    EditText qqPhoneInput;

    @OnClick(R.id.btn_submit)
    public void submit() {
        String biography = biographyInput.getText().toString();
        String qqPhone = qqPhoneInput.getText().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("biography", biography);
        attributes.put("qqPhone", qqPhone);
        RestClient.getInstance().updateUserAttributes(attributes).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        User.updateCurrentUser(response.toString());
                        Toast.makeText(IdeaActivity.this, "意见反馈成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).fail(new JsonErrorListener(getApplicationContext(), null));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea);
        ButterKnife.bind(this);
    }
}
