package swj.swj.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Matrix;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.activity.AddTextActivity;
import swj.swj.activity.PublishActivity;
import swj.swj.common.ActivityHyperlinkClickListener;


public class PublishFragment extends Fragment {

    private static final int PHOTO_REQUEST_TAKE_PHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery

    private String fileNames;

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
                window.setContentView(R.layout.activity_dialog);
                TextView tvTakePhoto = (TextView) window.findViewById(R.id.tv_1);
                TextView tvGallery = (TextView) window.findViewById(R.id.tv_2);
                TextView tvCancel = (TextView) window.findViewById(R.id.tv_3);
                tvTakePhoto.setText(getResources().getString(R.string.get_image_from_camera));
                tvGallery.setText(getResources().getString(R.string.gallery));
                tvCancel.setText(getResources().getString(R.string.cancel));
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
        Toast.makeText(getActivity(), "进入相机", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + "myImage");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        fileNames = "IMG_" + getNowTime() + ".jpg";
        File file = new File(dir, fileNames);
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PHOTO_REQUEST_TAKE_PHOTO);
    }

    private void getGallery() {
        Toast.makeText(getActivity(), "进入相册", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKE_PHOTO:
                String sdStatus = Environment.getExternalStorageState();
                // 检测sd是否可用
                if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                    Log.e(PublishFragment.class.toString(), "SD card is not available/writable right now.");
                    return;
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/" + "myImage" + "/" + fileNames);
                try {
                    Uri photoUri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                            file.getAbsolutePath(), null, null));
                    String photoPath = getRealFilePath(getActivity(), photoUri);
                    Intent intent = new Intent(getActivity(), PublishActivity.class).setAction("getCamera").putExtra("imagePath", photoPath);
                    startActivity(intent);
                } catch (FileNotFoundException e) {
                    Log.e(PublishFragment.class.toString(), "file not found", e);
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data == null) {
                    return;
                }
                Uri originalUri = data.getData();
                String picturePath = getRealFilePath(getActivity(), originalUri);
                Intent intent = new Intent(getActivity(), PublishActivity.class).setAction("getGallery").putExtra("imagePath", picturePath);
                startActivity(intent);
                break;
        }
    }

    public String getNowTime() {
        Date date = new Date();
        SimpleDateFormat dataFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dataFormat.format(date);
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}
