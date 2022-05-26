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
import android.widget.Button;
import android.widget.LinearLayout;

import com.sobot.utils.SobotDensityUtil;
import com.sobot.workorder.R;


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

    public SobotSelectPicDialog(Activity context, View.OnClickListener itemsOnClick) {
        super(context, R.style.SobotClearHistoryDialogStyle);
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
}