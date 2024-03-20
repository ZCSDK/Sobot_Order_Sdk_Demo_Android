package com.sobot.workorder.weight.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.utils.SobotDensityUtil;
import com.sobot.workorder.R;

import java.util.List;

/**
 * 录音播放 频率 弹窗
 */
public class SobotSpeedMenu {

    private Context mContext;
    private PopItemClick mPopItemClick;//点击事件
    private List<SobotActionItem> mActionItems;//显示的菜单

    private int mWidth;//宽度


    public SobotSpeedMenu(Context context, List<SobotActionItem> list, int width, PopItemClick popItemClick) {
        mActionItems = list;
        mContext = context;
        mWidth = width;
        mPopItemClick = popItemClick;
    }

    public PopupWindow getPopWindow() {
        View mPopView = LayoutInflater.from(mContext).inflate(R.layout.sobot_pop_menu, null);
        LinearLayout linearLayout = mPopView.findViewById(R.id.sobot_pop_content);
        if (null != mActionItems && mActionItems.size() > 0) {
            for (int i = 0; i < mActionItems.size(); i++) {
                final int index = i;
                View view = LayoutInflater.from(mContext).inflate(R.layout.sobot_pop_item_menu, null);
                TextView textView = view.findViewById(R.id.sobot_txt_name);
                final SobotActionItem actionItem = mActionItems.get(i);
                textView.setText(actionItem.mTitle);
                textView.setTextSize(14);
                textView.setTextColor(mContext.getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
                // 设置文本居中
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
                linearLayout.addView(view);
                if (i != (mActionItems.size() - 1)) {
                    View view1 = new View(mContext);
                    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SobotDensityUtil.dp2px(mContext,0.5f));
                    view1.setLayoutParams(lp);
                    view1.setBackgroundColor(mContext.getResources().getColor(R.color.sobot_wo_line_color));
                    linearLayout.addView(view1);
                }
            }
        }

        PopupWindow mPopWindow = new PopupWindow(mPopView,
                mWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        return mPopWindow;
    }

    public interface PopItemClick {
        void onPopItemClick(SobotActionItem item, int index);
    }
}
