package com.sobot.workorder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sobot.common.utils.SobotCommonApi;
import com.sobot.network.http.callback.SobotResultCallBack;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.activity.SobotWOClassificationActivity;
import com.sobot.workorder.activity.SobotWOCreateActivity;
import com.sobot.workorder.activity.SobotWODetailActivity;
import com.sobot.workorder.activity.SobotWOSearchActivity;
import com.sobot.workorder.callback.SobotResultBlock;
import com.sobot.workorder.callback.SobotResultCode;
import com.sobot.workorder.utils.SobotConstantUtils;
import com.sobot.workorder.utils.SobotEncryptionUtil;
import com.sobot.workorderlibrary.api.SobotOrderService;
import com.sobot.workorderlibrary.api.SobotOrderServiceFactory;
import com.sobot.workorderlibrary.api.apiutils.SobotOrderBaseUrl;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;

import java.util.Map;


/**
 * 接口输出类
 */
public class SobotOrderApi {

    private static String Tag = SobotOrderApi.class.getSimpleName();

    /**
     * 初始化SDK，设置域名和认证token
     * @param application
     * @param host 可以为空，默认阿里云服务；如果需要，请设置自己的域名
     */
    public static void initWithHost(Application application, String host) {
        if (application == null) {
            SobotLogUtils.e("initWithHost 参数 application不能为空");
            return;
        }
        if (!TextUtils.isEmpty(host)) {
            SobotOrderBaseUrl.setApi_Host(host);
        }
        SobotCommonApi.init(application, SobotOrderBaseUrl.getApi_Host());
    }


    /**
     * 通过邮箱密码方式启动工单系统，进入首页
     *
     * @param context
     * @param loginPwd 客服邮箱
     * @param loginPwd 登录密码
     * @param block 回调结果 可为空
     */
    public static void startWithAcount(Context context, String loginUser, String loginPwd, SobotResultBlock block) {
        loginUser(context, loginUser, loginPwd, true, block);
    }

    /**
     * 通过设置token方式启动工单系统，进入首页
     *
     * @param context
     * @param loginToken 登录客服token
     * @param block 回调结果 可为空
     */
    public static void startWithToken(final Context context, String loginToken, SobotResultBlock block) {
        loginUser(context, loginToken, true, block);
    }

