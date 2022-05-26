package com.sobot.workorder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.workorderlibrary.api.model.SobotTmpItemModel;

import java.util.ArrayList;
import java.util.List;

public class SobotWorkOrderUtilsAdapter extends BaseAdapter{

    private Context mContext;
    private List<SobotTmpItemModel> mListModel = new ArrayList<>();

    public SobotWorkOrderUtilsAdapter(Context context, List<SobotTmpItemModel> listModel) {
        this.mContext = context;
        mListModel.clear();
        mListModel.addAll(listModel);
    }

    @Override
    public int getCount() {
        return mListModel.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = View.inflate(mContext, SobotResourceUtils.getResLayoutId(mContext,"sobot_item_detail_his_child_item"), null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mListModel.get(position).isShowSummary()) {
            viewHolder.tv1.setVisibility(View.GONE);
            viewHolder.tv2.setVisibility(View.GONE);
            viewHolder.tv_summary_down.setVisibility(View.VISIBLE);
            viewHolder.tv3.setText(mListModel.get(position).getTitle());
        } else {
            viewHolder.tv1.setVisibility(View.VISIBLE);
            viewHolder.tv2.setVisibility(View.VISIBLE);
            viewHolder.tv_summary_down.setVisibility(View.GONE);
            viewHolder.tv1.setText(mListModel.get(position).getTitle());
            viewHolder.tv2.setText(mListModel.get(position).getContent());
        }

        return convertView;
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        View view_line;
        RelativeLayout tv_summary_down;

        public ViewHolder(View view){
            tv1 = view.findViewById(SobotResourceUtils.getResId(mContext,"tv_val1"));
            tv2 = view.findViewById(SobotResourceUtils.getResId(mContext,"tv_summary1"));
            tv3 = view.findViewById(SobotResourceUtils.getResId(mContext,"tv_summary2"));
            view_line = view.findViewById(SobotResourceUtils.getResId(mContext,"view_line"));
            tv_summary_down = view.findViewById(SobotResourceUtils.getResId(mContext,"tv_summary_down"));
        }
    }
}