package com.sobot.workorder.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.widget.refresh.layout.SobotRefreshLayout;
import com.sobot.widget.refresh.layout.api.RefreshLayout;
import com.sobot.widget.refresh.layout.listener.OnLoadMoreListener;
import com.sobot.widget.refresh.layout.listener.OnRefreshListener;
import com.sobot.workorder.adapter.SobotWOSecondAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.weight.popvindow.SobotActionItem;
import com.sobot.workorder.weight.popvindow.SobotTitileMenuPop;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;
import com.sobot.workorderlibrary.api.model.SobotWOSearchParamModel;
import com.sobot.workorderlibrary.api.model.SobotWOSecondItemModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单搜索页面
 */
public class SobotWOSearchActivity extends SobotWOBaseActivity implements View.OnClickListener, SobotWOSecondAdapter.OnItemClickListener, SobotTitileMenuPop.PopItemClick {
    private Button sobot_btn_search;//搜索
    private TextView sobot_tv_search_type;//搜索类型
    private EditText sobot_et_search;//搜索内容
    private ImageView sobot_iv_delete;//清除输入内容
    //列表
    private SobotRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    //是否还有更多数据
    private boolean isHasMoreData = false;
    private ArrayList<SobotWOSecondItemModel> datas;
    private SobotWOSecondAdapter adapter;


    private int pageNo = 1;//当前页数
    private int pageSize = 20;
    private int searchType = 5;//搜索类型：1按描述搜索 、2按标题搜索、3工单编号查询 、4受理客服、5模糊搜索


    private String taskId = "";
    private String taskStatus = "-1";
    private String ticketStatus = "";

