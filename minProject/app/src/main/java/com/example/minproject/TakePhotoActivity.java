package com.example.minproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakePhotoActivity extends AppCompatActivity {
    private static final String TAG = "TakePhotoActivity";
    private Button takePhoto;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private ImageView iv_show;
    private int viewWidth, viewHeight;      //mSurfaceView的宽和高
    //用于申请权限
    private static final int REQUEST_PERMISSIONS_CODE = 1;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo_activity);

        if (!android.os.Environment.getExternalStorageState().equals(  //判断手机是否安装SD卡
                android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "请安装SD卡！", Toast.LENGTH_SHORT).show(); // 提示安装SD卡

            Log.e("minProject", "没有SD卡喔" );

        }


        initView();

    }
    private void initView() {
        takePhoto = findViewById(R.id.take_photo);
        iv_show = findViewById(R.id.image_show);
        mSurfaceView = findViewById(R.id.photo_preview);
        viewWidth = mSurfaceView.getWidth();
        viewHeight = mSurfaceView.getHeight();
        mSurfaceHolder = mSurfaceView.getHolder();
        //不需要自己的缓冲区
        /*表明该Surface不包含原生数据，Surface用到的数据由其他对象提供，在Camera图像预览中就使用该类型的
        Surface，有Camera负责提供给预览Surface数据，这样图像预览会比较流畅。如果设置这种类型则就不能调用
        lockCanvas来获取Canvas对象了。需要注意的是，在高版本的Android SDK中，setType这个方法已经被depreciated了。*/
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);    //因为显示画面采用的是Camera回调画面，而不是Surface的信息
        //添加回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //初始化Camera
                initCamera();
                Log.d(TAG, "SurfaceView已生成");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //销毁，释放资源
                Log.d(TAG, "SurfaceView已被销毁");
                if (camera != null) {
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                    camera.release();
                    camera = null;
                }
            }
        });
        //设置点击监听
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {       //快门按下一瞬间会发生的操作

                    }
                }, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {

                    }
                }, pictureCallback);
                takePhoto.setVisibility(View.GONE);
            }
        });
    }

    private void initCamera() {
        //开启
        camera = Camera.open();
        //设置旋转角度
        camera.setDisplayOrientation(90);   //相对于预览画面而言，否则预览画面会是外的
        if (camera != null) {
            try {
                Camera.Parameters parameters = camera.getParameters();
                //设置预览照片的大小
                parameters.setPreviewFpsRange(viewWidth, viewHeight);
                //设置相机预览照片帧数
                parameters.setPreviewFpsRange(4, 10);
                //设置图片格式
                parameters.setPictureFormat(ImageFormat.JPEG);
                //图片质量
                parameters.set("jpeg-quality", 90);
                //设置照片的大小
                parameters.setPictureSize(viewWidth, viewHeight);
                //通过SurfaceView显示预览
                camera.setPreviewDisplay(mSurfaceHolder);
                //开始预览
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            final Bitmap resource = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (resource == null) {
                Toast.makeText(TakePhotoActivity.this, "拍照失败", Toast.LENGTH_SHORT).show();
            }
            //矩阵
            final Matrix matrix = new Matrix();
            matrix.setRotate(90);   //用于旋转，否则最终拍照效果会旋转90度；
            final Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(),
                    resource.getHeight(), matrix, true);
            if (bitmap != null && iv_show != null && iv_show.getVisibility() == View.GONE) {
                camera.stopPreview();
                iv_show.setVisibility(View.VISIBLE);
                mSurfaceView.setVisibility(View.GONE);
                Toast.makeText(TakePhotoActivity.this, "拍照成功", Toast.LENGTH_SHORT).show();
                iv_show.setImageBitmap(bitmap);
            }

            //todo
            //获取sd卡根目录
            File appDir = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
            if (!appDir.exists()) {      //如果该目录不存在
                appDir.mkdir();          //就创建该目录

            }
            String fileName = System.currentTimeMillis() + ".jpg"; //将获取当前系统时间设置为照片名称
            File file = new File(appDir, fileName);  //创建文件对象

            try {  //保存拍到的图片
                FileOutputStream fos = new FileOutputStream(file);  //创建一个文件输出流对象
                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);  //将图片内容压缩为JPEG格式输出到输出流对象中
                fos.flush();                                        //将缓冲区中的数据全部写出到输出流中
                fos.close();                                        //关闭文件输出流对象
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //将照片插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(TakePhotoActivity.this.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // 最后通知图库更新
            TakePhotoActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + "")));
            Toast.makeText(TakePhotoActivity.this, "照片保存至：" + file, Toast.LENGTH_LONG).show();
            Log.e("minProject", "照片保存至：" + file);
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        // 动态权限检查
        if (!isRequiredPermissionsGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //以下是AppCompat的一个方法，输入需要申请的权限的字符数组，会自动调用函数弹窗询问用户是否允许权限使用；
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS_CODE);
        }
    }

    /**
     * 判断我们需要的权限是否被授予，只要有一个没有授权，我们都会返回 false。
     *
     * @return true 权限都被授权
     */
    private boolean isRequiredPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

}
