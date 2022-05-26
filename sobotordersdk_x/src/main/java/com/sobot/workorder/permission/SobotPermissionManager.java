package com.sobot.workorder.permission;

import android.content.Context;
import android.text.TextUtils;

import com.sobot.utils.SobotSharedPreferencesUtil;
import com.sobot.workorderlibrary.api.apiutils.SobotWOConstant;
import com.sobot.workorderlibrary.api.model.SobotCusFunctionVoModel;
import com.sobot.workorderlibrary.api.model.SobotServiceVoModel;

/**
 * 权限管理类

 */

public class SobotPermissionManager {

    public static final int ONLINE_CUSTOMER_SERVICE = 1111;//在线客服
    public static final int ONLINE_ADMINISTRATORS = 2222;//在线管理员
    public static final int SUPER_ADMINISTRATORS = 3333;//超级管理员
    public static final int ALL_FUNCTION_ADMINISTRATORS = 4444;//全功能管理员
    public static final int WORK_ORDER_CUSTOMER_SERVICE = 5555;//工单客服
    public static final int CALL_CUSTOMER_SERVICE = 6669;//呼叫客服
    private static final int ALL_FUNCTION_CUSTOMER_SERVICE = 7777;//全功能客服
    public static final int WORK_ORDER_ADMINISTRATORS = 8888;//工单管理员
    public static final int CALL_ADMINISTRATORS = 9999;//呼叫管理员
    private static SobotPermissionApi instance;

    private static SobotPermissionApi getInstance(Context context) {
        if (instance == null) {
            SobotServiceVoModel sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
            int roleId = WORK_ORDER_CUSTOMER_SERVICE;
            if (sobotServiceVoModel != null && !TextUtils.isEmpty(sobotServiceVoModel.getCusRoleId())) {
                roleId = Integer.parseInt(sobotServiceVoModel.getCusRoleId());
            }
            switch (roleId) {
                case ONLINE_CUSTOMER_SERVICE://在线客服
                    instance = new SobotPermissionOnLineCustomerService();
                    break;
                case ONLINE_ADMINISTRATORS://在线管理员
                    instance = new SobotPermissionOnLineAdministrators();
                    break;
                case SUPER_ADMINISTRATORS://超级管理员
                    instance = new SobotPermissionSuper();
                    break;
                case ALL_FUNCTION_ADMINISTRATORS://全功能管理员
                    instance = new SobotPermissionAllFunctionAdministrator();
                    break;
                case WORK_ORDER_CUSTOMER_SERVICE://工单客服
                    instance = new SobotPermissionWorkOrderCustomerService();
                    break;
                case CALL_CUSTOMER_SERVICE://呼叫客服
                    instance = new SobotPermissionCallCustomerService();
                    break;
                case ALL_FUNCTION_CUSTOMER_SERVICE://全功能客服
                    instance = new SobotPermissionAllFunctionCustomerService();
                    break;
                case WORK_ORDER_ADMINISTRATORS://工单管理员
                    instance = new SobotPermissionWorkOrderAdministrators();
                    break;
                case CALL_ADMINISTRATORS://呼叫管理员
                    instance = new SobotPermissionCallAdministrators();
                    break;
            }
        }
        return instance;
    }

    public static boolean checkPermission(Context context,int type) {
        SobotPermissionApi instance = getInstance(context);
        if (instance != null) {
            return instance.checkPermission(type);
        }
        return false;
    }

    public static void clearInstance() {
        instance = null;
    }


    //-------2.8.6添加----------------
    public static final String USER_PERMISSION_TYPE_DEL_WORK_ORDER = "1222";// 可删除工单
    public static final String USER_PERMISSION_TYPE_DEL_WORK_ORDER_REPLY = "1223";// 可删除工单回复
    public static final String USER_PERMISSION_TYPE_COLSE_WORK_ORDER = "1224";// 可关闭工单
    public static final String USER_PERMISSION_TYPE_ACTIVATION_WORK_ORDER = "1225";// 可激活工单
    public static final String USER_PERMISSION_TYPE_WORK_SEARCH = "1227";// 工单全局搜索
    public static final String USER_PERMISSION_TYPE_WORK_STATUS = "1231";// 工单状态 可见/不可见

    public static final String USER_PERMISSION_TYPE_WORK_HISTORY = "1230";// 工单操作历史权限

    public static boolean isHasPermission(Context context, String type) {
        SobotServiceVoModel sobotServiceVoModel = SobotSharedPreferencesUtil.getInstance(context).get(SobotWOConstant.SOBOT_LOGIN_USER_INFO, SobotServiceVoModel.class);
        if (!TextUtils.isEmpty(type) && sobotServiceVoModel != null && sobotServiceVoModel.getFunctionStr() != null) {
            for (int i = 0; i < sobotServiceVoModel.getFunctionStr().size(); i++) {
                SobotCusFunctionVoModel cusFunctionVoModel = sobotServiceVoModel.getFunctionStr().get(i);
                if (type.equals(cusFunctionVoModel.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}