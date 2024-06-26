package com.sobot.workorder.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sobot.common.login.permission.SobotPermissionManager;
import com.sobot.workorder.R;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.fragment.SobotWOclassificationFragment;

/**
 * 工单分类界面-一级列表
 */
public class SobotWOClassificationActivity extends SobotWOBaseActivity implements View.OnClickListener {

    SobotWOclassificationFragment classificationFragment;
    //右上角创建工单按钮
    private TextView sobot_tv_right_create;
    //右上角搜索工单按钮
    private TextView sobot_tv_right_second_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initBundleData(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.sobot_activity_wo_classification;
    }

    @Override
    protected void initView() {
        sobot_tv_right_create = findViewById(R.id.sobot_tv_right_second);
        sobot_tv_right_create.setVisibility(View.VISIBLE);
        Drawable left = getResources().getDrawable(R.drawable.sobot_icon_create_workorder);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        sobot_tv_right_create.setCompoundDrawables(left, null, null, null);
        sobot_tv_right_create.setOnClickListener(this);
        sobot_tv_right_second_search = findViewById(R.id.sobot_tv_right);

        Drawable left2 = getResources().getDrawable(R.drawable.sobot_icon_search_workorder);
        left2.setBounds(0, 0, left2.getMinimumWidth(), left2.getMinimumHeight());
        sobot_tv_right_second_search.setCompoundDrawables(left2, null, null, null);
        sobot_tv_right_second_search.setOnClickListener(this);
        if (SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_WORK_SEARCH)) {
            sobot_tv_right_second_search.setVisibility(View.VISIBLE);
        } else {
            sobot_tv_right_second_search.setVisibility(View.GONE);
        }
        classificationFragment = (SobotWOclassificationFragment) getSupportFragmentManager()
                .findFragmentById(R.id.sobot_contentFrame);
        if (classificationFragment == null) {
            classificationFragment = SobotWOclassificationFragment.newInstance();

            addFragmentToActivity(getSupportFragmentManager(),
                    classificationFragment, R.id.sobot_contentFrame);
        }
    }

    @Override
    protected void initData() {
        setTitle(getString(R.string.sobot_title_workorder_center_string));

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
            startActivity(createIntent);
        } else if (v == sobot_tv_right_second_search) {
            Intent searchIntent = new Intent(getSobotBaseActivity(), SobotWOSearchActivity.class);
            startActivity(searchIntent);
        }
    }

}