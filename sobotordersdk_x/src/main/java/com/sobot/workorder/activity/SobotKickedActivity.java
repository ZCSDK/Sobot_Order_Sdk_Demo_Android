package com.sobot.workorder.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.SobotOrderApi;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotEncryptionUtil;
import com.sobot.workorderlibrary.api.SobotOrderService;
import com.sobot.workorderlibrary.api.SobotOrderServiceFactory;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;

public class SobotKickedActivity extends SobotWOBaseActivity implements View.OnClickListener {

    Button btnCancel;
    Button btnOk;
    TextView tvHintContent;
    View vLine;
    LinearLayout llBottom;
    RelativeLayout llRealContent;
    private boolean isTokenfailure;

    @Override
    protected int getContentViewResId() {
        return SobotResourceUtils.getResLayoutId(getSobotBaseContext(), "sobot_activity_kicked");
    }

    @Override
    protected void initView() {
        btnCancel = findViewById(getResId("btn_cancel"));
        btnOk = findViewById(getResId("btn_ok"));
        tvHintContent = findViewById(getResId("tv_hint_content"));
        vLine = findViewById(getResId("v_line"));
        llBottom = findViewById(getResId("ll_bottom"));
        llRealContent = findViewById(getResId("ll_real_content"));
        Intent intent = getIntent();
        isTokenfailure = intent.getBooleanExtra("isTokenfailure", false);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            llRealContent.setVisibility(View.GONE);
            String loginUser = SobotSharedPreferencesUtil.getInstance(getSobotBaseContext()).get(SobotWOConstant.SOBOT_KEY_USERNAME);
            SobotOrderApi.out(getSobotBaseContext(),SobotEncryptionUtil.decode(loginUser),null);
            SobotGlobalContext.getInstance(getSobotBaseActivity()).finishAllActivity();
        } else if (id == R.id.btn_ok) {
            String loginUser = SobotSharedPreferencesUtil.getInstance(getSobotBaseContext()).get(SobotWOConstant.SOBOT_KEY_USERNAME);
            String loginPwd = SobotSharedPreferencesUtil.getInstance(getSobotBaseContext()).get(SobotWOConstant.SOBOT_KEY_PWD);
            startWithAcount(getSobotBaseActivity(), getSobotBaseContext(), SobotEncryptionUtil.decode(loginUser), SobotEncryptionUtil.decode(loginPwd));
        }
    }

    /**
     * 通过邮箱密码方式登录工单系统
     *
     * @param context
     * @param loginPwd 客服邮箱
     * @param loginPwd 登录密码
     */
    public static void startWithAcount(final Activity activity, final Context context, String loginUser, String loginPwd) {
        if (context == null) {
            SobotLogUtils.e("startAuthWithAcount 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(loginUser)) {
            SobotLogUtils.e("客服账户不能为空");
            return;
        }
        if (TextUtils.isEmpty(loginPwd)) {
            SobotLogUtils.e("登录密码不能为空");
            return;
        }

        final SobotOrderService zhiChiApi = SobotOrderServiceFactory.createZhiChiApi(context);
        zhiChiApi.login(context, loginUser, loginPwd, new SobotResultCallBack<Object>() {
            @Override
            public void onSuccess(Object object) {
                if (object != null && !TextUtils.isEmpty(object.toString())) {
                    SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_KEY_TOKEN, object.toString());
                    zhiChiApi.getServiceDataInfo(context, new SobotResultCallBack<SobotServiceVoModel>() {
                        @Override
                        public void onSuccess(SobotServiceVoModel sobotServiceVoModel) {
                            if (sobotServiceVoModel != null) {
                                SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_LOGIN_USER_INFO, sobotServiceVoModel);
                            }
                        }

                        @Override
                        public void onFailure(Exception e, String des) {

                        }
                    });
                    activity.setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                    activity.finish();
                } else {
                    SobotLogUtils.e("工单客服登录失败:接口返回为空");
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotLogUtils.e("工单客服登录失败");
            }
        });
    }
}