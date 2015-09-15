package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.common.LocalUserInfo;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalProfileActivity extends Activity {

    private String TAG = "PersonalProfileActivity";

    private RelativeLayout reAvatar, reNickname, rePassword, reSign, rePhone;
    private TextView tvNickname, tvSign, tvPhone;
    private String nickname, sign, telephone, imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery
    private static final int PHOTO_REQUEST_CUT = 3;// result

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        initView();
    }

    private void initView() {

        nickname = LocalUserInfo.getInstance().getUserInfo("nick_name");
        sign = LocalUserInfo.getInstance().getUserInfo("sign");
        telephone = LocalUserInfo.getInstance().getUserInfo("telephone");

        reAvatar = (RelativeLayout) findViewById(R.id.re_avatar);
        reNickname = (RelativeLayout) findViewById(R.id.re_nickname);
        rePassword = (RelativeLayout) findViewById(R.id.re_password);
        reSign = (RelativeLayout) findViewById(R.id.re_sign);
        rePhone = (RelativeLayout) findViewById(R.id.re_phone);

        tvNickname = (TextView) findViewById(R.id.tv_profile_nickname);
        tvSign = (TextView) findViewById(R.id.tv_profile_sign);
        tvPhone = (TextView) findViewById(R.id.tv_profile_phone);

        tvNickname.setText(nickname);
        tvSign.setText(sign);
        tvPhone.setText(telephone);

        reAvatar.setOnClickListener(new MyListener());
        reNickname.setOnClickListener(new MyListener());
        rePassword.setOnClickListener(new MyListener());
        reSign.setOnClickListener(new MyListener());
        rePhone.setOnClickListener(new MyListener());
    }

    class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.re_avatar:
                    showPhotoDialog();
                    break;
                case R.id.re_nickname:
                    startActivity(new Intent(PersonalProfileActivity.this, UpdateNicknameActivity.class));
                    break;
                case R.id.re_password:
                    startActivity(new Intent(PersonalProfileActivity.this, ChangePasswordActivity.class));
                    break;
                case R.id.re_sign:
                    startActivity(new Intent(PersonalProfileActivity.this, UpdateSignActivity.class));
                    break;
                case R.id.re_phone:
                    startActivity(new Intent(PersonalProfileActivity.this, ResetPhoneActivity.class));
                    break;
            }
        }
    }

    private void showPhotoDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(PersonalProfileActivity.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.alert_dialog);
        TextView tvTakePhoto = (TextView) window.findViewById(R.id.tv_content1);
        tvTakePhoto.setText(getResources().getString(R.string.get_image_from_camera));
        tvTakePhoto.setOnClickListener(new View.OnClickListener() {

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
        tv_gallery.setText(getResources().getString(R.string.gallery));
        tv_gallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
        Date date = new Date();
        SimpleDateFormat dataFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dataFormat.format(date);
    }

    public void onResume() {
        super.onResume();
        String nickname_temp = LocalUserInfo.getInstance().getUserInfo("nick_name");
        if (!nickname_temp.equals(nickname)) {
            if (nickname_temp.isEmpty()) {
                tvNickname.setText(getResources().getString(R.string.unset));
            }
            tvNickname.setText(nickname_temp);
        }

        String sign_temp = LocalUserInfo.getInstance().getUserInfo("sign");
        tvSign.setText(sign_temp);
    }

    public void back(View view) {
        finish();
    }
}
