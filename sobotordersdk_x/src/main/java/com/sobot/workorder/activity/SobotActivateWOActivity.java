package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sobot.common.ui.toast.SobotToastUtil;
import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.permission.SobotPermissionManager;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotDialogUtils;
import com.sobot.workorder.utils.SobotTicketDictUtil;
import com.sobot.workorder.weight.SobotSettingItemView;
import com.sobot.workorder.weight.SobotWheelPickerDialog;
import com.sobot.workorderlibrary.api.apiutils.SobotWOBaseSeconndCode;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotCommonItemModel;
import com.sobot.workorderlibrary.api.model.SobotServiceGroupModelResult;
import com.sobot.workorderlibrary.api.model.SobotServiceModelResult;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;
import com.sobot.workorderlibrary.api.model.SobotTicketModel;
import com.sobot.workorderlibrary.api.model.SobotWODictModelResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 激活工单
 * create by gqf 2022-04-20
 */
public class SobotActivateWOActivity extends SobotWOBaseActivity implements View.OnClickListener {
    SobotSettingItemView sivWorkOrderStatus;
    SobotSettingItemView sivWorkOrderPriority;
    SobotSettingItemView sivWorkOrderCustomerServiceGroup;
    SobotSettingItemView sivWorkOrderCustomerService;
    EditText etWorkOrderReplyDescribe;
    TextView rightMenu;

    SobotTicketModel ticketDetail;

    private SobotServiceVoModel sobotServiceVoModel;

