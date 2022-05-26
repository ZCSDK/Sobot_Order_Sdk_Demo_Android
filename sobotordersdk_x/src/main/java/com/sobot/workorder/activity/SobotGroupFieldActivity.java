package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.workorder.adapter.SobotGroupFieldAdapter;
import com.sobot.workorder.adapter.SobotGroupItemFieldAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotSoftInputManage;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.model.SobotCombinFormFieldModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfigModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoListModel;
import com.sobot.workorderlibrary.api.model.SobotGroupFieldItemModel;
import com.sobot.workorderlibrary.api.model.SobotGroupFieldModel;
import com.sobot.workorderlibrary.api.model.SobotListTypeModel;

import java.util.ArrayList;
import java.util.List;

//组合字段-下拉列表类型的界面
public class SobotGroupFieldActivity extends SobotWOBaseActivity implements View.OnClickListener {

    private List<SobotCusFieldDataInfoListModel> infoLists = new ArrayList<>();
    private List<SobotGroupFieldItemModel> adapterList = new ArrayList<>();
    private SobotCusFieldConfigModel cusFieldConfig;
    private SobotCombinFormFieldModel formField;
    private SobotGroupFieldAdapter adapter;
    private SobotGroupItemFieldAdapter itemAdapter;
    private String group_dataId;
    private List<SobotGroupFieldItemModel> listItemData = new ArrayList<>();
    private boolean flag;

    View viewLine;
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
        return SobotResourceUtils.getResLayoutId(getSobotBaseContext(), "sobot_activity_wo_category_small");
    }

    @Override
    protected void initView() {
        viewLine = findViewById(getResId("order_category_small_line"));
        mListView = findViewById(getResId("rcv_order_category_small"));
        custom_field_btnCancle = findViewById(getResId("custom_field_btnCancle"));
        custom_field_top = findViewById(getResId("custom_field_top"));
        custom_field_etSearch = findViewById(getResId("custom_field_etSearch"));
        loading_layout = findViewById(getResId("loading_layout"));
        loading_layout.showContent();

        Intent intent = getIntent();
        cusFieldConfig = (SobotCusFieldConfigModel) intent.getSerializableExtra("cusFieldConfig");
        formField = (SobotCombinFormFieldModel) intent.getSerializableExtra("formField");
        group_dataId = intent.getStringExtra("group_dataId");
        flag = intent.getBooleanExtra("flag", false);
        if (TextUtils.isEmpty(group_dataId)) {
            infoLists = (List<SobotCusFieldDataInfoListModel>) intent.getSerializableExtra("cusFieldDataInfoLists");
        }

        if (formField != null && 1 == formField.getQueryFlag()) {
            custom_field_top.setVisibility(View.VISIBLE);
        } else {
            custom_field_top.setVisibility(View.GONE);
        }

        if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE != formField.getFieldType()) {
            custom_field_etSearch.setOnClickListener(this);
        }

        custom_field_btnCancle.setOnClickListener(this);
        mListView.setLayoutManager(new LinearLayoutManager(SobotGroupFieldActivity.this));
    }

    @Override
    protected void initData() {
        if (cusFieldConfig != null && infoLists != null && infoLists.size() != 0) {
            for (int i = 0; i < infoLists.size(); i++) {
                if (!TextUtils.isEmpty(cusFieldConfig.getTemDataId()) && cusFieldConfig.getTemDataId().equals(infoLists.get(i).getDataId())) {
                    infoLists.get(i).setChecked(true);
                }
            }

            if (adapter == null) {
                adapter = new SobotGroupFieldAdapter(SobotGroupFieldActivity.this, infoLists);
                mListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            adapter.setItemClickListener(new SobotGroupFieldAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(SobotCusFieldDataInfoListModel cusFieldDataInfoListModel, int position) {
                    SobotListTypeModel msg = new SobotListTypeModel();
                    if (infoLists != null && infoLists.size() != 0) {
                        infoLists.get(position).setChecked(true);
                        for (int i = 0; i < infoLists.size(); i++) {
                            if (i != position) {
                                infoLists.get(i).setChecked(false);
                            }
                        }

                        msg.setFieldValueId(infoLists.get(position).getDataId());
                        msg.setTmpId(infoLists.get(position).getTmpData());
                        msg.setFieldId(formField.getFieldId());
                        msg.setValue(infoLists.get(position).getDataValue());
                        msg.setText(infoLists.get(position).getDataName());
                        msg.setPosition(position);
                        msg.setChangeValue(infoLists.get(position).getOptionValue());
                        msg.setFlag(flag);

                    }
                    SobotSoftInputManage.hideInputMode(SobotGroupFieldActivity.this);
                    Intent resultIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("EventMsgModel", msg);
                    resultIntent.putExtras(bundle);
                    setResult(SobotConstantUtils.sobot_wo_create_type_group_field, resultIntent);
                    finish();
                }
            });
        } else if (!TextUtils.isEmpty(group_dataId)) {
            zhiChiApi.getCusFieldDataInfoByDataId(SobotGroupFieldActivity.this, group_dataId, new SobotResultCallBack<SobotGroupFieldModel>() {
                @Override
                public void onSuccess(SobotGroupFieldModel groupFieldModel) {
                    if (groupFieldModel != null && groupFieldModel.getItems() != null && groupFieldModel.getItems().size() > 0) {
                        mListView.setVisibility(View.VISIBLE);
                        viewLine.setVisibility(View.VISIBLE);
                        listItemData = groupFieldModel.getItems();
                        for (int i = 0; i < listItemData.size(); i++) {
                            if (group_dataId.equals(listItemData.get(i).getpId())) {
                                adapterList.add(listItemData.get(i));
                            }
                        }

                        if (adapterList.size() > 0) {
                            if (itemAdapter == null) {
                                itemAdapter = new SobotGroupItemFieldAdapter(getSobotBaseContext(), adapterList);
                                mListView.setAdapter(itemAdapter);
                            } else {
                                itemAdapter.notifyDataSetChanged();
                            }
                            itemAdapter.setItemClickListener(new SobotGroupItemFieldAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(SobotGroupFieldItemModel cusFieldDataInfoListModel, int position) {
                                    SobotListTypeModel msg = new SobotListTypeModel();
                                    if (adapterList != null && adapterList.size() > 0) {
                                        msg.setTmpId(adapterList.get(position).getTmpData());
                                        msg.setFieldId(formField.getFieldId());
                                        msg.setValue(adapterList.get(position).getId());
                                        msg.setFlag(flag);
                                        msg.setChangeValue(adapterList.get(position).getNumerical());
                                        if (!TextUtils.isEmpty(adapterList.get(position).getText())) {
                                            msg.setText(adapterList.get(position).getText());
                                        }
                                        msg.setPosition(position);
                                        for (int i = 0; i < adapterList.size(); i++) {
                                            adapterList.get(i).setChecked(false);
                                        }
                                        adapterList.get(position).setChecked(true);
                                        msg.setList(adapterList);
                                    }
                                    SobotSoftInputManage.hideInputMode(SobotGroupFieldActivity.this);
                                    Intent resultIntent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("EventMsgModel", msg);
                                    resultIntent.putExtras(bundle);
                                    setResult(SobotConstantUtils.sobot_wo_create_type_group_field, resultIntent);
                                    finish();
                                }
                            });
                        }
                    } else {
                        loading_layout.showEmpty();
                        mListView.setVisibility(View.GONE);
                        viewLine.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string"):des , SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }
}