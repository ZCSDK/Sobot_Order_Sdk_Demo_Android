package com.sobot.workorder.weight.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.ui.toast.SobotToastUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.activity.SobotCategorySmallActivity;
import com.sobot.workorder.activity.SobotWOCascadeActivity;
import com.sobot.workorder.base.SobotDialogBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotDateUtil;
import com.sobot.workorder.utils.SobotDecimalDigitsInputFilter;
import com.sobot.workorder.utils.SobotKeyboardUtil;
import com.sobot.workorder.utils.SobotSoftInputManage;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfig;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//工单状态关闭时检测关单必填字段弹窗
public class SobotCloseWoFieldsActivity extends SobotDialogBaseActivity implements View.OnClickListener {
    private LinearLayout sobot_negativeButton;
    LinearLayout work_order_customer_field_list_layout;
    private ArrayList<SobotCusFieldConfig> cusFieldList;
    private Button bt_save_commit;
    private int isEdit;//isEdit 是否编辑页面  0是创建工单页面  1 是回复页面  2是编辑页面

    private SobotCusFieldConfig mCusFieldConfig;//模板内字段

    @Override
    protected void initData() {
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_dialog_close_wo_field;
    }

    @Override
    protected void initView() {
        bt_save_commit = findViewById(R.id.bt_save_commit);
        bt_save_commit.setOnClickListener(this);
        sobot_negativeButton = findViewById(R.id.sobot_negativeButton);
        sobot_negativeButton.setOnClickListener(this);
        work_order_customer_field_list_layout = findViewById(R.id.work_order_customer_field_list_layout);
        cusFieldList = (ArrayList<SobotCusFieldConfig>) getIntent().getSerializableExtra("cusFieldList");
        isEdit = getIntent().getIntExtra("isEdit", 0);
        ArrayList<SobotCusFieldConfig> tempCusFieldList = checkHasCusFieldCloseRequired();
        if ( tempCusFieldList.size() > 0) {
            addWorkOrderCusFields(getContext(), isEdit, tempCusFieldList, work_order_customer_field_list_layout);
        }
    }

