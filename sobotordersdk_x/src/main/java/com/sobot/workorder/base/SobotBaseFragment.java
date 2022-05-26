package com.sobot.workorder.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sobot.common.ui.SobotMarkConfig;
import com.sobot.common.ui.notchlib.SobotINotchScreen;
import com.sobot.common.ui.notchlib.SobotNotchScreenManager;
import com.sobot.common.ui.permission.SobotPermissionListener;
import com.sobot.common.utils.SobotCommonApi;
import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.SobotOkHttpUtils;

import java.io.File;

/**
 * @author Created by jinxl on 2018/2/1.
 */
public abstract class SobotBaseFragment extends Fragment {

    protected File cameraFile;

    private Activity activity;
    //权限回调
    public SobotPermissionListener permissionListener;

    public SobotBaseFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = (Activity) context;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SobotCommonApi.getSwitchMarkStatus(SobotMarkConfig.DISPLAY_INNOTCH) && SobotCommonApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN)) {
            // 支持显示到刘海区域
            SobotNotchScreenManager.getInstance().setDisplayInNotch(getActivity());
            // 设置Activity全屏
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void displayInNotch(final View view) {
        if (SobotCommonApi.getSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN) && SobotCommonApi.getSwitchMarkStatus(SobotMarkConfig.DISPLAY_INNOTCH) && view != null) {
            // 获取刘海屏信息
            SobotNotchScreenManager.getInstance().getNotchInfo(getActivity(), new SobotINotchScreen.NotchScreenCallback() {
                @Override
                public void onResult(SobotINotchScreen.NotchScreenInfo notchScreenInfo) {
                    if (notchScreenInfo.hasNotch) {
                        for (Rect rect : notchScreenInfo.notchRects) {
                            if (view instanceof WebView && view.getParent() instanceof LinearLayout) {
                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.leftMargin = rect.right + 14;
                                view.setLayoutParams(layoutParams);
                            } else if (view instanceof WebView && view.getParent() instanceof RelativeLayout) {
                                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                                layoutParams.leftMargin = rect.right + 14;
                                view.setLayoutParams(layoutParams);
                            } else {
                                view.setPadding(rect.right, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
                            }
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SobotOkHttpUtils.getInstance().cancelTag(SobotBaseFragment.this);
    }

    public SobotBaseFragment getSobotBaseFragment() {
        return this;
    }

    public int getResId(String name) {
        return SobotResourceUtils.getIdByName(getSobotActivity(), "id", name);
    }

    public int getResDrawableId(String name) {
        return SobotResourceUtils.getIdByName(getSobotActivity(), "drawable", name);
    }

    public int getResLayoutId(String name) {
        return SobotResourceUtils.getIdByName(getSobotActivity(), "layout", name);
    }

    public int getResStringId(String name) {
        return SobotResourceUtils.getIdByName(getSobotActivity(), "string", name);
    }

    public int getResDimenId(String name) {
        return SobotResourceUtils.getIdByName(getSobotActivity(), "dimen", name);
    }

    public String getResString(String name) {
        return SobotResourceUtils.getResString(getSobotActivity(), name);
//        return getResources().getString(getResStringId(name));
    }

    public float getDimens(String name) {
        return getResources().getDimension(getResDimenId(name));
    }


    /**
     * 返回activity
     *
     * @return
     */
    public Activity getSobotActivity() {
        Activity activity = getActivity();
        if (activity == null) {
            return this.activity;
        }
        return activity;

    }

}