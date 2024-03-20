package com.sobot.workorder.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.sobot.common.login.SobotLoginTools;
import com.sobot.common.login.model.SobotServiceInfoModel;
import com.sobot.common.login.permission.SobotPermissionApi;
import com.sobot.common.login.permission.SobotPermissionManager;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.livedatabus.SobotLiveEventBus;
import com.sobot.workorder.R;
import com.sobot.workorder.base.SobotBaseFragment;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.fragment.SobotWOCustomFragment;
import com.sobot.workorder.fragment.SobotWODetailFragment;
import com.sobot.workorder.fragment.SobotWODetailHistoryFragment;
import com.sobot.workorder.fragment.SobotWODetailSourceFragment;
import com.sobot.workorder.fragment.SobotWOWebFragment;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotWoLiveDataBusKey;
import com.sobot.workorder.utils.SobotWorkOrderUtils;
import com.sobot.workorder.weight.dialog.SobotCommonDialog;
import com.sobot.workorder.weight.popwindow.SobotActionItem;
import com.sobot.workorder.weight.popwindow.SobotTitileMenuPop;
import com.sobot.workorder.weight.tab.SobotPagerSlidingTab;
import com.sobot.workorder.weight.tab.SobotViewPagerAdapter;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.apiutils.SobotWOBaseSeconndCode;
import com.sobot.workorderlibrary.api.model.SobotCustomerModel;
import com.sobot.workorderlibrary.api.model.SobotIframeModel;
import com.sobot.workorderlibrary.api.model.SobotWODetailResultModel;
import com.sobot.workorderlibrary.api.model.SobotWOSecondItemModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单详情页面
 */
public class SobotWODetailActivity extends SobotWOBaseActivity implements View.OnClickListener, SobotTitileMenuPop.PopItemClick {
    private String detailInfoId, customerId;
    private SobotWODetailResultModel detailResultInfo;
    private TextView rightMenu, tvReply, tvTakeWorkOrder, tvBefore, tvNext;
    private ImageView iv_wo_more;
    private SobotPagerSlidingTab mTab;
    private ViewPager viewPager;
    private SobotViewPagerAdapter pagerAdapter;
    private List<SobotBaseFragment> mFragments;
    private SobotWODetailFragment detailFragment;//工单详情
    private SobotWOCustomFragment customFragment;//客户信息
    private SobotWODetailSourceFragment sourceFragment;//工单来源
    private SobotWODetailHistoryFragment historyFragment;//操作历史

    private boolean flag = false;// true：表示从工单列表页来的。 false：表示查询不到
    private ArrayList<SobotWOSecondItemModel> items = new ArrayList();
    //上一页下一页
    private int position = 0;//工单位置,当前工单在items中的位置
    private int ticketNum = 0;//当前工单类型的总数
    private boolean isLoadMore = false;//是否还有更多数据

    private ArrayList<SobotActionItem> actionItems;
    private PopupWindow popupWindow;

    TextView conCernMenu;//右上角关注

    //iframe
    private List<SobotIframeModel> iframes = new ArrayList<>();
    private List<String> tabs = new ArrayList<>();//

