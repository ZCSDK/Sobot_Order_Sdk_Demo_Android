package com.sobot.workorder.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.sobot.common.utils.SobotImageUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.workorderlibrary.api.SobotOrderService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class SobotOrderImageUtils {

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // Setting post rotate to 90
        Matrix mtx = new Matrix();

        if (rotate != 0 && bitmap != null) {
            mtx.postRotate(rotate, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
        }
        // mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    public static void sendPicByUri(Object tag, String fileNumKey, Context context, Uri selectedImage,
                                    SobotOrderService zhiChiApi, SobotResultCallBack
                                            resultCallBack) {
        String picturePath = SobotImageUtils.getPath(context, selectedImage);
        SobotLogUtils.i("picturePath:" + picturePath);
        if (!TextUtils.isEmpty(picturePath)) {
            sendPicLimitBySize(tag, fileNumKey, picturePath, context, zhiChiApi, resultCallBack);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(context, "找不到图片", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendPicLimitBySize(tag, fileNumKey, file.getAbsolutePath(), context, zhiChiApi, resultCallBack);
        }
    }

    public static void sendPicLimitBySize(Object tag, String fileNumKey, String filePath, Context
            context, SobotOrderService zhiChiApi, SobotResultCallBack resultCallBack) {
        SobotLogUtils.i("filepathaaaaa:" + filePath);
        SobotLogUtils.i("filepathaaaaa:" + new File(filePath).exists());
        Bitmap bitmap = compress(filePath, context);
        if (bitmap != null) {
            int degree = readPictureDegree(filePath);
            bitmap = rotateBitmap(bitmap, degree);
            if (!(filePath.endsWith(".gif") || filePath.endsWith(".GIF"))) {
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(filePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            long size = getFileSize(filePath);
            @SuppressWarnings("unused")
            DecimalFormat df = new DecimalFormat("#.00");
            if (size < (20 * 1024 * 1024)) {
                if (zhiChiApi!=null){
                    zhiChiApi.sendFileByWorkOrder(tag, fileNumKey, filePath, resultCallBack);
                }
            } else {
                Toast toast = Toast.makeText(context, "图片大小需小于20M", Toast.LENGTH_LONG);
                // 可以控制toast显示的位置
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(context, "图片格式错误", Toast.LENGTH_LONG);
            // 可以控制toast显示的位置
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 10);
            toast.show();
        }

    }

    @SuppressWarnings("deprecation")
    public static Bitmap compress(String filePath, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置后decode图片不会返回一个bitmap对象，但是会将图片的信息封装到Options中
        BitmapFactory.decodeFile(filePath, options);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算采样大小
     *
     * @param options   选项
     * @param reqWidth  最大宽度
     * @param reqHeight 最大高度
     * @return 采样大小
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static long getFileSize(String filePath) {

        File f = new File(filePath);
        long s = 0;
        try {
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                s = fis.available();
            } else {
                f.createNewFile();
                System.out.println("文件不存在");
            }
            return s;
        } catch (Exception e) {
            //  Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

}
