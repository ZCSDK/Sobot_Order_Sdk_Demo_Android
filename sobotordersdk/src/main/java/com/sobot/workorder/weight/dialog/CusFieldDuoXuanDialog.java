package com.sobot.workorder.weight.dialog;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotDuoxuanAdapter;
import com.sobot.workorder.utils.CustomFieldsUtils;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;

import java.util.ArrayList;
import java.util.List;

/**
 * 多选对话框
 */
public class CusFieldDuoXuanDialog extends SobotBottomDialog implements View.OnClickListener {
    private TextView sobot_tv_title;
    private LinearLayout ll_search;
    private RecyclerView mListView;
    private LinearLayout coustom_pop_layout, sobot_negativeButton;
    private EditText custom_field_etSearch;
    private List<SobotCusFieldDataInfoList> infoLists;//返回的list
    private List<SobotCusFieldDataInfoList> tempInfoLists;//搜索的list
    private SobotDuoxuanAdapter adapter;
    private Activity activity;
    private DialogItemOnClick onClick;
    private ImageView iv_ext_search;
    private String title;

    private TextView btnSubmit;

    public CusFieldDuoXuanDialog(Activity context, String title, List<SobotCusFieldDataInfoList> list, DialogItemOnClick onClick) {
        super(context);
        this.activity = context;
        this.onClick = onClick;
        this.title = title;
        infoLists = new ArrayList<>();
        tempInfoLists = new ArrayList<>();
        if (list != null) {
            infoLists.addAll(list);
        }
    }

    @Override
    public void onClick(View v) {
        if (iv_ext_search == v) {
            custom_field_etSearch.clearFocus();
            if (!TextUtils.isEmpty(custom_field_etSearch.getText().toString())) {
                search(custom_field_etSearch.getText().toString());
            }
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.sobot_dialog_cus_duoxuan;
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
        if (!TextUtils.isEmpty(title)) {
            sobot_tv_title.setText(title);
        }
        if (infoLists != null && infoLists.size() != 0) {
            adapter = new SobotDuoxuanAdapter(activity, infoLists);
            mListView.setAdapter(adapter);
            adapter.setItemClickListener(new SobotDuoxuanAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(SobotCusFieldDataInfoList cusFieldDataInfoListModel, int position) {
                    if (cusFieldDataInfoListModel != null) {
                        for (int i = 0; i < infoLists.size(); i++) {
                            if (cusFieldDataInfoListModel.getDataId().equals(infoLists.get(i).getDataId())) {
                                if (infoLists.get(i).isChecked()) {
                                    infoLists.get(i).setChecked(false);
                                } else {
                                    infoLists.get(i).setChecked(true);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    protected void initView() {
        ll_search = findViewById(R.id.ll_search);
        mListView = findViewById(R.id.rv_list);
        sobot_negativeButton = findViewById(R.id.sobot_negativeButton);
        sobot_tv_title = findViewById(R.id.sobot_tv_title);
        custom_field_etSearch = findViewById(R.id.et_ext);
        btnSubmit = findViewById(R.id.btnSubmit);
        iv_ext_search = findViewById(R.id.iv_ext_search);
        mListView.setLayoutManager(new LinearLayoutManager(activity));
        custom_field_etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    CustomFieldsUtils.showKeyboard(getContext(), custom_field_etSearch);
                    ll_search.setBackgroundResource(R.drawable.sobot_bg_search_f);
                } else {
                    CustomFieldsUtils.hideKeyboard(getContext(), v);
                    ll_search.setBackgroundResource(R.drawable.sobot_bg_search);
                }
            }
        });
        sobot_negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭
                dismiss();
            }
        });
        searchListener();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                dismiss();
            }
        });
        iv_ext_search.setOnClickListener(this);
    }

    private void close() {
        if (onClick != null) {
            List<SobotCusFieldDataInfoList> list = new ArrayList<>();
            for (int i = 0; i < infoLists.size(); i++) {
                if (infoLists.get(i).isChecked()) {
                    list.add(infoLists.get(i));
                }
            }
            onClick.selectItem(list);
        }
    }

    private void searchListener() {
        custom_field_etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence searchStr, int start, int before, int count) {
                search(searchStr.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        custom_field_etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (TextUtils.isEmpty(custom_field_etSearch.getText())) {
                        mListView.setVisibility(View.VISIBLE);
                        if (tempInfoLists != null && tempInfoLists.size() > 0) {
                            infoLists.clear();
                            infoLists.addAll(tempInfoLists);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
    }

    private void search(String searchStr) {
        if (adapter != null) {
            adapter.setSearchText(searchStr);
        }
        List<SobotCusFieldDataInfoList> tmpList = new ArrayList<>();
        tmpList.clear();
        if (tempInfoLists != null && tempInfoLists.size() > 0) {
            infoLists.clear();
            infoLists.addAll(tempInfoLists);
        }

        for (int i = 0; i < infoLists.size(); i++) {
            if (infoLists.get(i).getDataName().contains(searchStr)) {
                tmpList.add(infoLists.get(i));
            }
        }

        if (tmpList != null && tmpList.size() > 0) {
            tempInfoLists.clear();
            tempInfoLists.addAll(infoLists);
            infoLists.clear();
            infoLists.addAll(tmpList);
            adapter.notifyDataSetChanged();
            mListView.setVisibility(View.VISIBLE);
        } else {
            if (!TextUtils.isEmpty(custom_field_etSearch.getText().toString())) {
                mListView.setVisibility(View.GONE);
            }
        }
    }
}
