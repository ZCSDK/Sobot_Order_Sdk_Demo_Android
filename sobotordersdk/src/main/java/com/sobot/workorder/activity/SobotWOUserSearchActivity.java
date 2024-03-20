package com.sobot.workorder.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.sobot.widget.ui.statusbar.SobotStatusBarUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotWOUserSearchAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotSoftInputManage;
import com.sobot.workorder.weight.dialog.SobotChoiceUserDialog;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.model.SobotTicketTimeModel;
import com.sobot.workorderlibrary.api.model.SobotWOUser;
import com.sobot.workorderlibrary.api.model.SobotWOUserList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//工单对应客户 搜索界面
public class SobotWOUserSearchActivity extends SobotWOBaseActivity implements View.OnClickListener, SobotWOUserSearchAdapter.OnItemClickListener {

    public int SOBOT_ADD_USER_REQUEST_CODE = 0X1100;
    private View titlebar;
    private EditText work_order_user_search_etSearch;
    private Button work_order_user_search_btnCancle;
    private ImageView work_order_user_search_ivDeleteText;
    private SobotRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SobotWOUserSearchAdapter adapter;
    private ArrayList<SobotWOUser> datas;
    private SobotLoadingLayout loading_layout;

    //右上角创建用户按钮
    private TextView sobot_tv_right_search;

