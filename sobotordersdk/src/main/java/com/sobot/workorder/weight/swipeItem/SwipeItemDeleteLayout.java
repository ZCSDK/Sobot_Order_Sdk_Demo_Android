package com.sobot.workorder.weight.swipeItem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.workorder.R;


/**
 * 仿Ios 左滑删除控件
 * @author Created by jinxl on 2019/2/18.
 */
public class SwipeItemDeleteLayout extends LinearLayout implements View.OnClickListener, SwipeItemLayout.SwipeItemLayoutDelegate {

    private EditText edittext_add_user_info;


    public EditText getEdittext_add_user_info() {
        return edittext_add_user_info;
    }
    public TextView getTextView_delete() {
        return mTv_delete_text;
    }

    //删除图标
    private ImageView mIv_delete;
    //删除按钮
    private TextView mTv_delete_text;
    //滑动容器
    private SwipeItemLayout mSwipeitem_container;

    private OnMenuClickListener mOnMenuClickListener;
    private OnMenuDelClickListener mOnMenuDelClickListener;

    public SwipeItemDeleteLayout(Context context) {
        this(context, null);
    }
    public SwipeItemDeleteLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeItemDeleteLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.sobot_item_swipe_delete, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIv_delete = findViewById(R.id.iv_delete);
        mTv_delete_text = findViewById(R.id.tv_delete_text);
        mSwipeitem_container = findViewById(R.id.sil_swipeitem_container);
        edittext_add_user_info = findViewById(R.id.edittext_add_user_info);
        mSwipeitem_container.setDelegate(this);
        mIv_delete.setOnClickListener(this);
        mTv_delete_text.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mIv_delete) {
            //删除图标
            mIv_delete.setVisibility(INVISIBLE);
            //调用删除
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onDeleteClick(v, this);
            }
//            mSwipeitem_container.openWithAnim();
//            if (mOnMenuDelClickListener != null){
//                mOnMenuDelClickListener.onImgDelClick(this);
//            }
        }

        if (v == mTv_delete_text) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onDeleteClick(v, this);
            }
        }
    }

    public void close() {
        mIv_delete.setVisibility(VISIBLE);
        mSwipeitem_container.closeWithAnim();
    }

    @Override
    public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
        mIv_delete.setVisibility(INVISIBLE);
    }

    @Override
    public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
        mIv_delete.setVisibility(VISIBLE);
    }

    @Override
    public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {

    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mOnMenuClickListener = listener;
    }

    public void setOnMenuDelClickListener(OnMenuDelClickListener listener) {
        mOnMenuDelClickListener = listener;
    }

    public interface OnMenuClickListener {
        void onDeleteClick(View v, SwipeItemDeleteLayout swipeItemDeleteLayout);
    }

    public interface OnMenuDelClickListener {
        void onImgDelClick(SwipeItemDeleteLayout swipeItemDeleteLayout);
    }
}
