package com.sobot.workorder.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.common.ui.toast.SobotToastUtil;
import com.sobot.common.utils.SobotImageUtils;
import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.gson.SobotGsonUtil;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotDensityUtil;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.adapter.SobotGroupListAdapter;
import com.sobot.workorder.adapter.SobotWOCreatePicAdapter;
import com.sobot.workorder.base.SobotWOBaseActivity;
import com.sobot.workorder.permission.SobotPermissionApi;
import com.sobot.workorder.permission.SobotPermissionManager;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotDateUtil;
import com.sobot.workorder.utils.SobotDecimalDigitsInputFilter;
import com.sobot.workorder.utils.SobotDialogUtils;
import com.sobot.workorder.utils.SobotKeyboardUtil;
import com.sobot.workorder.utils.SobotNoDoubleClickListener;
import com.sobot.workorder.utils.SobotOrderImageUtils;
import com.sobot.workorder.utils.SobotSoftInputManage;
import com.sobot.workorder.utils.SobotTicketDictUtil;
import com.sobot.workorder.weight.SobotCustomSwitchButton;
import com.sobot.workorder.weight.SobotSettingItemView;
import com.sobot.workorder.weight.SobotSwitchButton;
import com.sobot.workorder.weight.SobotWheelPickerDialog;
import com.sobot.workorder.weight.dialog.SobotCommonDialog;
import com.sobot.workorder.weight.dialog.SobotSelectPicDialog;
import com.sobot.workorderlibrary.api.apiutils.SobotWOBaseCode;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotCombinFormFieldListModel;
import com.sobot.workorderlibrary.api.model.SobotCombinFormFieldModel;
import com.sobot.workorderlibrary.api.model.SobotCommonItemModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldConfigModel;
import com.sobot.workorderlibrary.api.model.SobotCusFieldDataInfoListModel;
import com.sobot.workorderlibrary.api.model.SobotFileItemModel;
import com.sobot.workorderlibrary.api.model.SobotGroupFieldItemModel;
import com.sobot.workorderlibrary.api.model.SobotListTypeModel;
import com.sobot.workorderlibrary.api.model.SobotReplyPremission;
import com.sobot.workorderlibrary.api.model.SobotServiceGroupModelResult;
import com.sobot.workorderlibrary.api.model.SobotServiceModelResult;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;
import com.sobot.workorderlibrary.api.model.SobotTicketCusFieldCacheModel;
import com.sobot.workorderlibrary.api.model.SobotTicketCusFieldModel;
import com.sobot.workorderlibrary.api.model.SobotTicketCusFieldModelItem;
import com.sobot.workorderlibrary.api.model.SobotTicketModel;
import com.sobot.workorderlibrary.api.model.SobotTicketResultListModel;
import com.sobot.workorderlibrary.api.model.SobotTicketTemplateModel;
import com.sobot.workorderlibrary.api.model.SobotUploadFileModel;
import com.sobot.workorderlibrary.api.model.SobotUploadFileModelResult;
import com.sobot.workorderlibrary.api.model.SobotWODictModel;
import com.sobot.workorderlibrary.api.model.SobotWODictModelResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sobot.common.ui.SobotBaseConstant.REQUEST_CODE_MAKEPICTUREFROMCAMERA;
import static com.sobot.common.ui.SobotBaseConstant.REQUEST_CODE_PICTURE;
import static com.sobot.workorderlibrary.api.apiutils.SobotWOConstant.RESULT_SUCCESS_CODE_SECOND;

/**
 * 工单创建编辑回复页面合成一个页面
 */
public class SobotWOCreateActivity extends SobotWOBaseActivity implements View.OnClickListener {

    //表示当前显示：  0是创建工单页面  1 是回复页面  2是编辑页面
    private int isDisplayType = SobotConstantUtils.sobot_wo_display_type_new;
    private int posi = -1;
    private boolean flag;
    // 工单信息
    private SobotTicketModel ticketDetail;
    private boolean gongdan = false;//是否从聊天窗口进入的创建工单
    //模板中的组合表单字段
    private List<SobotCusFieldConfigModel> cusFieldList = new ArrayList<>();
    //缓存字段中的值用以切换模板时恢复 值显示
    private Map<String, SobotTicketCusFieldCacheModel> mCacheCusFieldVal = new HashMap<>();
    private List<SobotUploadFileModelResult> pic_list = new ArrayList<>();
    private List<SobotUploadFileModelResult> pic_list1 = new ArrayList<>();
    private SobotCusFieldConfigModel mCusFieldConfig;//模板内字段
    private SobotGroupListAdapter mAdapter;
    private SobotWOCreatePicAdapter adapter1;
    private SobotWOCreatePicAdapter adapter;
    private List<SobotWODictModelResult> dictStatus_list = new ArrayList<>();
    private List<SobotWODictModelResult> dictType_list = new ArrayList<>();
    private List<SobotWODictModelResult> dictPriority_list = new ArrayList<>();
    //所有工单模板
    private List<SobotTicketTemplateModel> ticketTemplateModelList = new ArrayList<>();


    private SobotSettingItemView siv_work_order_to_user;
    private SobotSettingItemView siv_work_order_customer_service_group;
    private SobotSettingItemView siv_work_order_customer_service;
    private SobotSettingItemView siv_work_order_copy_to;//抄送给哪个客服
    private SobotSettingItemView siv_work_order_status;
    private SobotSettingItemView siv_work_order_priority;
    private SobotSettingItemView siv_work_order_customer_category;//工单分类
    private SobotSettingItemView siv_work_order_ticket_template;//工单模版
    SobotCustomSwitchButton iv_work_order_display_level;
    RelativeLayout rl_work_order_display_level;
    SobotSwitchButton iv_work_order_reply_display_level;
    TextView work_order_content;//创建工单  工单内容
    EditText work_order_title;//创建工单  工单标题
    EditText et_work_order_describe;
    EditText et_work_order_reply_describe;
    RecyclerView rv_work_order_pic;
    LinearLayout work_order_ll_reply_gone;
    LinearLayout work_order_edit_reply_layout;
    LinearLayout work_order_customer_field_list_layout;
    LinearLayout work_order_reply_permssion;
    LinearLayout work_order_reply_permssion_line;
    private View siv_work_order_customer_category_below_line;
    private RecyclerView rv_work_order_reply_pic;
    private TextView mSummyTv;

    private String userName = null;
    private String userId = null;

    //本地广播数据类型实例。
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    protected static final int ALBUM_REQUEST_CODE_REPLY = 703;/* 图库 */
    protected static final int CAMERA_REQUEST_CODE_REPLY = 704;/* 拍照 */
    private String fileNumKey1 = "";
    private String fileNumKey = "";
    protected SobotSelectPicDialog menuWindow;
    private int selectImageType = 0;//0 工单选照片，1回复选照片

