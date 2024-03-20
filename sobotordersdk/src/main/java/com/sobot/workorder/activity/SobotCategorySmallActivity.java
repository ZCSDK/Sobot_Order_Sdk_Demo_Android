package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotCategorySmallAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotSoftInputManage;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfig;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;

import java.util.ArrayList;
import java.util.List;

//工单自定义字段 - 下拉列表类型界面
public class SobotCategorySmallActivity extends SobotWOBaseActivity implements View.OnClickListener {
    private int fieldType;
    private SobotCusFieldConfig cusFieldConfig;
    private List<SobotCusFieldDataInfoList> infoLists = new ArrayList<>();
    private List<SobotCusFieldDataInfoList> tempInfoLists = new ArrayList<>();
    private SobotCategorySmallAdapter adapter;
    private StringBuffer dataName = new StringBuffer();
    private StringBuffer fieldId = new StringBuffer();
    private StringBuffer dataValue = new StringBuffer();

    RecyclerView mListView;
    Button custom_field_btnCancle;
    RelativeLayout custom_field_top;
    public EditText custom_field_etSearch;
    public SobotLoadingLayout loading_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_wo_category_small;
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.rcv_order_category_small);
        custom_field_btnCancle = findViewById(R.id.custom_field_btnCancle);
        custom_field_top = findViewById(R.id.custom_field_top);
        custom_field_etSearch = findViewById(R.id.custom_field_etSearch);
        loading_layout = findViewById(R.id.loading_layout);
        loading_layout.showContent();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle == null) {
            finish();
        }
        fieldType = bundle.getInt("fieldType");
        if (bundle.getSerializable("cusFieldConfig") != null) {
            cusFieldConfig = (SobotCusFieldConfig) bundle.getSerializable("cusFieldConfig");
        }


        if (cusFieldConfig != null && 1 == cusFieldConfig.getQueryFlag() && cusFieldConfig.getFieldType() == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SPINNER_TYPE) {
            custom_field_top.setVisibility(View.VISIBLE);
        } else {
            custom_field_top.setVisibility(View.GONE);
        }

        if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE != fieldType) {
            custom_field_etSearch.setOnClickListener(this);
        }

        custom_field_btnCancle.setOnClickListener(this);
        mListView.setLayoutManager(new LinearLayoutManager(SobotCategorySmallActivity.this));
        searchListener();
    }

    @Override
    protected void initData() {
        if (cusFieldConfig != null && !TextUtils.isEmpty(cusFieldConfig.getFieldName())) {
            setTitle(cusFieldConfig.getFieldName());
        }

        if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
            showRightMenu(0, getResString("sobot_wo_commit_string"), true, this);
        }

        if (cusFieldConfig != null) {
            infoLists = cusFieldConfig.getCusFieldDataInfoList();
            if (infoLists != null && infoLists.size() != 0) {
                for (int i = 0; i < infoLists.size(); i++) {
                    if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
                        String tmpData[];
                        if (!TextUtils.isEmpty(cusFieldConfig.getFieldValue())) {
                            cusFieldConfig.setFieldValue(cusFieldConfig.getFieldValue() + ",");
                            tmpData = convertStrToArray(cusFieldConfig.getFieldValue());
                            if (tmpData != null && tmpData.length != 0) {
                                if (tmpData.length == 1) {
                                    if (tmpData[0].equals(infoLists.get(i).getDataName())) {
                                        infoLists.get(i).setChecked(true);
                                    }
                                } else {
                                    for (String aTmpData : tmpData) {
                                        if (aTmpData.equals(infoLists.get(i).getDataName())) {
                                            infoLists.get(i).setChecked(true);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(cusFieldConfig.getFieldValue()) && cusFieldConfig.getFieldValue().equals(infoLists.get(i).getDataName())) {
                            infoLists.get(i).setChecked(true);
                        }
                    }
                }

                if (adapter == null) {
                    adapter = new SobotCategorySmallAdapter(SobotCategorySmallActivity.this, infoLists, fieldType);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                adapter.setItemClickListener(new SobotCategorySmallAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(SobotCusFieldDataInfoList cusFieldDataInfoListModel, int position) {
                        if (cusFieldDataInfoListModel != null) {
                            if (fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE) {
                                dataName.delete(0, dataName.length());
                                dataValue.delete(0, dataValue.length());
                                fieldId.delete(0, fieldId.length());
                                if (cusFieldDataInfoListModel.isChecked()) {
                                    cusFieldDataInfoListModel.setChecked(false);
                                } else {
                                    cusFieldDataInfoListModel.setChecked(true);
                                }

                                for (int i = 0; i < infoLists.size(); i++) {
                                    if (infoLists.get(i).isChecked()) {
                                        dataName.append(infoLists.get(i).getDataName()).append(",");
                                        fieldId.append(infoLists.get(i).getFieldId()).append(",");
                                        dataValue.append(infoLists.get(i).getDataValue()).append(",");
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Intent intent = new Intent();
                                intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
                                intent.putExtra("fieldType", fieldType);

                                cusFieldDataInfoListModel.setChecked(true);
                                for (int i = 0; i < infoLists.size(); i++) {
                                    if (i != position) {
                                        infoLists.get(i).setChecked(false);
                                    }
                                }
                                intent.putExtra("category_typeName", cusFieldDataInfoListModel.getDataName());
                                intent.putExtra("category_fieldId", cusFieldDataInfoListModel.getFieldId());
                                intent.putExtra("category_typeValue", cusFieldDataInfoListModel.getDataValue());
                                setResult(SobotConstantUtils.sobot_wo_list_display_type_category, intent);
                                adapter.notifyDataSetChanged();
                                SobotSoftInputManage.hideInputMode(SobotCategorySmallActivity.this);
                                finish();
                            }
                        }
                    }
                });
            }
        }
    }

    // 使用String的split 方法把字符串截取为字符串数组
    private String[] convertStrToArray(String str) {
        String[] strArray;
        strArray = str.split(","); // 拆分字符为"," ,然后把结果交给数组strArray
        return strArray;
    }

    private void searchListener() {
        custom_field_etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchStr, int start, int before, int count) {
                if (adapter != null) {
                    adapter.setSearchText(searchStr.toString());
                }
                List<SobotCusFieldDataInfoList> tmpList = new ArrayList<>();
                tmpList.clear();
                if (tempInfoLists != null && tempInfoLists.size() > 0) {
                    infoLists.clear();
                    infoLists.addAll(tempInfoLists);
                }

                for (int i = 0; i < infoLists.size(); i++) {
                    if (infoLists.get(i).getDataName().contains(searchStr)) {
                        tmpList.add(infoLists.get(i));
                    }
                }

                if (tmpList != null && tmpList.size() > 0) {
                    tempInfoLists.clear();
                    tempInfoLists.addAll(infoLists);
                    infoLists.clear();
                    infoLists.addAll(tmpList);
                    adapter.notifyDataSetChanged();
                    loading_layout.showContent();
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    if (!TextUtils.isEmpty(custom_field_etSearch.getText().toString())) {
                        loading_layout.showEmpty();
                        mListView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        custom_field_etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (TextUtils.isEmpty(custom_field_etSearch.getText())) {
                        mListView.setVisibility(View.VISIBLE);
                        if (tempInfoLists != null && tempInfoLists.size() > 0) {
                            infoLists.clear();
                            infoLists.addAll(tempInfoLists);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == getRightMenu().getId()) {
            Intent intent = new Intent();
            intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
            intent.putExtra("fieldType", fieldType);
            intent.putExtra("category_typeName", dataName + "");
            intent.putExtra("category_typeValue", dataValue + "");
            intent.putExtra("category_fieldId", fieldId + "");
            setResult(SobotConstantUtils.sobot_wo_list_display_type_category, intent);
            finish();
        }
    }
}