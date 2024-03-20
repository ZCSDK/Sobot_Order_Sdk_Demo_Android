package com.sobot.workorder.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.widget.refresh.layout.SobotRefreshLayout;
import com.sobot.widget.refresh.layout.api.RefreshLayout;
import com.sobot.widget.refresh.layout.footer.ClassicsFooter;
import com.sobot.widget.refresh.layout.header.ClassicsHeader;
import com.sobot.widget.refresh.layout.listener.OnLoadMoreListener;
import com.sobot.widget.refresh.layout.listener.OnRefreshListener;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotTicketEventAdapter;
import com.sobot.workorder.base.SobotWOBaseFragment;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.model.SobotTicketEventListModel;
import com.sobot.workorderlibrary.api.model.SobotTicketEventModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * 工单详情-历史
 *
 */
public class SobotWODetailHistoryFragment extends SobotWOBaseFragment {
    private static final String TAG = SobotWODetailHistoryFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private SobotRefreshLayout refreshLayout;
    private SobotLoadingLayout loading_layout;
    private boolean isHasMoreData;
    private List<SobotTicketEventModel> datas;


    private int pageNo = 1;//当前页数
    private final int pageSize = 10;//每页显示条数
    private SobotTicketEventAdapter adapter;

    private String ticketId;

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            ticketId = savedInstanceState.getString("ticketId");
        }
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sobot_fragment_wo_detail_history, container, false);
        initView(root);
        return root;
    }

    public void initView(View view) {
        refreshLayout = view.findViewById(R.id.sobot_srl_workorder_history);
        loading_layout = view.findViewById(R.id.sobot_loading_layout);
        recyclerView = view.findViewById(R.id.sp_recyclerView);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                if (isHasMoreData) {
                    getData(pageNo + 1);
                } else {
//                    refreshLayout.setNoMoreData(true);
                }

            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                getData(1);
//                refreshLayout.setNoMoreData(false);
            }
        });
        datas = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getSobotActivity()));
        adapter = new SobotTicketEventAdapter(getContext(),datas);
        recyclerView.setAdapter(adapter);
        if(!SobotStringUtils.isEmpty(ticketId)){
            getData(pageNo);
        }
        loading_layout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(1);
            }
        });
        loading_layout.showContent();
    }
    public void setTicketId(String id){
        ticketId = id;
        if(recyclerView!=null){
            pageNo=1;
            getData(pageNo);
        }
    }

    public void getData(final int pageindex) {
        if (TextUtils.isEmpty(ticketId)) {
//            recyclerView.showEmpty();
            return;
        }
        if(zhiChiApi!=null) {
            zhiChiApi.getOrderOperationHistory(this, ticketId, pageindex, pageSize, new SobotResultCallBack<SobotTicketEventListModel>() {
                @Override
                public void onSuccess(SobotTicketEventListModel s) {

                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();

                    if (pageindex == 1) {
                        datas.clear();
                    }
                    pageNo = pageindex;
                    if (s.getDealHisList().size() > 0) {
                        datas.addAll(s.getDealHisList());
                        adapter.addData(datas);

                        if(s.getDealHisList().size()<pageSize){
                            isHasMoreData = false;
                            refreshLayout.setEnableLoadMore(false);
                        }else{
                            isHasMoreData = true;
                            refreshLayout.setEnableLoadMore(true);
                        }
                        loading_layout.showContent();
                    } else {
                        //加载到最后一页
                        isHasMoreData = false;
                        if(pageindex==1 && s.getDealHisList().size()==0){
                            loading_layout.showEmpty();
                        }
                    }
                }

                @Override
                public void onFailure(Exception e, String s) {
                    SobotToastUtil.showCustomToast(getContext(),s);
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();
                    if(pageindex==1){
                        loading_layout.showEmpty();
                    }
                }
            });
        }

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(needRefresh){
            getData(1);
            needRefresh = false;
        }
    }

    @Override
    public void setNeedRefresh(boolean needRefresh) {
        super.setNeedRefresh(needRefresh);
        if(needRefresh){
            if(getUserVisibleHint()){
                getData(1);
                setNeedRefresh(false);
            }
        }
    }
}