    private SobotServiceVoModel sobotServiceVoModel;


    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent data) {
            try {
                SobotLogUtils.i("广播是  :" + data.getAction());
                if (SobotConstantUtils.SOBOT_CREATE_WORKORDER_USER.equals(data.getAction())) {
                    //创建用户成功
                    siv_work_order_to_user.setText(data.getStringExtra("userName"));
                    siv_work_order_to_user.setValue(data.getStringExtra("userId"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewResId() {
        return getResLayoutId("sobot_activity_wo_create");
    }

    @Override
    protected void initView() {
        sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(getSobotBaseContext()).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
        initDiplayMode();
        initTitle();
        initBrocastReceiver();
        getLeftMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cancleBtn();
            }
        });
        initPageView();
    }

    @Override
    protected void initData() {

        initDict();
        fileNumKey = UUID.randomUUID().toString();

        GridLayoutManager layout = new GridLayoutManager(getApplicationContext(), 5);
        rv_work_order_pic.setLayoutManager(layout);
        adapter = new SobotWOCreatePicAdapter(SobotWOCreateActivity.this);
        adapter.setOnDeleteImgListener(new SobotWOCreatePicAdapter.OnDeleteImgListener() {
            @Override
            public void onDeleteImgClick(View view, final SobotUploadFileModelResult bean) {
                if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_new) {
                    //新增工单时，彻底删除附件
                    if (bean != null) {
                        zhiChiApi.deleteAppFile(SobotWOCreateActivity.this, bean.getFileNumKey(), bean.getFileUrl(), new SobotResultCallBack() {
                            @Override
                            public void onSuccess(Object o) {
                                pic_list.remove(bean);
                                adapter.updateDatas(pic_list);
                            }

                            @Override
                            public void onFailure(Exception e, String des) {
                                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : des, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

                            }
                        });
                    }

                } else {
                    if (bean != null) {
                        pic_list.remove(bean);
                        adapter.updateDatas(pic_list);
                    }
                }
            }
        });
        adapter.setOnClickImgListener(new SobotWOCreatePicAdapter.OnClickImgListener() {
            @Override
            public void onClickImgClick(SobotUploadFileModelResult bean) {
                Intent intent = new Intent(getSobotBaseContext(), SobotPhotoActivity.class);
                intent.putExtra("imageUrL", TextUtils.isEmpty(bean.getLocalPath()) ? bean.getFileUrl() : bean.getLocalPath());
                startActivity(intent);
            }
        });
        rv_work_order_pic.setAdapter(adapter);
        adapter.setOnItemClickListener(new SobotWOCreatePicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectImageType = 0;
                SobotSoftInputManage.hideInputMode(SobotWOCreateActivity.this);
                boolean b = (boolean) view.getTag(R.drawable.sobot_icon_avatar_app_online);
                if (b) {
                    menuWindow = new SobotSelectPicDialog(SobotWOCreateActivity.this, itemsOnClick);
                    menuWindow.show();
                } else {
                    SobotLogUtils.i("当前选择图片位置：" + position);
                    ArrayList<SobotUploadFileModelResult> list = new ArrayList<>();//所有图片的地址
                    for (int i = 0; i < pic_list.size(); i++) {
                        SobotUploadFileModelResult picFile = pic_list.get(i);
                        if (!"addFile".equals(picFile.getFileType())) {
                            list.add(picFile);
                        }
                    }
//                    Intent intent = new Intent(SobotWOCreateActivity.this, PhotoListActivity.class);
//                    intent.putExtra(SobotConstantUtils.work_order_pic_list, list);
//                    intent.putExtra(SobotConstantUtils.work_order_pic_list_current_item, position);
//                    intent.putExtra("fileNumKey", fileNumKey);
//                    intent.putExtra("isDisplayReply", isDisplayReply);
//                    startActivityForResult(intent, SobotConstantUtils.deleteAppFileSuccess);
                }
            }
        });
        adapter.addDatas(pic_list);


        fileNumKey1 = UUID.randomUUID().toString();

        GridLayoutManager layout1 = new GridLayoutManager(getApplicationContext(), 5);
        rv_work_order_reply_pic.setLayoutManager(layout1);
        adapter1 = new SobotWOCreatePicAdapter(SobotWOCreateActivity.this);
        rv_work_order_reply_pic.setAdapter(adapter1);
        adapter1.setOnDeleteImgListener(new SobotWOCreatePicAdapter.OnDeleteImgListener() {
            @Override
            public void onDeleteImgClick(View view, final SobotUploadFileModelResult bean) {
                if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_new) {
                    //新增工单时，彻底删除附件
                    if (bean != null) {
                        zhiChiApi.deleteAppFile(SobotWOCreateActivity.this, bean.getFileNumKey(), bean.getFileUrl(), new SobotResultCallBack() {
                            @Override
                            public void onSuccess(Object o) {
                                pic_list1.remove(bean);
                                adapter1.updateDatas(pic_list1);
                            }

                            @Override
                            public void onFailure(Exception e, String des) {
                                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : des, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

                            }
                        });
                    }

                } else {
                    if (bean != null) {
                        pic_list1.remove(bean);
                        adapter1.updateDatas(pic_list1);
                    }
                }
            }
        });
        adapter1.setOnItemClickListener(new SobotWOCreatePicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectImageType = 1;
                SobotSoftInputManage.hideInputMode(SobotWOCreateActivity.this);
                boolean b = (boolean) view.getTag(R.drawable.sobot_icon_avatar_app_online);
                if (b) {
                    menuWindow = new SobotSelectPicDialog(SobotWOCreateActivity.this, itemsOnClick);
                    menuWindow.show();
                } else {
                    SobotLogUtils.i("当前选择图片位置：" + position);
                    ArrayList<SobotUploadFileModelResult> list = new ArrayList<>();//所有图片的地址
                    for (int i = 0; i < pic_list1.size(); i++) {
                        SobotUploadFileModelResult picFile = pic_list1.get(i);
                        if (!"addFile".equals(picFile.getFileType())) {
                            list.add(picFile);
                        }
                    }
//                    Intent intent = new Intent(SobotWOCreateActivity.this, PhotoListActivity.class);
//                    intent.putExtra(SobotConstantUtils.work_order_pic_list, list);
//                    intent.putExtra(SobotConstantUtils.work_order_pic_list_current_item, position);
//                    intent.putExtra("fileNumKey", fileNumKey1);
//                    startActivityForResult(intent, SobotConstantUtils.deleteAppFileSuccess_reply);
                }
            }
        });
        adapter1.setOnClickImgListener(new SobotWOCreatePicAdapter.OnClickImgListener() {
            @Override
            public void onClickImgClick(SobotUploadFileModelResult bean) {
                Intent intent = new Intent(getSobotBaseContext(), SobotPhotoActivity.class);
                intent.putExtra("imageUrL", TextUtils.isEmpty(bean.getLocalPath()) ? bean.getFileUrl() : bean.getLocalPath());
                startActivity(intent);
            }
        });
        adapter1.addDatas(pic_list1);
    }

    // 为弹出窗口popupwindow实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            menuWindow.dismiss();
            int id = v.getId();
            if (id == R.id.btn_take_photo) {
                openCamera();
            } else if (id == R.id.btn_pick_photo) {
                selectPicFromLocal();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            // 取消广播接受者
            if (localBroadcastManager != null) {
                localBroadcastManager.unregisterReceiver(localReceiver);
            }
            if (localReceiver != null) {
                localReceiver = null;
            }
        } catch (Exception e) {
            //ignor
        }
    }

    private void initBrocastReceiver() {
        if (localReceiver == null) {
            localReceiver = new LocalReceiver();
        }
        localBroadcastManager = LocalBroadcastManager.getInstance(getSobotBaseContext());
        // 创建过滤器，并指定action，使之用于接收同action的广播
        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(SobotConstantUtils.SOBOT_CREATE_WORKORDER_USER);
        // 注册广播接收器
        localBroadcastManager.registerReceiver(localReceiver, localFilter);
    }

    private void initDict() {
        List<SobotWODictModelResult> dictStatus = SobotTicketDictUtil.getDictStatus(getSobotBaseContext());
        List<SobotWODictModelResult> dictType = SobotTicketDictUtil.getDictType(getSobotBaseContext());
        List<SobotWODictModelResult> dictPriority = SobotTicketDictUtil.getDictPriority(getSobotBaseContext());

        dictStatus_list.clear();
        dictType_list.clear();
        dictPriority_list.clear();
        dictStatus_list.addAll(dictStatus);
        /**
         * 创建的时候不可能会有已删除这个状态 因此需要额外删除
         */
        for (int i = dictStatus_list.size() - 1; i >= 0; i--) {
            if ("98".equals(dictStatus_list.get(i).getDictValue())) {
                dictStatus_list.remove(i);
                break;
            }
        }
        dictStatus_list.remove(new SobotWODictModelResult(98, "已删除"));
        dictType_list.addAll(dictType);
        dictPriority_list.addAll(dictPriority);
    }

    private void initDiplayMode() {
        Intent intent = getIntent();
        int displayType = intent.getIntExtra(SobotConstantUtils.sobot_wo_display_type, 0);
        if (SobotConstantUtils.sobot_wo_display_type_reply == displayType) {
            isDisplayType = SobotConstantUtils.sobot_wo_display_type_reply;
            ticketDetail = (SobotTicketModel) intent.getSerializableExtra(SobotConstantUtils.sobto_work_order_data);
        } else if (SobotConstantUtils.sobot_wo_display_type_edit == displayType) {
            isDisplayType = SobotConstantUtils.sobot_wo_display_type_edit;
            ticketDetail = (SobotTicketModel) intent.getSerializableExtra(SobotConstantUtils.sobto_work_order_data);
        } else {
            isDisplayType = SobotConstantUtils.sobot_wo_display_type_new;
        }
        gongdan = intent.getBooleanExtra("gongdan", false);
        userName = intent.getStringExtra("userName");
        userId = intent.getStringExtra("userId");
    }

    //显示标题
    private void initTitle() {
        showRightMenu(0, getResString("sobot_wo_commit_string"), true, this);
        if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_reply) {
            setTitle(getResString("sobot_reply_work_order_string"));
        } else if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_edit) {
            setTitle(getResString("sobot_edit_work_order_string"));
        } else {
            setTitle(getResString("sobot_create_work_order_string"));
        }
    }

    //初始化界面控件
    private void initPageView() {
        iv_work_order_display_level = findViewById(getResId("iv_work_order_display_level"));
        rl_work_order_display_level = findViewById(getResId("rl_work_order_display_level"));
        iv_work_order_reply_display_level = findViewById(getResId("iv_work_order_reply_display_level"));
        work_order_content = findViewById(getResId("work_order_content"));
        work_order_title = findViewById(getResId("work_order_title"));
        et_work_order_describe = findViewById(getResId("et_work_order_describe"));
        et_work_order_reply_describe = findViewById(getResId("et_work_order_reply_describe"));
        rv_work_order_pic = findViewById(getResId("rv_work_order_pic"));
        work_order_ll_reply_gone = findViewById(getResId("work_order_ll_reply_gone"));
        work_order_edit_reply_layout = findViewById(getResId("work_order_edit_reply_layout"));
        work_order_customer_field_list_layout = findViewById(getResId("work_order_customer_field_list_layout"));
        work_order_reply_permssion = findViewById(getResId("work_order_reply_permssion"));
        work_order_reply_permssion_line = findViewById(getResId("work_order_reply_permssion_line"));
        siv_work_order_customer_category_below_line = findViewById(R.id.siv_work_order_customer_category_below_line);
        work_order_ll_reply_gone.setVisibility(View.VISIBLE);
        siv_work_order_to_user = (SobotSettingItemView) findViewById(R.id.siv_work_order_to_user);
        siv_work_order_customer_service_group = (SobotSettingItemView) findViewById(R.id.siv_work_order_customer_service_group);
        siv_work_order_customer_service = (SobotSettingItemView) findViewById(R.id.siv_work_order_customer_service);
        siv_work_order_copy_to = (SobotSettingItemView) findViewById(R.id.siv_work_order_copy_to);
        siv_work_order_status = (SobotSettingItemView) findViewById(R.id.siv_work_order_status);
        siv_work_order_priority = (SobotSettingItemView) findViewById(R.id.siv_work_order_priority);
        siv_work_order_customer_category = (SobotSettingItemView) findViewById(R.id.siv_work_order_customer_category);
        siv_work_order_customer_category.setOnClickListener(this);
        siv_work_order_ticket_template = (SobotSettingItemView) findViewById(R.id.siv_work_order_ticket_template);
        siv_work_order_ticket_template.setOnClickListener(this);
        if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
            siv_work_order_ticket_template.setVisibility(View.VISIBLE);
        } else {
            siv_work_order_ticket_template.setVisibility(View.GONE);
        }
        rv_work_order_reply_pic = (RecyclerView) findViewById(R.id.rv_work_order_reply_pic);

        if (SobotPermissionManager.isHasPermission(getSobotBaseContext(), SobotPermissionManager.USER_PERMISSION_TYPE_WORK_STATUS)) {
            siv_work_order_status.setVisibility(View.VISIBLE);
        } else {
            siv_work_order_status.setVisibility(View.GONE);
        }

        //根据是否有ticketDetail 回显数据
        setTicketDetail();
        //如果从聊天窗口进入工单，
        //setWorkorderUserName();
        if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_reply) {
            sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(getSobotBaseContext()).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
            if (SobotPermissionManager.checkPermission(getSobotBaseContext(), SobotPermissionApi.USER_PERMISSION_TYPE_SHOW_OPTION)
                    || sobotServiceVoModel != null && !TextUtils.isEmpty(sobotServiceVoModel.getServiceId())
                    && ticketDetail != null && !TextUtils.isEmpty(ticketDetail.getDealUserId()) && sobotServiceVoModel.getServiceId().equals(ticketDetail.getDealUserId())) {
                work_order_reply_permssion.setVisibility(View.VISIBLE);
                work_order_reply_permssion_line.setVisibility(View.VISIBLE);
            } else {
                work_order_reply_permssion.setVisibility(View.GONE);
                work_order_reply_permssion_line.setVisibility(View.GONE);
            }

            rl_work_order_display_level.setVisibility(View.VISIBLE);
            work_order_content.setVisibility(View.GONE);
            work_order_title.setVisibility(View.GONE);
            work_order_edit_reply_layout.setVisibility(View.GONE);
            work_order_ll_reply_gone.setVisibility(View.GONE);
            siv_work_order_to_user.setVisibility(View.GONE);
        } else if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_edit) {
            rl_work_order_display_level.setVisibility(View.GONE);
            work_order_content.setVisibility(View.VISIBLE);
            work_order_title.setVisibility(View.VISIBLE);
            work_order_edit_reply_layout.setVisibility(View.VISIBLE);
//            siv_work_order_customer_category.setArrowDisplay(false);
//            siv_work_order_customer_category.setEnabled(false);
//            siv_work_order_customer_category.setClickable(false);
            work_order_ll_reply_gone.setVisibility(View.VISIBLE);
            siv_work_order_to_user.setVisibility(View.VISIBLE);
            siv_work_order_to_user.setEnabled(false);
            siv_work_order_to_user.setArrowDisplay(false);
        } else {
            rl_work_order_display_level.setVisibility(View.GONE);
            if (!gongdan) {
                siv_work_order_to_user.setOnClickListener(this);
                siv_work_order_copy_to.setArrowDisplay(false);
            }
            siv_work_order_to_user.setVisibility(View.VISIBLE);
            siv_work_order_to_user.setEnabled(true);
            //siv_work_order_to_user.setArrowDisplay(true);
            work_order_edit_reply_layout.setVisibility(View.GONE);
        }

        siv_work_order_customer_service_group.setOnClickListener(this);
        siv_work_order_customer_service.setOnClickListener(this);
        siv_work_order_copy_to.setOnClickListener(this);
        siv_work_order_status.setOnClickListener(this);
        siv_work_order_priority.setOnClickListener(this);

        getUserPremission();
    }

    //左上角返回按钮事件监听
    private void cancleBtn() {
        SobotSoftInputManage.hideInputMode(SobotWOCreateActivity.this);
        if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_reply || isDisplayType == SobotConstantUtils.sobot_wo_display_type_edit) {
            finish();
        } else {
            SobotCommonDialog dialog = new SobotCommonDialog(getResString("sobot_discard_job_string"), getResString("sobot_give_up_string"), new SobotCommonDialog.OnBtnClickListener() {
                @Override
                public void onClick() {
                    finish();
                }
            }, getResString("sobot_continue_editing_string"), null);
            dialog.show(SobotWOCreateActivity.this.getSupportFragmentManager(), "dialog");
        }
    }

    private void setTicketDetail() {
        if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
            zhiChiApi.getTicketTemplateList(getSobotBaseActivity(), new SobotResultCallBack<List<SobotTicketTemplateModel>>() {
                @Override
                public void onSuccess(List<SobotTicketTemplateModel> sobotTicketTemplateModels) {
                    ticketTemplateModelList.clear();
                    if (sobotTicketTemplateModels != null && sobotTicketTemplateModels.size() > 0) {
                        ticketTemplateModelList.addAll(sobotTicketTemplateModels);
                    }
                }

                @Override
                public void onFailure(Exception e, String des) {
                    SobotToastUtil.showCustomToast(SobotWOCreateActivity.this, des);
                }
            });
        }
        if (ticketDetail == null) {
            if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_new) {
                if (!TextUtils.isEmpty(userId)) {
                    siv_work_order_to_user.setText(userName);//对应用户名称
                    siv_work_order_to_user.setValue(userId);//对应用户Id
                    userId="";
                }
            }
            return;
        }

        if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_edit) {
            List<SobotTicketResultListModel> resultList = ticketDetail.getResultList();
            List<SobotCusFieldConfigModel> newFieldList = new ArrayList<>();
            if (resultList != null) {
                for (int i = 0; i < resultList.size(); i++) {
                    SobotTicketResultListModel tmpResult = resultList.get(i);
                    SobotCusFieldConfigModel con = new SobotCusFieldConfigModel();
                    con.setCusFieldDataInfoList(tmpResult.getCusFieldDataInfoList());
                    con.setFieldId(tmpResult.getFieldId());
                    con.setFieldType(tmpResult.getFieldType());
                    con.setFieldName(tmpResult.getTitle());
                    con.setOpenFlag(tmpResult.getIsOpenFlag());
                    con.setFieldDataValue(tmpResult.getValue());
//                    con.setFtmpResult.getText();
                    con.setSummy(tmpResult.getSummary());
                    if (tmpResult.getFieldType() == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_GROUP_TYPE) {
                        List<List<SobotCombinFormFieldModel>> combinFormFieldLists = tmpResult.getCombinFormFieldList();
                        List<SobotCombinFormFieldListModel> combinFormFieldList = new ArrayList<>();
                        if (combinFormFieldLists != null && combinFormFieldLists.size() > 0) {
                            for (int j = 0; j < combinFormFieldLists.size(); j++) {
                                List<SobotCombinFormFieldModel> listCombinFormField = combinFormFieldLists.get(j);
                                String tmpId = UUID.randomUUID().toString();
                                for (int k = 0; k < listCombinFormField.size(); k++) {
                                    SobotCombinFormFieldModel formField = listCombinFormField.get(k);
                                    if (formField.getAuthStatus() == 3) continue;//不可见的不显示
                                    formField.setTmpId(tmpId);
                                    SobotCombinFormFieldListModel combinFormFieldList1 = new SobotCombinFormFieldListModel();
                                    combinFormFieldList1.setCombinFormField(formField);
                                    combinFormFieldList.add(combinFormFieldList1);
                                    if (combinFormFieldLists.size() != 1) {
                                        if (k == listCombinFormField.size() - 1) {
                                            formField.setLastItem(true);
                                        }
                                    }
                                }
                            }
                        }
                        con.setCombinFormFieldList(combinFormFieldList);
                    }
                    newFieldList.add(con);
                }
            }
            if (cusFieldList == null) {
                cusFieldList = new ArrayList<>();
            }
            if ((cusFieldList == null || cusFieldList.size() == 0) && ticketDetail.getTemplateFieldList() != null) {
                cusFieldList.clear();
                List<SobotCusFieldConfigModel> fieldConfigs = deepCopy(ticketDetail.getTemplateFieldList());
                for (SobotCusFieldConfigModel model :
                        fieldConfigs) {
                    //过滤不显示字段//                cusFieldList.addAll(fieldConfigs);
                    if (!SobotStringUtils.isEmpty(model.getFieldId())&&model.getAuthStatus() != 3) {
                        cusFieldList.add(model);
                    }
                }

            }
            List<SobotCusFieldConfigModel> tempFieldConfigs = new ArrayList<>();
            for (SobotCusFieldConfigModel model :
                    newFieldList) {
                if (!SobotStringUtils.isEmpty(model.getFieldId())&&model.getAuthStatus() != 3) {
                    boolean hasDate = false;
                    for (int i = 0; i < cusFieldList.size(); i++) {
                        if (model.getFieldId().equals(cusFieldList.get(i).getFieldId())) {
                            hasDate = true;
                            continue;
                        }
                    }
                    if(!hasDate){
                        tempFieldConfigs.add(model);
                    }

                }
            }
            cusFieldList.addAll(tempFieldConfigs);

            if (cusFieldList != null) {
                for (int i = 0; i < cusFieldList.size(); i++) {
                    SobotCusFieldConfigModel tmpConfig = cusFieldList.get(i);
                    if (tmpConfig == null) {
                        continue;
                    }
                    for (int j = newFieldList.size() - 1; j >= 0; j--) {
                        SobotCusFieldConfigModel tmpNewField = newFieldList.get(j);
                        if (tmpNewField == null) {
                            continue;
                        }
                        if (tmpConfig.equals(tmpNewField)) {
                            tmpConfig.setFieldId(tmpNewField.getFieldId());
                            tmpConfig.setOpenFlag(tmpNewField.getOpenFlag());
                            tmpConfig.setFieldType(tmpNewField.getFieldType());
                            tmpConfig.setFieldDataValue(tmpNewField.getFieldDataValue());
                            tmpConfig.setSummy(tmpNewField.getSummy());

                            List<SobotCombinFormFieldListModel> tmpList = tmpConfig.getCombinFormFieldList();
                            List<SobotCombinFormFieldListModel> tmpNewList = tmpNewField.getCombinFormFieldList();
                            if (tmpList != null && tmpList.size() > 0) {
                                for (int k = 0; k < tmpList.size(); k++) {
                                    SobotCombinFormFieldListModel tmpfield = tmpList.get(k);
                                    for (int l = 0; l < tmpNewList.size(); l++) {
                                        SobotCombinFormFieldListModel tmpData = tmpNewList.get(l);
                                        if (tmpfield.getCombinFormField().getFieldId().equals(tmpData.getCombinFormField().getFieldId())) {
                                            List<SobotCusFieldDataInfoListModel> cusFieldDataInfoList = deepCopy(tmpfield.getCusFieldDataInfoList());
                                            if (cusFieldDataInfoList != null && cusFieldDataInfoList.size() > 0) {
                                                for (int m = 0; m < cusFieldDataInfoList.size(); m++) {
                                                    cusFieldDataInfoList.get(m).setTmpData(tmpData.getCombinFormField().getTmpId());
                                                }
                                            }
                                            tmpData.setCusFieldDataInfoList(cusFieldDataInfoList);
                                            tmpConfig.setCombinFormFieldList(tmpNewField.getCombinFormFieldList());
                                        }
                                    }
                                }
                            }

                            newFieldList.remove(j);
                        }
                    }
                }
                //只能编辑当前模板下的字段 不应该显示其他不在模板中的字段
//                cusFieldList.addAll(newFieldList);
            }

            if (cusFieldList != null && resultList != null) {
                for (int i = 0; i < cusFieldList.size(); i++) {
                    for (int j = 0; j < resultList.size(); j++) {
                        if (cusFieldList.get(i).getFieldId().equals(resultList.get(j).getFieldId())) {
                            if (resultList.get(j).getFieldType() == 9 || resultList.get(j).getFieldType() == 8 || resultList.get(j).getFieldType() == 7 || resultList.get(j).getFieldType() == 6) {
                                cusFieldList.get(i).setFieldValue(resultList.get(j).getText());
                            } else {
                                cusFieldList.get(i).setFieldValue(resultList.get(j).getValue());
                            }
                        }
                    }
                }
            }

            addWorkOrderCusFields(SobotWOCreateActivity.this, isDisplayType, processTmplate(ticketDetail), cusFieldList, work_order_customer_field_list_layout);
            saveCacheData(true);

            List<SobotFileItemModel> fileList = ticketDetail.getFileList();
            StringBuilder fileIds = new StringBuilder();
            if (fileList != null && fileList.size() != 0) {
                for (int i = 0; i < fileList.size(); i++) {
                    if (!TextUtils.isEmpty(fileList.get(i).getFileUrl())) {
                        fileIds.append(fileList.get(i).getFileUrl()).append(";");
                    }
                }
                ticketDetail.setFileIds(fileIds.toString());
            }
            List<SobotUploadFileModelResult> uploadFileList = new ArrayList<>();
            SobotUploadFileModelResult uploadFile;
            if (fileList != null && fileList.size() != 0) {
                for (int i = 0; i < fileList.size(); i++) {
                    uploadFile = new SobotUploadFileModelResult();
                    uploadFile.setFileNumKey(fileList.get(i).getFileId());
                    uploadFile.setFileName(fileList.get(i).getFileName());
                    uploadFile.setFileType(fileList.get(i).getFileType());
                    uploadFile.setFileUrl(fileList.get(i).getFileUrl());
                    uploadFileList.add(uploadFile);
                }
                if (uploadFileList.size() != 0) {
                    pic_list = uploadFileList;
                }
            }
            if (ticketDetail != null && ticketDetail.getDefaultFieldList() != null) {
                if (ticketDetail.getDefaultFieldList().size() > 0) {
                    for (int i = 0; i < ticketDetail.getDefaultFieldList().size(); i++) {
                        SobotCombinFormFieldModel combinFormField = ticketDetail.getDefaultFieldList().get(i);
                        if (combinFormField != null && "ticketContent".equals(combinFormField.getFieldVariable())) {
                            if (combinFormField.getAuthStatus() == 1) {
                                et_work_order_describe.setEnabled(true);
                            } else if (combinFormField.getAuthStatus() == 2) {
                                et_work_order_describe.setEnabled(false);
                            }
                        }
                    }
                }
            }
            et_work_order_describe.setText(Html.fromHtml(ticketDetail.getTicketContent()));//工单描述
            if (TextUtils.isEmpty(ticketDetail.getTemplateId())) {
                siv_work_order_ticket_template.setVisibility(View.GONE);
            } else {
                SobotLogUtils.d("================setTicketDetail==="+ticketDetail.getTemplateId());
                if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
                    siv_work_order_ticket_template.setVisibility(View.VISIBLE);
                    siv_work_order_ticket_template.setText(ticketDetail.getTemplateName());
                    siv_work_order_ticket_template.setValue(ticketDetail.getTemplateId());
                } else {
                    siv_work_order_ticket_template.setVisibility(View.GONE);
                }
                //如果工单模版id 不为空，重新请求一次自定义字段的数据，因为详情接口没有返回
                loadCusFieldByTemplateId(ticketDetail.getTemplateId());
            }
        } else if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_reply) {
            et_work_order_describe.setHint(getResString("sobot_reply_content_string"));
        }

        work_order_title.setText(ticketDetail.getTicketTitle());//工单标题

        if (!TextUtils.isEmpty(ticketDetail.getCopyUser())) {
            siv_work_order_copy_to.setText(ticketDetail.getCopyUserName());//抄送客服名称
            siv_work_order_copy_to.setValue(ticketDetail.getCopyUser());
        }

        if (!TextUtils.isEmpty(ticketDetail.getUserName())) {
            siv_work_order_to_user.setText(ticketDetail.getUserName());//对应用户名称
        }

        siv_work_order_status.setText(SobotTicketDictUtil.getStatusName(getSobotBaseContext(), ticketDetail.getTicketStatus() + ""));
        siv_work_order_status.setValue(ticketDetail.getTicketStatus() + "");

        siv_work_order_customer_category.setText(ticketDetail.getTicketTypeName());
        siv_work_order_customer_category.setValue(ticketDetail.getTicketType());

        if (!TextUtils.isEmpty(ticketDetail.getDealGroupId())) {
            siv_work_order_customer_service_group.setText(ticketDetail.getDealGroupName());
            siv_work_order_customer_service_group.setValue(ticketDetail.getDealGroupId());
        }


        if (!TextUtils.isEmpty(ticketDetail.getDealUserId())) {
            siv_work_order_customer_service.setText(ticketDetail.getDealUserName());
            siv_work_order_customer_service.setValue(ticketDetail.getDealUserId());
        }

        siv_work_order_priority.setText(SobotTicketDictUtil.getPriorityName(getSobotBaseContext(), ticketDetail.getTicketLevel() + ""));
        siv_work_order_priority.setValue(ticketDetail.getTicketLevel() + "");
    }

    //编辑的时候，把模板字段转换为map
    private Map<String, SobotCusFieldConfigModel> processTmplate(SobotTicketModel ticketDetail) {
        Map<String, SobotCusFieldConfigModel> map = null;
        if (ticketDetail != null && ticketDetail.getTemplateFieldList() != null) {
            map = new HashMap<>();

            for (int i = 0; i < ticketDetail.getTemplateFieldList().size(); i++) {
                SobotCusFieldConfigModel config = ticketDetail.getTemplateFieldList().get(i);
                if (!TextUtils.isEmpty(config.getFieldId()) && !map.containsKey(config.getFieldId())) {
                    map.put(config.getFieldId(), config);
                } else {
                    continue;
                }
            }
        }
        return map;
    }

    /**
     * 保存自定义字段的数据用以以后切换模板时恢复数据
     */
    private void saveCacheData(boolean isFirst) {
        if (ticketDetail != null && isFirst) {
            //需将不是本模板的内容也保存上
            List<SobotTicketResultListModel> resultList = ticketDetail.getResultList();
            SobotTicketCusFieldCacheModel cusFieldModel;
            if (resultList != null) {
                for (int i = 0; i < resultList.size(); i++) {
                    cusFieldModel = new SobotTicketCusFieldCacheModel();
                    SobotTicketResultListModel tmpData = resultList.get(i);
                    if (!TextUtils.isEmpty(tmpData.getValue())) {
                        cusFieldModel.setId(tmpData.getFieldId());
                        cusFieldModel.setValue(tmpData.getValue());
                        if (!TextUtils.isEmpty(tmpData.getText())) {
                            cusFieldModel.setDisplayVal(tmpData.getText());
                        }
                        mCacheCusFieldVal.put(cusFieldModel.getId(), cusFieldModel);
                    }
                }
            }
        } else {
            setCustomerFieldValue();
            List<SobotTicketCusFieldCacheModel> cusFieldValList = getCusFieldValListCache();
            for (SobotTicketCusFieldCacheModel data : cusFieldValList) {
                mCacheCusFieldVal.put(data.getId(), data);
            }
        }
    }

    /**
     * 根据@saveCacheData() 缓存的数据进行对比回显
     * 当前模板有的字段才显示
     */
    private void redisplayByCacheData(List<SobotCusFieldConfigModel> datas) {
        for (int i = 0; i < datas.size(); i++) {
            SobotCusFieldConfigModel data = datas.get(i);
            SobotTicketCusFieldCacheModel ticketCusFieldModel = mCacheCusFieldVal.get(data.getFieldId());
            if (ticketCusFieldModel != null) {
                if (data.getFieldType() == 6 || data.getFieldType() == 7 || data.getFieldType() == 8 || data.getFieldType() == 9) {
                    data.setFieldDataValue((String) ticketCusFieldModel.getValue());
                    data.setFieldValue(ticketCusFieldModel.getDisplayVal());
                } else {
                    data.setFieldValue((String) ticketCusFieldModel.getValue());
                }
            }
        }
    }

    /**
     * 显示滚动选择器
     *
     * @param data
     * @param listener
     */
    private void showWheelPickerDialog(List<SobotWODictModelResult> data, int itemIndex, SobotWheelPickerDialog
            .OnSelectedListener listener) {
        //数据必须大于3  不然选择器会崩溃
        if (data != null && data.size() >= 3) {
            //将数据转换成字符串集合
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                strings.add(data.get(i).getDictName());
            }
            SobotLogUtils.i("传给选择器的数据:" + strings.toString());
            try {
                SobotWheelPickerDialog dialogDict = new SobotWheelPickerDialog(SobotWOCreateActivity
                        .this, strings, itemIndex);
                dialogDict.setOnSelectedListener(listener);
                dialogDict.show();
            } catch (Exception e) {
                SobotToastUtil.showToast(SobotWOCreateActivity.this, getResString("sobot_data_error_string"));
            }
        } else {
            SobotToastUtil.showToast(SobotWOCreateActivity.this, getResString("sobot_data_error_string"));
        }
    }

    /**
     * 工单状态选择回调
     */
    private SobotWheelPickerDialog.OnSelectedListener dialogDictStatusListener = new SobotWheelPickerDialog.OnSelectedListener() {
        @Override
        public void onSelected(int index, String str) {
            String dictName = dictStatus_list.get(index).getDictName();
            String dictValue = dictStatus_list.get(index).getDictValue();
            SobotLogUtils.i("工单状态选择回调   " + dictName + "    " + dictValue);
            if (!TextUtils.isEmpty(dictValue) && "99".equals(dictValue)) {
                if (!SobotPermissionManager.isHasPermission(getSobotBaseContext(), SobotPermissionManager.USER_PERMISSION_TYPE_COLSE_WORK_ORDER)) {
                    SobotToastUtil.showToast(SobotWOCreateActivity.this, getResString("sobot_no_permission_enabled_string"));
                } else {
                    siv_work_order_status.setText(dictName);
                    siv_work_order_status.setValue(dictValue);
                }
            } else {
                siv_work_order_status.setText(dictName);
                siv_work_order_status.setValue(dictValue);
            }
        }
    };

    /**
     * 工单类型选择回调
     */
    private SobotWheelPickerDialog.OnSelectedListener dialogDictTypeListener = new SobotWheelPickerDialog.OnSelectedListener() {
        @Override
        public void onSelected(int index, String str) {
            String dictName = dictType_list.get(index).getDictName();
            String dictValue = dictType_list.get(index).getDictValue();
            SobotLogUtils.i("工单类型选择回调   " + dictName + "    " + dictValue);

        }
    };

    /**
     * 优先级选择回调
     */
    private SobotWheelPickerDialog.OnSelectedListener dialogDictPriorityListener = new SobotWheelPickerDialog.OnSelectedListener() {
        @Override
        public void onSelected(int index, String str) {
            String dictName = dictPriority_list.get(index).getDictName();
            String dictValue = dictPriority_list.get(index).getDictValue();
            SobotLogUtils.i("优先级选择回调   " + dictName + "    " + dictValue);
            siv_work_order_priority.setText(dictName);
            siv_work_order_priority.setValue(dictValue);
        }
    };

    /**
     * 开启列表展示activity
     *
     * @param data
     * @param displayType
     */
    public void startListActivity(ArrayList<SobotCommonItemModel> data, int displayType, String
            selectedValue) {
        Intent intent = new Intent(getApplicationContext(), SobotWOListActivity.class);
        intent.putExtra(SobotConstantUtils.sobot_wo_display_type, displayType);
        if (null != data && data.size() > 0) {
            intent.putExtra(SobotConstantUtils.sobto_work_order_data, data);
        }
        if (!TextUtils.isEmpty(selectedValue)) {
            intent.putExtra(SobotConstantUtils.sobot_wo_list_data_selected_value, selectedValue);
        }

        startActivityForResult(intent, displayType);
    }

    /*  以下为请求回调  */
    //查询受理客服组
    SobotResultCallBack<List<SobotServiceGroupModelResult>> queryAppServiceGroupListRequest = new SobotResultCallBack<List<SobotServiceGroupModelResult>>() {
        @Override
        public void onSuccess(List<SobotServiceGroupModelResult> items) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotLogUtils.i("查询受理客服组成功----getData：" + items);
            ArrayList<SobotCommonItemModel> data = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                SobotServiceGroupModelResult item = items.get(i);
                SobotCommonItemModel commonItemModel = new SobotCommonItemModel(item.getGroupName(), item.getGroupId());
                data.add(commonItemModel);
            }
            startListActivity(data, SobotConstantUtils.sobot_wo_list_display_type_service_group, siv_work_order_customer_service_group.getValue());

        }

        @Override
        public void onFailure(Exception e, String s) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotToastUtil.showCustomToast(SobotWOCreateActivity.this, s);
        }

    };

    //查询抄送客服
    SobotResultCallBack<List<SobotServiceModelResult>> queryAppServiceListRequestCopyTo = new SobotResultCallBack<List<SobotServiceModelResult>>() {

        @Override
        public void onSuccess(List<SobotServiceModelResult> items) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotLogUtils.i("查询受理客服----getData：" + items);
            ArrayList<SobotCommonItemModel> data = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                SobotServiceModelResult item = items.get(i);
                SobotCommonItemModel commonItemModel = new SobotCommonItemModel(item.getServiceName(), item.getServiceId());
                data.add(commonItemModel);
            }
            startListActivity(data, SobotConstantUtils.sobot_wo_list_display_type_service_copy, siv_work_order_copy_to.getValue());
        }

        @Override
        public void onFailure(Exception e, String s) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotToastUtil.showCustomToast(SobotWOCreateActivity.this, s);
        }
    };

    //查询受理客服
    SobotResultCallBack<List<SobotServiceModelResult>> queryAppServiceListRequest = new SobotResultCallBack<List<SobotServiceModelResult>>() {

        @Override
        public void onSuccess(List<SobotServiceModelResult> items) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotLogUtils.i("查询受理客服----getData：" + items);
            ArrayList<SobotCommonItemModel> data = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                SobotServiceModelResult item = items.get(i);
                SobotCommonItemModel commonItemModel = new SobotCommonItemModel(item.getServiceName(), item.getServiceId());
                data.add(commonItemModel);
            }
            startListActivity(data, SobotConstantUtils.sobot_wo_list_display_type_service,
                    siv_work_order_customer_service.getValue());
        }

        @Override
        public void onFailure(Exception e, String s) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotToastUtil.showCustomToast(SobotWOCreateActivity.this, s);
        }
    };

    //上传附件
    StopProgressDialogCallBack<SobotUploadFileModel> sendFileRequest = new StopProgressDialogCallBack<SobotUploadFileModel>() {

        @Override
        public void onSuccessed(SobotUploadFileModel uploadAppFileModel) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            if (selectImageType == 0) {
                //留言  上传附件
                SobotUploadFileModelResult item = uploadAppFileModel.getItem();
                SobotLogUtils.i("上传成功----getData：" + item.getFileNumKey() + "    " + item.getFileType
                        () + "    " + item.getFileUrl());
                int position = pic_list.size() - 1 < 0 ? 0 : pic_list.size() - 1;
                pic_list.add(position, item);
                adapter.addData(position, item);
            } else {
                //回复  上传附件
                SobotUploadFileModelResult item = uploadAppFileModel.getItem();
                SobotLogUtils.i("上传成功----getData：" + item.getFileNumKey() + "    " + item.getFileType
                        () + "    " + item.getFileUrl());
                int position = pic_list1.size() - 1 < 0 ? 0 : pic_list1.size() - 1;
                pic_list1.add(position, item);
                adapter1.addData(position, item);
            }
        }

        @Override
        public boolean onEmpty() {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            return super.onEmpty();
        }

        @Override
        public void onFailure(Exception e, String des) {
            super.onFailure(e, des);
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
        }
    };


    //新增工单
    StopProgressDialogCallBack<SobotTicketModel> addAppServiceTicketRequest = new StopProgressDialogCallBack<SobotTicketModel>() {

        @Override
        public void onSuccessed(final SobotTicketModel result) {
            SobotLogUtils.i("提交后的工单code----" + result.getTicketCode() + "  --------------和id:----------------     " + result.getTicketId());
            if(isDisplayType == SobotConstantUtils.sobot_wo_display_type_new){
                setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                finish();
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 到工单详情页面
                        SobotSoftInputManage.hideInputMode(SobotWOCreateActivity.this);
                        Intent intent = new Intent(SobotWOCreateActivity.this, SobotWODetailActivity.class);
                        intent.putExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_TICKETID, result.getTicketId());
                        intent.putExtra("flag", true);
                        startActivity(intent);
                        setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                        finish();
                    }
                }, 1300);
            }
        }

        @Override
        public void onFailure(Exception e, String des) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotToastUtil.showToast(SobotWOCreateActivity.this, SobotStringUtils.isEmpty(des) ? getResString("sobot_submission_failed_string") : des);
        }
    };

    //查询工单状态
    StopProgressDialogCallBack<SobotWODictModel> queryAppDictStatusRequest = new StopProgressDialogCallBack<SobotWODictModel>() {

        @Override
        public void onSuccessed(SobotWODictModel workOrderDictModel) {
            List<SobotWODictModelResult> items = workOrderDictModel.getItems();
            dictStatus_list.clear();
            if (items != null) {
                dictStatus_list.addAll(items);
            }
        }
    };

    //查询工单类型
    StopProgressDialogCallBack<SobotWODictModel> queryAppDictTypeRequest = new StopProgressDialogCallBack<SobotWODictModel>() {
        @Override
        public void onSuccessed(SobotWODictModel workOrderDictModel) {
            List<SobotWODictModelResult> items = workOrderDictModel.getItems();
            dictType_list.clear();
            if (items != null) {
                dictType_list.addAll(items);
            }
        }

    };

    //查询优先级
    StopProgressDialogCallBack<SobotWODictModel> queryAppDictPriorityRequest = new StopProgressDialogCallBack<SobotWODictModel>() {
        @Override
        public void onSuccessed(SobotWODictModel workOrderDictModel) {
            List<SobotWODictModelResult> items = workOrderDictModel.getItems();
            dictPriority_list.clear();
            if (items != null) {
                dictPriority_list.addAll(items);
            }
        }
    };

    //新增工单
    StopProgressDialogCallBack<SobotTicketModel> addAppTicketReplyInfoRequest = new StopProgressDialogCallBack<SobotTicketModel>() {

        @Override
        public void onSuccessed(SobotTicketModel result) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult(SobotConstantUtils.SOBOT_NEED_REFRESH);
                    finish();
                }
            }, 1300);
        }

        @Override
        public void onFailure(Exception e, String des) {
//            super.onFailure(e, des);
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotToastUtil.showToast(SobotWOCreateActivity.this, getResString("sobot_submission_failed_string"));
        }
    };


    private void getUserPremission() {
        zhiChiApi.getTicketAuthInfo(SobotWOCreateActivity.class, new SobotResultCallBack<SobotReplyPremission>() {
            @Override
            public void onSuccess(SobotReplyPremission sobotReplyPremission) {
                if (sobotReplyPremission != null) {
                            /*    SobotLogUtils.i("replyPremission.getTicketReplyType()---:" + replyPremission.getTicketReplyType()
                                        + "-------replyPremission.getReplyAuthflag()--------" + replyPremission.getReplyAuthflag());
                        //getTicketReplyType   是超管设置的默认回复可见范围
                        //getReplyAuthflag     是超管设置管理员回复工单权限*/
                    if (sobotReplyPremission.getTicketReplyType() == 1) {
                        iv_work_order_display_level.setSelected(true);
                        iv_work_order_reply_display_level.setChecked(true);
                    } else {
                        iv_work_order_display_level.setSelected(false);
                        iv_work_order_reply_display_level.setChecked(false);
                    }
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : des, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

            }
        });
    }

    /**
     * isEdit 是否编辑页面  0是创建工单页面  1 是回复页面  2是编辑页面
     */
    public void addWorkOrderCusFields(final Context context, final int isEdit, Map<String, SobotCusFieldConfigModel> tmpConfigs, final List<SobotCusFieldConfigModel> cusFieldList, LinearLayout containerLayout) {
        SobotLogUtils.i("isEdit================" + isEdit);
        if (containerLayout != null) {
            containerLayout.setVisibility(View.VISIBLE);
            containerLayout.removeAllViews();
            if (cusFieldList != null && cusFieldList.size() != 0) {
                int size = cusFieldList.size();
                for (int i = 0; i < cusFieldList.size(); i++) {
                    final SobotCusFieldConfigModel cusFieldConfig = cusFieldList.get(i);
                    SobotLogUtils.i("cusFieldConfig================" + cusFieldConfig.getFieldName() + "---------------" + cusFieldConfig.getAuthStatus() + "===========" + cusFieldConfig.getFieldType());
                    if (cusFieldConfig.getOpenFlag() != 1 || (isEdit == 2 && cusFieldConfig.getAuthStatus() == 3)) {
                        continue;
                    }
                    View view = View.inflate(context, R.layout.sobot_adapter_order_cusfield_list_item, null);
                    view.setTag(cusFieldConfig.getFieldId());
                    View bottomLine = view.findViewById(R.id.work_order_customer_field_text_bootom_line);
                    if (cusFieldList.size() == 1 || i == (size - 1)) {
                        bottomLine.setVisibility(View.GONE);
                    } else {
                        bottomLine.setVisibility(View.VISIBLE);
                    }
                    LinearLayout ll_more_text_layout = (LinearLayout) view.findViewById(R.id.work_order_customer_field_more_relativelayout);
                    final LinearLayout ll_group_text_layout = (LinearLayout) view.findViewById(R.id.work_order_customer_field_group_relativelayout);
                    RelativeLayout ll_group_layout = (RelativeLayout) view.findViewById(R.id.work_order_customer_field_group__rl_add);
                    final TextView addTv = view.findViewById(R.id.work_order_customer_field_group_add);
                    final TextView summyTv = view.findViewById(R.id.work_order_customer_field_group_summy);
                    ll_group_text_layout.setVisibility(View.GONE);
                    summyTv.setVisibility(View.GONE);
                    ll_group_layout.setVisibility(View.GONE);
                    addTv.setVisibility(View.GONE);
                    TextView fieldMoreName = (TextView) view.findViewById(R.id.work_order_customer_field_more_text_lable);
                    final EditText moreContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_more_content);

                    RelativeLayout ll_text_layout = (RelativeLayout) view.findViewById(R.id.work_order_customer_field_text);
                    TextView fieldName = (TextView) view.findViewById(R.id.work_order_customer_field_text_lable);
                    final TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                    EditText fieldValue = (EditText) view.findViewById(R.id.work_order_customer_field_text_content);
                    EditText numberContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_number);
                    numberContent.setFilters(new InputFilter[]{new SobotDecimalDigitsInputFilter()});

                    final EditText singleContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_single);
                    ImageView fieldImg = (ImageView) view.findViewById(R.id.work_order_customer_field_text_img);

                    if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SINGLE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.VISIBLE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        singleContent.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            singleContent.setHint(singleContent.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }
                        //只能读，不可以编辑
                        if (isEdit == 2 && cusFieldConfig.getAuthStatus() == 2) {
                            singleContent.setEnabled(false);
                        }
                        singleContent.setSingleLine(true);
                        singleContent.setMaxEms(11);
                        singleContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            singleContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            singleContent.setEnabled(false);
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_MORE_LINE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.GONE);
                        fieldMoreName.setText(cusFieldList.get(i).getFieldName());
                        moreContent.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            moreContent.setHint(moreContent.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }
                        //只能读，不可以编辑
                        if (isEdit == 2 && cusFieldConfig.getAuthStatus() == 2) {
                            moreContent.setEnabled(false);
                        }
                        moreContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                        //设置EditText的显示方式为多行文本输入
                        moreContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        //文本显示的位置在EditText的最上方
                        moreContent.setGravity(Gravity.TOP);
                        //改变默认的单行模式
                        moreContent.setSingleLine(false);
                        //水平滚动设置为False
                        moreContent.setHorizontallyScrolling(false);

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            moreContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            moreContent.setEnabled(false);
                        }
                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            textClick.setHint(textClick.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }
                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    String content = textClick.getText().toString();
                                    Date date = null;
                                    if (!TextUtils.isEmpty(content)) {
                                        date = com.sobot.utils.SobotDateUtil.parse(content, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? SobotDateUtil.DATE_FORMAT2 : SobotDateUtil.DATE_FORMAT0);
                                    }
                                    SobotKeyboardUtil.hideKeyboard(textClick);
                                    SobotDateUtil.openTimePickerView(context, textClick, date, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1);
                                }
                            });
                        }
                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_TIME_TYPE == cusFieldList.get(i).getFieldType()) {
                        final int fieldType = cusFieldList.get(i).getFieldType();
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            textClick.setHint(textClick.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            textClick.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    String content = textClick.getText().toString();
                                    Date date = null;
                                    if (!TextUtils.isEmpty(content)) {
                                        date = com.sobot.utils.SobotDateUtil.parse(content, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? SobotDateUtil.DATE_FORMAT2 : SobotDateUtil.DATE_FORMAT0);
                                    }

                                    SobotKeyboardUtil.hideKeyboard(textClick);
                                    SobotDateUtil.openTimePickerView(context, textClick, date, fieldType == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_DATE_TYPE ? 0 : 1);
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_NUMBER_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.GONE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        numberContent.setVisibility(View.VISIBLE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        numberContent.setSingleLine(true);
                        numberContent.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            numberContent.setHint(numberContent.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }

                        numberContent.setInputType(EditorInfo.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            numberContent.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 0) {
                            numberContent.setEnabled(false);
                        }
                        //只能读，不可以编辑
                        if (isEdit == 2 && cusFieldConfig.getAuthStatus() == 2) {
                            numberContent.setEnabled(false);
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SPINNER_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            textClick.setHint(textClick.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    if (context instanceof SobotWOCreateActivity) {
                                        ((SobotWOCreateActivity) context).startCategorySmallActivity(cusFieldConfig);
                                    }
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            textClick.setHint(textClick.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    if (context instanceof SobotWOCreateActivity) {
                                        ((SobotWOCreateActivity) context).startCascadeActivity(cusFieldConfig);
                                    }
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_RADIO_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        numberContent.setVisibility(View.GONE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            textClick.setHint(textClick.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }

                        if (!TextUtils.isEmpty(cusFieldConfig.getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    if (context instanceof SobotWOCreateActivity) {
                                        ((SobotWOCreateActivity) context).startCategorySmallActivity(cusFieldConfig);
                                    }
                                }
                            });
                        }

                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE == cusFieldList.get(i).getFieldType()) {
                        ll_more_text_layout.setVisibility(View.GONE);
                        textClick.setVisibility(View.VISIBLE);
                        ll_text_layout.setVisibility(View.VISIBLE);
                        fieldImg.setVisibility(View.VISIBLE);
                        fieldValue.setVisibility(View.GONE);
                        singleContent.setVisibility(View.GONE);
                        numberContent.setVisibility(View.GONE);
                        fieldName.setText(cusFieldList.get(i).getFieldName());
                        textClick.setHint(cusFieldList.get(i).getFieldRemark());
                        if (1 == cusFieldList.get(i).getFillFlag()) {
                            textClick.setHint(textClick.getHint() + "(" + getResString("sobot_required_string") + ")");
                        }

                        if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                            textClick.setText(cusFieldList.get(i).getFieldValue());
                        }

                        if (cusFieldConfig.getOpenFlag() == 1) {
                            ll_text_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isEdit == 2) {
                                        //编辑界面 同时字段只读，就直接返回
                                        if (cusFieldConfig.getAuthStatus() == 2) {
                                            return;
                                        }
                                    }
                                    if (context instanceof SobotWOCreateActivity) {
                                        ((SobotWOCreateActivity) context).startCategorySmallActivity(cusFieldConfig);
                                    }
                                }
                            });
                        }
                    } else if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_GROUP_TYPE == cusFieldConfig.getFieldType()) {
                        ll_text_layout.setVisibility(View.GONE);
                        ll_more_text_layout.setVisibility(View.GONE);
                        ll_group_text_layout.setVisibility(View.VISIBLE);
                        ll_group_layout.setVisibility(View.VISIBLE);
                        addTv.setVisibility(View.VISIBLE);
                        if (cusFieldConfig.getNumericalFlag() == 0) {
                            summyTv.setVisibility(View.GONE);
                        } else if (!TextUtils.isEmpty(cusFieldConfig.getSummy())) {
                            summyTv.setText(cusFieldConfig.getSummy());
                            summyTv.setVisibility(View.VISIBLE);
                        } else if (cusFieldConfig.getComputedUnit() == 1 || cusFieldConfig.getComputedUnit() == 2) {
                            SobotCombinFormFieldListModel fieldList;
                            SobotCombinFormFieldModel formField;
                            SobotCombinFormFieldModel formField1 = null;
                            SobotCombinFormFieldModel formField2 = null;
                            for (int j = 0; j < cusFieldConfig.getCombinFormFieldList().size(); j++) {
                                fieldList = cusFieldConfig.getCombinFormFieldList().get(j);
                                if (fieldList != null) {
                                    formField = fieldList.getCombinFormField();
                                    if (formField != null) {
                                        if (!TextUtils.isEmpty(formField.getFieldId()) && formField.getFieldId().equals(cusFieldConfig.getOperateUnitId1())) {
                                            formField1 = formField;
                                        }

                                        if (!TextUtils.isEmpty(formField.getFieldId()) && formField.getFieldId().equals(cusFieldConfig.getOperateUnitId2())) {
                                            formField2 = formField;
                                        }
                                    }
                                }
                            }

                            if (cusFieldConfig.getComputedUnit() == 1 && formField1 != null && formField2 != null && !TextUtils.isEmpty(formField1.getFieldName()) && !TextUtils.isEmpty(formField2.getFieldName())) {
                                summyTv.setText("【" + formField1.getFieldName() + "加" + formField2.getFieldName() + "】合计: 0.00");
                                summyTv.setVisibility(View.VISIBLE);
                            } else if (cusFieldConfig.getComputedUnit() == 2 && formField1 != null && formField2 != null && !TextUtils.isEmpty(formField1.getFieldName()) && !TextUtils.isEmpty(formField2.getFieldName())) {
                                summyTv.setText("【" + formField1.getFieldName() + "乘" + formField2.getFieldName() + "】合计: 0.00");
                                summyTv.setVisibility(View.VISIBLE);
                            }
                        } else if (!TextUtils.isEmpty(cusFieldConfig.getOperateUnitId1()) && TextUtils.isEmpty(cusFieldConfig.getOperateUnitId2())) {
                            SobotCombinFormFieldListModel fieldList;
                            SobotCombinFormFieldModel formField;
                            SobotCombinFormFieldModel formField1 = null;
                            for (int j = 0; j < cusFieldConfig.getCombinFormFieldList().size(); j++) {
                                fieldList = cusFieldConfig.getCombinFormFieldList().get(j);
                                if (fieldList != null) {
                                    formField = fieldList.getCombinFormField();
                                    if (formField != null) {
                                        if (!TextUtils.isEmpty(formField.getFieldId()) && formField.getFieldId().equals(cusFieldConfig.getOperateUnitId1())) {
                                            formField1 = formField;
                                        }
                                    }
                                }
                            }

                            if (formField1 != null) {
                                summyTv.setText("【" + formField1.getFieldName() + "】" + getResString("sobot_total_string") + ": 0.00");
                                summyTv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            summyTv.setVisibility(View.GONE);
                        }
                        cusFieldConfig.setSummy(summyTv.getText().toString());

                        //如果编辑工单时，模板
                        List<SobotCombinFormFieldListModel> tmpList = cusFieldConfig.getCombinFormFieldList();
                        if (tmpConfigs != null) {
                            if (tmpConfigs.get(cusFieldConfig.getFieldId()) != null && tmpConfigs.get(cusFieldConfig.getFieldId()).getCombinFormFieldList().size() > 0) {
                                tmpList = tmpConfigs.get(cusFieldConfig.getFieldId()).getCombinFormFieldList();
                            }
                        }

                        final List<SobotCombinFormFieldListModel> finalList = deepCopy(tmpList);
                        setTmpData(cusFieldConfig.getCombinFormFieldList());
                        final SobotGroupListAdapter adapter = new SobotGroupListAdapter(SobotWOCreateActivity.this, cusFieldConfig.getCombinFormFieldList(), cusFieldConfig, summyTv);

                        adapter.setTotalChangeListener(new SobotGroupListAdapter.OnTotalChangeListener() {
                            @Override
                            public void sendTotalMsg(SobotListTypeModel totalModel,SobotCusFieldConfigModel mCusFieldConfig,TextView mSummy) {
                                total(totalModel,mCusFieldConfig,mSummy);
                            }
                        });
                        final View groupView = addListView(cusFieldConfig, context, adapter);
                        ll_group_text_layout.addView(groupView);
                        adapter.setGroupView(groupView);
                        addTv.setOnClickListener(new SobotNoDoubleClickListener() {
                            @Override
                            public void onNoDoubleClick(View v) {
                                SobotLogUtils.d("===========添加条件="+adapter.getmListData().size()+">="+finalList.size() * 50);
                                if (adapter.getmListData().size() >= finalList.size() * 50) {
                                    SobotToastUtil.showToast(context, getResString("sobot_only_50_string"));
                                } else {
                                    //第一次点击添加按钮，用adapter取到当前数据，把当前数据的最后一条数据的lastItem设置为true，显示删除按钮
                                    List<SobotCombinFormFieldListModel> fieldLists = adapter.getmListData();
                                    if (fieldLists != null && fieldLists.size() > 0) {
                                        for (int j = 0; j < fieldLists.size(); j++) {
                                            if (j == fieldLists.size() - 1) {
                                                fieldLists.get(j).getCombinFormField().setLastItem(true);
                                            }
                                        }
                                    }

                                    //添加一组组合字段的时候，先把之前的数据清空
                                    List<SobotCombinFormFieldListModel> lists = deepCopy(finalList);
                                    if (lists != null && lists.size() > 0) {
                                        for (int j = 0; j < lists.size(); j++) {
                                            SobotCombinFormFieldListModel fieldList = lists.get(j);
                                            if (fieldList != null) {
                                                SobotCombinFormFieldModel field = fieldList.getCombinFormField();
                                                if (field != null) {
                                                    field.setTmpId("");
                                                    field.setText("");
                                                    field.setValue("");
                                                    field.setTmpValue("");
                                                    if (j == lists.size() - 1) {
                                                        field.setLastItem(true);
                                                    }
                                                }
                                                List<SobotGroupFieldItemModel> models = fieldList.getGroupFieldItemModels();
                                                if (models != null && models.size() > 0) {
                                                    for (int k = 0; k < models.size(); k++) {
                                                        models.get(k).setTmpData("");
                                                        models.get(k).setChecked(false);
                                                    }
                                                }
                                                List<SobotCusFieldDataInfoListModel> infoList = fieldList.getCusFieldDataInfoList();
                                                if (infoList != null && infoList.size() > 0) {
                                                    for (int k = 0; k < infoList.size(); k++) {
                                                        infoList.get(k).setTmpData("");
                                                        infoList.get(k).setChecked(false);
                                                    }
                                                }
                                            }
                                        }
                                        //然后设置唯一tmpId
                                        setTmpData(lists);
                                        adapter.addListData(lists);
                                        if (groupView != null) {
                                            groupView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SobotDensityUtil.dp2px(context, 40) * adapter.getItemCount()+SobotDensityUtil.dp2px(context, 40)));
                                        }
                                    }
                                }
                            }
                        });
                        TextView name = view.findViewById(R.id.work_order_customer_field_group_name);
                        name.setText(cusFieldList.get(i).getFieldName());
                    }
                    containerLayout.addView(view);
                }
            }
        }
    }

    private static View addListView(final SobotCusFieldConfigModel cusFieldConfig, final Context context, final SobotGroupListAdapter adapter) {

        if (cusFieldConfig == null) {
            return null;
        }

        RecyclerView groupView = (RecyclerView) View.inflate(context, R.layout.sobot_adapter_cusfield_group_list, null);
        groupView.setLayoutManager(new LinearLayoutManager(context));

        final List<SobotCombinFormFieldListModel> combinFormFieldLists = cusFieldConfig.getCombinFormFieldList();
        groupView.setTag(cusFieldConfig.getCreateId());
        if (combinFormFieldLists != null && combinFormFieldLists.size() > 0) {
            groupView.setTag(cusFieldConfig.getCreateId());
            groupView.setAdapter(adapter);
            groupView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SobotDensityUtil.dp2px(context, 42) * cusFieldConfig.getCombinFormFieldList().size()));
        }
        return groupView;
    }

    void setTmpData(List<SobotCombinFormFieldListModel> combinFormFieldLists) {
        if (combinFormFieldLists == null || combinFormFieldLists.size() == 0) {
            return;
        }
        String tmpId = UUID.randomUUID().toString();
        for (int j = 0; j < combinFormFieldLists.size(); j++) {
            SobotCombinFormFieldListModel combin = combinFormFieldLists.get(j);
            if (combin != null) {
                SobotCombinFormFieldModel formField = combin.getCombinFormField();
                if (formField != null && TextUtils.isEmpty(formField.getTmpId())) {
                    formField.setTmpId(tmpId);
                }

                List<SobotCusFieldDataInfoListModel> dataInfoList = combin.getCusFieldDataInfoList();
                if (dataInfoList != null && dataInfoList.size() > 0) {
                    for (int k = 0; k < dataInfoList.size(); k++) {
                        SobotCusFieldDataInfoListModel info = dataInfoList.get(k);
                        if (info != null && TextUtils.isEmpty(info.getTmpData())) {
                            info.setTmpData(tmpId);
                        }
                    }
                }

                List<SobotGroupFieldItemModel> models = combin.getGroupFieldItemModels();
                if (models != null && models.size() > 0) {
                    for (int i = 0; i < models.size(); i++) {
                        SobotGroupFieldItemModel model = models.get(i);
                        if (model != null && TextUtils.isEmpty(model.getText())) {
                            model.setTmpData(tmpId);
                        }
                    }
                }
            }
        }
    }

    private void setCustomerFieldValue() {
        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                if (cusFieldList.get(i).getOpenFlag() != 1) {
                    continue;
                }
                View view = work_order_customer_field_list_layout.findViewWithTag(cusFieldList.get(i).getFieldId());
                if (view != null) {
                    if (cusFieldList.get(i).getFieldType() == 1) {
                        EditText singleLine = (EditText) view.findViewById(R.id.work_order_customer_field_text_single);
                        cusFieldList.get(i).setFieldValue(singleLine.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 2) {
                        EditText moreContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_more_content);
                        cusFieldList.get(i).setFieldValue(moreContent.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 3) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 4) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 5) {
                        EditText numberContent = (EditText) view.findViewById(R.id.work_order_customer_field_text_number);
                        cusFieldList.get(i).setFieldValue(numberContent.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 6) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 7) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 8) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    } else if (cusFieldList.get(i).getFieldType() == 9) {
                        TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                        cusFieldList.get(i).setFieldValue(textClick.getText().toString());
                    }
                }
            }
        }
    }

    private boolean checkCusFieldRequired() {
        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                SobotCusFieldConfigModel fieldConfig = cusFieldList.get(i);
                if (fieldConfig.getFieldType() == 10) {
                    List<SobotCombinFormFieldListModel> combinFormFieldList = fieldConfig.getCombinFormFieldList();
                    if (combinFormFieldList != null && combinFormFieldList.size() > 0) {
                        for (int j = 0; j < combinFormFieldList.size(); j++) {
                            SobotCombinFormFieldModel field = combinFormFieldList.get(j).getCombinFormField();
                            if (1 == fieldConfig.getOpenFlag() && field.getFillFlag() == 1) {
                                if (field.getFieldType() == 6 || field.getFieldType() == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE) {
                                    if (TextUtils.isEmpty(field.getTmpValue()) && TextUtils.isEmpty(field.getText()) && TextUtils.isEmpty(field.getValue())) {
                                        SobotToastUtil.showToast(SobotWOCreateActivity.this, fieldConfig.getFieldName() + "：" + field.getFieldName() + "必选");
                                        return false;
                                    }
                                }

                                if (field.getFieldType() == 5 || field.getFieldType() == 1) {
                                    if (TextUtils.isEmpty(field.getTmpValue()) && TextUtils.isEmpty(field.getText()) && TextUtils.isEmpty(field.getValue())) {
                                        SobotToastUtil.showToast(SobotWOCreateActivity.this, fieldConfig.getFieldName() + "：" + field.getFieldName() + " " + getResString("sobot_required_string"));
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (1 == fieldConfig.getOpenFlag() && 1 == fieldConfig.getFillFlag()) {
                        if (!TextUtils.isEmpty(fieldConfig.getFieldValue())) {
                        } else {
                            SobotToastUtil.showToast(SobotWOCreateActivity.this, fieldConfig.getFieldName() + getResString("sobot_cannot_empty_string"));
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * 根据当前页面上所填的值获取自定义字段的值集合
     *
     * @return
     */
    private List<SobotTicketCusFieldCacheModel> getCusFieldValListCache() {
        List<SobotTicketCusFieldCacheModel> cusFieldModelList = new ArrayList<>();
        SobotTicketCusFieldCacheModel cusFieldModel;

        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                cusFieldModel = new SobotTicketCusFieldCacheModel();
                if (cusFieldList.get(i).getFieldType() == 6 || cusFieldList.get(i).getFieldType() == 7 || cusFieldList.get(i).getFieldType() == 8 || cusFieldList.get(i).getFieldType() == 9) {
                    if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldDataValue())) {
                        cusFieldModel.setId(cusFieldList.get(i).getFieldId());
                        cusFieldModel.setValue(cusFieldList.get(i).getFieldDataValue());
                        cusFieldModel.setDisplayVal(cusFieldList.get(i).getFieldValue());
                        if (cusFieldList.get(i).getFieldType() == 9) {
                            cusFieldModel.setText(cusFieldList.get(i).getFieldValue());
                        }
                    } else {
                        continue;
                    }
                } else {
                    if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                        cusFieldModel.setId(cusFieldList.get(i).getFieldId());
                        cusFieldModel.setValue(cusFieldList.get(i).getFieldValue());
                    } else {
                        continue;
                    }
                }

                cusFieldModelList.add(cusFieldModel);
            }
        }
        return cusFieldModelList;
    }


    public void startGroupFieldActivity(TextView summy, boolean flag, SobotGroupListAdapter adapter, SobotCusFieldConfigModel cusFieldConfig, List<SobotCusFieldDataInfoListModel> cusFieldDataInfoLists, SobotCombinFormFieldModel formField, int position, String group_dataId) {
        this.mAdapter = adapter;
        this.mSummyTv = summy;
        mCusFieldConfig = cusFieldConfig;
        posi = position;
        this.flag = flag;
        Intent intent = new Intent(this, SobotGroupFieldActivity.class);
        intent.putExtra("cusFieldConfig", cusFieldConfig);
        intent.putExtra("cusFieldDataInfoLists", (Serializable) cusFieldDataInfoLists);
        intent.putExtra("formField", formField);
        intent.putExtra("position", position);
        intent.putExtra("group_dataId", group_dataId);
        intent.putExtra("flag", flag);
        startActivityForResult(intent, SobotConstantUtils.sobot_wo_create_type_group_field);
    }

    public void startCascadeActivity(TextView summy, boolean flag, SobotGroupListAdapter adapter, SobotCusFieldConfigModel cusFieldConfig, List<SobotCusFieldDataInfoListModel> cusFieldDataInfoLists, SobotCombinFormFieldModel formField, int position, String group_dataId) {
        this.mAdapter = adapter;
        this.mSummyTv = summy;
        mCusFieldConfig = cusFieldConfig;
        posi = position;
        this.flag = flag;

        Intent intent = new Intent(this, SobotWOCascadeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", mCusFieldConfig.getFieldType());
        cusFieldConfig.setCusFieldDataInfoList(cusFieldDataInfoLists);
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        bundle.putSerializable("formField", formField);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, SobotConstantUtils.sobot_wo_create_type_group_field);
    }

    public void startCategorySmallActivity(SobotCusFieldConfigModel cusFieldConfig) {
        mCusFieldConfig = cusFieldConfig;
        Intent intent = new Intent(this, SobotCategorySmallActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", mCusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, SobotConstantUtils.sobot_wo_create_type_category_small);
    }

    public void startCascadeActivity(SobotCusFieldConfigModel cusFieldConfig) {
        mCusFieldConfig = cusFieldConfig;
        Intent intent = new Intent(this, SobotWOCascadeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("fieldType", mCusFieldConfig.getFieldType());
        bundle.putSerializable("cusFieldConfig", cusFieldConfig);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, SobotConstantUtils.sobot_wo_create_type_category_small);
    }

    @Override
    public void onClick(View v) {
        SobotSoftInputManage.hideInputMode(SobotWOCreateActivity.this);
        Intent intent;
        int id = v.getId();
        if (id == R.id.siv_work_order_customer_service_group) {//受理客服组
            SobotDialogUtils.startProgressDialog(SobotWOCreateActivity.this);
            zhiChiApi.queryAppServiceGroupList(SobotWOCreateActivity.this, queryAppServiceGroupListRequest);
        } else if (id == R.id.siv_work_order_copy_to) {//抄送客服
            boolean isNew = false;
            if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
                isNew = true;
            }
            zhiChiApi.queryAppServiceList(SobotWOCreateActivity.this, "", isNew, queryAppServiceListRequestCopyTo);
        } else if (id == R.id.siv_work_order_customer_service) {//受理客服
            SobotDialogUtils.startProgressDialog(SobotWOCreateActivity.this);
            String groupId = "";
            if (!TextUtils.isEmpty(siv_work_order_customer_service_group.getValue()) &&
                    !siv_work_order_customer_service_group.getValue().equals("-1")) {
                groupId = siv_work_order_customer_service_group.getValue();
            }
            boolean isNew = false;
            if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
                isNew = true;
            }
            zhiChiApi.queryAppServiceList(SobotWOCreateActivity.this, groupId, isNew, queryAppServiceListRequest);
        } else if (id == R.id.siv_work_order_status) {//工单状态
            showWheelPickerDialog(dictStatus_list, SobotTicketDictUtil.getDictIndex
                            (dictStatus_list, siv_work_order_status.getValue()),
                    dialogDictStatusListener);
        } else if (id == R.id.siv_work_order_priority) {//优先级
            showWheelPickerDialog(dictPriority_list, SobotTicketDictUtil.getDictIndex
                            (dictPriority_list, siv_work_order_priority.getValue())
                    , dialogDictPriorityListener);
        } else if (id == R.id.siv_work_order_customer_category) {//工单分类
            // 保存之前模板相同字段
            saveCacheData(false);

            intent = new Intent(SobotWOCreateActivity.this, SobotWOCategoryActivity.class);
            intent.putExtra("work_order_category", siv_work_order_customer_category.getTextByTrim());
            startActivityForResult(intent, SobotConstantUtils.sobot_wo_list_display_type_category);
        } else if (id == R.id.siv_work_order_ticket_template) {//工单模版
            // 保存之前模板相同字段
            saveCacheData(false);
            ArrayList<SobotCommonItemModel> data = new ArrayList<>();
            for (int i = 0; i < ticketTemplateModelList.size(); i++) {
                SobotTicketTemplateModel item = ticketTemplateModelList.get(i);
                SobotCommonItemModel commonItemModel = new SobotCommonItemModel(item.getTemplateName(), item.getTemplateId());
                data.add(commonItemModel);
            }
            startListActivity(data, SobotConstantUtils.sobot_wo_list_ticket_template, siv_work_order_ticket_template.getValue());
        } else if (id == R.id.siv_work_order_to_user) {//工单 对应客户  进入的是用户搜索界面
            intent = new Intent(SobotWOCreateActivity.this, SobotWOUserSearchActivity.class);
            startActivityForResult(intent, SobotConstantUtils.sobot_wo_list_display_type_user_center);
        }
        if (v == getRightMenu()) {
            setCustomerFieldValue();
            if (checkResult()) {
                SobotSoftInputManage.hideInputMode(SobotWOCreateActivity.this);

                submitMethod();
            }
        }
    }

    private void submitMethod() {
        SobotDialogUtils.startProgressDialog(SobotWOCreateActivity.this, "");
        SobotTicketModel ticket = getTicket();
        int replyType;

        if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_reply) {
            if (iv_work_order_display_level.isOpen()) {
                replyType = 1;
            } else {
                replyType = 0;
            }
            zhiChiApi.addNewAppTicketReplyInfo(SobotWOCreateActivity.this, ticket, replyType, isDisplayType, addAppTicketReplyInfoRequest);
        } else if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_edit) {
            if (iv_work_order_reply_display_level.isChecked()) {
                replyType = 1;
            } else {
                replyType = 0;
            }

            zhiChiApi.addNewAppEditTicketReplyInfo(SobotWOCreateActivity.this, ticket, replyType, isDisplayType, addAppTicketReplyInfoRequest);
        } else {
            zhiChiApi.addNewAppServiceTicket(SobotWOCreateActivity.this, ticket, addAppServiceTicketRequest);
        }
    }

    /**
     * 将要提交的数据放到一个对象中
     *
     * @return
     */
    public SobotTicketModel getTicket() {
        SobotTicketModel ticket = new SobotTicketModel();
        if (ticketDetail != null) {
            ticket.setTicketId(ticketDetail.getTicketId());
            ticket.setTicketCode(ticketDetail.getTicketCode());
            ticket.setFileIds(ticketDetail.getFileIds());
        } else {
            ticket.setTicketTitle(work_order_title.getText().toString());
        }

        StringBuilder sb_fileStr = new StringBuilder();
        for (int i = 0; i < pic_list.size(); i++) {
            if (!TextUtils.isEmpty(pic_list.get(i).getFileUrl()) && !"addFile".equals(pic_list.get(i).getFileType())) {
                sb_fileStr.append(pic_list.get(i).getFileUrl()).append(";");
            }
        }

        String fileStr = sb_fileStr.toString();
        if (!TextUtils.isEmpty(fileStr) && ";".equals(fileStr.substring(fileStr.length() - 1))) {
            fileStr = fileStr.substring(0, fileStr.length() - 1);
        }

        if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_edit || isDisplayType == SobotConstantUtils.sobot_wo_display_type_new) {
            ticket.setTicketTitle(work_order_title.getText().toString());
            if (!TextUtils.isEmpty(userId)) {
                ticket.setUserName(userName);
                ticket.setCustomerId(userId);
            } else {
                ticket.setUserName(siv_work_order_to_user.getTextByTrim());
                if (!"-1".equals(siv_work_order_to_user.getValue())) {
                    ticket.setCustomerId(siv_work_order_to_user.getValue());
                }
            }
            ticket.setFileStr(fileStr);
            ticket.setTicketType(siv_work_order_customer_category.getValue());
            ticket.setTicketTypeName(siv_work_order_customer_category.getTextByTrim());

            ticket.setExtendFields(SobotGsonUtil.beanToJson(getCusFieldValList()));
            ticket.setTicketContent(et_work_order_describe.getText().toString().trim());
            ticket.setReplyContent(et_work_order_reply_describe.getText().toString().trim());
            StringBuilder replyFileStr = new StringBuilder();
            if (pic_list1 != null && pic_list1.size() != 0) {
                for (int i = 0; i < pic_list1.size(); i++) {
                    if (!TextUtils.isEmpty(pic_list1.get(i).getFileUrl()) && !"addFile".equals(pic_list1.get(i).getFileType())) {
                        replyFileStr.append(pic_list1.get(i).getFileUrl()).append(";");
                    }
                }
                String fileReplyStr = replyFileStr.toString();
                if (!TextUtils.isEmpty(fileReplyStr) && ";".equals(fileReplyStr.substring(fileReplyStr.length() - 1))) {
                    fileReplyStr = fileReplyStr.substring(0, fileReplyStr.length() - 1);
                }
                ticket.setReplyFileStr(fileReplyStr);
            }
        } else {
            ticket.setReplyFileStr(fileStr);
            ticket.setReplyContent(et_work_order_describe.getText().toString().trim());
            if (ticketDetail != null) {
                ticket.setTicketTitle(ticketDetail.getTicketTitle());
                ticket.setTicketContent(ticketDetail.getTicketContent());
            }
        }

        ticket.setTicketLevel(Integer.parseInt(siv_work_order_priority.getValue()));
        ticket.setTicketStatus(Integer.parseInt(siv_work_order_status.getValue()));

        if (!TextUtils.isEmpty(siv_work_order_copy_to.getValue()) &&
                !siv_work_order_copy_to.getValue().equals("-1")) {
            ticket.setCopyUserName(siv_work_order_copy_to.getTextByTrim());
            ticket.setCopyUser(siv_work_order_copy_to.getValue());
        }
        SobotLogUtils.d("================getTicket()==="+siv_work_order_ticket_template.getValue());
        if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
            if (!TextUtils.isEmpty(siv_work_order_ticket_template.getValue()) &&
                    !siv_work_order_ticket_template.getValue().equals("-1")) {
                ticket.setTemplateName(siv_work_order_ticket_template.getTextByTrim());
                SobotLogUtils.d("================setTemplateId===" + siv_work_order_ticket_template.getValue());
                ticket.setTemplateId(siv_work_order_ticket_template.getValue());
            }
        }else{
            //老版
//            ticket.setTemplateName();
//            ticket.setTemplateId();
        }

        if (!TextUtils.isEmpty(siv_work_order_customer_service_group.getValue()) &&
                !siv_work_order_customer_service_group.getValue().equals("-1")) {
            ticket.setDealGroupId(siv_work_order_customer_service_group.getValue());
            ticket.setDealGroupName(siv_work_order_customer_service_group.getTextByTrim());
        }
        if (!TextUtils.isEmpty(siv_work_order_customer_service.getValue()) &&
                !siv_work_order_customer_service.getValue().equals("-1")) {
            ticket.setDealUserId(siv_work_order_customer_service.getValue());
            ticket.setDealUserName(siv_work_order_customer_service.getTextByTrim());
        }

        return ticket;
    }

    /**
     * 根据当前页面上所填的值获取自定义字段的值集合
     *
     * @return
     */
    private List<SobotTicketCusFieldModel> getCusFieldValList() {
        List<SobotTicketCusFieldModel> cusFieldModelList = new ArrayList<>();
        SobotTicketCusFieldModel cusFieldModel;

        if (ticketDetail != null) {
            //需将不是本模板的内容也保存上
            List<SobotTicketResultListModel> resultList = ticketDetail.getResultList();
            SobotTicketCusFieldModel cusFieldModel2;
            if (resultList != null) {
                for (int i = 0; i < resultList.size(); i++) {
                    cusFieldModel2 = new SobotTicketCusFieldModel();
                    SobotTicketResultListModel tmpData = resultList.get(i);
                    if (!TextUtils.isEmpty(tmpData.getValue()) && tmpData.getFieldType() != 10) {
                        cusFieldModel2.setId(tmpData.getFieldId());
                        cusFieldModel2.setValue(tmpData.getValue());
                        cusFieldModelList.add(cusFieldModel2);
                    }
                }
            }
        }

        if (cusFieldList != null && cusFieldList.size() != 0) {
            for (int i = 0; i < cusFieldList.size(); i++) {
                cusFieldModel = new SobotTicketCusFieldModel();
                if (cusFieldList.get(i).getFieldType() == 6 || cusFieldList.get(i).getFieldType() == 7 || cusFieldList.get(i).getFieldType() == 8 || cusFieldList.get(i).getFieldType() == 9) {
                    if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldDataValue())) {
                        cusFieldModel.setId(cusFieldList.get(i).getFieldId());
                        cusFieldModel.setValue(cusFieldList.get(i).getFieldDataValue());
                        if (cusFieldList.get(i).getFieldType() == 9) {
                            cusFieldModel.setText(cusFieldList.get(i).getFieldValue());
                        }
                    } else {
                        continue;
                    }
                } else if (cusFieldList.get(i).getFieldType() == 10) {
                    List<List<SobotTicketCusFieldModelItem>> listModel = new ArrayList<>();
                    SobotCusFieldConfigModel cusFieldConfig = cusFieldList.get(i);
                    if (cusFieldConfig != null) {
                        cusFieldModel.setId(cusFieldConfig.getFieldId());
                        List<SobotCombinFormFieldListModel> combinFormFieldList = cusFieldConfig.getCombinFormFieldList();
                        if (combinFormFieldList != null && combinFormFieldList.size() > 0) {
                            String preId = "";
                            List<SobotTicketCusFieldModelItem> listItem = null;
                            for (int j = 0; j < combinFormFieldList.size(); j++) {
                                SobotCombinFormFieldListModel fieldList = combinFormFieldList.get(j);
                                if (fieldList != null && fieldList.getCombinFormField() != null && !TextUtils.isEmpty(fieldList.getCombinFormField().getTmpId()) && !preId.equals(fieldList.getCombinFormField().getTmpId())) {
                                    listItem = new ArrayList<>();
                                    preId = fieldList.getCombinFormField().getTmpId();
                                    SobotCombinFormFieldModel combinFormField = combinFormFieldList.get(j).getCombinFormField();
                                    if (combinFormField.getFieldType() == 6) {
                                        if (!TextUtils.isEmpty(combinFormField.getValue())) {
                                            SobotTicketCusFieldModelItem modelItem = new SobotTicketCusFieldModelItem();
                                            modelItem.setId(combinFormField.getFieldId());
                                            modelItem.setValue(combinFormField.getValue());
                                            modelItem.setText(combinFormField.getText());
                                            listItem.add(modelItem);
                                        }
                                    } else {
                                        if (!TextUtils.isEmpty(combinFormField.getValue())) {
                                            SobotTicketCusFieldModelItem modelItem = new SobotTicketCusFieldModelItem();
                                            modelItem.setId(combinFormField.getFieldId());
                                            modelItem.setValue(combinFormField.getValue());
                                            modelItem.setText(combinFormField.getText());
                                            listItem.add(modelItem);
                                        }
                                    }

                                    listModel.add(listItem);
                                } else {
                                    if (listItem != null) {
                                        SobotCombinFormFieldModel combinFormField = combinFormFieldList.get(j).getCombinFormField();
                                        if (combinFormField.getFieldType() == 6) {
                                            if (!TextUtils.isEmpty(combinFormField.getValue())) {
                                                SobotTicketCusFieldModelItem modelItem = new SobotTicketCusFieldModelItem();
                                                modelItem.setId(combinFormField.getFieldId());
                                                modelItem.setText(combinFormField.getText());
                                                modelItem.setValue(combinFormField.getValue());
                                                listItem.add(modelItem);
                                            }
                                        } else {
                                            if (!TextUtils.isEmpty(combinFormField.getValue())) {
                                                SobotTicketCusFieldModelItem modelItem = new SobotTicketCusFieldModelItem();
                                                modelItem.setId(combinFormField.getFieldId());
                                                modelItem.setValue(combinFormField.getValue());
                                                modelItem.setText(combinFormField.getText());
                                                listItem.add(modelItem);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (listModel.size() > 0) {
                            cusFieldModel.setValue(listModel);
                            if (cusFieldConfig.getComputedUnit() != 0) {
                                cusFieldModel.setSummary(cusFieldConfig.getSummy());
                            }
                        }
                    }

                } else {
                    if (!TextUtils.isEmpty(cusFieldList.get(i).getFieldValue())) {
                        cusFieldModel.setId(cusFieldList.get(i).getFieldId());
                        cusFieldModel.setValue(cusFieldList.get(i).getFieldValue());
                    } else {
                        continue;
                    }
                }

                String s = cusFieldList.get(i).getFieldId();
                for (int j = 0; j < cusFieldModelList.size(); j++) {
                    SobotTicketCusFieldModel cusFieldModel1 = cusFieldModelList.get(j);
                    if (!TextUtils.isEmpty(cusFieldModel1.getId()) && cusFieldModel1.getId().equals(s)) {
                        cusFieldModelList.remove(cusFieldModel1);
                    }
                }
                cusFieldModelList.add(cusFieldModel);
            }
        }
        for (int i = 0; i < cusFieldModelList.size(); i++) {
            if (cusFieldModelList.get(i).getValue() == null) {
                cusFieldModelList.remove(i);
            }
        }
        return cusFieldModelList;
    }

    private boolean checkResult() {
        String describe = et_work_order_describe.getText().toString().trim();
        String replyDescribe = et_work_order_reply_describe.getText().toString().trim();
        String orderTitle = work_order_title.getText().toString().trim();

        if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_reply) {
//            if (TextUtils.isEmpty(describe)) {
//                ToastUtil.showToast(this, "工单回复内容不能为空");
//                return false;
//            }
        } else if (isDisplayType == SobotConstantUtils.sobot_wo_display_type_edit) {
//            if (TextUtils.isEmpty(replyDescribe)) {
//                ToastUtil.showToast(this, "工单编辑回复内容不能为空");
//                return false;
//            }
        } else {
            if (TextUtils.isEmpty(orderTitle)) {
                SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot_title_cannot_be_blank_string"));
                return false;
            }
            if (TextUtils.isEmpty(describe)) {
                SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot__description_cannot_empty_string"));
                return false;
            }
            if (siv_work_order_customer_category.isEmpty()) {
                SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot_category_cannot_empty_string"));
                return false;
            }
            if (siv_work_order_to_user.isEmpty()) {
                SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot_work_order_user_cannt_empty_string"));
                return false;
            }
            if (siv_work_order_customer_service.getValue().equals("-1") && siv_work_order_status.getTextByTrim().equals(getResString("sobot_wo_item_state_doing_string"))) {
                SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot_select_customer_service_string"));
                return false;
            }
            //新版工单，模板必填
            if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() > 0) {
                if (siv_work_order_ticket_template.getValue().equals("-1") && siv_work_order_ticket_template.getTextByTrim().equals(getResString("sobot_select_work_order_ticket_template_string"))) {
                    SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot_select_work_order_ticket_template_tips_string"));
                    return false;
                }
            }
        }

        if (isDisplayType != SobotConstantUtils.sobot_wo_display_type_reply) {
            if (!checkCusFieldRequired()) {
                return false;
            }
        }
        return true;
    }

    public static <T> List<T> deepCopy(List<T> src) {
        List<T> dest = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (List<T>) in.readObject();
        } catch (Exception e) {
            //ignore
        }
        return dest;
    }

    protected abstract class StopProgressDialogCallBack<T> implements SobotResultCallBack<SobotWOBaseCode<T>> {
        @Override
        public void onSuccess(SobotWOBaseCode<T> result) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            if (result != null) {
                if (RESULT_SUCCESS_CODE_SECOND.equals(result.getCode())) {// 查询成功
                    onSuccessed(result.getData());
                } else if ("999999".equals(result.getCode())) {
                    SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot_unknown_exception_string"));
                } else if ("200027".equals(result.getCode())) {
                    SobotToastUtil.showCustomToast(getSobotBaseContext(), getResString("sobot_not_chaoguo_five_string"));
                } else if ("999998".equals(result.getRetCode())) {
                    onEmpty();
                    Intent intent = new Intent(getSobotBaseActivity(), SobotKickedActivity.class);
                    intent.putExtra("isTokenfailure", true);
                    startActivity(intent);
                } else {
                    String msg = result.getMsg();
                    if (TextUtils.isEmpty(msg)) {
                        msg = result.getRetMsg();
                    }

                    if (onEmpty() && !TextUtils.isEmpty(msg)) {
                        SobotToastUtil.showCustomToast(getSobotBaseContext(), msg);
                    }
                }
            }
        }

        public abstract void onSuccessed(T t);

        public boolean onEmpty() {
            return true;
        }

        ;

        @Override
        public void onFailure(Exception e, String des) {
            SobotDialogUtils.stopProgressDialog(SobotWOCreateActivity.this);
            SobotToastUtil.showCustomToast(getSobotBaseActivity(), getResString("sobot_network_error_string"));
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            SobotLogUtils.i("多媒体返回的结果：" + requestCode + "--" + resultCode + "--" + data);

            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CODE_PICTURE) { // 发送本地图片
                    if (data != null && data.getData() != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage == null) {
                            selectedImage = SobotImageUtils.getUri(data, getBaseContext());
                        }
                        String path = SobotImageUtils.getPath(getSobotBaseActivity(), selectedImage);
                        SobotLogUtils.i("选取的图片地址：" + path);
                        SobotDialogUtils.startProgressDialog(SobotWOCreateActivity.this);
                        SobotOrderImageUtils.sendPicByUri(SobotWOCreateActivity.this, fileNumKey1,
                                getSobotBaseContext(), selectedImage, zhiChiApi, sendFileRequest);
                    }
                } else if (requestCode == REQUEST_CODE_MAKEPICTUREFROMCAMERA) {//拍照
                    if (cameraFile != null && cameraFile.exists()) {
                        SobotLogUtils.i("相机拍照：" + cameraFile.toString());
                        SobotDialogUtils.startProgressDialog(SobotWOCreateActivity.this);
                        SobotOrderImageUtils.sendPicLimitBySize(SobotWOCreateActivity.this, fileNumKey1,
                                cameraFile.getPath(), getSobotBaseContext(), zhiChiApi, sendFileRequest);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data != null) {
            if ("CATEGORYSMALL".equals(data.getStringExtra("CATEGORYSMALL")) && mCusFieldConfig != null) {
                if (-1 != data.getIntExtra("fieldType", -1) && SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE == data.getIntExtra("fieldType", -1)) {
                    String value = data.getStringExtra("category_typeName");
                    String dataValue = data.getStringExtra("category_typeValue");
                    if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(dataValue)) {
                        for (int i = 0; i < cusFieldList.size(); i++) {
                            if (mCusFieldConfig.getFieldId().equals(cusFieldList.get(i).getFieldId())) {
                                cusFieldList.get(i).setFieldValue(value.substring(0, value.length() - 1));
                                cusFieldList.get(i).setFieldDataValue(dataValue.substring(0, dataValue.length() - 1));
                                View view = work_order_customer_field_list_layout.findViewWithTag(cusFieldList.get(i).getFieldId());
                                TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                                textClick.setText(cusFieldList.get(i).getFieldValue());
                                break;
                            }
                        }
                    }
                } else {
                    if (mCusFieldConfig.getFieldId().equals(data.getStringExtra("category_fieldId"))) {
                        for (int i = 0; i < cusFieldList.size(); i++) {
                            if (mCusFieldConfig.getFieldId().equals(cusFieldList.get(i).getFieldId())) {
                                cusFieldList.get(i).setFieldValue(data.getStringExtra("category_typeName"));
                                cusFieldList.get(i).setFieldDataValue(data.getStringExtra("category_typeValue"));
                                View view = work_order_customer_field_list_layout.findViewWithTag(cusFieldList.get(i).getFieldId());
                                TextView textClick = (TextView) view.findViewById(R.id.work_order_customer_date_text_click);
                                textClick.setText(cusFieldList.get(i).getFieldValue());
                                break;
                            }
                        }
                    }
                }
            } else {
                switch (requestCode) {
                    case SobotConstantUtils.sobot_wo_list_display_type_service_group://受理客服组
                        if (data.getExtras() != null) {
                            SobotCommonItemModel selectedItem_service_group = (SobotCommonItemModel) data.getExtras()
                                    .getSerializable(SobotConstantUtils.sobot_wo_list_selected_item);
                            if (selectedItem_service_group != null && !"-1".equals(selectedItem_service_group.getItemValue())) {
                                siv_work_order_customer_service_group.setText(selectedItem_service_group.getItemKey());
                                siv_work_order_customer_service_group.setValue(selectedItem_service_group.getItemValue());
                                siv_work_order_customer_service.reset();
                            } else {
                                siv_work_order_customer_service_group.reset();
                            }
                        }
                        break;
                    case SobotConstantUtils.sobot_wo_list_display_type_service://受理客服
                        if (data.getExtras() != null) {
                            SobotCommonItemModel selectedItem_service = (SobotCommonItemModel) data.getExtras()
                                    .getSerializable(SobotConstantUtils.sobot_wo_list_selected_item);
                            if (selectedItem_service != null && !"-1".equals(selectedItem_service.getItemValue())) {
                                siv_work_order_customer_service.setText(selectedItem_service.getItemKey());
                                siv_work_order_customer_service.setValue(selectedItem_service.getItemValue());
                            } else {
                                siv_work_order_customer_service.reset();
                            }
                        }
                        break;
                    case SobotConstantUtils.sobot_wo_deleteAppFileSuccess://图片预览
                        if (data.getExtras() != null) {
                            List<SobotUploadFileModelResult> tmpList = (List<SobotUploadFileModelResult>) data.getExtras().getSerializable(SobotConstantUtils.sobot_wo_pic_list);
                            if (tmpList != null) {
                                pic_list.clear();
                                pic_list.addAll(tmpList);
                                adapter.addDatas(pic_list);
                            }
                        }
                        break;
                    case SobotConstantUtils.sobot_wo_deleteAppFileSuccess_reply://回复  的   图片预览
                        if (data.getExtras() != null) {
                            List<SobotUploadFileModelResult> tmpList1 = (List<SobotUploadFileModelResult>) data.getExtras().getSerializable(SobotConstantUtils.sobot_wo_pic_list);
                            if (tmpList1 != null) {
                                pic_list.clear();
                                pic_list.addAll(tmpList1);
                                adapter.addDatas(pic_list);
                            }
                        }
                        break;
                    case SobotConstantUtils.sobot_wo_list_display_type_service_copy://选择抄送客服
                        if (data.getExtras() != null) {
                            SobotCommonItemModel selectedItem_service_copy = (SobotCommonItemModel) data.getExtras()
                                    .getSerializable(SobotConstantUtils.sobot_wo_list_selected_item);
                            if (selectedItem_service_copy != null && !"-1".equals(selectedItem_service_copy.getItemValue())) {
                                siv_work_order_copy_to.setText(selectedItem_service_copy.getItemKey());
                                siv_work_order_copy_to.setValue(selectedItem_service_copy.getItemValue());
                                SobotLogUtils.i("名称----" + selectedItem_service_copy.getItemKey() + "----------id---------" + selectedItem_service_copy.getItemValue());
                            } else {
                                siv_work_order_copy_to.reset();
                            }
                        }
                        break;
                    case SobotConstantUtils.sobot_wo_list_display_type_category:
                        siv_work_order_customer_category.setText(data.getStringExtra("category_typeName"));
                        final String category_typeId = data.getStringExtra("category_typeId");
                        siv_work_order_customer_category.setValue(data.getStringExtra("category_typeId"));
                        if (sobotServiceVoModel != null && sobotServiceVoModel.getNewBossSwitch() < 1) {
                            loadCusField(category_typeId);
                        }
                        break;
                    case SobotConstantUtils.sobot_wo_list_display_type_user_center:
                        SobotLogUtils.i("userId---" + data.getStringExtra("userId") + "-----userName------" + data.getStringExtra("userName"));
                        siv_work_order_to_user.setText(data.getStringExtra("userName"));
                        siv_work_order_to_user.setValue(data.getStringExtra("userId"));
                        break;
                    case SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_SPINNER_TYPE:
                        SobotLogUtils.i("userId---" + data.getStringExtra("userId") + "-----userName------" + data.getStringExtra("userName"));
                        siv_work_order_to_user.setText(data.getStringExtra("userName"));
                        siv_work_order_to_user.setValue(data.getStringExtra("userId"));
                        break;
                    case SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_RADIO_TYPE:
                        SobotLogUtils.i("userId---" + data.getStringExtra("userId") + "-----userName------" + data.getStringExtra("userName"));
                        siv_work_order_to_user.setText(data.getStringExtra("userName"));
                        siv_work_order_to_user.setValue(data.getStringExtra("userId"));
                        break;
                    case SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CHECKBOX_TYPE:
                        SobotLogUtils.i("userId---" + data.getStringExtra("userId") + "-----userName------" + data.getStringExtra("userName"));
                        siv_work_order_to_user.setText(data.getStringExtra("userName"));
                        siv_work_order_to_user.setValue(data.getStringExtra("userId"));
                        break;

                    case SobotConstantUtils.sobot_wo_create_type_group_field:
                        if (SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_GROUP_TYPE == data.getIntExtra("fieldType", -1)) {
                            String value = data.getStringExtra("category_typeName");
                            String dataValue = data.getStringExtra("category_typeValue");
                            Bundle bundle = data.getExtras();
                            SobotCombinFormFieldModel combinFormFieldModel = null;
                            if (bundle != null && bundle.getSerializable("formField") != null) {
                                combinFormFieldModel = (SobotCombinFormFieldModel) bundle.getSerializable("formField");
                            }
                            //处理自定义组合字段 中级联类型 返回值
                            if (mCusFieldConfig != null && combinFormFieldModel != null) {
                                List<SobotCombinFormFieldListModel> combinFormFieldList = mCusFieldConfig.getCombinFormFieldList();
                                for (int i = 0; i < combinFormFieldList.size(); i++) {
                                    SobotCombinFormFieldModel tmpField = combinFormFieldList.get(i).getCombinFormField();
                                    if (tmpField.getTmpId().equals(combinFormFieldModel.getTmpId()) && tmpField.getFieldType() == SobotConstantUtils.SOBOT_WO_CUSTOMER_FIELD_CASCADE_TYPE && combinFormFieldModel.getFieldId().equals(tmpField.getFieldId())) {
                                        tmpField.setValue(dataValue);
                                        tmpField.setTmpValue(dataValue);
                                        tmpField.setText(value);
                                    }
                                }

                                if (mAdapter != null) {
                                    mAdapter.notifyDataSetChanged();
                                }

                            }
                        } else {
                            SobotListTypeModel eventMsgModel = (SobotListTypeModel) data.getSerializableExtra("EventMsgModel");
                            if (eventMsgModel != null) {
                                showGroupFieldResult(eventMsgModel);
                            }
                        }
                        break;
                    case SobotConstantUtils.sobot_wo_list_ticket_template://工单模版
                        if (data.getExtras() != null) {
                            SobotCommonItemModel selectedItem_ticket_template = (SobotCommonItemModel) data.getExtras()
                                    .getSerializable(SobotConstantUtils.sobot_wo_list_selected_item);
                            SobotLogUtils.d("================onActivityResult==="+selectedItem_ticket_template.getItemValue());
                            if (selectedItem_ticket_template != null && !"-1".equals(selectedItem_ticket_template.getItemValue())) {
                                siv_work_order_ticket_template.setText(selectedItem_ticket_template.getItemKey());
                                siv_work_order_ticket_template.setValue(selectedItem_ticket_template.getItemValue());
                                loadCusFieldByTemplateId(selectedItem_ticket_template.getItemValue());
                            } else {
                                siv_work_order_ticket_template.reset();
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 根据工单分类获取关联的自定义字段
     *
     * @param ticketTypeId
     */
    private void loadCusField(String ticketTypeId) {
        //获取自定义字段列表
        zhiChiApi.queryAppTemplateFiledInfoByTypeId(SobotWOCreateActivity.this, ticketTypeId, new SobotResultCallBack<List<SobotCusFieldConfigModel>>() {
            @Override
            public void onSuccess(List<SobotCusFieldConfigModel> cusFieldConfigList) {
                if (cusFieldConfigList != null && cusFieldConfigList.size() != 0) {
                    work_order_customer_field_list_layout.setVisibility(View.VISIBLE);
                    siv_work_order_customer_category_below_line.setVisibility(View.VISIBLE);
                    cusFieldList.clear();
                    for (int i = 0; i < cusFieldConfigList.size(); i++) {
                        //不显示，不可显示字段
                        if (cusFieldConfigList.get(i).getAuthStatus() != 3) {
                            cusFieldList.add(cusFieldConfigList.get(i));
                        }
                    }
                    //回显之前模板相同字段
                    redisplayByCacheData(cusFieldList);
                    addWorkOrderCusFields(SobotWOCreateActivity.this, isDisplayType, null, cusFieldList, work_order_customer_field_list_layout);
                } else {
                    work_order_customer_field_list_layout.setVisibility(View.GONE);
                    siv_work_order_customer_category_below_line.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : des, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

            }
        });
    }

    /**
     * 根据工单模版id获取关联的自定义字段
     *
     * @param templateId
     */
    private void loadCusFieldByTemplateId(String templateId) {
        //获取自定义字段列表
        zhiChiApi.queryAppTemplateFiledInfoByTemplateId(SobotWOCreateActivity.this, templateId, new SobotResultCallBack<List<SobotCusFieldConfigModel>>() {
            @Override
            public void onSuccess(List<SobotCusFieldConfigModel> cusFieldConfigList) {
                if (cusFieldConfigList != null && cusFieldConfigList.size() != 0) {
                    work_order_customer_field_list_layout.setVisibility(View.VISIBLE);
                    siv_work_order_customer_category_below_line.setVisibility(View.VISIBLE);
                    cusFieldList.clear();
                    for (int i = 0; i < cusFieldConfigList.size(); i++) {
                        //不显示，不可显示字段
                        if (cusFieldConfigList.get(i).getAuthStatus() != 3) {
                            cusFieldList.add(cusFieldConfigList.get(i));
                        }
                    }
                    //回显之前模板相同字段
                    redisplayByCacheData(cusFieldList);
                    addWorkOrderCusFields(SobotWOCreateActivity.this, isDisplayType, null, cusFieldList, work_order_customer_field_list_layout);
                } else {
                    work_order_customer_field_list_layout.setVisibility(View.GONE);
                    siv_work_order_customer_category_below_line.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotToastUtil.showCustomToast(getApplicationContext(), SobotStringUtils.isEmpty(des) ? SobotResourceUtils.getResString(getBaseContext(), "sobot_wo_net_error_string") : des, SobotResourceUtils.getDrawableId(getBaseContext(), "sobot_icon_warning_attention"));

            }
        });
    }

    //组合字段类型为下拉列表时
    public void showGroupFieldResult(SobotListTypeModel msgModel) {
        if (msgModel != null && mCusFieldConfig != null) {
            List<SobotCombinFormFieldListModel> combinFormFieldList = mCusFieldConfig.getCombinFormFieldList();
            List<SobotGroupFieldItemModel> groupFieldLists = (List<SobotGroupFieldItemModel>) msgModel.getList();
            if (groupFieldLists != null && groupFieldLists.size() > 0) {
                mCusFieldConfig.getCombinFormFieldList().get(posi).setGroupFieldItemModels(groupFieldLists);
                SobotCombinFormFieldListModel fieldList = mCusFieldConfig.getCombinFormFieldList().get(posi);
                if (fieldList != null) {
                    List<SobotGroupFieldItemModel> models = fieldList.getGroupFieldItemModels();
                    SobotCombinFormFieldModel formField = fieldList.getCombinFormField();
                    if (models != null && models.size() > 0) {
                        for (int i = 0; i < models.size(); i++) {
                            models.get(i).setChecked(false);
                            models.get(i).setTmpData(formField.getTmpId());
                        }
                        models.get(msgModel.getPosition()).setChecked(true);
                        formField.setText(models.get(msgModel.getPosition()).getText());
                        formField.setTmpValue(models.get(msgModel.getPosition()).getId());
                        formField.setValue(models.get(msgModel.getPosition()).getId());
                        formField.setChangeValue(msgModel.getChangeValue());

                        for (int i = 0; i < combinFormFieldList.size(); i++) {
                            SobotCombinFormFieldModel tmpField = combinFormFieldList.get(i).getCombinFormField();
                            if (tmpField.getTmpId().equals(formField.getTmpId()) && formField.getFieldLevel() == 2) {
                                if (tmpField.getTmpId().equals(formField.getTmpId()) && tmpField.getFieldLevel() == 3) {
                                    tmpField.setText("");
                                    tmpField.setTmpValue("");
                                    tmpField.setValue("");

                                    for (int j = 0; j < combinFormFieldList.size(); j++) {
                                        SobotCombinFormFieldModel tmpformField = combinFormFieldList.get(j).getCombinFormField();
                                        if (tmpformField != null && tmpformField.getFieldType() == 5 && tmpformField.getRelatedField().equals(tmpField.getFieldId()) && tmpField.getTmpId().equals(tmpformField.getTmpId())) {
                                            tmpformField.setText("");
                                            tmpformField.setTmpValue("");
                                            tmpformField.setValue("");
                                        }
                                    }
                                }

                                if (tmpField.getTmpId().equals(formField.getTmpId()) && tmpField.getRelatedField().equals(formField.getFieldId())) {
                                    tmpField.setText("");
                                    tmpField.setTmpValue("");
                                    tmpField.setValue("");
                                }
                            }

                            if (tmpField.getTmpId().equals(formField.getTmpId()) && tmpField.getFieldType() == 5 && formField.getFieldId().equals(tmpField.getRelatedField())) {
                                tmpField.setValue(formField.getChangeValue());
                                tmpField.setTmpValue(formField.getChangeValue());
                                tmpField.setText(formField.getChangeValue());
                            }
                        }
                    }
                }
            }

            if (flag && combinFormFieldList != null && combinFormFieldList.size() > 0) {
                for (int i = 0; i < combinFormFieldList.size(); i++) {
                    SobotCombinFormFieldModel field = combinFormFieldList.get(i).getCombinFormField();
                    List<SobotCusFieldDataInfoListModel> infoList = combinFormFieldList.get(i).getCusFieldDataInfoList();

                    if (field != null && !TextUtils.isEmpty(field.getTmpId()) && !TextUtils.isEmpty(field.getFieldId()) &&
                            field.getFieldId().equals(msgModel.getFieldId()) && field.getTmpId().equals(msgModel.getTmpId()) && infoList != null && infoList.size() > 0) {
                        for (int k = 0; k < infoList.size(); k++) {
                            SobotCusFieldDataInfoListModel dataInfo = infoList.get(k);
                            if (dataInfo.isChecked() && dataInfo.getDataId().equals(msgModel.getFieldValueId())) {
                                break;
                            } else {
                                dataInfo.setChecked(false);
                            }
                            infoList.get(k).setChecked(false);
                        }

                        infoList.get(msgModel.getPosition()).setChecked(true);
                        field.setText(infoList.get(msgModel.getPosition()).getDataName());
                        field.setTmpValue(infoList.get(msgModel.getPosition()).getDataValue());
                        field.setValue(infoList.get(msgModel.getPosition()).getDataValue());
                        field.setChangeValue(msgModel.getChangeValue());

                        for (int j = 0; j < combinFormFieldList.size(); j++) {
                            SobotCombinFormFieldModel tmpField = combinFormFieldList.get(j).getCombinFormField();
                            if (field.getFieldLevel() == 1) {
                                if (TextUtils.isEmpty(field.getRelatedField()) && field.getFieldId().equals(tmpField.getRelatedField())) {
                                    if (field.getTmpId().equals(tmpField.getTmpId()) && !TextUtils.isEmpty(tmpField.getRelatedField()) && (tmpField.getFieldLevel() == 2 || tmpField.getFieldLevel() == 3)) {
                                        tmpField.setText("");
                                        tmpField.setTmpValue("");
                                        tmpField.setValue("");

                                        for (int k = 0; k < combinFormFieldList.size(); k++) {
                                            SobotCombinFormFieldModel tmpformField = combinFormFieldList.get(k).getCombinFormField();
                                            if (tmpformField != null && tmpformField.getFieldType() == 5 && tmpField.getTmpId().equals(tmpformField.getTmpId())) {
                                                tmpformField.setText("");
                                                tmpformField.setTmpValue("");
                                                tmpformField.setValue("");
                                            }

                                            if (tmpformField != null && (tmpField.getFieldLevel() == 2 || tmpField.getFieldLevel() == 3)) {

                                                if (tmpField.getTmpId().equals(tmpformField.getTmpId()) && tmpField.getFieldId().equals(tmpformField.getRelatedField())) {
                                                    tmpformField.setText("");
                                                    tmpformField.setTmpValue("");
                                                    tmpformField.setValue("");
                                                }
                                            }
                                        }
                                    }
                                    if (tmpField.getFieldType() == 5 && field.getTmpId().equals(tmpField.getTmpId()) && field.getFieldId().equals(tmpField.getRelatedField())) {
                                        tmpField.setValue(field.getChangeValue());
                                        tmpField.setTmpValue(field.getChangeValue());
                                        tmpField.setText(field.getChangeValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    //组合字段类型为数值类型时触发，要计算
    public void total(SobotListTypeModel msgModel,SobotCusFieldConfigModel mCusFieldConfig,TextView mSummyTv) {
        if (msgModel != null && mCusFieldConfig != null) {
            List<SobotCombinFormFieldListModel> fieldList = mCusFieldConfig.getCombinFormFieldList();

            List<DataList> dataList = new ArrayList<>();
            dataList.clear();
            if (fieldList != null && fieldList.size() > 0 && (mCusFieldConfig.getComputedUnit() == 1 || mCusFieldConfig.getComputedUnit() == 2)) {
                SobotCombinFormFieldListModel fieldList1;
                SobotCombinFormFieldModel formField;
                SobotCombinFormFieldModel formField1 = null;
                SobotCombinFormFieldModel formField2 = null;
                DataList dataItem;
                for (int j = 0; j < fieldList.size(); j++) {
                    fieldList1 = fieldList.get(j);
                    if (fieldList1 != null) {
                        formField = fieldList1.getCombinFormField();
                        if (formField != null) {
                            dataItem = new DataList();
                            if (!TextUtils.isEmpty(formField.getFieldId()) && formField.getFieldId().equals(mCusFieldConfig.getOperateUnitId1())) {
                                formField1 = formField;
                                dataItem.setTmpId(formField1.getTmpId());
                                dataItem.setFieldId(formField1.getFieldId());
                                if (!TextUtils.isEmpty(formField1.getValue())) {
                                    dataItem.setNum1(Double.parseDouble(formField1.getValue()));
                                } else {
                                    dataItem.setNum1(0);
                                }
                            }

                            if (!TextUtils.isEmpty(formField.getFieldId()) && formField.getFieldId().equals(mCusFieldConfig.getOperateUnitId2())) {
                                formField2 = formField;
                                dataItem.setTmpId(formField2.getTmpId());
                                dataItem.setFieldId(formField2.getFieldId());
                                if (!TextUtils.isEmpty(formField2.getValue())) {
                                    dataItem.setNum1(Double.parseDouble(formField2.getValue()));
                                } else {
                                    dataItem.setNum1(0);
                                }
                            }
                            dataList.add(dataItem);
                        }
                    }
                }

                if (dataList.size() > 0) {
                    for (int i = dataList.size() - 1; i >= 0; i--) {
                        DataList itemData = dataList.get(i);
                        if (itemData != null && TextUtils.isEmpty(itemData.getTmpId()) && TextUtils.isEmpty(itemData.getFieldId())) {
                            dataList.remove(i);
                        }
                    }
                }

                if (mCusFieldConfig.getComputedUnit() == 1 && formField1 != null && formField2 != null && !TextUtils.isEmpty(formField1.getFieldName()) && !TextUtils.isEmpty(formField2.getFieldName())) {
                    if (dataList.size() > 0) {
                        double count = 0;
                        for (int i = 0; i < dataList.size(); i++) {
                            count = count + dataList.get(i).getNum1();
                        }
                        if (mSummyTv != null) {
                            mSummyTv.setText("【" + formField1.getFieldName() + "加" + formField2.getFieldName() + "】合计: " + String.format("%.2f", count));
                            mSummyTv.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (mCusFieldConfig.getComputedUnit() == 2 && formField1 != null && formField2 != null && !TextUtils.isEmpty(formField1.getFieldName()) && !TextUtils.isEmpty(formField2.getFieldName())) {
                    if (dataList.size() > 0) {
                        double count = 0;
                        for (int i = 0; i < dataList.size(); ) {
                            count = count + dataList.get(i).getNum1() * dataList.get(i + 1).getNum1();
                            i = i + 2;
                        }
                        if (mSummyTv != null) {
                            mSummyTv.setText("【" + formField1.getFieldName() + "乘" + formField2.getFieldName() + "】合计: " + String.format("%.2f", count));
                            mSummyTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else if (fieldList != null && fieldList.size() > 0 && !TextUtils.isEmpty(mCusFieldConfig.getOperateUnitId1()) && TextUtils.isEmpty(mCusFieldConfig.getOperateUnitId2())) {
                SobotCombinFormFieldListModel fieldList1;
                SobotCombinFormFieldModel formField;
                SobotCombinFormFieldModel formField1 = null;
                DataList dataItem;
                for (int j = 0; j < fieldList.size(); j++) {
                    fieldList1 = fieldList.get(j);
                    if (fieldList1 != null) {
                        formField = fieldList1.getCombinFormField();
                        if (formField != null) {
                            dataItem = new DataList();
                            if (!TextUtils.isEmpty(formField.getFieldId()) && formField.getFieldId().equals(mCusFieldConfig.getOperateUnitId1())) {
                                formField1 = formField;
                                dataItem.setTmpId(formField1.getTmpId());
                                dataItem.setFieldId(formField1.getFieldId());
                                if (!TextUtils.isEmpty(formField1.getValue())) {
                                    dataItem.setNum1(Double.parseDouble(formField1.getValue()));
                                } else {
                                    dataItem.setNum1(0);
                                }
                            }
                            dataList.add(dataItem);
                        }
                    }
                }

                if (dataList.size() > 0) {
                    for (int i = dataList.size() - 1; i >= 0; i--) {
                        DataList itemData = dataList.get(i);
                        if (itemData != null && TextUtils.isEmpty(itemData.getTmpId()) && TextUtils.isEmpty(itemData.getFieldId())) {
                            dataList.remove(i);
                        }
                    }
                }

                if (formField1 != null && dataList.size() > 0) {
                    double count = 0;
                    for (int i = 0; i < dataList.size(); i++) {
                        count = count + dataList.get(i).getNum1();
                    }
                    if (mSummyTv != null) {
                        mSummyTv.setText("【" + formField1.getFieldName() + "】合计: " + String.format("%.2f", count));
                        mSummyTv.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (mSummyTv != null) {
                mCusFieldConfig.setSummy(mSummyTv.getText().toString());
            }
        }
    }

    class DataList {
        double num1 = 0;
        String tmpId;
        String fieldId;

        public String getFieldId() {
            return fieldId;
        }

        public void setFieldId(String fieldId) {
            this.fieldId = fieldId;
        }

        double getNum1() {
            return num1;
        }

        void setNum1(double num1) {
            this.num1 = num1;
        }

        public String getTmpId() {
            return tmpId;
        }

        public void setTmpId(String tmpId) {
            this.tmpId = tmpId;
        }
    }


    public boolean onEmpty() {
        return true;
    }
}