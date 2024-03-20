package com.sobot.workorder.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.sobot.common.utils.SobotImageUtils;
import com.sobot.network.http.HttpBaseUtils;
import com.sobot.pictureframe.SobotBitmapUtil;
import com.sobot.utils.SobotDensityUtil;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotMD5Util;
import com.sobot.widget.image.photoview.SobotPhotoView;
import com.sobot.workorder.R;
import com.sobot.workorder.weight.image.SoborRoundProgressBar;
import com.sobot.workorder.weight.image.SobotGifView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//图片预览界面
public class SobotPhotoActivity extends Activity {

    private SobotPhotoView mImageView;

    private SobotGifView sobot_image_view;
    private RelativeLayout sobot_rl_gif;
    String imageUrL;
    Bitmap bitmap;
    String sdCardPath;
    private SoborRoundProgressBar sobot_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sobot_activity_wo_photo);
        sobot_progress = (SoborRoundProgressBar) findViewById(R.id.sobot_pic_progress_round);
        sobot_progress.setRoundWidth(10);//设置圆环的宽度
        sobot_progress.setCricleProgressColor(Color.WHITE);
        sobot_progress.setTextColor(Color.WHITE);
        sobot_progress.setTextDisplayable(true);
        sobot_progress.setVisibility(View.GONE);
        mImageView = (SobotPhotoView) findViewById(R.id.sobot_order_big_photo);
        sobot_image_view = (SobotGifView) findViewById(R.id.sobot_image_view);
        sobot_image_view.setIsCanTouch(true);
        sobot_rl_gif = (RelativeLayout) findViewById(R.id.sobot_rl_gif);
        sobot_rl_gif.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sobot_image_view.setLoadFinishListener(new SobotGifView.LoadFinishListener() {
            @Override
            public void endCallBack(String pathAbsolute) {
                showView(pathAbsolute);
            }
        });

        initBundleData(savedInstanceState);

        SobotLogUtils.i("SobotPhotoActivity-------" + imageUrL);

        if (TextUtils.isEmpty(imageUrL)) {
            return;
        }

        if (imageUrL.startsWith("http")) {
            File dirPath = this.getImageDir(this);
            String encode = SobotMD5Util.encode(imageUrL);
            File savePath = new File(dirPath, encode);
            sdCardPath = savePath.getAbsolutePath();
            if (!savePath.exists()) {
                if (imageUrL.contains("?")) {
                    imageUrL = imageUrL.substring(0, imageUrL.indexOf("?"));
                }
                displayImage(imageUrL, savePath, sobot_image_view);
            } else {
                showView(savePath.getAbsolutePath());
            }
        } else {
            File gifSavePath = new File(imageUrL);
            sdCardPath = imageUrL;
            if (gifSavePath.exists()) {
                showView(imageUrL);
            }
        }
        sobot_rl_gif.setVisibility(View.VISIBLE);
    }

    private void initBundleData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            imageUrL = getIntent().getStringExtra("imageUrL");
        } else {
            imageUrL = savedInstanceState.getString("imageUrL");
        }

    }

    void showView(String savePath) {
        if (!TextUtils.isEmpty(imageUrL)
                && (imageUrL.endsWith(".gif") || imageUrL.endsWith(".GIF"))
        ) {
            showGif(savePath);
        } else {
            if (!TextUtils.isEmpty(imageUrL)
                    && (imageUrL.endsWith(".gif") || imageUrL.endsWith(".GIF"))) {
                showGif(savePath);
            } else {
                bitmap = SobotBitmapUtil.compress(savePath, getApplicationContext(), true);
                //判断图片是否有旋转，有的话旋转后再显示
                try {
                    int degree = SobotImageUtils.readPictureDegree(savePath);
                    if (degree > 0) {
                        bitmap = SobotImageUtils.rotateBitmap(bitmap, degree);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
                }
                mImageView.setVisibility(View.VISIBLE);

                mImageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageView.playSoundEffect(SoundEffectConstants.CLICK);
                        finish();
                    }
                });

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mImageView.playSoundEffect(SoundEffectConstants.CLICK);
        finish();
    }

    private void showGif(String savePath) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(savePath);
            bitmap = BitmapFactory.decodeFile(savePath);
//			sobot_image_view.setGifImageType(GifView.GifImageType.COVER);
            sobot_image_view.setGifImage(in, imageUrL);
            int screenWidth = SobotDensityUtil
                    .getScreenWidth(SobotPhotoActivity.this);
            int screenHeight = SobotDensityUtil
                    .getScreenHeight(SobotPhotoActivity.this);
            int w = SobotDensityUtil.dp2px(SobotPhotoActivity.this,
                    bitmap.getWidth());
            int h = SobotDensityUtil.dp2px(SobotPhotoActivity.this,
                    bitmap.getHeight());
            if (w == h) {
                if (w > screenWidth) {
                    w = screenWidth;
                    h = w;
                }
            } else {
                if (w > screenWidth) {

                    h = (int) (h * (screenWidth * 1.0f / w));
                    w = screenWidth;
                }
                if (h > screenHeight) {

                    w = (int) (w * (screenHeight * 1.0f / h));
                    h = screenHeight;

                }
            }
            SobotLogUtils.i("bitmap" + w + "*" + h);
//			sobot_image_view.setShowDimension(w, h);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    w, h);
            sobot_image_view.setLayoutParams(layoutParams);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sobot_rl_gif.setVisibility(View.VISIBLE);
    }


    public void displayImage(String url, File saveFile, final SobotGifView gifView) {
        sobot_progress.setVisibility(View.VISIBLE);
        // 下载图片
        HttpBaseUtils.getInstance().download(url, saveFile, new HttpBaseUtils.FileCallBack() {

            @Override
            public void onResponse(File file) {
                SobotLogUtils.i("down load onSuccess gif"
                        + file.getAbsolutePath());
                // 把图片文件打开为文件流，然后解码为bitmap
                showView(file.getAbsolutePath());
                sobot_progress.setProgress(100);
                sobot_progress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e, String msg, int responseCode) {
                SobotLogUtils.w("图片下载失败:" + msg, e);
            }

            @Override
            public void inProgress(int progress) {
                //SobotLogUtils.i("图片下载进度:" + progress);
                sobot_progress.setProgress(progress);
            }
        });
    }

    public File getFilesDir(Context context, String tag) {
        if (isSdCardExist() == true) {
            return context.getExternalFilesDir(tag);
        } else {
            return context.getFilesDir();
        }
    }

    public File getImageDir(Context context) {
        File file = getFilesDir(context, "images");
        return file;
    }

    public boolean isSdCardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (bitmap != null && bitmap.isRecycled() == false) {
            bitmap.recycle();
            System.gc();
        }
        super.onDestroy();
    }


    protected void onSaveInstanceState(Bundle outState) {
        //被摧毁前缓存一些数据
        outState.putString("imageUrL", imageUrL);
        super.onSaveInstanceState(outState);
    }
}