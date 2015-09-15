package swj.swj.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import swj.swj.R;
import swj.swj.activity.PublishActivity;


public class PublishFragment extends BaseFragment {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;  //take photo
    private static final int PHOTO_REQUEST_GALLERY = 2; //get from gallery
    private static final String[] items = {"拍照", "从照片库里选择", "取消"};

    @Override
    public View initView() {
        View v = View.inflate(mActivity, R.layout.fragment_publish, null);
        Button bt_image = (Button) v.findViewById(R.id.bt_image);
        bt_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                getCamera();
                                break;
                            case 1:
                                getGallery();
                                break;
                        }
                    }
                }).show();
            }
        });
        return v;
    }

    @Override
    public void initData() {
        super.initData();
    }

    //use camera
    public void getCamera() {
        Toast.makeText(mActivity, "进入相机", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
    }

    //Use photo album
    private void getGallery() {
        Toast.makeText(mActivity, "进入相册", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_TAKEPHOTO) {
            Log.e(PublishFragment.class.getName(),"outPutStream no close ");
            String sdStatus = Environment.getExternalStorageState();
            // 检测sd是否可用
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                Log.v("TestFile", "SD card is not avaiable/writeable right now.");
                return;
            }
            String name = getNowTime() + ".jpg";
            Toast.makeText(mActivity, name, Toast.LENGTH_LONG).show();
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FileOutputStream outPutStream = null;
            File file = new File("/sdcard/myImage/");
            String fileName = "/sdcard/myImage/" + name;
            file.mkdirs();

            try {
                outPutStream = new FileOutputStream(fileName);
                // 把数据写入文件
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outPutStream);
            } catch (IOException e) {
                Log.e(PublishFragment.class.getName(),"outPutStream null");
            } finally {
                try {
                    if (outPutStream != null) {
                        outPutStream.close();
                    }
                } catch (IOException e) {
                    Log.e(PublishFragment.class.getName(),"outPutStream no close ");
                }
            }
            startActivity(new Intent(mActivity, PublishActivity.class).setAction("getCamera").putExtra("fileName", fileName));
        } else if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data == null) {
                return;
            }
            Uri originalUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = mActivity.getContentResolver().query(originalUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picPath = cursor.getString(columnIndex);   // 图片的绝对路径
            cursor.close();
            Log.d("picPath", picPath);
            startActivity(new Intent(mActivity, PublishActivity.class).setAction("getGallery").putExtra("picPath", picPath));
        }
    }

    public String getNowTime() {
        Toast.makeText(mActivity, "生成相片", Toast.LENGTH_SHORT).show();
        Date date = new Date();
        SimpleDateFormat dataFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dataFormat.format(date);
    }
}
