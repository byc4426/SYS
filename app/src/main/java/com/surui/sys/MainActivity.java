package com.surui.sys;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.client.android.CaptureActivity;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private android.widget.TextView text;
    private android.widget.Button but;
    private ImageView image_view;

    private final int MY_PERMISSIONS_REQUEST_LOCATION = 111;
    private final int MY_PERMISSIONS_REQUEST_RE = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.image_view = (ImageView) findViewById(R.id.image_view);
        this.but = (Button) findViewById(R.id.but);
        this.text = (TextView) findViewById(R.id.text);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //6.0动态请求权限定位权限
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
                    // 向用户解释为什么需要这个权限
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setMessage("申请照相机权限，否则将无法拍照！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //申请照相机权限
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_LOCATION);
                                    }
                                })
                                .show();
                    } else {
                        //申请照相机权限
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                } else {
                    startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), 1);
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setMessage("申请读写权限，否则将无法正常使用！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //申请权限
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_RE);
                            }
                        })
                        .show();
            } else {
                //申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_RE);
            }
        }else {
            Util.createDirectory("AAA");
        }

    }

    //请求权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(this, CaptureActivity.class), 1);
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "申请权限失败！", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_RE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Util.createDirectory("AAA");
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "申请权限失败！", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //返回结果的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String result = data.getExtras().getString("result");
            text.setText(result);
            String path = data.getExtras().getString("path");
            String name = "";
            if (path.indexOf("/") != -1) {
                name = path.substring(path.lastIndexOf("/") + 1, path.length());
            }
            Uri destinationUri = Uri.fromFile(new File(Util.createDirectory("AAA"), name));
            Uri sourceUri = Uri.parse("file://" + path);
            UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(16, 9)
                    .withMaxResultSize(1280, 720)
                    .start(MainActivity.this);
        }

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
//            File mPictureFile = StorageHelper.getOutputFile();
//            ImageUtil.copyFile(resultUri.getPath(),mPictureFile.getAbsolutePath());
            Glide.with(MainActivity.this).load(resultUri).into(image_view);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(MainActivity.this, "错误", Toast.LENGTH_SHORT).show();
            final Throwable cropError = UCrop.getError(data);
        }
    }
}
