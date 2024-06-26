package com.sobot.workorder.weight.timePicker.utils;

import android.content.Context;
import android.view.Gravity;

import com.sobot.workorder.R;


/**
 * 动画
 */
public class SobotPickerViewAnimateUtil {
    private static final int INVALID = -1;

    /**
     * Get default animation resource when not defined by the user
     *
     * @param gravity       the gravity of the dialog
     * @param isInAnimation determine if is in or out animation. true when is is
     * @return the id of the animation resource
     */
    public static int getAnimationResource(Context context, int gravity, boolean isInAnimation) {
        switch (gravity) {
            case Gravity.BOTTOM:
                return isInAnimation ? R.anim.sobot_pickerview_slide_in_bottom : R.anim.sobot_pickerview_slide_out_bottom;
        }
        return INVALID;
    }
}
