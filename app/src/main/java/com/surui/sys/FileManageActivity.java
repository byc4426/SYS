package com.surui.sys;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManageActivity extends AppCompatActivity {

    private android.widget.ListView listview;
    private File[] filess;
    private TextView openDir;
    private FileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manage);
        filess = Util.createDirectory(FileManageActivity.this,"AAA").listFiles();//文件列表
        openDir = (TextView) findViewById(R.id.openDir);
//        openDir.setText(Util.createDirectory("AAA").getAbsolutePath());
        this.listview = (ListView) findViewById(R.id.listview);
        adapter = new FileAdapter();
        listview.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        filess = Util.createDirectory(FileManageActivity.this,"AAA").listFiles();//文件列表
        adapter.notifyDataSetChanged();
    }

    class FileAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return filess.length;
        }

        @Override
        public Object getItem(int position) {
            return filess[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(FileManageActivity.this).inflate(R.layout.item_file_manage, null);
                viewHolder = new ViewHolder();
                viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.sizeAndTime = (TextView) convertView.findViewById(R.id.timeAndSize);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Glide.with(FileManageActivity.this)
                    .load("file://" + filess[position].getAbsoluteFile())
                    .thumbnail(0.1f)
                    .centerCrop()
                    .into(viewHolder.image);
            viewHolder.name.setText(filess[position].getName());
            viewHolder.sizeAndTime.setText(filess[position].length() / 1024 + "KB    " +
                    getDateTimeFromMillisecond(filess[position].lastModified()));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //设置intent的Action属性
                    intent.setAction(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                        Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), "com.surui.sys.fileprovider", filess[position]);
                        //添加这一句表示对目标应用临时授权该Uri所代表的文件
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        //设置intent的data和Type属性。
                        intent.setDataAndType(imageUri, "image/*");
                        //跳转
                        startActivity(intent);
                    } else {
                        intent.setDataAndType(Uri.fromFile(filess[position]), "image/*");
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView name;
            TextView sizeAndTime;
        }
    }


    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    public String getDateTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }


}
