package com.sobot.workorder.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sobot.workorder.R;

/**
 * 带switch按钮的item
 * Created by jinxl on 2017/4/14.
 */
public class SobotCustomSwitchButton extends RelativeLayout {

    //表示是否打开
    private boolean isOpen = false;
    //控件显示内容
    private String content = "";

    private TextView leftTextView;
    private SobotSwitchButton sb_ios;

    private CustomSwitchButtonClickListener onClickListener;

    public SobotCustomSwitchButton(Context context) {
        this(context, null);
    }

    public SobotCustomSwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SobotCustomSwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SobotCustomSwitchButton);
//        系统会在自定义属性前加上它所属的declare-styleable 的name_
        content = array.getString(R.styleable.SobotCustomSwitchButton_csb_content);
        isOpen = array.getBoolean(R.styleable.SobotCustomSwitchButton_csb_selected, false);
        array.recycle();//回收*/
        initView();
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    public boolean switchState() {
        setSelected(!isOpen);
        return this.isOpen;
    }

    public void setSelected(boolean isOpen) {
        this.isOpen = isOpen;
        if (sb_ios != null) {
            sb_ios.setChecked(isOpen);
        }
    }

    public void setContent(String str) {
        leftTextView.setText(str);
    }

    private void initView() {
        View view = null;
        view = View.inflate(getContext(), R.layout.sobot_layout_switch_item, null);
        leftTextView = (TextView) view.findViewById(R.id.tv_leftTextView);
        sb_ios = (SobotSwitchButton) view.findViewById(R.id.sb_ios);
        setContent(content);
        setSelected(isOpen);
        sb_ios.setOnCheckedChangeListener(new SobotSwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SobotSwitchButton view, boolean isChecked) {
                SobotCustomSwitchButton.this.isOpen = isChecked;
                if (onClickListener != null) {
                    onClickListener.onSwitchChanged(view, isChecked);
                }
            }
        });
        LayoutParams param = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(view, param);
    }

    public void setTextViewLeftDrawable(int resId, TextView view) {
        Drawable drawable = getResources().getDrawable(resId);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        view.setCompoundDrawables(drawable, null, null, null);
    }

    public void setSwitchListner(CustomSwitchButtonClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface CustomSwitchButtonClickListener {
        void onSwitchChanged(View v, boolean isSelected);
    }
}
