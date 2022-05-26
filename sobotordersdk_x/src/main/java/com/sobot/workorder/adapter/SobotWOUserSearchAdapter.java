package com.sobot.workorder.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.pictureframe.SobotBitmapUtil;
import com.sobot.workorder.R;
import com.sobot.workorderlibrary.api.model.SobotWOUser;

import java.util.List;

/**
 * 工单对应客户 adapter
 */

public class SobotWOUserSearchAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SobotWOUser> mList;
    private SobotWOUserSearchAdapter.OnItemClickListener itemClickListener;


    public SobotWOUserSearchAdapter(Context context, List<SobotWOUser> list) {
        super();
        mContext = context;
        this.mList = list;
    }

    public void setItemClickListener(SobotWOUserSearchAdapter.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_adapter_user_search_item, parent, false);
        RecyclerView.ViewHolder vh = new SobotWOUserSearchAdapter.SobotWOUserViewHolder(v);
        return vh;
    }


    private void setUserHead(SobotWOUserViewHolder viewHolder, int pos, String faceUrl) {
        if (null == mList.get(pos)) {
            return;
        }

        String source = mList.get(pos).getSource();
        if ("null".equals(faceUrl)) {
            faceUrl = "";
        }
        switch (source) {
            case "0":
                if (!TextUtils.isEmpty(faceUrl)) {
                    SobotBitmapUtil.display(mContext, mList.get(pos).getImg(), viewHolder.userHead);
                } else {
                    SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_avatar_computer_online, viewHolder.userHead);
                }
                break;
            case "1":
                if (!TextUtils.isEmpty(faceUrl)) {
                    SobotBitmapUtil.display(mContext, mList.get(pos).getImg(), viewHolder.userHead);
                } else {
                    SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_avatar_wechat_online, viewHolder.userHead);
                }
                break;
            case "2":
                if (!TextUtils.isEmpty(faceUrl)) {
                    SobotBitmapUtil.display(mContext, mList.get(pos).getImg(), viewHolder.userHead);
                } else {
                    SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_avatar_app_online, viewHolder.userHead);
                }
                break;
            case "3":
                if (!TextUtils.isEmpty(faceUrl)) {
                    SobotBitmapUtil.display(mContext, mList.get(pos).getImg(), viewHolder.userHead);
                } else {
                    SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_avatar_weibo_online, viewHolder.userHead);
                }
                break;
            case "4":
                if (!TextUtils.isEmpty(faceUrl)) {
                    SobotBitmapUtil.display(mContext, mList.get(pos).getImg(), viewHolder.userHead);
                } else {
                    SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_avatar_phone_online, viewHolder.userHead);
                }
                break;
            default:
                if (!TextUtils.isEmpty(faceUrl)) {
                    SobotBitmapUtil.display(mContext, mList.get(pos).getImg(), viewHolder.userHead);
                } else {
                    SobotBitmapUtil.display(mContext, R.drawable.sobot_icon_avatar_computer_online, viewHolder.userHead);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mList == null || mList.size() == 0) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        final SobotWOUser workOrderUser = mList.get(position);
        final SobotWOUserViewHolder viewHolder = (SobotWOUserViewHolder) vh;
        if (workOrderUser != null) {
            if (mList != null && mList.size() != 0 && (mList.size() == 1 || position == mList.size() - 1)) {
                viewHolder.line.setVisibility(View.GONE);
            } else {
                viewHolder.line.setVisibility(View.VISIBLE);
            }

            setUserHead(viewHolder, position, workOrderUser.getImg());
            if (!TextUtils.isEmpty(workOrderUser.getEmail())) {
                viewHolder.userEmal.setText(SobotResourceUtils.getResString(mContext, "sobot_email_string") + ":    " + workOrderUser.getEmail());
            } else {
                viewHolder.userEmal.setText(SobotResourceUtils.getResString(mContext, "sobot_email_string") + ":--");
            }

            if (!TextUtils.isEmpty(workOrderUser.getTmpNick())) {
                viewHolder.userName.setText(Html.fromHtml(workOrderUser.getTmpNick()));
            } else {
                if (!TextUtils.isEmpty(workOrderUser.getNick())) {
                    viewHolder.userName.setText(Html.fromHtml(workOrderUser.getNick()));
                }
            }

            if (!TextUtils.isEmpty(workOrderUser.getTel())) {
                viewHolder.userTel.setText(SobotResourceUtils.getResString(mContext, "sobot_phone_string") + ":    " + workOrderUser.getTel());
            } else {
                viewHolder.userTel.setText(SobotResourceUtils.getResString(mContext, "sobot_phone_string") + ":--");
            }

            viewHolder.user_check_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (act == null) {
//                        return;
//                    }
//
//                    Intent intent = new Intent(act, WorkOrderUserInfoActivity.class);
//                    intent.putExtra("work_order_user", workOrderUser);
//                    act.startActivity(intent);
//                    act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });


            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null && vh.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(workOrderUser, vh.getAdapterPosition());
                    }
                }
            });
        }
    }

    class SobotWOUserViewHolder extends RecyclerView.ViewHolder {
        private ImageView userHead;
        private TextView userName;
        private TextView userEmal;
        private TextView userTel;
        private View line;
        private RelativeLayout user_check_rl;

        public SobotWOUserViewHolder(View itemView) {
            super(itemView);
            userHead = (ImageView) itemView.findViewById(R.id.work_order_search_user_item_userhead);
            userName = (TextView) itemView.findViewById(R.id.work_order_search_user_item_username);
            userEmal = (TextView) itemView.findViewById(R.id.work_order_search_user_item_useremal);
            userTel = (TextView) itemView.findViewById(R.id.work_order_search_user_item_usertel);
            line = itemView.findViewById(R.id.work_order_search_user_item_line);
            user_check_rl = (RelativeLayout) itemView.findViewById(R.id.work_order_search_user_rl);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Object info, int index);
    }
}