    private PopupWindow popupWindow;
    private List<SobotActionItem> actionItems;
    private SobotLoadingLayout loading_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registBroadCast();
    }

    @Override
    protected int getContentViewResId() {
        return SobotResourceUtils.getResLayoutId(getSobotBaseContext(), "sobot_activity_wo_search");
    }

    @Override
    protected void initView() {
        actionItems = new ArrayList<>();
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this, "sobot_mohu_search_string")));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this, "sobot_orderid_search_string")));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this, "sobot_ordertitle_search_string")));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this, "sobot_desc_search_string")));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this, "sobot_service_search_string")));

        sobot_btn_search = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_btn_search"));
        sobot_tv_search_type = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_tv_search_type"));
        sobot_et_search = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_et_search"));
        sobot_iv_delete = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_iv_delete"));
        refreshLayout = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_srl_workorder_search"));
        recyclerView = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_rv_workorder_search_list"));
        loading_layout = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_loading_layout"));
        sobot_et_search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        sobot_et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    //搜索
                    search(1);
                    return true;
                }
                return false;
            }
        });
        sobot_et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sobot_btn_search.setText(SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_search_string"));
                    sobot_iv_delete.setVisibility(View.VISIBLE);
                } else {
                    sobot_iv_delete.setVisibility(View.GONE);
                    sobot_btn_search.setText(SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_btn_cancle"));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                pageNo = 1;
                search(pageNo);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                if (isHasMoreData) {
                    search(pageNo + 1);
                } else {
                    refreshLayout.setNoMoreData(true);
                }
            }
        });
        sobot_btn_search.setOnClickListener(this);
        sobot_tv_search_type.setOnClickListener(this);
        sobot_iv_delete.setOnClickListener(this);
        datas = new ArrayList<>();
        adapter = new SobotWOSecondAdapter(SobotWOSearchActivity.this, datas);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(SobotWOSearchActivity.this));
        loading_layout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = 1;
                search(pageNo);
            }
        });
        loading_layout.showEmpty();
    }

    @Override
    protected void initData() {
        if (getIntent() != null) {
            taskId = getIntent().getStringExtra("taskId");
            taskStatus = getIntent().getStringExtra("taskStatus");
            ticketStatus = getIntent().getStringExtra("ticketStatus");
            if (SobotStringUtils.isEmpty(taskId)) taskId = "";
            if (SobotStringUtils.isEmpty(ticketStatus)) ticketStatus = "";
        }
    }

    private void search(final int pageIndex) {
        if (TextUtils.isEmpty(sobot_et_search.getText().toString().trim())) {
            //搜索内容为空
            SobotToastUtil.showCustomToast(SobotWOSearchActivity.this, SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_search_input_empty_tip_string"));
            return;
        }
        SobotWOSearchParamModel model = new SobotWOSearchParamModel();
        model.setPageNo(pageIndex);
        model.setPageSize(pageSize);
        model.setQueryType(searchType);

        model.setTaskId(taskId);
        model.setTaskStatus(taskStatus );
        model.setTicketStatus(ticketStatus);
        model.setQueryContent(sobot_et_search.getText().toString().trim());
        showProgressDialog();
        zhiChiApi.searchOrder(SobotWOSearchActivity.this, model, new SobotResultCallBack<List<SobotWOSecondItemModel>>() {
            @Override
            public void onSuccess(List<SobotWOSecondItemModel> data) {
                dismissProgressDialog();
                loading_layout.showContent();
                if (pageIndex == 1) {
                    datas.clear();
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
                SobotLogUtils.i("data---:" + data.size());
                pageNo = pageIndex;
                if (data.size() > 0) {
                    datas.addAll(data);
                    adapter.notifyDataSetChanged();
                    isHasMoreData = true;
                } else {
                    //加载到最后一页
                    isHasMoreData = false;
                }
                if (data.size() == 0 && pageIndex == 1) {
                    //显示未查询到数据
                    loading_layout.showEmpty();
                }
                if(isHasMoreData){
                    //发送广播
                    sendBroadcast(new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_RESULT));
                }
            }

            @Override
            public void onFailure(Exception e, String s) {
                dismissProgressDialog();
//                e.printStackTrace();
                SobotLogUtils.d("=========" + s);
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                if(pageIndex==1){
                    loading_layout.showError();
                }else{
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string"):s , SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
                }

            }
        });
    }

    @Override
    public void onItemClick(SobotWOSecondItemModel info, int index) {
        //点击进入详情
        Intent intent = new Intent(SobotWOSearchActivity.this, SobotWODetailActivity.class);
        intent.putExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_TICKETID, datas.get(index).getTicketId());
        intent.putExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_ISSHOWRECEIPT, datas.get(index).getIsShowReceipt());
        intent.putExtra("items", datas);
        intent.putExtra("flag", true);
        intent.putExtra("fromWhere", "searchActivity");
        intent.putExtra("ticketNum", datas.size());
        intent.putExtra("isLoadMore", isHasMoreData);
        startActivity(intent);
    }

    @Override
    public void takeWorkOrder(final SobotWOSecondItemModel info, int index) {
        //接单
        if (info == null) {
            return;
        }
        zhiChiApi.acceptOrder(this, info.getTicketId(), new SobotResultCallBack<String>() {
            @Override
            public void onSuccess(String response) {
                String msg = "";
                try {
                    JSONObject object = new JSONObject(response);

                    if (object.has("code") && !object.isNull("code")) {
                        if ("1".equals(object.optString("code"))) {
                            SobotToastUtil.showCustomToast(SobotWOSearchActivity.this, SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_wo_accept_order_string"));
                            //设置处理人的信息
                            SobotServiceVoModel user = SobotSharedPreferencesUtil.getInstance(getBaseContext()).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
                            if (user != null) {
                                info.setDealUserName(user.getServiceName());
                                info.setDealUserId(user.getServiceId());
                            }
                            adapter.notifyDataSetChanged();
                            setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                            return;
                        }
                    }
                    if (object.has("msg")) {
                        msg = object.optString("msg", "");
                    } else if (object.has("retMsg")) {
                        msg = object.optString("retMsg", "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SobotToastUtil.showCustomToast(SobotWOSearchActivity.this, SobotStringUtils.isEmpty(msg) ? SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_wo_net_error_string") : msg, SobotResourceUtils.getDrawableId(SobotWOSearchActivity.this, "sobot_icon_warning_attention"));
            }

            @Override
            public void onFailure(Exception e, String s) {
                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_wo_net_error_string"):s , SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == sobot_btn_search) {
            String btnString = sobot_btn_search.getText().toString();
            if (SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_btn_cancle").equals(btnString)) {
                finish();
            } else if (SobotResourceUtils.getResString(SobotWOSearchActivity.this, "sobot_search_string").equals(btnString)) {
                //搜索
                search(1);
            }

        } else if (view == sobot_iv_delete) {
            //清空
            sobot_et_search.setText("");
        } else if (view == sobot_tv_search_type) {
            //弹窗
            if (null == popupWindow) {
                SobotTitileMenuPop sobotSelectSearchTypePop = new SobotTitileMenuPop(SobotWOSearchActivity.this, actionItems, SobotWOSearchActivity.this);
                popupWindow = sobotSelectSearchTypePop.getPopWindow();
                //弹窗出现外部为阴影
                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                attributes.alpha = 0.5f;
                getWindow().setAttributes(attributes);
                //弹窗取消监听 取消之后恢复阴影
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams attributes = getWindow().getAttributes();
                        attributes.alpha = 1;
                        getWindow().setAttributes(attributes);
                    }
                });
            }
            popupWindow.showAsDropDown(sobot_tv_search_type, -20, 20);

        }
    }

    @Override
    public void onPopItemClick(int index) {
        popupWindow.dismiss();
        switch (index) {
            case 0:
                //模糊搜索
                searchType = 5;
                sobot_tv_search_type.setText(SobotResourceUtils.getResString(this, "sobot_mohu_string"));
                sobot_et_search.setHint(SobotResourceUtils.getResString(this, "sobot_search_hide_string"));
                break;
            case 1:
                //工单编号
                searchType = 3;
                sobot_tv_search_type.setText(SobotResourceUtils.getResString(this, "sobot_orderid_string"));
                sobot_et_search.setHint(SobotResourceUtils.getResString(this, "sobot_orderid_search_string"));
                break;
            case 2:
                //工单标题
                searchType = 2;
                sobot_tv_search_type.setText(SobotResourceUtils.getResString(this, "sobot_ordertitile_string"));
                sobot_et_search.setHint(SobotResourceUtils.getResString(this, "sobot_ordertitle_search_string"));
                break;
            case 3:
                //问题描述
                searchType = 1;
                sobot_tv_search_type.setText(SobotResourceUtils.getResString(this, "sobot_desc_string"));
                sobot_et_search.setHint(SobotResourceUtils.getResString(this, "sobot_desc_search_string"));
                break;
            case 4:
                //受理客服
                searchType = 4;
                sobot_tv_search_type.setText(SobotResourceUtils.getResString(this, "sobot_service_string"));
                sobot_et_search.setHint(SobotResourceUtils.getResString(this, "sobot_service_search_hide_string"));
                break;
        }
    }
    //广播，翻页
    private void registBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_REQUEST);
        registerReceiver(broadcast, filter);
    }
    private BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_REQUEST)) {
                if (isHasMoreData) {
                    search(pageNo+1);
                } else {
                    Intent intentNoMore = new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_NOMORE);
                    sendBroadcast(intentNoMore);
                }
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }
}