    //变更编辑客户信息后更细工单详情里边的客户信息
    private Observer updateCustomerObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registBroadCast();
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_wo_detail;
    }

    @Override
    protected void initView() {
        if (getIntent() != null) {
            detailInfoId = getIntent().getStringExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_TICKETID);
            customerId = getIntent().getStringExtra(SobotConstantUtils.SOBOT_WORK_ORDER_DETAIL_INFO_CUSTOMERID);
            flag = getIntent().getBooleanExtra("flag", false);
            ticketNum = getIntent().getIntExtra("ticketNum", 0);
            isLoadMore = getIntent().getBooleanExtra("isLoadMore", false);
            if (flag) {
                ArrayList<SobotWOSecondItemModel> tmpitems = (ArrayList<SobotWOSecondItemModel>) getIntent().getSerializableExtra("items");
                if (tmpitems != null && tmpitems.size() > 0) {
                    items.addAll(tmpitems);
                }
            }

        }
        conCernMenu = getRightSecondMenu();
        conCernMenu.setOnClickListener(this);
        rightMenu = getRightMenu();
        Drawable left = getResources().getDrawable(R.drawable.sobot_wo_detail_more_selector);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        rightMenu.setCompoundDrawables(left, null, null, null);
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setOnClickListener(this);
//        if(!SobotStringUtils.isEmpty(detailInfoId)) {
//            setTitle(detailInfoId);
//            if(SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER)) {
//                Drawable left = getResources().getDrawable(R.drawable.sobot_wo_detail_more_selector"));
//                left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
//                rightMenu.setCompoundDrawables(left, null, null, null);
//                rightMenu.setVisibility(View.VISIBLE);
//                rightMenu.setOnClickListener(this);
//            }
//        }
        mTab = findViewById(R.id.sobot_wo_pst_tab);
        iv_wo_more = findViewById(R.id.iv_wo_more);
        viewPager = findViewById(R.id.vp_wo_select_tab);
        tvReply = findViewById(R.id.tv_work_order_reply);
        tvTakeWorkOrder = findViewById(R.id.tv_work_order_take);
        tvBefore = findViewById(R.id.tv_work_order_before);
        tvNext = findViewById(R.id.tv_work_order_next);
        tvReply.setOnClickListener(this);
        tvTakeWorkOrder.setOnClickListener(this);
        tvBefore.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        iv_wo_more.setOnClickListener(this);
        if (flag) {
            //显示回复和接单
            tvReply.setVisibility(View.VISIBLE);
            tvTakeWorkOrder.setVisibility(View.VISIBLE);
            setTitle("");
        } else {
            //显示回复
            tvReply.setVisibility(View.VISIBLE);
            tvTakeWorkOrder.setVisibility(View.GONE);
            setTitle(getString(R.string.sobot_order_search));
        }
        mFragments = new ArrayList<>();
        detailFragment = new SobotWODetailFragment();
        customFragment = new SobotWOCustomFragment();
        sourceFragment = new SobotWODetailSourceFragment();
        historyFragment = new SobotWODetailHistoryFragment();

        if (flag) {
            tvBefore.setVisibility(View.VISIBLE);
            tvNext.setVisibility(View.VISIBLE);
            positionNowWorkOrderID();
        } else {
            tvBefore.setVisibility(View.INVISIBLE);
            tvNext.setVisibility(View.INVISIBLE);
        }
        actionItems = new ArrayList<>();
        actionItems.add(new SobotActionItem(getString(R.string.sobot_edit_work_order_string), getResources().getDrawable(R.drawable.sobot_icon_detail_edit_menu)));
        actionItems.add(new SobotActionItem(getString(R.string.sobot_delete_work_order_string), getResources().getDrawable(R.drawable.sobot_icon_detail_delete_menu)));
        zhiChiApi.getIframe(this, new SobotResultCallBack<List<SobotIframeModel>>() {
            @Override
            public void onSuccess(List<SobotIframeModel> list) {
                iframes.addAll(list);
                initTab();
            }

            @Override
            public void onFailure(Exception e, String s) {
                initTab();
            }
        });

    }

    @Override
    protected void initData() {
        //变更编辑客户信息后更细工单详情里边的客户信息
        updateCustomerObserver = new Observer<SobotCustomerModel>() {
            @Override
            public void onChanged(@Nullable SobotCustomerModel customerModel) {
                if (customerModel != null && detailResultInfo != null && detailResultInfo.getTicketDetail() != null) {
                    //保存工单对应的客户信息
                    detailResultInfo.getTicketDetail().setSobotCallCustomerModel(customerModel);
                }
            }
        };
        SobotLiveEventBus.get(SobotWoLiveDataBusKey.SOBOT_LIVEBUS_WO_UPDATE_CUSTOMER).observeForever(updateCustomerObserver);
    }

    public void refresh() {
        if (!SobotStringUtils.isEmpty(detailInfoId)) {
            showProgressDialog();
            zhiChiApi.getOrderDetail(SobotWODetailActivity.this, detailInfoId, new SobotResultCallBack<SobotWODetailResultModel>() {
                @Override
                public void onSuccess(SobotWODetailResultModel sobotTicketModel) {
                    dismissProgressDialog();
                    if (sobotTicketModel == null) return;
                    detailResultInfo = sobotTicketModel;
                    setTitle("#" + detailResultInfo.getTicketDetail().getTicketCode());
                    if (detailResultInfo.getTicketDetail() != null) {
                        setOrderConcern(detailResultInfo.getTicketDetail().getConcerned());
                    }
                    if (detailResultInfo.getTicketDetail() != null && TextUtils.isEmpty(detailResultInfo.getTicketDetail().getDealUserName()) && detailResultInfo.getTicketDetail().getIsShowReceipt() == 0 &&
                            (detailResultInfo.getTicketDetail().getTicketStatus() == 0 || detailResultInfo.getTicketDetail().getTicketStatus() == 1 || detailResultInfo.getTicketDetail().getTicketStatus() == 2)) {
                        tvTakeWorkOrder.setVisibility(View.VISIBLE);
                    } else {
                        tvTakeWorkOrder.setVisibility(View.GONE);
                    }
                    tvReply.setText(R.string.sobot_reply_string);
                    //已删除的工单不显示回复按钮和编辑工单
                    if (detailResultInfo.getTicketDetail().getTicketStatus() == 98) {
                        tvReply.setVisibility(View.GONE);
                        rightMenu.setVisibility(View.GONE);
                    } else if (detailResultInfo.getTicketDetail().getTicketStatus() == 99) {//已关闭的工单管理可以删除
                        //已关闭工单，管理员可激活
                        if (SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_ACTIVATION_WORK_ORDER)) {
                            tvReply.setVisibility(View.VISIBLE);
                            tvReply.setText(R.string.sobot_detail_activation_order_string);
                        } else {
                            tvReply.setVisibility(View.GONE);
                        }
                        //右上角菜单只有删除工单工单
                        if (SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER)) {
                            rightMenu.setVisibility(View.GONE);
                            if (actionItems.size() == 2) {
                                actionItems.remove(1);
                            }
                        }
                    } else {
                        SobotServiceInfoModel user = SobotLoginTools.getInstance().getServiceInfo();
                        //是否有编辑权限，有的话显示编辑按钮
                        if (SobotPermissionManager.checkPermission(SobotPermissionApi.USER_PERMISSION_TYPE_EDIT_WORK_ORDER) || (user != null && user.getServiceId().equals(detailResultInfo.getTicketDetail().getDealUserId()))) {
                            if (!SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER)) {
                                //没有删除权限
                                if (actionItems.size() == 2) {
                                    actionItems.remove(1);
                                }
                            }
                            rightMenu.setVisibility(View.VISIBLE);
                        } else {
                            rightMenu.setVisibility(View.GONE);
                        }
                    }
                    zhiChiApi.getCustomById(getSobotBaseActivity(), customerId, new SobotResultCallBack<SobotCustomerModel>() {
                        @Override
                        public void onSuccess(SobotCustomerModel sobotCallCustomerModel) {
                            if (sobotCallCustomerModel != null && detailResultInfo.getTicketDetail() != null) {
                                //保存工单对应的客户信息
                                detailResultInfo.getTicketDetail().setSobotCallCustomerModel(sobotCallCustomerModel);
                            }
                        }

                        @Override
                        public void onFailure(Exception e, String des) {
                            //提示
                        }
                    });
                    detailFragment.setDetailResultModel(detailResultInfo);
                    String cusId = SobotStringUtils.isEmpty(detailResultInfo.getTicketDetail().getCustomerId()) ? customerId : detailResultInfo.getTicketDetail().getCustomerId();
                    customFragment.setCustomerId(cusId, detailResultInfo.getTicketDetail().getTicketId());
                    sourceFragment.setTicketDetail(detailResultInfo.getTicketDetail());
                    historyFragment.setTicketId(detailResultInfo.getTicketDetail().getTicketId());
                }

                @Override
                public void onFailure(Exception e, String s) {
                    dismissProgressDialog();
                    SobotLogUtils.d("请求失败===" + s);
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? getString(R.string.sobot_wo_net_error_string) : s, R.drawable.sobot_icon_warning_attention);

                }
            });
        }
    }

    private void initTab() {
        mFragments.clear();
        tabs.clear();
        //默认
        mFragments.add(detailFragment);
        mFragments.add(customFragment);
        mFragments.add(sourceFragment);
        tabs.add(getString(R.string.sobot_detail_work_order_string));
        tabs.add(getString(R.string.sobot_wo_custom_info));
        tabs.add(getString(R.string.sobot_source_work_order_string));
        //是否显示操作历史
        if (SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_WORK_HISTORY)) {
            mFragments.add(historyFragment);
            tabs.add(getString(R.string.sobot_history_work_order_string));
        }
        //是否显示iframe
        if (iframes.size() > 0) {
            for (int i = 0; i < iframes.size(); i++) {
                SobotWOWebFragment webFragment = new SobotWOWebFragment();
                webFragment.setUrl(iframes.get(i).getIframeUrl());
                mFragments.add(webFragment);
                tabs.add(iframes.get(i).getIframeTitle());
            }
        }
        if (tabs.size() > 4) {
            iv_wo_more.setVisibility(View.VISIBLE);
        }
        pagerAdapter = new SobotViewPagerAdapter(getSobotBaseContext(), getSupportFragmentManager(), tabs.toArray(new String[tabs.size()]), mFragments);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(mFragments.size());//加大预加载，防止切换fragment，fragment会清空
        mTab.setViewPager(viewPager);
        refresh();
    }

    @Override
    public void onClick(View view) {
        if (detailResultInfo == null || (detailResultInfo != null && detailResultInfo.getTicketDetail() == null)) {
            return;
        }
        if (view == rightMenu) {
            //弹窗，编辑工单，删除工单
            //弹窗
            SobotTitileMenuPop sobotSelectSearchTypePop = new SobotTitileMenuPop(SobotWODetailActivity.this, actionItems, SobotWODetailActivity.this);
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
            popupWindow.showAsDropDown(rightMenu, -15, 20);
//
        } else if (view == iv_wo_more) {
            //弹窗
            List<SobotActionItem> tabItems = new ArrayList<>();
            for (int i = 0; i < tabs.size(); i++) {
                tabItems.add(new SobotActionItem(tabs.get(i)));
            }
            SobotTitileMenuPop sobotSelectSearchTypePop = new SobotTitileMenuPop(SobotWODetailActivity.this, tabItems, new SobotTitileMenuPop.PopItemClick() {
                @Override
                public void onPopItemClick(int index) {
                    //切换到对应的tab
                    popupWindow.dismiss();
                    viewPager.setCurrentItem(index);
                }
            });
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
            popupWindow.showAsDropDown(iv_wo_more, -15, 20);

        } else if (view == tvReply) {
            if (tvReply.getText().equals(getString(R.string.sobot_detail_activation_order_string))) {
                //激活工单
                Intent intent = new Intent(getApplicationContext(), SobotActivateWOActivity.class);
                intent.putExtra(SobotConstantUtils.sobto_work_order_data, detailResultInfo.getTicketDetail());
                startActivityForResult(intent, SobotConstantUtils.sobot_wo_reply_response_need_update);
            } else {
                //回复工单
                Intent intent = new Intent(getApplicationContext(), SobotWOCreateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SobotConstantUtils.sobto_work_order_data, detailResultInfo.getTicketDetail());
                bundle.putInt(SobotConstantUtils.sobot_wo_display_type, SobotConstantUtils.sobot_wo_display_type_reply);
                intent.putExtras(bundle);
                startActivityForResult(intent, SobotConstantUtils.sobot_wo_reply_response_need_update);
            }
        } else if (view == tvTakeWorkOrder) {
            //接单
            zhiChiApi.acceptOrder(getSobotBaseActivity(), detailResultInfo.getTicketDetail().getTicketId(), new SobotResultCallBack<String>() {
                @Override
                public void onSuccess(String response) {
                    //提示
                    String msg = "";
                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.has("code") && !object.isNull("code")) {
                            if ("1".equals(object.optString("code"))) {
                                SobotToastUtil.showCustomToast(getBaseContext(), getString(R.string.sobot_wo_accept_order_string), R.drawable.sobot_icon_login_right);
                                //设置处理人的信息
                                SobotServiceInfoModel user = SobotLoginTools.getInstance().getServiceInfo();
                                if (user != null) {
                                    detailResultInfo.getTicketDetail().setDealUserName(user.getServiceName());
                                    detailResultInfo.getTicketDetail().setDealUserId(user.getServiceId());
                                }
                                //隐藏接单按钮
                                tvTakeWorkOrder.setVisibility(View.GONE);
                                SobotLiveEventBus.get(SobotConstantUtils.SOBOT_REFRESH_DATA).post(true);
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
                    com.sobot.widget.ui.toast.SobotToastUtil.showCustomToast(getBaseContext(), SobotStringUtils.isEmpty(msg) ? getString(R.string.sobot_wo_net_error_string) : msg, R.drawable.sobot_icon_warning_attention);
                    //只显示回复，隐藏接单
                    tvTakeWorkOrder.setVisibility(View.GONE);
                    //刷新数据
                    refresh();
                }

                @Override
                public void onFailure(Exception e, String s) {
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? getString(R.string.sobot_wo_net_error_string) : s, R.drawable.sobot_icon_warning_attention);

                }
            });
        } else if (view == tvBefore) {
            //上一个
            //新版
            if (position > 0) {
                position = position - 1;
                detailInfoId = items.get(position).getTicketId();
                customerId = items.get(position).getCustomerId();
                refresh();
                if (position == 0) {
                    tvBefore.setEnabled(false);
                }
            }
            tvNext.setEnabled(true);
        } else if (view == tvNext) {
            //下一个
            tvBefore.setEnabled(true);
            if (position < 0) {
                position = 0;
            }
            if (position < items.size() - 1) {
                position = position + 1;
                detailInfoId = items.get(position).getTicketId();
                customerId = items.get(position).getCustomerId();
                refresh();
                tvNext.setEnabled(true);
                if (position == items.size() - 1) {
                    if (isLoadMore) {
                        Intent intent = new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_REQUEST);
                        sendBroadcast(intent);
                    } else {
                        tvNext.setEnabled(false);
                    }
                }
            }

        } else if (view == conCernMenu) {
            if (detailResultInfo == null || detailResultInfo.getTicketDetail() == null) {
                return;
            }
            final boolean isConcern = !detailResultInfo.getTicketDetail().getConcerned();

            zhiChiApi.concernOrder(getSobotBaseActivity(), detailResultInfo.getTicketDetail().getTicketId(), isConcern, new SobotResultCallBack<SobotWOBaseSeconndCode>() {
                @Override
                public void onSuccess(SobotWOBaseSeconndCode s) {
                    if ("1".equals(s.getCode())) {
                        setOrderConcern(isConcern);
                        detailResultInfo.getTicketDetail().setIsConcerned(isConcern);
                    } else {
                        if (SobotStringUtils.isEmpty(s.getMsg())) {
                            SobotToastUtil.showCustomToast(getBaseContext(), s.getMsg());
                        }
                    }
                    //弹窗
                    if (isConcern) {
                        if (TextUtils.isEmpty(detailResultInfo.getTicketDetail().getConcernServiceName())) {//没有关注人
                            detailResultInfo.getTicketDetail().setConcernServiceName(SobotWorkOrderUtils.getServiceName(getBaseContext()));
                        } else {
                            detailResultInfo.getTicketDetail().setConcernServiceName(detailResultInfo.getTicketDetail().getConcernServiceName() + "," + SobotWorkOrderUtils.getServiceName(getBaseContext()));
                        }
                        SobotToastUtil.showCustomToast(getBaseContext(), getString(R.string.sobot_gaunzhu_successs));
                    } else {
                        if (detailResultInfo.getTicketDetail().getConcernServiceName().contains(SobotWorkOrderUtils.getServiceName(getBaseContext()))) {//关注人有自己
                            String concernName = detailResultInfo.getTicketDetail().getConcernServiceName().replaceFirst(SobotWorkOrderUtils.getServiceName(getBaseContext()) + ",", "");
                            if (detailResultInfo.getTicketDetail().getConcernServiceName().equals(concernName)) {
                                concernName = concernName.replaceFirst(SobotWorkOrderUtils.getServiceName(getBaseContext()), "");
                            }
                            detailResultInfo.getTicketDetail().setConcernServiceName(concernName);
                        }
                        SobotToastUtil.showCustomToast(getBaseContext(), getString(R.string.sobot_quxiao_guanzhu));
                    }
                    //更新页面信息
                    historyFragment.setTicketId(detailResultInfo.getTicketDetail().getTicketId());
                }

                @Override
                public void onFailure(Exception e, String s) {
                    if (SobotStringUtils.isEmpty(s)) {
                        SobotToastUtil.showCustomToast(getBaseContext(), s);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SobotLiveEventBus.get(SobotConstantUtils.SOBOT_REFRESH_DATA).post(true);
        if (requestCode == SobotConstantUtils.sobot_wo_reply_response_need_update) {
            //重新请求数据，并刷新三个fragment
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refresh();
                }
            }, 1000);

        }
    }


    //设置关注
    private void setOrderConcern(boolean isConcern) {
        if (conCernMenu.getVisibility() != View.VISIBLE) {
            conCernMenu.setVisibility(View.VISIBLE);
        }
        Drawable left;
        if (isConcern) {
            left = getResources().getDrawable(R.drawable.sobot_detail_concern_yes_selector);
        } else {
            left = getResources().getDrawable(R.drawable.sobot_detail_concern_no_selector);
        }
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        conCernMenu.setCompoundDrawables(left, null, null, null);

    }

    @Override
    public void onPopItemClick(int index) {
        popupWindow.dismiss();
        if (index == 0) {
            //编辑工单
            Intent intent = new Intent(getApplicationContext(), SobotWOCreateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(SobotConstantUtils.sobto_work_order_data, detailResultInfo.getTicketDetail());
            bundle.putInt(SobotConstantUtils.sobot_wo_display_type, SobotConstantUtils.sobot_wo_display_type_edit);
            intent.putExtras(bundle);
            startActivityForResult(intent, SobotConstantUtils.sobot_wo_reply_response_need_update);
        } else {
            //删除工单
            //弹框提示是否删除
            SobotCommonDialog dialog = new SobotCommonDialog(getString(R.string.sobot_delete_hint_string), getString(R.string.sobot_delete_string), new SobotCommonDialog.OnBtnClickListener() {
                @Override
                public void onClick() {
                    deleteOrder();
                }
            }, getString(R.string.sobot_cancle_string), new SobotCommonDialog.OnBtnClickListener() {
                @Override
                public void onClick() {
                }
            });
            dialog.show(SobotWODetailActivity.this.getSupportFragmentManager(), "dialog");
        }
    }

    private void deleteOrder() {
        showProgressDialog();
        zhiChiApi.deleteOrder(getSobotBaseActivity(), detailInfoId, new SobotResultCallBack<SobotWOBaseSeconndCode>() {
            @Override
            public void onSuccess(SobotWOBaseSeconndCode s) {
                dismissProgressDialog();
                if ("1".equals(s.getCode())) {
                    //
                    SobotToastUtil.showCustomToast(getBaseContext(), getString(R.string.sobot_delete_success_string));
                    SobotLiveEventBus.get(SobotConstantUtils.SOBOT_REFRESH_DATA).post(true);
                    finish();
                }
            }

            @Override
            public void onFailure(Exception e, String s) {
                dismissProgressDialog();
            }
        });
    }

    //工单编号的位置
    private void positionNowWorkOrderID() {
        if (ticketNum >= 2 && items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (detailInfoId.equals(items.get(i).getTicketId())) {
                    position = i;
                    break;
                }
            }
            postionInItems();
        } else {
            tvBefore.setEnabled(false);
            tvNext.setEnabled(false);
        }
    }

    private void postionInItems() {

        if (position == 0) {
            tvBefore.setEnabled(false);
            tvNext.setEnabled(true);
        } else if (position == items.size() - 1) {
            if (isLoadMore) {
                tvBefore.setEnabled(true);
                tvNext.setEnabled(true);
            } else {
                tvBefore.setEnabled(false);
                tvNext.setEnabled(true);
            }
        } else {
            tvBefore.setEnabled(true);
            tvNext.setEnabled(true);
        }

    }

    //广播 下一页
    private void registBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_RESULT);
        filter.addAction(SobotConstantUtils.SOBOT_BROADCAST_TICKET_NOMORE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcast, filter,Context.RECEIVER_EXPORTED);
        }else {
            registerReceiver(broadcast, filter);
        }
    }

    private BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_RESULT)) {
                ArrayList<SobotWOSecondItemModel> itemsitems = (ArrayList<SobotWOSecondItemModel>) intent.getSerializableExtra("items");
                isLoadMore = intent.getBooleanExtra("isLoadMore", false);
                if (itemsitems == null || itemsitems.size() == 0) {
                    tvNext.setEnabled(false);
                } else {
                    items.addAll(itemsitems);
                    ticketNum = items.size();
                }
            }
            if (intent.getAction().equals(SobotConstantUtils.SOBOT_BROADCAST_TICKET_NOMORE)) {
                tvNext.setEnabled(false);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
        if (updateCustomerObserver != null) {
            SobotLiveEventBus.get(SobotWoLiveDataBusKey.SOBOT_LIVEBUS_WO_UPDATE_CUSTOMER).removeObserver(updateCustomerObserver);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}