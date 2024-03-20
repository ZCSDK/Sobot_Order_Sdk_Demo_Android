package com.sobot.workorder.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sobot.workorder.R;
import com.sobot.workorder.base.SobotWOBaseFragment;
import com.sobot.workorder.utils.SobotTicketDictUtil;
import com.sobot.workorderlibrary.api.model.SobotTicketModel;


/**
 * 工单详情-来源
 *
 * @author jinxl
 */
public class SobotWODetailSourceFragment extends SobotWOBaseFragment implements View.OnClickListener {

    TextView mTvTicketFrom;
    TextView mTvStartName;
    TextView mTvTel;
    TextView mTvMail;

    private SobotTicketModel mTicketDetail;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mTicketDetail = (SobotTicketModel) savedInstanceState.getSerializable("ticketDetail");
            if (mTicketDetail != null) {
                initData();
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.sobot_fragment_wo_detail_source, container, false);
        initView(root);
        return root;
    }

    public void setTicketDetail(SobotTicketModel ticketDetail) {
        mTicketDetail = ticketDetail;
        if (mTvTicketFrom != null) {
            initData();
        }
    }

    private void initView(View rootView) {
        if (rootView == null) {
            return;
        }
        mTvTicketFrom = rootView.findViewById(R.id.sobot_tv_ticketFrom);
        mTvStartName = rootView.findViewById(R.id.sobot_tv_startName);
        mTvTel = rootView.findViewById(R.id.sobot_tv_tel);
        mTvMail = rootView.findViewById(R.id.sobot_tv_mail);
        if (mTicketDetail != null) {
            initData();
        }
    }


    public void initData() {
        mTvTicketFrom.setText(SobotTicketDictUtil.getTicketFromStr(getSobotActivity(), mTicketDetail.getTicketFrom()));
        String startName = "";
        if (isAdded()) {
            switch (mTicketDetail.getStartType()) {
                case 0:
                    startName = getSobotActivity().getString(R.string.sobot_service_string);
                    break;
                case 1:
                    startName = getSobotActivity().getString(R.string.sobot_common_user);
                    break;
            }
        }
        mTvStartName.setText(mTicketDetail.getStartName() + "(" + startName + ")");

        mTvTel.setText(mTicketDetail.getTel());
        mTvTel.setOnClickListener(this);
        if (!TextUtils.isEmpty(mTicketDetail.getEmail())) {
            mTvMail.setText(mTicketDetail.getEmail());
        } else {
            mTvMail.setText("- -");
        }
    }


    @Override
    public void onClick(View v) {
        if (v == mTvTel) {
            String phone = mTvTel.getText().toString();
            if (!TextUtils.isEmpty(phone)) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("tel:" + phone));// mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                getSobotActivity().startActivity(intent);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (needRefresh) {
            initData();
            needRefresh = false;
        }
    }

    @Override
    public void setNeedRefresh(boolean needRefresh) {
        super.setNeedRefresh(needRefresh);
        if (needRefresh) {
            if (getUserVisibleHint()) {
                initData();
                setNeedRefresh(false);
            }
        }
    }
}