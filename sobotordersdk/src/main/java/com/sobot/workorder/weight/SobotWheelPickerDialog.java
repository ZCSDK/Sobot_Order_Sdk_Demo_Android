package com.sobot.workorder.weight;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.weight.wheelpicker.SobotWheelPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by 12155 on 2016/5/22.
 */
public class SobotWheelPickerDialog extends Dialog implements View.OnClickListener {

    private TextView tv_left_text;
    private TextView tv_right_text;
    private SobotWheelPicker curvedPicker;
    private String dataCurved;
    private int dataCurvedIndex;
    private OnSelectedListener listener;
    private List<String> data = new ArrayList<>();
    private int itemIndex;
    private Context mActivity;

    public SobotWheelPickerDialog(Context context, List<String> data, int itemIndex) {
        // 给Dialog的Window设置样式
        super(context, R.style.SobotWheelPickerDialogStyle);
        this.data.addAll(data);
        mActivity = context;
        this.itemIndex = itemIndex;

        // 修改Dialog(Window)的弹出位置
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        setParams(context, layoutParams);
        window.setAttributes(layoutParams);
    }

    private void setParams(Context context, WindowManager.LayoutParams lay) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        lay.width = dm.widthPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeAppLanguage();
        setContentView(R.layout.sobot_wheel_picker_dialog);
        initView();
        initData();
    }

    private void initData() {
        curvedPicker.setData(data);
        curvedPicker.setSelectedItemPosition(itemIndex, false);
        dataCurvedIndex = itemIndex;
        dataCurved = data.get(itemIndex);
        curvedPicker.setOnWheelChangeListener(new SobotWheelPicker.OnWheelChangeListener() {
            @Override
            public void onWheelScrolled(int offset) {

            }

            @Override
            public void onWheelSelected(int position) {
                dataCurvedIndex = position;
                dataCurved = data.get(position);
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
                if (state != SobotWheelPicker.SCROLL_STATE_IDLE) {
                    tv_right_text.setEnabled(false);
                } else {
                    tv_right_text.setEnabled(true);
                }
            }
        });
    }

    private void initView() {
        tv_left_text = (TextView) findViewById(R.id.tv_left_text);
        tv_right_text = (TextView) findViewById(R.id.tv_right_text);
        curvedPicker = (SobotWheelPicker) findViewById(R.id.main_wheel_curved);

        tv_left_text.setOnClickListener(this);
        tv_right_text.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        this.dismiss();
        int id = v.getId();
        if (id == R.id.tv_left_text) {
        } else if (id == R.id.tv_right_text) {
            if (listener != null) {
                listener.onSelected(dataCurvedIndex, dataCurved);
            }
        }
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnSelectedListener {
        void onSelected(int index, String str);
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