    //提交时工单状态为已解决或者已关闭时 检测是否有关单必填的自定义字段未填写的
    private ArrayList<SobotCusFieldConfig> checkHasCusFieldCloseRequired() {
        ArrayList<SobotCusFieldConfig> tempCusFieldList = new ArrayList<>();
        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                SobotCusFieldConfig fieldConfig = cusFieldList.get(i);
                if (fieldConfig.getFieldType() == 10) {
                    //组合表单
                } else {
                    //可读写才能做关单必填
                    if (1 == fieldConfig.getMustFillFlag() && fieldConfig.getAuthStatus() == 1 && fieldConfig.getOpenFlag() == 1) {
                        if (SobotStringUtils.isEmpty(fieldConfig.getFieldValue())) {
                            tempCusFieldList.add(fieldConfig);
                        }
                    }
                }
            }
        }
        return tempCusFieldList;
    }

    //提交时工单状态为已解决或者已关闭时 检测是否有关单必填的自定义字段未填写的
    private boolean checkCusFieldCloseRequired() {
        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                SobotCusFieldConfig fieldConfig = cusFieldList.get(i);
                if (fieldConfig.getFieldType() == 10) {
                    //组合表单
                } else {
                    // 可读写才做关单必填
                    if (1 == fieldConfig.getMustFillFlag() && fieldConfig.getAuthStatus() == 1 && fieldConfig.getOpenFlag() == 1) {
                        if (!TextUtils.isEmpty(fieldConfig.getFieldValue())) {
                        } else {
                            SobotToastUtil.showToast(SobotCloseWoFieldsActivity.this, fieldConfig.getFieldName() + " " + getResString("sobot_cannot_empty_string"));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * isEdit 是否编辑页面  0是创建工单页面  1 是回复页面  2是编辑页面
     */
    public void addWorkOrderCusFields(final Context context, final int isEdit, final List<SobotCusFieldConfig> cusFieldList, LinearLayout containerLayout) {
        SobotLogUtils.i("isEdit================" + isEdit);
        if (containerLayout != null) {
            containerLayout.setVisibility(View.VISIBLE);
            containerLayout.removeAllViews();
            if (cusFieldList != null && cusFieldList.size() != 0) {
                int size = cusFieldList.size();
                for (int i = 0; i < cusFieldList.size(); i++) {
                    final SobotCusFieldConfig cusFieldConfig = cusFieldList.get(i);
                    SobotLogUtils.i("cusFieldConfig================" + cusFieldConfig.getFieldName() + "---------------" + cusFieldConfig.getFieldValue() + "===========" + cusFieldConfig.getFieldType());
                    if (cusFieldConfig.getOpenFlag() != 1 || (isEdit == 2 && cusFieldConfig.getAuthStatus() == 3)) {
                        continue;
                    }
                    View view = View.inflate(context, R.layout.sobot_adapter_order_cusfield_list_item, null);
                    view.setTag(cusFieldConfig.getFieldId());
                    View bottomLine = view.findViewById(R.id.work_order_customer_field_text_bootom_line);
                    if (cusFieldList.size() == 1 || i == (size - 1)) {
                        bottomLine.setVisibility(View.GONE);
                    } else {
                        bottomLine.setVisibility(View.VISIBLE);
                    }
                    LinearLayout ll_more_text_layout = (LinearLayout) view.findViewById(R.id.work_order_customer_field_more_relativelayout);
                    final LinearLayout ll_group_text_layout = (LinearLayout) view.findViewById(R.id.work_order_customer_field_group_relativelayout);
                    RelativeLayout ll_group_layout = (RelativeLayout) view.findViewById(R.id.work_order_customer_field_group__rl_add);
                    final TextView addTv = view.findViewById(R.id.work_order_customer_field_group_add);
                    final TextView summyTv = view.findViewById(R.id.work_order_customer_field_group_summy);
                    ll_group_text_layout.setVisibility(View.GONE);
                    summyTv.setVisibility(View.GONE);
                    ll_group_layout.setVisibility(View.GONE);
                    addTv.setVisibility(View.GONE);
                    TextView fieldMoreName = (TextView) view.findViewById(R.id.work_order_customer_field_more_text_lable);
                    final EditText moreContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_more_content);
                    moreContent.setHint(R.string.sobot_wo_input_hint);
                    RelativeLayout ll_text_layout = (RelativeLayout) view.findViewById(R.id.work_order_customer_field_text);
                    TextView fieldName = (TextView) view.findViewById(R.id.work_order_customer_field_text_lable);
                    final TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    EditText fieldValue = (EditText) view.findViewById(R.id.work_order_customer_field_text_content);
                    EditText numberContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_number);
                    numberContent.setFilters(new InputFilter[]{new SobotDecimalDigitsInputFilter()});
                    numberContent.setHint(R.string.sobot_wo_input_hint);
                    fieldValue.setHint(R.string.sobot_wo_input_hint);
                    final EditText singleContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_single);
                    ImageView fieldImg = (ImageView) view.findViewById(R.id.work_order_customer_field_text_img);

                    if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SINGLE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.VISIBLE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        singleContent.setHint(R.string.sobot_wo_input_hint);
                        //只能读，不可以编辑
                        if (isEdit == 2 && cusFieldConfig.getAuthStatus() == 2) {
                            singleContent.setEnabled(false);
                        }
                        singleContent.setSingleLine(true);
                        singleContent.setMaxEms(11);
                        singleContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            singleContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            singleContent.setEnabled(false);
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_MORE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.GONE);
                        fieldMoreName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        singleContent.setHint(R.string.sobot_wo_input_hint);
                        //只能读，不可以编辑
                        if (isEdit == 2 && cusFieldConfig.getAuthStatus() == 2) {
                            moreContent.setEnabled(false);
                        }
                        moreContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                        //设置EditText的显示方式为多行文本输入
                        moreContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        //文本显示的位置在EditText的最上方
                        moreContent.setGravity(Gravity.TOP);
                        //改变默认的单行模式
                        moreContent.setSingleLine(false);
                        //水平滚动设置为False
                        moreContent.setHorizontallyScrolling(false);

                        if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            moreContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            moreContent.setEnabled(false);
                        }
                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(SobotDateUtil.getDateByString(cusFieldList.get(i).getFieldValue()));
                        }
                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    String content = textClick.getText().toString();
                                    Date date = null;
                                    if (!TextUtils.isEmpty(content)) {
                                        date = com.sobot.utils.SobotDateUtil.parse(content, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? SobotDateUtil.DATE_FORMAT2 : SobotDateUtil.DATE_FORMAT0);
                                    }
                                    SobotKeyboardUtil.hideKeyboard(textClick);
                                    SobotDateUtil.openTimePickerView(context, textClick, date, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1);
                                }
                            });
                        }
                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_TIME_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            //转成时间
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    String content = textClick.getText().toString();
                                    Date date = null;
                                    if (!TextUtils.isEmpty(content)) {
                                        date = com.sobot.utils.SobotDateUtil.parse(content, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? SobotDateUtil.DATE_FORMAT2 : SobotDateUtil.DATE_FORMAT0);
                                    }

                                    SobotKeyboardUtil.hideKeyboard(textClick);
                                    SobotDateUtil.openTimePickerView(context, textClick, date, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1);
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_NUMBER_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.VISIBLE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        numberContent.setSingleLine(true);
                        numberContent.setHint(R.string.sobot_wo_input_hint);
                        numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                        if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            numberContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            numberContent.setEnabled(false);
                        }
                        //只能读，不可以编辑
                        if (isEdit == 2 && cusFieldConfig.getAuthStatus() == 2) {
                            numberContent.setEnabled(false);
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SPINNER_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        //下拉
                        if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldDataValue()) && cusFieldList.get(i).getCusFieldDataInfoList() != null) {
                            //已选的值
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            for (int j = 0; j < dataInfoList.size(); j++) {
                                if (cusFieldList.get(i).getFieldDataValue().equals(dataInfoList.get(j).getDataValue())) {
                                    textClick.setText(dataInfoList.get(j).getDataName());
                                }
                            }
                        } else if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            //预设文案
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            for (int j = 0; j < dataInfoList.size(); j++) {
                                if (cusFieldList.get(i).getFieldValue().equals(dataInfoList.get(j).getDataValue())) {
                                    textClick.setText(dataInfoList.get(j).getDataName());
                                }
                            }
                        }
                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    startCategorySmallActivity(cusFieldConfig);
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        //级联
                        if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldDataValue()) && cusFieldList.get(i).getCusFieldDataInfoList() != null) {
                            String[] fields = cusFieldList.get(i).getFieldDataValue().split(",");
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            StringBuilder text = new StringBuilder("");
                            for (int k = 0; k < fields.length; k++) {
                                for (int j = 0; j < dataInfoList.size(); j++) {
                                    if (fields[k].equals(dataInfoList.get(j).getDataValue())) {
                                        if (text.length() > 0) {
                                            text.append(",");
                                        }
                                        text.append(dataInfoList.get(j).getDataName());
                                    }
                                }
                            }
                            textClick.setText(text.toString());
                        } else if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            String[] fields = cusFieldList.get(i).getFieldValue().split(",");
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            StringBuilder text = new StringBuilder("");
                            for (int k = 0; k < fields.length; k++) {
                                for (int j = 0; j < dataInfoList.size(); j++) {
                                    if (fields[k].equals(dataInfoList.get(j).getDataValue())) {
                                        if (text.length() > 0) {
                                            text.append(",");
                                        }
                                        text.append(dataInfoList.get(j).getDataName());
                                    }
                                }
                            }
                            textClick.setText(text.toString());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    startCascadeActivity(cusFieldConfig);
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_RADIO_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldDataValue()) && cusFieldList.get(i).getCusFieldDataInfoList() != null) {
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            for (int j = 0; j < dataInfoList.size(); j++) {
                                if (cusFieldList.get(i).getFieldDataValue().equals(dataInfoList.get(j).getDataValue())) {
                                    textClick.setText(dataInfoList.get(j).getDataName());
                                }
                            }
                        } else if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            for (int j = 0; j < dataInfoList.size(); j++) {
                                if (cusFieldList.get(i).getFieldValue().equals(dataInfoList.get(j).getDataValue())) {
                                    textClick.setText(dataInfoList.get(j).getDataName());
                                }
                            }
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    startCategorySmallActivity(cusFieldConfig);
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color='#F5222D'>&nbsp;*</font>"));
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        //复选
                        if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldDataValue()) && cusFieldList.get(i).getCusFieldDataInfoList() != null) {
                            String[] fields = cusFieldList.get(i).getFieldDataValue().split(",");
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            StringBuilder text = new StringBuilder("");
                            for (int k = 0; k < fields.length; k++) {
                                for (int j = 0; j < dataInfoList.size(); j++) {
                                    if (fields[k].equals(dataInfoList.get(j).getDataValue())) {
                                        if (text.length() > 0) {
                                            text.append(",");
                                        }
                                        text.append(dataInfoList.get(j).getDataName());
                                    }
                                }
                            }
                            textClick.setText(text.toString());
                        } else if (!SobotStringUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            String[] fields = cusFieldList.get(i).getFieldValue().split(",");
                            List<SobotCusFieldDataInfoList> dataInfoList = cusFieldList.get(i).getCusFieldDataInfoList();
                            StringBuilder text = new StringBuilder("");
                            for (int k = 0; k < fields.length; k++) {
                                for (int j = 0; j < dataInfoList.size(); j++) {
                                    if (fields[k].equals(dataInfoList.get(j).getDataValue())) {
                                        if (text.length() > 0) {
                                            text.append(",");
                                        }
                                        text.append(dataInfoList.get(j).getDataName());
                                    }
                                }
                            }
                            textClick.setText(text.toString());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    startCategorySmallActivity(cusFieldConfig);
                                }
                            });
                        }
                    }
                    containerLayout.addView(view);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == sobot_negativeButton) {
            //关闭
            finish();
        } else if (v == bt_save_commit) {
            setCustomerFieldValue();
            if (checkCusFieldCloseRequired()) {
                //所有关单必填字段都必须填写了才能提交返回
                return;
            }
            SobotSoftInputManage.hideInputMode(SobotCloseWoFieldsActivity.this);
            //保存提交
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("cusFieldList", cusFieldList);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void setCustomerFieldValue() {
        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                if (cusFieldList.get(i).getOpenFlag() != 1) {
                    continue;
                }
                View view = work_order_customer_field_list_layout.findViewWithTag(cusFieldList.get(i).getFieldId());
                if (view != null) {
                    if (cusFieldList.get(i).getFieldType() == 1) {
                        EditText singleLine = (EditText) view.findViewById(R.id.work_order_customer_field_text_single);
                        cusFieldList.get(i).setFieldValue(singleLine.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 2) {
                        EditText moreContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_more_content);
                        cusFieldList.get(i).setFieldValue(moreContent.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 3) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 4) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 5) {
                        EditText numberContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_number);
                        cusFieldList.get(i).setFieldValue(numberContent.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 6) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 7) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 8) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 9) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    }
                }
            }
        }
    }

    public void startCategorySmallActivity(SobotCusFieldConfig cusFieldConfig) {
        SobotSoftInputManage.hideInputMode(SobotCloseWoFieldsActivity.this);
        mCusFieldConfig = cusFieldConfig;
        Intent intent = new Intent(this, SobotCategorySmallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", mCusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, SobotConstantUtils.sobot_wo_create_type_category_small);
    }

    public void startCascadeActivity(SobotCusFieldConfig cusFieldConfig) {
        SobotSoftInputManage.hideInputMode(SobotCloseWoFieldsActivity.this);
        mCusFieldConfig = cusFieldConfig;
        Intent intent = new Intent(this, SobotWOCascadeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", mCusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, SobotConstantUtils.sobot_wo_create_type_category_small);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if ("CATEGORYSMALL".equals(data.getStringExtra("CATEGORYSMALL")) && mCusFieldConfig != null) {
                if (-1 != data.getIntExtra("fieldType", -1) && SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE == data.getIntExtra("fieldType", -1)) {
                    String value = data.getStringExtra("category_typeName");
                    String dataValue = data.getStringExtra("category_typeValue");
                    if (value != null && dataValue != null) {
                        for (int i = 0; i < cusFieldList.size(); i++) {
                            if (mCusFieldConfig.getFieldId().equals(cusFieldList.get(i).getFieldId())) {
                                if (value.length() > 0) {
                                    cusFieldList.get(i).setFieldValue(value.substring(0, value.length() - 1));
                                } else {
                                    cusFieldList.get(i).setFieldValue(value);
                                }
                                if (dataValue.length() > 0) {
                                    cusFieldList.get(i).setFieldDataValue(dataValue.substring(0, dataValue.length() - 1));
                                } else {
                                    cusFieldList.get(i).setFieldDataValue(dataValue);
                                }
                                View view = work_order_customer_field_list_layout.findViewWithTag(cusFieldList.get(i).getFieldId());
                                TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                                textClick.setText(cusFieldList.get(i).getFieldValue());
                                break;
                            }
                        }
                    }
                } else {
                    if (mCusFieldConfig.getFieldId().equals(data.getStringExtra("category_fieldId"))) {
                        for (int i = 0; i < cusFieldList.size(); i++) {
                            if (mCusFieldConfig.getFieldId().equals(cusFieldList.get(i).getFieldId())) {
                                cusFieldList.get(i).setFieldValue(data.getStringExtra("category_typeName"));
                                cusFieldList.get(i).setFieldDataValue(data.getStringExtra("category_typeValue"));
                                View view = work_order_customer_field_list_layout.findViewWithTag(cusFieldList.get(i).getFieldId());
                                TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                                textClick.setText(cusFieldList.get(i).getFieldValue());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
