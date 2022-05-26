package com.sobot.workorder.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.workorder.activity.SobotWODetailActivity;
import com.sobot.workorder.adapter.SobotWorkOrderDetailReplyAdapter;
import com.sobot.workorder.adapter.base.SobotOnItemClickListener;
import com.sobot.workorder.base.SobotWOBaseFragment;
import com.sobot.workorder.utils.SobotDateUtil;
import com.sobot.workorder.utils.SobotTicketDictUtil;
import com.sobot.workorder.utils.SobotWorkOrderUtils;
import com.sobot.workorder.weight.SobotExpandableTextView;
import com.sobot.workorder.weight.dialog.SobotCommonDialog;
import com.sobot.workorderlibrary.api.apiutils.SobotWOBaseSeconndCode;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfigModel;
import com.sobot.workorderlibrary.api.model.SobotOrderReplyItemModel;
import com.sobot.workorderlibrary.api.model.SobotTicketModel;
import com.sobot.workorderlibrary.api.model.SobotTicketResultListModel;
import com.sobot.workorderlibrary.api.model.SobotWODetailResultModel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * 工单详情
 *
 */
public class SobotWODetailFragment extends SobotWOBaseFragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = SobotWODetailFragment.class.getSimpleName();

    SobotTicketModel ticketDetail;
    SobotWODetailResultModel detailResultModel;
    TextView tvWorkOrderDetailInfoTitle;
    TextView tvWorkOrderDetailInfoStatus;
    TextView tvWorkOrderDetailInfoTime;
    LinearLayout ll_detail_info;
    CheckBox cb_detail_info;
    TextView tvUpdateTime;
    TextView tvTicketLevel;
    TextView tvTicketTypeName;
    TextView tvDealGroupName;
    TextView tvDealUserName;
    TextView tvOrderUserName;
    LinearLayout llFileContainer;
    ListView lllv_reply_container;
    TextView tv_reply_empty;
    View vSecondDivider;
    TextView tvReplyTitle;
    LinearLayout vSecondLine;
    LinearLayout ll_result_list_container;
    LinearLayout ll_result_list_container2;
    LinearLayout ll_result_list_container3;
    LinearLayout v_second_line_result_divider;
    LinearLayout v_second_line_result_divider1;
    SobotExpandableTextView expand_text_view;
    ScrollView sv_container;
    TextView tvOrderConcernUserName;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View root = inflater.inflate(getResLayoutId("sobot_fragment_wo_detail"), container, false);
        initView(root);
        return root;
    }

    public void initView(View rootView) {
        tvWorkOrderDetailInfoTitle = rootView.findViewById(getResId("tv_work_order_detail_info_title"));
        tvWorkOrderDetailInfoStatus = rootView.findViewById(getResId("tv_work_order_detail_info_status"));
        tvWorkOrderDetailInfoTime = rootView.findViewById(getResId("tv_work_order_detail_info_time"));
        ll_detail_info = rootView.findViewById(getResId("ll_detail_info"));
        cb_detail_info = rootView.findViewById(getResId("cb_detail_info"));
        tvUpdateTime = rootView.findViewById(getResId("tv_update_time"));
        tvTicketLevel = rootView.findViewById(getResId("tv_ticket_level"));
        tvTicketTypeName = rootView.findViewById(getResId("tv_ticketTypeName"));
        tvDealGroupName = rootView.findViewById(getResId("tv_dealGroupName"));
        tvDealUserName = rootView.findViewById(getResId("tv_dealUserName"));
        tvOrderUserName = rootView.findViewById(getResId("tv_orderUserName"));
        llFileContainer = rootView.findViewById(getResId("ll_file_container"));
        lllv_reply_container = rootView.findViewById(getResId("lllv_reply_container"));
        tv_reply_empty = rootView.findViewById(getResId("tv_reply_empty"));
        vSecondDivider = rootView.findViewById(getResId("v_second_divider"));
        tvReplyTitle = rootView.findViewById(getResId("tv_reply_title"));
        vSecondLine = rootView.findViewById(getResId("v_second_line"));
        ll_result_list_container = rootView.findViewById(getResId("ll_result_list_container"));
        ll_result_list_container2 = rootView.findViewById(getResId("ll_result_list_container2"));
        ll_result_list_container3 = rootView.findViewById(getResId("ll_result_list_container3"));
        v_second_line_result_divider = rootView.findViewById(getResId("v_second_line_result_divider"));
        v_second_line_result_divider1 = rootView.findViewById(getResId("v_second_line_result_divider1"));
        expand_text_view = rootView.findViewById(getResId("expand_text_view"));
        sv_container = rootView.findViewById(getResId("sv_container"));
        tvOrderConcernUserName = rootView.findViewById(getResId("tv_order_concern_UserName"));

        cb_detail_info.setOnCheckedChangeListener(this);
        if(detailResultModel!=null){
            updateData();
        }
    }

    public void setDetailResultModel(SobotWODetailResultModel data){
        detailResultModel = data;
        if(tvWorkOrderDetailInfoTitle!=null){
            updateData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(needRefresh){
            updateData();
            needRefresh = false;
        }
    }

    @Override
    public void setNeedRefresh(boolean needRefresh) {
        super.setNeedRefresh(needRefresh);
        if(needRefresh){
            if(getUserVisibleHint()){
                updateData();
                setNeedRefresh(false);
            }
        }
    }

    public void updateData() {
        if (getActivity() == null || !isAdded() || detailResultModel == null || detailResultModel.getTicketDetail() == null) {
            return;
        }
        try {
            sv_container.smoothScrollTo(0, 0);
            ticketDetail = detailResultModel.getTicketDetail();
            //设置标题
            tvWorkOrderDetailInfoTitle.setText(ticketDetail.getTicketTitle());
            //设置状态
            tvWorkOrderDetailInfoStatus.setText(SobotTicketDictUtil.getStatusName(getSobotActivity(),ticketDetail.getTicketStatus() + ""));
            tvWorkOrderDetailInfoStatus.setBackgroundResource(SobotTicketDictUtil.getStatusBgResId(getSobotActivity(),ticketDetail.getTicketStatus() + ""));
            tvWorkOrderDetailInfoStatus.setTextColor(SobotTicketDictUtil.getStatusColor(ticketDetail.getTicketStatus()));
            //设置时间
            tvWorkOrderDetailInfoTime.setText("创建时间：" + SobotDateUtil.timeStamp2Date
                    (ticketDetail.getCreateTime() + "", "yyyy-MM-dd HH:mm"));
            expand_text_view.setText(Html.fromHtml(ticketDetail.getTicketContent()));
            llFileContainer.removeAllViews();
            SobotWorkOrderUtils.updateFileList(getContext(), ticketDetail.getFileList(), llFileContainer);

            if (!TextUtils.isEmpty(ticketDetail.getVoiceUrl()) && ticketDetail.getVoiceUrl().startsWith("http")) {
            //去掉了语音控件2022.04.28
            }

            //显示关注人
            setConcernText();


            //显示自定义字段
            List<List<SobotTicketResultListModel>> processResults = processResultList(ticketDetail);

            SobotWorkOrderUtils.updateWorkOderResultListView(getContext(), processResults.get(0), ll_result_list_container, true);
            boolean isShowDivider = processResults.get(1) != null && processResults.get(1).size() > 0;
            v_second_line_result_divider.setVisibility(isShowDivider ? View.VISIBLE : View.GONE);
            SobotWorkOrderUtils.updateWorkOderResultListView(getContext(), processResults.get(1), ll_result_list_container2, false);
            boolean isShowDivider1 = processResults.get(2) != null && processResults.get(2).size() > 0;
            v_second_line_result_divider1.setVisibility(isShowDivider1 ? View.VISIBLE : View.GONE);
            SobotWorkOrderUtils.updateWorkOderResultListView(getContext(), processResults.get(2), ll_result_list_container3, false);

            //显示回复
            List<SobotOrderReplyItemModel> replyList = detailResultModel.getReplyList();
            if (replyList == null || replyList.size() == 0) {
                lllv_reply_container.setVisibility(View.GONE);
                tv_reply_empty.setVisibility(View.VISIBLE);
            } else {
                lllv_reply_container.setVisibility(View.VISIBLE);
                tv_reply_empty.setVisibility(View.GONE);
                //已删除或者已关闭工单不能修改
                SobotWorkOrderDetailReplyAdapter adapter = new SobotWorkOrderDetailReplyAdapter(getActivity(), replyList, !(ticketDetail.getTicketStatus() == 99 || ticketDetail.getTicketStatus() == 98));
                adapter.setDeleteListener(new SobotOnItemClickListener() {
                    @Override
                    public void onItemClick(final Object info, int index) {
                        //弹框提示是否删除
                        SobotCommonDialog dialog = new SobotCommonDialog(SobotResourceUtils.getResString(getContext(),"sobot_delete_hint_string"), SobotResourceUtils.getResString(getContext(),"sobot_delete_string"), new SobotCommonDialog.OnBtnClickListener() {
                            @Override
                            public void onClick() {
                                deleteReply((String)info);
                            }
                        }, SobotResourceUtils.getResString(getContext(),"sobot_cancle_string"), new SobotCommonDialog.OnBtnClickListener() {
                            @Override
                            public void onClick() {
                            }
                        });
                        dialog.show(getActivity().getSupportFragmentManager(), "dialog");
                    }
                });
                lllv_reply_container.setAdapter(adapter);
//                setListViewHeightBasedOnChildren(lllv_reply_container);
            }

        } catch (Exception e) {
            //ignor
        }
    }

    @Override
    public void setArguments(@Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        super.setArguments(args);
    }

    /**
     * 显示关注人
     */
    private void setConcernText() {
//        StringBuffer concernString = new StringBuffer();
//        concernString.append("--");
//        List<SobotTicketConcernUserModel> concernUsers = ticketDetail.getConcernUsers();
//        if (null != concernUsers && concernUsers.size() > 0) {
//            concernString.setLength(0);
//            for (SobotTicketConcernUserModel concernUser : concernUsers) {
//                concernString.append(concernUser.getUserName());
//                concernString.append("、");
//            }
//            concernString.deleteCharAt(concernString.lastIndexOf("、"));
//        }
        if (!TextUtils.isEmpty(ticketDetail.getConcernServiceName())) {
            tvOrderConcernUserName.setText("关注人：" + ticketDetail.getConcernServiceName());
        } else {
            tvOrderConcernUserName.setText("关注人：--");
        }
//        tvOrderConcernUserName.setText("关注人：" + concernString.toString());
    }

    /**
     * 根据templateFieldList(当前工单类型模板自定义字段)
     * 将自定义字段的内容筛选出来
     *
     * @param ticketDetail
     * @return List<List < TicketResultListModel>>
     * index:0  当前模板中的自定义字段的值
     * index:1  非当前模板中的自定义字段的值
     * index:2  当前模板中停用的字段
     */
    private List<List<SobotTicketResultListModel>> processResultList(SobotTicketModel ticketDetail) {
        List<List<SobotTicketResultListModel>> result = new ArrayList<>();
        if (ticketDetail != null) {
            //当前模板的字段
            List<SobotCusFieldConfigModel> templateFieldList = ticketDetail.getTemplateFieldList();
            //当前模板的字段 id集合
            LinkedHashSet<String> lSet = new LinkedHashSet<>();
            //当前模板的可见字段 id集合
            LinkedHashSet<String> canLookIdSet = new LinkedHashSet<>();
            for (SobotCusFieldConfigModel tmpConfig : templateFieldList) {
                if (tmpConfig != null) {
                    lSet.add(tmpConfig.getFieldId());
                    if (tmpConfig.getAuthStatus() == 1 || tmpConfig.getAuthStatus() == 2) {
                        canLookIdSet.add(tmpConfig.getFieldId());
                    }
                }
            }

            //当前模板中的自定义字段的值
            List<SobotTicketResultListModel> tempConfigs1 = new ArrayList<>();
            //非当前模板中的自定义字段的值
            List<SobotTicketResultListModel> tempConfigs2 = new ArrayList<>();
            //当前模板中已停用自定义字段的值
            List<SobotTicketResultListModel> tempConfigs3 = new ArrayList<>();
            //本工单所有的自定义字段的值
            List<SobotTicketResultListModel> resultList = ticketDetail.getResultList();
            if (resultList != null) {
                for (SobotTicketResultListModel tmpData : resultList) {
                    if (tmpData == null) {
                        continue;
                    }
                    if (lSet.contains(tmpData.getFieldId()) && tmpData.getIsOpenFlag() == 1) {
                        if (canLookIdSet.contains(tmpData.getFieldId())) {
                            tempConfigs1.add(tmpData);
                        }
                    } else if (tmpData.getIsOpenFlag() == 0) {
                        if (canLookIdSet.contains(tmpData.getFieldId())) {
                            tempConfigs3.add(tmpData);
                        }
                    } else {
                        if (tmpData.getFieldType() == 6 || tmpData.getFieldType() == 7 || tmpData.getFieldType() == 8|| tmpData.getFieldType() == 9) {
                            if (!TextUtils.isEmpty(tmpData.getText())) {
                                if (canLookIdSet.contains(tmpData.getFieldId())) {
                                    tempConfigs2.add(tmpData);
                                }
                            }
                        } else {
                            if (!TextUtils.isEmpty(tmpData.getValue())) {
                                if (canLookIdSet.contains(tmpData.getFieldId())) {
                                    tempConfigs2.add(tmpData);
                                }
                            }
                        }
                    }
                }
            }
            result.add(tempConfigs1);
            result.add(tempConfigs2);
            result.add(tempConfigs3);
        }
        return result;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        cb_detail_info.setText(isChecked ? SobotResourceUtils.getResString(getContext(),"sobot_detail_close_string") : SobotResourceUtils.getResString(getContext(),"sobot_detail_open_string"));
        ll_detail_info.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        if (isChecked) {
            if (ticketDetail == null) {
                return;
            }
            tvUpdateTime.setText("最近更新：" + SobotDateUtil.timeStamp2Date
                    (ticketDetail.getUpdateTime() + "", "yyyy-MM-dd HH:mm"));
            tvTicketLevel.setText("优先级：" + getEmptyStr(SobotTicketDictUtil.getPriorityName(getSobotActivity(),ticketDetail.getTicketLevel() + "")));
            tvTicketTypeName.setText("工单分类：" + getEmptyStr(ticketDetail.getTicketTypeName()));
            tvDealGroupName.setText("受理客服组：" + getEmptyStr(ticketDetail.getDealGroupName()));
            tvDealUserName.setText("受理客服：" + getEmptyStr(ticketDetail.getDealUserName()));
            String tmpStr = TextUtils.isEmpty(ticketDetail.getUserName()) ? "已删除" : ticketDetail.getUserName();
            tvOrderUserName.setText("对应客户：" + tmpStr);

        }
    }

    private String getEmptyStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return "--";
        }
        return str;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void deleteReply(String dealLogId) {
       zhiChiApi.deleteOrderReply(getContext(), detailResultModel.getTicketDetail().getTicketId(), dealLogId, new SobotResultCallBack<SobotWOBaseSeconndCode>() {
           @Override
           public void onSuccess(SobotWOBaseSeconndCode s) {

                if(getActivity()!=null && getActivity() instanceof SobotWODetailActivity){
                    ((SobotWODetailActivity)getActivity()).refresh();
                }
           }

           @Override
           public void onFailure(Exception e, String s) {

           }
       });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}