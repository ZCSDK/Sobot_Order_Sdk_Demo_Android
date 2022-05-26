package com.sobot.workorder.adapter.base;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 */
public abstract class SobotBaseRecyclerViewAdapter<E> extends RecyclerView
        .Adapter<SobotBaseHolder> implements View
        .OnClickListener {
    protected OnItemClickListener mOnItemClickListener;
    protected List<E> list = new ArrayList<>();
    protected Context context = null;

    public SobotBaseRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    // 创建 ViewHolder 如果有多个类型 直接重写此方法  要注意加上click事件
    @Override
    public SobotBaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), getLayout(viewType), null);
        view.setOnClickListener(this);
        return getHolder(parent, viewType, view);
    }

    // 给ViewHolder 里的控件设置内容
    public void onBindViewHolder(SobotBaseHolder holder, int position) {
        holder.setData(this, position);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // 根 View 点击事件
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addDatas(List<E> datas) {
        list.clear();
        list.addAll(datas);
        this.notifyDataSetChanged();
    }

    public void addData(E data) {
        list.add(data);
        notifyItemInserted(list.size() - 1);
    }

    public void addData(int position, E data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 返回viewholder的值
     *
     * @return
     */
    public abstract int getLayout(int viewType);

    /**
     * 返回viewHolder对象
     *
     * @param view getLayout() 加载出的布局
     * @return
     */
    public abstract SobotBaseHolder getHolder(ViewGroup parent, int viewType, View view);

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
