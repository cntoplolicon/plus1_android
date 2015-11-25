package com.oneplusapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oneplusapp.R;
import com.oneplusapp.common.ActivityHyperlinkClickListener;
import com.oneplusapp.common.BitmapUtil;
import com.oneplusapp.common.CommonMethods;
import com.oneplusapp.common.JsonErrorListener;
import com.oneplusapp.common.RestClient;
import com.oneplusapp.model.User;
import com.oneplusapp.view.MenuDialog;
import com.oneplusapp.view.UserAvatarImageView;
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

public class PersonalProfileActivity extends BaseActivity {

    private static final String KEY_CAMERA_FILE_URI = "camera_file_uri";

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    private Uri cameraFileUri;

    @Bind(R.id.tv_profile_nickname)
    TextView tvNickname;
    @Bind(R.id.iv_avatar)
    UserAvatarImageView ivAvatar;
    @Bind(R.id.tv_password)
    TextView tvPassword;
    @Bind(R.id.tv_biography)
    TextView tvBiography;
    @Bind(R.id.tv_profile_phone)
    TextView tvPhone;
    @Bind(R.id.rl_user_phone)
    RelativeLayout rlUserPhone;
    @Bind(R.id.rl_password)
    RelativeLayout rlPassword;

    private MenuDialog selectAvatarDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        ButterKnife.bind(this);

        buildSelectAvatarDialog();

        showCurrentUserAvatar();
        ivAvatar.setUser(User.current);
        tvNickname.setOnClickListener(new ActivityHyperlinkClickListener(this, UpdateNicknameActivity.class));
        tvPassword.setOnClickListener(new ActivityHyperlinkClickListener(this, ChangePasswordActivity.class));
        tvBiography.setOnClickListener(new ActivityHyperlinkClickListener(this, UpdateSignActivity.class));
        rlUserPhone.setOnClickListener(new ActivityHyperlinkClickListener(this, ResetPhoneActivity.class));

        if (User.current.getUsername() == null) {
            rlUserPhone.setVisibility(View.GONE);
            rlPassword.setVisibility(View.GONE);
        }

        if (savedInstanceState != null) {
            cameraFileUri = savedInstanceState.getParcelable(KEY_CAMERA_FILE_URI);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CAMERA_FILE_URI, cameraFileUri);
    }

    @OnClick(R.id.rl_avatar)
    public void onAvatarTextViewClicked() {
        selectAvatarDialog.show();
    }

    private void getImageFromGallery() {
        Intent intentFromGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
    }

    private void getImageFromCamera() {
        Intent intentFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (CommonMethods.hasSdCard()) {
            File avatar = BitmapUtil.getImageFile();
            cameraFileUri = Uri.fromFile(avatar);
            intentFromCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraFileUri);
        }
        startActivityForResult(intentFromCamera, CAMERA_REQUEST_CODE);
    }

    private void buildSelectAvatarDialog() {
        selectAvatarDialog = new MenuDialog(this);
        selectAvatarDialog.addButton(R.string.get_image_from_camera, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromCamera();
            }
        });
        selectAvatarDialog.addButton(R.string.get_image_from_gallery, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        selectAvatarDialog.addCancelButton();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                beginCrop(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                if (CommonMethods.hasSdCard()) {
                    BitmapUtil.notifyMediaScanner(this, cameraFileUri);
                    beginCrop(cameraFileUri);
                } else {
                    Toast.makeText(getBaseContext(), R.string.unable_to_find_sd_card, Toast.LENGTH_LONG).show();
                }
                break;
            case Crop.REQUEST_CROP:
                if (data == null) {
                    return;
                }
                if (resultCode == Crop.RESULT_ERROR) {
                    Throwable t = Crop.getError(data);
                    Log.e(PersonalProfileActivity.class.getName(), "failed cropping image", t);
                    Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
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
                        showCurrentUserAvatar();
                    }
                });
    }

    private void showCurrentUserAvatar() {
        if (User.current.getAvatar() == null) {
            ivAvatar.setImageResource(R.drawable.default_user_avatar);
        } else {
            ImageLoader.getInstance().displayImage(User.current.getAvatar(), ivAvatar);
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
}
