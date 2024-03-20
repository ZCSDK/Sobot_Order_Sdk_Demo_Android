package com.sobot.workorder.weight.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.workorder.R;

import java.util.List;

/**
 * @author: Sobot
 * 2022/5/6
 */
public class SobotMenuPop extends PopupWindow {

    private Context mContext;
    private LinearLayout linearLayout;

    private PopItemClick mPopItemClick;//点击事件
    private List<SobotActionItem> mActionItems;//显示的菜单
    private boolean isShowCheck;

    public SobotMenuPop(Context context){
        super(context);
        this.mContext = context;
        initView();
    }
    private void initView(){
        View mPopView = LayoutInflater.from(mContext).inflate(R.layout.sobot_wo_pop_title_menu, null);
        linearLayout = mPopView.findViewById(R.id.sobot_pop_content);

        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
//        this.setOutsideClickable(false);
        backgroundAlpha((Activity) mContext, 1.0f);//0.0-1.0
//        initDate();
    }

    public void setDate(List<SobotActionItem> mActionItems, final PopItemClick mPopItemClick,boolean isShowCheck) {
        this.mActionItems = mActionItems;
        this.mPopItemClick = mPopItemClick;
        this.isShowCheck = isShowCheck;
        initDate();
    }
    public void initDate(){
        if(linearLayout!=null)
            if (null != mActionItems && mActionItems.size() > 0) {
                for (int i = 0; i < mActionItems.size(); i++) {
                    final int index = i;
                    View view = LayoutInflater.from(mContext).inflate(R.layout.sobot_pop_item_title_menu, null);
                    TextView textView = view.findViewById(R.id.sobot_txt_title);
                    SobotActionItem actionItem = mActionItems.get(i);
                    textView.setText(actionItem.mTitle);
                    textView.setTextColor(mContext.getResources().getColor(R.color.sobot_wo_wenzi_gray1));
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
                    ImageView imageView = view.findViewById(R.id.sobot_img_state);
                    if(isShowCheck&& actionItem.isChecked){
                        imageView.setVisibility(View.VISIBLE);
                        textView.setSelected(true);
                    }else{
                        textView.setSelected(false);
                        imageView.setVisibility(View.GONE);
                    }
                    if(i==mActionItems.size()-1){
                        view.findViewById(R.id.sobot_v_line).setVisibility(View.GONE);
                    }
                    linearLayout.addView(view);
                }
            }
    }

    public interface PopItemClick {
        void onPopItemClick(int index);
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

}
