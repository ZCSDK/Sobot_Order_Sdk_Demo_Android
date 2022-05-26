package com.sobot.workorder.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.workorder.R;
import com.sobot.workorderlibrary.api.model.SobotGroupFieldItemModel;

import java.util.List;

//组合字段-下拉列表的下一级下拉列表 adapter
public class SobotGroupItemFieldAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SobotGroupFieldItemModel> mList;
    private OnItemClickListener itemClickListener;

    public SobotGroupItemFieldAdapter(Context context, List<SobotGroupFieldItemModel> list) {
        super();
        this.mContext = context;
        this.mList = list;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_adapter_category_small_items, parent, false);
        RecyclerView.ViewHolder vh = new SobotGroupItemFieldViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        final SobotGroupFieldItemModel modelResult = mList.get(position);
        SobotGroupItemFieldViewHolder myViewHolder = (SobotGroupItemFieldViewHolder) vh;
        myViewHolder.categorySmallTitle.setText(mList.get(position).getText());
        myViewHolder.categorySmallCheckBox.setVisibility(View.GONE);
        myViewHolder.categorySmallIshave.setVisibility(View.GONE);
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

    class SobotGroupItemFieldViewHolder extends RecyclerView.ViewHolder {

        private TextView categorySmallTitle;
        private ImageView categorySmallIshave;
        private ImageView categorySmallCheckBox;
        private View categorySmallline;

        SobotGroupItemFieldViewHolder(View view) {
            super(view);
            categorySmallTitle = (TextView) view.findViewById(R.id.work_order_category_small_title);
            categorySmallIshave = (ImageView) view.findViewById(R.id.work_order_category_small_ishave);
            categorySmallCheckBox = (ImageView) view.findViewById(R.id.work_order_category_small_checkbox);
            categorySmallline = view.findViewById(R.id.order_category_small_line);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SobotGroupFieldItemModel info, int index);
    }
}