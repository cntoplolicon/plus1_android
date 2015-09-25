package swj.swj.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response;

import org.json.JSONObject;

import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.RestClient;

public class ResetPwdStepOne extends GetSecurityCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd_step_one);

        ButterKnife.bind(this);
        setPageTitle(getResources().getString(R.string.reset_pwd_step_one));
    }

    @Override
    protected Class<?> getNextActivity() {
        return ResetPwdStepTwo.class;
    }

    @Override
    protected void getSecurityCode(String username, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        RestClient.getInstance().newSecurityCode4Password(username, onSuccess, onError);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reset_pwd_step_one, menu);
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
