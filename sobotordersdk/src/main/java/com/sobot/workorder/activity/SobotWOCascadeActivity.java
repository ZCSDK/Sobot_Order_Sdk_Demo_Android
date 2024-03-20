package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotCascadeAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotSoftInputManage;
import com.sobot.workorderlibrary.api.model.SobotCombinFormFieldModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfig;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//工单自定义字段 - 级联类型界面
public class SobotWOCascadeActivity extends SobotWOBaseActivity {
    private int fieldType;
    private SobotCusFieldConfig cusFieldConfig;
    private List<SobotCusFieldDataInfoList> infoLists = new ArrayList<>();//原始数据，多级
    private List<SobotCusFieldDataInfoList> tempInfoLists = new ArrayList<>();//当前这级别
    private LinkedList<SobotCusFieldDataInfoList> selectInfoLists = new LinkedList<>();//选中的，从第一级到最后一级（xx省xx市xx县）
    private SobotCascadeAdapter adapter;

    RecyclerView mListView;
    public SobotLoadingLayout loading_layout;
    private LinearLayout horizontalScrollView_ll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_wo_cascade;
    }

    @Override
    protected void initView() {
        horizontalScrollView_ll = findViewById(R.id.horizontalScrollView_ll);
        mListView = findViewById(R.id.rcv_order_category_small);
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


        mListView.setLayoutManager(new LinearLayoutManager(SobotWOCascadeActivity.this));
    }

    @Override
    protected void initData() {
        if (cusFieldConfig != null && !TextUtils.isEmpty(cusFieldConfig.getFieldName())) {
            setTitle(cusFieldConfig.getFieldName());
        }
        SobotCusFieldDataInfoList cusFieldDataInfoListModel = new SobotCusFieldDataInfoList();
        cusFieldDataInfoListModel.setDataName(getResString("sobot_all_string"));
        selectInfoLists.add(cusFieldDataInfoListModel);
        updateIndicator();
        if (cusFieldConfig != null) {
            infoLists = cusFieldConfig.getCusFieldDataInfoList();
            if (infoLists != null && infoLists.size() != 0) {
                tempInfoLists.clear();
                for (int i = 0; i < infoLists.size(); i++) {
                    //一进入界面，只显示第一级
                    if (TextUtils.isEmpty(infoLists.get(i).getParentDataId())) {
                        tempInfoLists.add(infoLists.get(i));
                    }
                }
                for (int j = 0; j < tempInfoLists.size(); j++) {
                    for (int x = 0; x < infoLists.size(); x++) {
                        if (tempInfoLists.get(j).getDataId().equals(infoLists.get(x).getParentDataId())) {
                            tempInfoLists.get(j).setHasNewNode(true);
                            break;
                        }
                    }
                }

                if (adapter == null) {
                    adapter = new SobotCascadeAdapter(SobotWOCascadeActivity.this, tempInfoLists);
                    mListView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
                adapter.setItemClickListener(new SobotCascadeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(SobotCusFieldDataInfoList cusFieldDataInfoListModel, int position) {
                        if (cusFieldDataInfoListModel != null) {
                            tempInfoLists.clear();
                            for (int i = 0; i < infoLists.size(); i++) {
                                //显示下一级
                                if (infoLists.get(i).getParentDataId().equals(cusFieldDataInfoListModel.getDataId()) && !TextUtils.isEmpty(infoLists.get(i).getDataId())) {
                                    tempInfoLists.add(infoLists.get(i));
                                }
                            }
                            if (tempInfoLists.size() == 0) {
                                if (fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE) {
                                    //最后一级
                                    Intent intent = new Intent();
                                    intent.putExtra("CATEGORYSMALL", "CATEGORYSMALL");
                                    intent.putExtra("fieldType", fieldType);

                                    cusFieldDataInfoListModel.setChecked(true);
                                    StringBuffer dataNames = new StringBuffer();
                                    StringBuffer dataValues = new StringBuffer();
                                    for (int i = 0; i < selectInfoLists.size(); i++) {
                                        if (!TextUtils.isEmpty(selectInfoLists.get(i).getDataValue())) {
                                            dataNames.append(selectInfoLists.get(i).getDataName()).append(",");
                                            dataValues.append(selectInfoLists.get(i).getDataValue()).append(",");
                                        }
                                    }
                                    dataNames.append(cusFieldDataInfoListModel.getDataName());
                                    dataValues.append(cusFieldDataInfoListModel.getDataValue());
                                    intent.putExtra("category_typeName", dataNames.toString());
                                    intent.putExtra("category_fieldId", cusFieldConfig.getFieldId());
                                    intent.putExtra("category_typeValue", dataValues.toString());
                                    setResult(SobotConstantUtils.sobot_wo_list_display_type_category, intent);
                                    adapter.notifyDataSetChanged();
                                    SobotSoftInputManage.hideInputMode(SobotWOCascadeActivity.this);
                                    finish();
                                } else {
                                    //组合字段中级联类型 最后一级
                                    Intent intent = new Intent();
                                    intent.putExtra("fieldType", fieldType);

                                    cusFieldDataInfoListModel.setChecked(true);
                                    StringBuffer dataNames = new StringBuffer();
                                    StringBuffer dataValues = new StringBuffer();
                                    for (int i = 0; i < selectInfoLists.size(); i++) {
                                        if (!TextUtils.isEmpty(selectInfoLists.get(i).getDataValue())) {
                                            dataNames.append(selectInfoLists.get(i).getDataName()).append(",");
                                            dataValues.append(selectInfoLists.get(i).getDataValue()).append(",");
                                        }
                                    }
                                    dataNames.append(cusFieldDataInfoListModel.getDataName());
                                    dataValues.append(cusFieldDataInfoListModel.getDataValue());
                                    intent.putExtra("category_typeName", dataNames.toString());
                                    intent.putExtra("category_fieldId", cusFieldConfig.getFieldId());
                                    intent.putExtra("category_typeValue", dataValues.toString());
                                    Bundle bundle = getIntent().getBundleExtra("bundle");
                                    if (bundle != null) {
                                        if (bundle != null && bundle.getSerializable("cusFieldConfig") != null) {
                                            SobotCombinFormFieldModel combinFormFieldModel = (SobotCombinFormFieldModel) bundle.getSerializable("formField");
                                            Bundle bundle1 = new Bundle();
                                            bundle1.putSerializable("formField", combinFormFieldModel);
                                            intent.putExtras(bundle1);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                    SobotSoftInputManage.hideInputMode(SobotWOCascadeActivity.this);
                                    setResult(SobotConstantUtils.sobot_wo_create_type_group_field, intent);
                                    finish();
                                }
                            } else {
                                for (int j = 0; j < tempInfoLists.size(); j++) {
                                    for (int x = 0; x < infoLists.size(); x++) {
                                        if (tempInfoLists.get(j).getDataId().equals(infoLists.get(x).getParentDataId())) {
                                            tempInfoLists.get(j).setHasNewNode(true);
                                            break;
                                        }
                                    }
                                }
                                selectInfoLists.add(cusFieldDataInfoListModel);
                                updateIndicator();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        }
    }

    private void updateIndicator() {
        horizontalScrollView_ll.removeAllViews();
        for (int i = 0; i < selectInfoLists.size(); i++) {
            View view = LayoutInflater.from(getSobotBaseContext()).inflate(R.layout.sobot_layout_order_category_header_items, null);
            if (view != null) {
                TextView titleTv = view.findViewById(R.id.work_order_category_title);
                titleTv.setText(selectInfoLists.get(i).getDataName());
                horizontalScrollView_ll.addView(view);
                final int position = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position != (selectInfoLists.size() - 1)) {
                            tempInfoLists.clear();
                            for (int j = 0; j < infoLists.size(); j++) {
                                if (position == 0) {
                                    if (TextUtils.isEmpty(infoLists.get(j).getParentDataId())) {
                                        tempInfoLists.add(infoLists.get(j));
                                    }
                                } else {
                                    //显示下一级
                                    if (infoLists.get(j).getParentDataId().equals(selectInfoLists.get(position).getDataId())) {
                                        tempInfoLists.add(infoLists.get(j));
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            int deleteNum = horizontalScrollView_ll.getChildCount() - (position + 1);
                            for (int m = 0; m < deleteNum; m++) {
                                selectInfoLists.removeLast();
                            }
                            updateIndicator();
                        }
                    }
                });
            }
        }

        if (horizontalScrollView_ll.getChildCount() > 0) {
            for (int i = 0; i < horizontalScrollView_ll.getChildCount(); i++) {
                View view = horizontalScrollView_ll.getChildAt(i);
                TextView titleTv = view.findViewById(R.id.work_order_category_title);
                View line = view.findViewById(R.id.work_order_category_line);
                if (i == (horizontalScrollView_ll.getChildCount() - 1)) {
                    titleTv.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_wenzi_gray1")));
                    line.setVisibility(View.VISIBLE);
                } else {
                    titleTv.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_wenzi_gray2")));
                    line.setVisibility(View.GONE);
                }
            }
        }

    }
}