package com.sobot.workorder.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.common.ui.toast.SobotToastUtil;
import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.widget.refresh.layout.SobotRefreshLayout;
import com.sobot.widget.refresh.layout.api.RefreshLayout;
import com.sobot.widget.refresh.layout.footer.ClassicsFooter;
import com.sobot.widget.refresh.layout.header.ClassicsHeader;
import com.sobot.widget.refresh.layout.listener.OnLoadMoreListener;
import com.sobot.widget.refresh.layout.listener.OnRefreshListener;
import com.sobot.workorder.adapter.SobotWOSecondAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.permission.SobotPermissionManager;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotWorkOrderUtils;
import com.sobot.workorder.weight.popvindow.SobotActionItem;
import com.sobot.workorder.weight.popvindow.SobotTitileMenuPop;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;
import com.sobot.workorderlibrary.api.model.SobotWOCenterItemModel;
import com.sobot.workorderlibrary.api.model.SobotWOSecondItemModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单分类列表
 */
public class SobotWOTicketListActivity extends SobotWOBaseActivity implements SobotWOSecondAdapter.OnItemClickListener, View.OnClickListener, SobotTitileMenuPop.PopItemClick {
    //右上角的菜单
    private TextView sobot_tv_right_search;
    private TextView sobot_tv_right_sort;
    private SobotRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SobotWOCenterItemModel modelResult;
    private SobotLoadingLayout loading_layout;

