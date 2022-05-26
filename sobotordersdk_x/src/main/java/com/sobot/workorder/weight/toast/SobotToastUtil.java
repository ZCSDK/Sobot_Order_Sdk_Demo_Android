package com.sobot.workorder.weight.toast;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.utils.SobotLogUtils;

import java.util.Timer;
import java.util.TimerTask;

public class SobotToastUtil {

    private static Toast toast;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof OnAfterShowListener) {
                OnAfterShowListener listener = (OnAfterShowListener) msg.obj;
                listener.doAfter();
            }
        }
    };

    /**
     * 自定义的toast
     *
     * @param context
     * @param str
     */
    public static void showCustomToast(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            SobotLogUtils.i("toast 内容 不能为空");
            return;
        }
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义停留长时间的toast
     *
     * @param context
     * @param str
     */
    public static void showCustomLongToast(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            SobotLogUtils.i("toast 内容 不能为空");
            return;
        }
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 自定义的带图片的toast
     *
     * @param context
     * @param str
     * @param resId
     */
    public static void showCustomToast(Context context, String str, int resId) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT, resId).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义停留长时间的带图片的toast
     *
     * @param context
     * @param str
     * @param resId
     */
    public static void showCustomLongToast(Context context, String str, int resId) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT, resId).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 自定义toast，显示固定时间,然后执行监听方法
     *
     * @param context
     * @param str
     * @param onAfterShowListener
     */
    public static void showCustomToastWithListenr(Context context, String str, final OnAfterShowListener onAfterShowListener) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (onAfterShowListener != null)
                        doListener(onAfterShowListener);
                }
            }, 1000);//延时执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义带图标的toast，显示固定时间,然后执行监听方法
     *
     * @param context
     * @param str
     * @param resId
     * @param onAfterShowListener
     */
    public static void showCustomToastWithListenr(Context context, String str, int resId, final OnAfterShowListener onAfterShowListener) {
        try {
            SobotCustomToast.makeText(context, str, Toast.LENGTH_SHORT, resId).show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (onAfterShowListener != null)
                        doListener(onAfterShowListener);
                }
            }, 1500);//延时执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doListener(OnAfterShowListener onAfterShowListener) {
        Message message = mHandler.obtainMessage();
        message.obj = onAfterShowListener;
        mHandler.sendMessage(message);
    }

    /**
     * 显示复制的提示
     *
     * @param context
     * @param v
     * @param str
     * @param x
     * @param y
     */
    public static void showCopyPopWindows(final Context context, View v, final String str, int x, int y) {
        /** pop view */
        View mPopView = LayoutInflater.from(context).inflate(SobotResourceUtils.getIdByName(context, "layout", "sobot_pop_chat_room_long_press"), null);

        final PopupWindow mPopWindow = new PopupWindow(mPopView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        /** set */
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /** 这个很重要 ,获取弹窗的长宽度 */
        mPopView.measure(150, 150);
        int popupWidth = mPopView.getMeasuredWidth();
        int popupHeight = mPopView.getMeasuredHeight() + 20;
        /** 获取父控件的位置 */
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        /** 显示位置 */
        mPopWindow.showAtLocation(v, Gravity.NO_GRAVITY,
                location[0] + v.getWidth() / 2 - popupWidth / 2 + x,
                location[1] - popupHeight + y);// + v.getWidth() / 2) -

        mPopWindow.update();
        ((TextView) mPopView.findViewById(SobotResourceUtils.getResId(context, "sobot_tv_copy_txt"))).setText(SobotResourceUtils.getResString(context, "sobot_ctrl_copy"));
        mPopView.findViewById(SobotResourceUtils.getResId(context, "sobot_tv_copy_txt")).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= 11) {
                            SobotLogUtils.i("API是大于11");
                            android.content.ClipboardManager cmb = (android.content.ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cmb != null) {
                                cmb.setText(str);
                            }
                        } else {
                            SobotLogUtils.i("API是小于11");
                            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context.getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cmb != null) {
                                cmb.setText(str);
                            }
                        }

                        com.sobot.workorder.weight.toast.SobotToastUtil.showCustomToast(context, SobotResourceUtils.getResString(context, "sobot_ctrl_v_success"), SobotResourceUtils.getDrawableId(context, "sobot_iv_login_right"));
                        mPopWindow.dismiss();
                    }
                });
    }


    public interface OnAfterShowListener {
        void doAfter();
    }
}