package com.sobot.orderdemo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sobot.common.ui.toast.SobotToastUtil;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.SobotOrderApi;
import com.sobot.workorder.base.SobotWOBaseFragment;
import com.sobot.workorder.callback.SobotResultBlock;
import com.sobot.workorder.callback.SobotResultCode;
import com.sobot.workorder.utils.SobotEncryptionUtil;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;

import java.util.HashMap;
import java.util.Map;

public class SobotSettingFragment extends SobotWOBaseFragment implements View.OnClickListener {
    private TextView tv_to_order, tv_open_order_by_id, tv_create_order, tv_exit_login;
    private EditText order_id, userid, nick;

    public SobotSettingFragment() {
    }

    public static SobotSettingFragment newInstance() {
        return new SobotSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View root = inflater.inflate(getResLayoutId("fragment_setting"), container, false);
        initView(root);
        initData();
        return root;
    }

    private void initView(View view) {
        tv_to_order = view.findViewById(R.id.tv_to_order);
        tv_to_order.setOnClickListener(this);
        tv_open_order_by_id = view.findViewById(R.id.tv_open_order_by_id);
        tv_open_order_by_id.setOnClickListener(this);
        tv_create_order = view.findViewById(R.id.tv_create_order);
        tv_create_order.setOnClickListener(this);
        tv_exit_login = view.findViewById(R.id.tv_exit_login);
        tv_exit_login.setOnClickListener(this);
        order_id = view.findViewById(R.id.order_id);
        nick = view.findViewById(R.id.nick);
        userid = view.findViewById(R.id.userid);
    }

    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v == tv_to_order) {
            String cacheloginToken = SobotSharedPreferencesUtil.getInstance(getSobotActivity()).get(SobotWOConstant.SOBOT_KEY_TOKEN);
            SobotOrderApi.startWithToken(getSobotActivity(), cacheloginToken, null);
        } else if (v == tv_open_order_by_id) {
            if (TextUtils.isEmpty(order_id.getText().toString().trim())) {
                SobotToastUtil.showCustomToast(getSobotActivity(), "工单编号不能为空");
                return;
            }
            SobotOrderApi.openOrderDetail(getSobotActivity(), order_id.getText().toString().trim(), new SobotResultBlock() {
                @Override
                public void resultBolok(SobotResultCode code, String msg, Object obj) {
                    if(code == SobotResultCode.CODE_FAILED){
                        SobotToastUtil.showCustomToast(getContext(),msg);
                    }
                }
            });
        } else if (v == tv_create_order) {
            Map<String, Object> param = new HashMap<>();
            if (TextUtils.isEmpty(userid.getText().toString())){
                if(!TextUtils.isEmpty(nick.getText().toString())){
                    SobotToastUtil.showCustomToast(getSobotActivity(),"如果输入昵称，但是用户id为空，对应客户是也是空的，无效的");
                }
            }else{
                if(SobotStringUtils.isEmpty(nick.getText().toString())){
                    SobotToastUtil.showCustomToast(getContext(),"用户昵称不能为空");
                    return;
                }
                param.put("gongdan",true);//不能选择客户
                param.put("userName", nick.getText().toString());
                param.put("userId", userid.getText().toString());
            }
            SobotOrderApi.openCreateWorkOrder(getSobotActivity(), param, null);
        } else if (v == tv_exit_login) {
            String cacheloginUser = SobotSharedPreferencesUtil.getInstance(getSobotActivity()).get(SobotWOConstant.SOBOT_KEY_USERNAME);
            SobotOrderApi.out(getSobotActivity(), SobotEncryptionUtil.decode(cacheloginUser),null);
            getSobotActivity().finish();
        }
    }
}