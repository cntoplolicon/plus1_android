package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.CommonDialog;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.ResetViewClickable;

public abstract class GetSecurityCodeActivity extends Activity {

    @Bind(R.id.et_username)
    EditText usernameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean inputValidation() {
        if (!CommonMethods.isValidUsername(usernameInput.getText().toString().trim())) {
            CommonDialog.showDialog(GetSecurityCodeActivity.this, getResources().getString(R.string.username_invalid_format));
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_submit)
    protected void onSubmit(View view) {
        if (!inputValidation()) {
            return;
        }
        view.setEnabled(false);
        final String username = usernameInput.getText().toString();
        getSecurityCode(username).done(new DoneCallback<JSONObject>() {
            @Override
            public void onDone(JSONObject response) {
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
        }).fail(new JsonErrorListener(getApplicationContext(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject errors) {
                CommonMethods.toastError(getApplicationContext(), errors, "username");
            }
        })).always(new ResetViewClickable<JSONObject, VolleyError>(view));
    }

    protected abstract Class<?> getNextActivity();

    protected abstract Promise<JSONObject, VolleyError, Void> getSecurityCode(String username);

}
