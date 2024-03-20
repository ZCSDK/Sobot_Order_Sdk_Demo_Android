package com.sobot.orderdemo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sobot.common.login.callback.SobotResultBlock;
import com.sobot.common.login.callback.SobotResultCode;
import com.sobot.workorder.SobotOrderApi;
import com.sobot.workorder.weight.toast.SobotToastUtil;


//工单 sdk demo 启动
public class SobotStartActivity extends Activity implements View.OnClickListener {
    private TextView ac_demo_start_tv;
    private EditText account;
    private EditText password;
    private EditText token;
    private EditText host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_sobot_app_start);
        ac_demo_start_tv = findViewById(R.id.ac_demo_start_tv);
        ac_demo_start_tv.setOnClickListener(this);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        host = findViewById(R.id.host);
        token = findViewById(R.id.token);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_demo_start_tv:
                if (TextUtils.isEmpty(account.getText().toString().trim())) {
                    SobotToastUtil.showCustomToast(this, "账号不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password.getText().toString().trim()) && TextUtils.isEmpty(token.getText().toString().trim())) {
                    SobotToastUtil.showCustomToast(this, "密码或者token至少一个不能为空");
                    return;
                }

                if (!TextUtils.isEmpty(host.getText().toString().trim())) {
                    //初始化方法由基础组件包提供，要使用IM SDK，需要在宿主应用application.onCreate函数中调用基础组件包提供的初始化函数
                    if (host.getText().toString().trim().startsWith("http")) {
                        SobotOrderApi.initWithHost(SobotApplicgtion.applicgtion, host.getText().toString().trim());
                    } else {
                        SobotToastUtil.showCustomToast(this, "请输入http或者https开头的域名");
                        return;
                    }
                } else {
                    SobotOrderApi.initWithHost(SobotApplicgtion.applicgtion, "");
//                    SobotOrderApi.initWithHost(SobotApplicgtion.applicgtion, "http://test.sobot.com");
//                    SobotOrderApi.initWithHost(SobotApplicgtion.applicgtion, "https://ten.sobot.com");
                }

                if (!TextUtils.isEmpty(password.getText().toString().trim())) {
                    SobotOrderApi.loginUser(SobotStartActivity.this, account.getText().toString().trim(), password.getText().toString().trim(), false, new SobotResultBlock() {
                        @Override
                        public void resultBolok(SobotResultCode code, String msg, Object obj) {
                            if (code == SobotResultCode.CODE_SUCCEEDED) {
                                Intent classificationIntent = new Intent(SobotStartActivity.this, SobotHomeActivity.class);
                                startActivity(classificationIntent);
                            } else {
                                SobotToastUtil.showCustomToast(SobotStartActivity.this, msg);
                            }
                        }
                    });
                } else {
                    SobotOrderApi.loginUser(SobotStartActivity.this, token.getText().toString().trim(), false, new SobotResultBlock() {
                        @Override
                        public void resultBolok(SobotResultCode code, String msg, Object obj) {
                            if (code == SobotResultCode.CODE_SUCCEEDED) {
                                Intent classificationIntent = new Intent(SobotStartActivity.this, SobotHomeActivity.class);
                                startActivity(classificationIntent);
                            } else {
                                SobotToastUtil.showCustomToast(SobotStartActivity.this, msg);
                            }
                        }
                    });
                }
                break;
        }

    }
}

