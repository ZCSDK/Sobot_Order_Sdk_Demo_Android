package com.sobot.workorder.utils;

import android.content.Context;
import android.graphics.Color;

import com.sobot.workorder.R;
import com.sobot.workorderlibrary.api.model.SobotWODictModelResult;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public class SobotTicketDictUtil {
    static List<SobotWODictModelResult> dictSource = null;


    public static void setDictSource(List<SobotWODictModelResult> dictSource) {
        SobotTicketDictUtil.dictSource = dictSource;
    }

    public static List<SobotWODictModelResult> getDictSource() {
        return dictSource;
    }

    public static String getDictSourceByValue(int dictValue) {
        String result = "";
        if (null != dictSource){
            for (int i = 0; i < dictSource.size(); i++) {
                if (dictSource.get(i).getDictValue().equals(dictValue + "")) {
                    return dictSource.get(i).getDictName();
                }
            }
        }
        return result;
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

    public static String getTicketFromStr(Context context,int ticketFrom) {
        if (context==null){
            return "";
        }
        if(null!=dictSource && dictSource.size()>0){
            return getDictSourceByValue(ticketFrom);
        }
        String tmpStr = "";
        switch (ticketFrom) {
            case 0:
                tmpStr = context.getResources().getString(R.string.sobot_title_workorder_center_string);
                break;
            case 1:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_pc);
                break;
            case 2:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_h5);
                break;
            case 3:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_weixin);
                break;
            case 4:
                tmpStr =context.getResources().getString(R.string.sobot_ticket_form_app);
                break;
            case 5:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_app);
                break;
            case 6:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_onling_pc);
                break;
            case 7:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_customer_enter);
                break;
            case 8:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_call_center);
                break;
            case 9:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_onling_weixin);
                break;
            case 10:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_onling_h5);
                break;
            case 11:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_onling_app);
                break;
            case 12:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_email);
                break;
            case 13:
                tmpStr = context.getResources().getString(R.string.sobot_ticket_form_yuyin);
                break;
        }
        return tmpStr;
    }
}