    private List<SobotWODictModelResult> dictStatus_list = new ArrayList<>();
    private List<SobotWODictModelResult> dictPriority_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return SobotResourceUtils.getResLayoutId(this, "sobot_activity_activate_order");
    }

    @Override
    protected void initView() {
        sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(getSobotBaseContext()).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
        setTitle(SobotResourceUtils.getResString(this, "sobot_detail_activation_order_string"));
        rightMenu = getRightMenu();
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText(SobotResourceUtils.getResString(this, "sobot_detail_activation_string"));
        rightMenu.setOnClickListener(this);
        sivWorkOrderStatus = findViewById(SobotResourceUtils.getResId(this, "siv_work_order_status"));
        sivWorkOrderPriority = findViewById(SobotResourceUtils.getResId(this, "siv_work_order_priority"));
        sivWorkOrderCustomerServiceGroup = findViewById(SobotResourceUtils.getResId(this, "siv_work_order_customer_service_group"));
        sivWorkOrderCustomerService = findViewById(SobotResourceUtils.getResId(this, "siv_work_order_customer_service"));
        etWorkOrderReplyDescribe = findViewById(SobotResourceUtils.getResId(this, "et_work_order_reply_describe"));

        List<SobotWODictModelResult> dictStatus = SobotTicketDictUtil.getDictStatus(this);
        List<SobotWODictModelResult> dictPriority = SobotTicketDictUtil.getDictPriority(this);

        dictStatus_list.clear();
        dictPriority_list.clear();
        dictStatus_list.addAll(dictStatus);
        /**
         * 创建的时候不可能会有已删除这个状态 因此需要额外删除
         */
        for (int i = dictStatus_list.size() - 1; i >= 0; i--) {
            if ("3".equals(dictStatus_list.get(i).getDictValue())//已解决
                    || "98".equals(dictStatus_list.get(i).getDictValue())//已删除
                    || "99".equals(dictStatus_list.get(i).getDictValue())//已关闭
            ) {
                dictStatus_list.remove(i);
            }
        }
        dictPriority_list.addAll(dictPriority);

        sivWorkOrderStatus.setOnClickListener(this);
        sivWorkOrderPriority.setOnClickListener(this);
        sivWorkOrderCustomerServiceGroup.setOnClickListener(this);
        sivWorkOrderCustomerService.setOnClickListener(this);
        if (SobotPermissionManager.isHasPermission(this, SobotPermissionManager.USER_PERMISSION_TYPE_WORK_STATUS)) {
            sivWorkOrderStatus.setVisibility(View.VISIBLE);
        } else {
            sivWorkOrderStatus.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        ticketDetail = (SobotTicketModel) getIntent().getSerializableExtra(SobotConstantUtils.sobto_work_order_data);
        if (ticketDetail == null) {
            return;
        }

        if (!TextUtils.isEmpty(ticketDetail.getDealGroupId())) {
            sivWorkOrderCustomerServiceGroup.setText(ticketDetail.getDealGroupName());
            sivWorkOrderCustomerServiceGroup.setValue(ticketDetail.getDealGroupId());
        }

        if (!TextUtils.isEmpty(ticketDetail.getDealUserId())) {//有受理客服
            sivWorkOrderStatus.setText(SobotTicketDictUtil.getStatusName(this, "1"));
            sivWorkOrderStatus.setValue("1");
            sivWorkOrderCustomerService.setText(ticketDetail.getDealUserName());
            sivWorkOrderCustomerService.setValue(ticketDetail.getDealUserId());
        } else {
            sivWorkOrderStatus.setText(SobotTicketDictUtil.getStatusName(this, "0"));
            sivWorkOrderStatus.setValue("0");
        }

        sivWorkOrderPriority.setText(SobotTicketDictUtil.getPriorityName(this, ticketDetail.getTicketLevel() + ""));
        sivWorkOrderPriority.setValue(ticketDetail.getTicketLevel() + "");
    }

    @Override
    public void onClick(View view) {
        if (rightMenu == view) {

            String remark = etWorkOrderReplyDescribe.getText().toString().trim();
            if (TextUtils.isEmpty(remark)) {
                etWorkOrderReplyDescribe.setText("");
                SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, SobotResourceUtils.getResString(SobotActivateWOActivity.this, "sobot_please_input_remark_string"));
                return;
            } else if (remark.length() > 200) {
                SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, SobotResourceUtils.getResString(SobotActivateWOActivity.this, "sobot_please_input_remark_hint1_string") + (remark.length() - 200) + SobotResourceUtils.getResString(SobotActivateWOActivity.this, "sobot_please_input_remark_hint2_string"));
                return;
            }
            showProgressDialog();
            zhiChiApi.activationWorkOrder(SobotActivateWOActivity.this, getTicket(), new SobotResultCallBack<SobotWOBaseSeconndCode>() {
                @Override
                public void onSuccess(SobotWOBaseSeconndCode sobotWOBaseSeconndCode) {
                    dismissProgressDialog();
                    if ("1".equals(sobotWOBaseSeconndCode.getCode())) {
                        //激活成功
                        SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, SobotResourceUtils.getResString(SobotActivateWOActivity.this, "sobot_detail_activation_success_string"), SobotResourceUtils.getDrawableId(SobotActivateWOActivity.this, "sobot_icon_login_right"));
                        setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                        finish();
                    } else {
                        SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(sobotWOBaseSeconndCode.getMsg()) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : sobotWOBaseSeconndCode.getMsg(), SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    dismissProgressDialog();
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : des, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
                }
            });
        } else if (view == sivWorkOrderCustomerServiceGroup) {
            //受理客服组
            SobotDialogUtils.startProgressDialog(SobotActivateWOActivity.this);
            zhiChiApi.queryAppServiceGroupList(SobotActivateWOActivity.this, queryAppServiceGroupListRequest);

        } else if (view == sivWorkOrderCustomerService) {
            //受理客服
            SobotDialogUtils.startProgressDialog(SobotActivateWOActivity.this);
            String groupId = "";
            if (!TextUtils.isEmpty(sivWorkOrderCustomerServiceGroup.getValue()) &&
                    !sivWorkOrderCustomerServiceGroup.getValue().equals("-1")) {
                groupId = sivWorkOrderCustomerServiceGroup.getValue();
            }
            boolean isNew = false;
            if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
                isNew = true;
            }
            zhiChiApi.queryAppServiceList(SobotActivateWOActivity.this, groupId,isNew, queryAppServiceListRequest);
        } else if (view == sivWorkOrderStatus) {
            //工单状态
            showWheelPickerDialog(dictStatus_list, SobotTicketDictUtil.getDictIndex
                            (dictStatus_list, sivWorkOrderStatus.getValue()),
                    dialogDictStatusListener);
        } else if (view == sivWorkOrderPriority) {
            //优先级
            showWheelPickerDialog(dictPriority_list, SobotTicketDictUtil.getDictIndex
                            (dictPriority_list, sivWorkOrderPriority.getValue())
                    , dialogDictPriorityListener);
        }
    }

    /**
     * 将要提交的数据放到一个对象中
     *
     * @return
     */
    public SobotTicketModel getTicket() {
        SobotTicketModel ticket = new SobotTicketModel();
        if (ticketDetail != null) {
            ticket.setTicketId(ticketDetail.getTicketId());
            ticket.setTicketCode(ticketDetail.getTicketCode());
        }

        ticket.setReplyContent(etWorkOrderReplyDescribe.getText().toString().trim());


        ticket.setTicketLevel(Integer.parseInt(sivWorkOrderPriority.getValue()));
        ticket.setTicketStatus(Integer.parseInt(sivWorkOrderStatus.getValue()));

        if (!TextUtils.isEmpty(sivWorkOrderCustomerServiceGroup.getValue()) &&
                !sivWorkOrderCustomerServiceGroup.getValue().equals("-1")) {
            ticket.setDealGroupId(sivWorkOrderCustomerServiceGroup.getValue());
            ticket.setDealGroupName(sivWorkOrderCustomerServiceGroup.getTextByTrim());
        }
        if (!TextUtils.isEmpty(sivWorkOrderCustomerService.getValue()) &&
                !sivWorkOrderCustomerService.getValue().equals("-1")) {
            ticket.setDealUserId(sivWorkOrderCustomerService.getValue());
            ticket.setDealUserName(sivWorkOrderCustomerService.getTextByTrim());
        }

        return ticket;
    }

    /**
     * 显示滚动选择器
     *
     * @param data
     * @param listener
     */
    private void showWheelPickerDialog(List<SobotWODictModelResult> data, int itemIndex, SobotWheelPickerDialog
            .OnSelectedListener listener) {
        //数据必须大于3  不然选择器会崩溃
        if (data != null && data.size() >= 3) {
            //将数据转换成字符串集合
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                strings.add(data.get(i).getDictName());
            }
            SobotLogUtils.i("传给选择器的数据:" + strings.toString());
            try {
                SobotWheelPickerDialog dialogDict = new SobotWheelPickerDialog(SobotActivateWOActivity
                        .this, strings, itemIndex);
                dialogDict.setOnSelectedListener(listener);
                dialogDict.show();
            } catch (Exception e) {
                SobotToastUtil.showToast(SobotActivateWOActivity.this, getResString("sobot_data_error_string"));
            }
        } else {
            SobotToastUtil.showToast(SobotActivateWOActivity.this, getResString("sobot_data_error_string"));
        }
    }

    /**
     * 工单状态选择回调
     */
    private SobotWheelPickerDialog.OnSelectedListener dialogDictStatusListener = new SobotWheelPickerDialog.OnSelectedListener() {
        @Override
        public void onSelected(int index, String str) {
            String dictName = dictStatus_list.get(index).getDictName();
            String dictValue = dictStatus_list.get(index).getDictValue();
            SobotLogUtils.i("工单状态选择回调   " + dictName + "    " + dictValue);
            if (!TextUtils.isEmpty(dictValue) && "99".equals(dictValue)) {
                if (!SobotPermissionManager.isHasPermission(getSobotBaseContext(), SobotPermissionManager.USER_PERMISSION_TYPE_COLSE_WORK_ORDER)) {
                    SobotToastUtil.showToast(SobotActivateWOActivity.this, getResString("sobot_no_permission_enabled_string"));
                } else {
                    sivWorkOrderStatus.setText(dictName);
                    sivWorkOrderStatus.setValue(dictValue);
                }
            } else {
                sivWorkOrderStatus.setText(dictName);
                sivWorkOrderStatus.setValue(dictValue);
            }
        }
    };

    /**
     * 优先级选择回调
     */
    private SobotWheelPickerDialog.OnSelectedListener dialogDictPriorityListener = new SobotWheelPickerDialog.OnSelectedListener() {
        @Override
        public void onSelected(int index, String str) {
            String dictName = dictPriority_list.get(index).getDictName();
            String dictValue = dictPriority_list.get(index).getDictValue();
            SobotLogUtils.i("优先级选择回调   " + dictName + "    " + dictValue);
            sivWorkOrderPriority.setText(dictName);
            sivWorkOrderPriority.setValue(dictValue);
        }
    };
    /*  以下为请求回调  */
    //查询受理客服组
    SobotResultCallBack<List<SobotServiceGroupModelResult>> queryAppServiceGroupListRequest = new SobotResultCallBack<List<SobotServiceGroupModelResult>>() {
        @Override
        public void onSuccess(List<SobotServiceGroupModelResult> sobotServiceGroupModelResults) {
            SobotDialogUtils.stopProgressDialog(SobotActivateWOActivity.this);
            SobotLogUtils.i("查询受理客服组成功----getData：" + sobotServiceGroupModelResults);
            ArrayList<SobotCommonItemModel> data = new ArrayList<>();
            for (int i = 0; i < sobotServiceGroupModelResults.size(); i++) {
                SobotServiceGroupModelResult item = sobotServiceGroupModelResults.get(i);
                SobotCommonItemModel commonItemModel = new SobotCommonItemModel(item.getGroupName(), item.getGroupId());
                data.add(commonItemModel);
            }
            startListActivity(data, SobotConstantUtils.sobot_wo_list_display_type_service_group, sivWorkOrderCustomerServiceGroup.getValue());

        }

        @Override
        public void onFailure(Exception e, String s) {
            SobotDialogUtils.stopProgressDialog(SobotActivateWOActivity.this);
            com.sobot.workorder.weight.toast.SobotToastUtil.showCustomToast(SobotActivateWOActivity.this,s);
        }
    };

    /**
     * 开启列表展示activity
     *
     * @param data
     * @param displayType
     */
    public void startListActivity(ArrayList<SobotCommonItemModel> data, int displayType, String
            selectedValue) {
        Intent intent = new Intent(getApplicationContext(), SobotWOListActivity.class);
        intent.putExtra(SobotConstantUtils.sobot_wo_display_type, displayType);
        if(null!=data && data.size()>0){
            intent.putExtra(SobotConstantUtils.sobto_work_order_data,data);
        }
        if (!TextUtils.isEmpty(selectedValue)) {
            intent.putExtra(SobotConstantUtils.sobot_wo_list_data_selected_value, selectedValue);
        }

        startActivityForResult(intent, displayType);
    }



    //查询受理客服
    SobotResultCallBack<List<SobotServiceModelResult>> queryAppServiceListRequest = new SobotResultCallBack<List<SobotServiceModelResult>>() {

        @Override
        public void onSuccess(List<SobotServiceModelResult> items) {
            SobotDialogUtils.stopProgressDialog(SobotActivateWOActivity.this);
            SobotLogUtils.i("查询受理客服----getData：" + items);
            ArrayList<SobotCommonItemModel> data = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                SobotServiceModelResult item = items.get(i);
                SobotCommonItemModel commonItemModel = new SobotCommonItemModel(item.getServiceName(), item.getServiceId());
                data.add(commonItemModel);
            }
            startListActivity(data, SobotConstantUtils.sobot_wo_list_display_type_service,
                    sivWorkOrderCustomerService.getValue());
        }

        @Override
        public void onFailure(Exception e, String s) {
            SobotDialogUtils.stopProgressDialog(SobotActivateWOActivity.this);
            com.sobot.workorder.weight.toast.SobotToastUtil.showCustomToast(SobotActivateWOActivity.this,s);
        }
    };
}