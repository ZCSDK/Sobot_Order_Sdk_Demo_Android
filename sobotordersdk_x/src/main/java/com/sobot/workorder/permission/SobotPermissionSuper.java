package com.sobot.workorder.permission;

import android.util.SparseBooleanArray;

/**
 * 超级管理员

 */

public class SobotPermissionSuper implements SobotPermissionApi {

    private SparseBooleanArray sparseArray;

    public SobotPermissionSuper() {
        sparseArray = new SparseBooleanArray();
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_TALK, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_WORKORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_MONITOR_STATISTIC, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_MONITOR_ONLINE, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_MONITOR_CALL, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_STATISTIC_ONLINE, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_STATISTIC_CALL, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_STATISTIC_WORKORDER, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE__STATISTIC_ROBOT, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_SET_PERSON_STATUS, true);
        sparseArray.put(SobotPermissionApi.USER_PERMISSION_TYPE_SET_SESSION_ORDER, true);
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