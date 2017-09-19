package com.zx.lunbanpress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者： 周旭 on 2017年9月19日 0019.
 * 邮箱：374952705@qq.com
 * 博客：http://www.jianshu.com/u/56db5d78044d
 */

public class ImgUtils {

    //存储压缩后的图片的路径
    public static final String COMPRESS_PIC_PATH = Environment.getExternalStorageDirectory().getPath() + "/";

    /**
     * 获取本地文件大小
     *
     * @param imgPath 图片的路径
     * @return 图片实际的大小，单位byte
     */
    public static int getFileSize(String imgPath) {
        int size = 0;
        try {
            FileInputStream fis = new FileInputStream(new File(imgPath));
            size = fis.available();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    //获取系统时间
    public static String getSystemTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        //获取当前时间     
        String currTime = formatter.format(curDate);
        return currTime;
    }


    /**
     * 质量压缩
     *
     * @param imgPath 原图的路径
     * @return 返回压缩后的图片的路径
     */
    public static String compressQuality(String imgPath) throws FileNotFoundException {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        newOpts.inSampleSize = 1; //不压缩
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        String outPath = COMPRESS_PIC_PATH + getSystemTime() + ".jpg";
        boolean isSuccess = storeImage(bitmap, outPath);
        Log.i("tag", "质量压缩 size--->:" + "byte:" + getFileSize(outPath) + "    kb:"
                + (float)getFileSize(outPath) / 1024);
        if (isSuccess) {
            return outPath;
        }
        return "";
    }


    /**
     * 按比例压缩
     *
     * @param path    原图片路径
     * @param targetW 压缩后宽度
     * @param targetH 压缩后高度
     * @return 压缩后的图片的保存路径
     */
    public static String compressScale(String path, int targetW, int targetH) throws FileNotFoundException {
        // 获取option  
        BitmapFactory.Options options = new BitmapFactory.Options();
        // inJustDecodeBounds设置为true,这样使用该option decode出来的Bitmap是null，  
        // 只是把长宽存放到option中  
        options.inJustDecodeBounds = true;
        // 此时bitmap为null  
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int inSampleSize = 1; // 1是不缩放  
        // 计算宽高缩放比例  
        int inSampleSizeW = options.outWidth / targetW;
        int inSampleSizeH = options.outHeight / targetH;
        // 最终取大的那个为缩放比例，这样才能适配，例如宽缩放3倍才能适配屏幕，而  
        // 高不缩放就可以，那样的话如果按高缩放，宽在屏幕内就显示不下了  
        if (inSampleSizeW > inSampleSizeH) {
            inSampleSize = inSampleSizeW;
        } else {
            inSampleSize = inSampleSizeH;
        }
        // 一定要记得将inJustDecodeBounds设为false，否则Bitmap为null  
        options.inJustDecodeBounds = false;
        // 设置缩放比例(采样率)  
        options.inSampleSize = inSampleSize;
        bitmap = BitmapFactory.decodeFile(path, options);
        String outPath = COMPRESS_PIC_PATH + getSystemTime() + ".jpg";
        boolean isSuccess = storeImage(bitmap, outPath);
        Log.i("tag", "按比例压缩 size--->:" + "byte:" + getFileSize(outPath) + "    kb:"
                + (float)getFileSize(outPath) / 1024);
        if (isSuccess) {
            return outPath;
        }
        return "";
    }

    /**
     * 把bitmap转化成图片存储在本地
     *
     * @param bitmap
     * @param outPath 本地的存储路径
     * @throws FileNotFoundException
     */
    public static boolean storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outPath);
        boolean compressResult = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        return compressResult;
    }
}
