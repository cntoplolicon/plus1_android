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

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalProfileActivity extends Activity {


    private String TAG = "PersonalProfileSettingsFragment";

    private RelativeLayout re_avatar, re_nickname, re_password, re_sign, re_phone;
    private TextView tv_nickname, tv_sex, tv_sign, tv_phone;
    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery
    private static final int PHOTO_REQUEST_CUT = 3;// result
    private static final int UPDATE_NICK = 4;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        initView();
    }

    private void initView() {

        re_avatar = (RelativeLayout) findViewById(R.id.re_avatar);
        re_nickname = (RelativeLayout) findViewById(R.id.re_nickname);
        re_password = (RelativeLayout) findViewById(R.id.re_password);
        re_sign = (RelativeLayout) findViewById(R.id.re_sign);
        re_phone = (RelativeLayout) findViewById(R.id.re_phone);

        re_avatar.setOnClickListener(new MyListener());
        re_nickname.setOnClickListener(new MyListener());
        re_password.setOnClickListener(new MyListener());
        re_sign.setOnClickListener(new MyListener());
        re_phone.setOnClickListener(new MyListener());

    }

    class MyListener implements View.OnClickListener {
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
                    startActivity(new Intent(PersonalProfileActivity.this, ChangePassword.class));
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
        final AlertDialog alertDialog = new AlertDialog.Builder(PersonalProfileActivity.this).create();
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
