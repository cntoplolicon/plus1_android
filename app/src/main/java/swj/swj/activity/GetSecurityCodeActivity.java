package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;

public abstract class GetSecurityCodeActivity extends Activity {

    @Bind(R.id.et_username)
    EditText usernameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean inputValidation() {
        if (!CommonMethods.isValidUsername(usernameInput.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.username_invalid_format), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    @OnClick(R.id.btn_submit)
    protected void onSubmit() {
        if (!inputValidation()) {
            return;
        }
        final String username = usernameInput.getText().toString();
        getSecurityCode(username,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String securityCode = response.optString("security_code", "");
                        if (!securityCode.isEmpty()) {
                            Log.d("security_code", securityCode);
                            Toast.makeText(getBaseContext(), securityCode, Toast.LENGTH_LONG).show();
                        }

                        Intent intent = new Intent(getBaseContext(), getNextActivity());
                        intent.putExtra("counter_start", System.currentTimeMillis());
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                },
                new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject errors) {
                        CommonMethods.toastError(getApplicationContext(), errors, "username");
                    }
                }));
    }

    protected abstract Class<?> getNextActivity();

    protected abstract void getSecurityCode(String username, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError);

}
