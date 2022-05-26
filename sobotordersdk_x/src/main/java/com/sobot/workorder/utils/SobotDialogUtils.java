package com.sobot.workorder.utils;

import android.app.Activity;
import android.content.Context;

import com.sobot.workorder.weight.dialog.SobotLoadingDialog;

public class SobotDialogUtils {

    public static SobotLoadingDialog progressDialog;

    public static void startProgressDialog(Context context) {
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context);
        } else {
            SobotLoadingDialog.setText(progressDialog, "");
        }
        progressDialog.show();
    }

    public static void startProgressDialog(Context context, String str) {
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context, str);

        } else {
            SobotLoadingDialog.setText(progressDialog, "");
        }
        progressDialog.show();
    }

    public static void stopProgressDialog(Context context) {
        if (progressDialog != null && context != null && progressDialog.isShowing()) {
            Activity act = (Activity) context;
            if (!act.isFinishing()) {
                progressDialog.dismiss();
            }
        }
        progressDialog = null;
    }


}
