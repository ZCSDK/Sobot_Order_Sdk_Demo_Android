package com.sobot.workorder.weight.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.utils.CustomFieldsUtils;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfig;
import com.sobot.workorderlibrary.api.model.SobotCustomerModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class CostomerDetailDialog extends SobotBottomDialog implements View.OnClickListener {
    private LinearLayout coustom_pop_layout, sobot_negativeButton;
    private TextView tv_title;
    private TextView tv_nick_value;
    //真实姓名
    private TextView tv_realname_value;
    //性别
    private TextView tv_sex_value;
    private TextView tv_phone_value;
    private LinearLayout ll_add_phone;
    //邮箱
    private TextView tv_email_value;
    private LinearLayout ll_add_email;

    private RelativeLayout rl_call_user_country;//客户国家
    private TextView tv_country_value;//客户国家
    //城市
    private TextView tv_city_value;
    private TextView tv_company_value;//客户公司名称
    private TextView tv_qq_value;//客户QQ
    private TextView tv_wx_value;//客户微信
    private TextView tv_remark_value;//客户备注信息
    private TextView tv_is_vip;//是否是vip
    private TextView tv_source_value;//来源
    private TextView btn_save;
    private LinearLayout ll_cusfield;//显示客户自定义字段的布局
    private DialogItemOnClick listener;
    private Activity activity;
    private SobotCustomerModel customerModel;
    private List<SobotCusFieldConfig> cusFieldConfigList;//所有自定义字段

    public CostomerDetailDialog(Activity context, SobotCustomerModel customer, List<SobotCusFieldConfig> cusFieldConfigList, DialogItemOnClick listener) {
        super(context);
        activity = context;
        customerModel = customer;
        this.listener = listener;
        this.cusFieldConfigList = cusFieldConfigList;
    }

    @Override
    protected int getLayout() {
        return R.layout.sobot_dialog_custom_detail;
    }

    @Override
    protected View getDialogContainer() {
        if (coustom_pop_layout == null) {
            coustom_pop_layout = findViewById(R.id.sobot_container);
        }

        return coustom_pop_layout;
    }

    @Override
    protected void initData() {
        if (customerModel != null) {
            tv_title.setText(getText(customerModel.getNick()));
            tv_nick_value.setText(getText(customerModel.getNick()));
            tv_realname_value.setText(getText(customerModel.getUname()));
            tv_source_value.setText(setSourceText(customerModel.getSource()));
            //性别
            if (1 == customerModel.getSex()) {
                tv_sex_value.setText(R.string.sobot_custom_male);
            } else if (2 == customerModel.getSex()) {
                tv_sex_value.setText(R.string.sobot_custom_female);
            } else {
                tv_sex_value.setText("--");
            }

            tv_company_value.setText(getText(customerModel.getEnterpriseName()));
            tv_qq_value.setText(getText(customerModel.getQq()));
            tv_wx_value.setText(getText(customerModel.getWx()));
            tv_remark_value.setText(getText(customerModel.getRemark()));


            if (SobotStringUtils.isNoEmpty(customerModel.getTel()) && customerModel.getTel().contains(";")) {
                String[] tmpUserPhones = customerModel.getTel().split(";");
                tv_phone_value.setText(tmpUserPhones[0]);
                if (tmpUserPhones.length > 1) {
                    for (int i = 1; i < tmpUserPhones.length; i++) {
                        addUserPhone(tmpUserPhones[i]);
                    }
                }
            } else {
                tv_phone_value.setText(getText(customerModel.getTel()));
            }
            if (SobotStringUtils.isNoEmpty(customerModel.getEmail()) && customerModel.getEmail().contains(";")) {
                String[] tmpUserEmail = customerModel.getEmail().split(";");
                tv_email_value.setText(tmpUserEmail[0]);
                if (tmpUserEmail.length > 1) {
                    for (int i = 1; i < tmpUserEmail.length; i++) {
                        addUserEmail(tmpUserEmail[i]);
                    }
                }
            } else {
                tv_email_value.setText(getText(customerModel.getEmail()));
            }

            if (!TextUtils.isEmpty(customerModel.getCountryName())) {
                tv_country_value.setText(customerModel.getCountryName());
                rl_call_user_country.setVisibility(View.VISIBLE);
            } else {
                rl_call_user_country.setVisibility(View.GONE);
            }
            //设置城市，需要自己拼接
            String city = "--";

            if (!TextUtils.isEmpty(customerModel.getProviceName())) {
                city = customerModel.getProviceName();
            }
            if (!TextUtils.isEmpty(customerModel.getCityName())) {
                city = customerModel.getProviceName() + "-" + customerModel.getCityName();
            }
            if (!TextUtils.isEmpty(customerModel.getAreaName())) {
                city = customerModel.getProviceName() + "-" + customerModel.getCityName() + "-" + customerModel.getAreaName();
            }
            tv_city_value.setText(city);
            if ("1".equals(customerModel.getIsVip())) {
                tv_is_vip.setText(R.string.sobot_vip_customer);
            } else {
                tv_is_vip.setText(R.string.sobot_common_user);
            }
            if (cusFieldConfigList != null && cusFieldConfigList.size() > 0) {
                //设置自定义字段的值
                if (customerModel != null && customerModel.getCusList() != null) {
                    Map<String, String> map = customerModel.getCusList();
                    for (int i = 0; i < cusFieldConfigList.size(); i++) {
                        for (int j = 0; j < map.size(); j++) {
                            String value = map.get(cusFieldConfigList.get(i).getFieldVariable());
                            if (!TextUtils.isEmpty(value)) {
                                if (cusFieldConfigList.get(i).getFieldType() < 6) {
                                    cusFieldConfigList.get(i).setFieldValue(value);
                                } else {
                                    try {
                                        JSONObject object = new JSONObject(value);
                                        String id = object.optString("id");
                                        String value1 = object.optString("value");
                                        cusFieldConfigList.get(i).setFieldValue(value1);
                                    } catch (JSONException e) {
                                        cusFieldConfigList.get(i).setFieldValue(value);
//                                    throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    }
                }
                //设置城市，需要自己拼接
                setCurFieldValue("cityName", city);
                CustomFieldsUtils.showCusFields(activity, cusFieldConfigList, ll_cusfield);
            }
        }
    }

    /**
     * 设置自定义字段的值
     *
     * @param fieldName 字段的名字
     * @param value     字段对应的值
     */
    private void setCurFieldValue(String fieldName, String value) {
        if (cusFieldConfigList != null) {
            for (int i = 0; i < cusFieldConfigList.size(); i++) {
                if (cusFieldConfigList.get(i).getFieldVariable().equals(fieldName)) {
                    SobotCusFieldConfig config = cusFieldConfigList.get(i);
                    config.setFieldValue(value);
                }
            }
        }
    }

    private void addUserEmail(String userEmail) {
        if (TextUtils.isEmpty(userEmail)) {
            return;
        }
        ll_add_email.setVisibility(View.VISIBLE);
        View view = View.inflate(getContext(), R.layout.sobot_item_swipe_copy, null);
        TextView tv = view.findViewById(R.id.tv_add_user_info);
        tv.setText(userEmail);
        ll_add_email.addView(view);
    }

    private void addUserPhone(String userPhone) {
        if (TextUtils.isEmpty(userPhone)) {
            return;
        }
        ll_add_phone.setVisibility(View.VISIBLE);
        View view = View.inflate(getContext(), R.layout.sobot_item_swipe_copy, null);
        TextView tv = view.findViewById(R.id.tv_add_user_info);
        tv.setText(userPhone);
        ll_add_phone.addView(view);
    }

    private String getText(String text) {
        return TextUtils.isEmpty(text) ? "--" : text;
    }

    private String setSourceText(String sourceType) {
        if (TextUtils.isEmpty(sourceType)) {
            return "";
        }
        String sourceName;
        switch (sourceType) {
            case "0":
                sourceName = getContext().getResources().getString(R.string.sobot_source_pc);
                break;
            case "1":
                sourceName = getContext().getResources().getString(R.string.sobot_source_wechat);
                break;
            case "2":
                sourceName = getContext().getResources().getString(R.string.sobot_source_app);
                break;
            case "3":
                sourceName = getContext().getResources().getString(R.string.sobot_source_weibo);
                break;
            case "4":
                sourceName = getContext().getResources().getString(R.string.sobot_source_mobile_web);
                break;
            case "5":
                sourceName = getContext().getResources().getString(R.string.sobot_source_rong);
                break;
            case "6":
                sourceName = getContext().getResources().getString(R.string.sobot_ticket_form_call_center);
                break;
            case "7":
                sourceName = getContext().getResources().getString(R.string.sobot_title_workorder_center_string);
                break;
            case "8":
                sourceName = getContext().getResources().getString(R.string.sobot_ticket_form_customer_enter);
                break;
            default:
                sourceName = getContext().getResources().getString(R.string.sobot_source_pc);
                break;
        }
        return sourceName;
    }

    @Override
    protected void initView() {
        rl_call_user_country = findViewById(R.id.rl_call_user_country);
        sobot_negativeButton = findViewById(R.id.sobot_negativeButton);
        ll_add_email = findViewById(R.id.ll_add_email);
        ll_add_phone = findViewById(R.id.ll_add_phone);
        tv_title = findViewById(R.id.sobot_tv_title);
        tv_nick_value = findViewById(R.id.work_order_user_nick);
        tv_realname_value = findViewById(R.id.work_order_user_realname);
        tv_sex_value = findViewById(R.id.work_order_user_sex);
        tv_source_value = findViewById(R.id.work_order_user_source);
        tv_email_value = findViewById(R.id.work_order_user_email);
        tv_phone_value = findViewById(R.id.work_order_user_tel);
        tv_company_value = findViewById(R.id.work_order_user_company);
        tv_country_value = findViewById(R.id.tv_country_value);
        tv_city_value = findViewById(R.id.create_work_order_user_city);
        tv_qq_value = findViewById(R.id.work_order_user_qq);
        tv_wx_value = findViewById(R.id.work_order_user_wechat);
        tv_remark_value = findViewById(R.id.work_order_user_remark);
        tv_is_vip = findViewById(R.id.tv_is_vip_value);
        btn_save = findViewById(R.id.btn_save);
        ll_cusfield = findViewById(R.id.work_order_user_cusfield);
        ll_cusfield.setVisibility(View.GONE);
        btn_save.setOnClickListener(this);
        sobot_negativeButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == sobot_negativeButton) {
            //关闭
            dismiss();
        } else if (v == btn_save) {
            if (listener != null && customerModel != null) {
                listener.selectItem(customerModel);
            }
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}
