package com.sobot.workorder.adapter.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2016/5/18.
 */
public abstract class SobotBaseHolder extends RecyclerView.ViewHolder {

    public SobotBaseHolder(View itemView) {
        super(itemView);
        initView();
    }

    /**
     * 用来findviewbyid
     */
    public abstract void initView();

    /**
     * 绑定数据
     *
     * @param position
     */
    public abstract void setData(SobotBaseRecyclerViewAdapter adapter, int position);
}
