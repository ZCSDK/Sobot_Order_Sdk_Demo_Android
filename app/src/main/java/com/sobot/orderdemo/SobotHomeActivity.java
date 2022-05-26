package com.sobot.orderdemo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.utils.SobotDensityUtil;
import com.sobot.workorder.activity.SobotWOCreateActivity;
import com.sobot.workorder.activity.SobotWOSearchActivity;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.fragment.SobotWOclassificationFragment;
import com.sobot.workorder.utils.SobotConstantUtils;

/**
 * 工单分类界面-一级列表
 */
public class SobotHomeActivity extends SobotWOBaseActivity implements View.OnClickListener {

    SobotWOclassificationFragment classificationFragment;
    SobotSettingFragment settingFragment;
    //左上角创建工单按钮
    private TextView sobot_tv_right_create;
    //右上角搜索工单按钮
    private TextView sobot_tv_right_second_search;
    private TextView tv_order, tv_setting;
    private FrameLayout setting_fragment;
    private FrameLayout order_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initBundleData(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return SobotResourceUtils.getResLayoutId(getSobotBaseContext(), "sobot_home");
    }

    @Override
    protected void initView() {
        tv_order = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "tv_order"));
        tv_setting = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "tv_setting"));
        tv_order.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
        Drawable ordertop = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "order_menu"));
        ordertop.setBounds(0, 0, SobotDensityUtil.dp2px(getSobotBaseContext(), 24), SobotDensityUtil.dp2px(getSobotBaseContext(), 24));
        tv_order.setCompoundDrawables(null, ordertop, null, null);

        Drawable settingtop = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "setting_menu_nor"));
        settingtop.setBounds(0, 0, SobotDensityUtil.dp2px(getSobotBaseContext(), 24), SobotDensityUtil.dp2px(getSobotBaseContext(), 24));
        tv_setting.setCompoundDrawables(null, settingtop, null, null);


        setting_fragment = findViewById(R.id.setting_fragment);
        order_fragment = findViewById(R.id.order_fragment);
        order_fragment.setVisibility(View.VISIBLE);
        setting_fragment.setVisibility(View.GONE);
        tv_order.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_theme_color")));
        tv_setting.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_wenzi_gray1")));

        sobot_tv_right_create = getLeftMenu();
        sobot_tv_right_create.setVisibility(View.VISIBLE);
        Drawable left = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_icon_create_workorder"));
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        sobot_tv_right_create.setCompoundDrawables(left, null, null, null);
        sobot_tv_right_create.setOnClickListener(this);
        sobot_tv_right_second_search = findViewById(SobotResourceUtils.getResId(getSobotBaseContext(), "sobot_tv_right"));

        Drawable left2 = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_icon_search_workorder"));
        left2.setBounds(0, 0, left2.getMinimumWidth(), left2.getMinimumHeight());
        sobot_tv_right_second_search.setCompoundDrawables(left2, null, null, null);
        sobot_tv_right_second_search.setOnClickListener(this);
            sobot_tv_right_second_search.setVisibility(View.VISIBLE);
        classificationFragment = (SobotWOclassificationFragment) getSupportFragmentManager()
                .findFragmentById(getResId("order_fragment"));
        if (classificationFragment == null) {
            classificationFragment = SobotWOclassificationFragment.newInstance();

            addFragmentToActivity(getSupportFragmentManager(),
                    classificationFragment, getResId("order_fragment"));
        }

        settingFragment = (SobotSettingFragment) getSupportFragmentManager()
                .findFragmentById(getResId("setting_fragment"));
        if (settingFragment == null) {
            settingFragment = SobotSettingFragment.newInstance();

            addFragmentToActivity(getSupportFragmentManager(),
                    settingFragment, getResId("setting_fragment"));
        }
    }

    @Override
    protected void initData() {
        setTitle(SobotResourceUtils.getResString(SobotHomeActivity.this, "sobot_title_workorder_center_string"));
    }

    public void addFragmentToActivity(FragmentManager fragmentManager,
                                      Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_tv_right_create) {
            Intent createIntent = new Intent(getSobotBaseActivity(), SobotWOCreateActivity.class);
            startActivityForResult(createIntent, SobotConstantUtils.SOBOT_NEED_REFRESH);
        } else if (v == sobot_tv_right_second_search) {
            Intent searchIntent = new Intent(getSobotBaseActivity(), SobotWOSearchActivity.class);
            startActivityForResult(searchIntent, SobotConstantUtils.SOBOT_NEED_REFRESH);
        } else if (v == tv_order) {
            order_fragment.setVisibility(View.VISIBLE);
            setting_fragment.setVisibility(View.GONE);
            tv_order.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_theme_color")));
            tv_setting.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_wenzi_gray1")));
            Drawable ordertop = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "order_menu"));
            ordertop.setBounds(0, 0, SobotDensityUtil.dp2px(getSobotBaseContext(), 24), SobotDensityUtil.dp2px(getSobotBaseContext(), 24));
            tv_order.setCompoundDrawables(null, ordertop, null, null);

            Drawable settingtop = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "setting_menu_nor"));
            settingtop.setBounds(0, 0, SobotDensityUtil.dp2px(getSobotBaseContext(), 24), SobotDensityUtil.dp2px(getSobotBaseContext(), 24));
            tv_setting.setCompoundDrawables(null, settingtop, null, null);
        } else if (v == tv_setting) {
            order_fragment.setVisibility(View.GONE);
            setting_fragment.setVisibility(View.VISIBLE);
            tv_order.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_wenzi_gray1")));
            tv_setting.setTextColor(ContextCompat.getColor(getSobotBaseContext(), getResColorId("sobot_wo_theme_color")));
            Drawable ordertop = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "order_menu_nor"));
            ordertop.setBounds(0, 0, SobotDensityUtil.dp2px(getSobotBaseContext(), 24), SobotDensityUtil.dp2px(getSobotBaseContext(), 24));
            tv_order.setCompoundDrawables(null, ordertop, null, null);

            Drawable settingtop = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "setting_menu"));
            settingtop.setBounds(0, 0, SobotDensityUtil.dp2px(getSobotBaseContext(), 24), SobotDensityUtil.dp2px(getSobotBaseContext(), 24));
            tv_setting.setCompoundDrawables(null, settingtop, null, null);
        }
    }

}