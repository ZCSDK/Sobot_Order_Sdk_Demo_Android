package com.sobot.workorder.base;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.sobot.workorderlibrary.api.SobotOrderService;
import com.sobot.workorderlibrary.api.SobotOrderServiceFactory;

public class SobotWOBaseFragment extends SobotBaseFragment {
    public SobotOrderService zhiChiApi;
    public boolean needRefresh=false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (zhiChiApi == null) {
            synchronized (SobotWOBaseActivity.class) {
                if (zhiChiApi == null) {
                    zhiChiApi = SobotOrderServiceFactory.createZhiChiApi(getSobotActivity());
                }
            }
        }
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }
}
