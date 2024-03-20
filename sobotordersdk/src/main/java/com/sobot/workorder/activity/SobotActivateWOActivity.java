package com.sobot.workorder.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sobot.common.login.SobotLoginTools;
import com.sobot.common.login.model.SobotServiceInfoModel;
import com.sobot.common.login.permission.SobotPermissionManager;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.livedatabus.SobotLiveEventBus;
import com.sobot.widget.ui.toast.SobotToastUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotDialogUtils;
import com.sobot.workorder.weight.SobotSettingItemView;
import com.sobot.workorder.weight.SobotWheelPickerDialog;
import com.sobot.workorderlibrary.api.apiutils.SobotWOBaseSeconndCode;
import com.sobot.workorderlibrary.api.model.SobotCommonItemModel;
import com.sobot.workorderlibrary.api.model.SobotServiceGroupModelResult;
import com.sobot.workorderlibrary.api.model.SobotServiceModelResult;
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

    private SobotServiceInfoModel sobotServiceVoModel;

    private List<SobotWODictModelResult> dictStatus_list = new ArrayList<>();
    private List<SobotWODictModelResult> dictPriority_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_activate_order;
    }

    @Override
    protected void initView() {
        sobotServiceVoModel = SobotLoginTools.getInstance().getServiceInfo();
        setTitle(getString(R.string.sobot_detail_activation_order_string));
        rightMenu = getRightMenu();
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText(getString(R.string.sobot_detail_activation_string));
        rightMenu.setOnClickListener(this);
        sivWorkOrderStatus = findViewById(R.id.siv_work_order_status);
        sivWorkOrderPriority = findViewById(R.id.siv_work_order_priority);
        sivWorkOrderCustomerServiceGroup = findViewById(R.id.siv_work_order_customer_service_group);
        sivWorkOrderCustomerService = findViewById(R.id.siv_work_order_customer_service);
        etWorkOrderReplyDescribe = findViewById(R.id.et_work_order_reply_describe);

        List<SobotWODictModelResult> dictStatus = getDictStatus(this);
        List<SobotWODictModelResult> dictPriority = getDictPriority(this);

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
        if (SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_WORK_STATUS)) {
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
            sivWorkOrderStatus.setText(getStatusName(this, "1"));
            sivWorkOrderStatus.setValue("1");
            sivWorkOrderCustomerService.setText(ticketDetail.getDealUserName());
            sivWorkOrderCustomerService.setValue(ticketDetail.getDealUserId());
        } else {
            sivWorkOrderStatus.setText(getStatusName(this, "0"));
            sivWorkOrderStatus.setValue("0");
        }

        sivWorkOrderPriority.setText(getPriorityName(this, ticketDetail.getTicketLevel() + ""));
        sivWorkOrderPriority.setValue(ticketDetail.getTicketLevel() + "");
    }

    @Override
    public void onClick(View view) {
        if (rightMenu == view) {
            String remark = etWorkOrderReplyDescribe.getText().toString().trim();
            if (TextUtils.isEmpty(remark)) {
                etWorkOrderReplyDescribe.setText("");
                SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, getString(R.string.sobot_please_input_remark_string));
                return;
            } else if (remark.length() > 200) {
                SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, getString(R.string.sobot_please_input_remark_hint1_string) + (remark.length() - 200) + getString(R.string.sobot_please_input_remark_hint2_string));
                return;
            }
            showProgressDialog();
            zhiChiApi.activationWorkOrder(SobotActivateWOActivity.this, getTicket(), new SobotResultCallBack<SobotWOBaseSeconndCode>() {
                @Override
                public void onSuccess(SobotWOBaseSeconndCode sobotWOBaseSeconndCode) {
                    dismissProgressDialog();
                    if ("1".equals(sobotWOBaseSeconndCode.getCode())) {
                        //激活成功
                        SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, getString(R.string.sobot_detail_activation_success_string), R.drawable.sobot_icon_login_right);
                        SobotLiveEventBus.get(SobotConstantUtils.SOBOT_REFRESH_DATA).post(true);
                        finish();
                    } else {
                        SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(sobotWOBaseSeconndCode.getMsg()) ? getString(R.string.sobot_wo_net_error_string) : sobotWOBaseSeconndCode.getMsg(), R.drawable.sobot_icon_warning_attention);

                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    dismissProgressDialog();
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? getString(R.string.sobot_wo_net_error_string) : des, R.drawable.sobot_icon_warning_attention);
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
            zhiChiApi.queryAppServiceList(SobotActivateWOActivity.this, groupId, isNew, queryAppServiceListRequest);
        } else if (view == sivWorkOrderStatus) {
            //工单状态
            showWheelPickerDialog(dictStatus_list, getDictIndex
                            (dictStatus_list, sivWorkOrderStatus.getValue()),
                    dialogDictStatusListener);
        } else if (view == sivWorkOrderPriority) {
            //优先级
            showWheelPickerDialog(dictPriority_list, getDictIndex
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
                if (!SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_COLSE_WORK_ORDER)) {
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
            com.sobot.workorder.weight.toast.SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, s);
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
        if (null != data && data.size() > 0) {
            intent.putExtra(SobotConstantUtils.sobto_work_order_data, data);
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
            com.sobot.workorder.weight.toast.SobotToastUtil.showCustomToast(SobotActivateWOActivity.this, s);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            switch (requestCode) {
                case SobotConstantUtils.sobot_wo_list_display_type_service_group://受理客服组
                    if (data.getExtras() != null) {
                        SobotCommonItemModel selectedItem_service_group = (SobotCommonItemModel) data.getExtras()
                                .getSerializable(SobotConstantUtils.sobot_wo_list_selected_item);
                        if (selectedItem_service_group != null && !"-1".equals(selectedItem_service_group.getItemValue())) {
                            sivWorkOrderCustomerServiceGroup.setText(selectedItem_service_group.getItemKey());
                            sivWorkOrderCustomerServiceGroup.setValue(selectedItem_service_group.getItemValue());
                            sivWorkOrderCustomerService.reset();
                        } else {
                            sivWorkOrderCustomerServiceGroup.reset();
                        }
                    }
                    break;
                case SobotConstantUtils.sobot_wo_list_display_type_service://受理客服
                    if (data.getExtras() != null) {
                        SobotCommonItemModel selectedItem_service = (SobotCommonItemModel) data.getExtras()
                                .getSerializable(SobotConstantUtils.sobot_wo_list_selected_item);
                        if (selectedItem_service != null && !"-1".equals(selectedItem_service.getItemValue())) {
                            sivWorkOrderCustomerService.setText(selectedItem_service.getItemKey());
                            sivWorkOrderCustomerService.setValue(selectedItem_service.getItemValue());
                        } else {
                            sivWorkOrderCustomerService.reset();
                        }
                    }
                    break;
            }
        }
    }

    public List<SobotWODictModelResult> getDictStatus(Context context) {
        List<SobotWODictModelResult> dictStatus = new ArrayList<>();
        dictStatus.add(new SobotWODictModelResult(0, context.getResources().getString(R.string.sobot_wo_item_state_not_start_string)));
        dictStatus.add(new SobotWODictModelResult(1, context.getResources().getString(R.string.sobot_wo_item_state_doing_string)));
        dictStatus.add(new SobotWODictModelResult(2, context.getResources().getString(R.string.sobot_wo_item_state_waiting_string)));
        dictStatus.add(new SobotWODictModelResult(3, context.getResources().getString(R.string.sobot_wo_item_state_resolved_string)));
        dictStatus.add(new SobotWODictModelResult(98, context.getResources().getString(R.string.sobot_already_delete)));
        dictStatus.add(new SobotWODictModelResult(99, context.getResources().getString(R.string.sobot_wo_item_state_closed_string)));
        return dictStatus;
    }

    public List<SobotWODictModelResult> getDictPriority(Context context) {
        List<SobotWODictModelResult> dictPriority = new ArrayList<>();
        dictPriority.add(new SobotWODictModelResult(0, context.getResources().getString(R.string.sobot_low_string)));
        dictPriority.add(new SobotWODictModelResult(1, context.getResources().getString(R.string.sobot_middle_string)));
        dictPriority.add(new SobotWODictModelResult(2, context.getResources().getString(R.string.sobot_height_string)));
        dictPriority.add(new SobotWODictModelResult(3, context.getResources().getString(R.string.sobot_urgent_string)));
        return dictPriority;
    }

    public static List<SobotWODictModelResult> getDictType(Context context) {
        List<SobotWODictModelResult> dictType = new ArrayList<>();
        dictType.add(new SobotWODictModelResult(9, context.getResources().getString(R.string.sobot_other_string)));
        dictType.add(new SobotWODictModelResult(0, context.getResources().getString(R.string.sobot_complaint_string)));
        dictType.add(new SobotWODictModelResult(1, context.getResources().getString(R.string.sobot_problem_string)));
        dictType.add(new SobotWODictModelResult(2, context.getResources().getString(R.string.sobot_affair_affair)));
        dictType.add(new SobotWODictModelResult(3, context.getResources().getString(R.string.sobot_fault_string)));
        dictType.add(new SobotWODictModelResult(4, context.getResources().getString(R.string.sobot_task_string)));
        return dictType;
    }

    public String getPriorityName(Context context, String value) {
        List<SobotWODictModelResult> dictPriority = getDictPriority(context);
        String result = "";
        for (int i = 0; i < dictPriority.size(); i++) {
            if (dictPriority.get(i).getDictValue().equals(value)) {
                return dictPriority.get(i).getDictName();
            }
        }
        return result;
    }

    public String getStatusName(Context context, String value) {
        List<SobotWODictModelResult> dictStatus = getDictStatus(context);
        String result = "";
        for (int i = 0; i < dictStatus.size(); i++) {
            if (dictStatus.get(i).getDictValue().equals(value)) {
                return dictStatus.get(i).getDictName();
            }
        }
        return result;
    }
    public int getDictIndex(List<SobotWODictModelResult> list, String value) {
        int result = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDictValue().equals(value)) {
                return i;
            }
        }

        return result;
    }
}