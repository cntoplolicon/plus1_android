package swj.swj.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import swj.swj.R;
import swj.swj.activity.PersonalSettingsActivity;


public class MySelfFragment extends BaseFragment {

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_myself, null);

        return v;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TextView tvPersonalSettings = (TextView) getActivity().findViewById(R.id.personal_settings_tv);
        tvPersonalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PersonalSettingsActivity.class));
            }
        });
    }
}
