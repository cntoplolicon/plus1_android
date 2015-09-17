package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.LocalUserInfo;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by jiewei on 9/3/15.
 */
public class UpdateNicknameActivity extends Activity {

    private EditText nicknameInput;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);

        nicknameInput = (EditText) findViewById(R.id.et_nick);
        nicknameInput.setText(User.current.getNickname());
    }

    public void onSubmit(View view) {
        if (!inputValidtion()) {
            return;
        }
        String nickname = nicknameInput.getText().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("nickname", nickname);
        RestClient.getInstance().updateUserAttributes(attributes, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                User.updateCurrentUser(response.toString());
                finish();
            }
        }, new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject errors) {
                CommonMethods.toastError(UpdateNicknameActivity.this, errors, "nickname");
            }
        }));
    }

    private boolean inputValidtion() {
        if (nicknameInput.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.nickname_required, Toast.LENGTH_LONG);
            return false;
        }
        return true;
    }

    public void back(View view) {
        finish();
    }
}
