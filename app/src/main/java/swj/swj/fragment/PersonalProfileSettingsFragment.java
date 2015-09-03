package swj.swj.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.activity.ChangePassword;

/**
 * Created by jiewei on 9/1/15.
 */
public class PersonalProfileSettingsFragment extends BaseFragment {

    private String TAG = "PersonalProfileSettingsFragment";

    private RelativeLayout re_avatar, re_nickname, re_password, re_sign, re_phone;
    private TextView tv_nickname, tv_sex, tv_sign, tv_phone;
    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery
    private static final int PHOTO_REQUEST_CUT = 3;// result
    private static final int UPDATE_NICK = 4;


    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_personal_profile_settings, null);
        return v;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        re_avatar = (RelativeLayout) getActivity().findViewById(R.id.re_avatar);
        re_nickname = (RelativeLayout) getActivity().findViewById(R.id.re_nickname);
        re_password = (RelativeLayout) getActivity().findViewById(R.id.re_password);
        re_sign = (RelativeLayout) getActivity().findViewById(R.id.re_sign);
        re_phone = (RelativeLayout) getActivity().findViewById(R.id.re_phone);

        re_avatar.setOnClickListener(new MyListener());
        re_nickname.setOnClickListener(new MyListener());
        re_password.setOnClickListener(new MyListener());
        re_sign.setOnClickListener(new MyListener());
        re_phone.setOnClickListener(new MyListener());
    }

    class MyListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_avatar:
                    Log.d(TAG, "re_avatar");
                    showPhotoDialog();
                    break;
                case R.id.re_nickname:
                    Log.d(TAG, "re_nickname");
                    break;
                case R.id.re_password:
                    Log.d(TAG, "re_password");
                    startActivity(new Intent(getActivity(), ChangePassword.class));
                    break;
                case R.id.re_sign:
                    Log.d(TAG, "re_sign");
                    break;
                case R.id.re_phone:
                    Log.d(TAG, "re_phone");
                    break;
            }
        }
    }

    private void showPhotoDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_dialog);
        TextView tv_take_photo = (TextView) window.findViewById(R.id.tv_content1);
        tv_take_photo.setText("拍照");
        tv_take_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imageName = getNowTime() + ".png";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("sdcard/swj/", imageName)));
                startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                alertDialog.cancel();
            }
        });
        TextView tv_gallery = (TextView) window.findViewById(R.id.tv_content2);
        tv_gallery.setText("相册");
        tv_gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getNowTime();
                imageName = getNowTime() + ".png";
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                alertDialog.cancel();
            }
        });

    }

    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dataFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dataFormat.format(date);
    }

}
