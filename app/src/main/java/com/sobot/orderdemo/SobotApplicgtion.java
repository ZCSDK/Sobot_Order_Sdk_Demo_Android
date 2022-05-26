package com.sobot.orderdemo;

import android.app.Application;

import com.sobot.common.ui.SobotMarkConfig;
import com.sobot.common.utils.SobotCommonApi;
import com.sobot.workorder.SobotOrderApi;

public class SobotApplicgtion extends Application {
    public static SobotApplicgtion applicgtion;

    @Override
    public void onCreate() {
        super.onCreate();
        applicgtion=this;
        //是否显示智齿日志
        SobotOrderApi.setShowDebug(true);
        //设置是否横屏
        SobotCommonApi.setSwitchMarkStatus(SobotMarkConfig.LANDSCAPE_SCREEN, false);
        //设置横屏下刘海屏和水滴屏是否显示
        SobotCommonApi.setSwitchMarkStatus(SobotMarkConfig.DISPLAY_INNOTCH, false);
    }

    public SobotApplicgtion getApplicgtion() {
        return applicgtion;
    }

    public void setApplicgtion(SobotApplicgtion applicgtion) {
        this.applicgtion = applicgtion;
    }
}
