package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import swj.swj.R;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends Activity {

    private Button btnLogOut;
    private TextView tvNickName, tvPersonalProfile;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        initView();
    }

    private void initView() {
        tvNickName = (TextView) findViewById(R.id.tv_personal_settings_nickname);
        tvPersonalProfile = (TextView) findViewById(R.id.tv_personal_settings_profile);
        btnLogOut = (Button) findViewById(R.id.btn_logout);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PersonalSettingsActivity.this)
                        .setMessage(getResources().getString(R.string.log_out_confirm))
                        .setPositiveButton(getResources().getString(R.string.submit),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        RestClient.getInstance().signOut(new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                User.clearCurrentUser();
                                                startActivity(new Intent(PersonalSettingsActivity.this, LoginActivity.class));
                                                PersonalSettingsActivity.this.finish();
                                            }
                                        }, new JsonErrorListener(getApplicationContext(), null));
                                    }
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                        .create().show();
            }
        });

        tvPersonalProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonalSettingsActivity.this, PersonalProfileActivity.class));
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public void onResume() {
        super.onResume();
        tvNickName.setText(User.current.getNickname());
    }
}
