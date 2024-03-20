package com.sobot.workorder.utils;

import android.app.Activity;
import android.content.Context;

import com.sobot.utils.SobotLogUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.weight.dialog.SobotLoadingDialog;

public class SobotDialogUtils {

    public static SobotLoadingDialog progressDialog;

    public static void startProgressDialog(Context context) {
        SobotLogUtils.i("上传成功----startProgressDialog1：");
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context);
        } else {
            SobotLoadingDialog.setText(progressDialog, context.getResources().getString(R.string.sobot_please_wait_a_moment_string));
        }
        progressDialog.show();
    }

    public static void startProgressDialog(Context context, String str) {
        SobotLogUtils.i("上传成功----startProgressDialog2：");
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context, str);

        } else {
            SobotLoadingDialog.setText(progressDialog, context.getResources().getString(R.string.sobot_please_wait_a_moment_string));
        }
        progressDialog.show();
    }

    public static void stopProgressDialog(Context context) {
        SobotLogUtils.i("上传成功----stopProgressDialog：");
        if (progressDialog != null && context != null && progressDialog.isShowing()) {
            Activity act = (Activity) context;
            if (!act.isFinishing()) {
                progressDialog.dismiss();
            }
        }
        progressDialog = null;
    }


}
