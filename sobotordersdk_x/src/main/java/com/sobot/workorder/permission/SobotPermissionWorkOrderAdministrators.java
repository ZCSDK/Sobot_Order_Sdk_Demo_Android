package com.sobot.workorder.permission;

import android.util.SparseBooleanArray;

/**
 * 工单管理员

 */

public class SobotPermissionWorkOrderAdministrators implements SobotPermissionApi {

    private SparseBooleanArray sparseArray;

    public SobotPermissionWorkOrderAdministrators() {
        sparseArray = new SparseBooleanArray();
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_MONITOR_STATISTIC, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_STATISTIC_WORKORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_OTHER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_EDIT_WORK_ORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_DEL_WORK_ORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER_REPLY, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_SHOW_OPTION, true);
    }

    @Override
    public boolean checkPermission(int type) {
        return sparseArray.get(type);
    }
}