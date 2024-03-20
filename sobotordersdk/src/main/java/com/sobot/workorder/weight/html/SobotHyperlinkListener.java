package com.sobot.workorder.weight.html;

import android.content.Context;

/**
 * 超链接点击的监听事件
 */
public interface SobotHyperlinkListener {
    // 链接的点击事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
    boolean onUrlClick(Context context, String url);

    //邮箱的点击拦截事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
    boolean onEmailClick(Context context, String email);

    //电话的点击拦截事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
    boolean onPhoneClick(Context context, String phone);
}