package com.sobot.workorder.weight.dialog;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.workorder.weight.toast.SobotCustomToast;

/**
 * @author: Sobot
 * 2022/3/30
 */
public class SobotCommonUtils {

    public static void showPopWindows(final Context mContext, final View viewById, final String str, int x, int y, final CopyListener copyListener, final PopupWindow.OnDismissListener dismissListener) {

        x = dip2px(mContext, x);
        y = dip2px(mContext, y);
        // pop view
        View mPopView = LayoutInflater.from(mContext).inflate(SobotResourceUtils.getResLayoutId(mContext, "sobot_wo_pop_long_press"), null);
        final PopupWindow mPopWindow = new PopupWindow(mPopView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        if (dismissListener != null) {
            mPopWindow.setOnDismissListener(dismissListener);
        }
        // set
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //这个很重要 ,获取弹窗的长宽度
        mPopView.measure(150, 150);
        int popupWidth = mPopView.getMeasuredWidth();
        int popupHeight = mPopView.getMeasuredHeight() + 20;
        // 获取父控件的位置 */
        int[] location = new int[2];
        viewById.getLocationOnScreen(location);
        //* 显示位置 */
        mPopWindow.showAtLocation(viewById, Gravity.NO_GRAVITY,
                location[0] + viewById.getWidth() / 2 - popupWidth / 2 + x,
                location[1] - popupHeight + y);
        mPopWindow.update();

        mPopView.findViewById(SobotResourceUtils.getResId(mContext, "sobot_tv_copy_txt")).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (copyListener != null) {
                            copyListener.onCopy();
                        }

                        ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cmb != null) {
                            cmb.setText(str);
                            cmb.getText();
                        }

                        SobotCustomToast.makeText(mContext, "复制成功！", Toast.LENGTH_SHORT, SobotResourceUtils.getDrawableId(mContext, "sobot_iv_login_right")).show();
                        mPopWindow.dismiss();
                    }
                });
    }

    /**
     * 将dp单位的值转换为px为单位的值
     *
     * @param context  上下文对象
     * @param dipValue dp为单位的值
     * @return 返回转换后的px为单位的值
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5F);
    }

    public interface CopyListener {
        void onCopy();
    }
}
