package com.sobot.workorder.weight;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sobot.workorder.R;
import com.sobot.workorder.weight.wheelpicker.SobotWheelPicker;

import java.util.ArrayList;
import java.util.List;


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

    public SobotWheelPickerDialog(Context context, List<String> data, int itemIndex) {
        // 给Dialog的Window设置样式
        super(context, R.style.SobotWheelPickerDialogStyle);
        this.data.addAll(data);
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
}
