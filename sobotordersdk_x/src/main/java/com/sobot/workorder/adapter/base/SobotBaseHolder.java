package com.sobot.workorder.adapter.base;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

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
