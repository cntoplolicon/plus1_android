package swj.swj.activity;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.jdeferred.Promise;
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
    }


    @Override
    protected Class<?> getNextActivity() {
        return ResetPhoneStepTwoActivity.class;
    }

    @Override
    protected Promise<JSONObject, VolleyError, Void> getSecurityCode(String username) {
        return RestClient.getInstance().newSecurityCode4Account(username);
    }

}
