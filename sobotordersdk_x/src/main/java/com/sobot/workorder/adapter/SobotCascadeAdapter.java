package com.sobot.workorder.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.workorder.R;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoListModel;

import java.util.List;

/**
 * 工单自定义字段 级联 适配器
 */

public class SobotCascadeAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private String searchText = "";
    private List<SobotCusFieldDataInfoListModel> mList;
    private OnItemClickListener itemClickListener;

    public SobotCascadeAdapter(Context context, List<SobotCusFieldDataInfoListModel> list) {
        super();
        this.mContext = context;
        this.mList = list;
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_adapter_cascade_items, parent, false);
        RecyclerView.ViewHolder vh = new CategorySmallViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        final SobotCusFieldDataInfoListModel modelResult = mList.get(position);
        final CategorySmallViewHolder myViewHolder = (CategorySmallViewHolder) vh;
        myViewHolder.categorySmallTitle.setText(Html.fromHtml(modelResult.getDataName().replaceFirst(
                TextUtils.isEmpty(searchText) ? "" : searchText, getColorStr(searchText))));
        if (!modelResult.isHasNewNode()) {
            myViewHolder.categorySmallIshave.setVisibility(View.GONE);
        } else {
            myViewHolder.categorySmallIshave.setVisibility(View.VISIBLE);
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
        private View categorySmallline;

        CategorySmallViewHolder(View view) {
            super(view);
            categorySmallTitle = (TextView) view.findViewById(R.id.work_order_category_small_title);
            categorySmallIshave = (ImageView) view.findViewById(R.id.work_order_category_small_ishave);
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
        void onItemClick(SobotCusFieldDataInfoListModel info, int index);
    }
}