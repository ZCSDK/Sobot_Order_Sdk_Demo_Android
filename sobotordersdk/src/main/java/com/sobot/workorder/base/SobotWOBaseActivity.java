package com.sobot.workorder.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

import com.sobot.common.login.SobotLoginTools;
import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.widget.ui.base.SobotBaseActivity;
import com.sobot.workorder.utils.SobotDialogUtils;
import com.sobot.workorderlibrary.api.SobotOrderService;
import com.sobot.workorderlibrary.api.SobotOrderServiceFactory;
import com.sobot.workorderlibrary.utils.SobotWOSPUtil;

import java.util.Locale;

/**
 * @Description: 工单sdk 基类 BaseActivity
 * @Author: znw
 * @CreateDate: 2022/03/20 11:05
 * @Version: 1.0
 */
public abstract class SobotWOBaseActivity extends SobotBaseActivity {

    public SobotOrderService zhiChiApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (zhiChiApi == null) {
            synchronized (SobotWOBaseActivity.class) {
                if (zhiChiApi == null) {
                    zhiChiApi = SobotOrderServiceFactory.createZhiChiApi(SobotWOBaseActivity.this);
                }
            }
        }
//        changeAppLanguage();
        super.onCreate(savedInstanceState);
        SobotGlobalContext.getInstance().addActivity(this);
        if (SobotLoginTools.getInstance().getLoginUser() == null) {
            SobotGlobalContext.getInstance().finishAllActivity();
        }
    }

    //国际化指定语言，放到setContentView()之前
    public void changeAppLanguage() {
        try {
            Locale locale = (Locale) SobotWOSPUtil.getInstance(SobotWOBaseActivity.this).get("SobotLanguage", Locale.class);
            if (locale != null) {
                Resources resources = getResources();
                DisplayMetrics metrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                //Android 7.0以上的方法
                if (Build.VERSION.SDK_INT >= 24) {
                    configuration.setLocale(locale);
                    configuration.setLocales(new LocaleList(locale));
                    createConfigurationContext(configuration);
                    //实测，updateConfiguration这个方法虽然很多博主说是版本不适用
                    //但是我的生产环境androidX+Android Q环境下，必须加上这一句，才可以通过重启App来切换语言
                    resources.updateConfiguration(configuration, metrics);

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    //Android 4.1 以上方法
                    configuration.setLocale(locale);
                    resources.updateConfiguration(configuration, metrics);
                } else {
                    configuration.locale = locale;
                    resources.updateConfiguration(configuration, metrics);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示加载中
     */
    public void showProgressDialog() {
        if (!isFinishing()) {
            SobotDialogUtils.startProgressDialog(this.getParent());
        }
    }

    public void dismissProgressDialog() {
        SobotDialogUtils.stopProgressDialog(this);
    }
}
