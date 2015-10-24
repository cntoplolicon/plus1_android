package swj.swj.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.activity.AddTextActivity;
import swj.swj.activity.PublishActivity;
import swj.swj.common.ActivityHyperlinkClickListener;


public class PublishFragment extends Fragment {

    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery

    private String filename;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, container, false);
        Button btnAddImage = (Button) view.findViewById(R.id.btn_image);
        Button btnAddText = (Button) view.findViewById(R.id.btn_text);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setCanceledOnTouchOutside(false);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setContentView(R.layout.activity_dialog);
                TextView tvTakePhoto = (TextView) window.findViewById(R.id.tv_camera);
                TextView tvGallery = (TextView) window.findViewById(R.id.tv_gallery);
                TextView tvCancel = (TextView) window.findViewById(R.id.tv_cancel);
                tvTakePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCamera();
                        alertDialog.cancel();
                    }
                });
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getGallery();
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
        });

        btnAddText.setOnClickListener(new ActivityHyperlinkClickListener(getActivity(), AddTextActivity.class));
        return view;
    }

    private void getCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "myImage");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        filename = "IMG_" + getNowTime() + ".jpg";
        File file = new File(dir, filename);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PHOTO_REQUEST_TAKE_PHOTO);
    }

    private void getGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case PHOTO_REQUEST_TAKE_PHOTO:
                String sdStatus = Environment.getExternalStorageState();
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Log.e(PublishFragment.class.toString(), "SD card is not available/writable right now.");
                    return;
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/" + "myImage" + "/" + filename);
                Uri fileUri = Uri.fromFile(file);
                Intent intentCamera = new Intent(getActivity(), PublishActivity.class).setAction("getCamera").putExtra("imagePath", fileUri);
                startActivity(intentCamera);
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data == null) {
                    return;
                }
                Uri originalUri = data.getData();
                Intent intentGallery = new Intent(getActivity(), PublishActivity.class).setAction("getGallery").putExtra("imagePath", originalUri);
                startActivity(intentGallery);
                break;
        }
    }

    private String getNowTime() {
        Date date = new Date();
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");
        return dataFormat.format(date);
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PublishScreen");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PublishScreen");
    }
}
