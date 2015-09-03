package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import swj.swj.R;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalSettingsActivity extends Activity {

    private Button btnLogOut;
    private TextView tvChangePassword, tvPersonalProfile;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_settings);
        initView();
    }

    private void initView() {
        btnLogOut = (Button) findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(PersonalSettingsActivity.this);
                alertdialogbuilder.setMessage("确认退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //quit fun
                    }
                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog altLogOut = alertdialogbuilder.create();
                altLogOut.show();
            }
        });

        tvPersonalProfile = (TextView) findViewById(R.id.tv_personal_settings_profile);
        tvPersonalProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*PersonalProfileSettingsFragment personalProfileSettingsFragment = new PersonalProfileSettingsFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fl, personalProfileSettingsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                startActivity(new Intent(PersonalSettingsActivity.this, PersonalProfileActivity.class));
            }
        });
    }
}
