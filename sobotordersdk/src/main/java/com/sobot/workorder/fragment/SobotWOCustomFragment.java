package com.sobot.workorder.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sobot.common.login.SobotLoginTools;
import com.sobot.common.login.model.SobotServiceInfoModel;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.livedatabus.SobotLiveEventBus;
import com.sobot.widget.loading.SobotLoadingLayout;
import com.sobot.widget.refresh.layout.SobotRefreshLayout;
import com.sobot.widget.refresh.layout.api.RefreshLayout;
import com.sobot.widget.refresh.layout.footer.ClassicsFooter;
import com.sobot.widget.refresh.layout.header.ClassicsHeader;
import com.sobot.widget.refresh.layout.listener.OnLoadMoreListener;
import com.sobot.widget.refresh.layout.listener.OnRefreshListener;
import com.sobot.widget.ui.toast.SobotToastUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.CallSearchCustomAdapter;
import com.sobot.workorder.base.SobotWOBaseFragment;
import com.sobot.workorder.utils.CustomFieldsUtils;
import com.sobot.workorder.utils.SobotWoLiveDataBusKey;
import com.sobot.workorder.weight.dialog.CostomerDetailDialog;
import com.sobot.workorder.weight.dialog.DialogItemOnClick;
import com.sobot.workorder.weight.dialog.SobotMoreMenuPop;
import com.sobot.workorder.weight.popwindow.SobotActionItem;
import com.sobot.workorder.weight.swipeItem.SwipeItemDeleteLayout;
import com.sobot.workorderlibrary.api.model.EnterPriseModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfig;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoList;
import com.sobot.workorderlibrary.api.model.SobotCusFieldEntity;
import com.sobot.workorderlibrary.api.model.SobotCustomerModel;
import com.sobot.workorderlibrary.api.model.SobotEncryptTel;
import com.sobot.workorderlibrary.api.model.placename.CountryModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 客户详情
 */
public class SobotWOCustomFragment extends SobotWOBaseFragment implements View.OnClickListener {
    private View mRootView;
    private View ll_customer;
    private LinearLayout ll_search_custom;
    private SobotRefreshLayout refreshLayout;
    private SobotLoadingLayout loading_layout;
    private LinearLayout ll_search;
    private TextView tv_custom_name;
    //昵称
    private TextView tv_nick_value;
    private EditText ed_nick_value;
    private TextView tv_nick_must;
    //真实姓名
    private TextView tv_realname_value;
    private EditText ed_realname_value;
    //性别
    private LinearLayout ll_sex;
    private TextView tv_sex_value;
    private TextView tv_phone_value;
    private EditText ed_phone_value;
    private ImageView iv_copy;
    private ImageView iv_email_copy;
    private ImageView iv_phone_add;
    private TextView tv_email_value;
    private EditText ed_email_value;
    private ImageView iv_email_add;

    //城市
//        private SelectCityDialog cityDialog;
    private TextView tv_company_value;//客户公司名称
    private LinearLayout ll_isvip;//是否是vip
    private TextView tv_is_vip_value;//是否是vip
    private TextView tv_source_value;//来源

    //搜索客户
    private TextView tv_search_type;
    private EditText et_search_input;
    //输入后0.5s去搜索
    private long inputTime = 0;
    private ImageView iv_clear, iv_search;
    private RecyclerView rv_customer;
    private CallSearchCustomAdapter customAdapter;
    private List<SobotCustomerModel> customerList;
    private TextView btn_association_cancel;
    private TextView btn_association_save;
    private LinearLayout ll_association_menu;
    private int searchType = 1;//1客户昵称 nick，2真实姓名 uname，3公司 enterpriseName ，4电话 tel，5邮箱 email，6对接ID partnerld ,7 客户状态crmStatus
    private int pageIndex = 1;
    //是否还有更多数据
    private boolean isHasMoreData = false;

    private LinearLayout ll_cusfield;//显示客户自定义字段的布局

    private LinearLayout ll_vip_level;
    private TextView tv_vip_level_value;//客户VIP等级
    private List<CountryModel> countryList;//所有国家
    private int countryIndex = -1;

    private String tels = "";//所有的手机号码
    private List<SobotEncryptTel> encryptTels;//返回的加密号码
    private String emails = "";//所有的邮箱
    LinearLayout ll_add_email;//用来添加用户邮箱的布局
    LinearLayout ll_add_phone;//用来添加用户电话的布局

    private List<SobotCusFieldConfig> cusFieldConfigList;//所有自定义字段
    private List<SobotCusFieldDataInfoList> vipList;//vip的选项

    private SobotCustomerModel customerModel;
    private String provinceName = "";
    private String cityName = "";
    private String areaName = "";
    private String provinceId = "";
    private String cityId = "";
    private String areaId = "";
    //国家名
    private String countryName = "";
    //国家id
    private String countryId = "";
    private String tID = "";
    private List<SwipeItemDeleteLayout> listEmailView = new ArrayList<>();
    private List<SwipeItemDeleteLayout> listPhoneView = new ArrayList<>();
    private String customerId;
    private String selectCustomerId = "";
    private List<String> sexList;
    private List<String> isVipList;

    //座席信息
    private SobotServiceInfoModel serviceVo;
    private PopupWindow popupWindow;
    private List<SobotActionItem> searchItems;

    private int userInfoType = -1;//客户资料点击查看类型 0：新建客户 1客户资料 2关联客户,3编辑，4未关联客户,5关联的客户已失效
    private int olduserInfoType = -1;//客户资料点击查看类型 0：新建客户 1客户资料 2关联客户,3编辑，4未关联客户,5关联的客户已失效
    public static int TYPE_ADD = 0;
    public static int TYPE_EDIT = 3;
    public static int TYPE_SHOW = 1;
    public static int TYPE_RELA = 2;
    public static int TYPE_NO_RELA = 4;
    public static int TYPE_EXPIRE = 5;

    private View ll_no_data;
    private ImageView iv_no_data_icon;
    private TextView tv_no_data_title, tv_no_data_desc, tv_relaty_custom;

    //    private SelectCompanyDialog companyDialog;
    private EnterPriseModel selectCompany;
    private TextView tv_nick, tv_realname, tv_sex, tv_source, tv_phone, tv_email, tv_company, tv_is_vip, tv_vip_level;
    private View v_nick, v_realname, v_sex, v_source, v_phone, v_email, v_city, v_company, v_is_vip, v_vip_level;

