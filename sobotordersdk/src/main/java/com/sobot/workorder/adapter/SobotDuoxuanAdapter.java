package com.sobot.workorder.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sobot.workorder.R;
import com.sobot.workorder.utils.CustomFieldsUtils;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;

import java.util.ArrayList;
import java.util.List;

/**
 * 工单自定义字段 下拉列表 适配器
 */

public class SobotDuoxuanAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int fieldType;
    private String searchText = "";
    private List<SobotCusFieldDataInfoList> mList;
    private List<SobotCusFieldDataInfoList> selectList;
    private OnItemClickListener itemClickListener;

    public SobotDuoxuanAdapter(Context context, List<SobotCusFieldDataInfoList> list) {
        super();
        this.mContext = context;
        this.fieldType = fieldType;
        this.mList = list;
        selectList = new ArrayList<>();
    }


    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_item_duoxuan, parent, false);
        RecyclerView.ViewHolder vh = new CategorySmallViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        final SobotCusFieldDataInfoList modelResult = mList.get(position);
        final CategorySmallViewHolder myViewHolder = (CategorySmallViewHolder) vh;
        myViewHolder.categorySmallTitle.setText(Html.fromHtml(modelResult.getDataName().replaceFirst(
                TextUtils.isEmpty(searchText) ? "" : searchText, getColorStr(searchText))));
        if (CustomFieldsUtils.WORK_ORDER_CUSTOMER_FIELD_CHECKBOX_TYPE == fieldType) {
            myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            myViewHolder.categorySmallCheckBox.setVisibility(View.VISIBLE);
            if (modelResult.isChecked()) {
                myViewHolder.categorySmallCheckBox.setSelected(true);
            } else {
                myViewHolder.categorySmallCheckBox.setSelected(false);
            }
        } else {
            myViewHolder.categorySmallCheckBox.setVisibility(View.GONE);
            if (modelResult.isChecked()) {
                myViewHolder.categorySmallIshave.setVisibility(View.VISIBLE);
                myViewHolder.categorySmallIshave.setBackgroundResource(R.drawable.sobot_icon_selected_mark);
            } else {
                myViewHolder.categorySmallIshave.setVisibility(View.GONE);
            }
        }

        if (mList.size() >= 2) {
            if (position == mList.size() - 1) {
                myViewHolder.categorySmallline.setVisibility(View.GONE);
            } else {
                myViewHolder.categorySmallline.setVisibility(View.VISIBLE);
            }
        } else {
            myViewHolder.categorySmallline.setVisibility(View.GONE);
        }

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null && vh.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(modelResult, vh.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    class CategorySmallViewHolder extends RecyclerView.ViewHolder {

        private TextView categorySmallTitle;
        private ImageView categorySmallIshave;
        private ImageView categorySmallCheckBox;
        private View categorySmallline;

        CategorySmallViewHolder(View view) {
            super(view);
            categorySmallTitle = (TextView) view.findViewById(R.id.work_order_category_small_title);
            categorySmallIshave = (ImageView) view.findViewById(R.id.work_order_category_small_ishave);
            categorySmallCheckBox = (ImageView) view.findViewById(R.id.work_order_category_small_checkbox);
            categorySmallline = view.findViewById(R.id.order_category_small_line);
        }
    }

    private String getColorStr(String str) {
        if (!TextUtils.isEmpty(str)) {
            return "<font color='#09aeb0' >" + str + "</font>";
        }
        return "";
    }

    public interface OnItemClickListener {
        void onItemClick(SobotCusFieldDataInfoList info, int index);
    }
}