package com.sobot.workorder.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.utils.SobotDensityUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.activity.SobotWOCreateActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotDecimalDigitsInputFilter;
import com.sobot.workorder.utils.SobotNoDoubleClickListener;
import com.sobot.workorderlibrary.api.model.SobotCombinFormFieldListModel;
import com.sobot.workorderlibrary.api.model.SobotCombinFormFieldModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfigModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoListModel;
import com.sobot.workorderlibrary.api.model.SobotGroupFieldItemModel;
import com.sobot.workorderlibrary.api.model.SobotListTypeModel;

import java.util.List;

public class SobotGroupListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context mContext;
    private List<SobotCombinFormFieldListModel> mListData;
    private SobotCusFieldConfigModel mCusFieldConfig;
    private TextView mSummy;
    private SobotGroupListAdapter adapter;
    private OnTotalChangeListener totalChangeListener;
    private View groupView;

    public SobotGroupListAdapter(Context context, List<SobotCombinFormFieldListModel> list, SobotCusFieldConfigModel mCusFieldConfig, TextView mSummy) {
        super();
        this.mContext = context;
        this.mListData = list;
        this.mCusFieldConfig = mCusFieldConfig;
        this.mSummy = mSummy;
        adapter = this;
    }

    public List<SobotCombinFormFieldListModel> getmListData() {
        return mListData;
    }

    public void setmListData(List<SobotCombinFormFieldListModel> mListData) {
        this.mListData = mListData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.sobot_adatpter_order_cusfield_group_list_item, parent, false);
        RecyclerView.ViewHolder vh = new GroupListViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
        final SobotCombinFormFieldListModel combin = mListData.get(position);
        final GroupListViewHolder holder = (GroupListViewHolder) vh;
        holder.groupLable.setText("");
        holder.groupValue.setText("");
        holder.groupPrice.setText("");//单价
        holder.groupText.setText("");//文本类型
        holder.mFormField = null;

        if (combin != null) {
            final SobotCombinFormFieldModel formField = combin.getCombinFormField();//当前对象
            if (formField != null) {
                holder.groupLable.setText(formField.getFieldName());
                holder.rl.setTag(formField.getTmpId());
                if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SPINNER_TYPE == formField.getFieldType() || SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE == formField.getFieldType()) {

                    for (int i = 0; i < mListData.size(); i++) {
                        SobotCombinFormFieldListModel tmpField = mListData.get(i);
                        if (tmpField != null) {
                            SobotCombinFormFieldModel tmpcombin = tmpField.getCombinFormField();
                            if (tmpcombin != null) {
                                if (formField.getFieldLevel() == 1 && formField.getTmpId().equals(tmpcombin.getTmpId()) && formField.getFieldId().equals(tmpcombin.getRelatedField())) {
                                    formField.setClearValue(true);
                                } else {
                                    formField.setClearValue(false);
                                }
                            }
                        }
                    }
                    holder.groupPrice.setVisibility(View.GONE);//单价
                    holder.groupText.setVisibility(View.GONE);//文本
                    holder.groupValue.setVisibility(View.VISIBLE);//显示下拉列表类型的值
                    holder.groupImg.setVisibility(View.VISIBLE);
                    holder.groupValue.setOnClickListener(new SobotNoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            if (vh.getAdapterPosition() != RecyclerView.NO_POSITION) {
                                onClickListener(combin, vh.getAdapterPosition());
                            }
                        }
                    });

                    if (!TextUtils.isEmpty(formField.getText())) {
                        holder.groupValue.setText(formField.getText());
                    } else {
                        holder.groupValue.setText("");
                    }

                    if (formField.getFillFlag() == 1) {
                        holder.groupValue.setHint(formField.getFieldName() + "必选");
                    } else {
                        holder.groupValue.setHint("");
                    }

                } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_NUMBER_TYPE == formField.getFieldType()) {
                    holder.mFormField = formField;

                    holder.groupPrice.setVisibility(View.VISIBLE);//单价
                    holder.groupText.setVisibility(View.GONE);//文本
                    holder.groupValue.setVisibility(View.GONE);//显示下拉列表类型的值
                    holder.groupImg.setVisibility(View.GONE);

                    if (!TextUtils.isEmpty(formField.getValue())) {
                        holder.groupPrice.setText(formField.getValue());
                    } else {
                        holder.groupPrice.setText("");
                    }

                    if (formField.getFillFlag() == 1) {
                        holder.groupPrice.setHint(formField.getFieldName() + SobotResourceUtils.getResString(mContext,"sobot_required_string"));
                    } else {
                        holder.groupPrice.setHint("");
                    }
                    if (!TextUtils.isEmpty(formField.getValue())) {
                        sendTotalMsg(mCusFieldConfig,mSummy);
                    }
                } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SINGLE_LINE_TYPE == formField.getFieldType()) {
                    holder.mFormField = formField;
                    holder.groupPrice.setVisibility(View.GONE);//单价
                    holder.groupText.setVisibility(View.VISIBLE);
                    holder.groupValue.setVisibility(View.GONE);//下拉列表类型  值
                    holder.groupImg.setVisibility(View.GONE);

                    if (!TextUtils.isEmpty(formField.getValue())) {
                        holder.groupText.setText(formField.getValue());
                    } else {
                        holder.groupText.setText("");
                    }

                    if (formField.getFillFlag() == 1) {
                        holder.groupText.setHint(formField.getFieldName() + SobotResourceUtils.getResString(mContext,"sobot_required_string"));
                    } else {
                        holder.groupText.setHint("");
                    }
                }

                if (mListData.get(position).getCombinFormField().isLastItem()) {
                    holder.rl.setVisibility(View.VISIBLE);
                } else {
                    holder.rl.setVisibility(View.GONE);
                }
                holder.rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = mListData.size() - 1; i >= 0; i--) {
                            SobotCombinFormFieldModel field = mListData.get(i).getCombinFormField();
                            if (field != null) {
                                if (!TextUtils.isEmpty(v.getTag().toString()) && v.getTag().toString().equals(field.getTmpId())) {
                                    mListData.remove(i);
                                }
                            }
                        }

                        int count = 0;
                        SobotCombinFormFieldModel field = null;
                        for (int i = 0; i < mListData.size(); i++) {
                            field = mListData.get(i).getCombinFormField();
                            if (field != null) {
                                if (field.isLastItem()) {
                                    count++;
                                }
                            }
                        }

                        if (count == 1) {
                            if (field != null) {
                                field.setLastItem(false);
                            }
                        }

                        v.setVisibility(View.GONE);
                        notifyDataSetChanged();
                        if (!TextUtils.isEmpty(holder.groupPrice.getText().toString().trim())) {
                            sendTotalMsg(mCusFieldConfig,mSummy);
                        }
                        if (groupView != null) {
                            groupView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SobotDensityUtil.dp2px(mContext, 42) * getItemCount()));
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }


    @Override
    public void onClick(View v) {

    }

    class GroupListViewHolder extends RecyclerView.ViewHolder {

        TextView groupLable;
        TextView groupValue;
        EditText groupPrice;//单价
        EditText groupText;//文本类型
        ImageView groupImg;
        View lineDelView;
        TextView delTv;
        RelativeLayout rl;

        SobotCombinFormFieldModel mFormField;

        GroupListViewHolder(View view) {
            super(view);
            groupLable = view.findViewById(R.id.work_order_customer_field_group_text_lable);
            delTv = view.findViewById(R.id.work_order_customer_field_group_tv_delete);
            groupValue = view.findViewById(R.id.work_order_customer_field_group_text_value);
            groupPrice = view.findViewById(R.id.work_order_customer_field_group_price);//组合字段中的数值类型   单价
            groupPrice.setFilters(new InputFilter[]{new SobotDecimalDigitsInputFilter(), new InputFilter.LengthFilter(10)});
            groupText = view.findViewById(R.id.work_order_customer_field_group_edittext_text);//组合字段中的文本类型
            groupImg = view.findViewById(R.id.work_order_customer_field_group_img);
            lineDelView = view.findViewById(R.id.work_order_customer_field_group_line_delete);
            rl = view.findViewById(R.id.work_order_customer_field_group_rl_delete);
            final TextWatcher groupPriceTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if (mFormField != null) {
                        mFormField.setValue(charSequence.toString());
                        mFormField.setText(charSequence.toString());
                        mFormField.setTmpValue(charSequence.toString());
                        if (!TextUtils.isEmpty(mFormField.getParentFieldId())) {
                            sendTotalMsg(mCusFieldConfig,mSummy);
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };
            groupPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        groupPrice.addTextChangedListener(groupPriceTextWatcher);
                    } else {
                        groupPrice.removeTextChangedListener(groupPriceTextWatcher);
                    }
                }
            });
            final TextWatcher groupTextTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    if (mFormField != null) {
                        mFormField.setValue(charSequence.toString());
                        mFormField.setText(charSequence.toString());
                        mFormField.setTmpValue(charSequence.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            };

            groupText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        groupText.addTextChangedListener(groupTextTextWatcher);
                    } else {
                        groupText.removeTextChangedListener(groupTextTextWatcher);
                    }
                }
            });
        }
    }

    private void onClickListener(SobotCombinFormFieldListModel combin, int position) {
        if (combin != null) {
            SobotCombinFormFieldModel formField = combin.getCombinFormField();
            if (formField != null) {
                if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SPINNER_TYPE == formField.getFieldType()) {
                    List<SobotCusFieldDataInfoListModel> cusFieldDataInfoLists = combin.getCusFieldDataInfoList();
                    if (formField.getFieldLevel() == 1 && cusFieldDataInfoLists != null && cusFieldDataInfoLists.size() > 0) {
                        ((SobotWOCreateActivity) mContext).startGroupFieldActivity(mSummy, true, adapter, mCusFieldConfig, cusFieldDataInfoLists, formField, position, "");
                    } else if (formField.getFieldLevel() == 2) {
                        SobotCombinFormFieldListModel combinFormFieldList = getFirstCombinFormFieldList(combin);
                        if (combinFormFieldList != null) {
                            List<SobotCusFieldDataInfoListModel> cusFieldDataInfoList = combinFormFieldList.getCusFieldDataInfoList();
                            if (cusFieldDataInfoList != null && cusFieldDataInfoList.size() > 0) {
                                for (int j = 0; j < cusFieldDataInfoList.size(); j++) {
                                    SobotCusFieldDataInfoListModel cusFieldDataInfoList1 = cusFieldDataInfoList.get(j);
                                    if (!TextUtils.isEmpty(formField.getRelatedField()) && cusFieldDataInfoList1.isChecked()) {
                                        ((SobotWOCreateActivity) mContext).startGroupFieldActivity(mSummy, true, adapter, mCusFieldConfig,
                                                cusFieldDataInfoLists, formField, position, cusFieldDataInfoList1.getDataId());
                                    }
                                }
                            }
                        }
                    } else if (formField.getFieldLevel() == 3) {
                        SobotCombinFormFieldListModel combinFormFieldList = getFirstCombinFormFieldList(combin);
                        if (combinFormFieldList != null) {
                            SobotCombinFormFieldModel combinFormField = combinFormFieldList.getCombinFormField();
                            List<SobotGroupFieldItemModel> groupFieldItemModels = combinFormFieldList.getGroupFieldItemModels();
                            if (combinFormField != null && !TextUtils.isEmpty(combinFormField.getFieldId()) && groupFieldItemModels != null && groupFieldItemModels.size() > 0) {
                                for (int j = 0; j < groupFieldItemModels.size(); j++) {
                                    SobotGroupFieldItemModel groupFieldItemModel = groupFieldItemModels.get(j);
                                    if (groupFieldItemModel != null) {
                                        if (!TextUtils.isEmpty(formField.getRelatedField()) && formField.getRelatedField().equals(combinFormField.getFieldId()) && groupFieldItemModel.isChecked()) {
                                            ((SobotWOCreateActivity) mContext).startGroupFieldActivity(mSummy, false, adapter, mCusFieldConfig, cusFieldDataInfoLists, formField, position, groupFieldItemModel.getDataId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE == formField.getFieldType()) {
                    List<SobotCusFieldDataInfoListModel> cusFieldDataInfoLists = combin.getCusFieldDataInfoList();
                    if (cusFieldDataInfoLists != null && cusFieldDataInfoLists.size() > 0) {
                        ((SobotWOCreateActivity) mContext).startCascadeActivity(mSummy, true, adapter, mCusFieldConfig, cusFieldDataInfoLists, formField, position, "");
                    }
                }
            }
        }
    }

    private SobotCombinFormFieldListModel getFirstCombinFormFieldList(SobotCombinFormFieldListModel combin) {
        for (int i = 0; i < mListData.size(); i++) {
            boolean equals = mListData.get(i).getCombinFormField().getFieldId().equals(combin.getCombinFormField().getRelatedField());
            boolean equals1 = mListData.get(i).getCombinFormField().getTmpId().equals(combin.getCombinFormField().getTmpId());
            if (equals && equals1) {
                return mListData.get(i);
            }
        }
        return null;
    }

    public void addListData(List<SobotCombinFormFieldListModel> listData) {
        mListData.addAll(listData);
        notifyDataSetChanged();
    }

    //修改组合字段中数值类型的数据时发送 EventBus
    private void sendTotalMsg(SobotCusFieldConfigModel mCusFieldConfig,TextView mSummy) {
        if (totalChangeListener != null) {
            SobotListTypeModel model = new SobotListTypeModel();
            totalChangeListener.sendTotalMsg(model,mCusFieldConfig,mSummy);
        }
    }


    public interface OnTotalChangeListener {
        void sendTotalMsg(SobotListTypeModel totalModel,SobotCusFieldConfigModel mCusFieldConfig,TextView mSummy);
    }

    public OnTotalChangeListener getTotalChangeListener() {
        return totalChangeListener;
    }

    public void setTotalChangeListener(OnTotalChangeListener totalChangeListener) {
        this.totalChangeListener = totalChangeListener;
    }

    public View getGroupView() {
        return groupView;
    }

    public void setGroupView(View groupView) {
        this.groupView = groupView;
    }

}