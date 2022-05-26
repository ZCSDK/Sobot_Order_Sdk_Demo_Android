package com.sobot.workorder.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * 软件盘的管理
 */
public class SobotSoftInputManage {
    /**
     * //此方法只是打开软键盘
     *
     * @param act activity
     */
    public static void openInputMode(Activity act) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && act.getCurrentFocus() != null) {
            imm.showSoftInputFromInputMethod(act.getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * //此方法只是关闭软键盘
     *
     * @param act activity
     */
    public static void hideInputMode(Activity act) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive() && act.getCurrentFocus() != null) {
            if (act.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * //此方法，如果显示则隐藏，如果隐藏则显示
     *
     * @param act activity
     */
    public static void toggleInputMode(Activity act) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm != null && imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}