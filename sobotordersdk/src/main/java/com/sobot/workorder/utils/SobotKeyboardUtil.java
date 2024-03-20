package com.sobot.workorder.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class SobotKeyboardUtil {

    public static void showKeyboard(final View view) {
        if (view != null && view.getContext() != null) {
            view.requestFocus();
            InputMethodManager inputManager =
                    (InputMethodManager) view.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, 0);
        }
    }

    /**
     * Hide the soft input.
     *
     * @param activity The activity.
     */
    public static void hideSoftInput(@NonNull final Activity activity) {
        hideSoftInput(activity.getWindow());
    }

    /**
     * Hide the soft input.
     *
     * @param window The window.
     */
    public static void hideSoftInput(@NonNull final Window window) {
        View view = window.getCurrentFocus();
        if (view == null) {
            View decorView = window.getDecorView();
            View focusView = decorView.findViewWithTag("keyboardTagView");
            if (focusView == null) {
                view = new EditText(window.getContext());
                view.setTag("keyboardTagView");
                ((ViewGroup) decorView).addView(view, 0, 0);
            } else {
                view = focusView;
            }
            view.requestFocus();
        }
        hideKeyboard(view);
    }

    public static void hideKeyboard(final View view) {
        if (view == null) {
            return;
        }
        if (view != null && view.getContext() != null) {
            InputMethodManager imm =
                    (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}