    //分页
    private int pageNo = 1, pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_wo_search_user;
    }

    @Override
    protected void initView() {
        setTitle(getResString("sobot_choose_customer_string"));
        titlebar = getToolBar();
        sobot_tv_right_search = findViewById(R.id.sobot_tv_right);
        sobot_tv_right_search.setVisibility(View.VISIBLE);
        loading_layout = findViewById(R.id.loading_layout);
        loading_layout.showContent();
        Drawable left2 = getResources().getDrawable(R.drawable.sobot_icon_custom_title_right_invite_normal);
        left2.setBounds(0, 0, left2.getMinimumWidth(), left2.getMinimumHeight());
        sobot_tv_right_search.setCompoundDrawables(left2, null, null, null);
        sobot_tv_right_search.setOnClickListener(this);
        work_order_user_search_etSearch = (EditText) findViewById(R.id.work_order_user_search_etSearch);
        work_order_user_search_btnCancle = (Button) findViewById(R.id.work_order_user_search_btnCancle);
        work_order_user_search_btnCancle.setVisibility(View.GONE);
        work_order_user_search_ivDeleteText = (ImageView) findViewById(R.id.work_order_user_search_ivDeleteText);
        refreshLayout = (SobotRefreshLayout) findViewById(R.id.sobot_srl_workorder_user_search);
        recyclerView = (RecyclerView) findViewById(R.id.sobot_rv_workorder_user_search_list);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadMore(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SobotWOUserSearchActivity.this));
        datas = new ArrayList<>();
        adapter = new SobotWOUserSearchAdapter(SobotWOUserSearchActivity.this, datas);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                getData(pageNo + 1);
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                pageNo = 1;
                datas.clear();
                getData(pageNo);
            }
        });
        initViewListener();
    }

    @SuppressLint("CheckResult")
    private void initViewListener() {
        work_order_user_search_btnCancle.setOnClickListener(this);
        work_order_user_search_ivDeleteText.setOnClickListener(this);
        work_order_user_search_etSearch.setOnClickListener(this);
        work_order_user_search_etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        work_order_user_search_etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    pageNo = 1;
                    datas.clear();
                    getData(pageNo);
                    return true;
                }
                return false;
            }
        });
        work_order_user_search_etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    work_order_user_search_ivDeleteText.setVisibility(View.VISIBLE);
                } else {
                    work_order_user_search_etSearch.setHint(R.string.work_order_user_search_etSearch_hint);
                    work_order_user_search_ivDeleteText.setVisibility(View.GONE);
                }
                pageNo = 1;
                datas.clear();
                getData(pageNo);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void initData() {
        pageNo = 1;
        datas.clear();
        getData(pageNo);
    }

    /**
     * @param pageIndex 请求的页数
     */
    private void getData(final int pageIndex) {
        SobotWOUserList param = new SobotWOUserList();
        param.setPageSize(pageSize);
        param.setQueryContent(work_order_user_search_etSearch.getText().toString());
        param.setPageNo(pageIndex);
        zhiChiApi.getAppUserList(this, param, new SobotResultCallBack<List<SobotWOUser>>() {
            @Override
            public void onSuccess(List<SobotWOUser> workOrderUsers) {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                pageNo = pageIndex;
                if (pageIndex == 1) {
                    datas.clear();
                }
                if (workOrderUsers != null) {
                    if (workOrderUsers.size() == 0) {
                        if (pageIndex == 1) {
                            loading_layout.showEmpty();
                        }
                        refreshLayout.setNoMoreData(true);
                    } else {
                        if (workOrderUsers.size() < pageSize) {
                            refreshLayout.setNoMoreData(true);
                        } else {
                            refreshLayout.setNoMoreData(false);
                        }
                        loading_layout.showContent();
                        datas.addAll(workOrderUsers);
                        String searchContent = work_order_user_search_etSearch.getText().toString();
                        if (!TextUtils.isEmpty(searchContent)) {
                            for (int i = 0; i < datas.size(); i++) {
                                SobotWOUser workOrderUser = datas.get(i);
                                if (workOrderUser != null && !TextUtils.isEmpty(workOrderUser.getNick())) {
                                    try {
                                        String nick = workOrderUser.getNick().replaceFirst(searchContent, "<font  color=\"#09aeb0\">" + searchContent + "</font>");
                                        workOrderUser.setTmpNick(nick);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                refreshLayout.finishRefresh();
                refreshLayout.finishLoadMore();
                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? getString(R.string.sobot_wo_net_error_string):des , R.drawable.sobot_icon_warning_attention);

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.work_order_user_search_btnCancle) {
            if (work_order_user_search_btnCancle.getText().toString().trim().equals(getString(R.string.sobot_cancle_string))) {
                SobotSoftInputManage.hideInputMode(SobotWOUserSearchActivity.this);
                titlebar.setVisibility(View.VISIBLE);
                work_order_user_search_btnCancle.setVisibility(View.GONE);
                SobotStatusBarUtils.setStatusBarColor(this, getResColor("sobot_status_bar_color"));
            }
        } else if (id == R.id.work_order_user_search_ivDeleteText) {
            work_order_user_search_etSearch.setText("");
            pageNo = 1;
            datas.clear();
            getData(pageNo);
        } else if (id == R.id.work_order_user_search_etSearch) {
            titlebar.setVisibility(View.GONE);
            work_order_user_search_btnCancle.setVisibility(View.VISIBLE);
            SobotStatusBarUtils.setStatusBarColor(this, getResColor("sobot_wo_white_color"));
        } else if (id == sobot_tv_right_search.getId()) {
            Intent intent = new Intent(SobotWOUserSearchActivity.this, SobotWOAddUserActivity.class);
            intent.putExtra("workFlag", false);//这个属性用来表示是编辑用户信息还是新增用户信息。false表示新增用户信息
            startActivityForResult(intent, SOBOT_ADD_USER_REQUEST_CODE);
        }

    }


    @Override
    public void onItemClick(Object info, final int position) {
        if(position >= 0 && position < datas.size()) {
            zhiChiApi.getUserCorrespondTicketInfo(SobotWOUserSearchActivity.this, datas.get(position).getId(), new SobotResultCallBack<SobotTicketTimeModel>() {
                @Override
                public void onSuccess(final SobotTicketTimeModel ticketModel) {
                    if (ticketModel != null && ticketModel.getItem() != null) {
                        String time = ticketModel.getTime();
                        SobotChoiceUserDialog dialog = new SobotChoiceUserDialog(String.format(getResString("sobotgongdancreated"), time),
                                getResString("sobot_continue_adding_string"), "", new SobotChoiceUserDialog.OnBtnClickListener() {
                            @Override
                            public void onClick() {
                                Intent intent = new Intent();
                                intent.putExtra("userId", datas.get(position).getId());
                                intent.putExtra("userName", datas.get(position).getNick());
                                setResult(SobotConstantUtils.sobot_wo_list_display_type_user_center, intent);
                                finish();
                            }
                        }, getResString("sobot_see_order_string"), new SobotChoiceUserDialog.OnBtnClickListener() {
                            @Override
                            public void onClick() {
                                if (ticketModel.getItem() != null && !TextUtils.isEmpty(ticketModel.getItem().getTicketId())) {
                                    Intent intent = new Intent(SobotWOUserSearchActivity.this, SobotWODetailActivity.class);
                                    intent.putExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_TICKETID, ticketModel.getItem().getTicketId());
                                    startActivity(intent);
                                }
                            }
                        }, "");
                        dialog.show(SobotWOUserSearchActivity.this.getSupportFragmentManager(), "dialog");
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("userId", datas.get(position).getId());
                        intent.putExtra("userName", datas.get(position).getNick());
                        setResult(SobotConstantUtils.sobot_wo_list_display_type_user_center, intent);
                        finish();
                    }

                }

                @Override
                public void onFailure(Exception e, String des) {
                    if (e instanceof IllegalStateException) {
                        Intent intent = new Intent();
                        intent.putExtra("userId", datas.get(position - 1).getId());
                        intent.putExtra("userName", datas.get(position - 1).getNick());
                        setResult(SobotConstantUtils.sobot_wo_list_display_type_user_center, intent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SOBOT_ADD_USER_REQUEST_CODE) {
                pageNo = 1;
                datas.clear();
                getData(pageNo);
            }
        }
    }
}