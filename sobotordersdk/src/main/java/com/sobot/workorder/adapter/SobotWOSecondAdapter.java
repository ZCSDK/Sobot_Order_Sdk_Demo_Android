package com.sobot.workorder.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sobot.workorder.R;
import com.sobot.workorder.utils.SobotDateUtil;
import com.sobot.workorder.utils.SobotTicketDictUtil;
import com.sobot.workorder.weight.dialog.SobotCommonUtils;
import com.sobot.workorderlibrary.api.model.SobotWODictModelResult;
import com.sobot.workorderlibrary.api.model.SobotWOSecondItemModel;
import com.sobot.workorderlibrary.utils.SobotWOSPUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author: Sobot
 * 2022/3/22
 * 工单分类列表中的adapter
 */
public class SobotWOSecondAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SobotWOSecondItemModel> list;
    private OnItemClickListener itemClickListener;

    private static final int CENTER_TYPE_NOMAL = 1;

    public SobotWOSecondAdapter(Context context, List<SobotWOSecondItemModel> list) {
        super();
        mContext = context;
        this.list = list;
    }

    public void setList(List<SobotWOSecondItemModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_workorder_second_item, parent, false);
        RecyclerView.ViewHolder vh = new SecondViewHolder(v);
        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        return CENTER_TYPE_NOMAL;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        final SobotWOSecondItemModel modelResult = list.get(position);
        final SecondViewHolder viewHolder = (SecondViewHolder) vh;
        if (modelResult != null) {
            viewHolder.work_order_id.setText("#" + modelResult.getTicketCode());
            viewHolder.work_order_status.setText(getStatusName(mContext, modelResult.getTicketStatus() + ""));
            viewHolder.work_order_status.setTextColor(SobotTicketDictUtil.getStatusColor(modelResult.getTicketStatus()));


            viewHolder.work_order_title.setText(modelResult.getTicketTitle());
            long time = modelResult.getCreateTime();
            if (time < 10000000000l) {
                time = time * 1000;
            }
            viewHolder.work_order_createtime.setText(SobotDateUtil.DATE_FORMAT.format(new Date(time)));

            if (TextUtils.isEmpty(modelResult.getDealUserName()) && 0 == modelResult.getIsShowReceipt() && (modelResult.getTicketStatus() == 0 || modelResult.getTicketStatus() == 1 || modelResult.getTicketStatus() == 2)) {
                viewHolder.work_order_list_take_work_order.setVisibility(View.VISIBLE);

                viewHolder.work_order_list_take_work_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //接单
                        if (itemClickListener != null) {
                            itemClickListener.takeWorkOrder(modelResult, vh.getAdapterPosition());
                        }
                    }
                });
            } else {
                viewHolder.work_order_list_take_work_order.setVisibility(View.GONE);
            }

            //中文或者接单不显示时 优先级不动，反之 换行显示
            if ("zh".equals(SobotWOSPUtil.getInstance(mContext).get("SobotLanguageStr", "").toLowerCase()) || viewHolder.work_order_list_take_work_order.getVisibility() != View.VISIBLE) {
                viewHolder.work_order_priority.setText(getPriorityName(mContext, modelResult.getTicketLevel() + ""));
                viewHolder.work_order_priority_hint.setTextColor(SobotTicketDictUtil.getPriorityColor(modelResult.getTicketLevel()));
                viewHolder.work_order_priority.setTextColor(SobotTicketDictUtil.getPriorityColor(modelResult.getTicketLevel()));

                viewHolder.sobot_work_order_priority_second_ll.setVisibility(View.GONE);
                viewHolder.work_order_priority_hint.setVisibility(View.VISIBLE);
                viewHolder.work_order_priority.setVisibility(View.VISIBLE);
            } else {
                viewHolder.sobot_work_order_priority_second_ll.setVisibility(View.VISIBLE);
                viewHolder.work_order_priority_hint.setVisibility(View.GONE);
                viewHolder.work_order_priority.setVisibility(View.GONE);

                viewHolder.work_order_priority_second.setText(getPriorityName(mContext, modelResult.getTicketLevel() + ""));
                viewHolder.work_order_priority_hint_second.setTextColor(SobotTicketDictUtil.getPriorityColor(modelResult.getTicketLevel()));
                viewHolder.work_order_priority_second.setTextColor(SobotTicketDictUtil.getPriorityColor(modelResult.getTicketLevel()));
            }

            if (!TextUtils.isEmpty(modelResult.getDealGroupName()) && !TextUtils.isEmpty(modelResult.getDealUserName())) {
                viewHolder.work_order_human.setText(modelResult.getDealGroupName() + "-" + modelResult.getDealUserName());
            } else if (!TextUtils.isEmpty(modelResult.getDealGroupName())) {
                viewHolder.work_order_human.setText(modelResult.getDealGroupName());
            } else if (!TextUtils.isEmpty(modelResult.getDealUserName())) {
                viewHolder.work_order_human.setText(modelResult.getDealUserName());
            } else {
                viewHolder.work_order_human.setText("-");
            }

            viewHolder.work_order_custom.setText(modelResult.getReplyUserName());
            viewHolder.work_order_operationtime.setText(SobotDateUtil.getStandardDate(mContext, modelResult.getReplyTime(), true));

            if (TextUtils.isEmpty(modelResult.getReplyContent())) {
                viewHolder.reply_custom_rl.setVisibility(View.GONE);
            } else {
                viewHolder.reply_custom_rl.setVisibility(View.VISIBLE);

                viewHolder.work_order_operationcontent.setText(Html.fromHtml((modelResult.getReplyContent().replace("\n", "<br/>").replaceAll("<img[^>]*>", "[图片]"))));
            }

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(modelResult, vh.getAdapterPosition());
                    }
                }
            });
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    SobotCommonUtils.showPopWindows(mContext, viewHolder.work_order_id, modelResult.getTicketCode(), 30, 69, new SobotCommonUtils.CopyListener() {
                        @Override
                        public void onCopy() {
                            if (viewHolder.work_order_id != null) {
                                viewHolder.work_order_id.setSelected(false);
                            }
                        }
                    }, new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            if (viewHolder.work_order_id != null) {
                                viewHolder.work_order_id.setSelected(false);
                            }
                        }
                    });
                    viewHolder.work_order_id.setSelected(true);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    class SecondViewHolder extends RecyclerView.ViewHolder {
        TextView work_order_id;/*编号*/
        TextView work_order_status;/*状态*/
        TextView work_order_priority_hint;/*优先级提示*/
        TextView work_order_priority;/*优先级*/
        TextView work_order_title;/*工单名*/
        TextView work_order_createtime;/*工单创建时间*/
        TextView work_order_human;/*受理人*/

        TextView work_order_custom;/*最新操作的客服*/
        TextView work_order_operationtime;/*操作时间*/
        TextView work_order_operationcontent;/*操作内容*/
        TextView work_order_list_take_work_order;/*接单按钮*/
        RelativeLayout reply_custom_rl;
        LinearLayout ll_real_content;

        LinearLayout sobot_work_order_priority_second_ll;
        TextView work_order_priority_hint_second;/*优先级提示*/
        TextView work_order_priority_second;/*优先级*/

        public SecondViewHolder(View itemView) {
            super(itemView);
            work_order_title = itemView.findViewById(R.id.sobot_work_order_title);
            work_order_id = itemView.findViewById(R.id.sobot_work_order_id);
            work_order_status = itemView.findViewById(R.id.sobot_work_order_status);
            work_order_priority_hint = itemView.findViewById(R.id.sobot_work_order_priority_hint);
            work_order_priority = itemView.findViewById(R.id.sobot_work_order_priority);
            work_order_createtime = itemView.findViewById(R.id.sobot_work_order_createtime);
            work_order_human = itemView.findViewById(R.id.sobot_work_order_human);

            work_order_custom = itemView.findViewById(R.id.sobot_work_order_custom);
            work_order_operationtime = itemView.findViewById(R.id.sobot_work_order_operationtime);
            work_order_operationcontent = itemView.findViewById(R.id.sobot_work_order_operationcontent);
            work_order_list_take_work_order = itemView.findViewById(R.id.sobot_work_order_list_take_work_order);
            reply_custom_rl = itemView.findViewById(R.id.sobot_reply_custom_rl);
            ll_real_content = itemView.findViewById(R.id.sobot_ll_real_content);

            work_order_priority_hint_second = itemView.findViewById(R.id.sobot_work_order_priority_hint_second);
            work_order_priority_second = itemView.findViewById(R.id.sobot_work_order_priority_second);
            sobot_work_order_priority_second_ll = itemView.findViewById(R.id.sobot_work_order_priority_second_ll);
        }
    }
    public List<SobotWODictModelResult> getDictStatus(Context context) {
        List<SobotWODictModelResult> dictStatus = new ArrayList<>();
        dictStatus.add(new SobotWODictModelResult(0, context.getResources().getString(R.string.sobot_wo_item_state_not_start_string)));
        dictStatus.add(new SobotWODictModelResult(1, context.getResources().getString(R.string.sobot_wo_item_state_doing_string)));
        dictStatus.add(new SobotWODictModelResult(2, context.getResources().getString(R.string.sobot_wo_item_state_waiting_string)));
        dictStatus.add(new SobotWODictModelResult(3, context.getResources().getString(R.string.sobot_wo_item_state_resolved_string)));
        dictStatus.add(new SobotWODictModelResult(98, context.getResources().getString(R.string.sobot_already_delete)));
        dictStatus.add(new SobotWODictModelResult(99, context.getResources().getString(R.string.sobot_wo_item_state_closed_string)));
        return dictStatus;
    }

    public List<SobotWODictModelResult> getDictPriority(Context context) {
        List<SobotWODictModelResult> dictPriority = new ArrayList<>();
        dictPriority.add(new SobotWODictModelResult(0, context.getResources().getString(R.string.sobot_low_string)));
        dictPriority.add(new SobotWODictModelResult(1, context.getResources().getString(R.string.sobot_middle_string)));
        dictPriority.add(new SobotWODictModelResult(2, context.getResources().getString(R.string.sobot_height_string)));
        dictPriority.add(new SobotWODictModelResult(3, context.getResources().getString(R.string.sobot_urgent_string)));
        return dictPriority;
    }

    public String getPriorityName(Context context, String value) {
        List<SobotWODictModelResult> dictPriority = getDictPriority(context);
        String result = "";
        for (int i = 0; i < dictPriority.size(); i++) {
            if (dictPriority.get(i).getDictValue().equals(value)) {
                return dictPriority.get(i).getDictName();
            }
        }
        return result;
    }

    public String getStatusName(Context context, String value) {
        List<SobotWODictModelResult> dictStatus = getDictStatus(context);
        String result = "";
        for (int i = 0; i < dictStatus.size(); i++) {
            if (dictStatus.get(i).getDictValue().equals(value)) {
                return dictStatus.get(i).getDictName();
            }
        }
        return result;
    }
    public interface OnItemClickListener {
        void onItemClick(SobotWOSecondItemModel info, int index);

        void takeWorkOrder(SobotWOSecondItemModel info, int index);
    }
}
