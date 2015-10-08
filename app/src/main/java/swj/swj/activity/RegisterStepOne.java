package swj.swj.activity;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.jdeferred.Promise;
import org.json.JSONObject;

import butterknife.ButterKnife;
import swj.swj.R;
import swj.swj.common.RestClient;

public class RegisterStepOne extends GetSecurityCodeActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step_one);

        ButterKnife.bind(this);
    }

    @Override
    protected Class<?> getNextActivity() {
        return RegisterStepTwo.class;
    }

    @Override
    protected Promise<JSONObject, VolleyError, Void> getSecurityCode(String username) {
        return RestClient.getInstance().newSecurityCode4Account(username);
    }

}
