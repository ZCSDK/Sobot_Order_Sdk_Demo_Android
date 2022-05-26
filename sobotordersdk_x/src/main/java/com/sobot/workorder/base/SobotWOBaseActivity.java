package com.sobot.workorder.base;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.sobot.common.ui.base.SobotBaseActivity;
import com.sobot.workorder.utils.SobotDialogUtils;
import com.sobot.workorderlibrary.api.SobotOrderService;
import com.sobot.workorderlibrary.api.SobotOrderServiceFactory;

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
        super.onCreate(savedInstanceState);
    }
    /**
     * 显示加载中
     */
    public void showProgressDialog(){
        SobotDialogUtils.startProgressDialog(this);
    }
    public void dismissProgressDialog(){
        SobotDialogUtils.stopProgressDialog(this);
    }
}
