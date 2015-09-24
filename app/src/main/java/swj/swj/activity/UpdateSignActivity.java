package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by jiewei on 9/4/15.
 */
public class UpdateSignActivity extends Activity {

    @Bind(R.id.et_biography)
    EditText biographyInput;

    @OnClick(R.id.btn_submit)
    public void submit() {
        String biography = biographyInput.getText().toString();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("biography", biography);
        RestClient.getInstance().updateUserAttributes(attributes,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        User.updateCurrentUser(response.toString());
                        finish();
                    }
                }, new JsonErrorListener(getApplicationContext(), null));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);
        ButterKnife.bind(this);

        biographyInput.setText(User.current.getBiography());

    }

    public void back(View view) {
        finish();
    }
}
