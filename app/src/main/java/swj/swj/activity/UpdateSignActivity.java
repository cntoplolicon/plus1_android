package swj.swj.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import swj.swj.R;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by jiewei on 9/4/15.
 */
public class UpdateSignActivity extends Activity {

    private EditText biographyInput;

    private View.OnClickListener onSubmit = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sign);

        biographyInput = (EditText) findViewById(R.id.et_biography);
        biographyInput.setText(User.current.getBiography());

        Button submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(onSubmit);
    }

    public void back(View view) {
        finish();
    }
}
