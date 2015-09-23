package swj.swj.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;

/**
 * Created by jiewei on 9/7/15.
 */
public class ResetPhoneStepTwoActivity extends VerifySecurityCodeActivity {

    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!inputValidation()) {
                return;
            }

            final String username = getIntent().getStringExtra("username");
            String securityCode = ((EditText) findViewById(R.id.et_security_code)).getText().toString();

            RestClient.getInstance().verifySecurityCode(username, securityCode,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Map<String, Object> attributes = new HashMap<>();
                            attributes.put("username", username);
                            RestClient.getInstance().updateUserAttributes(attributes, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Intent intent = new Intent(ResetPhoneStepTwoActivity.this, PersonalProfileActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                        }
                                    },
                                    new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject errors) {
                                            CommonMethods.toastError(ResetPhoneStepTwoActivity.this, errors, "username");
                                        }
                                    }));
                        }
                    }, new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject errors) {
                            CommonMethods.toastError(ResetPhoneStepTwoActivity.this, errors, "security_code");
                        }
                    }));
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone_step_two);

        ButterKnife.bind(this);
        Button submitButton = (Button)findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(onSubmit);
    }

}
