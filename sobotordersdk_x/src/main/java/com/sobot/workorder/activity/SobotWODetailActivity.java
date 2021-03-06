package com.sobot.workorder.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.base.SobotBaseFragment;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.fragment.SobotWODetailFragment;
import com.sobot.workorder.fragment.SobotWODetailHistoryFragment;
import com.sobot.workorder.fragment.SobotWODetailSourceFragment;
import com.sobot.workorder.permission.SobotPermissionApi;
import com.sobot.workorder.permission.SobotPermissionManager;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotWorkOrderUtils;
import com.sobot.workorder.weight.dialog.SobotCommonDialog;
import com.sobot.workorder.weight.popvindow.SobotActionItem;
import com.sobot.workorder.weight.popvindow.SobotTitileMenuPop;
import com.sobot.workorder.weight.tab.SobotPagerSlidingTab;
import com.sobot.workorder.weight.tab.SobotViewPagerAdapter;
import com.sobot.workorder.weight.toast.SobotToastUtil;
import com.sobot.workorderlibrary.api.apiutils.SobotWOBaseSeconndCode;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;
import com.sobot.workorderlibrary.api.model.SobotWODetailResultModel;
import com.sobot.workorderlibrary.api.model.SobotWOSecondItemModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ??????????????????
 */
public class SobotWODetailActivity extends SobotWOBaseActivity implements View.OnClickListener, SobotTitileMenuPop.PopItemClick {
    private String detailInfoId;
    private SobotWODetailResultModel detailResultInfo;
    private TextView rightMenu,tvReply,tvTakeWorkOrder,tvBefore,tvNext;
    private SobotPagerSlidingTab mTab;
    private ViewPager viewPager;
    private SobotViewPagerAdapter pagerAdapter;
    private List<SobotBaseFragment> mFragments;
    private SobotWODetailFragment detailFragment;
    private SobotWODetailSourceFragment sourceFragment;
    private SobotWODetailHistoryFragment historyFragment;

    private boolean flag = false;// true???????????????????????????????????? false?????????????????????
    private ArrayList<SobotWOSecondItemModel> items = new ArrayList();;//??????????????????
    private int position = 0;//????????????,???????????????items????????????
    private int ticketNum = 0;//???????????????????????????
    private boolean isLoadMore = false;//????????????????????????
    private ArrayList<String> itemsTemp = new ArrayList<String>();/*???????????????????????????????????? ticketId*/

    private ArrayList<SobotActionItem> actionItems;
    private PopupWindow popupWindow;

