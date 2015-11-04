package com.oneplusapp.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.oneplusapp.R;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.User;

/**
 * Created by jiewei on 9/3/15.
 */
public class UpdateNicknameActivity extends BaseActivity {

    @Bind(R.id.et_nick)
    EditText nicknameInput;

    @OnClick(R.id.btn_submit)
    public void submit() {
        if (!inputValidation()) {
            return;
        }
        String nickname = nicknameInput.getText().toString().trim();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("nickname", nickname);
        RestClient.getInstance().updateUserAttributes(attributes).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        User.updateCurrentUser(response.toString());
                        Toast.makeText(UpdateNicknameActivity.this, R.string.nickname_reset_succeed, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).fail(
                new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject errors) {
                        CommonMethods.showError(UpdateNicknameActivity.this, errors, "nickname");
                    }
                }));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nickname);
        ButterKnife.bind(this);

        nicknameInput.setText(User.current.getNickname());
    }

    private boolean inputValidation() {
        if (nicknameInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.nickname_required, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
