package com.surui.sys;

import android.os.Environment;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by surui29 on 2017/2/10.
 */

public class Util {

    public static File createDirectory(String DirName){
        File file = new File(getSDCardPath(),DirName);
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
}
