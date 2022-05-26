package com.sobot.workorder.utils;

import android.content.Context;
import android.graphics.Color;

import com.sobot.common.utils.SobotResourceUtils;
import com.sobot.workorderlibrary.api.model.SobotWODictModelResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class SobotTicketDictUtil {
    static List<SobotWODictModelResult> dictType = null;
    static List<SobotWODictModelResult> dictPriority = null;
    static List<SobotWODictModelResult> dictStatus = null;


    public static List<SobotWODictModelResult> getDictStatus(Context context) {
        if (dictStatus == null) {
            dictStatus = new ArrayList<>();
            dictStatus.add(new SobotWODictModelResult(0, SobotResourceUtils.getResString(context, "sobot_wo_item_state_not_start_string")));
            dictStatus.add(new SobotWODictModelResult(1, SobotResourceUtils.getResString(context, "sobot_wo_item_state_doing_string")));
            dictStatus.add(new SobotWODictModelResult(2, SobotResourceUtils.getResString(context, "sobot_wo_item_state_waiting_string")));
            dictStatus.add(new SobotWODictModelResult(3, SobotResourceUtils.getResString(context, "sobot_wo_item_state_resolved_string")));
            dictStatus.add(new SobotWODictModelResult(98, SobotResourceUtils.getResString(context, "sobot_wo_item_state_delete_string")));
            dictStatus.add(new SobotWODictModelResult(99, SobotResourceUtils.getResString(context, "sobot_wo_item_state_closed_string")));
        }
        return dictStatus;
    }

    public static List<SobotWODictModelResult> getDictType(Context context) {
        if (dictType == null) {
            dictType = new ArrayList<>();
            dictType.add(new SobotWODictModelResult(9, SobotResourceUtils.getResString(context, "sobot_other_string")));
            dictType.add(new SobotWODictModelResult(0, SobotResourceUtils.getResString(context, "sobot_complaint_string")));
            dictType.add(new SobotWODictModelResult(1, SobotResourceUtils.getResString(context, "sobot_problem_string")));
            dictType.add(new SobotWODictModelResult(2, SobotResourceUtils.getResString(context, "sobot_affair_affair")));
            dictType.add(new SobotWODictModelResult(3, SobotResourceUtils.getResString(context, "sobot_fault_string")));
            dictType.add(new SobotWODictModelResult(4, SobotResourceUtils.getResString(context, "sobot_task_string")));
        }
        return dictType;
    }

    public static List<SobotWODictModelResult> getDictPriority(Context context) {
        if (dictPriority == null) {
            dictPriority = new ArrayList<>();
            dictPriority.add(new SobotWODictModelResult(0, SobotResourceUtils.getResString(context, "sobot_low_string")));
            dictPriority.add(new SobotWODictModelResult(1, SobotResourceUtils.getResString(context, "sobot_middle_string")));
            dictPriority.add(new SobotWODictModelResult(2, SobotResourceUtils.getResString(context, "sobot_height_string")));
            dictPriority.add(new SobotWODictModelResult(3, SobotResourceUtils.getResString(context, "sobot_urgent_string")));
        }
        return dictPriority;
    }

    public static int getDictIndex(List<SobotWODictModelResult> list, String value) {
        int result = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDictValue().equals(value)) {
                return i;
            }
        }

        return result;
    }

    /**
     * 根据工单状态获取  工单状态的名称
     *
     * @param value
     * @return
     */
    public static String getStatusName(Context context, String value) {
        dictStatus = getDictStatus(context);
        String result = "";
        for (int i = 0; i < dictStatus.size(); i++) {
            if (dictStatus.get(i).getDictValue().equals(value)) {
                return dictStatus.get(i).getDictName();
            }
        }
        return result;
    }

    public static String getPriorityName(Context context, String value) {
        dictPriority = getDictPriority(context);
        String result = "";
        for (int i = 0; i < dictPriority.size(); i++) {
            if (dictPriority.get(i).getDictValue().equals(value)) {
                return dictPriority.get(i).getDictName();
            }
        }
        return result;
    }

    public static int getStatusBgResId(Context context, String value) {
        dictStatus = getDictStatus(context);
        String resName = "workorder_icon_dictstatus_0";
        for (int i = 0; i < dictStatus.size(); i++) {
            if (dictStatus.get(i).getDictValue().equals(value)) {
                resName = "workorder_icon_dictstatus_" + dictStatus.get(i).getDictValue();
                break;
            }
        }
        return SobotResourceUtils.getIdByName(context, "drawable", resName);
    }


    public static int getStatusColor(int value) {
        String color = "#000000";
        switch (value) {
            case 0:
                color = "#F9676F";
                break;
            case 1:
                color = "#EEB04A";
                break;
            case 3:
                color = "#1FCFA6";
                break;
            case 2:
                color = "#4D9DFE";
                break;
            case 98:
                color = "#8B98AD";
                break;
            case 99:
                color = "#BDC3D1";
                break;
        }
        return Color.parseColor(color);
    }

    public static int getPriorityColor(int value) {
        String color = "#000000";
        switch (value) {
            case 0:
                color = "#b5b5b5";
                break;
            case 1:
                color = "#0dbcbe";
                break;
            case 2:
                color = "#f3aa6a";
                break;
            case 3:
                color = "#f05353";
                break;
        }
        return Color.parseColor(color);
    }
}
