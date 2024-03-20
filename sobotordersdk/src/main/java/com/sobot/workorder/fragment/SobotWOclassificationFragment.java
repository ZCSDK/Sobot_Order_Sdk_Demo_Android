package com.sobot.workorder.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.common.login.SobotLoginTools;
import com.sobot.common.login.model.SobotServiceInfoModel;
import com.sobot.common.utils.SobotGlobalContext;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.widget.livedatabus.SobotLiveEventBus;
import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.widget.refresh.layout.SobotRefreshLayout;
import com.sobot.widget.refresh.layout.api.RefreshLayout;
import com.sobot.widget.refresh.layout.footer.ClassicsFooter;
import com.sobot.widget.refresh.layout.header.ClassicsHeader;
import com.sobot.widget.refresh.layout.listener.OnRefreshListener;
import com.sobot.workorder.R;
import com.sobot.workorder.activity.SobotKickedActivity;
import com.sobot.workorder.activity.SobotWOTicketListActivity;
import com.sobot.workorder.adapter.SobotWOCenterAdapter;
import com.sobot.workorder.adapter.base.SobotOnItemClickListener;
import com.sobot.workorder.base.SobotWOBaseFragment;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotDialogUtils;
import com.sobot.workorder.utils.SobotTicketDictUtil;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotTiketNumModel;
import com.sobot.workorderlibrary.api.model.SobotWOCenterItemModel;
import com.sobot.workorderlibrary.api.model.SobotWOCenterListModel;
import com.sobot.workorderlibrary.api.model.SobotWODictModelResult;
import com.sobot.workorderlibrary.utils.SobotWOSPUtil;

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
    private LocalReceiver localReceiver;
    private SobotLoadingLayout loading_layout;
    private boolean isFirst = false;
    private SobotServiceInfoModel sobotServiceVoModel;

    private Observer refreshObserver;//刷新列表的广播
    private boolean isRefresh = false;

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
        View root = inflater.inflate(R.layout.sobot_fragment_oclassification, container, false);
        initView(root);
        initObserverTag();
        isFirst = true;
        sobotServiceVoModel = SobotLoginTools.getInstance().getServiceInfo();
        if (sobotServiceVoModel != null) {
            if (sobotServiceVoModel.getNewBossSwitch() > 0) {
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
                        if (sobotWOCenterItemModels.size() > 0) {
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
            }
            initData();
        } else {
            //获取用户详情
            zhiChiApi.queryAppAgentMenus(this, new SobotResultCallBack<SobotServiceInfoModel>() {
                @Override
                public void onSuccess(SobotServiceInfoModel serviceVoModel) {
                    if (serviceVoModel != null) {
                        sobotServiceVoModel = serviceVoModel;
                        SobotLoginTools.getInstance().setServiceInfo(sobotServiceVoModel);
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
                                if (sobotWOCenterItemModels.size() > 0) {
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
        refreshLayout = (SobotRefreshLayout) view.findViewById(R.id.sobot_srl_workorder_classification);
        loading_layout = view.findViewById(R.id.sobot_loading_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.sobot_rv_workorder_classification);
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

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isRefresh = false;
                    initData();
                }
            }, 2000);

        }
    }

    protected void initData() {
        if (isFirst) {
            //显示加载中
            SobotDialogUtils.startProgressDialog(getSobotActivity());
        }
        if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {

            zhiChiApi.getFilterTicketNum(this, new SobotResultCallBack<List<SobotTiketNumModel>>() {
                @Override
                public void onSuccess(List<SobotTiketNumModel> list) {
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            if (isFirst) {
                                //关闭加载中
                                SobotDialogUtils.stopProgressDialog(getSobotActivity());
                                isFirst = false;
                            }
                            loading_layout.showContent();
                            refreshLayout.finishRefresh();
                            datas.clear();
                            if (list.size() > 0) {
                                datas.addAll(list);
                                adapter.setList(datas);
                            } else {
                                loading_layout.showEmpty();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Exception e, String s) {
                    refreshLayout.finishRefresh();
                }
            });
        } else {
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
        //查询工单来源
        if (null == SobotTicketDictUtil.getDictSource() || SobotTicketDictUtil.getDictSource().size() == 0) {
            if (zhiChiApi != null) {
                zhiChiApi.queryDictDataList(this, new SobotResultCallBack<List<SobotWODictModelResult>>() {
                    @Override
                    public void onSuccess(List<SobotWODictModelResult> sobotWODictModelResults) {
                        SobotTicketDictUtil.setDictSource(sobotWODictModelResults);
                    }

                    @Override
                    public void onFailure(Exception e, String s) {

                    }
                });
            }
        }
    }

    private void initObserverTag() {
        refreshObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                isRefresh = true;
            }
        };
        SobotLiveEventBus.get(SobotConstantUtils.SOBOT_REFRESH_DATA).observeForever(refreshObserver);
    }

    @Override
    public void onItemClick(Object info, int index) {
        if (info instanceof SobotTiketNumModel) {
            SobotTiketNumModel bean = (SobotTiketNumModel) info;
            SobotWOCenterItemModel modelResult = new SobotWOCenterItemModel();
            modelResult.setCompanyId(bean.getCompanyId());
            modelResult.setTicketNum(bean.getTicketNum());
            modelResult.setTaskName(bean.getFilterName());
            modelResult.setTaskId(bean.getFilterId());
            modelResult.setTaskStatus(bean.getFilterStatus());
            modelResult.setCreateTime(bean.getCreateTime());
            modelResult.setTaskType(bean.getFilterType());
            Intent intent = new Intent(getSobotActivity(), SobotWOTicketListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("modelResult", modelResult);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        } else if (info instanceof SobotWOCenterItemModel) {
            SobotWOCenterItemModel modelResult = (SobotWOCenterItemModel) info;
            Intent intent = new Intent(getSobotActivity(), SobotWOTicketListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("modelResult", modelResult);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    //上次token失效时间，两次间隔时间小于2s 不处理
    private long lastSessionTimeoutTime;

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent data) {
            try {
                SobotLogUtils.i("广播是  :" + data.getAction());
                if (SobotWOConstant.SOBOT_BROADCAST_SESSION_TIMEOUT.equals(data.getAction())) {
                    long intervalTime = System.currentTimeMillis() - lastSessionTimeoutTime;
//                    SobotLogUtils.i(System.currentTimeMillis() + "======" + lastSessionTimeoutTime + "-----" + intervalTime);
                    if (intervalTime > 2000) {
                        lastSessionTimeoutTime = System.currentTimeMillis();
                        //登录方式 1 ：app，0：sdk
                        int logintype = SobotWOSPUtil.getInstance(getSobotActivity()).get(SobotWOConstant.SOBOT_KEY_LOGIN_FROM, 0);
                        if (logintype == 0) {
                            if (SobotLoginTools.getInstance().getLoginUser() != null) {
                                String loginUser = SobotLoginTools.getInstance().getLoginUser().getLoginAccount();
                                String loginPwd = SobotLoginTools.getInstance().getLoginUser().getLoginPwd();
                                Intent intent = new Intent(getSobotActivity(), SobotKickedActivity.class);
                                intent.putExtra("loginUser", loginUser);
                                intent.putExtra("loginPwd", loginPwd);
                                startActivity(intent);
                            } else {
                                SobotGlobalContext.getInstance(getSobotActivity()).finishAllActivity();
                            }
                        }
                    }
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
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(SobotWOConstant.SOBOT_BROADCAST_SESSION_TIMEOUT);
        // 注册广播接收器
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSobotActivity().registerReceiver(localReceiver, localFilter, Context.RECEIVER_EXPORTED);
        } else {
            getSobotActivity().registerReceiver(localReceiver, localFilter);
        }
    }

    @Override
    public void onDestroy() {
        try {
            //关闭加载中
            SobotDialogUtils.stopProgressDialog(getSobotActivity());
            // 取消广播接受者
            if (localReceiver != null) {
                getSobotActivity().unregisterReceiver(localReceiver);
            }
            if (localReceiver != null) {
                localReceiver = null;
            }
        } catch (Exception e) {
            //ignor
        }
        if (refreshObserver != null) {
            SobotLiveEventBus.get(SobotConstantUtils.SOBOT_REFRESH_DATA).removeObserver(refreshObserver);
        }
        super.onDestroy();
    }
}