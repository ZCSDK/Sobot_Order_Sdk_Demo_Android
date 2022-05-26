package com.sobot.workorder.permission;

import android.util.SparseBooleanArray;

/**
 * 呼叫客服

 */

public class SobotPermissionCallCustomerService implements SobotPermissionApi {

    private SparseBooleanArray sparseArray;

    public SobotPermissionCallCustomerService() {
        sparseArray = new SparseBooleanArray();
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_OTHER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER_REPLY, true);
    }

    @Override
    public boolean checkPermission(int type) {
        return sparseArray.get(type);
    }
}
