package swj.swj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
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

    @Bind(R.id.tv_page_title)
    TextView pageTitle;
    protected void setPageTitle(String title){
        pageTitle.setText(title);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_phone_to_get_scode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
