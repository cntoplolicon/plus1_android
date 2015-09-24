package swj.swj.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.RestClient;

/**
 * Created by jiewei on 9/7/15.
 */
public class ResetPhoneActivity extends GetSecurityCodeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_phone_step_one);

        ButterKnife.bind(this);
        setPageTitle(getResources().getString(R.string.change_username_step_one));
    }


    @Override
    protected Class<?> getNextActivity() {
        return ResetPhoneStepTwoActivity.class;
    }

    @Override
    protected void getSecurityCode(String username, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        RestClient.getInstance().newSecurityCode4Account(username, onSuccess, onError);
    }

    public void back(View view) {
        finish();
    }
}
