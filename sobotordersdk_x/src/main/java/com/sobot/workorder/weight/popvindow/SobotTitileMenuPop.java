package com.sobot.workorder.weight.popvindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;

import java.util.List;

/**
 * @author: Sobot
 * 2022/4/12
 */
public class SobotTitileMenuPop {

    private Context mContext;
    private PopItemClick mPopItemClick;//点击事件
    private List<SobotActionItem> mActionItems;//显示的菜单
    private boolean isShowCheck=false;//是否显示选中
    private int type=TYPE_TOP_TRIANGLE;//
    public static int TYPE_BOTTOM_CIRCULAR=1;//下边显示圆角
    public static int TYPE_TOP_TRIANGLE=2;//顶部带三角

    public SobotTitileMenuPop(Context context, List<SobotActionItem> list, PopItemClick popItemClick) {
        this.type = type;
        mActionItems = list;
        mContext = context;
        mPopItemClick = popItemClick;
    }
    public SobotTitileMenuPop(Context context, List<SobotActionItem> list,int type,boolean isShowCheck, PopItemClick popItemClick) {
        this.type = type;
        mActionItems = list;
        mContext = context;
        mPopItemClick = popItemClick;
        this.isShowCheck = isShowCheck;
    }

        public PopupWindow getPopWindow() {

        View mPopView = LayoutInflater.from(mContext).inflate(SobotResourceUtils.getResLayoutId(mContext, "sobot_wo_pop_title_menu"), null);
        LinearLayout linearLayout = mPopView.findViewById(SobotResourceUtils.getResId(mContext, "sobot_pop_content"));
//        if(type == TYPE_BOTTOM_CIRCULAR){
//            linearLayout.setBackgroundResource(SobotResourceUtils.getResId(mContext,"sobot_bg_second_color_bottom_5dp"));
//        }else{
//            linearLayout.setBackgroundResource(SobotResourceUtils.getResId(mContext,"sobot_bg_second_color_bottom_5dp"));
//        }
        if (null != mActionItems && mActionItems.size() > 0) {
            for (int i = 0; i < mActionItems.size(); i++) {
                final int index = i;
                View view = LayoutInflater.from(mContext).inflate(SobotResourceUtils.getResLayoutId(mContext, "sobot_pop_item_title_menu"), null);
                TextView textView = view.findViewById(SobotResourceUtils.getResId(mContext,"sobot_txt_title"));
                SobotActionItem actionItem = mActionItems.get(i);
                textView.setText(actionItem.mTitle);
                textView.setTextColor(SobotResourceUtils.getResColorValue(mContext, "sobot_wo_wenzi_gray1"));
                textView.setTextSize(14);
                // 设置文本居中          xaz
                textView.setSingleLine(true);
                //设置左边图片
                if(null!=actionItem.mDrawable){
                    actionItem.mDrawable.setBounds(0, 0, actionItem.mDrawable.getMinimumWidth(), actionItem.mDrawable.getMinimumHeight());
                    textView.setCompoundDrawables(actionItem.mDrawable,null,null,null);
                    textView.setCompoundDrawablePadding(30);
                }
                //设置行间距
                textView.setLineSpacing(0, 1.1f);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPopItemClick != null) {
                            mPopItemClick.onPopItemClick(index);
                        }
                    }
                });
                ImageView imageView = view.findViewById(SobotResourceUtils.getResId(mContext,"sobot_img_state"));
                if(isShowCheck&& actionItem.isChecked){
                    imageView.setVisibility(View.VISIBLE);
                    textView.setSelected(true);
                }else{
                    textView.setSelected(false);
                    imageView.setVisibility(View.GONE);
                }
                if(i==mActionItems.size()-1){
                    view.findViewById(SobotResourceUtils.getResId(mContext,"sobot_v_line")).setVisibility(View.GONE);
                }
                linearLayout.addView(view);
            }
        }

        PopupWindow mPopWindow = new PopupWindow(mPopView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        return mPopWindow;
    }

    public interface PopItemClick {
        void onPopItemClick(int index);
    }
}
