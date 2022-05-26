package com.sobot.workorder.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.pictureframe.SobotBitmapUtil;
import com.sobot.workorder.adapter.base.SobotOnItemClickListener;
import com.sobot.workorder.permission.SobotPermissionManager;
import com.sobot.workorder.utils.SobotDateUtil;
import com.sobot.workorder.utils.SobotWorkOrderUtils;
import com.sobot.workorderlibrary.api.model.SobotFileItemModel;
import com.sobot.workorderlibrary.api.model.SobotOrderReplyItemModel;

import java.util.List;


public class SobotWorkOrderDetailReplyAdapter extends BaseAdapter {

    boolean isCanEdit = true;
    List<SobotOrderReplyItemModel> list;
    Context context;
    private SobotOnItemClickListener deleteListener;

    public SobotWorkOrderDetailReplyAdapter(Context context, List<SobotOrderReplyItemModel> list) {
        this.isCanEdit = isCanEdit;
        this.list = list;
        this.context = context;
    }

    public void setDeleteListener(SobotOnItemClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public SobotWorkOrderDetailReplyAdapter(Context context, List<SobotOrderReplyItemModel> list, boolean isCanEdit) {
        this.isCanEdit = isCanEdit;
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        if (list != null) {
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, SobotResourceUtils.getResLayoutId(context,"sobot_item_detail_reply"), null);
            holder = new ViewHolder(context,convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SobotOrderReplyItemModel bean = list.get(position);
        SobotBitmapUtil.display(context, bean.getFaceImg(), holder.ivHead, SobotResourceUtils.getDrawableId(context,"sobot_icon_custom_select_pic_error"), SobotResourceUtils.getDrawableId(context,"sobot_icon_custom_select_pic_error"));
        holder.tvUserName.setText(bean.getDealUserName());
        holder.tvCreateTime.setText(SobotDateUtil.timeStamp2Date(bean.getReplyTime() + "",
                "yyyy-MM-dd HH:mm"));
        if (bean != null && !TextUtils.isEmpty(bean.getReplyContent())) {
            holder.tvContent.setText(Html.fromHtml(bean.getReplyContent().replace("<p>", "").replace
                    ("</p>", "").replaceAll("<img[^>]*>", "[图片]")));
        } else {
            holder.tvContent.setText("");
        }
        List<SobotFileItemModel> fileList = bean.getFileList();
        holder.llFileList.removeAllViews();
        if (fileList.size() > 0) {
            // 添加列表
            SobotWorkOrderUtils.updateFileList(context, fileList, holder.llFileList);
        }

        if (bean.getReplyType() == 0) {
            //所有人可见
            holder.tvDisplayLevel.setVisibility(View.GONE);
        } else {
            //仅客服可见
            holder.tvDisplayLevel.setVisibility(View.VISIBLE);
        }

        if (SobotPermissionManager.isHasPermission(context,SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER_REPLY) && isCanEdit) {
            //超管：3333  管理员：2222
            //超管和管理员时  删除按钮显示
            holder.tvDelete.setVisibility(View.VISIBLE);
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deleteListener!=null)
                        deleteListener.onItemClick(bean.getDealLogId(),position);
                }
            });
        } else {
            holder.tvDelete.setVisibility(View.GONE);
        }
        return convertView;
    }


    static class ViewHolder {
        ImageView ivHead;
        TextView tvUserName;
        TextView tvCreateTime;
        TextView tvDisplayLevel;
        TextView tvContent;
        LinearLayout llFileList;
        TextView tvDelete;

        ViewHolder(Context context,View view) {
            ivHead = view.findViewById(SobotResourceUtils.getResId(context,"iv_head"));
            tvUserName = view.findViewById(SobotResourceUtils.getResId(context,"tv_user_name"));
            tvCreateTime = view.findViewById(SobotResourceUtils.getResId(context,"tv_create_time"));
            tvDisplayLevel = view.findViewById(SobotResourceUtils.getResId(context,"tv_display_level"));
            tvContent = view.findViewById(SobotResourceUtils.getResId(context,"tv_content"));
            llFileList = view.findViewById(SobotResourceUtils.getResId(context,"ll_file_container"));
            tvDelete = view.findViewById(SobotResourceUtils.getResId(context,"tv_delete"));
        }
    }

}