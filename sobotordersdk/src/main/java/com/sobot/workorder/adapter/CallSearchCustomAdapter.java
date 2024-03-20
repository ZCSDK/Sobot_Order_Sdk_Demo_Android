package com.sobot.workorder.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.workorder.R;
import com.sobot.workorderlibrary.api.model.SobotCustomerModel;

import java.util.List;

public class CallSearchCustomAdapter extends RecyclerView.Adapter {
    private List<SobotCustomerModel> list;
    private Context mContext;
    private OnItemClick listener;
    private String select ="";
    private String selectCustomerId = "";
    public CallSearchCustomAdapter(Context context, List<SobotCustomerModel> list, OnItemClick listener){
        this.mContext = context;
        this.list = list;
        this.listener = listener;
    }

    public void setDate(String select) {
        this.select = select;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_item_call_search_custom2, viewGroup, false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final SobotCustomerModel checkin = list.get(i);
        MyViewHolder vh = (MyViewHolder) viewHolder;
        if(checkin!=null){
            if(!TextUtils.isEmpty(checkin.getNick())) {
                String name = checkin.getNick();
                SpannableString spannableString = new SpannableString(name);
                if(name.contains(select)){
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#09AEB0")), name.indexOf(select),name.indexOf(select)+select.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                vh.tv_call_custom_name.setText(spannableString);
            }else{
                vh.tv_call_custom_name.setText("--");
            }
            if(!TextUtils.isEmpty(checkin.getTel())) {
                String tel = checkin.getTel();
                SpannableString spannableString = new SpannableString(tel);
                if(tel.contains(select)){
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#09AEB0")), tel.indexOf(select),tel.indexOf(select)+select.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                vh.tv_call_custom_phone.setText(spannableString);
            }else{
                vh.tv_call_custom_phone.setText("--");
            }
            if(selectCustomerId.equals(checkin.getId())){
                vh.cb_customer.setChecked(true);
            }else{
                vh.cb_customer.setChecked(false);
            }
            vh.cb_customer.setClickable(false);

            vh.iv_customer_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        listener.onItemDetail(checkin);
                    }
                }
            });
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        selectCustomerId = checkin.getId();
                        listener.onItemClick(checkin);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_call_custom_name;
        private TextView tv_call_custom_phone;
        private CheckBox cb_customer;
        private ImageView iv_customer_detail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_call_custom_name = itemView.findViewById(R.id.tv_call_custom_name);
            tv_call_custom_phone = itemView.findViewById(R.id.tv_call_custom_phone);
            cb_customer = itemView.findViewById(R.id.cb_customer);
            iv_customer_detail = itemView.findViewById(R.id.iv_customer_detail);

        }
    }
    public interface OnItemClick{
        void onItemClick(SobotCustomerModel user);
        void onItemDetail(SobotCustomerModel user);
    }
}
