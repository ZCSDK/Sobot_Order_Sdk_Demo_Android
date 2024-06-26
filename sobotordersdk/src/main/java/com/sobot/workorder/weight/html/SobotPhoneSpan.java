package com.sobot.workorder.weight.html;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.sobot.widget.ui.toast.SobotToastUtil;
import com.sobot.workorder.R;


public class SobotPhoneSpan extends ClickableSpan {

    private String phone;
    private int color;
    private Context context;

    public SobotPhoneSpan(Context context, String phone, int color) {
        this.phone = phone;
        this.color = context.getResources().getColor(color);
        this.context = context;
    }

    @Override
    public void onClick(View widget) {
        if (SobotOption.newHyperlinkListener != null) {
            boolean isIntercept = SobotOption.newHyperlinkListener.onPhoneClick(context, "tel:" + phone);
            if (isIntercept) {
                return;
            }
        }
        callUp(phone, context);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false); // 去掉下划线
    }

    public static void callUp(String phone, Context context) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + phone));// mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
            context.startActivity(intent);
        } catch (Exception e) {
            SobotToastUtil.showCustomToast(context, context.getString(R.string.sobot_no_support_call));
            e.printStackTrace();
        }
    }

}