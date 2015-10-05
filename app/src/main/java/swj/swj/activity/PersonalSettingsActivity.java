package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.adapter.HomePageListItemViewsAdapter;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends Activity {

    @Bind(R.id.tv_personal_settings_nickname)
    TextView tvNickName;

    @OnClick(R.id.tv_personal_settings_profile)
    public void submit() {
        startActivity(new Intent(PersonalSettingsActivity.this, PersonalProfileActivity.class));
    }

    @OnClick(R.id.btn_logout)
    public void logout() {
        new AlertDialog.Builder(PersonalSettingsActivity.this)
                .setMessage(getResources().getString(R.string.log_out_confirm))
                .setPositiveButton(getResources().getString(R.string.submit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                RestClient.getInstance().signOut(new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        User.clearCurrentUser();
                                        HomePageListItemViewsAdapter.getInstance().reset();
                                        startActivity(new Intent(PersonalSettingsActivity.this, LoginActivity.class));
                                        finish();
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        ButterKnife.bind(this);
    }

    public void onResume() {
        super.onResume();
        tvNickName.setText(User.current.getNickname());
    }
}
