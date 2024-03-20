package com.sobot.workorder.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.gson.SobotGsonUtil;
import com.sobot.gson.reflect.TypeToken;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.ui.toast.SobotToastUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.weight.dialog.CusFieldDuoXuanDialog;
import com.sobot.workorder.weight.dialog.CusFieldSelectDialog;
import com.sobot.workorder.weight.dialog.CusFieldjilianDialog;
import com.sobot.workorder.weight.dialog.DialogItemOnClick;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfig;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;
import com.sobot.workorderlibrary.api.model.SobotTemplateFieldEntity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomFieldsUtils {
    public static int WORK_ORDER_CUSTOMER_FIELD_SINGLE_LINE_TYPE = 1;//单行文本
    public static int WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE = 2;//多行文本
    public static int WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE = 3;//日期
    public static int WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE = 4;//时间
    public static int WORK_ORDER_CUSTOMER_FIELD_NUMBER_TYPE = 5;//数值
    public static int WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE = 6;//下拉列表
    public static int WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE = 7;//复选框
    public static int WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE = 8;//单选框
    public static int WORK_ORDER_CUSTOMER_FIELD_CASCADE_TYPE = 9;//级联
    public static void showCusFields(final Activity context, final List<SobotCusFieldConfig> cusFieldList, LinearLayout containerLayout) {
        if (containerLayout != null) {
            containerLayout.setVisibility(View.VISIBLE);
            containerLayout.removeAllViews();
            if (cusFieldList != null && cusFieldList.size() != 0) {
                for (int i = 0; i < cusFieldList.size(); i++) {
                    View view = View.inflate(context, R.layout.sobot_item_cusfield_list_view, null);
                    final SobotCusFieldConfig cusFieldConfig = cusFieldList.get(i);
                    if (cusFieldConfig.getOpenFlag() == 1) {
                        view.setTag(cusFieldConfig.getFieldId());
                        TextView call_customer_field_text_lable = view.findViewById(R.id.call_customer_field_text_lable);
                        TextView call_customer_field_text_single = view.findViewById(R.id.call_customer_field_text_single);
                        if(SobotStringUtils.isNoEmpty(cusFieldConfig.getFieldName())) {
                            call_customer_field_text_lable.setVisibility(View.VISIBLE);
                            call_customer_field_text_lable.setText(cusFieldConfig.getFieldName());
                        }else{
                            call_customer_field_text_lable.setVisibility(View.INVISIBLE);
                        }
                        call_customer_field_text_single.setText(!TextUtils.isEmpty(cusFieldConfig.getFieldValue())?cusFieldConfig.getFieldValue():"--");
                        if(cusFieldConfig.isCopy()){
                            ImageView copy = view.findViewById(R.id.iv_field_copy);
                            copy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    copyStr(context,cusFieldConfig.getFieldValue());
                                }
                            });
                            //显示
                        }
                        containerLayout.addView(view);
                    }
                }
            }
        }
    }
    public static void addWorkOrderUserCusFields(final Activity context, final List<SobotCusFieldConfig> cusFieldList, LinearLayout containerLayout) {
        if (containerLayout != null) {
            containerLayout.setVisibility(View.VISIBLE);
            containerLayout.removeAllViews();
            if (cusFieldList != null && cusFieldList.size() != 0) {
                int size = cusFieldList.size();
                for (int i = 0; i < cusFieldList.size(); i++) {
                    final SobotCusFieldConfig cusFieldConfig = cusFieldList.get(i);
                    if (cusFieldConfig.getOpenFlag() != 1) {
                        continue;
                    }
                    View view = View.inflate(context, R.layout.sobot_item_cusfield_list, null);
                    view.setTag(cusFieldConfig.getFieldId());
                    View bottomLine = view.findViewById(R.id.call_customer_field_text_bootom_line);
                    if (cusFieldList.size() == 1 || i == (size - 1)) {
                        bottomLine.setVisibility(View.GONE);
                    } else {
                        bottomLine.setVisibility(View.VISIBLE);
                    }
                    LinearLayout ll_more_text_layout = (LinearLayout) view.findViewById(R.id.call_customer_field_more_relativelayout);

                    LinearLayout ll_group_text_layout = (LinearLayout) view.findViewById(R.id.call_customer_field_group_relativelayout);
                    RelativeLayout ll_group_layout = (RelativeLayout) view.findViewById(R.id.call_customer_field_group__rl_add);
                    ll_group_text_layout.setVisibility(View.GONE);
                    ll_group_layout.setVisibility(View.GONE);

                    TextView fieldMoreName = (TextView) view.findViewById(R.id.call_customer_field_more_text_lable);
                    EditText moreContent = (EditText) view.findViewById(R.id.call_customer_field_text_more_content);

                    RelativeLayout ll_text_layout = (RelativeLayout) view.findViewById(R.id.call_customer_field_text);
                    final TextView fieldName = (TextView) view.findViewById(R.id.call_customer_field_text_lable);
                    final TextView textClick = (TextView) view.findViewById(R.id.call_customer_date_text_click);
                    EditText fieldValue = (EditText) view.findViewById(R.id.call_customer_field_text_content);
                    EditText numberContent = (EditText) view.findViewById(R.id.call_customer_field_text_number);

                    EditText singleContent = (EditText) view.findViewById(R.id.call_customer_field_text_single);
                    ImageView fieldImg = (ImageView) view.findViewById(R.id.call_customer_field_text_img);

                    if (WORK_ORDER_CUSTOMER_FIELD_SINGLE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.VISIBLE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        singleContent.setHint(cusFieldList.get(i).getFieldRemark());
                        singleContent.setHint(R.string.sobot_wo_input_hint);
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
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

                    } else if (WORK_ORDER_CUSTOMER_FIELD_MORE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.GONE);
                        fieldMoreName.setText(cusFieldList.get(i).getFieldName());
                        moreContent.setHint(cusFieldList.get(i).getFieldRemark());
                        moreContent.setHint(R.string.sobot_wo_input_hint);
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldMoreName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
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
                        moreContent.setVerticalScrollBarEnabled(true);

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            moreContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            moreContent.setEnabled(false);
                        }
                    } else if (WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        final String fName = cusFieldList.get(i).getFieldName();
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String content = textClick.getText().toString();
                                    Date date = null;
                                    if (!TextUtils.isEmpty(content)) {
                                        date = SobotDateUtil.parse(content, fieldType == WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? SobotDateUtil.ymd : SobotDateUtil.hm);
                                    }
                                    hideKeyboard(context,textClick);
                                    SobotDateUtil.openTimePickerView(context, textClick, date, fieldType == WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1,fName);
                                }
                            });
                        }
                    } else if (WORK_ORDER_CUSTOMER_FIELD_TIME_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }
                        final String fName = cusFieldList.get(i).getFieldName();
                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String content = textClick.getText().toString();
                                    Date date = null;
                                    if (!TextUtils.isEmpty(content)) {
                                        date = SobotDateUtil.parse(content, fieldType == WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? SobotDateUtil.ymd : SobotDateUtil.hm);
                                    }
                                    hideKeyboard(context,textClick);
                                    SobotDateUtil.openTimePickerView(context, textClick, date, fieldType == WORK_ORDER_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1,fName);
                                }
                            });
                        }

                    } else if (WORK_ORDER_CUSTOMER_FIELD_NUMBER_TYPE == cusFieldList.get(i).getFieldType()) {
                        try {
                            if (!TextUtils.isEmpty(cusFieldList.get(i).getLimitOptions())) {
                                JSONArray array = new JSONArray(cusFieldList.get(i).getLimitOptions());
                                if (array.length() > 0) {
                                    if (array.optInt(0) == 2) {
                                        numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                                    } else if (array.optInt(0) == 3) {
                                        numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.VISIBLE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        numberContent.setSingleLine(true);
                        numberContent.setHint(cusFieldList.get(i).getFieldRemark());
                        numberContent.setHint(R.string.sobot_wo_input_hint);
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            numberContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            numberContent.setEnabled(false);
                        }

                    } else if (WORK_ORDER_CUSTOMER_FIELD_SPINNER_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            final List<SobotCusFieldDataInfoList> clist =cusFieldList.get(i).getCusFieldDataInfoList();
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //下拉列表
                                    if(clist!=null&& clist.size()>0) {
                                        String text = textClick.getText().toString();
                                        int sIndex =-1;
                                        List<String> listStr = new ArrayList<>();
                                        for (int j = 0; j < clist.size(); j++) {
                                            listStr.add(clist.get(j).getDataName());
                                            if(!TextUtils.isEmpty(text) && text.equals(clist.get(j).getDataName())){
                                                sIndex = j;
                                            }
                                        }
                                        showDialog(context, listStr, fieldName.getText().toString(), sIndex, new DialogItemOnClick() {
                                            @Override
                                            public void selectItem(Object selectObj) {
                                                int index = (int) selectObj;
                                                if(index>=0) {
                                                    SobotCusFieldDataInfoList info=clist.get(index);
                                                    textClick.setText(info.getDataName());
                                                    textClick.setTag(info.getDataValue());
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    } else if (WORK_ORDER_CUSTOMER_FIELD_RADIO_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldConfig.getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            final List<SobotCusFieldDataInfoList> clist =cusFieldList.get(i).getCusFieldDataInfoList();
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //单选框
                                    if(clist!=null&& clist.size()>0) {
                                        String text = textClick.getText().toString();
                                        int sIndex =-1;
                                        List<String> listStr = new ArrayList<>();
                                        for (int j = 0; j < clist.size(); j++) {
                                            listStr.add(clist.get(j).getDataName());
                                            if(!TextUtils.isEmpty(text) && text.equals(clist.get(j).getDataName())){
                                                sIndex = j;
                                            }
                                        }
                                        showDialog(context, listStr, fieldName.getText().toString(), sIndex, new DialogItemOnClick() {
                                            @Override
                                            public void selectItem(Object selectObj) {
                                                int index = (int) selectObj;
                                                if(index>=0) {
                                                    SobotCusFieldDataInfoList info=clist.get(index);
                                                    textClick.setText(info.getDataName());
                                                    textClick.setTag(info.getDataValue());
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    } else if (WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        textClick.setHint(R.string.sobot_wo_select_hint);
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            fieldName.setText(Html.fromHtml(cusFieldList.get(i).getFieldName() + "<font color=red>" + " *" + "</font>"));
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }
                        if (cusFieldConfig.getOpenFlag() == 1) {
                            List<SobotCusFieldDataInfoList> finalInfoLists = cusFieldList.get(i).getCusFieldDataInfoList();
                            if(finalInfoLists==null){
                                finalInfoLists = new ArrayList<>();
                            }
                            //重置选中
                            for (int j = 0; j < finalInfoLists.size(); j++) {
                                finalInfoLists.get(j).setChecked(false);
                            }
                            final List<SobotCusFieldDataInfoList> finalInfoLists1 = finalInfoLists;
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int i = 0; i < finalInfoLists1.size(); i++) {
                                        if (!TextUtils.isEmpty(textClick.getText().toString())) {
                                            String[] tmpData = convertStrToArray(textClick.getText().toString());
                                            for (String aTmpData : tmpData) {
                                                if (aTmpData.equals(finalInfoLists1.get(i).getDataName())) {
                                                    finalInfoLists1.get(i).setChecked(true);
                                                }
                                            }
                                        }
                                    }
                                    //复选框
                                    showDuoxuanDialog(context, cusFieldConfig.getFieldName(), finalInfoLists1, new DialogItemOnClick() {
                                        @Override
                                        public void selectItem(Object selectObj) {
                                            StringBuffer dataName = new StringBuffer();
                                            StringBuilder dataValue = new StringBuilder();
                                            List<SobotCusFieldDataInfoList> list = (List<SobotCusFieldDataInfoList>) selectObj;
                                            for (int j = 0; j < list.size(); j++) {
                                                if(dataValue.length()>0){
                                                    dataValue.append(",");
                                                    dataName.append(",");
                                                }
                                                dataName.append(list.get(j).getDataName());
                                                dataValue.append(list.get(j).getDataValue());
                                            }
                                            cusFieldConfig.setFieldValue(dataName.toString());
                                            textClick.setText(dataName.toString());
                                            textClick.setTag(dataValue.toString());
                                        }
                                    });
                                }
                            });
                        }
                    } else if (WORK_ORDER_CUSTOMER_FIELD_CASCADE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            textClick.setHint(textClick.getHint() +"("+ context.getResources().getString(R.string.sobot_required_string)+")");
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            List<SobotCusFieldDataInfoList> finalInfoLists = cusFieldList.get(i).getCusFieldDataInfoList();
                            if(finalInfoLists==null){
                                finalInfoLists = new ArrayList<>();
                            }
                            //重置选中
                            for (int j = 0; j < finalInfoLists.size(); j++) {
                                finalInfoLists.get(j).setChecked(false);
                            }
                            final List<SobotCusFieldDataInfoList> finalInfoLists1 = finalInfoLists;
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (int i = 0; i < finalInfoLists1.size(); i++) {
                                        if (!TextUtils.isEmpty(textClick.getText().toString())) {
                                            String[] tmpData = convertStrToArray(textClick.getText().toString());
                                            for (String aTmpData : tmpData) {
                                                if (aTmpData.equals(finalInfoLists1.get(i).getDataName())) {
                                                    finalInfoLists1.get(i).setChecked(true);
                                                }
                                            }
                                        }
                                    }
                                    //级联
                                    showJilianDialog(context, cusFieldConfig.getFieldName(),finalInfoLists1, new DialogItemOnClick() {
                                        @Override
                                        public void selectItem(Object selectObj) {
                                            StringBuffer dataName = new StringBuffer();
                                            StringBuilder dataValue = new StringBuilder();
                                            List<SobotCusFieldDataInfoList> list = (List<SobotCusFieldDataInfoList>) selectObj;
                                            for (int j = 0; j < list.size(); j++) {
                                                if(dataValue.length()>0){
                                                    dataValue.append(",");
                                                    dataName.append(",");
                                                }
                                                dataName.append(list.get(j).getDataName());
                                                dataValue.append(list.get(j).getDataValue());
                                            }
                                            cusFieldConfig.setFieldValue(dataName.toString());
                                            textClick.setText(dataName.toString());
                                            textClick.setTag(dataValue.toString());
                                        }
                                    });
                                }
                            });
                        }

                    }
                    containerLayout.addView(view);
                }
            }
        }
    }
    /**
     * 单独加归属地
     * @param cusFields
     * @param provinceName
     * @param province
     * @return
     */
    public static List<SobotCusFieldConfig>  templateCheageCofig(final List<SobotTemplateFieldEntity> cusFields, String provinceName, String province) {
        List<SobotCusFieldConfig> cusFieldList = new ArrayList<>();
        if(cusFields!=null && cusFields.size()>0){
            for (int i = 0; i < cusFields.size(); i++) {
                SobotCusFieldConfig cusFieldConfig = new SobotCusFieldConfig();
                if(!TextUtils.isEmpty(cusFields.get(i).getFieldID())) {
                    cusFieldConfig.setFieldId(cusFields.get(i).getFieldID());
                }else if (!TextUtils.isEmpty(cusFields.get(i).getFieldId())){
                    cusFieldConfig.setFieldId(cusFields.get(i).getFieldId());
                }else if (!TextUtils.isEmpty(cusFields.get(i).getTemplateFieldId())){
                    cusFieldConfig.setFieldId(cusFields.get(i).getTemplateFieldId());
                }else {
                    cusFieldConfig.setFieldId(cusFields.get(i).getFieldName());
                }
                cusFieldConfig.setFieldName(cusFields.get(i).getFieldName());
                cusFieldConfig.setFieldValue(cusFields.get(i).getFieldValue());
                cusFieldConfig.setFieldType(cusFields.get(i).getFieldType());
                if(!TextUtils.isEmpty(cusFields.get(i).getFieldData())){
                    ArrayList<SobotCusFieldDataInfoList> list =  SobotGsonUtil.jsonToBeans(cusFields.get(i).getFieldData(), new TypeToken<ArrayList<SobotCusFieldDataInfoList>>() {
                    }.getType());
                    if(null !=list) {
                        cusFieldConfig.setCusFieldDataInfoList(list);
                    }
                }
                cusFieldConfig.setLimitChar(cusFields.get(i).getLimitChar());
                cusFieldConfig.setFieldRemark(cusFields.get(i).getDescription());
                cusFieldConfig.setFillFlag(cusFields.get(i).getIsRequired());
                cusFieldConfig.setCustomFieldFlag(cusFields.get(i).getCustomerFlag()+"");
                cusFieldConfig.setTemplateFieldId(cusFields.get(i).getTemplateFieldId());
                cusFieldConfig.setBasicName(cusFields.get(i).getBasicName());
                cusFieldConfig.setBasicType(cusFields.get(i).getBasicType());
                cusFieldConfig.setOpenFlag(1);
                cusFieldList.add(cusFieldConfig);
                if(!SobotStringUtils.isEmpty(provinceName)&& cusFieldConfig.getFieldId().equals("number")){
                    SobotCusFieldConfig guishudi = new SobotCusFieldConfig();
                    guishudi.setFieldId("guishudi_android");
                    guishudi.setFieldName(provinceName);
                    guishudi.setFieldValue(province);
                    guishudi.setTemplateFieldId("guishudi_android");
                    guishudi.setFieldType(1);
                    guishudi.setOpenFlag(1);
                    cusFieldList.add(guishudi);
                }
            }
        }
        return cusFieldList;
    }
    public static List<SobotCusFieldConfig>  templateCheageCofig(final List<SobotTemplateFieldEntity> cusFields) {
        List<SobotCusFieldConfig> cusFieldList = new ArrayList<>();
        if(cusFields!=null && cusFields.size()>0){
            for (int i = 0; i < cusFields.size(); i++) {
                SobotCusFieldConfig cusFieldConfig = new SobotCusFieldConfig();
                if(!TextUtils.isEmpty(cusFields.get(i).getFieldID())) {
                    cusFieldConfig.setFieldId(cusFields.get(i).getFieldID());
                }else if (!TextUtils.isEmpty(cusFields.get(i).getFieldId())){
                    cusFieldConfig.setFieldId(cusFields.get(i).getFieldId());
                }else if (!TextUtils.isEmpty(cusFields.get(i).getTemplateFieldId())){
                    cusFieldConfig.setFieldId(cusFields.get(i).getTemplateFieldId());
                }else {
                    cusFieldConfig.setFieldId(cusFields.get(i).getFieldName());
                }
                cusFieldConfig.setFieldName(cusFields.get(i).getFieldName());
                cusFieldConfig.setFieldValue(cusFields.get(i).getFieldValue());
                cusFieldConfig.setFieldType(cusFields.get(i).getFieldType());
                if(!TextUtils.isEmpty(cusFields.get(i).getFieldData())){
                    ArrayList<SobotCusFieldDataInfoList> list =  SobotGsonUtil.jsonToBeans(cusFields.get(i).getFieldData(), new TypeToken<ArrayList<SobotCusFieldDataInfoList>>() {
                    }.getType());
                    if(null !=list) {
                        cusFieldConfig.setCusFieldDataInfoList(list);
                    }
                }
                cusFieldConfig.setLimitChar(cusFields.get(i).getLimitChar());
                cusFieldConfig.setFieldRemark(cusFields.get(i).getDescription());
                cusFieldConfig.setFillFlag(cusFields.get(i).getIsRequired());
                cusFieldConfig.setCustomFieldFlag(cusFields.get(i).getCustomerFlag()+"");
                cusFieldConfig.setTemplateFieldId(cusFields.get(i).getTemplateFieldId());
                cusFieldConfig.setBasicName(cusFields.get(i).getBasicName());
                cusFieldConfig.setBasicType(cusFields.get(i).getBasicType());
                cusFieldConfig.setOpenFlag(1);
                cusFieldList.add(cusFieldConfig);
            }
        }
        return cusFieldList;
    }
    public static void hideKeyboard(Context context,View view) {
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(context);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void showKeyboard(Context context,EditText view) {
        if (context == null||view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
    public static void showDialog(Activity context,List<String > listStr,String title,int selectIndex,DialogItemOnClick listener){
        CusFieldSelectDialog dialog = new CusFieldSelectDialog(context,title, listStr,selectIndex , listener);
        dialog.show();
    }
    public static void showJilianDialog(Activity context, String title, List<SobotCusFieldDataInfoList>  list, DialogItemOnClick listener){
        CusFieldjilianDialog dialog = new CusFieldjilianDialog(context, title,list, listener);
        dialog.show();
    }
    public static void showDuoxuanDialog(Activity context, String title, List<SobotCusFieldDataInfoList>  list, DialogItemOnClick listener){
        CusFieldDuoXuanDialog dialog = new CusFieldDuoXuanDialog(context, title,list, listener);
        dialog.show();
    }
    // 使用String的split 方法把字符串截取为字符串数组
    private static String[] convertStrToArray(String str) {
        String[] strArray;
        strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    private static void copyStr(Context context, String copyString) {
        if (Build.VERSION.SDK_INT >= 11) {
            SobotLogUtils.i("API是大于11");
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyString);
            cmb.getText();
        } else {
            SobotLogUtils.i("API是小于11");
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyString);
            cmb.getText();
        }

        SobotToastUtil.showToast(context, context.getResources().getString(R.string.sobot_ctrl_v_success));
    }

}
