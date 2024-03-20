package com.sobot.workorder.weight.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sobot.workorder.R;


@SuppressLint("ValidFragment")
public class SobotChoiceUserDialog extends DialogFragment
        implements
        View.OnClickListener {

    public SobotChoiceUserDialog() {
    }

    /**
     * 标题文本视图
     */
    private TextView tv_title;
    /**
     * 标题文本视图
     */
    private TextView tv_message;
    /**
     * 左边按钮
     */
    private Button btn_left;
    /**
     * 右边按钮
     */
    private Button btn_right;
    /**
     * 确定，取消间分隔线
     */
    private View v_line;
    /**
     * 左边按钮的点击回调事件
     */
    private OnBtnClickListener mLeftBtnClickListener;
    /**
     * 右边按钮的点击回调事件
     */
    private OnBtnClickListener mRightBtnClickListener;
    /**
     * 点击dialog的区域外dialog是否消失
     */
    private boolean mCanceledOnTouchOutside = true;
    private boolean mInterceptBackButton = false;
    /**
     * 标题
     */
    private String mTitle;
    /**
     * 标题
     */
    private String mMessage;
    /**
     * 左边按钮的文本
     */
    private String mLeftBtnText;
    /**
     * 右边按钮的文本
     */
    private String mRightBtnText;

    private String mColor;

    /**
     * 按钮点击回调接口
     *
     * @author ChaiHongwei
     */
    public interface OnBtnClickListener {
        void onClick();
    }

    /**
     * 构造一个包含标题、提示文本、两个按钮的dialog
     *
     * @param title                   标题文本
     * @param leftBtnText             左边按钮的文本
     * @param onLeftBtnClickListener  左边按钮点击回调接口
     * @param rightBtnText            右边按钮的文本
     * @param onRightBtnClickListener 右边按钮点击回调接口
     */
    public SobotChoiceUserDialog(String title, String leftBtnText, String message,
                                 OnBtnClickListener onLeftBtnClickListener, String rightBtnText,
                                 OnBtnClickListener onRightBtnClickListener, String color) {
        mTitle = title;
        mMessage = message;
        mLeftBtnText = leftBtnText;
        mLeftBtnClickListener = onLeftBtnClickListener;
        mRightBtnText = rightBtnText;
        mRightBtnClickListener = onRightBtnClickListener;
        mColor = color;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mCanceledOnTouchOutside = canceledOnTouchOutside;
    }

    public void setInterceptBackButton(boolean flag) {
        mInterceptBackButton = flag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sobot_dialog_choice_user, container, false);

        initView(view);
        initEvent();
        setTitle(mTitle).setLeftBtnText(mLeftBtnText).setMessage(mMessage)
                .setRightBtnText(mRightBtnText).showLine();

        if (!TextUtils.isEmpty(mColor)) {
            btn_right.setTextColor(Color.parseColor(mColor));
        }
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        if (mInterceptBackButton) {
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int keycode, KeyEvent keyEvent) {
                    if (keycode == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
        }

        Window dialogwWindow = dialog.getWindow();
        if (dialogwWindow != null) {
            dialogwWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        Fragment fragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();
        if (fragment != null && fragment.isAdded()) {
            transaction.hide(fragment).show(fragment).commitAllowingStateLoss();
        } else {
//			super.show(manager, tag);
            transaction.add(this, tag);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * 初始化视图
     */
    private void initView(View view) {
        tv_title = (TextView) view.findViewById(R.id.choice_user_tv_title);
        tv_message = (TextView) view.findViewById(R.id.choice_user_tv_message);
        btn_left = (Button) view.findViewById(R.id.choice_user_btn_left);
        btn_right = (Button) view.findViewById(R.id.choice_user_btn_right);
        v_line = view.findViewById(R.id.choice_user_v_line);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismissAllowingStateLoss();
        int id = v.getId();
        if (id == R.id.choice_user_btn_left) {
            if (mLeftBtnClickListener != null) {
                mLeftBtnClickListener.onClick();
            }
        } else if (id == R.id.choice_user_btn_right) {
            if (mRightBtnClickListener != null) {
                mRightBtnClickListener.onClick();
            }
        }
    }

    /**
     * 设置标题
     *
     * @param title 标题文本字符串
     * @return 返回当前对象
     */
    public SobotChoiceUserDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }

        return this;
    }

    public SobotChoiceUserDialog showLine() {
        v_line.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 设置左边按钮文本
     *
     * @param text 按钮文本
     * @return 返回当前对象
     */
    public SobotChoiceUserDialog setLeftBtnText(String text) {
        if (!TextUtils.isEmpty(text)) {
            btn_left.setVisibility(View.VISIBLE);
            btn_left.setTextColor(getResources().getColor(R.color.sobot_wo_theme_color));
            btn_left.setText(text);
        } else {
            btn_left.setVisibility(View.GONE);
        }

        return this;
    }

    /**
     * 设置右边按钮文本
     *
     * @param text 按钮文本
     * @return 返回当前对象
     */
    public SobotChoiceUserDialog setRightBtnText(String text) {
        if (!TextUtils.isEmpty(text)) {
            btn_right.setVisibility(View.VISIBLE);

            btn_right.setText(text);
        } else {
            btn_right.setVisibility(View.GONE);
        }

        return this;
    }

    /**
     * 设置提示内容信息
     *
     * @param message 提示内容字符串
     * @return 返回当前对象
     */
    public SobotChoiceUserDialog setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText(message);
        } else {
            tv_message.setVisibility(View.GONE);
        }
        return this;
    }
}