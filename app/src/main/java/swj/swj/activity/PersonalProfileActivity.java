package swj.swj.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.soundcloud.android.crop.Crop;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import swj.swj.R;
import swj.swj.application.SnsApplication;
import swj.swj.common.ActivityHyperlinkClickListener;
import swj.swj.common.BitmapUtil;
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
        ivAvatar.setImageResource(R.drawable.default_useravatar);
        String avatarUrl = User.current.getAvatar();
        if (avatarUrl != null) {
            ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + avatarUrl, ivAvatar);
        }

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
                Intent intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                if (data == null) {
                    return;
                }
                if (resultCode == Crop.RESULT_ERROR) {
                    Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                processCroppedImage(Crop.getOutput(data));

                break;
        }
    }

    private void processCroppedImage(Uri uri) {
        BitmapUtil.prepareImageForUploading(this, uri)
                .done(new DoneCallback<Bitmap>() {
                    @Override
                    public void onDone(Bitmap bitmap) {
                        ivAvatar.setImageBitmap(bitmap);
                    }
                })
                .fail(new BitmapUtil.ImageProcessingFailureCallback(this))
                .then(new DonePipe<Bitmap, JSONObject, VolleyError, Void>() {
                    @Override
                    public Promise<JSONObject, VolleyError, Void> pipeDone(Bitmap bitmap) {
                        byte[] imageData = BitmapUtil.compressBitmap(bitmap, BitmapUtil.DEFAULT_QUALITY);
                        Map<String, Object> attributes = new HashMap<>();
                        attributes.put("avatar", new ByteArrayBody(imageData, ContentType.create("image/jpeg"), "avatar.jpg"));
                        return RestClient.getInstance().updateUserAvatar(attributes);
                    }
                })
                .done(new DoneCallback<JSONObject>() {
                    @Override
                    public void onDone(JSONObject response) {
                        User.updateCurrentUser(response.toString());
                    }
                })
                .fail(new JsonErrorListener(getApplicationContext(), null) {
                    @Override
                    public void onFail(VolleyError e) {
                        super.onFail(e);
                        ImageLoader.getInstance().displayImage(SnsApplication.getImageServerUrl() + User.current.getAvatar(), ivAvatar);
                    }
                });
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
}
