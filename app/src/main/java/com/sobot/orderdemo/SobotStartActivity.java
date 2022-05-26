package com.sobot.orderdemo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.workorder.SobotOrderApi;
import com.sobot.workorder.callback.SobotResultBlock;
import com.sobot.workorder.callback.SobotResultCode;
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
                    SobotOrderApi.initWithHost(SobotApplicgtion.applicgtion, host.getText().toString().trim());
                } else {
                    SobotOrderApi.initWithHost(SobotApplicgtion.applicgtion, "");
                    //SobotOrderApi.initWithHost(SobotAppSdkApplicgtion.applicgtion, "http://test.sobot.com");
                    //SobotOrderApi.initWithHost(SobotAppSdkApplicgtion.applicgtion, "https://ten.sobot.com");
                }


                if (!TextUtils.isEmpty(password.getText().toString().trim())) {
//                    OrderOrderApi.startWithAcount(SobotStartActivity.this, "guoqf@sobot.com", "gqfAaa111");
//                    SobotOrderApi.startWithAcount(SobotStartActivity.this, "wanglei1@sobot.com", "Wang0901",null);
                    // SobotOrderApi.startWithAcount(SobotStartActivity.this, "zhengnw@sobot.com", "znw123456", null);
                    SobotOrderApi.loginUser(SobotStartActivity.this, account.getText().toString().trim(), password.getText().toString().trim(), false, new SobotResultBlock() {
                        @Override
                        public void resultBolok(SobotResultCode code, String msg, Object obj) {
                            if(code== SobotResultCode.CODE_SUCCEEDED) {
                                Intent classificationIntent = new Intent(SobotStartActivity.this, SobotHomeActivity.class);
                                startActivity(classificationIntent);
                            }else{
                                SobotToastUtil.showCustomToast(SobotStartActivity.this,msg);
                            }
                        }
                    });
                } else {
                    SobotOrderApi.loginUser(SobotStartActivity.this,  token.getText().toString().trim(), false, new SobotResultBlock() {
                        @Override
                        public void resultBolok(SobotResultCode code, String msg, Object obj) {
                            if(code== SobotResultCode.CODE_SUCCEEDED) {
                                Intent classificationIntent = new Intent(SobotStartActivity.this, SobotHomeActivity.class);
                                startActivity(classificationIntent);
                            }else{
                                SobotToastUtil.showCustomToast(SobotStartActivity.this,msg);
                            }
                        }
                    });
                }
                break;
        }

    }
}

