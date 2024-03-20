package com.sobot.workorder.weight.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.workorder.R;

import java.util.ArrayList;
import java.util.List;

public class SobotWOCommonDialog extends SobotBottomDialog implements View.OnClickListener {
    private LinearLayout coustom_pop_layout, sobot_negativeButton;
    private RecyclerView rv_list;
    private LinearLayout ll_search;
    private TextView tv_title, tv_nodata;
    private SobotItemOnClick listener;
    private CallCommonAdapter adapter;
    private List<String> data;
    private int selectIndex = -1;
    private Activity activity;
    private String title;

    public SobotWOCommonDialog(Activity context, String title, List<String> list, int selectIndex, SobotItemOnClick listener) {
        super(context);
        activity = context;
        this.listener = listener;
        this.title = title;
        this.data = new ArrayList<>();
        data.addAll(list);
        this.selectIndex = selectIndex;
    }

    @Override
    protected int getLayout() {
        return R.layout.sobot_dialog_common_select;
    }

    @Override
    protected View getDialogContainer() {
        if (coustom_pop_layout == null) {
            coustom_pop_layout = findViewById(R.id.sobot_container);
        }

        return coustom_pop_layout;
    }

    @Override
    protected void initData() {
        adapter = new CallCommonAdapter(activity, data, listener);
        rv_list.setAdapter(adapter);
        if (data.size() == 0) {
            tv_nodata.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            tv_nodata.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initView() {
        sobot_negativeButton = findViewById(R.id.sobot_negativeButton);
        tv_nodata = findViewById(R.id.tv_nodata);
        tv_title = findViewById(R.id.sobot_tv_title);
        tv_title.setText(TextUtils.isEmpty(title) ? "" : title);
        rv_list = findViewById(R.id.rv_list);
        ll_search = findViewById(R.id.ll_search);
        ll_search.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        // 设置RecyclerView的LayoutManager
        rv_list.setLayoutManager(layoutManager);
        sobot_negativeButton.setOnClickListener(this);
    }

    public void setData(List<String> list) {
        if (data == null) {
            data = new ArrayList<>();
        } else {
            data.clear();
            data.addAll(list);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_negativeButton) {
            //关闭
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    class CallCommonAdapter extends RecyclerView.Adapter {
        private List<String> list;
        private Context mContext;
        private SobotItemOnClick listener;

        public CallCommonAdapter(Context context, List<String> list, SobotItemOnClick listener) {
            this.mContext = context;
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_item_select_exts, viewGroup, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            final int findIndex = i;
            String checkin = list.get(findIndex);
            MyViewHolder vh = (MyViewHolder) viewHolder;
            if (checkin != null) {
                vh.tv_ext.setText(checkin);
                if (selectIndex == findIndex) {
                    vh.iv_exts.setVisibility(View.VISIBLE);
                } else {
                    vh.iv_exts.setVisibility(View.GONE);
                }
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        if (listener != null) {
                            selectIndex = findIndex;
                            listener.selectItem(findIndex);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_exts;
        private TextView tv_ext;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ext = itemView.findViewById(R.id.tv_ext);
            iv_exts = itemView.findViewById(R.id.iv_exts);

        }
    }
}
