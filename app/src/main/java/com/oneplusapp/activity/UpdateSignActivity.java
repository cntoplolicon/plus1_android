package com.oneplusapp.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.oneplusapp.R;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.User;

import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateSignActivity extends BaseActivity {

    @Bind(R.id.et_content)
    EditText biographyInput;

    @OnClick(R.id.btn_submit)
    public void submit() {
        String biography = biographyInput.getText().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("biography", biography);
        RestClient.getInstance().updateUserAttributes(attributes).done(
                new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        User.updateCurrentUser(response.toString());
                        Toast.makeText(UpdateSignActivity.this, R.string.sign_reset_succeed, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).fail(new JsonErrorListener(getApplicationContext(), null));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);
        ButterKnife.bind(this);
        biographyInput.setText(User.current.getBiography());
    }
}
