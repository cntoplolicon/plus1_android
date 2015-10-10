package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.jdeferred.DoneCallback;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.common.CommonMethods;
import swj.swj.common.JsonErrorListener;
import swj.swj.common.RestClient;
import swj.swj.model.User;

/**
 * Created by jiewei on 9/3/15.
 */
public class PersonalProfileActivity extends Activity {

    private static final String IMAGE_FILE_NAME = "personalImage.jpg";
    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery
    @Bind(R.id.re_nickname)
    TextView reNickname;
    @Bind(R.id.tv_profile_nickname)
    TextView tvNickname;
    @Bind(R.id.iv_avatar)
    ImageView ivAvatar;
    @Bind(R.id.re_password)
    TextView rePassword;
    @Bind(R.id.re_sign)
    TextView reSign;
    @Bind(R.id.re_phone)
    TextView rePhone;
    @Bind(R.id.tv_profile_phone)
    TextView tvPhone;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + User.current.getAvatar(), ivAvatar);

        reNickname.setOnClickListener(new ActivityHyperlinkClickListener(this, UpdateNicknameActivity.class));
        rePassword.setOnClickListener(new ActivityHyperlinkClickListener(this, ChangePasswordActivity.class));
        reSign.setOnClickListener(new ActivityHyperlinkClickListener(this, UpdateSignActivity.class));
        rePhone.setOnClickListener(new ActivityHyperlinkClickListener(this, ResetPhoneActivity.class));
    }

    @OnClick(R.id.re_avatar)
    public void showPhotoDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(PersonalProfileActivity.this).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.activity_dialog);
        TextView tvTakePhoto = (TextView) window.findViewById(R.id.tv_camera);
        TextView tvGallery = (TextView) window.findViewById(R.id.tv_gallery);
        TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
        tvTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (CommonMethods.hasSdCard()) {
                    File avatar = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                    intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatar));
                }
                startActivityForResult(intentFromCamera, PHOTO_REQUEST_TAKE_PHOTO);
                alertDialog.cancel();
            }
        });
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*");
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, PHOTO_REQUEST_GALLERY);
                alertDialog.cancel();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:
                beginCrop(data.getData());
                break;
            case PHOTO_REQUEST_TAKE_PHOTO:
                if (CommonMethods.hasSdCard()) {
                    File avatar = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME);
                    beginCrop(Uri.fromFile(avatar));
                } else {
                    //toast error message when unable to find sdcard
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.unable_to_find_sd_card), Toast.LENGTH_LONG).show();
                }
                break;
            case Crop.REQUEST_CROP:
                if (data != null) {
                    handleCrop(resultCode, data);
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("avatar", getAvatar());
                    RestClient.getInstance().updateUserAvatar(attributes).done(
                            new DoneCallback<JSONObject>() {
                                @Override
                                public void onDone(JSONObject response) {
                                    User.updateCurrentUser(response.toString());
                                }
                            }).fail(new JsonErrorListener(getApplicationContext(), null));
                }
                break;
        }
    }

    public void onResume() {
        super.onResume();
        tvNickname.setText(User.current.getNickname());
        tvPhone.setText(User.current.getUsername());
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            ivAvatar.setImageDrawable(null);
            ivAvatar.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private ByteArrayBody getAvatar() {
        Drawable drawable = ivAvatar.getDrawable();
        if (drawable == null) {
            return null;
        }
        Bitmap avatarBitmap = ((BitmapDrawable) drawable).getBitmap();
        try {
            byte[] avatarData = CommonMethods.bitmap2ByteArray(avatarBitmap);
            return new ByteArrayBody(avatarData, ContentType.create("image/png"), "avatar.png");
        } catch (IOException e) {
            Log.e(PersonalProfileActivity.class.getName(), "failed getting avatar data", e);
            return null;
        }
    }
}