    private LinearLayout ll_search_no_data;
    private TextView tv_association;//跳转关联

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.sobot_fragment_custom_info, container, false);
        cusFieldConfigList = new ArrayList<>();
        userInfoType = TYPE_NO_RELA;
        initView();
        return mRootView;
    }

    public void setCustomerId(String customerId, String tId) {
        this.customerId = customerId;
        this.tID = tId;
        if (!SobotStringUtils.isEmpty(customerId)) {
            //请求客户
            requestCustomById(false);
        } else {
            //没有关联客户
            userInfoType = TYPE_NO_RELA;
            showView();
        }
    }

    private void initView() {
        tv_nick = mRootView.findViewById(R.id.tv_nick);
        tv_association = mRootView.findViewById(R.id.tv_association);
        tv_association.setOnClickListener(this);
        tv_realname = mRootView.findViewById(R.id.tv_realname);
        tv_sex = mRootView.findViewById(R.id.tv_sex);
        tv_source = mRootView.findViewById(R.id.tv_source);
        tv_phone = mRootView.findViewById(R.id.tv_phone);
        tv_email = mRootView.findViewById(R.id.tv_email);
        tv_company = mRootView.findViewById(R.id.tv_company);
        tv_is_vip = mRootView.findViewById(R.id.tv_is_vip);
        tv_vip_level = mRootView.findViewById(R.id.tv_vip_level);
        refreshLayout = mRootView.findViewById(R.id.sobot_srl_custom);
        loading_layout = mRootView.findViewById(R.id.sobot_loading_layout);

        v_nick = mRootView.findViewById(R.id.v_nick);
        v_realname = mRootView.findViewById(R.id.v_realname);
        v_sex = mRootView.findViewById(R.id.v_sex);
        v_source = mRootView.findViewById(R.id.v_source);
        v_phone = mRootView.findViewById(R.id.v_phone);
        v_email = mRootView.findViewById(R.id.v_email);
        v_city = mRootView.findViewById(R.id.v_city);
        v_company = mRootView.findViewById(R.id.v_company);
        v_is_vip = mRootView.findViewById(R.id.v_is_vip);
        v_vip_level = mRootView.findViewById(R.id.v_vip_level);

        ll_no_data = mRootView.findViewById(R.id.ll_no_data);
        iv_no_data_icon = mRootView.findViewById(R.id.iv_no_data_icon);
        tv_no_data_title = mRootView.findViewById(R.id.tv_no_data_title);
        tv_no_data_desc = mRootView.findViewById(R.id.tv_no_data_desc);
        tv_relaty_custom = mRootView.findViewById(R.id.tv_relaty_custom);
        tv_relaty_custom.setOnClickListener(this);

        ll_search_custom = mRootView.findViewById(R.id.ll_search_custom);
        ll_search = mRootView.findViewById(R.id.ll_search);
        rv_customer = mRootView.findViewById(R.id.rv_customer);
        ll_search_no_data = mRootView.findViewById(R.id.ll_search_no_data);
        iv_clear = mRootView.findViewById(R.id.iv_clear);
        tv_search_type = mRootView.findViewById(R.id.tv_search_type);
        et_search_input = mRootView.findViewById(R.id.et_search_input);
        iv_search = mRootView.findViewById(R.id.iv_search);
        btn_association_cancel = mRootView.findViewById(R.id.btn_association_cancel);
        btn_association_save = mRootView.findViewById(R.id.btn_association_save);
        ll_association_menu = mRootView.findViewById(R.id.ll_association_menu);
        btn_association_cancel.setOnClickListener(this);
        btn_association_save.setOnClickListener(this);
        iv_clear.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        tv_search_type.setOnClickListener(this);
        rv_customer.setLayoutManager(new LinearLayoutManager(getContext()));
        customerList = new ArrayList<>();
        customAdapter = new CallSearchCustomAdapter(getSobotActivity(), customerList, new CallSearchCustomAdapter.OnItemClick() {
            @Override
            public void onItemClick(SobotCustomerModel user) {
                selectCustomerId = user.getId();
            }

            @Override
            public void onItemDetail(SobotCustomerModel user) {
                showCustomerDetail(user);
            }
        });
        rv_customer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    et_search_input.clearFocus();
                }
                return false;
            }
        });
        rv_customer.setAdapter(customAdapter);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getSobotActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getSobotActivity()));
        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull @NotNull RefreshLayout refreshLayout) {
                if (isHasMoreData) {
                    searchCustomByType(pageIndex + 1);
                } else {
                    refreshLayout.setNoMoreData(true);
                }
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                pageIndex = 1;
                searchCustomByType(pageIndex);
            }
        });
        refreshLayout.setNoMoreData(true);
        loading_layout.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageIndex = 1;
                searchCustomByType(pageIndex);
            }
        });

        et_search_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    CustomFieldsUtils.showKeyboard(getContext(), et_search_input);
                    ll_search.setBackgroundResource(R.drawable.sobot_bg_search_f);
                } else {
                    CustomFieldsUtils.hideKeyboard(getContext(), v);
                    ll_search.setBackgroundResource(R.drawable.sobot_bg_search);
                }
            }
        });
        et_search_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    //搜索
                    searchCustomByType(1);
                    return true;
                }
                return false;
            }
        });
        et_search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int inputCount = s.length();
                if (inputCount > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                } else {
                    iv_clear.setVisibility(View.GONE);

                }
                inputTime = System.currentTimeMillis();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchCustomByType(1);
                    }
                }, 500);
            }
        });
        ll_customer = mRootView.findViewById(R.id.ll_customer);
        ll_search_custom.setVisibility(View.GONE);
        tv_custom_name = (TextView) mRootView.findViewById(R.id.custom_name);
        tv_nick_value = (TextView) mRootView.findViewById(R.id.work_order_user_nick);
        tv_realname_value = (TextView) mRootView.findViewById(R.id.work_order_user_realname);
        tv_sex_value = (TextView) mRootView.findViewById(R.id.work_order_user_sex);
        ll_sex = mRootView.findViewById(R.id.ll_sex);
        tv_source_value = (TextView) mRootView.findViewById(R.id.work_order_user_source);
        tv_email_value = (TextView) mRootView.findViewById(R.id.work_order_user_email);
        tv_phone_value = (TextView) mRootView.findViewById(R.id.work_order_user_tel);
        tv_company_value = (TextView) mRootView.findViewById(R.id.work_order_user_company);
        ll_cusfield = (LinearLayout) mRootView.findViewById(R.id.work_order_user_cusfield);

        ll_vip_level = mRootView.findViewById(R.id.ll_vip_level);
        tv_vip_level_value = mRootView.findViewById(R.id.tv_vip_level_value);

        ed_nick_value = mRootView.findViewById(R.id.ed_nick_value);
        tv_nick_must = mRootView.findViewById(R.id.tv_nick_must);
        ed_realname_value = mRootView.findViewById(R.id.ed_realname_value);
        ed_phone_value = mRootView.findViewById(R.id.ed_phone_value);
        iv_copy = mRootView.findViewById(R.id.iv_copy);
        iv_email_copy = mRootView.findViewById(R.id.iv_email_copy);
        iv_phone_add = mRootView.findViewById(R.id.iv_phone_add);
        ed_email_value = mRootView.findViewById(R.id.ed_email_value);
        iv_email_add = mRootView.findViewById(R.id.iv_email_add);
        ll_isvip = mRootView.findViewById(R.id.ll_isvip);
        tv_is_vip_value = mRootView.findViewById(R.id.tv_is_vip_value);
        ll_add_email = mRootView.findViewById(R.id.ll_add_email);
        ll_add_phone = mRootView.findViewById(R.id.ll_add_phone);
        iv_phone_add.setOnClickListener(this);
        iv_email_add.setOnClickListener(this);

        //1客户昵称 nick，2真实姓名 uname，3公司 enterpriseName ，4电话 tel，5邮箱 email，6对接ID partnerld
        SobotActionItem sobot_call_nike = new SobotActionItem(getSobotActivity().getResources().getString(R.string.sobot_custom_nickname), false, 1);
        SobotActionItem sobot_call_realName = new SobotActionItem(getSobotActivity().getResources().getString(R.string.sobot_custom_name), false, 2);
        SobotActionItem sobot_call_company = new SobotActionItem(getSobotActivity().getResources().getString(R.string.sobot_custom_company), false, 3);
        SobotActionItem sobot_call_search_number = new SobotActionItem(getSobotActivity().getResources().getString(R.string.sobot_phone_number), false, 4);
        SobotActionItem sobot_call_search_email = new SobotActionItem(getSobotActivity().getResources().getString(R.string.sobot_email_string), false, 5);
        SobotActionItem sobot_duijie_id = new SobotActionItem(getSobotActivity().getResources().getString(R.string.sobot_docking_id), false, 6);
        searchItems = new ArrayList<>();
        searchItems.add(sobot_call_nike);
        searchItems.add(sobot_call_realName);
        searchItems.add(sobot_call_company);
        searchItems.add(sobot_call_search_number);
        searchItems.add(sobot_call_search_email);
        searchItems.add(sobot_duijie_id);
        iv_copy.setOnClickListener(this);
        iv_email_copy.setOnClickListener(this);
        requestCusField();
        initDate();
    }


    private void initDate() {
        serviceVo = SobotLoginTools.getInstance().getServiceInfo();
        if (serviceVo == null) {
            getSobotActivity().finish();
        }
        sexList = new ArrayList<>();
        isVipList = new ArrayList<>();
        countryList = new ArrayList<>();
        sexList.add(getSobotActivity().getResources().getString(R.string.sobot_custom_male));
        sexList.add(getSobotActivity().getResources().getString(R.string.sobot_custom_female));
        isVipList.add(getSobotActivity().getResources().getString(R.string.sobot_common_user));
        isVipList.add(getSobotActivity().getResources().getString(R.string.sobot_vip_customer));

        showView();
    }

    /**
     * 判断显示的view:新建、编辑、查看、关联
     */
    private void showView() {
        if (userInfoType == TYPE_SHOW) {
            ll_no_data.setVisibility(View.GONE);
            //查看用户
            showCustomerView();
            updateUI(false);
        } else if (userInfoType == TYPE_RELA) {
            ll_no_data.setVisibility(View.GONE);
            //关联用户
            showRelevanceView();

            searchCustomByType(1);
        } else if (userInfoType == TYPE_NO_RELA) {
            //未关联客户
            ll_no_data.setVisibility(View.VISIBLE);
            iv_no_data_icon.setImageResource(R.drawable.sobot_custom_no_data);
            tv_no_data_title.setText(R.string.sobot_custom_no_data);
            tv_no_data_desc.setVisibility(View.GONE);
        } else if (userInfoType == TYPE_EXPIRE) {
            //关联客户已失效
            ll_no_data.setVisibility(View.VISIBLE);
            iv_no_data_icon.setImageResource(R.drawable.sobot_custom_expire);
            tv_no_data_title.setText(R.string.sobot_custom_expired);
            tv_no_data_desc.setVisibility(View.VISIBLE);
            tv_no_data_desc.setText(R.string.sobot_custom_expired_reason);
        }
    }

    private void updateUI(boolean isEdit) {
        if (!isEdit) {
            //展示客户信息
            tv_vip_level.setTypeface(null, Typeface.NORMAL);
            ll_add_email.setVisibility(View.GONE);
            ll_add_phone.setVisibility(View.GONE);

            if (cusFieldConfigList != null && cusFieldConfigList.size() > 0) {
                CustomFieldsUtils.showCusFields(getSobotActivity(), cusFieldConfigList, ll_cusfield);
            }
            if (selectCompany != null && !TextUtils.isEmpty(selectCompany.getEnterpriseName())) {
                tv_company_value.setText(selectCompany.getEnterpriseName());
            } else {
                tv_company_value.setText("--");
            }
            tv_nick_must.setVisibility(View.GONE);
            if (tv_sex_value.getTag() == null) {
                tv_sex_value.setText("--");
            }
            if (null != customerModel && "1".equals(customerModel.getIsVip())) {
                tv_is_vip_value.setText(R.string.sobot_vip_customer);
                if (SobotStringUtils.isEmpty(tv_vip_level_value.getText().toString())) {
                    ll_vip_level.setVisibility(View.GONE);
                } else {
                    ll_vip_level.setVisibility(View.VISIBLE);
                }
            } else {
                tv_is_vip_value.setText(R.string.sobot_common_user);
            }
            tv_custom_name.setVisibility(View.VISIBLE);
            ed_nick_value.setVisibility(View.GONE);
            tv_nick_value.setVisibility(View.VISIBLE);
            ed_realname_value.setVisibility(View.GONE);
            tv_realname_value.setVisibility(View.VISIBLE);
            tv_sex_value.setCompoundDrawables(null, null, null, null);
            tv_phone_value.setVisibility(View.VISIBLE);
            ed_phone_value.setVisibility(View.GONE);
            ll_add_phone.removeAllViews();
            String[] tmpUserPhone = null;
            if (!TextUtils.isEmpty(tels)) {
                tmpUserPhone = tels.split(";");
            }
            if (tmpUserPhone != null && tmpUserPhone.length > 0) {
                ll_add_phone.setVisibility(View.VISIBLE);
                for (int i = 1; i < tmpUserPhone.length; i++) {
                    addUserPhoneView(tmpUserPhone[i]);
                }
            }
            iv_copy.setVisibility(View.VISIBLE);
            iv_email_copy.setVisibility(View.VISIBLE);
            iv_phone_add.setVisibility(View.GONE);
            tv_email_value.setVisibility(View.VISIBLE);
            ed_email_value.setVisibility(View.GONE);
            ll_add_email.removeAllViews();
            String[] tmpUserEmail = null;
            if (!TextUtils.isEmpty(emails)) {
                tmpUserEmail = emails.split(";");
            } else {
                iv_email_copy.setVisibility(View.GONE);
            }
            if (tmpUserEmail != null && tmpUserEmail.length > 1) {
                ll_add_email.setVisibility(View.VISIBLE);
                for (int i = 1; i < tmpUserEmail.length; i++) {
                    addUserEmailView(tmpUserEmail[i]);
                }
            }
            iv_email_add.setVisibility(View.GONE);
            tv_company_value.setCompoundDrawables(null, null, null, null);
            tv_source_value.setCompoundDrawables(null, null, null, null);
            tv_is_vip_value.setCompoundDrawables(null, null, null, null);
            tv_sex_value.setOnClickListener(null);
            tv_company_value.setOnClickListener(null);
            tv_is_vip_value.setOnClickListener(null);
            tv_vip_level_value.setOnClickListener(null);
            tv_nick.setTypeface(null, Typeface.NORMAL);
            tv_realname.setTypeface(null, Typeface.NORMAL);
            tv_sex.setTypeface(null, Typeface.NORMAL);
            tv_source.setTypeface(null, Typeface.NORMAL);
            tv_phone.setTypeface(null, Typeface.NORMAL);
            tv_email.setTypeface(null, Typeface.NORMAL);
            tv_company.setTypeface(null, Typeface.NORMAL);
            tv_is_vip.setTypeface(null, Typeface.NORMAL);
            tv_vip_level.setTypeface(null, Typeface.NORMAL);
            tv_nick.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_realname.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_sex.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_source.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_phone.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_email.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_company.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_is_vip.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            tv_vip_level.setTextColor(getSobotActivity().getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            v_nick.setVisibility(View.GONE);
            v_realname.setVisibility(View.GONE);
            v_sex.setVisibility(View.GONE);
            v_source.setVisibility(View.GONE);
            v_phone.setVisibility(View.GONE);
            v_email.setVisibility(View.GONE);
            v_city.setVisibility(View.GONE);
            v_company.setVisibility(View.GONE);
            v_is_vip.setVisibility(View.GONE);
            v_vip_level.setVisibility(View.GONE);
        } else {
            //编辑客户信息
            if (userInfoType == TYPE_ADD) {
                //新建用户
//                tv_edit_custom.setText(getResources().getString(R.string.call_str_new_customer));
//                tv_source_value.setText(R.string.sobot_call_center);
            } else if (userInfoType == TYPE_EDIT) {
//                btn_cancel.setVisibility(View.VISIBLE);
//                tv_edit_custom.setText(getResources().getString(R.string.sobot_edit_user_string));
            }
            tv_nick_must.setVisibility(View.VISIBLE);
//            tv_edit_custom.setVisibility(View.VISIBLE);
            tv_custom_name.setVisibility(View.GONE);
            ed_nick_value.setVisibility(View.VISIBLE);
            tv_nick_value.setVisibility(View.GONE);
            ed_realname_value.setVisibility(View.VISIBLE);
            tv_realname_value.setVisibility(View.GONE);
            Drawable drawable = getResources().getDrawable(R.drawable.sobot_item_arrow);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tv_phone_value.setVisibility(View.GONE);
            ed_phone_value.setVisibility(View.VISIBLE);
            ll_add_phone.removeAllViews();
            listPhoneView.clear();
            String[] tmpUserPhone = null;
            if (!TextUtils.isEmpty(tels)) {
                tmpUserPhone = tels.split(";");
            }
            if (tmpUserPhone != null && tmpUserPhone.length > 0) {
                for (int i = 1; i < tmpUserPhone.length; i++) {
                    addUserPhone(tmpUserPhone[i]);
                }
            }
            iv_copy.setVisibility(View.GONE);
            iv_email_copy.setVisibility(View.GONE);
            iv_phone_add.setVisibility(View.VISIBLE);
            tv_email_value.setVisibility(View.GONE);
            ed_email_value.setVisibility(View.VISIBLE);
            ll_add_email.removeAllViews();
            listEmailView.clear();
            String[] tmpUserEmail = null;
            if (!TextUtils.isEmpty(emails)) {
                tmpUserEmail = emails.split(";");
            }
            if (tmpUserEmail != null && tmpUserEmail.length > 1) {
                for (int i = 1; i < tmpUserEmail.length; i++) {
                    addUserEmail(tmpUserEmail[i]);
                }
            }
            if (selectCompany != null && !TextUtils.isEmpty(selectCompany.getEnterpriseName())) {
                tv_company_value.setText(selectCompany.getEnterpriseName());
            } else {
                tv_company_value.setText("");
            }
            if (tv_sex_value.getTag() == null) {
                tv_sex_value.setText("");
            }
            if (null != customerModel && "1".equals(customerModel.getIsVip())) {
                tv_is_vip_value.setText(R.string.sobot_vip_customer);
                if (vipList != null && vipList.size() > 0) {
                    ll_vip_level.setVisibility(View.VISIBLE);
                } else {
                    ll_vip_level.setVisibility(View.GONE);
                }
            } else {
                tv_is_vip_value.setText(R.string.sobot_common_user);
            }
            iv_email_add.setVisibility(View.VISIBLE);
            tv_sex_value.setCompoundDrawables(null, null, drawable, null);
            tv_company_value.setCompoundDrawables(null, null, drawable, null);
            tv_is_vip_value.setCompoundDrawables(null, null, drawable, null);
            tv_vip_level_value.setCompoundDrawables(null, null, drawable, null);
            tv_sex_value.setOnClickListener(this);
            tv_company_value.setOnClickListener(this);
            tv_is_vip_value.setOnClickListener(this);
            tv_vip_level_value.setOnClickListener(this);
            if (cusFieldConfigList != null && cusFieldConfigList.size() > 0) {
                CustomFieldsUtils.addWorkOrderUserCusFields(getSobotActivity(), cusFieldConfigList, ll_cusfield);
            }
            ll_add_email.setVisibility(View.VISIBLE);
            ll_add_phone.setVisibility(View.VISIBLE);
            tv_nick.setTypeface(null, Typeface.BOLD);
            tv_realname.setTypeface(null, Typeface.BOLD);
            tv_sex.setTypeface(null, Typeface.BOLD);
            tv_source.setTypeface(null, Typeface.BOLD);
            tv_phone.setTypeface(null, Typeface.BOLD);
            tv_email.setTypeface(null, Typeface.BOLD);
            tv_company.setTypeface(null, Typeface.BOLD);
            tv_is_vip.setTypeface(null, Typeface.BOLD);
            tv_vip_level.setTypeface(null, Typeface.BOLD);
            tv_nick.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_realname.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_sex.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_source.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_phone.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_email.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_company.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_is_vip.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            tv_vip_level.setTextColor(getContext().getResources().getColor(R.color.sobot_wo_new_wenzi_gray1));
            v_nick.setVisibility(View.VISIBLE);
            v_realname.setVisibility(View.VISIBLE);
            v_sex.setVisibility(View.VISIBLE);
            v_source.setVisibility(View.VISIBLE);
            v_phone.setVisibility(View.VISIBLE);
            v_email.setVisibility(View.VISIBLE);
            v_city.setVisibility(View.VISIBLE);
            v_company.setVisibility(View.VISIBLE);
            v_is_vip.setVisibility(View.VISIBLE);
            v_vip_level.setVisibility(View.VISIBLE);
        }

    }

    //显示关联用户
    private void showRelevanceView() {
        tv_custom_name.setVisibility(View.GONE);
        ll_customer.setVisibility(View.GONE);
        ll_search_custom.setVisibility(View.VISIBLE);
        btn_association_save.setVisibility(View.VISIBLE);
    }

    //显示查看用户
    private void showCustomerView() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                tv_custom_name.setVisibility(View.VISIBLE);
                ll_customer.setVisibility(View.VISIBLE);
                ll_search_custom.setVisibility(View.GONE);
            }
        });

    }

    private void requestAssociationCustom(final boolean isUpdate) {
        zhiChiApi.relationUser(getSobotActivity(), selectCustomerId, tID, new SobotResultCallBack() {
            @Override
            public void onSuccess(Object o) {
                SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_relateve_success));
                if (isUpdate) {
                    showCustomerView();
                    customerId = selectCustomerId;
                    requestCustomById(isUpdate);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (!TextUtils.isEmpty(des)) {
                    Toast.makeText(getContext(), des, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initCustomer() {
        if (customerModel != null) {
            tv_custom_name.setText(getText(customerModel.getNick()));
            tv_nick_value.setText(getText(customerModel.getNick()));
            ed_nick_value.setText(getText(customerModel.getNick()));
            tv_realname_value.setText(getText(customerModel.getUname()));
            ed_realname_value.setText(customerModel.getUname());
            tv_source_value.setText(setSourceText(customerModel.getSource()));
            tv_source_value.setTag(customerModel.getSource());
            //性别
            if (1 == customerModel.getSex()) {
                tv_sex_value.setText(R.string.sobot_custom_male);
                tv_sex_value.setTag(customerModel.getSex());
            } else if (2 == customerModel.getSex()) {
                tv_sex_value.setText(R.string.sobot_custom_female);
                tv_sex_value.setTag(customerModel.getSex());
            } else {
                tv_sex_value.setText("--");
            }
            String[] tmpUserPhone = null;
            if (!TextUtils.isEmpty(customerModel.getTel())) {
                tels = customerModel.getTel();
                tmpUserPhone = customerModel.getTel().split(";");
                if (tmpUserPhone != null && tmpUserPhone.length > 0) {
                    ll_add_phone.removeAllViews();
                    tv_phone_value.setText(tmpUserPhone[0]);
                    if (tmpUserPhone.length > 1) {
                        for (int i = 1; i < tmpUserPhone.length; i++) {
                            addUserPhoneView(tmpUserPhone[i]);
                        }
                    }
                } else {
                    tv_phone_value.setText("--");
                    iv_copy.setVisibility(View.GONE);
                }
            } else {
                tv_phone_value.setText("--");
                iv_copy.setVisibility(View.GONE);
            }

            if (customerModel.getTelList() != null && customerModel.getTelList().size() > 0) {
                encryptTels = customerModel.getTelList();
            }
            String[] tmpUserEmail = null;
            if (!TextUtils.isEmpty(customerModel.getEmail())) {
                emails = customerModel.getEmail();
                tmpUserEmail = customerModel.getEmail().split(";");
                if (tmpUserEmail != null && tmpUserEmail.length > 0) {
                    tv_email_value.setText(tmpUserEmail[0]);
                    iv_email_copy.setVisibility(View.VISIBLE);
                    ll_add_email.removeAllViews();
                    if (tmpUserEmail.length > 1) {
                        for (int i = 1; i < tmpUserEmail.length; i++) {
                            addUserEmailView(tmpUserEmail[i]);
                        }
                    }
                } else {
                    tv_email_value.setText("--");
                    iv_email_copy.setVisibility(View.GONE);
                }
            } else {
                tv_email_value.setText("--");
                iv_email_copy.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(customerModel.getEnterpriseId())) {
                if (selectCompany == null) {
                    selectCompany = new EnterPriseModel();
                }
                selectCompany.setEnterpriseId(customerModel.getEnterpriseId());
                selectCompany.setEnterpriseName(customerModel.getEnterpriseName());
            }
            tv_company_value.setText(getText(customerModel.getEnterpriseName()));

            //客户等级
            if ("1".equals(customerModel.getIsVip())) {
                tv_is_vip_value.setText(R.string.sobot_vip_customer);
                if (null != vipList && vipList.size() > 0) {
                    ll_vip_level.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(customerModel.getVipLevel())) {
                    try {
                        JSONObject vipLevel = new JSONObject(customerModel.getVipLevel());
                        String id = vipLevel.optString("id", "");
                        String value = vipLevel.optString("value", "");
                        if (!TextUtils.isEmpty(value)) {
                            tv_vip_level_value.setText(value);
                            tv_vip_level_value.setTag(id);
                            ll_vip_level.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else if (!TextUtils.isEmpty(customerModel.getVipLevelName())) {
                    tv_vip_level_value.setText(customerModel.getVipLevelName());
                    tv_vip_level_value.setTag(customerModel.getVipLevel());
                }
            } else {
                ll_vip_level.setVisibility(View.GONE);
                tv_is_vip_value.setText(R.string.sobot_common_user);
            }

            if (!TextUtils.isEmpty(customerModel.getCountryName())) {
                countryName = customerModel.getCountryName();
                countryId = customerModel.getCountryId();
            }

            if (!TextUtils.isEmpty(customerModel.getProviceName())) {
                provinceName = customerModel.getProviceName();
                provinceId = customerModel.getProviceId();
            }
            if (!TextUtils.isEmpty(customerModel.getCityName())) {
                cityName = customerModel.getCityName();
                cityId = customerModel.getCityId();
            }
            if (!TextUtils.isEmpty(customerModel.getAreaName())) {
                areaName = customerModel.getAreaName();
                areaId = customerModel.getAreaId();
            }
            //设置自定义字段的值
            if (customerModel != null && customerModel.getCusList() != null) {
                Map<String, String> map = customerModel.getCusList();
                for (int i = 0; i < cusFieldConfigList.size(); i++) {
                    for (int j = 0; j < map.size(); j++) {
                        String value = map.get(cusFieldConfigList.get(i).getFieldVariable());
                        if (!TextUtils.isEmpty(value)) {
                            if (cusFieldConfigList.get(i).getFieldType() < 6) {
                                cusFieldConfigList.get(i).setFieldValue(value);
                            } else {
                                try {
                                    JSONObject object = new JSONObject(value);
                                    String id = object.optString("id");
                                    String value1 = object.optString("value");
                                    cusFieldConfigList.get(i).setFieldValue(value1);
                                } catch (JSONException e) {
                                    cusFieldConfigList.get(i).setFieldValue(value);
//                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
            //设置城市，需要自己拼接
            String city = "--";
            if (!TextUtils.isEmpty(provinceName)) {
                city = provinceName;
            }
            if (!TextUtils.isEmpty(cityName)) {
                city = provinceName + "-" + cityName + "-" + areaName;
            }
            setCurFieldValue("cityName", city);
            //设置自定义字段
            CustomFieldsUtils.showCusFields(getSobotActivity(), cusFieldConfigList, ll_cusfield);
        } else {
            tv_custom_name.setText("--");
            tv_nick_value.setText("--");
            tv_sex_value.setText("--");
            tv_realname_value.setText("--");
            tv_source_value.setText(setSourceText("6"));
            tv_company_value.setText("--");
        }
    }

    /**
     * 设置自定义字段的值
     *
     * @param fieldName 字段的名字
     * @param value     字段对应的值
     */
    private void setCurFieldValue(String fieldName, String value) {
        if (cusFieldConfigList != null) {
            for (int i = 0; i < cusFieldConfigList.size(); i++) {
                if (cusFieldConfigList.get(i).getFieldVariable().equals(fieldName)) {
                    SobotCusFieldConfig config = cusFieldConfigList.get(i);
                    config.setFieldValue(value);
                }
            }
        }
    }

    /**
     * 设置textView的值，如果为空，显示“--”
     *
     * @param text 值
     * @return 显示的值
     */
    private String getText(String text) {
        return TextUtils.isEmpty(text) ? "--" : text;
    }

    /**
     * 查询客户--根据客户id
     */
    private void requestCustomById(final boolean isUpdate) {
        if (zhiChiApi == null || getSobotActivity() == null) return;
        zhiChiApi.getCustomById(getSobotActivity(), customerId, new SobotResultCallBack<SobotCustomerModel>() {
            @Override
            public void onSuccess(SobotCustomerModel sobotCallCustomerModel) {
                if (sobotCallCustomerModel != null && !TextUtils.isEmpty(sobotCallCustomerModel.getId())) {
                    //有数据，显示数据
                    userInfoType = TYPE_SHOW;
                    customerModel = sobotCallCustomerModel;
                    if (isUpdate) {
                        //变更、编辑客户信息后更细工单详情里边的客户信息
                        SobotLiveEventBus.get(SobotWoLiveDataBusKey.SOBOT_LIVEBUS_WO_UPDATE_CUSTOMER).post(sobotCallCustomerModel);
                    }
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            showView();
                            initCustomer();
                        }
                    });
                } else {
                    //无数据，显示已失效
                    userInfoType = TYPE_EXPIRE;
                    showView();
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                //提示
            }
        });
    }

    /**
     * 添加邮箱
     *
     * @param userEmail
     */
    private void addUserEmail(String userEmail) {
        if (listEmailView.size() >= 9) {
            return;
        }

        if (listEmailView != null && listEmailView.size() > 0) {
            if (TextUtils.isEmpty(listEmailView.get(listEmailView.size() - 1).getEdittext_add_user_info().getText())) {
                return;
            }
        }

        final SwipeItemDeleteLayout view = (SwipeItemDeleteLayout) View.inflate(getSobotActivity(), R.layout.sobot_item_swipe, null);
        listEmailView.add(view);
        view.getEdittext_add_user_info().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                view.close();
            }
        });
        view.getEdittext_add_user_info().setHint(getString(R.string.sobot_wo_input_hint));
        view.getTextView_delete().setText(getString(R.string.sobot_delete_string));
        view.getEdittext_add_user_info().requestFocus();
        if (!TextUtils.isEmpty(userEmail)) {
            view.getEdittext_add_user_info().setText(userEmail);
        }

        view.setOnMenuClickListener(new SwipeItemDeleteLayout.OnMenuClickListener() {
            @Override
            public void onDeleteClick(View v, SwipeItemDeleteLayout swipeItemDeleteLayout) {
                ll_add_email.removeView(swipeItemDeleteLayout);
                listEmailView.remove(swipeItemDeleteLayout);
                if (listEmailView.size() == 0) {
                }
            }
        });

        view.setOnMenuDelClickListener(new SwipeItemDeleteLayout.OnMenuDelClickListener() {
            @Override
            public void onImgDelClick(SwipeItemDeleteLayout swipeItemDeleteLayout) {
                if (listPhoneView != null && listPhoneView.size() > 0) {
                    for (int i = 0; i < listPhoneView.size(); i++) {
                        listPhoneView.get(i).close();
                    }
                }

                if (listEmailView != null && listEmailView.size() > 0) {
                    for (int i = 0; i < listEmailView.size(); i++) {
                        if (swipeItemDeleteLayout != listEmailView.get(i)) {
                            listEmailView.get(i).close();
                        }
                    }
                }
            }
        });

        ll_add_email.addView(view);
    }

    /**
     * 显示邮箱
     *
     * @param userEmail 所有的邮箱
     */
    private void addUserEmailView(final String userEmail) {
        if (TextUtils.isEmpty(userEmail)) {
            return;
        }
        ll_add_email.setVisibility(View.VISIBLE);
        View view = View.inflate(getSobotActivity(), R.layout.sobot_item_swipe_copy, null);
        TextView tv = view.findViewById(R.id.tv_add_user_info);
        tv.setText(userEmail);
        ImageView iv_copy = view.findViewById(R.id.iv_copy);
        iv_copy.setVisibility(View.VISIBLE);
        iv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyStr(userEmail);
            }
        });
        ll_add_email.addView(view);
    }

    public void addUserPhone(String userPhone) {
        if (listPhoneView.size() >= 9) {
            return;
        }

        if (listPhoneView != null && listPhoneView.size() > 0) {
            if (TextUtils.isEmpty(listPhoneView.get(listPhoneView.size() - 1).getEdittext_add_user_info().getText())) {
                return;
            }
        }

        final SwipeItemDeleteLayout view = (SwipeItemDeleteLayout) View.inflate(getSobotActivity(), R.layout.sobot_item_swipe, null);
        listPhoneView.add(view);
        view.getTextView_delete().setText(getString(R.string.sobot_delete_string));
        view.getEdittext_add_user_info().setHint(getString(R.string.sobot_wo_input_hint));
        view.getEdittext_add_user_info().setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        view.getEdittext_add_user_info().setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});//
        if (!TextUtils.isEmpty(userPhone)) {
            view.getEdittext_add_user_info().setTag(userPhone);
            view.getEdittext_add_user_info().setText(userPhone);
        }

        view.setOnMenuClickListener(new SwipeItemDeleteLayout.OnMenuClickListener() {
            @Override
            public void onDeleteClick(View v, SwipeItemDeleteLayout swipeItemDeleteLayout) {
                ll_add_phone.removeView(swipeItemDeleteLayout);
                listPhoneView.remove(swipeItemDeleteLayout);
                if (listPhoneView.size() == 0) {
                }
            }
        });

        view.setOnMenuDelClickListener(new SwipeItemDeleteLayout.OnMenuDelClickListener() {
            @Override
            public void onImgDelClick(SwipeItemDeleteLayout swipeItemDeleteLayout) {
                if (listEmailView != null && listEmailView.size() > 0) {
                    for (int i = 0; i < listEmailView.size(); i++) {
                        listEmailView.get(i).close();
                    }
                }

                if (listPhoneView != null && listPhoneView.size() > 0) {
                    for (int i = 0; i < listPhoneView.size(); i++) {
                        if (swipeItemDeleteLayout != listPhoneView.get(i)) {
                            listPhoneView.get(i).close();
                        }
                    }
                }
            }
        });
        ll_add_phone.addView(view);
    }

    /**
     * 显示手机号
     *
     * @param userPhone 客户手机号
     */
    public void addUserPhoneView(final String userPhone) {
        if (TextUtils.isEmpty(userPhone)) {
            return;
        }
        ll_add_phone.setVisibility(View.VISIBLE);
        View view = View.inflate(getSobotActivity(), R.layout.sobot_item_swipe_copy, null);
        TextView tv = view.findViewById(R.id.tv_add_user_info);
        tv.setText(userPhone);
        ImageView iv_copy = view.findViewById(R.id.iv_copy);
        iv_copy.setVisibility(View.VISIBLE);
        iv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyStr(userPhone);
            }
        });
        ll_add_phone.addView(view);
    }

    @Override
    public void onClick(View v) {
        if (v == iv_clear) {
            et_search_input.setText("");
        } else if (v == tv_relaty_custom || v == tv_association) {
            olduserInfoType = userInfoType;
            userInfoType = TYPE_RELA;
            showView();
        } else if (v == tv_search_type) {
            //pop
            for (int i = 0; i < searchItems.size(); i++) {
                if (searchItems.get(i).id == searchType) {
                    searchItems.get(i).setIsChecked(true);
                } else {
                    searchItems.get(i).setIsChecked(false);
                }
            }
            SobotMoreMenuPop pop = new SobotMoreMenuPop(getSobotActivity(), searchItems, false, true, new SobotMoreMenuPop.PopItemClick() {
                @Override
                public void onPopItemClick(SobotActionItem item, int index) {
                    popupWindow.dismiss();
                    searchType = searchItems.get(index).id;
                    tv_search_type.setText(searchItems.get(index).mTitle);
                    if (SobotStringUtils.isNoEmpty(et_search_input.getText().toString())) {
                        searchCustomByType(1);
                    }
                }
            });

            popupWindow = pop.getPopWindow();
            //弹窗出现外部为阴影
            WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
            attributes.alpha = 1f;
            getActivity().getWindow().setAttributes(attributes);
            //弹窗取消监听 取消之后恢复阴影
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
                    attributes.alpha = 1;
                    getActivity().getWindow().setAttributes(attributes);
                }
            });
            popupWindow.showAsDropDown(ll_search, 0, 0);
        } else if (v == iv_search) {
            //搜索
            iv_search.requestFocus();
            searchCustomByType(1);
        } else if (v == btn_association_cancel) {
            CustomFieldsUtils.hideKeyboard(getContext(), et_search_input);
            userInfoType = olduserInfoType;
            showView();
        } else if (v == btn_association_save) {
            if (!TextUtils.isEmpty(selectCustomerId)) {
                requestAssociationCustom(true);
            }
        } else if (tv_sex_value == v) {
//            男：1，女2
            int index = -1;
            /*if (tv_sex_value.getTag() != null) {
                int sex = (int) tv_sex_value.getTag();
                if (sex == 1) {
                    index = 0;
                } else if (sex == 2) {
                    index = 1;
                }
            }
            SelectCommonDialog dialog = new SelectCommonDialog(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_custom_sex), sexList, index, new DialogItemOnClick() {
                @Override
                public void selectItem(Object selectObj) {
                    int selectIndex = (int) selectObj;
                    if (selectIndex >= 0) {
                        tv_sex_value.setText(sexList.get(selectIndex));
                        tv_sex_value.setTag(selectIndex == 0 ? 1 : 2);
                    }
                }
            });
            dialog.show();*/
        } else if (tv_company_value == v) {
            /*companyDialog = new SelectCompanyDialog(getSobotActivity(), selectCompany, new DialogItemOnClick() {
                @Override
                public void selectItem(Object selectObj) {
                    companyDialog.dismiss();

                    if (null != selectObj) {
                        EnterPriseModel model = (EnterPriseModel) selectObj;
                        if(selectCompany==null){
                            selectCompany = new EnterPriseModel();
                        }
                        selectCompany.setEnterpriseName(model.getEnterpriseName());
                        selectCompany.setEnterpriseId(model.getId());
                        tv_company_value.setText(selectCompany.getEnterpriseName());
                    }
                }
            });
            companyDialog.show();*/
        } else if (tv_is_vip_value == v) {
            /*int index = -1;
            String isVip = tv_is_vip_value.getText().toString();
            if (!TextUtils.isEmpty(isVip)) {
                if (isVip.equals(getSobotActivity().getResources().getString(R.string.sobot_common_user))) {
                    index = 0;
                } else if (isVip.equals(getSobotActivity().getResources().getString(R.string.sobot_vip_customer))) {
                    index = 1;
                }
            }
            SelectCommonDialog dialog = new SelectCommonDialog(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_custom_grade), isVipList, index, new DialogItemOnClick() {
                @Override
                public void selectItem(Object selectObj) {
                    int selectIndex = (int) selectObj;
                    if (selectIndex >= 0) {
                        if (selectIndex == 0) {
                            tv_is_vip_value.setText(getSobotActivity().getResources().getString(R.string.sobot_common_user));
                            ll_vip_level.setVisibility(View.GONE);
                        } else if (selectIndex == 1) {
                            ll_vip_level.setVisibility(View.VISIBLE);
                            tv_is_vip_value.setText(getSobotActivity().getResources().getString(R.string.sobot_vip_customer));
                        }
                    }
                }
            });
            dialog.show();*/
        } else if (tv_vip_level_value == v) {
            //vip列表
            /*int index = -1;
            List<String> levelList = new ArrayList<>();
            if(vipList!=null && vipList.size()>0) {
                for (int i = 0; i < vipList.size(); i++) {
                    levelList.add(vipList.get(i).getDataName());
                    if (tv_vip_level_value.getText().toString().equals(vipList.get(i).getDataName())) {
                        index = i;
                    }
                }
                SelectCommonDialog dialog = new SelectCommonDialog(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_custom_grade), levelList, index, new DialogItemOnClick() {
                    @Override
                    public void selectItem(Object selectObj) {
                        int index = (int) selectObj;
                        if (index >= 0) {
                            CusFieldDataInfoList enterPriseModel = vipList.get(index);
                            tv_vip_level_value.setText(enterPriseModel.getDataName());
                            tv_vip_level_value.setTag(enterPriseModel.getDataValue());
                        }
                    }
                });
                dialog.show();
            }else{

            }*/

        } else if (iv_copy == v) {
            copyStr(tv_phone_value.getText().toString());
        } else if (iv_email_copy == v) {
            copyStr(tv_email_value.getText().toString());
        } else if (v == iv_email_add) {
            addUserEmail("");
        } else if (v == iv_phone_add) {
            addUserPhone("");
        }
    }

    /**
     * 显示选择国家的弹窗
     */
    private void showChoiceCountry() {
        /* List<String> countryStr = new ArrayList<>();
            for (int i = 0; i < countryList.size(); i++) {
                countryStr.add(countryList.get(i).getCountryName());
            }
            SelectCommonDialog dialog = new SelectCommonDialog(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_custom_country), countryStr, countryIndex, new DialogItemOnClick() {
                @Override
                public void selectItem(Object selectObj) {
                    countryIndex = (int) selectObj;
                    if (countryIndex >= 0) {
                        CountryModel countryModel = countryList.get(countryIndex);
                        countryId = countryModel.getCountryId();
                        countryName = countryModel.getCountryName();
                        tv_country_value.setText(countryModel.getCountryName());
                        tv_country_value.setTag(countryModel.getCountryId());
                        if(!TextUtils.isEmpty(provinceId)){
                            provinceId="";
                            provinceName="";
                            cityId="";
                            cityName="";
                            areaId="";
                            areaName="";
                            tv_city_value.setText("");
                        }
                    }
                }
            });
            dialog.show();*/
    }

    /**
     * 显示选择城市的弹窗
     */
    private void showChoiceCity() {
        //选择城市
            /*SobotServiceInfoModel serviceInfoModel = SobotLoginTools.getInstance().getServiceInfo();
            if (serviceInfoModel != null && serviceInfoModel.getRegion() == 1 && TextUtils.isEmpty(countryId)) {
                SobotToastUtil.showToast(getContext(), getSobotActivity().getResources().getString(R.string.sobot_costomer_country_hint));
            } else {
                SelectCityDialog cityDialog = new SelectCityDialog(getSobotActivity(), countryId, provinceId, provinceName, cityId, cityName, areaId, areaName, new SelectCityDialog.CityListener() {
                    @Override
                    public void selectCity(String pId, String pName, String cId, String cName, String aId, String aName) {
                        provinceId = pId;
                        provinceName = pName;
                        cityId = cId;
                        cityName = cName;
                        areaId = aId;
                        areaName = aName;
                        if(!TextUtils.isEmpty(cityName)){
                            tv_city_value.setText(provinceName + "-" + cityName + "-" + areaName);
                        }else{
                            tv_city_value.setText(provinceName);
                        }
                    }
                },serviceInfoModel.getRegion() == 1);
                cityDialog.show();
            }*/
    }

    private void copyStr(String copyString) {
        if (Build.VERSION.SDK_INT >= 11) {
            SobotLogUtils.i("API是大于11");
            android.content.ClipboardManager cmb = (android.content.ClipboardManager) getSobotActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyString);
            cmb.getText();
        } else {
            SobotLogUtils.i("API是小于11");
            android.text.ClipboardManager cmb = (android.text.ClipboardManager) getSobotActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setText(copyString);
            cmb.getText();
        }

        SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_ctrl_v_success));
    }

    /**
     * 保存客户
     */