    public int displayType = 0;//0创建时间倒序   1创建时间正序 2更新时间倒序  3更新时间正序
    //分页
    private int pageNo = 1, pageSize = 20;
    //是否还有更多数据
    private boolean isHasMoreData = false;
    private ArrayList<SobotWOSecondItemModel> datas;
    private SobotWOSecondAdapter adapter;
    //排序弹窗
    private PopupWindow popupWindow;
//    private String[] tipStrs;
//    private SobotTitileMenuPop popupWindow;
    private List<SobotActionItem> actionItems;
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registBroadCast();
    }

    @Override
    protected int getContentViewResId() {
        return SobotResourceUtils.getResLayoutId(getSobotBaseContext(), "sobot_activity_wo_second_list");
    }

    @Override
    protected void initView() {
        actionItems = new ArrayList<>();
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this,"sobot_desc_create_string"),getResources().getDrawable(SobotResourceUtils.getDrawableId(this,"sobot_wo_time_desc_selector")),true));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this,"sobot_asc_create_string"),getResources().getDrawable(SobotResourceUtils.getDrawableId(this,"sobot_wo_time_asc_selector")),false));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this,"sobot_desc_update_string"),getResources().getDrawable(SobotResourceUtils.getDrawableId(this,"sobot_wo_update_desc_selector")),false));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this,"sobot_asc_update_string"),getResources().getDrawable(SobotResourceUtils.getDrawableId(this,"sobot_wo_update_asc_selector")),false));

        modelResult = (SobotWOCenterItemModel) getIntent().getBundleExtra("bundle").getSerializable("modelResult");
        if (modelResult != null) {
            setTitle(modelResult.getTaskName());
            /*如果是未读工单，做处理*/
            if ("301".equals(modelResult.getTaskId())) {
            //点击工单分类进入小分类列表,移除工单未读标识发送广播
                SobotSharedPreferencesUtil.getInstance(getApplicationContext()).put(SobotWorkOrderUtils.getWorkOrderUnReadKey(getApplicationContext()),"" );
                Intent intent = new Intent();
                intent.setAction(SobotConstantUtils.SOBOT_BROADCAST_WORK_ORDER_HAVE_MSG);
                sendBroadcast(intent);
            }
        } else {
            finish();
        }
        //title右边的搜索按钮
        sobot_tv_right_search = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_tv_right"));
        loading_layout = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_loading_layout"));
        Drawable left = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_workorder_search_selector"));
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        sobot_tv_right_search.setCompoundDrawables(left, null, null, null);
        sobot_tv_right_search.setOnClickListener(this);
        if(SobotPermissionManager.isHasPermission(this, SobotPermissionManager.USER_PERMISSION_TYPE_WORK_SEARCH)){
            sobot_tv_right_search.setVisibility(View.VISIBLE);
        }else{
            sobot_tv_right_search.setVisibility(View.GONE);
        }
        //title右边的排序按钮
        sobot_tv_right_sort = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_tv_right_second"));
        Drawable left2 = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_workorder_sort_selector"));
        left2.setBounds(0, 0, left2.getMinimumWidth(), left2.getMinimumHeight());
        sobot_tv_right_sort.setCompoundDrawables(left2, null, null, null);
        sobot_tv_right_sort.setOnClickListener(this);
        sobot_tv_right_sort.setVisibility(View.VISIBLE);


        refreshLayout = (SobotRefreshLayout) findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_srl_workorder_second"));
        recyclerView = (RecyclerView) findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_rv_workorder_second_list"));
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setEnableLoadMore(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SobotWOTicketListActivity.this));
        datas = new ArrayList<>();
        adapter = new SobotWOSecondAdapter(SobotWOTicketListActivity.this, datas);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                if (isHasMoreData) {
                    updateDataBySortType(pageNo + 1, displayType);
                } else {
                    refreshLayout.setNoMoreData(true);
                }
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                pageNo = 1;
                updateDataBySortType(pageNo, displayType);
            }
        });
        refreshLayout.setNoMoreData(true);
        loading_layout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNo = 1;
                updateDataBySortType(pageNo, displayType);
            }
        });
    }

    @Override
    protected void initData() {
        updateDataBySortType(1, displayType);
    }

    private void updateDataBySortType(int pageIndex, int displayType) {
        switch (displayType) {
            case 0://
                getData(pageIndex, "createTime", "desc");
                SobotLogUtils.i("时间倒序");
                break;
            case 1://
                getData(pageIndex, "createTime", "asc");
                SobotLogUtils.i("时间正序");
                break;
            case 2://
                getData(pageIndex, "updateTime", "desc");
                SobotLogUtils.i("更新时间倒序");
                break;
            case 3://
                getData(pageIndex, "updateTime", "asc");
                SobotLogUtils.i("更新时间正序");
                break;
            default:
                break;
        }
    }

    /**
     * @param pageIndex 请求的页数
     * @param sortCol   按那个字段排序（createTime/updateTime）
     * @param sortSeq   升序（asc） /降序（desc）
     */
    private void getData(final int pageIndex, String sortCol, String sortSeq) {
        if(pageIndex==1){
            showProgressDialog();
        }
        SobotServiceVoModel sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(SobotWOTicketListActivity.this).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
        if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
            zhiChiApi.getWorkOrderSecondListNew(SobotWOTicketListActivity.this, modelResult.getTaskId(),  "", "", pageIndex, pageSize, sortCol, sortSeq, new SobotResultCallBack<List<SobotWOSecondItemModel>>() {
                @Override
                public void onSuccess(List<SobotWOSecondItemModel> data) {
                    if (pageIndex == 1) {
                        dismissProgressDialog();
                    }
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
                        //发送广播
                        Intent intent = new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_RESULT);
                        intent.putExtra("items", (ArrayList<SobotWOSecondItemModel>) data);
                        intent.putExtra("isLoadMore", isHasMoreData);
                        sendBroadcast(intent);
                        refreshLayout.setNoMoreData(false);
                    } else {
                        //加载到最后一页
                        isHasMoreData = false;
                        sendBroadcast(new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_NOMORE));
                        if (pageIndex == 1) {
                            loading_layout.showEmpty();
                        }
                    }
                }

                @Override
                public void onFailure(Exception e, String s) {
                    if (pageIndex == 1) {
                        dismissProgressDialog();
                    }
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();
                    if (pageIndex == 1) {
                        loading_layout.showEmpty();
                    } else {
                        SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : s, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
                    }
                }
            });
        }else {
            zhiChiApi.getWorkOrderSecondList(SobotWOTicketListActivity.this, modelResult.getTaskId(), modelResult.getTaskStatus() + "", "", pageIndex, pageSize, sortCol, sortSeq, new SobotResultCallBack<List<SobotWOSecondItemModel>>() {
                @Override
                public void onSuccess(List<SobotWOSecondItemModel> data) {
                    if (pageIndex == 1) {
                        dismissProgressDialog();
                    }
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
                        //发送广播
                        Intent intent = new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_RESULT);
                        intent.putExtra("items", (ArrayList<SobotWOSecondItemModel>) data);
                        intent.putExtra("isLoadMore", isHasMoreData);
                        sendBroadcast(intent);
                        refreshLayout.setNoMoreData(false);
                    } else {
                        //加载到最后一页
                        isHasMoreData = false;
                        sendBroadcast(new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_NOMORE));
                        if (pageIndex == 1) {
                            loading_layout.showEmpty();
                        }
                    }
                }

                @Override
                public void onFailure(Exception e, String s) {
                    if (pageIndex == 1) {
                        dismissProgressDialog();
                    }
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();
                    if (pageIndex == 1) {
                        loading_layout.showEmpty();
                    } else {
                        SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : s, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SobotConstantUtils.SOBOT_NEED_REFRESH && resultCode == SobotConstantUtils.SOBOT_NEED_REFRESH) {
            pageNo = 1;
            updateDataBySortType(pageNo, displayType);
            setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
        }
    }

    /**
     * 点击进入详情
     *
     * @param info
     * @param index
     */
    @Override
    public void onItemClick(SobotWOSecondItemModel info, int index) {
        Intent intent = new Intent(SobotWOTicketListActivity.this, SobotWODetailActivity.class);
        intent.putExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_TICKETID, datas.get(index).getTicketId());
        intent.putExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_ISSHOWRECEIPT, datas.get(index).getIsShowReceipt());

        intent.putExtra("items", datas);
        intent.putExtra("flag", true);
        intent.putExtra("fromWhere", "listActivity");
        intent.putExtra("ticketNum", datas.size());
        intent.putExtra("isLoadMore", isHasMoreData);
        startActivityForResult(intent,SobotConstantUtils.SOBOT_NEED_REFRESH);
    }

    /**
     * 接单
     *
     * @param info
     * @param index
     */
    @Override
    public void takeWorkOrder(final SobotWOSecondItemModel info, final int index) {
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
                            SobotToastUtil.showCustomToast(SobotWOTicketListActivity.this, SobotResourceUtils.getResString(SobotWOTicketListActivity.this, "sobot_wo_accept_order_string"));
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
                SobotToastUtil.showCustomToast(SobotWOTicketListActivity.this, SobotStringUtils.isEmpty(msg) ? SobotResourceUtils.getResString(SobotWOTicketListActivity.this, "sobot_wo_net_error_string") : msg, SobotResourceUtils.getDrawableId(SobotWOTicketListActivity.this, "sobot_icon_warning_attention"));
            }

            @Override
            public void onFailure(Exception e, String s) {
                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? SobotResourceUtils.getResString(SobotWOTicketListActivity.this, "sobot_wo_net_error_string"):s , SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == sobot_tv_right_sort){
            //弹窗
            SobotTitileMenuPop sobotSelectSearchTypePop = new SobotTitileMenuPop(SobotWOTicketListActivity.this, actionItems,SobotTitileMenuPop.TYPE_BOTTOM_CIRCULAR,true, SobotWOTicketListActivity.this);
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
            popupWindow.showAsDropDown(sobot_tv_right_sort, 0, 0);

        } else if (view == sobot_tv_right_search) {
            Intent intent = new Intent(SobotWOTicketListActivity.this, SobotWOSearchActivity.class);
            intent.putExtra("taskId", modelResult != null ? modelResult.getTaskId() : "");
            intent.putExtra("taskStatus", modelResult != null ? modelResult.getTaskStatus() + "" : "");
            intent.putExtra("ticketStatus", "");
            startActivityForResult(intent,SobotConstantUtils.SOBOT_NEED_REFRESH);
        }
    }

    @Override
    public void onPopItemClick(int index) {
        for (int i = 0; i < actionItems.size(); i++) {
            if(i==index){
                actionItems.get(i).isChecked = true;
            }else{
                actionItems.get(i).isChecked = false;
            }
        }
        updateDataBySortType(1, index);
        popupWindow.dismiss();
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
                    updateDataBySortType(pageNo+1, displayType);
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