    TextView conCernMenu;//???????????????


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registBroadCast();
    }

    @Override
    protected int getContentViewResId() {
        return SobotResourceUtils.getResLayoutId(getSobotBaseContext(), "sobot_activity_wo_detail");
    }

    @Override
    protected void initView() {
        if(getIntent()!=null) {
            detailInfoId = getIntent().getStringExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_TICKETID);
            flag = getIntent().getBooleanExtra("flag", false);
            ticketNum = getIntent().getIntExtra("ticketNum", 0);
            isLoadMore = getIntent().getBooleanExtra("isLoadMore", false);
            if(flag){
                items = (ArrayList<SobotWOSecondItemModel>) getIntent().getSerializableExtra("items");
            }

        }
        conCernMenu = getRightSecondMenu();
        conCernMenu.setOnClickListener(this);
        rightMenu = getRightMenu();
        Drawable left = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_wo_detail_more_selector"));
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        rightMenu.setCompoundDrawables(left, null, null, null);
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setOnClickListener(this);
//        if(!SobotStringUtils.isEmpty(detailInfoId)) {
//            setTitle(detailInfoId);
//            if(SobotPermissionManager.isHasPermission(getSobotBaseContext(),SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER)) {
//                Drawable left = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_wo_detail_more_selector"));
//                left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
//                rightMenu.setCompoundDrawables(left, null, null, null);
//                rightMenu.setVisibility(View.VISIBLE);
//                rightMenu.setOnClickListener(this);
//            }
//        }
        mTab = findViewById(SobotResourceUtils.getResId(this,"sobot_wo_pst_tab"));
        viewPager = findViewById(SobotResourceUtils.getResId(this,"vp_wo_select_tab"));
        tvReply = findViewById(SobotResourceUtils.getResId(this,"tv_work_order_reply"));
        tvTakeWorkOrder = findViewById(SobotResourceUtils.getResId(this,"tv_work_order_take"));
        tvBefore = findViewById(SobotResourceUtils.getResId(this,"tv_work_order_before"));
        tvNext = findViewById(SobotResourceUtils.getResId(this,"tv_work_order_next"));
        tvReply.setOnClickListener(this);
        tvTakeWorkOrder.setOnClickListener(this);
        tvBefore.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        if(flag){
            //?????????????????????
            tvReply.setVisibility(View.VISIBLE);
            tvTakeWorkOrder.setVisibility(View.VISIBLE);
            setTitle("");
        }else{
            //????????????
            tvReply.setVisibility(View.VISIBLE);
            tvTakeWorkOrder.setVisibility(View.GONE);
            setTitle("????????????");
        }
        mFragments = new ArrayList<>();
        detailFragment = new SobotWODetailFragment();
        sourceFragment =  new SobotWODetailSourceFragment();
        historyFragment = new SobotWODetailHistoryFragment();
        initTab();
        if(flag){
            tvBefore.setVisibility(View.VISIBLE);
            tvNext.setVisibility(View.VISIBLE);
            positionNowWorkOrderID();
        }else{
            tvBefore.setVisibility(View.INVISIBLE);
            tvNext.setVisibility(View.INVISIBLE);
        }
        actionItems = new ArrayList<>();
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this,"sobot_edit_work_order_string"),getResources().getDrawable(SobotResourceUtils.getDrawableId(this,"sobot_icon_detail_edit_menu"))));
        actionItems.add(new SobotActionItem(SobotResourceUtils.getResString(this,"sobot_delete_work_order_string"),getResources().getDrawable(SobotResourceUtils.getDrawableId(this,"sobot_icon_detail_delete_menu"))));


    }

    @Override
    protected void initData() {
        if(!SobotStringUtils.isEmpty(detailInfoId)) {
            showProgressDialog();
            zhiChiApi.getOrderDetail(SobotWODetailActivity.this, detailInfoId, new SobotResultCallBack<SobotWODetailResultModel>() {
                @Override
                public void onSuccess(SobotWODetailResultModel sobotTicketModel) {
                    dismissProgressDialog();
                    detailResultInfo = sobotTicketModel;
                    setTitle("#" +detailResultInfo.getTicketDetail().getTicketCode());
                    if(detailResultInfo.getTicketDetail()!=null){
                        setOrderConcern(detailResultInfo.getTicketDetail().getConcerned());
                    }
                    if (detailResultInfo.getTicketDetail() != null && TextUtils.isEmpty(detailResultInfo.getTicketDetail().getDealUserName()) && detailResultInfo.getTicketDetail().getIsShowReceipt() == 0 &&
                            (detailResultInfo.getTicketDetail().getTicketStatus() == 0 || detailResultInfo.getTicketDetail().getTicketStatus() == 1 || detailResultInfo.getTicketDetail().getTicketStatus() == 2)) {
                        tvTakeWorkOrder.setVisibility(View.VISIBLE);
                    } else {
                        tvTakeWorkOrder.setVisibility(View.GONE);
                    }
                    //??????????????????????????????????????????????????????
                    if (detailResultInfo.getTicketDetail().getTicketStatus() == 98) {
                        tvReply.setVisibility(View.GONE);
                        rightMenu.setVisibility(View.GONE);

                    } else if (detailResultInfo.getTicketDetail().getTicketStatus() == 99) {//????????????????????????????????????
                        //????????????????????????????????????
                        if (SobotPermissionManager.isHasPermission(getBaseContext(),SobotPermissionManager.USER_PERMISSION_TYPE_ACTIVATION_WORK_ORDER)) {
                            tvReply.setVisibility(View.VISIBLE);
                            tvReply.setText(getResStringId("sobot_detail_activation_order_string"));
                        }
                        //???????????????????????????????????????
                        if (SobotPermissionManager.isHasPermission(getBaseContext(),SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER)) {
                            rightMenu.setVisibility(View.GONE);
                            if(actionItems.size()==2){
                                actionItems.remove(1);
                            }
                        }else{
                            rightMenu.setVisibility(View.GONE);
                        }
                    }else{

                        SobotServiceVoModel user = SobotSharedPreferencesUtil.getInstance(getBaseContext()).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
                        //???????????????????????????????????????????????????
                        if(SobotPermissionManager.checkPermission(SobotWODetailActivity.this, SobotPermissionApi.USER_PERMISSION_TYPE_EDIT_WORK_ORDER) || user.getServiceId().equals(detailResultInfo.getTicketDetail().getDealUserId())){
                            if(!SobotPermissionManager.isHasPermission(getBaseContext(),SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER)){
                                //??????????????????
                                if(actionItems.size()==2){
                                    actionItems.remove(1);
                                }
                            }
                            rightMenu.setVisibility(View.VISIBLE);
                        }else{
                            rightMenu.setVisibility(View.GONE);
                        }

                    }

                    detailFragment.setDetailResultModel(detailResultInfo);
                    sourceFragment.setTicketDetail(detailResultInfo.getTicketDetail());
                    historyFragment.setTicketId(detailResultInfo.getTicketDetail().getTicketId());
                }

                @Override
                public void onFailure(Exception e, String s) {
                    dismissProgressDialog();
                    SobotLogUtils.d("????????????==="+s);
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string"):s , SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

                }
            });
        }

    }

    public void refresh(){
        initData();
    }
    private void initTab(){
        mFragments.clear();
        mFragments.add(detailFragment);
        mFragments.add(sourceFragment);
        if(SobotPermissionManager.isHasPermission(getBaseContext(),SobotPermissionManager.USER_PERMISSION_TYPE_WORK_HISTORY)){
            mFragments.add(historyFragment);
            pagerAdapter = new SobotViewPagerAdapter(getSobotBaseContext(),getSupportFragmentManager(),new String[]{SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_detail_work_order_string"),SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_source_work_order_string"),SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_history_work_order_string")},mFragments);
        }else{
            pagerAdapter = new SobotViewPagerAdapter(getSobotBaseContext(),getSupportFragmentManager(),new String[]{SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_detail_work_order_string"),SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_source_work_order_string")},mFragments);
        }
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(mFragments.size());//??????????????????????????????fragment???fragment?????????
        mTab.setViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        if(detailResultInfo==null||(detailResultInfo!=null&&detailResultInfo.getTicketDetail()==null)){
            return;
        }
        if(view == rightMenu){
            //????????????????????????????????????
            //??????
            if (null == popupWindow) {
                SobotTitileMenuPop sobotSelectSearchTypePop = new SobotTitileMenuPop(SobotWODetailActivity.this, actionItems, SobotWODetailActivity.this);
                popupWindow = sobotSelectSearchTypePop.getPopWindow();
                //???????????????????????????
                WindowManager.LayoutParams attributes = getWindow().getAttributes();
                attributes.alpha = 0.5f;
                getWindow().setAttributes(attributes);
                //?????????????????? ????????????????????????
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams attributes = getWindow().getAttributes();
                        attributes.alpha = 1;
                        getWindow().setAttributes(attributes);
                    }
                });
            }
            popupWindow.showAsDropDown(rightMenu, -15, 20);