//    private void saveCustom() {
//        if (customerModel == null) {
//            customerModel = new SobotCallCustomerModel();
//        }
//        String nick = ed_nick_value.getText().toString().trim();
//        String realName = ed_realname_value.getText().toString().trim();
//
//        if (TextUtils.isEmpty(nick)) {
//            SobotToastUtil.showCustomToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_call_customer_name_tip));
//            return;
//        } else {
//            tv_nick_value.setText(nick);
//            tv_custom_name.setText(nick);
//        }
//        if (TextUtils.isEmpty(realName)) {
//            tv_realname_value.setText("--");
//        } else {
//            tv_realname_value.setText(realName);
//        }
//        customerModel.setNick(nick);
//        customerModel.setUname(realName);
//        String sex = tv_sex_value.getText().toString().trim();
//        if (!TextUtils.isEmpty(sex) && null != tv_sex_value.getTag()) {
//            customerModel.setSex((Integer) tv_sex_value.getTag());
//        }
//        //默认是6 呼叫中心
//        String source = tv_source_value.getText().toString();
//        if (!TextUtils.isEmpty(source)) {
//            if (tv_source_value.getTag() != null) {
//                customerModel.setSource((String) tv_source_value.getTag());
//            }
//        } else {
//            customerModel.setSource("6");
//        }
//        //手机号
//        StringBuilder phoneSubmitSb = new StringBuilder();
//        String tel = ed_phone_value.getText().toString();
//        StringBuilder phoneSb = new StringBuilder(tel);
//        if (!TextUtils.isEmpty(tel)) {
//            if (tel.contains("*")) {
//                if (encryptTels != null && encryptTels.size() > 0) {
//                    for (int i = 0; i < encryptTels.size(); i++) {
//                        if (encryptTels.get(i).getTel().equals(tel)) {
//                            phoneSubmitSb.append(encryptTels.get(i).getEncrypt());
//                        }
//                    }
//                } else if (!TextUtils.isEmpty(encryptCustomerNumber)) {
//                    phoneSubmitSb.append(encryptCustomerNumber);
//                }
//            } else {
//                phoneSubmitSb.append(tel);
//            }
//        }
//        if (listPhoneView != null && listPhoneView.size() > 0) {
//            SwipeItemDeleteLayout view;
//            for (int i = 0; i < listPhoneView.size(); i++) {
//                view = listPhoneView.get(i);
//                String phoneValue = view.getEdittext_add_user_info().getText().toString();
//                if (!TextUtils.isEmpty(phoneValue)) {
//                    if (phoneSubmitSb.length() > 1) {
//                        phoneSubmitSb.append(";");
//                        phoneSb.append(";");
//                    }
//                    if (phoneValue.contains("*")) {
//                        if (encryptTels != null && encryptTels.size() > 0) {
//                            for (int j = 0; i < encryptTels.size(); i++) {
//                                if (encryptTels.get(j).getTel().equals(phoneValue)) {
//                                    phoneSubmitSb.append(encryptTels.get(j).getEncrypt());
//                                }
//                            }
//                        } else if (!TextUtils.isEmpty(encryptCustomerNumber)) {
//                            phoneSubmitSb.append(encryptCustomerNumber);
//                        }
//                    } else {
//                        phoneSubmitSb.append(phoneValue);
//                    }
//                    phoneSb.append(phoneValue);
//                }
//            }
//        }
//        tels = phoneSb.toString();
//        tv_phone_value.setText(getText(phoneSb.toString()));
//        customerModel.setTel(phoneSb.toString());
//        customerModel.setEncryptCustomerNumber(phoneSubmitSb.toString());
//
//        StringBuilder emailSb = new StringBuilder();
//        String email = ed_email_value.getText().toString();
//        if (!TextUtils.isEmpty(email)) {
//            emailSb.append(email);
//        }
//        if (listEmailView != null && listEmailView.size() > 0) {
//            SwipeItemDeleteLayout view;
//            for (int i = 0; i < listEmailView.size(); i++) {
//                view = listEmailView.get(i);
//                if (emailSb.length() > 1) {
//                    emailSb.append(";");
//                }
//                emailSb.append(view.getEdittext_add_user_info().getText());
//            }
//        }
//        emails = emailSb.toString();
//        customerModel.setEmail(emails);
//        tv_email_value.setText(getText(emails));
//
//        String country = tv_country_value.getText().toString();
//        if (!TextUtils.isEmpty(country)) {
//            customerModel.setCountryName(countryName);
//            customerModel.setCountryId((String) tv_country_value.getTag());
//        }
//        customerModel.setProviceName(provinceName);
//        customerModel.setProviceId(provinceId);
//        customerModel.setCityName(cityName);
//        customerModel.setCityId(cityId);
//        customerModel.setAreaName(areaName);
//        customerModel.setAreaId(areaId);
//        if (selectCompany != null && !TextUtils.isEmpty(selectCompany.getEnterpriseName())) {
//            customerModel.setEnterpriseName(selectCompany.getEnterpriseName());
//            customerModel.setEnterpriseId(selectCompany.getEnterpriseId());
//        }
//        String qq = ed_qq_value.getText().toString();
//        if (!TextUtils.isEmpty(qq)) {
//            tv_qq_value.setText(qq);
//            customerModel.setQq(qq);
//        } else {
//            tv_qq_value.setText("--");
//            customerModel.setQq("");
//        }
//        String wx = ed_wx_value.getText().toString();
//        if (!TextUtils.isEmpty(wx)) {
//            tv_wx_value.setText(wx);
//            customerModel.setWx(wx);
//        } else {
//            tv_wx_value.setText("--");
//            customerModel.setWx("");
//        }
//        String remark = ed_remark_value.getText().toString();
//        if (!TextUtils.isEmpty(remark)) {
//            tv_remark_value.setText(remark);
//            customerModel.setRemark(remark);
//        } else {
//            tv_remark_value.setText("--");
//            customerModel.setRemark("");
//        }
//        //
//        int isVip = 0;
//        if (!TextUtils.isEmpty(tv_is_vip_value.getText().toString()) && tv_is_vip_value.getText().toString().equals(getSobotActivity().getResources().getString(R.string.sobot_vip_customer))) {
//            isVip = 1;
//            if(tv_vip_level_value.getTag()!=null && !TextUtils.isEmpty((String) tv_vip_level_value.getTag())){
//                customerModel.setVipLevel((String) tv_vip_level_value.getTag());
//            }else{
//                SobotToastUtil.showCustomToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_customer_vip_level_hint));
//                return;
//            }
//        }
//        customerModel.setIsVip(isVip + "");
//        Map<String, String> fiels = setCustomerFieldValue();
//        if (!checkCusFieldRequired()) {
//            return;
//        }
//        if (userInfoType == TYPE_ADD) {
//            customerModel.setSource("6");
//            zhiChiApi.saveCustom(getSobotActivity(), customerModel, fiels, new SobotResultCallBack<SobotCallCustomerModel>() {
//                @Override
//                public void onSuccess(SobotCallCustomerModel sobotCallCustomerModel) {
//                    if (sobotCallCustomerModel != null) {
//                        //失败
//                        if (sobotCallCustomerModel.getTotalCount() > 0) {
//                            if (sobotCallCustomerModel.getType() == 1) {
//                                if (sobotCallCustomerModel.getData() != null && !TextUtils.isEmpty(sobotCallCustomerModel.getData().getNick())) {
//                                    SobotToastUtil.showToast(getSobotActivity(), String.format(getSobotActivity().getResources().getString(R.string.call_add_custom_repet_email_tip2), sobotCallCustomerModel.getData().getNick()));
//                                } else {
//                                    SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.call_add_custom_repet_email_tip));
//                                }
//                            } else if (sobotCallCustomerModel.getType() == 2) {
//                                if (sobotCallCustomerModel.getData() != null && !TextUtils.isEmpty(sobotCallCustomerModel.getData().getNick())) {
//                                    SobotToastUtil.showToast(getSobotActivity(), String.format(getSobotActivity().getResources().getString(R.string.call_add_custom_repet_phone_tip2), sobotCallCustomerModel.getData().getNick()));
//                                } else {
//                                    SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.call_add_custom_repet_phone_tip));
//                                }
//                            } else {
//                                SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_save_failed));
//                            }
//                        } else {
//                            userInfoType = TYPE_SHOW;
//                            getSobotActivity().setResult(SobotCallConstant.ACTIVITY_RESULT_DETAIL);
//                            SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_call_add_success));
//                            //成功
//                            updateUI(false);
//                            customerModel.setId(sobotCallCustomerModel.getId());
//                            //关联
//                            selectCustomerId = sobotCallCustomerModel.getId();
//                            if (!TextUtils.isEmpty(selectCustomerId)) {
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        requestAssociationCustom(false);
//                                    }
//                                }, 2000);
//                            }
//                        }
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Exception e, String des) {
//                    if (!TextUtils.isEmpty(des)) {
//                        SobotToastUtil.showCustomToast(getSobotActivity(), des);
//                    }
//                }
//            });
//
//        } else {
//            zhiChiApi.updateCustom(getSobotActivity(), customerModel, setCustomerFieldValue(), new SobotResultCallBack<SobotCallCustomerModel>() {
//                @Override
//                public void onSuccess(SobotCallCustomerModel sobotCallCustomerModel) {
//                    if (sobotCallCustomerModel.getTotalCount() > 0) {
//                        if (sobotCallCustomerModel.getType() == 1) {
//                            if (sobotCallCustomerModel.getData() != null && !TextUtils.isEmpty(sobotCallCustomerModel.getData().getNick())) {
//                                SobotToastUtil.showToast(getSobotActivity(), String.format(getSobotActivity().getResources().getString(R.string.call_add_custom_repet_email_tip2), sobotCallCustomerModel.getData().getNick()));
//                            } else {
//                                SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.call_add_custom_repet_email_tip));
//                            }
//                        } else if (sobotCallCustomerModel.getType() == 2) {
//                            if (sobotCallCustomerModel.getData() != null && !TextUtils.isEmpty(sobotCallCustomerModel.getData().getNick())) {
//                                SobotToastUtil.showToast(getSobotActivity(), String.format(getSobotActivity().getResources().getString(R.string.call_add_custom_repet_phone_tip2), sobotCallCustomerModel.getData().getNick()));
//                            } else {
//                                SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.call_add_custom_repet_phone_tip));
//                            }
//                        } else {
//                            SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_call_edit_fail));
//                        }
//                    } else {
//                        getSobotActivity().setResult(SobotCallConstant.ACTIVITY_RESULT_DETAIL);
//                        SobotToastUtil.showToast(getSobotActivity(), getSobotActivity().getResources().getString(R.string.sobot_call_edit_success));
//                        //成功
//                        updateUI(false);
//                    }
//                }
//
//                @Override
//                public void onFailure(Exception e, String des) {
//                    if (!TextUtils.isEmpty(des)) {
//                        SobotToastUtil.showCustomToast(getSobotActivity(), des);
//                    }
//                }
//            });
//        }
//    }

    /**
     * 客户来源转换成文字
     *
     * @param sourceType
     * @return
     */
    private String setSourceText(String sourceType) {
        if (TextUtils.isEmpty(sourceType)) {
            return "";
        }
        String sourceName;
        switch (sourceType) {
            case "1":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_source_wechat);
                break;
            case "2":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_source_app);
                break;
            case "3":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_source_weibo);
                break;
            case "4":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_source_mobile_web);
                break;
            case "5":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_source_rong);
                break;
            case "6":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_ticket_form_call_center);
                break;
            case "7":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_title_workorder_center_string);
                break;
            case "8":
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_ticket_form_customer_enter);
                break;
            default:
                sourceName = getSobotActivity().getResources().getString(R.string.sobot_source_pc);
                break;
        }
        return sourceName;
    }

    /**
     * 根据类型搜索--关联客户
     */
    protected void searchCustomByType(final int curPage) {
        if (System.currentTimeMillis() - inputTime < 500) {
            return;
        }
        final String inputSearchText = et_search_input.getText().toString();
        //搜索客户
        zhiChiApi.searchCustom(getSobotActivity(), curPage, searchType, inputSearchText, new SobotResultCallBack<List<SobotCustomerModel>>() {

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<SobotCustomerModel> sobotCallCustomerModels) {
                pageIndex = curPage;
                loading_layout.showContent();
                if (pageIndex == 1) {
                    customerList.clear();
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
                SobotLogUtils.i("data---:" + sobotCallCustomerModels.size());
                if (null != sobotCallCustomerModels && sobotCallCustomerModels.size() > 0) {
                    customerList.addAll(sobotCallCustomerModels);
                    ll_search_no_data.setVisibility(View.GONE);
                    ll_association_menu.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.VISIBLE);
                    isHasMoreData = true;
                    refreshLayout.setNoMoreData(false);
                } else {
                    //加载到最后一页
                    isHasMoreData = false;
                }
                if (null != customerList && customerList.size() > 0) {
                    ll_search_no_data.setVisibility(View.GONE);
                    ll_association_menu.setVisibility(View.VISIBLE);
                    refreshLayout.setVisibility(View.VISIBLE);
                } else {
                    ll_search_no_data.setVisibility(View.VISIBLE);
                    ll_association_menu.setVisibility(View.GONE);
                    refreshLayout.setVisibility(View.GONE);
                }
                customAdapter.setDate(inputSearchText);

            }

            @Override
            public void onFailure(Exception e, String des) {
                refreshLayout.finishLoadMore();
                refreshLayout.finishRefresh();
                if (pageIndex == 1) {
                    loading_layout.showEmpty();
                } else {
                    SobotToastUtil.showCustomToast(getSobotActivity(), SobotStringUtils.isEmpty(des) ? getString(R.string.sobot_wo_net_error_string) : des, R.drawable.sobot_icon_warning_attention);
                }
            }
        });
    }

    /**
     * 客户信息弹框
     *
     * @param user
     */
    private void showCustomerDetail(final SobotCustomerModel user) {
        List<SobotCusFieldConfig> cusFieldList = new ArrayList<>();
        cusFieldList.addAll(cusFieldConfigList);
        if (cusFieldList.size() > 0) {
            if (user != null && user.getExtendFields() != null) {
                Map<String, String> map = user.getExtendFields();
                for (int i = 0; i < cusFieldList.size(); i++) {
                    for (int j = 0; j < map.size(); j++) {
                        String value = map.get(cusFieldList.get(i).getFieldVariable());
                        if (!TextUtils.isEmpty(value)) {
                            cusFieldList.get(i).setFieldValue(value);
                        }
                    }
                }
            }
        }
        CostomerDetailDialog costomerDetailDialog = new CostomerDetailDialog(getSobotActivity(), user, cusFieldList, new DialogItemOnClick() {
            @Override
            public void selectItem(Object selectObj) {
                selectCustomerId = user.getId();
                requestAssociationCustom(true);
            }
        });
        costomerDetailDialog.show();
    }

    private void requestCusField() {
        zhiChiApi.getCusFieldInfoList(getContext(), 1, new SobotResultCallBack<List<SobotCusFieldEntity>>() {
            @Override
            public void onSuccess(List<SobotCusFieldEntity> cusFieldConfigs) {
                if (null != cusFieldConfigs) {

                    //"operateType": 11,12固定字段，不要放到自定义字段中
                    for (int i = 0; i < cusFieldConfigs.size(); i++) {
                        SobotCusFieldConfig cusFieldConfig = cusFieldConfigs.get(i).getCusFieldConfig();
                        if (cusFieldConfig != null) {
                            //国外的账户，显示国家,国内账户不显示国家
                            if (serviceVo.getRegion() != 1 && cusFieldConfig.getFieldVariable().equals("countryName")) {
                                //不显示国家
                                continue;
                            }
                            // 排除以下，固定字段(客户昵称，真实姓名，性别，电话，邮箱，公司，VIP)
                            if (cusFieldConfig.getFieldVariable().equals("uname")) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("nick")) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("sex")) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("tel")) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("email")) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("enterpriseName")) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("vipLevel")) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("source")) {
                                continue;
                            }
                            if (cusFieldConfig.getOperateType() == 11) {
                                continue;
                            }
                            if (cusFieldConfig.getFieldVariable().equals("liableServiceName")) {
                                continue;
                            }

                            if (cusFieldConfig.getOpenFlag() == 1) {
                                cusFieldConfigList.add(cusFieldConfig);
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(customerId)) {
                        requestCustomById(false);
                    }

                }
            }

            @Override
            public void onFailure(Exception e, String des) {

            }
        });
    }

    //检测自定义字段是否必填并提示
//    private boolean checkCusFieldRequired() {
//        if (cusFieldConfigList != null && cusFieldConfigList.size() > 0) {
//            for (int i = 0; i < cusFieldConfigList.size(); i++) {
//                CusFieldConfig fieldConfig = cusFieldConfigList.get(i);
//                if (1 == fieldConfig.getOpenFlag() && 1 == fieldConfig.getFillFlag()) {
//                    if (TextUtils.isEmpty(fieldConfig.getFieldValue())) {
//                        String s = getSobotActivity().getResources().getString(R.string.call_retcode_500042);
//                        SobotToastUtil.showToast(getSobotActivity(), String.format(s,fieldConfig.getFieldName()));
//                        return false;
//                    }
//                }
//            }
//        }
//        return true;
//    }

    @Override
    public void onDestroyView() {
//        if (companyDialog != null && companyDialog.isShowing()) {
//            companyDialog.dismiss();
//        }
        super.onDestroyView();
    }
}