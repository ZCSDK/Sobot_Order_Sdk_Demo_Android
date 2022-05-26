package com.sobot.workorder.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.base.SobotOnItemClickListener;
import com.sobot.workorderlibrary.api.model.SobotWOCenterItemModel;

import java.util.List;


/**
 * @author: Sobot
 * 工单中心列表的adapter
 * 2022/3/22
 */
public class SobotWOCenterAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Object> list;
    private SobotOnItemClickListener itemClickListener;

    private static final int CENTER_TYPE_TITLE = 0;
    private static final int CENTER_TYPE_NOMAL = 1;

    public SobotWOCenterAdapter(Context context, List<Object> list) {
        super();
        mContext = context;
        this.list = list;
    }

    public void setList(List<Object> list) {
        this.list=list;
        notifyDataSetChanged();
    }

    public void setItemClickListener(SobotOnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == CENTER_TYPE_TITLE) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_workorder_center_title_item, parent, false);
            vh = new TitleViewHolder(v);
        } else if (viewType == CENTER_TYPE_NOMAL) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_workorder_center_normal_item, parent, false);
            vh = new ViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        final Object object = list.get(position);
        if(object == null ) return;
        if (vh.getItemViewType() == CENTER_TYPE_TITLE) {
            ((TitleViewHolder) vh).work_order_bigtitle.setText(object.toString());
        } else {
            ViewHolder viewHolder = (ViewHolder) vh;
            SobotWOCenterItemModel been = (SobotWOCenterItemModel) object;
            viewHolder.work_order_title.setText(been.getTaskName());
            viewHolder.unread_work_order_msg_num_item.setVisibility(View.GONE);
            if(been.isFilter()){
                viewHolder.work_order_num.setText(been.getTicketNum() + "");
                viewHolder.work_order_num.setVisibility(View.VISIBLE);
                if (0 < been.getTicketNum() && been.getTicketNum() < 1000) {
                } else if (1000 <= been.getTicketNum() && been.getTicketNum() <= 9999) {
                } else {
                    viewHolder.work_order_num.setText("9999+");
                }
            }else {
                if (been.getTicketNum() != 0 && ("311".equals(been.getTaskId()) || "319".equals(been.getTaskId()) || "302".equals(been.getTaskId()) || "301".equals(been.getTaskId()) || "304".equals(been.getTaskId())
                        || "305".equals(been.getTaskId()) || "201".equals(been.getTaskId()) || "202".equals(been.getTaskId()))) {
                    viewHolder.work_order_num.setText(been.getTicketNum() + "");
                    viewHolder.work_order_num.setVisibility(View.VISIBLE);
                    if (0 < been.getTicketNum() && been.getTicketNum() < 1000) {
                    } else if (1000 <= been.getTicketNum() && been.getTicketNum() <= 9999) {
                    } else {
                        viewHolder.work_order_num.setText("9999+");
                    }
                } else {
                    viewHolder.work_order_num.setVisibility(View.GONE);
                }
            }
            if (position == list.size() - 1) {
                viewHolder.work_order_line.setVisibility(View.GONE);
            } else if ((position + 1) < list.size() - 1 && list.get(position + 1) instanceof String) {
                viewHolder.work_order_line.setVisibility(View.GONE);
            }
        }
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(object, vh.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (list == null || list.size() == 0) {
            return CENTER_TYPE_NOMAL;
        }
        if (list.get(position) instanceof String) {
            return CENTER_TYPE_TITLE;
        } else if (list.get(position) instanceof SobotWOCenterItemModel) {
            return CENTER_TYPE_NOMAL;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {

        private TextView work_order_bigtitle;

        public TitleViewHolder(View itemView) {
            super(itemView);
            work_order_bigtitle = itemView.findViewById(SobotResourceUtils.getResId(mContext, "sobot_work_order_bigtitle"));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView work_order_title;
        private TextView unread_work_order_msg_num_item;
        private TextView work_order_num;
        private View work_order_line;

        public ViewHolder(View itemView) {
            super(itemView);
            work_order_title = itemView.findViewById(SobotResourceUtils.getResId(mContext, "sobot_work_order_title"));
            unread_work_order_msg_num_item = itemView.findViewById(SobotResourceUtils.getResId(mContext, "sobot_unread_work_order_msg_num_item"));
            work_order_num = itemView.findViewById(SobotResourceUtils.getResId(mContext, "sobot_work_order_num"));
            work_order_line = itemView.findViewById(SobotResourceUtils.getResId(mContext, "sobot_v_line"));
        }
    }
}
