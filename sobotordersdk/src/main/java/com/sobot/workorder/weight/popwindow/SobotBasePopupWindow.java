package com.sobot.workorder.weight.popwindow;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.RequiresApi;

public class SobotBasePopupWindow {
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private boolean mIsFocusable = true;
    private boolean mIsOutside = true;
    private int mResLayoutId = -1;
    private View mContentView;
    private PopupWindow mPopupWindow;
    private int mAnimationStyle = -1;

    private boolean mClippEnable = true;//default is true
    private boolean mIgnoreCheekPress = false;
    private int mInputMode = -1;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private int mSoftInputMode = -1;
    private boolean mTouchable = true;//default is ture
    private View.OnTouchListener mOnTouchListener;

    private SobotBasePopupWindow(Context context) {
        mContext = context;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean isShow() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    /**
     * @param anchor
     * @param xOff
     * @param yOff
     * @return
     */
    public SobotBasePopupWindow showAsDropDown(View anchor, int xOff, int yOff) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff);
        }
        return this;
    }

    public SobotBasePopupWindow showAsDropDown(View anchor) {
        if (mPopupWindow != null) {
            //兼容7.1以上手机失效问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Rect rect = new Rect();
                anchor.getGlobalVisibleRect(rect);
                //测量
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getRealMetrics(dm);
                int heightPixels = dm.heightPixels;
                int h = heightPixels - rect.bottom;
                mPopupWindow.setHeight(h);
            }
            mPopupWindow.showAsDropDown(anchor);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public SobotBasePopupWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff, gravity);
        }
        return this;
    }


    /**
     * 相对于父控件的位置（通过设置Gravity.CENTER，下方Gravity.BOTTOM等 ），可以设置具体位置坐标
     *
     * @param parent
     * @param gravity
     * @param x       the popup's x location offset
     * @param y       the popup's y location offset
     * @return
     */
    public SobotBasePopupWindow showAtLocation(View parent, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(parent, gravity, x, y);
        }
        return this;
    }

    /**
     * 添加一些属性设置
     *
     * @param popupWindow
     */
    private void apply(PopupWindow popupWindow) {
        popupWindow.setClippingEnabled(mClippEnable);
        if (mIgnoreCheekPress) {
            popupWindow.setIgnoreCheekPress();
        }
        if (mInputMode != -1) {
            popupWindow.setInputMethodMode(mInputMode);
        }
        if (mSoftInputMode != -1) {
            popupWindow.setSoftInputMode(mSoftInputMode);
        }
        if (mOnDismissListener != null) {
            popupWindow.setOnDismissListener(mOnDismissListener);
        }
        if (mOnTouchListener != null) {
            popupWindow.setTouchInterceptor(mOnTouchListener);
        }
        popupWindow.setTouchable(mTouchable);


    }

    private PopupWindow build() {

        if (mContentView == null) {
            mContentView = LayoutInflater.from(mContext).inflate(mResLayoutId, null);
        }

        if (mWidth != 0 && mHeight != 0) {
            mPopupWindow = new PopupWindow(mContentView, mWidth, mHeight);
        } else {
            mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (mAnimationStyle != -1) {
            mPopupWindow.setAnimationStyle(mAnimationStyle);
        }

        apply(mPopupWindow);//设置一些属性

        mPopupWindow.setFocusable(mIsFocusable);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOutsideTouchable(mIsOutside);

        if (mWidth == 0 || mHeight == 0) {
            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //如果外面没有设置宽高的情况下，计算宽高并赋值
            mWidth = mPopupWindow.getContentView().getMeasuredWidth();
            mHeight = mPopupWindow.getContentView().getMeasuredHeight();
        }


        mPopupWindow.update();

        return mPopupWindow;
    }

    public View getmContentView() {
        return mContentView;
    }

    public void setmContentView(View mContentView) {
        this.mContentView = mContentView;
    }

    /**
     * 关闭popWindow
     */
    public void dissmiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public static class PopupWindowBuilder {
        private SobotBasePopupWindow mHwbPopWindow;

        public PopupWindowBuilder(Context context) {
            mHwbPopWindow = new SobotBasePopupWindow(context);
        }

        public PopupWindowBuilder size(int width, int height) {
            mHwbPopWindow.mWidth = width;
            mHwbPopWindow.mHeight = height;
            return this;
        }


        public PopupWindowBuilder setFocusable(boolean focusable) {
            mHwbPopWindow.mIsFocusable = focusable;
            return this;
        }


        public PopupWindowBuilder setView(int resLayoutId) {
            mHwbPopWindow.mResLayoutId = resLayoutId;
            mHwbPopWindow.mContentView = null;
            return this;
        }

        public PopupWindowBuilder setView(View view) {
            mHwbPopWindow.mContentView = view;
            mHwbPopWindow.mResLayoutId = -1;
            return this;
        }

        public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable) {
            mHwbPopWindow.mIsOutside = outsideTouchable;
            return this;
        }

        /**
         * 设置弹窗动画
         *
         * @param animationStyle
         * @return
         */
        public PopupWindowBuilder setAnimationStyle(int animationStyle) {
            mHwbPopWindow.mAnimationStyle = animationStyle;
            return this;
        }


        public PopupWindowBuilder setClippingEnable(boolean enable) {
            mHwbPopWindow.mClippEnable = enable;
            return this;
        }


        public PopupWindowBuilder setIgnoreCheekPress(boolean ignoreCheekPress) {
            mHwbPopWindow.mIgnoreCheekPress = ignoreCheekPress;
            return this;
        }

        public PopupWindowBuilder setInputMethodMode(int mode) {
            mHwbPopWindow.mInputMode = mode;
            return this;
        }

        public PopupWindowBuilder setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener) {
            mHwbPopWindow.mOnDismissListener = onDissmissListener;
            return this;
        }


        public PopupWindowBuilder setSoftInputMode(int softInputMode) {
            mHwbPopWindow.mSoftInputMode = softInputMode;
            return this;
        }


        public PopupWindowBuilder setTouchable(boolean touchable) {
            mHwbPopWindow.mTouchable = touchable;
            return this;
        }

        public PopupWindowBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter) {
            mHwbPopWindow.mOnTouchListener = touchIntercepter;
            return this;
        }


        public SobotBasePopupWindow create() {
            //构建PopWindow
            mHwbPopWindow.build();
            return mHwbPopWindow;
        }

    }

}