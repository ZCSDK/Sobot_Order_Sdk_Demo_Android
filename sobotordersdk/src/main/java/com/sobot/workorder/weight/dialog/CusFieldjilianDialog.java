package com.sobot.workorder.weight.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotCascadeAdapter;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;

import java.util.ArrayList;
import java.util.List;

/**
 * 级联对话框
 */
public class CusFieldjilianDialog extends SobotBottomDialog implements View.OnClickListener {

    private int fieldType;//是最后一级显示还是每一级都能选
    private List<SobotCusFieldDataInfoList> infoLists = new ArrayList<>();//原始数据，多级
    private List<SobotCusFieldDataInfoList> tempInfoLists = new ArrayList<>();//当前这级别
    private List<SobotCusFieldDataInfoList> selectInfoLists = new ArrayList<>();//选中的，从第一级到最后一级（xx省xx市xx县）
    private SobotCascadeAdapter adapter;
    private Activity activity;
    private TextView sobot_tv_title;
    private TextView tv_nodata;
    private String title = "";
    RecyclerView mListView;
    DialogItemOnClick listner;
    private LinearLayout horizontalScrollView_ll, sobot_negativeButton, coustom_pop_layout;

    public CusFieldjilianDialog(Activity context, String title, List<SobotCusFieldDataInfoList> list, DialogItemOnClick listner) {
        super(context);
        this.infoLists = new ArrayList<>();
        this.title = title;
        infoLists.addAll(list);
        this.activity = context;
        this.listner = listner;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected int getLayout() {
        return R.layout.sobot_dialog_cus_jilian;
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
        if (!TextUtils.isEmpty(title)) {
            sobot_tv_title.setText(title);
        }
        updateIndicator();
        if (infoLists != null && infoLists.size() != 0) {
            tv_nodata.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
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
                adapter = new SobotCascadeAdapter(activity, tempInfoLists);
                mListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            adapter.setItemClickListener(new SobotCascadeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(SobotCusFieldDataInfoList cusFieldDataInfoListModel, int position) {
                    if (cusFieldDataInfoListModel != null) {
                        selectInfoLists.add(cusFieldDataInfoListModel);
                        tempInfoLists.clear();
                        for (int i = 0; i < infoLists.size(); i++) {
                            //显示下一级
                            if (infoLists.get(i).getParentDataId().equals(cusFieldDataInfoListModel.getDataId()) && !TextUtils.isEmpty(infoLists.get(i).getDataId())) {
                                tempInfoLists.add(infoLists.get(i));
                            }
                        }
                        if (tempInfoLists.size() == 0) {
                            if (null != listner) {
                                listner.selectItem(selectInfoLists);
                            }
                            dismiss();
//
                        } else {
                            for (int j = 0; j < tempInfoLists.size(); j++) {
                                for (int x = 0; x < infoLists.size(); x++) {
                                    if (tempInfoLists.get(j).getDataId().equals(infoLists.get(x).getParentDataId())) {
                                        tempInfoLists.get(j).setHasNewNode(true);
                                        break;
                                    }
                                }
                            }
                            updateIndicator();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }else{
            tv_nodata.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initView() {
        mListView = findViewById(R.id.rv_list);
        tv_nodata = findViewById(R.id.tv_nodata);
        horizontalScrollView_ll = findViewById(R.id.ll_level);
        sobot_negativeButton = findViewById(R.id.sobot_negativeButton);
        sobot_tv_title = findViewById(R.id.sobot_tv_title);
        mListView.setLayoutManager(new LinearLayoutManager(activity));
        sobot_negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭
                dismiss();
            }
        });
    }

    private void updateIndicator() {
        horizontalScrollView_ll.removeAllViews();
        if (selectInfoLists.size() > 0) {
            horizontalScrollView_ll.setVisibility(View.VISIBLE);
        } else {
            horizontalScrollView_ll.setVisibility(View.GONE);
        }
        for (int i = 0; i < selectInfoLists.size(); i++) {
            TextView titleTv = (TextView) View.inflate(activity, R.layout.sobot_item_select_level, null);
            if (titleTv != null) {
                titleTv.setText(selectInfoLists.get(i).getDataName());
                horizontalScrollView_ll.addView(titleTv);
                final int position = i;
                titleTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectInfoLists.size() > 0) {
                            tempInfoLists.clear();
                            SobotCusFieldDataInfoList curInfo = selectInfoLists.get(selectInfoLists.size() - 1);
                            for (int j = 0; j < infoLists.size(); j++) {
                                if (selectInfoLists.size() == 1) {
                                    if (TextUtils.isEmpty(infoLists.get(j).getParentDataId())) {
                                        tempInfoLists.add(infoLists.get(j));
                                    }
                                } else {
                                    //显示下一级
                                    if (infoLists.get(j).getParentDataId().equals(curInfo.getParentDataId())) {
                                        tempInfoLists.add(infoLists.get(j));
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                            selectInfoLists.remove(curInfo);
                            updateIndicator();
                        }
                    }
                });
            }
        }
    }
}
