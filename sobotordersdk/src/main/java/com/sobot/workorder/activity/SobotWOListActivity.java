package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotWOListAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorderlibrary.api.model.SobotCommonItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 客服列表
 */
public class SobotWOListActivity extends SobotWOBaseActivity implements View.OnClickListener {

    // 适配器的adapter
    private SobotWOListAdapter adapter = null;
    private List<SobotCommonItemModel> list = new ArrayList<>();
    private List<SobotCommonItemModel> searchTempList = new ArrayList<>();
    private ListView mListView;
    private int displayType;
    private String selectedItemValue;
    private ArrayList<SobotCommonItemModel> tmplist;
    private ArrayList<SobotCommonItemModel> firstTmplist = new ArrayList<>();

    public EditText quickReplyListEtSearch;
    ImageView quickReplyListIvDeleteText;


    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_wo_list;
    }

    @Override
    protected void initView() {
        quickReplyListEtSearch = findViewById(R.id.quick_reply_list_etSearch);
        quickReplyListIvDeleteText = findViewById(R.id.quick_reply_list_ivDeleteText);

        quickReplyListIvDeleteText.setOnClickListener(this);
        quickReplyListEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    quickReplyListIvDeleteText.setVisibility(View.VISIBLE);
                } else {
                    quickReplyListIvDeleteText.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tmplist != null) {
                    String tempSearch = quickReplyListEtSearch.getText().toString();
                    searchTempList.clear();
                    if (TextUtils.isEmpty(tempSearch)) {
                        searchTempList.addAll(firstTmplist);
                        searchTempList.addAll(tmplist);
                    } else {
                        for (int i = 0; i < tmplist.size(); i++) {
                            if (tmplist.get(i) != null && tmplist.get(i).getItemKey() != null && tmplist.get(i).getItemKey().contains(tempSearch)) {
                                searchTempList.add(tmplist.get(i));
                            }
                        }
                    }
                    adapter.setData(searchTempList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        mListView = (ListView) findViewById(R.id.xlv_data);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SobotCommonItemModel model = adapter.getmList().get(position);
                if (!TextUtils.isEmpty(selectedItemValue) && !model.getItemValue().endsWith(selectedItemValue)) {
                    for (int i = 0; i < adapter.getmList().size(); i++) {
                        adapter.getmList().get(i).setIsChecked(false);
                    }
                    model.setIsChecked(true);
                    Intent intent = new Intent();
                    intent.putExtra(SobotConstantUtils.sobot_wo_list_selected_item, model);
                    setResult(displayType, intent);
                    adapter.notifyDataSetChanged();
                    finish();
                } else {
                    for (int i = 0; i < adapter.getmList().size(); i++) {
                        adapter.getmList().get(i).setIsChecked(false);
                    }
                    model.setIsChecked(true);
                    Intent intent = new Intent();
                    intent.putExtra(SobotConstantUtils.sobot_wo_list_selected_item, model);
                    setResult(displayType, intent);
                    adapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        tmplist = new ArrayList<>();
        if (intent != null) {
            displayType = intent.getExtras().getInt(SobotConstantUtils.sobot_wo_display_type);
            if (!TextUtils.isEmpty(intent.getStringExtra(SobotConstantUtils.sobot_wo_list_data_selected_value))) {
                selectedItemValue = intent.getStringExtra(SobotConstantUtils.sobot_wo_list_data_selected_value);
            }
            ArrayList<SobotCommonItemModel> tmplist1 = (ArrayList<SobotCommonItemModel>) intent.getSerializableExtra(SobotConstantUtils.sobto_work_order_data);
            if(tmplist1 != null && tmplist1.size()>0){
                tmplist.addAll(tmplist1);
            }
        }
        firstTmplist.clear();
        list.clear();
        switch (displayType) {
            case SobotConstantUtils.sobot_wo_list_display_type_service_group:
                setTitle(getResString("sobot_customer_service_team_string"));
                SobotCommonItemModel serviceGroupModelResult = new SobotCommonItemModel(getResString("sobot_no_select_group_string"), "-1");
                list.add(0, serviceGroupModelResult);
                firstTmplist.add(0, serviceGroupModelResult);
                break;
            case SobotConstantUtils.sobot_wo_list_display_type_service:
                setTitle(getResString("sobot_accept_customer_service_string"));
                SobotCommonItemModel tempBean = new SobotCommonItemModel(getResString("sobot_no_select_service_string"), "-1");
                list.add(0, tempBean);
                firstTmplist.add(0, tempBean);
                break;
            case SobotConstantUtils.sobot_wo_list_display_type_service_copy:
                setTitle(getResString("sobot_select_copy_customer_service_string"));
                SobotCommonItemModel tempBean1 = new SobotCommonItemModel(getResString("sobot_no_select_copy_service_string"), "-1");
                list.add(0, tempBean1);
                firstTmplist.add(0, tempBean1);
                break;
            default:
                break;
        }

        if (tmplist != null) {
            list.addAll(tmplist);
        }

        //回显之前选中的项目
        for (int i = 0; i < list.size(); i++) {
            if (!TextUtils.isEmpty(selectedItemValue) && list.get(i).getItemValue().equals(selectedItemValue)) {
                list.get(i).setIsChecked(true);
            }
        }

        if (adapter == null) {
            adapter = new SobotWOListAdapter(SobotWOListActivity.this, list, displayType);
            mListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("searchContent", quickReplyListEtSearch.getText().toString());
        if (v.getId() == R.id.quick_reply_list_ivDeleteText) {
            quickReplyListEtSearch.setText("");
        }
    }
}