    /**
     * 打开搜索工单页面
     */
    public static void openSearchWorkOrder(final Context context, SobotResultBlock block) {
        String loginToken = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_KEY_TOKEN);
        if (SobotStringUtils.isEmpty(loginToken)) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, "登录已失效，请重新登录！", null);

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
     * @param param  可为空，也可添加参数 userName 用户昵称 userId 用户id
     * @param block  回调结果 可为空
     */
    public static void openCreateWorkOrder(final Context context, Map<String, Object> param, SobotResultBlock block) {
        String loginToken = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_KEY_TOKEN);
        if (SobotStringUtils.isEmpty(loginToken)) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, "登录已失效，请重新登录！", null);

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
     * @param block  回调结果 可为空
     */
    public static void openOrderDetail(Context context, String orderId, SobotResultBlock block) {
        String loginToken = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_KEY_TOKEN);
        if (SobotStringUtils.isEmpty(loginToken)) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, "登录已失效，请重新登录！", null);
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
     * @param context
     * @param loginUser 登录账号
     * @param block  回调结果 可为空
     */
    public static void out(final Context context, String loginUser, final SobotResultBlock block) {
        SobotOrderService zhiChiApi = SobotOrderServiceFactory.createZhiChiApi(context);
        zhiChiApi.logout(context, loginUser, new SobotResultCallBack<Object>() {
            @Override
            public void onSuccess(Object object) {
                SobotLogUtils.i("工单客服退出登录成功");
                if (block != null)
                    block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "登录已失效，请重新登录！", null);
            }

            @Override
            public void onFailure(Exception e, String des) {
                SobotLogUtils.e("工单客服退出登录失败：" + des);
                if (block != null)
                    block.resultBolok(SobotResultCode.CODE_FAILED, "登录已失效，请重新登录！", null);
            }
        });
        SobotSharedPreferencesUtil.getInstance(context).clearAll();
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
     * @param block 回调结果 可为空
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
        //是否切换账号密码,如果切换账号密码需要重新登录
        boolean isChangeLoginUserName = false;
        String cacheloginUser = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_KEY_USERNAME);
        String cacheloginPwd = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_KEY_PWD);
        if (!SobotStringUtils.isEmpty(cacheloginUser) && !SobotStringUtils.isEmpty(cacheloginPwd)) {
            if (!loginUser.equals(SobotEncryptionUtil.decode(cacheloginUser)) || !loginPwd.equals(SobotEncryptionUtil.decode(cacheloginPwd))) {
                SobotLogUtils.i("登录账号密码和上次登录不一样，重新登录");
                isChangeLoginUserName = true;
            }
        } else {
            isChangeLoginUserName = true;
        }
        if (isChangeLoginUserName == false) {
            SobotServiceVoModel sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
            if (sobotServiceVoModel != null) {
                //已经登录过,并且账号密码没发生变化
                if (isGoToActivity) {
                    //进入工单中心界面
                    Intent classificationIntent = new Intent(context, SobotWOClassificationActivity.class);
                    context.startActivity(classificationIntent);
                }
                if (block != null)
                    block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
                return;
            }
        }
        final SobotOrderService zhiChiApi = SobotOrderServiceFactory.createZhiChiApi(context);
        zhiChiApi.login(context, loginUser, loginPwd, new SobotResultCallBack<Object>() {
            @Override
            public void onSuccess(Object object) {
                if (object != null && !TextUtils.isEmpty(object.toString())) {
                    //账号密码方式登录
                    SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_KEY_LOGINTYPE, 0);
                    SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_KEY_TOKEN, object.toString());
                    SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_KEY_USERNAME, SobotEncryptionUtil.encode(loginUser));
                    SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_KEY_PWD, SobotEncryptionUtil.encode(loginPwd));

                    zhiChiApi.getServiceDataInfo(context, new SobotResultCallBack<SobotServiceVoModel>() {
                        @Override
                        public void onSuccess(SobotServiceVoModel sobotServiceVoModel) {
                            if (sobotServiceVoModel != null) {
                                SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_LOGIN_USER_INFO, sobotServiceVoModel);
                            }
                            if (block != null)
                                block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
                        }

                        @Override
                        public void onFailure(Exception e, String des) {
                            if (block != null)
                                block.resultBolok(SobotResultCode.CODE_FAILED, "工单客服信息:返回失败", null);
                            SobotLogUtils.e("工单客服登录失败:接口返回为空");
                        }
                    });

                } else {
                    if (block != null)
                        block.resultBolok(SobotResultCode.CODE_FAILED, "工单客服登录失败:接口返回为空", null);
                    SobotLogUtils.e("工单客服登录失败:接口返回为空");
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (block != null)
                    block.resultBolok(SobotResultCode.CODE_FAILED, SobotStringUtils.isEmpty(des)?"工单客服登录失败":des, null);
                SobotLogUtils.e("工单客服登录失败");
            }
        });
    }

    /**
     * 通过设置token方式登录工单系统
     *
     * @param context
     * @param loginToken     登录客服token
     * @param isGoToActivity 是否进入工单中心界面 true 进入 false 只登录不进入
     * @param block 回调结果 可为空
     */
    public static void loginUser(final Context context, String loginToken, final boolean isGoToActivity, final SobotResultBlock block) {
        if (context == null) {
            SobotLogUtils.e("startAuthWithAcount 参数 context不能为空");
            return;
        }

        if (TextUtils.isEmpty(loginToken)) {
            if (block != null)
                block.resultBolok(SobotResultCode.CODE_FAILED, "登录token不能为空", null);
            SobotLogUtils.e("登录token不能为空");
            return;
        }
        //是否切换token,如果切换token需要重新登录
        boolean isChangeLoginToken = false;
        String cacheloginToken = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_KEY_TOKEN);
        if (!SobotStringUtils.isEmpty(cacheloginToken)) {
            if (!loginToken.equals(cacheloginToken)) {
                SobotLogUtils.e("启动token和上次不一样，重新登录");
                isChangeLoginToken = true;
            }
        } else {
            isChangeLoginToken = true;
        }
        if (isChangeLoginToken == false) {
            SobotServiceVoModel sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
            if (sobotServiceVoModel != null) {
                //已经登录过,并且token没发生变化
                if (block != null)
                    block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
                if (isGoToActivity) {
                    //进入工单中心界面
                    Intent classificationIntent = new Intent(context, SobotWOClassificationActivity.class);
                    context.startActivity(classificationIntent);
                }
                return;
            }
        }
        //保存token
        SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_KEY_LOGINTYPE, 1);
        SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_KEY_TOKEN, loginToken);
        SobotOrderService zhiChiApi = SobotOrderServiceFactory.createZhiChiApi(context);
        zhiChiApi.getServiceDataInfo(context, new SobotResultCallBack<SobotServiceVoModel>() {
            @Override
            public void onSuccess(SobotServiceVoModel sobotServiceVoModel) {
                if (block != null)
                    block.resultBolok(SobotResultCode.CODE_SUCCEEDED, "", null);
                if (sobotServiceVoModel != null) {
                    SobotSharedPreferencesUtil.getInstance(context).put(SobotWOConstant.SOBOT_LOGIN_USER_INFO, sobotServiceVoModel);
                }
                if (isGoToActivity) {
                    //进入工单中心界面
                    Intent classificationIntent = new Intent(context, SobotWOClassificationActivity.class);
                    context.startActivity(classificationIntent);
                }
            }

            @Override
            public void onFailure(Exception e, String des) {
                if (block != null)
                    block.resultBolok(SobotResultCode.CODE_FAILED, des, null);
            }
        });
    }
}