package com.sobot.workorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sobot.pictureframe.SobotBitmapUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.utils.SobotDateUtil;
import com.sobot.workorder.utils.SobotWorkOrderUtils;
import com.sobot.workorderlibrary.api.model.SobotTicketEventModel;
import com.sobot.workorderlibrary.api.model.SobotTicketResultListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作历史的adapter
 */
public class SobotTicketEventAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SobotTicketEventModel> list = new ArrayList<>();

    public SobotTicketEventAdapter(Context context, List<SobotTicketEventModel> listModel) {
        this.mContext = context;
        list.clear();
        list.addAll(listModel);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_item_detail_history, viewGroup, false);
        return new ViewHolder(mContext, v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        SobotTicketEventModel data = list.get(position);
        ViewHolder viewHolder = (ViewHolder) vh;
        viewHolder.v_divier.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        switch (data.getLogFrom()) {
            case 1:
                //页面操作
                SobotBitmapUtil.display(mContext, data.getFaceImg(), viewHolder.iv_head, R.drawable.sobot_icon_text_image_err, R.drawable.sobot_icon_text_image_err);
                viewHolder.tv_title.setText(data.getUpdateServiceName());
                if ("工单创建".equals(data.getUpdateTitle())) {
                    viewHolder.tv_action.setText(mContext.getResources().getString(R.string.sobot_created_ticket) + " :");
                } else if ("工单更新".equals(data.getUpdateTitle())) {
                    viewHolder.tv_action.setText(mContext.getResources().getString(R.string.sobot_work_order_processed) + " :");
                } else if ("工单激活".equals(data.getUpdateTitle())) {
                    viewHolder.tv_action.setText(mContext.getResources().getString(R.string.sobot_ticket_activated) + " :");
                } else {
                    viewHolder.tv_action.setText(data.getUpdateTitle() + ":");
                }
                break;
            case 2:
                //流转触发器
                viewHolder.tv_title.setText(data.getTrigName());
                SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_flip_flop_avatar, viewHolder.iv_head);
                viewHolder.tv_action.setText(mContext.getResources().getString(R.string.sobot_chufa_caozuo) + " :");
                break;
            case 3:
                viewHolder.tv_title.setText(data.getTrigName());
                SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_timer_trigger_avatar, viewHolder.iv_head);
                viewHolder.tv_action.setText(mContext.getResources().getString(R.string.sobot_chufa_caozuo) + " :");
                break;
        }
        viewHolder.tv_create_time.setText(SobotDateUtil.timeStamp2Date(data.getUpdateTime() + "", "yyyy-MM-dd HH:mm"));

        List<SobotTicketEventModel.CombinFormDataList> combinFormDataList = data.getCombinFormDataList();
        if (combinFormDataList != null && combinFormDataList.size() > 0) {
            for (int i = 0; i < combinFormDataList.size(); i++) {
                SobotTicketEventModel.CombinFormDataList combinFormDataList1 = combinFormDataList.get(i);
                if (combinFormDataList1 != null) {
                    data.getResultList().add(new SobotTicketResultListModel(combinFormDataList1.isChecked(), combinFormDataList1.getFieldId(),
                            10, combinFormDataList1.getTitle(), combinFormDataList1.getValue(), combinFormDataList1.getText(),
                            combinFormDataList1.getIsOpenFlag(), combinFormDataList1.getSummary()));
                }
            }
        }
        SobotWorkOrderUtils.updateResultListView(mContext, data.getResultList(), viewHolder.ll_container);

    }

    public void addData(List<SobotTicketEventModel> addList) {
        list.clear();
        list.addAll(addList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_head;
        TextView tv_title;
        TextView tv_action;
        TextView tv_create_time;
        LinearLayout ll_container;
        View v_divier;

        public ViewHolder(Context context, View view) {
            super(view);
            iv_head = view.findViewById(R.id.iv_head);
            tv_title = view.findViewById(R.id.tv_title);
            tv_action = view.findViewById(R.id.tv_action);
            tv_create_time = view.findViewById(R.id.tv_create_time);
            ll_container = view.findViewById(R.id.ll_container);
            v_divier = view.findViewById(R.id.v_divier);
            v_divier = view.findViewById(R.id.v_divier);
        }
    }
}