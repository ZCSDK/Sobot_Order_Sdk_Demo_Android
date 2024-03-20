package com.sobot.workorder.weight.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.sobot.workorder.R;

/**
 * Created by jinxl on 2017/6/12.
 */

public abstract class SobotBottomDialog extends Dialog {

    protected DialogOnClickListener listener;
    private final int screenHeight;
    protected View sobot_container;

    public SobotBottomDialog(Activity context) {
        // 给Dialog的Window设置样式
        this(context, R.style.CallDialogStyle);

    }

    public SobotBottomDialog(Activity context, int themeResId) {
        // 给Dialog的Window设置样式

        super(context, themeResId);

        // 修改Dialog(Window)的弹出位置
        screenHeight = getScreenHeight(context);
        Window window = getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        if(getWindow() != null){
            View view = getWindow().getDecorView();
            view.getWindowVisibleDisplayFrame(rect);
            lay.width = dm.widthPixels;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - (sobot_container !=null ?sobot_container.getHeight():screenHeight*0.7) - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                dismiss();
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initView();
        sobot_container = getDialogContainer();
        sobot_container.measure(0,0);
        initData();
    }

    /**
     * 获取dialog布局
     * @return
     */
    protected abstract int getLayout();


    /**
     * 获取布局容器的高度
     * @return
     */
    protected abstract View getDialogContainer();

    protected abstract void initData();

    protected abstract void initView();

    public void setOnClickListener(DialogOnClickListener listener) {
        this.listener = listener;
    }

    public interface DialogOnClickListener {
        void onSure();
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    @Override
    public void dismiss() {
        hideKeyboard(getCurrentFocus());
        super.dismiss();
    }
    public void hideKeyboard(final View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}