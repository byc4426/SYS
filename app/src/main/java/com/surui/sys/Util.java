package com.surui.sys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by surui29 on 2017/2/10.
 */

public class Util {
    public static byte[] bytes;

    public static File createDirectory(Context ct, String DirName) {
        ArrayList<StorageBean> storageData = StorageUtils.getStorageData(ct);
        String path = "";
        for (StorageBean s : storageData) {
            if (s.getRemovable()) {//是否可移除，是则是外置存储，USB或SD卡
                if (s.getMounted().equals("mounted")) {//是否已挂载
                    path = s.getPath();//获取此存储介质的真实路径
                }
            }
        }
        if (TextUtils.isEmpty(path) || path.equals("")) {
            path = getSDCardPath();
        }
        File file = new File(path.trim(), DirName.trim());
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    public static String getSDCardPath() {
        File sdcardDir = null;
//判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        } else {
//            Toast.makeText(CaptureActivity.this, "SD卡不可用", Toast.LENGTH_SHORT).show();
            Log.e("---", "SD卡不可用");
            return "";
        }
        return sdcardDir.toString();
    }

    /**
     * 根据byte数组生成文件
     *
     * @param bytes 生成文件用到的byte数组
     */
    public static String createFileWithByte(byte[] bytes) {
        // TODO Auto-generated method stub
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File dir = new File(getSDCardPath(),
                "AAA");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir,
                "IMG_" + System.currentTimeMillis() + ".png");
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
            Log.e("保存文件", file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }

    public static Bitmap dataToBitmap(byte[] data, Camera arg1) {
        Camera.Parameters parameters = arg1.getParameters();
        int imageFormat = parameters.getPreviewFormat();
        int w = parameters.getPreviewSize().width;
        int h = parameters.getPreviewSize().height;
        Rect rect = new Rect(0, 0, w, h);
        YuvImage yuvImg = new YuvImage(data, imageFormat, w, h, null);
        Bitmap bitmap = null;
        try {
            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            yuvImg.compressToJpeg(rect, 100, outputstream);
            bitmap = BitmapFactory.decodeByteArray(outputstream.toByteArray(), 0, outputstream.size());
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            return bitmap;
        } catch (Exception e) {
        }
        return bitmap;
    }


    /**
     * 保存文件
     *
     * @param bm
     * @throws IOException
     */
    public static String saveFile(Context ct, Bitmap bm, String fileName) {
        ArrayList<StorageBean> storageData = StorageUtils.getStorageData(ct);
        String path = "";
        for (StorageBean s : storageData) {
            if (s.getRemovable()) {//是否可移除，是则是外置存储，USB或SD卡
                if (s.getMounted().equals("mounted")) {//是否已挂载
                    path = s.getPath();//获取此存储介质的真实路径
                }
            }
        }
        if (TextUtils.isEmpty(path) || path.equals("")) {
            path = getSDCardPath();
        }
        Log.e("path", path);
        File dir = new File(path.trim(), "AAA");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir,
                fileName + ".png");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 70, bos);
            bos.flush();
            bos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {

        }
        return file.getAbsolutePath();
    }
}
