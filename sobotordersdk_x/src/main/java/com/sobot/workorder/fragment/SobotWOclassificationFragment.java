package com.sobot.workorder.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sobot.common.ui.toast.SobotToastUtil;
import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.widget.refresh.layout.SobotRefreshLayout;
import com.sobot.widget.refresh.layout.api.RefreshLayout;
import com.sobot.widget.refresh.layout.footer.ClassicsFooter;
import com.sobot.widget.refresh.layout.header.ClassicsHeader;
import com.sobot.widget.refresh.layout.listener.OnRefreshListener;
import com.sobot.workorder.activity.SobotKickedActivity;
import com.sobot.workorder.activity.SobotWOTicketListActivity;
import com.sobot.workorder.adapter.SobotWOCenterAdapter;
import com.sobot.workorder.adapter.base.SobotOnItemClickListener;
import com.sobot.workorder.base.SobotWOBaseFragment;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotDialogUtils;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;
import com.sobot.workorderlibrary.api.model.SobotWOCenterItemModel;
import com.sobot.workorderlibrary.api.model.SobotWOCenterListModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单fragment
 * create by gqf.
 * 2022.04.24
 */
public class SobotWOclassificationFragment extends SobotWOBaseFragment implements SobotOnItemClickListener {

    private SobotRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SobotWOCenterAdapter adapter;
    private List<Object> datas;
    //本地广播数据类型实例。
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    private SobotLoadingLayout loading_layout;
    private boolean isFirst = false;
    private SobotServiceVoModel sobotServiceVoModel;

    public SobotWOclassificationFragment() {
        // Required empty public constructor
    }

    public static SobotWOclassificationFragment newInstance() {
        return new SobotWOclassificationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBrocastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View root = inflater.inflate(getResLayoutId("sobot_fragment_oclassification"), container, false);
        initView(root);
        isFirst = true;
        sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(getContext()).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
        if(sobotServiceVoModel!=null){
            initData();
        }else{
            //获取用户详情
            zhiChiApi.getServiceDataInfo(this, new SobotResultCallBack<SobotServiceVoModel>() {
                @Override
                public void onSuccess(SobotServiceVoModel sobotServiceVoModel) {
                    if (sobotServiceVoModel != null) {
                        SobotSharedPreferencesUtil.getInstance(getContext()).put(SobotWOConstant.SOBOT_LOGIN_USER_INFO, sobotServiceVoModel);
                    }
                    initData();
                }

                @Override
                public void onFailure(Exception e, String des) {
                    initData();
                }
            });
        }
        return root;
    }

