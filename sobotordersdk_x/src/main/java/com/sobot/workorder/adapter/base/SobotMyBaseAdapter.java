package com.sobot.workorder.adapter.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <T>
 * @author jinliang
 */
public abstract class SobotMyBaseAdapter<T> extends BaseAdapter {

    public List<T> getmList() {
        return mList;
    }

    public List<T> mList = new ArrayList<T>();
    protected Context context;

    public void addData(List<T> moreList) {
        mList.addAll(0, moreList);
    }

    public void setData(List<T> moreList) {
        this.mList = moreList;
    }

    public SobotMyBaseAdapter(Context context, List<T> list) {
        super();
        this.mList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract View getView(int position, View convertView,
                                 ViewGroup parent);
}