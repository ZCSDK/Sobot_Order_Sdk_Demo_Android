package com.sobot.workorder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sobot.album.AlbumConfig;
import com.sobot.album.SobotAlbum;
import com.sobot.album.SobotMediaLoader;
import com.sobot.common.login.SobotLoginTools;
import com.sobot.common.login.callback.SobotResultBlock;
import com.sobot.common.login.callback.SobotResultCode;
import com.sobot.common.login.model.SobotServiceInfoModel;
import com.sobot.common.utils.SobotCommonApi;
import com.sobot.utils.SobotLogUtils;
import com.sobot.widget.SobotWidgetApi;
import com.sobot.widget.ui.SobotMarkConfig;
import com.sobot.workorder.activity.SobotWOClassificationActivity;
import com.sobot.workorder.activity.SobotWOCreateActivity;
import com.sobot.workorder.activity.SobotWODetailActivity;
import com.sobot.workorder.activity.SobotWOSearchActivity;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorderlibrary.api.apiutils.SobotOrderBaseUrl;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.utils.SobotWOSPUtil;

import java.util.Locale;
import java.util.Map;


/**
 * 接口输出类
 */
public class SobotOrderApi {

    private static String Tag = SobotOrderApi.class.getSimpleName();

    /**
     * 初始化SDK，设置域名和认证token
     *
     * @param application
     * @param host        可以为空，默认阿里云服务；如果需要，请设置自己的域名
     */
    public static void initWithHost(Application application, String host) {
        if (application == null) {
            SobotLogUtils.e("initWithHost 参数 application不能为空");
            return;
        }
        if (!TextUtils.isEmpty(host)) {
            SobotOrderBaseUrl.setApi_Host(host);
        }
        //清空语言设置
        SobotWOSPUtil.getInstance(application).clearAll();
        SobotCommonApi.init(application, SobotOrderBaseUrl.getApi_Host());
        SobotWidgetApi.setSwitchMarkStatus(SobotMarkConfig.SHOW_PERMISSION_TIPS_POP, true);
        SobotAlbum.initialize(AlbumConfig.newBuilder(application)
                .setAlbumLoader(new SobotMediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }


    /**
     * 通过邮箱密码方式启动工单系统，进入首页
     *
     * @param context
     * @param loginPwd 客服邮箱
     * @param loginPwd 登录密码
     * @param block    回调结果 可为空
     */
    public static void startWithAcount(Context context, String loginUser, String loginPwd, SobotResultBlock block) {
        loginUser(context, loginUser, loginPwd, true, block);
    }

    /**
     * 通过设置token方式启动工单系统，进入首页
     *
     * @param context
     * @param block   回调结果 可为空
     */
    public static void startWithToken(final Context context, String loginToken, SobotResultBlock block) {
        loginUser(context, loginToken, true, block);
    }

    /**
     * 打开搜索工单页面
     */
    public static void openSearchWorkOrder(final Context context, SobotResultBlock block) {
        boolean isLogin = SobotLoginTools.getInstance().isLogin();
        if (!isLogin) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, context.getResources().getString(R.string.sobot_expired_login_again), null);
        } else {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
            Intent createIntent = new Intent(context, SobotWOSearchActivity.class);
            context.startActivity(createIntent);
        }
    }

