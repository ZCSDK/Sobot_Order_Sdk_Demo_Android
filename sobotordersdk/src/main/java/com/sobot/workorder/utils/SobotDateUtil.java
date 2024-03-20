package com.sobot.workorder.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sobot.utils.SobotStringUtils;
import com.sobot.workorder.R;
import com.sobot.workorder.weight.timePicker.SobotTimePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间工具类
 *
 * @author ChaiHongwei
 */
public class SobotDateUtil {
    /**
     * 时:分
     */
    public final static SimpleDateFormat DATE_FORMAT0 = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());
    /**
     * 年-月-日 时:分:秒
     */
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    /**
     * 年-月-日
     */
    public final static SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault());
    /**
     * 年-月-日 时:分
     */
    public final static SimpleDateFormat DATE_FORMAT3 = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm", Locale.getDefault());
    /**
     * 分:秒
     */
    public final static SimpleDateFormat DATE_FORMAT4 = new SimpleDateFormat(
            "mm:ss", Locale.getDefault());
    public final static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
    /**
     * 将毫秒级整数转换为字符串格式时间
     *
     * @param millisecondDate 毫秒级时间整数
     * @param format          要转换成的时间格式(参见 DateUtil常量)
     * @return 返回相应格式的时间字符串
     */
    public static String toDate(long millisecondDate, SimpleDateFormat format) {
        String time = "";
        try {
            time = format.format(new Date(millisecondDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    /**
     * 将毫秒级整数转换为字符串格式时间
     *
     * @param millisecondDate 毫秒级时间整数
     * @param format          要转换成的时间格式(参见 DateUtil常量)
     * @return 返回相应格式的时间字符串
     */
    public static String toDate(String millisecondDate, SimpleDateFormat format) {
        String time = "";
        try {
            time = format.format(new Date(Long.parseLong(millisecondDate)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    /**
     * 将秒级整数转换为字符串格式时间
     *
     * @param millisecondDate 毫秒级时间整数
     * @param format          要转换成的时间格式(参见 DateUtil常量)
     * @return 返回相应格式的时间字符串
     */
    /**
     * 秒转换为指定格式的日期
     *
     * @param second
     * @param patten
     * @return
     */
    public static String secondToDate(long second, String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    /**
     * 将秒级整数转换为字符串格式时间（HH:mm:ss）
     *
     * @param second 秒
     * @return
     */
    public static String secondToHHMMSS(long second) {
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", second);
    }


    public static long stringToLong(String date) {
        if (!TextUtils.isEmpty(date)) {
            try {
                return DATE_FORMAT.parse(date).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static long stringToLongMs(String date) {
        if (!TextUtils.isEmpty(date)) {
            if(date.length()<=10){
                date +="000";
            }
            try {
                Calendar seconds = Calendar.getInstance();
                seconds.setTime(DATE_FORMAT4.parse(date));
                return seconds.get(Calendar.SECOND);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 格式化时间
     *
     * @param time 不显示HH:mm，并且不显示“今天”
     * @return
     */
    public static String formatDateTime(String time) {
        return formatDateTime(time, false, "");
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatDateTime(String time, boolean showHours, String showToday) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (time == null || "".equals(time) || time.length() < 19) {
            return "";
        }
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        if (current.after(today)) {
            return showToday + " " + time.split(" ")[1].substring(0, 5);
        } else {
            int index = time.indexOf("-") + 1;
            if (showHours) {
                return time.substring(index, time.length()).substring(0, 11);
            } else {
                return time.substring(index, time.length()).substring(0, 5);
            }
        }
    }


    public static void main(String[] args) {

//        String time = formatDateTime("2016-01-07 15:41:00", true, "今天");
//        System.out.println("time:" + time);
//        time = formatDateTime("2016-01-03 11:41:00");
//        System.out.println("time:" + time);
//        time = formatDateTime("2016-01-01 15:43:00");
//        System.out.println("time:" + time);
    }

    /**
     * 将时间戳格式化
     *
     * @param seconds
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if (seconds == null || TextUtils.isEmpty(seconds) || seconds.equals("null")) {
            return "";
        }
        if (format == null || TextUtils.isEmpty(format)) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if(seconds.length()<=10){
            seconds +="000";
        }
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    /**
     * 把long 转换成 日期 再转换成String类型
     */
    public static String longToDateStr(Long millSec, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    /**
     * 要回显的view 也就是textView
     *
     * @param context      应该为activity
     * @param selectedTime 选中的日期  可以为null 如果为null那么取当前时间
     * @param displayType  显示的类型 0 为 年月日  1为 时分
     */
    public static void openTimePickerView(Context context, View v, Date selectedTime, final int displayType) {
        Calendar selectedDate = Calendar.getInstance();
        if (selectedTime != null) {
            selectedDate.setTime(selectedTime);
        }
        boolean[] typeArray = displayType == 0 ? new boolean[]{true, true, true, false, false, false} : new boolean[]{false, false, false, true, true, false};
        //时间选择器
        SobotTimePickerView pvTime = new SobotTimePickerView.Builder(context, new SobotTimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                if (v != null && v instanceof TextView) {
                    TextView tv = (TextView) v;
                    tv.setText(displayType == 0 ? DATE_FORMAT2.format(date) : DATE_FORMAT0.format(date));
                }
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(typeArray)
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(context.getResources().getColor(R.color.sobot_wo_line_color))
                .setContentSize(17)
                .setSubCalSize(14)
                .setTextColorCenter(context.getResources().getColor(R.color.sobot_wo_wenzi_gray1))
                .setTextColorOut(context.getResources().getColor(R.color.sobot_wo_wenzi_gray2))
                .setTitleBgColor(context.getResources().getColor(R.color.sobot_wo_white_to_gray_bg_color))
                .setCancelColor(context.getResources().getColor(R.color.sobot_wo_theme_color))
                .setSubmitColor(context.getResources().getColor(R.color.sobot_wo_wenzi_white))
                .setDate(selectedDate)
                .setBgColor(context.getResources().getColor(R.color.sobot_wo_white_to_gray_bg_color))
                .setBackgroundId(0x80000000) //设置外部遮罩颜色
                .setDecorView(null)
                .setLineSpacingMultiplier(2.0f)
                .build();

        pvTime.show(v);
    }
    public static void openTimePickerView(Context context, View v, Date selectedTime, final int displayType,String title) {
        Calendar selectedDate = Calendar.getInstance();
        if (selectedTime != null) {
            selectedDate.setTime(selectedTime);
        }
        boolean[] typeArray = displayType == 0 ? new boolean[]{true, true, true, false, false, false} : new boolean[]{false, false, false, true, true, false};
        //时间选择器
        SobotTimePickerView pvTime = new SobotTimePickerView.Builder(context, new SobotTimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                if (v != null && v instanceof TextView) {
                    TextView tv = (TextView) v;
                    tv.setText(displayType == 0 ? DATE_FORMAT2.format(date) : DATE_FORMAT0.format(date));
                }
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(typeArray)
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(context.getResources().getColor(R.color.sobot_wo_line_color))
                .setContentSize(17)
                .setTitleText(title)
                .setSubCalSize(14)
                .setTextColorCenter(context.getResources().getColor(R.color.sobot_wo_wenzi_gray1))
                .setTextColorOut(context.getResources().getColor(R.color.sobot_wo_wenzi_gray2))
                .setTitleBgColor(context.getResources().getColor(R.color.sobot_wo_white_to_gray_bg_color))
                .setCancelColor(context.getResources().getColor(R.color.sobot_wo_theme_color))
                .setSubmitColor(context.getResources().getColor(R.color.sobot_wo_wenzi_white))
                .setDate(selectedDate)
                .setBgColor(context.getResources().getColor(R.color.sobot_wo_white_to_gray_bg_color))
                .setBackgroundId(0x80000000) //设置外部遮罩颜色
                .setDecorView(null)
                .setLineSpacingMultiplier(2.0f)
                .build();

        pvTime.show(v);
    }

    /**
     * 获取通话时长
     *
     * @param seconds
     * @return
     */
    public static String formatCallTimeDuration(long seconds) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%02d", seconds / 60 / 60));
        sb.append(":").append(String.format("%02d", (seconds / 60) % 60));
        sb.append(":").append(String.format("%02d", seconds % 60));
        return sb.toString();

    }
    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getStandardDate(Context context,long timeStr, Boolean isAutoMatchTimeZone) {

        StringBuffer sb = new StringBuffer();
        if(timeStr< 10000000000l){
            timeStr = timeStr*1000;
        }
        long time = System.currentTimeMillis() - timeStr;
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前
        if (day > 7) {
            sb.append(DATE_FORMAT2.format(new Date(timeStr)));
            return sb.toString();
        } else if (day > 1 && day <= 7) {
            sb.append(day + context.getResources().getString(R.string.sobot_time_day_ago));
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1"+context.getResources().getString(R.string.sobot_time_day_ago));
            } else {
                sb.append(hour + context.getResources().getString(R.string.sobot_time_hour_ago));
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1"+context.getResources().getString(R.string.sobot_time_hour_ago));
            } else {
                sb.append(minute + context.getResources().getString(R.string.sobot_time_minute_ago));
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1"+context.getResources().getString(R.string.sobot_time_minute_ago));
            } else {
                sb.append(mill + context.getResources().getString(R.string.sobot_time_second_ago));
            }
        } else {
            sb.append(context.getResources().getString(R.string.sobot_time_just));
        }
        return sb.toString();
    }

    public static String getDateByString(String time){
        String date = "";
        if(!SobotStringUtils.isEmpty(time)){
            try {
                long t = Long.parseLong(time);
                if(t< 10000000000l){
                    t = t*1000;
                }
                return DATE_FORMAT2.format(new Date(t));
            }catch (NumberFormatException e){
                return time;
            }

        }
        return date;
    }
    public static Date parse(String str, SimpleDateFormat format) {
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    /**
     * 获取通话时长
     *
     * @param seconds
     * @return
     */
    public static String formatCallTimeDurationTwo(long seconds) {
        if (seconds <= 0) return "00:00";
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%02d", (seconds / 60) % 60));
        sb.append(":").append(String.format("%02d", seconds % 60));
        return sb.toString();
    }
}