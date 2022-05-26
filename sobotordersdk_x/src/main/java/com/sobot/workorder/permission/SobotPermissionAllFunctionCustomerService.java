package com.sobot.workorder.permission;

import android.util.SparseBooleanArray;

/**
 * 全功能客服

 */

public class SobotPermissionAllFunctionCustomerService implements SobotPermissionApi {

    private SparseBooleanArray sparseArray;

    public SobotPermissionAllFunctionCustomerService() {
        sparseArray = new SparseBooleanArray();
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_TALK, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_SET_PERSON_STATUS, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_SET_SESSION_ORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_OTHER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER_REPLY, true);
    }

    @Override
    public boolean checkPermission(int type) {
        return sparseArray.get(type);
    }
}