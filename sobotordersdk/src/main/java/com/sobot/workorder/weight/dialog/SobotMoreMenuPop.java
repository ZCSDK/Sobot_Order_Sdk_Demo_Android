package com.sobot.workorder.weight.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.workorder.R;
import com.sobot.workorder.weight.popwindow.SobotActionItem;

import java.util.List;

/**
 * @author: Sobot
 * 2022/4/12
 */
public class SobotMoreMenuPop {

    private Context mContext;
    private PopItemClick mPopItemClick;//点击事件
    private List<SobotActionItem> mActionItems;//显示的菜单
    private boolean isShowCheck = false;//是否显示选中
    private boolean isShowBold = false;//是否显示选中加粗

    public SobotMoreMenuPop(Context context, List<SobotActionItem> list, PopItemClick popItemClick) {
        mActionItems = list;
        mContext = context;
        mPopItemClick = popItemClick;
    }

    public SobotMoreMenuPop(Context context, List<SobotActionItem> list, boolean isShowCheck, boolean isShowBold, PopItemClick popItemClick) {
        mActionItems = list;
        mContext = context;
        mPopItemClick = popItemClick;
        this.isShowCheck = isShowCheck;
        this.isShowBold = isShowBold;
    }

    public PopupWindow getPopWindow() {

        View mPopView = LayoutInflater.from(mContext).inflate(R.layout.sobot_pop_title_menu, null);
        LinearLayout linearLayout = mPopView.findViewById(R.id.sobot_pop_content);
        if (null != mActionItems && mActionItems.size() > 0) {
            for (int i = 0; i < mActionItems.size(); i++) {
                final int index = i;
                View view = LayoutInflater.from(mContext).inflate(R.layout.sobot_pop_item_more_menu, null);
                TextView textView = view.findViewById(R.id.sobot_txt_title);
                final SobotActionItem actionItem = mActionItems.get(i);
                textView.setText(actionItem.mTitle);
                textView.setTextColor(mContext.getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
                // 设置文本居中          xaz
                textView.setSingleLine(true);
                //设置左边图片
                if (null != actionItem.mDrawable) {
                    actionItem.mDrawable.setBounds(0, 0, actionItem.mDrawable.getMinimumWidth(), actionItem.mDrawable.getMinimumHeight());
                    textView.setCompoundDrawables(actionItem.mDrawable, null, null, null);
                    textView.setCompoundDrawablePadding(30);
                }
                //设置行间距
                textView.setLineSpacing(0, 1.1f);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPopItemClick != null) {
                            mPopItemClick.onPopItemClick(actionItem, index);
                        }
                    }
                });
                ImageView imageView = view.findViewById(R.id.sobot_img_state);
                if (isShowCheck && actionItem.isChecked) {
                    imageView.setVisibility(View.VISIBLE);
                    textView.setSelected(true);
                } else {
                    textView.setSelected(false);
                    imageView.setVisibility(View.GONE);
                }

                if (isShowBold && actionItem.isChecked) {
                    textView.setTypeface(null, Typeface.BOLD);
                } else {
                    textView.setTypeface(null, Typeface.NORMAL);
                }
                if (i == mActionItems.size() - 1) {
                    view.findViewById(R.id.sobot_v_line).setVisibility(View.GONE);
                }
                linearLayout.addView(view);
            }
        }

        PopupWindow mPopWindow = new PopupWindow(mPopView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        return mPopWindow;
    }

    public interface PopItemClick {
        void onPopItemClick(SobotActionItem item, int index);
    }
}
