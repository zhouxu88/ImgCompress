package com.zx.lunbanpress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.zx.lunbanpress.ImgUtils.getFileSize;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Activity activity;
    private ImageView iv1, iv2;
    private String mPath, mPath2;
    private int tag;
    private String outPath; //压缩后图片的保存途径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);


        findViewById(R.id.open_btn).setOnClickListener(this);
        findViewById(R.id.open_btn2).setOnClickListener(this);
        findViewById(R.id.open_btn3).setOnClickListener(this);
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PlusImgActivity.class);
                intent.putExtra("path", mPath);
                startActivity(intent);
            }
        });

        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PlusImgActivity.class);
                intent.putExtra("path", mPath2);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    refreshAdapter(PictureSelector.obtainMultipleResult(data));
                    break;
            }
        }
    }

    // 图片选择结果回调
    private void refreshAdapter(List<LocalMedia> localMediaList) {
        // 例如 LocalMedia 里面返回三种path
        // 1.media.getPath(); 为原图path
        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
        // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
        Log.i(TAG, "onActivityResult:" + localMediaList.size());
        for (LocalMedia localMedia : localMediaList) {
            String path = localMedia.getPath();
            String compressPath = localMedia.getCompressPath();
            Log.i(TAG, "compressPath:----->" + compressPath);
        }
        mPath = localMediaList.get(0).getPath();
        Glide.with(activity).load(mPath).into(iv1);

        Bitmap bmp = BitmapFactory.decodeFile(mPath);//这里的bitmap是个空
        int outWidth = bmp.getWidth();
        int outHeight = bmp.getHeight();
        Log.i(TAG, "原图：width:" + outWidth + ";   height:" + outHeight);
        Log.i(TAG, "原图实际:--->" + getFileSize(mPath));
        Log.i(TAG, "压缩后图片的路径:----->" + ImgUtils.COMPRESS_PIC_PATH);

        mPath2 = localMediaList.get(0).getCompressPath();
//        Bitmap bmp2 = BitmapFactory.decodeFile(mPath2);
//        int outWidth2 = bmp2.getWidth();
//        int outHeight2 = bmp2.getHeight();
//        Log.i(TAG, "压缩后图：width:" + outWidth2 + ";   height" + outHeight2);
//        Log.i(TAG, "压缩后实际:--->" + getFileSize(mPath2));
//        Glide.with(activity).load(mPath2).into(iv2);

        switch (tag) {
            case 1:
                try {
                    outPath = ImgUtils.compressQuality(mPath);
                    Glide.with(activity).load(outPath).into(iv2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    outPath = ImgUtils.compressScale(mPath, 480, 800);
                    Glide.with(activity).load(outPath).into(iv2);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                lunBanPress(mPath);
                break;
        }
        Log.i(TAG, "压缩后图片的路径 outPath:-------->" + outPath);
    }


    private void lunBanPress(String path) {
        String pressPath = Environment.getExternalStorageDirectory().getPath();
        Luban.with(this)
                .load(path)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(pressPath)                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        Log.i(TAG, "onStart:开始鲁班压缩 ");
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        Glide.with(activity).load(file).into(iv2);
                        Log.i(TAG, "onSuccess: 鲁班压缩成功 ：");
                        try {
                            int size = new FileInputStream(file).available();
                            Log.i("tag", "鲁班压缩 size--->:" + "byte:" + size + "    kb:"
                                    + (float) size / 1024);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        Log.i(TAG, "onError: 鲁班压缩出错");
                    }
                }).launch();    //启动压缩
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_btn:
                tag = 1;
                break;
            case R.id.open_btn2:
                tag = 2;
                break;
            case R.id.open_btn3:
                tag = 3;
                break;
        }
        PictureSelectorConfig.initMultiConfig(activity);
    }
}
