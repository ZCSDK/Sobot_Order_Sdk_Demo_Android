package com.sobot.workorder.permission;

import android.util.SparseBooleanArray;

/**
 * 呼叫管理员

 */

public class SobotPermissionCallAdministrators implements SobotPermissionApi {

    private SparseBooleanArray sparseArray;

    public SobotPermissionCallAdministrators() {
        sparseArray = new SparseBooleanArray();
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_MONITOR_STATISTIC, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_MONITOR_CALL, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_STATISTIC_CALL, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_STATISTIC_WORKORDER, true);
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