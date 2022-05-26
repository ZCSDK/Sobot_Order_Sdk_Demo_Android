package com.sobot.workorder.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.workorder.R;
import com.sobot.workorder.adapter.base.SobotMyBaseAdapter;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorderlibrary.api.model.SobotCommonItemModel;

import java.util.List;

public class SobotWOListAdapter extends SobotMyBaseAdapter<SobotCommonItemModel> {

    private int displayType;

    public SobotWOListAdapter(Context context, List<SobotCommonItemModel> list, int displayType) {
        super(context, list);
        this.displayType = displayType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.sobot_adapter_item_work_order_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SobotCommonItemModel bean = mList.get(position);
        switch (displayType) {
            case SobotConstantUtils.sobot_wo_list_display_type_service_group:
                if ("-1".equals(bean.getItemValue())) {
                    holder.iv_work_order_list_left_img.setBackgroundResource(R.drawable.sobot_icon_not_selected_service_group);
                } else {
                    holder.iv_work_order_list_left_img.setBackgroundResource(R.drawable.sobot_icon_selected_service_group);
                }
                break;
            case SobotConstantUtils.sobot_wo_list_display_type_service:
                if ("-1".equals(bean.getItemValue())) {
                    holder.iv_work_order_list_left_img.setBackgroundResource(R.drawable.sobot_icon_not_selected_service);
                } else {
                    holder.iv_work_order_list_left_img.setBackgroundResource(R.drawable.sobot_icon_selected_service);
                }
                break;
            case SobotConstantUtils.sobot_wo_list_display_type_service_copy:
                if ("-1".equals(bean.getItemValue())) {
                    holder.iv_work_order_list_left_img.setBackgroundResource(R.drawable.sobot_icon_not_selected_service);
                } else {
                    holder.iv_work_order_list_left_img.setBackgroundResource(R.drawable.sobot_icon_selected_service);
                }
                break;
            default:
                break;
        }

        holder.tv_setting_item_left_text.setText(bean.getItemKey());
        if (bean.isChecked()) {
            holder.tv_setting_item_left_text.setTextColor(context.getResources().getColor(R.color.sobot_wo_theme_color));
            holder.iv_work_order_list_right_img.setVisibility(View.VISIBLE);
        } else {
            holder.tv_setting_item_left_text.setTextColor(context.getResources().getColor(R.color.sobot_wo_wenzi_gray1));
            holder.iv_work_order_list_right_img.setVisibility(View.INVISIBLE);
        }
        if (position == mList.size() - 1) {
            holder.v_dividerline.setVisibility(View.GONE);
            holder.v_dividerline_full.setVisibility(View.VISIBLE);
        } else {
            holder.v_dividerline.setVisibility(View.VISIBLE);
            holder.v_dividerline_full.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        public ImageView iv_work_order_list_left_img;// 左边图像
        public TextView tv_setting_item_left_text; // 显示内容
        public ImageView iv_work_order_list_right_img;// 右边图像
        public View v_dividerline;// 分割线
        public View v_dividerline_full;// 分割线

        public ViewHolder(View view) {
            iv_work_order_list_left_img = (ImageView) view.findViewById(R.id.iv_work_order_list_left_img);
            tv_setting_item_left_text = (TextView) view.findViewById(R.id.tv_setting_item_left_text);
            iv_work_order_list_right_img = (ImageView) view.findViewById(R.id.iv_work_order_list_right_img);
            v_dividerline = view.findViewById(R.id.v_dividerline);
            v_dividerline_full = view.findViewById(R.id.v_dividerline_full);
        }
    }

}