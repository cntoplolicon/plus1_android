package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.model.User;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalProfileActivity extends Activity {

    private TextView tvNickname, tvPhone, reAvatar, reNickname, rePassword, reSign, rePhone;
    private String imageName;
    ImageView ivAvatar;
    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        initView();
    }

    private void initView() {

        reAvatar = (TextView) findViewById(R.id.re_avatar);
        reNickname = (TextView) findViewById(R.id.re_nickname);
        rePassword = (TextView) findViewById(R.id.re_password);
        reSign = (TextView) findViewById(R.id.re_sign);
        rePhone = (TextView) findViewById(R.id.re_phone);
        tvNickname = (TextView) findViewById(R.id.tv_profile_nickname);
        tvPhone = (TextView) findViewById(R.id.tv_profile_phone);

        ivAvatar = (ImageView)findViewById(R.id.iv_avatar);
        Log.d("PersonalProfile", User.current.getAvatar());
        String imageUrl = "http://infection-development.s3-website.cn-north-1.amazonaws.com.cn/" + User.current.getAvatar();

        ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                ivAvatar.setImageBitmap(loadedImage);
            }

        });

        tvPhone.setText(User.current.getUsername());

        reNickname.setOnClickListener(new ActivityHyperlinkClickListener(this, UpdateNicknameActivity.class));
        rePassword.setOnClickListener(new ActivityHyperlinkClickListener(this, ChangePasswordActivity.class));
        reSign.setOnClickListener(new ActivityHyperlinkClickListener(this, UpdateSignActivity.class));
        rePhone.setOnClickListener(new ActivityHyperlinkClickListener(this, ResetPhoneActivity.class));
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
                startActivityForResult(intent, PHOTO_REQUEST_TAKE_PHOTO);
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
        tvNickname.setText(User.current.getNickname());
    }

    public void back(View view) {
        finish();
    }
}
