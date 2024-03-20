package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sobot.common.login.SobotLoginTools;
import com.sobot.common.login.callback.SobotResultBlock;
import com.sobot.common.login.callback.SobotResultCode;
import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.ui.toast.SobotToastUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.SobotOrderApi;
import com.sobot.workorder.base.SobotWOBaseActivity;

public class SobotKickedActivity extends SobotWOBaseActivity implements View.OnClickListener {

    Button btnCancel;
    Button btnOk;
    TextView tvHintContent;
    View vLine;
    LinearLayout llBottom;
    RelativeLayout llRealContent;

    String loginUser;
    String loginPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //只能在super.onCreate后边清理（因为base里边有判断会直接finishAllActivity）
        SobotLoginTools.getInstance().clearLoginInfo();
        setFinishOnTouchOutside(false);//点击空白处，窗体不关闭
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_kicked;
    }

    @Override
    protected void initView() {
        btnCancel = findViewById(R.id.btn_cancel);
        btnOk = findViewById(R.id.btn_ok);
        tvHintContent = findViewById(R.id.tv_hint_content);
        vLine = findViewById(R.id.v_line);
        llBottom = findViewById(R.id.ll_bottom);
        llRealContent = findViewById(R.id.ll_real_content);
        Intent intent = getIntent();
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        loginUser = getIntent().getStringExtra("loginUser");
        loginPwd = getIntent().getStringExtra("loginPwd");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            llRealContent.setVisibility(View.GONE);
            SobotGlobalContext.getInstance(getSobotBaseActivity()).finishAllActivity();
        } else if (id == R.id.btn_ok) {
            if (SobotStringUtils.isNoEmpty(loginUser) && SobotStringUtils.isNoEmpty(loginPwd)) {
                SobotOrderApi.loginUser(getSobotBaseActivity(), loginUser, loginPwd, false, new SobotResultBlock() {
                    @Override
                    public void resultBolok(SobotResultCode code, String msg, Object obj) {
                        if (code == SobotResultCode.CODE_SUCCEEDED) {
                            finish();
                        }else {
                            SobotToastUtil.showCustomToast(getSobotBaseActivity(),SobotStringUtils.checkStringIsNull(msg));
                        }
                    }
                });
            } else {
                SobotGlobalContext.getInstance(getSobotBaseActivity()).finishAllActivity();
            }
        }
    }
}

