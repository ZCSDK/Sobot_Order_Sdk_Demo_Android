package com.sobot.workorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sobot.workorder.R;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorderlibrary.api.model.SobotWOCategoryModelResult;

import java.util.List;

//工单类别 adapter
public class SobotWOCategoryAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<SobotWOCategoryModelResult> mList;
    private OnItemClickListener itemClickListener;

    public SobotWOCategoryAdapter(Context context, List<SobotWOCategoryModelResult> list) {
        super();
        this.mContext = context;
        this.mList = list;
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_adapter_order_category_items, parent, false);
        RecyclerView.ViewHolder vh = new CategorySmallViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, final int position) {
        final CategorySmallViewHolder myViewHolder = (CategorySmallViewHolder) vh;
        final SobotWOCategoryModelResult data = mList.get(position);
        myViewHolder.categoryTitle.setText(data.getTypeName());
        myViewHolder.categoryTitle.setSelected(data.isChecked());
        if (SobotConstantUtils.SOBOT_WO_ORDER_CATEGORY_NODEFLAG_NO == data.getNodeFlag()) {
            myViewHolder.categoryIshave.setVisibility(View.GONE);
        } else {
            myViewHolder.categoryIshave.setVisibility(View.VISIBLE);
            myViewHolder.categoryIshave.setBackgroundResource(R.drawable.sobot_icon_wo_arrow_icon);
        }

        if (data.isChecked()) {
            myViewHolder.categoryIshave.setVisibility(View.VISIBLE);
            myViewHolder.categoryIshave.setBackgroundResource(R.drawable.sobot_icon_wo_selected);
        }

        if (mList.size() >= 2) {
            if (position == mList.size() - 1) {
                myViewHolder.work_order_category_line.setVisibility(View.GONE);
            } else {
                myViewHolder.work_order_category_line.setVisibility(View.VISIBLE);
            }
        } else {
            myViewHolder.work_order_category_line.setVisibility(View.GONE);
        }
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null && vh.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(data, vh.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    class CategorySmallViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryTitle;
        private ImageView categoryIshave;
        private View work_order_category_line;

        CategorySmallViewHolder(View view) {
            super(view);
            categoryTitle = (TextView) view.findViewById(R.id.work_order_category_title);
            categoryIshave = (ImageView) view.findViewById(R.id.work_order_category_ishave);
            work_order_category_line = view.findViewById(R.id.work_order_category_line);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SobotWOCategoryModelResult info, int index);
    }
}