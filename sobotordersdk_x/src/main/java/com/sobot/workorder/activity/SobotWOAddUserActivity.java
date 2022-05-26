package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotRegularUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.model.SobotCreateWOUserItemsModel;
import com.sobot.workorderlibrary.api.model.SobotCreateWOUserModelResult;
import com.sobot.workorderlibrary.api.model.SobotWOUserModel;

import java.util.List;

//工单对应客户 添加用户界面
public class SobotWOAddUserActivity extends SobotWOBaseActivity implements View.OnClickListener {

    private EditText et_user_nike;
    private EditText et_user_email;
    private EditText et_user_phone;
    private ImageView iv_delete_user_name;
    private ImageView iv_delete_user_email;
    private ImageView iv_delete_user_phone;
    private TextView tv_commit_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return SobotResourceUtils.getResLayoutId(getSobotBaseContext(), "sobot_activity_wo_add_user");
    }

    @Override
    protected void initView() {
        setTitle(getResString("sobot_add_user_string"));
        tv_commit_user = findViewById(getResId("tv_commit_user"));
        tv_commit_user.setOnClickListener(this);
        et_user_nike = findViewById(getResId("et_user_nike"));
        et_user_email = findViewById(getResId("et_user_email"));
        et_user_phone = findViewById(getResId("et_user_phone"));
        iv_delete_user_name = findViewById(getResId("iv_delete_user_name"));
        iv_delete_user_email = findViewById(getResId("iv_delete_user_email"));
        iv_delete_user_phone = findViewById(getResId("iv_delete_user_phone"));
        iv_delete_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_user_nike.setText("");
            }
        });
        iv_delete_user_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_user_email.setText("");
            }
        });
        iv_delete_user_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_user_phone.setText("");
            }
        });
        showDeleteImageView(et_user_email, iv_delete_user_email);
        showDeleteImageView(et_user_nike, iv_delete_user_name);
        showDeleteImageView(et_user_phone, iv_delete_user_phone);
    }

    public void showDeleteImageView(EditText editText, final ImageView imageView) {
        if (editText != null && imageView != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!TextUtils.isEmpty(s.toString())) {
                        imageView.setVisibility(View.VISIBLE);
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    @Override
    protected void initData() {
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == tv_commit_user.getId()) {
            if (SobotStringUtils.isEmpty(et_user_nike.getText().toString().trim())) {
                SobotToastUtil.showCustomToast(getSobotBaseContext(), "昵称不能为空!");
                return;
            }
            if (!TextUtils.isEmpty(et_user_email.getText().toString().trim())) {
                if (!SobotRegularUtils.isEmail(et_user_email.getText().toString().trim())) {
                    SobotToastUtil.showCustomToast(getSobotBaseContext(), "邮箱格式不正确!");
                    return;
                }
            }
            if (!TextUtils.isEmpty(et_user_phone.getText().toString().trim())) {
                if (!SobotRegularUtils.isMobileSimple(et_user_phone.getText().toString().trim())) {
                    SobotToastUtil.showCustomToast(getSobotBaseContext(), "电话格式不正确!");
                    return;
                }
            }
            SobotWOUserModel user = new SobotWOUserModel();
            user.setEmail(et_user_email.getText().toString().trim());
            user.setTel(et_user_phone.getText().toString().trim());
            user.setNick(et_user_nike.getText().toString().trim());
            zhiChiApi.addAppUserInfo(SobotWOAddUserActivity.this, user, new SobotResultCallBack<SobotCreateWOUserModelResult>() {
                @Override
                public void onSuccess(SobotCreateWOUserModelResult baseCodeThird) {
                    if (baseCodeThird.getTotalCount() == 0) {
                        if (baseCodeThird.getItem() != null) {
                            if (!TextUtils.isEmpty(baseCodeThird.getItem().getId())) {
                                SobotToastUtil.showCustomToast(getSobotBaseContext(), "保存成功");
                                Intent intent = new Intent();
                                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getSobotBaseContext());
                                intent.setAction(SobotConstantUtils.SOBOT_CREATE_WORKORDER_USER);
                                intent.putExtra("userId", baseCodeThird.getItem().getId());
                                intent.putExtra("userName", baseCodeThird.getItem().getNick());
                                localBroadcastManager.sendBroadcast(intent);
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setResult(RESULT_OK);
                                finish();
                            }
                        }, 1000);

                    } else {
                        int fieldType = -1;
                        if (baseCodeThird.getItems() != null) {
                            List<SobotCreateWOUserItemsModel> itemsModelList = baseCodeThird.getItems();
                            if (itemsModelList != null && itemsModelList.size() > 0) {
                                // fieldType (0 邮箱冲突 1 手机号冲突）
                                try {
                                    fieldType = itemsModelList.get(0).getFieldType();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (fieldType == 0) {
                                    SobotToastUtil.showCustomToast(getSobotBaseContext(), "与其他客户邮箱重复!");
                                } else if (fieldType == 1) {
                                    SobotToastUtil.showCustomToast(getSobotBaseContext(), "与其他客户电话重复!");
                                } else {
                                    SobotToastUtil.showCustomToast(getSobotBaseContext(), "保存失败!");
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotToastUtil.showCustomToast(getSobotBaseContext(), des);
                }
            });
        }
    }
}