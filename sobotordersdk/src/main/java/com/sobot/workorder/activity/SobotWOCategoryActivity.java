package com.sobot.workorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotWOCategoryAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.model.SobotWOCategoryModelResult;

import java.util.ArrayList;
import java.util.List;

//工单分类界面
public class SobotWOCategoryActivity extends SobotWOBaseActivity implements View.OnClickListener {

    private SobotWOCategoryAdapter categoryAdapter;
    private RecyclerView category_rcv;
    private List<SobotWOCategoryModelResult> categoryModelResults = new ArrayList<>();
    private String partentId = "-1";
    //当前level从0开始
    private int typeLevel = 0;
    private String work_order_category;
    private LinearLayout horizontalScrollView_ll;

    String navigationBarTitle[];
    SparseArray<List<SobotWOCategoryModelResult>> cacheData = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_wo_category;
    }

    @Override
    protected void initView() {
        setTitle(getString(R.string.sobot_select_classification));
        getLeftMenu().setOnClickListener(this);
        navigationBarTitle = new String[]{getResString("sobot_categrory_first_string"), getResString("sobot_categrory_second_string"), getResString("sobot_categrory_third_string"), getResString("sobot_categrory_four_string"), getResString("sobot_categrory_five_string")};
        work_order_category = getIntent().getStringExtra("work_order_category");
        category_rcv = findViewById(R.id.work_order_category_rcv);
        horizontalScrollView_ll = findViewById(R.id.horizontalScrollView_ll);
        category_rcv.setLayoutManager(new LinearLayoutManager(getSobotBaseContext()));
        categoryAdapter = new SobotWOCategoryAdapter(getSobotBaseContext(), categoryModelResults);
        category_rcv.setAdapter(categoryAdapter);
        categoryAdapter.setItemClickListener(new SobotWOCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SobotWOCategoryModelResult info, int position) {
                if (SobotConstantUtils.SOBOT_WO_ORDER_CATEGORY_NODEFLAG_YES == categoryModelResults.get(position).getNodeFlag()) {
                    typeLevel = categoryModelResults.get(position).getTypeLevel();
                    initData(categoryModelResults.get(position).getTypeId());
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("category_typeName", categoryModelResults.get(position).getTypeName());
                    intent.putExtra("category_typeId", categoryModelResults.get(position).getTypeId());
                    setResult(SobotConstantUtils.sobot_wo_list_display_type_category, intent);
                    if (categoryModelResults.get(position).isChecked()) {
                        categoryModelResults.get(position).setChecked(false);
                    } else {
                        categoryModelResults.get(position).setChecked(true);
                    }
                    for (int i = 0; i < categoryModelResults.size(); i++) {
                        if (i != position) {
                            categoryModelResults.get(i).setChecked(false);
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }

    @Override
    protected void initData() {
        initData(partentId);
    }

    private void initData(String partentId) {
        zhiChiApi.queryTicketTypeInfoListByPid(this, partentId, new SobotResultCallBack<List<SobotWOCategoryModelResult>>() {
            @Override
            public void onSuccess(List<SobotWOCategoryModelResult> workOrderCategoryModelResults) {
                if (workOrderCategoryModelResults != null) {
                    cacheData.put(typeLevel, workOrderCategoryModelResults);
                    showData(workOrderCategoryModelResults);
                    setDefaultView();
                }

            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? getString(R.string.sobot_wo_net_error_string):des , R.drawable.sobot_icon_warning_attention);

            }
        });
    }

    private void showData(List<SobotWOCategoryModelResult> items) {
        if (items != null) {
            categoryModelResults.clear();
            categoryModelResults.addAll(items);
            categoryAdapter.notifyDataSetChanged();
        }
    }

    private void setDefaultView() {
        if (!TextUtils.isEmpty(work_order_category) && categoryModelResults != null) {
            for (int i = 0; i < categoryModelResults.size(); i++) {
                if (work_order_category.equals(categoryModelResults.get(i).getTypeName())) {
                    categoryModelResults.get(i).setChecked(true);
                }
            }
            categoryAdapter.notifyDataSetChanged();

            if (categoryModelResults.size() > 0) {
                updateIndicator(typeLevel + 1);
            }
        }
    }

    private void updateIndicator(int currentShowLevel) {
        int maxLength = Math.min(navigationBarTitle.length, currentShowLevel);
        horizontalScrollView_ll.removeAllViews();
        for (int i = 0; i < maxLength; i++) {
            View view = LayoutInflater.from(getSobotBaseContext()).inflate(R.layout.sobot_layout_order_category_header_items, null);
            if (view != null) {
                TextView titleTv = view.findViewById(R.id.work_order_category_title);
                titleTv.setText(navigationBarTitle[i]);
                horizontalScrollView_ll.addView(view);
                final int position = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        typeLevel = position;
                        List<SobotWOCategoryModelResult> datas = cacheData.get(position);
                        if (datas != null) {
                            showData(datas);
                            updateIndicator(position + 1);
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

    private void backPressed() {
        if (typeLevel == 0) {
            finish();
        } else {
            typeLevel--;
            List<SobotWOCategoryModelResult> datas = cacheData.get(typeLevel);
            if (datas != null) {
                showData(datas);
                updateIndicator(typeLevel + 1);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == getLeftMenu()) {
            backPressed();
        }
    }
}