package com.sobot.workorder.permission;

/**
 * 权限常量类

 */

public interface SobotPermissionApi {
    //对话权限
    int USER_PERMISSION_TYPE_TALK = 0;
    //工单权限
    int USER_PERMISSION_TYPE_WORKORDER = 1;
    //监控统计权限
    int USER_PERMISSION_TYPE_MONITOR_STATISTIC = 2;
    //监控-在线权限
    int USER_PERMISSION_TYPE_MONITOR_ONLINE = 3;
    //监控-呼叫权限
    int USER_PERMISSION_TYPE_MONITOR_CALL = 4;
    //统计-在线权限
    int USER_PERMISSION_TYPE_STATISTIC_ONLINE = 5;
    //统计-呼叫权限
    int USER_PERMISSION_TYPE_STATISTIC_CALL = 6;
    //统计-工单权限
    int USER_PERMISSION_TYPE_STATISTIC_WORKORDER = 7;
    //统计-机器人权限
    int USER_PERMISSION_TYPE__STATISTIC_ROBOT = 8;
    //设置-个人状态权限
    int USER_PERMISSION_TYPE_SET_PERSON_STATUS = 9;
    //设置-会话顺序权限
    int USER_PERMISSION_TYPE_SET_SESSION_ORDER = 10;
    //其它权限
    int USER_PERMISSION_TYPE_OTHER = 11;
    //工单详情页是否有右上角的编辑工单权限
    int USER_PERMISSION_TYPE_EDIT_WORK_ORDER = 12;
    //工单详情页是否有右上角的删除工单权限
    int USER_PERMISSION_TYPE_DEL_WORK_ORDER = 13;
    //工单回复权限
    int USER_PERMISSION_TYPE_WORKORDER_REPLY = 14;
    //点击工单详情页的回复按钮，跳转到回复工单页，是否显示  工单状态、优先级、受理客服组、受理客服、抄送客服
    int USER_PERMISSION_TYPE_SHOW_OPTION = 15;

    boolean checkPermission(int type);
}