//
        }else if(view ==tvReply){
            if(tvReply.getText().equals(getResString("sobot_detail_activation_order_string"))){
                //????????????
                Intent intent = new Intent(getApplicationContext(), SobotActivateWOActivity.class);
                intent.putExtra(SobotConstantUtils.sobto_work_order_data, detailResultInfo.getTicketDetail());
                startActivityForResult(intent, SobotConstantUtils.sobot_wo_reply_response_need_update);
            }else {
                //????????????
                Intent intent = new Intent(getApplicationContext(), SobotWOCreateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SobotConstantUtils.sobto_work_order_data, detailResultInfo.getTicketDetail());
                bundle.putInt(SobotConstantUtils.sobot_wo_display_type, SobotConstantUtils.sobot_wo_display_type_reply);
                intent.putExtras(bundle);
                startActivityForResult(intent, SobotConstantUtils.sobot_wo_reply_response_need_update);
            }
        }else if(view ==tvTakeWorkOrder){
            //??????
            zhiChiApi.acceptOrder(getSobotBaseActivity(), detailResultInfo.getTicketDetail().getTicketId(), new SobotResultCallBack<String>() {
                @Override
                public void onSuccess(String response) {
                    //??????
                    String msg = "";
                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.has("code") && !object.isNull("code")) {
                            if ("1".equals(object.optString("code"))) {
                                SobotToastUtil.showCustomToast(getBaseContext(), SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_accept_order_string"),SobotResourceUtils.getDrawableId(getBaseContext(),"sobot_icon_login_right"));
                                //????????????????????????
                                SobotServiceVoModel user = SobotSharedPreferencesUtil.getInstance(getBaseContext()).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
                                if (user != null) {
                                    detailResultInfo.getTicketDetail().setDealUserName(user.getServiceName());
                                    detailResultInfo.getTicketDetail().setDealUserId(user.getServiceId());
                                }
                                //??????????????????
                                tvTakeWorkOrder.setVisibility(View.GONE);
                                setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                                return;
                            }
                        }
                        if (object.has("msg")) {
                            msg = object.optString("msg", "");
                        } else if (object.has("retMsg")) {
                            msg = object.optString("retMsg", "");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    com.sobot.common.ui.toast.SobotToastUtil.showCustomToast(getBaseContext(), SobotStringUtils.isEmpty(msg) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : msg, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));
                    //??????????????????????????????
                    tvTakeWorkOrder.setVisibility(View.GONE);
                    //????????????
                    initData();
                }

                @Override
                public void onFailure(Exception e, String s) {
                    SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(s) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string"):s , SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

                }
            });
        }else if(view ==tvBefore){
            //?????????
            //??????
            if(position>0){
                position = position-1;
                detailInfoId = items.get(position).getTicketId();
                initData();
                if(position==0){
                    tvBefore.setEnabled(false);
                }
            }
            tvNext.setEnabled(true);
        }else if(view ==tvNext){
            //?????????
            tvBefore.setEnabled(true);
            if(position<0){
                position=0;
            }
            if(position<items.size() - 1){
                position = position+1;
                detailInfoId = items.get(position).getTicketId();
                initData();
                tvNext.setEnabled(true);
                if(position==items.size() - 1) {
                    if(isLoadMore) {
                        Intent intent = new Intent(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_REQUEST);
                        sendBroadcast(intent);
                    }else{
                        tvNext.setEnabled(false);
                    }
                }
            }

        }else if(view == conCernMenu){
            if(detailResultInfo == null || detailResultInfo.getTicketDetail() == null){
                return;
            }
            final boolean isConcern= !detailResultInfo.getTicketDetail().getConcerned();

            zhiChiApi.concernOrder(getSobotBaseActivity(), detailResultInfo.getTicketDetail().getTicketId(), isConcern, new SobotResultCallBack<SobotWOBaseSeconndCode>() {
                @Override
                public void onSuccess(SobotWOBaseSeconndCode s) {
                    if("1".equals(s.getCode())){
                        setOrderConcern(isConcern);
                        detailResultInfo.getTicketDetail().setIsConcerned(isConcern);
                    }else{
                        if(SobotStringUtils.isEmpty(s.getMsg())){
                            SobotToastUtil.showCustomToast(getBaseContext(),s.getMsg());
                        }
                    }
                    //??????
                    if (isConcern) {
                        if (TextUtils.isEmpty(detailResultInfo.getTicketDetail().getConcernServiceName())) {//???????????????
                            detailResultInfo.getTicketDetail().setConcernServiceName(SobotWorkOrderUtils.getServiceName(getBaseContext()));
                        } else {
                            detailResultInfo.getTicketDetail().setConcernServiceName(detailResultInfo.getTicketDetail().getConcernServiceName() + "," + SobotWorkOrderUtils.getServiceName(getBaseContext()));
                        }
                        SobotToastUtil.showCustomToast(getBaseContext(),"????????????",SobotResourceUtils.getDrawableId(getBaseContext(),"sobot_icon_login_right"));
                    } else {
                        if (detailResultInfo.getTicketDetail().getConcernServiceName().contains(SobotWorkOrderUtils.getServiceName(getBaseContext()))) {//??????????????????
                            String concernName = detailResultInfo.getTicketDetail().getConcernServiceName().replaceFirst(SobotWorkOrderUtils.getServiceName(getBaseContext()) + ",", "");
                            if (detailResultInfo.getTicketDetail().getConcernServiceName().equals(concernName)) {
                                concernName = concernName.replaceFirst(SobotWorkOrderUtils.getServiceName(getBaseContext()), "");
                            }
                            detailResultInfo.getTicketDetail().setConcernServiceName(concernName);
                        }
                        SobotToastUtil.showCustomToast(getBaseContext(),"???????????????");
                    }
                    //??????????????????
                    historyFragment.setTicketId(detailResultInfo.getTicketDetail().getTicketId());
                }

                @Override
                public void onFailure(Exception e, String s) {
                    if(SobotStringUtils.isEmpty(s)){
                        SobotToastUtil.showCustomToast(getBaseContext(),s);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SobotConstantUtils.sobot_wo_reply_response_need_update){
            //????????????????????????????????????fragment
            initData();
        }
    }


    //????????????
    private void setOrderConcern(boolean isConcern) {
        if(conCernMenu.getVisibility()!=View.VISIBLE){
            conCernMenu.setVisibility(View.VISIBLE);
        }
        Drawable left;
        if (isConcern) {
            left = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_detail_concern_yes_selector"));
        } else {
            left = getResources().getDrawable(SobotResourceUtils.getDrawableId(getSobotBaseContext(), "sobot_detail_concern_no_selector"));
        }
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        conCernMenu.setCompoundDrawables(left, null, null, null);

    }

    @Override
    public void onPopItemClick(int index) {
        popupWindow.dismiss();
        if(index==0){
            //????????????
            Intent intent = new Intent(getApplicationContext(), SobotWOCreateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(SobotConstantUtils.sobto_work_order_data,detailResultInfo.getTicketDetail());
            bundle.putInt(SobotConstantUtils.sobot_wo_display_type, SobotConstantUtils.sobot_wo_display_type_edit);
            intent.putExtras(bundle);
            startActivityForResult(intent, SobotConstantUtils.sobot_wo_reply_response_need_update);
        }else{
            //????????????
            //????????????????????????
            SobotCommonDialog dialog = new SobotCommonDialog(SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_delete_hint_string"), SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_delete_string"), new SobotCommonDialog.OnBtnClickListener() {
                @Override
                public void onClick() {
                    deleteOrder();
                }
            }, SobotResourceUtils.getResString(getSobotBaseContext(),"sobot_cancle_string"), new SobotCommonDialog.OnBtnClickListener() {
                @Override
                public void onClick() {
                }
            });
            dialog.show(SobotWODetailActivity.this.getSupportFragmentManager(), "dialog");
        }
    }
    private void deleteOrder(){
        showProgressDialog();
        zhiChiApi.deleteOrder(getSobotBaseActivity(), detailInfoId, new SobotResultCallBack<SobotWOBaseSeconndCode>() {
            @Override
            public void onSuccess(SobotWOBaseSeconndCode s) {
                dismissProgressDialog();
                if("1".equals(s.getCode())){
                    //
                    SobotToastUtil.showCustomToast(getBaseContext(),SobotResourceUtils.getResString(getBaseContext(),"sobot_delete_success_string"));
                    setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                    finish();
                }
            }

            @Override
            public void onFailure(Exception e, String s) {
                dismissProgressDialog();
            }
        });
    }
    //?????????????????????
    private void positionNowWorkOrderID() {
        if (ticketNum >= 2 && items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (detailInfoId.equals(items.get(i).getTicketId())) {
                    position = i;
                    break;
                }
            }
            postionInItems();
        } else {
            tvBefore.setEnabled(false);
            tvNext.setEnabled(false);
        }
    }
    private void postionInItems() {

        if (position == 0) {
            tvBefore.setEnabled(false);
            tvNext.setEnabled(true);
        } else if (position == items.size() - 1) {
            if (isLoadMore) {
                tvBefore.setEnabled(true);
                tvNext.setEnabled(true);
            } else {
                tvBefore.setEnabled(false);
                tvNext.setEnabled(true);
            }
        } else {
            tvBefore.setEnabled(true);
            tvNext.setEnabled(true);
        }

    }
    //?????? ?????????
    private void registBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_RESULT);
        filter.addAction(SobotConstantUtils.SOBOT_BROADCAST_TICKET_NOMORE);
        registerReceiver(broadcast, filter);
    }

    private BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SobotConstantUtils.SOBOT_BROADCAST_TICKET_LOADMORE_RESULT)) {
                ArrayList<SobotWOSecondItemModel> itemsitems = (ArrayList<SobotWOSecondItemModel>) intent.getSerializableExtra("items");
                isLoadMore = intent.getBooleanExtra("isLoadMore", false);
                SobotLogUtils.d("======itemsitems===="+itemsitems.size());
                if (itemsitems == null || itemsitems.size() == 0) {
                    tvNext.setEnabled(false);
                } else {
                    items.addAll(itemsitems);
                    ticketNum = items.size();
                }
            }
            if (intent.getAction().equals(SobotConstantUtils.SOBOT_BROADCAST_TICKET_NOMORE)) {
                tvNext.setEnabled(false);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}