    /**
     * 打开创建工单页面
     *
     * @param context
     * @param param   可为空，也可添加参数 userName 用户昵称 userId 用户id
     * @param block   回调结果 可为空
     */
    public static void openCreateWorkOrder(final Context context, Map<String, Object> param, SobotResultBlock block) {
        boolean isLogin = SobotLoginTools.getInstance().isLogin();
        if (!isLogin) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, context.getResources().getString(R.string.sobot_expired_login_again), null);
        } else {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
            Intent createIntent = new Intent(context, SobotWOCreateActivity.class);
            if (param != null) {
                for (String key : param.keySet()) {
                    Object value = param.get(key);
                    if (value instanceof Boolean) {
                        createIntent.putExtra(key, (Boolean) value);
                    } else if (value instanceof String) {
                        createIntent.putExtra(key, (String) value);
                    } else if (value instanceof Integer) {
                        createIntent.putExtra(key, (Integer) value);
                    }
                }
            }
            context.startActivity(createIntent);
        }
    }

    /**
     * 进入工单详情页
     *
     * @param context
     * @param orderId 工单id
     * @param block   回调结果 可为空
     */
    public static void openOrderDetail(Context context, String orderId, SobotResultBlock block) {
        boolean isLogin = SobotLoginTools.getInstance().isLogin();
        if (!isLogin) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, context.getResources().getString(R.string.sobot_expired_login_again), null);
        } else {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
            Intent intent = new Intent(context, SobotWODetailActivity.class);
            intent.putExtra(SobotConstantUtils.SOBOT_WO_DETAIL_INFO_TICKETID, orderId);
            context.startActivity(intent);
        }
    }


    /**
     * 退出工单系统
     *
     * @param context
     * @param loginAcount 登录账号
     * @param block     回调结果 可为空
     */
    public static void out(final Context context, String loginAcount, final com.sobot.common.login.callback.SobotResultBlock block) {
        SobotLoginTools.getInstance().logOut(context, SobotOrderBaseUrl.getApi_Host(), loginAcount, new com.sobot.common.login.callback.SobotResultBlock() {
            @Override
            public void resultBolok(com.sobot.common.login.callback.SobotResultCode sobotResultCode, String msg, Object obj) {
                SobotLoginTools.getInstance().clearLoginInfo();
                if (block!=null){
                    block.resultBolok(sobotResultCode,msg,obj);
                }
            }
        });
        SobotWOSPUtil.getInstance(context.getApplicationContext()).clearAll();
    }


    /**
     * 日志显示设置
     *
     * @param isShowLog true 显示日志信息 默认false不显示
     */
    public static void setShowDebug(Boolean isShowLog) {
        SobotCommonApi.setShowLogDebug(isShowLog);
    }

    /**
     * 通过邮箱密码方式登录工单系统
     *
     * @param context
     * @param loginPwd       客服邮箱
     * @param loginPwd       登录密码
     * @param isGoToActivity 是否进入工单中心界面 true 进入 false 只登录不进入
     * @param block          回调结果 可为空
     */
    public static void loginUser(final Context context, final String loginUser, final String loginPwd, final boolean isGoToActivity, final SobotResultBlock block) {
        if (context == null) {
            SobotLogUtils.e("startAuthWithAcount 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(loginUser)) {
            SobotLogUtils.e("客服账户不能为空");
            return;
        }
        if (TextUtils.isEmpty(loginPwd)) {
            SobotLogUtils.e("登录密码不能为空");
            return;
        }
        SobotLoginTools.getInstance().doLogin(context, context, SobotOrderBaseUrl.getApi_Host(), "", loginUser, loginPwd, "", new com.sobot.common.login.callback.SobotResultBlock() {
            @Override
            public void resultBolok(com.sobot.common.login.callback.SobotResultCode code, String msg, Object obj) {
                if (code == com.sobot.common.login.callback.SobotResultCode.CODE_SUCCEEDED) {
                    //账号密码方式登录
                    SobotWOSPUtil.getInstance(context.getApplicationContext()).put(SobotWOConstant.SOBOT_KEY_LOGIN_FROM, 0);
                    SobotServiceInfoModel serviceVoModel = SobotLoginTools.getInstance().getServiceInfo();
                    updateLanguage(context, serviceVoModel);
                    if (isGoToActivity) {
                        //进入工单中心界面
                        Intent classificationIntent = new Intent(context, SobotWOClassificationActivity.class);
                        context.startActivity(classificationIntent);
                    }
                    if (block != null)
                        block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
                } else {
                    SobotLogUtils.e("工单客服登录失败:接口返回为空");
                    if (block != null)
                        block.resultBolok(SobotResultCode.CODE_FAILED,msg, null);
                }

            }
        });
    }

    /**
     * 通过设置token方式登录工单系统
     *
     * @param context
     * @param loginToken     登录客服token
     * @param isGoToActivity 是否进入工单中心界面 true 进入 false 只登录不进入
     * @param block          回调结果 可为空
     */
    public static void loginUser(final Context context, String loginToken, final boolean isGoToActivity, final SobotResultBlock block) {
        if (context == null) {
            SobotLogUtils.e("startAuthWithAcount 参数 context不能为空");
            return;
        }
        if (TextUtils.isEmpty(loginToken)) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, context.getResources().getString(R.string.sobot_token_empty), null);
            SobotLogUtils.e("登录token不能为空");
            return;
        }
        SobotLoginTools.getInstance().doLogin(context, context, SobotOrderBaseUrl.getApi_Host(), "", SobotLoginTools.getInstance().getLoginUser().getLoginAccount(), SobotLoginTools.getInstance().getLoginUser().getLoginAccount(), loginToken, new com.sobot.common.login.callback.SobotResultBlock() {
            @Override
            public void resultBolok(com.sobot.common.login.callback.SobotResultCode code, String msg, Object obj) {
                if (code == com.sobot.common.login.callback.SobotResultCode.CODE_SUCCEEDED) {
                    //token方式登录
                    SobotWOSPUtil.getInstance(context.getApplicationContext()).put(SobotWOConstant.SOBOT_KEY_LOGIN_FROM, 0);
                    SobotServiceInfoModel serviceVoModel = SobotLoginTools.getInstance().getServiceInfo();
                    updateLanguage(context, serviceVoModel);
                    if (isGoToActivity) {
                        //进入工单中心界面
                        Intent classificationIntent = new Intent(context, SobotWOClassificationActivity.class);
                        context.startActivity(classificationIntent);
                    }
                    if (block != null)
                        block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);

                    return;
                } else {
                    SobotLogUtils.e("工单客服登录失败:接口返回为空");
                    if (block != null)
                        block.resultBolok(SobotResultCode.CODE_FAILED, msg, null);
                }

            }
        });
    }

    public static void updateLanguage(Context context, SobotServiceInfoModel sobotServiceVoModel) {
        if (!TextUtils.isEmpty(sobotServiceVoModel.getServiceLanguage())) {
            Locale locale = null;
            if ("zh".equals(sobotServiceVoModel.getServiceLanguage())) {
                //中文
                locale = new Locale("zh");
            } else if ("en".equals(sobotServiceVoModel.getServiceLanguage())) {
                //英文
                locale = new Locale("en");
            } else {
                locale = new Locale("zh");
            }
            //多语言语言设置,初始化之后设置有效，之前无效（初始化会清理语言设置）
            SobotWOSPUtil.getInstance(context.getApplicationContext()).put("SobotLanguage", locale);
            SobotWOSPUtil.getInstance(context.getApplicationContext()).put("SobotLanguageStr", sobotServiceVoModel.getServiceLanguage());
        }
    }

    /**
     * app内部登录工单系统
     *
     * @param context
     */
    public static void appLoginUser(Context context) {
        if (context == null) {
            SobotLogUtils.e("appLoginUser 参数 context不能为空");
            return;
        }
        SobotWOSPUtil.getInstance(context.getApplicationContext()).put(SobotWOConstant.SOBOT_KEY_LOGIN_FROM, 1);
    }
}