    private void initView(View view) {
        refreshLayout = (SobotRefreshLayout) view.findViewById(getResId("sobot_srl_workorder_classification"));
        loading_layout = view.findViewById(getResId("sobot_loading_layout"));
        recyclerView = (RecyclerView) view.findViewById(getResId("sobot_rv_workorder_classification"));
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setEnableLoadMore(false);
        datas = new ArrayList<>();
        adapter = new SobotWOCenterAdapter(getContext(), datas);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                initData();
            }
        });
        loading_layout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        loading_layout.showContent();
    }

    protected void initData() {
        if (isFirst) {
            //显示加载中
            SobotDialogUtils.startProgressDialog(getSobotActivity());
        }
        if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
            zhiChiApi.getWorkOrderListNew(this, new SobotResultCallBack<List<SobotWOCenterItemModel>>() {
                @Override
                public void onSuccess(List<SobotWOCenterItemModel> sobotWOCenterItemModels) {
                    if (isFirst) {
                        //关闭加载中
                        SobotDialogUtils.stopProgressDialog(getSobotActivity());
                        isFirst = false;
                    }
                    loading_layout.showContent();
                    refreshLayout.finishRefresh();
                    datas.clear();
                    if(sobotWOCenterItemModels.size()>0){
                        datas.addAll(sobotWOCenterItemModels);
                        adapter.setList(datas);
                    } else {
                        loading_layout.showEmpty();
                    }
                }

                @Override
                public void onFailure(Exception e, String s) {
                    if (isFirst) {
                        //关闭加载中
                        SobotDialogUtils.stopProgressDialog(getSobotActivity());
                        isFirst = false;
                    }
                    SobotLogUtils.d("======请求失败===" + s);
                    refreshLayout.finishRefresh();
                    loading_layout.showEmpty();
                }
            });
        }else {
            zhiChiApi.getWorkOrderList(this, new SobotResultCallBack<List<SobotWOCenterListModel>>() {
                @Override
                public void onSuccess(List<SobotWOCenterListModel> sobotWOCenterListModels) {
                    if (isFirst) {
                        //关闭加载中
                        SobotDialogUtils.stopProgressDialog(getSobotActivity());
                        isFirst = false;
                    }
                    loading_layout.showContent();
                    refreshLayout.finishRefresh();
                    SobotLogUtils.d("======请求成功===" + sobotWOCenterListModels.size());
                    datas.clear();
                    if (sobotWOCenterListModels != null && sobotWOCenterListModels.size() > 0) {
                        for (int i = 0; i < sobotWOCenterListModels.size(); i++) {
                            SobotWOCenterListModel bean = sobotWOCenterListModels.get(i);
                            List<SobotWOCenterItemModel> list = bean.getItemNewList();
                            datas.add(bean.getItemName());
                            datas.addAll(list);
                        }
                        adapter.setList(datas);
                    } else {
                        loading_layout.showEmpty();
                    }

                }

                @Override
                public void onFailure(Exception e, String s) {
                    if (isFirst) {
                        //关闭加载中
                        SobotDialogUtils.stopProgressDialog(getSobotActivity());
                        isFirst = false;
                    }
                    SobotLogUtils.d("======请求失败===" + s);
                    refreshLayout.finishRefresh();
                    loading_layout.showEmpty();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SobotConstantUtils.SOBOT_NEED_REFRESH && resultCode == SobotConstantUtils.SOBOT_NEED_REFRESH) {
            //result=SOBOT_NEED_REFRESH，需要刷新
            initData();
        }
    }

    @Override
    public void onItemClick(Object info, int index) {
        if (info instanceof SobotWOCenterItemModel) {

            SobotWOCenterItemModel modelResult = (SobotWOCenterItemModel) info;
            Intent intent = new Intent(getSobotActivity(), SobotWOTicketListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("modelResult", modelResult);
            intent.putExtra("bundle", bundle);
            startActivityForResult(intent, SobotConstantUtils.SOBOT_NEED_REFRESH);
        }
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent data) {
            try {
                SobotLogUtils.i("广播是  :" + data.getAction());
                if (SobotWOConstant.SOBOT_BROADCAST_SESSION_TIMEOUT.equals(data.getAction())) {
                    int logintype = SobotSharedPreferencesUtil.getInstance(getContext()).get(SobotWOConstant.SOBOT_KEY_LOGINTYPE, 0);
                    if (logintype == 0) {
                        Intent kickedIntent = new Intent(getContext(), SobotKickedActivity.class);
                        startActivityForResult(kickedIntent, SobotConstantUtils.SOBOT_NEED_REFRESH);
                    } else {
                        if (data != null) {
                            SobotToastUtil.showCustomToast(getContext(), data.getStringExtra("tipmsg"));
                        } else {
                            SobotToastUtil.showCustomToast(getContext(), "请求失败！登录已失效，请重新登录！");
                        }
                        SobotGlobalContext.getInstance().finishAllActivity();
                    }
                    SobotSharedPreferencesUtil.getInstance(getSobotActivity()).remove(SobotWOConstant.SOBOT_LOGIN_USER_INFO);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initBrocastReceiver() {
        if (localReceiver == null) {
            localReceiver = new LocalReceiver();
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(SobotWOConstant.SOBOT_BROADCAST_SESSION_TIMEOUT);
        // 注册广播接收器
        localBroadcastManager.registerReceiver(localReceiver, localFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            // 取消广播接受者
            if (localBroadcastManager != null) {
                localBroadcastManager.unregisterReceiver(localReceiver);
            }
            if (localReceiver != null) {
                localReceiver = null;
            }
        } catch (Exception e) {
            //ignor
        }
    }
}