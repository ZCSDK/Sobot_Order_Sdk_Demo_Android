package com.sobot.workorder.weight.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.workorder.R;

import java.text.DecimalFormat;

@SuppressLint("ValidFragment")
public class SobotCommonDialog extends DialogFragment
        implements
        View.OnClickListener {

    public SobotCommonDialog() {
    }

    /**
     * 内容部分的容器视图
     */
    private LinearLayout ll_content;
    /**
     * 标题文本视图
     */
    private TextView tv_title;
    /**
     * 提示信息文本视图
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
     * 进度条
     */
    private ProgressBar pb_progress;
    /**
     * 进度百分百
     */
    private TextView tv_per;
    /**
     * 进度最大值
     */
    private TextView tv_max;
    /**
     * 当前进度值
     */
    private TextView tv_progress;
    /**
     * 确定，取消间分隔线
     */
    private View v_line;
    /**
     * 进度最大值
     */
    private int mMaxProgress;
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
     * 格式化进度值
     */
    private DecimalFormat df = new DecimalFormat("0.00");
    /**
     * 显示类型 ,用于确定是否有标题、进度条和按钮的个数等
     */
    private int mShowType = 0;
    /**
     * 标题
     */
    private String mTitle;
    /**
     * 提示信息
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

    /**
     * 按钮点击回调接口
     *
     * @author ChaiHongwei
     */
    public interface OnBtnClickListener {
        void onClick();
    }

    /**
     * 构造一个进度条，提示的dialog
     *
     * @param msg 标题文本
     */
    public SobotCommonDialog(String msg) {
        mTitle = msg;
        mShowType = 0;
    }

    /**
     * 构造一个含有标题、进度提示和一个按钮的dialog
     *
     * @param title                 标题文本
     * @param onOneBtnClickListener
     */
    public SobotCommonDialog(String title, String oneBtnText,
                             OnBtnClickListener onOneBtnClickListener) {
        mTitle = title;
        mRightBtnText = oneBtnText;
        mRightBtnClickListener = onOneBtnClickListener;

        mShowType = 1;
    }

    /**
     * 构造一个不含标题，且保护提示文本和两个按钮的dialog
     *
     * @param message                 提示文本
     * @param leftBtnText             左边按钮的文本
     * @param onLeftBtnClickListener  左边按钮点击回调接口
     * @param rightBtnText            右边按钮的文本
     * @param onRightBtnClickListener 右边按钮点击回调接口
     */
    public SobotCommonDialog(String message, String leftBtnText,
                             OnBtnClickListener onLeftBtnClickListener, String rightBtnText,
                             OnBtnClickListener onRightBtnClickListener) {
        mMessage = message;
        mLeftBtnText = leftBtnText;
        mLeftBtnClickListener = onLeftBtnClickListener;
        mRightBtnText = rightBtnText;
        mRightBtnClickListener = onRightBtnClickListener;

        mShowType = 2;
    }

    /**
     * 构造一个包含标题、提示文本、两个按钮的dialog
     *
     * @param title                   标题文本
     * @param message                 提示文本
     * @param leftBtnText             左边按钮的文本
     * @param onLeftBtnClickListener  左边按钮点击回调接口
     * @param rightBtnText            右边按钮的文本
     * @param onRightBtnClickListener 右边按钮点击回调接口
     */
    public SobotCommonDialog(String title, String message, String leftBtnText,
                             OnBtnClickListener onLeftBtnClickListener, String rightBtnText,
                             OnBtnClickListener onRightBtnClickListener) {
        mTitle = title;
        mMessage = message;
        mLeftBtnText = leftBtnText;
        mLeftBtnClickListener = onLeftBtnClickListener;
        mRightBtnText = rightBtnText;
        mRightBtnClickListener = onRightBtnClickListener;

        mShowType = 3;
    }

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mCanceledOnTouchOutside = canceledOnTouchOutside;
    }

    public void setInterceptBackButton(boolean flag) {
        mInterceptBackButton = flag;
    }

    /**
     * 构造一个包含包含标题、提示文本、一个按钮的dialog
     *
     * @param title                 标题文本
     * @param message               提示文本
     * @param oneBtnText            唯一一个按钮的文本
     * @param onOneBtnClickListener 按钮点击回调接口
     */
    public SobotCommonDialog(String title, String message, String oneBtnText,
                             OnBtnClickListener onOneBtnClickListener) {
        mTitle = title;
        mMessage = message;
        mRightBtnText = oneBtnText;
        mRightBtnClickListener = onOneBtnClickListener;

        mShowType = 4;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sobot_dialog_common, container,
                false);

        initView(view);
        initEvent();

        if (mShowType == 0) {
            setLoadingBar();
        } else if (mShowType == 1) {
            ll_content.setVisibility(View.GONE);
            setTitle(mTitle).setRightBtnText(mRightBtnText);
        } else if (mShowType == 2) {
            setMessage(mMessage).setLeftBtnText(mLeftBtnText).setRightBtnText(
                    mRightBtnText).showLine();
        } else if (mShowType == 3) {
            setTitle(mTitle).setMessage(mMessage).setLeftBtnText(mLeftBtnText)
                    .setRightBtnText(mRightBtnText).showLine();
        } else if (mShowType == 4) {
            setTitle(mTitle).setMessage(mMessage)
                    .setRightBtnText(mRightBtnText);
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
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_message = (TextView) view.findViewById(R.id.tv_message);

        btn_left = (Button) view.findViewById(R.id.btn_left);
        btn_right = (Button) view.findViewById(R.id.btn_right);

        ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
        v_line = view.findViewById(R.id.v_line);
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
        if (id == R.id.btn_left) {
            if (mLeftBtnClickListener != null) {
                mLeftBtnClickListener.onClick();
            }
        } else if (id == R.id.btn_right) {
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
    public SobotCommonDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }

        return this;
    }

    /**
     * 设置提示内容信息
     *
     * @param message 提示内容字符串
     * @return 返回当前对象
     */
    public SobotCommonDialog setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText(message);
        } else {
            tv_message.setVisibility(View.GONE);
        }

        return this;
    }

    /**
     * 设置提示内容左边的图标
     *
     * @param resId 图标资源Id,负数则清除左侧图标
     * @return 返回当前对象
     */
    public SobotCommonDialog setMessageIcon(int resId) {
        if (resId < 0) {
            tv_message.setCompoundDrawables(null, null, null, null);
        } else {
            Drawable img = getActivity().getResources().getDrawable(resId);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            tv_message.setCompoundDrawables(img, null, null, null);
        }

        return this;
    }

    /**
     * 设置加载中dialog
     *
     * @return 返回当前对象
     */
    public SobotCommonDialog setLoadingBar() {
        return setLoadingBar(R.layout.sobot_dialog_progress);
    }

    ImageView iv;
    TextView id_tv_loadingmsg;

    @SuppressLint("WrongViewCast")
    public SobotCommonDialog setLoadingBar(int resource) {
        View v = LayoutInflater.from(getActivity()).inflate(resource, null);
        iv = (ImageView) v.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv.getBackground();
        animationDrawable.start();
        id_tv_loadingmsg = (TextView) v.findViewById(R.id.id_tv_loadingmsg);
        if (ll_content.getChildCount() > 0) {
            ll_content.removeAllViews();
        }
        ll_content.setVisibility(View.VISIBLE);
        ll_content.addView(v);

        return this;
    }

    /**
     * 设置带进度提示dialog
     *
     * @return 返回当前对象
     */
    public SobotCommonDialog setProgressBar() {
        return setProgressBar(R.layout.sobot_layout_progressbar);
    }

    /**
     * 设置带进度提示的dialog
     *
     * @param resource 进度布局资源,布局中一定要带有id为pb_progress的ProgressBar,tv_per,tv_max,
     *                 tv_progress三个TextView
     * @return 返回当前dialog对象
     */
    public SobotCommonDialog setProgressBar(int resource) {
        View v = LayoutInflater.from(getActivity()).inflate(resource, null);
        pb_progress = (ProgressBar) v.findViewById(R.id.pb_progress);
        tv_per = (TextView) v.findViewById(R.id.tv_per);
        tv_max = (TextView) v.findViewById(R.id.tv_max);
        tv_progress = (TextView) v.findViewById(R.id.tv_progress);

        if (ll_content.getChildCount() > 0) {
            ll_content.removeAllViews();
        }
        ll_content.setVisibility(View.VISIBLE);
        ll_content.addView(v);

        return this;
    }

    /**
     * 设置进度最大值
     *
     * @param max 进度最大值
     * @return 返回当前对象
     */
    public SobotCommonDialog setMax(int max) {
        Message msg = Message.obtain();
        msg.what = 1;
        msg.arg1 = max;
        handler.sendMessage(msg);

        return this;
    }

    /**
     * 设置进度值
     *
     * @param progress 当前进度值
     * @return 返回当前对象
     */
    public SobotCommonDialog setProgress(int progress) {
        Message msg = Message.obtain();
        msg.what = 2;
        msg.arg1 = progress;
        handler.sendMessage(msg);

        return this;
    }

    public SobotCommonDialog showLine() {
        v_line.setVisibility(View.VISIBLE);
        return this;
    }

    // 处理进度提示的handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int max = msg.arg1;
                    if (pb_progress != null && max > 0) {
                        pb_progress.setMax(max);

                        tv_max.setText("/" + df.format(max * 1.0 / 1024 / 1024)
                                + "M");
                        mMaxProgress = max;
                    }
                    break;

                case 2:
                    int progress = msg.arg1;
                    if (pb_progress != null && progress > 0) {
                        pb_progress.setProgress(progress);

                        tv_progress.setText(df
                                .format(progress * 1.0 / 1024 / 1024));

                        tv_per.setText((int) (progress * 1.0 / mMaxProgress * 100)
                                + "%");
                    }
                    break;
            }
        }
    };

    /**
     * 设置自定义内容视图
     *
     * @param v 自定义View
     * @return 返回当前对象
     */
    public SobotCommonDialog setContentView(View v) {
        if (ll_content.getChildCount() > 0) {
            ll_content.removeAllViews();
        }
        ll_content.setVisibility(View.VISIBLE);
        ll_content.addView(v);

        return this;
    }

    /**
     * 设置自定义内容视图
     *
     * @param v      自定义View
     * @param params 布局参数对象,可用于定位view
     * @return 返回当前对象
     */
    public SobotCommonDialog setContentView(View v,
                                            LinearLayout.LayoutParams params) {
        if (ll_content.getChildCount() > 0) {
            ll_content.removeAllViews();
        }
        ll_content.setVisibility(View.VISIBLE);
        ll_content.addView(v, params);

        return this;
    }

    /**
     * 设置左边按钮文本
     *
     * @param text 按钮文本
     * @return 返回当前对象
     */
    public SobotCommonDialog setLeftBtnText(String text) {
        if (!TextUtils.isEmpty(text)) {
            btn_left.setVisibility(View.VISIBLE);

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
    public SobotCommonDialog setRightBtnText(String text) {
        if (!TextUtils.isEmpty(text)) {
            btn_right.setVisibility(View.VISIBLE);

            btn_right.setText(text);
        } else {
            btn_right.setVisibility(View.GONE);
        }

        return this;
    }
}
