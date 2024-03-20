package com.sobot.workorder.weight.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sobot.utils.SobotDensityUtil;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.workorder.R;

import java.util.Locale;


/**
 * 图片选择方式弹窗
 */
public class SobotSelectPicDialog extends Dialog {

    protected Button btn_take_photo, btn_pick_photo, btn_cancel;
    protected Button btn_call_up_task_tel;//这个button是呼叫任务详情页面右边控件的点击传过来的电话号码
    protected View btn_call_up_task_tel_line;
    protected LinearLayout coustom_pop_layout;
    protected View.OnClickListener itemsOnClick;
    protected final int screenHeight;
    private Activity mActivity;

    public SobotSelectPicDialog(Activity context, View.OnClickListener itemsOnClick) {
        super(context, R.style.SobotClearHistoryDialogStyle);
        mActivity = context;
        this.itemsOnClick = itemsOnClick;
        screenHeight = SobotDensityUtil.getScreenHeight(context);
        // 修改Dialog(Window)的弹出位置
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            setParams(context, layoutParams);
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeAppLanguage();
        setContentView(R.layout.sobot_dialog_picture_popup);
        initView();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getY() <= (screenHeight - coustom_pop_layout.getHeight() - 20)) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                dismiss();
            }
        }
        return true;
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        if (getWindow() != null) {
            View view = getWindow().getDecorView();
            view.getWindowVisibleDisplayFrame(rect);
        }
        lay.width = dm.widthPixels;
    }

    protected void initView() {
        btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        btn_pick_photo = (Button) findViewById(R.id.btn_pick_photo);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_call_up_task_tel = (Button) findViewById(R.id.btn_call_up_task_tel);
        btn_call_up_task_tel_line = (View) findViewById(R.id.btn_call_up_task_tel_line);
        coustom_pop_layout = (LinearLayout) findViewById(R.id.pop_layout);

        btn_take_photo.setOnClickListener(itemsOnClick);
        btn_pick_photo.setOnClickListener(itemsOnClick);
        btn_cancel.setOnClickListener(itemsOnClick);

        btn_take_photo.setText(R.string.sobot_attach_take_pic_string);
        btn_pick_photo.setText(R.string.sobot_choice_form_picture_string);
        btn_call_up_task_tel.setVisibility(View.GONE);
        btn_call_up_task_tel_line.setVisibility(View.GONE);
    }
    public void changeAppLanguage() {
        try {
            Locale locale = (Locale) SobotSharedPreferencesUtil.getInstance(getContext()).get("SobotLanguage", Locale.class);
            if (locale == null) {
                //获取系统语言
                locale = getSysPreferredLocale();
            }
            updateLanguage(locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Locale getSysPreferredLocale() {
        Locale locale;
        //7.0以下直接获取系统默认语言
        if (Build.VERSION.SDK_INT < 24) {
            // 等同于context.getResources().getConfiguration().locale;
            locale = Locale.getDefault();
            // 7.0以上获取系统首选语言
        } else {
            /*
             * 以下两种方法等价，都是获取经API调整过的系统语言列表（可能与用户实际设置的不同）
             * 1.context.getResources().getConfiguration().getLocales()
             * 2.LocaleList.getAdjustedDefault()
             */
            // 获取用户实际设置的语言列表
            locale = LocaleList.getDefault().get(0);
        }
        return locale;
    }
    public void updateLanguage(Locale locale) {
        try {
            if (locale != null) {
                Resources resources = mActivity.getResources();
                DisplayMetrics metrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                //Android 7.0以上的方法
                if (Build.VERSION.SDK_INT >= 24) {
                    configuration.setLocale(locale);
                    configuration.setLocales(new LocaleList(locale));
                    if(mActivity!=null) {
                        mActivity.createConfigurationContext(configuration);
                    }
                    //实测，updateConfiguration这个方法虽然很多博主说是版本不适用
                    //但是我的生产环境androidX+Android Q环境下，必须加上这一句，才可以通过重启App来切换语言
                    resources.updateConfiguration(configuration, metrics);

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    //Android 4.1 以上方法
                    configuration.setLocale(locale);
                    resources.updateConfiguration(configuration, metrics);
                } else {
                    configuration.locale = locale;
                    resources.updateConfiguration(configuration, metrics);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}