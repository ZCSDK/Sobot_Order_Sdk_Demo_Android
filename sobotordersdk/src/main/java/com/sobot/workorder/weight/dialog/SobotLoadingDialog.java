package com.sobot.workorder.weight.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.sobot.workorder.R;


public class SobotLoadingDialog extends Dialog {

    public SobotLoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public static SobotLoadingDialog createDialog(final Context context) {
        SobotLoadingDialog customProgressDialog = new SobotLoadingDialog(context, R.style.SobotDialogProgress);
        customProgressDialog.setContentView(R.layout.sobot_dialog_progress);
        if (customProgressDialog.getWindow() != null) {
            customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        }
        customProgressDialog.setCanceledOnTouchOutside(false);
        customProgressDialog.setCancelable(true);
        return customProgressDialog;
    }

    public static SobotLoadingDialog createDialog(final Context context, String str) {
        SobotLoadingDialog customProgressDialog = new SobotLoadingDialog(context, R.style.SobotDialogProgress);
        customProgressDialog.setContentView(R.layout.sobot_dialog_progress);
        if (customProgressDialog.getWindow() != null) {
            customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        }
        customProgressDialog.setCanceledOnTouchOutside(false);
        customProgressDialog.setCancelable(true);
        if (!TextUtils.isEmpty(str)) {
            TextView textView = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
            textView.setText(str);
        }
        return customProgressDialog;
    }

    public static void setText(SobotLoadingDialog customProgressDialog, String str) {
        TextView textView = (TextView) customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
        if (!TextUtils.isEmpty(str)) {
            textView.setText(str);
        }
    }

}
