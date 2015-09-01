package swj.swj.fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.activity.ChangePassword;

/**
 * Created by jiewei on 9/1/15.
 */
public class PersonalSettingsFragment extends BaseFragment {

    private Button btnLogOut;
    private TextView tvChangePassword,tvPersonalProfile;

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_personal_settings, null);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnLogOut = (Button) getActivity().findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getActivity());
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

        tvChangePassword = (TextView) getActivity().findViewById(R.id.tv_personal_settings_change_pwd);
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_change_password = new Intent(getActivity(), ChangePassword.class);
                startActivity(i_change_password);
            }
        });

        tvPersonalProfile = (TextView) getActivity().findViewById(R.id.tv_personal_settings_profile);
        tvPersonalProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonalProfileSettingsFragment personalProfileSettingsFragment = new PersonalProfileSettingsFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fl, personalProfileSettingsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }


}
