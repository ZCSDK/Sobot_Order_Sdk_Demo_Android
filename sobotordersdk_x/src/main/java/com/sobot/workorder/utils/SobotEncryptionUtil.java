package com.sobot.workorder.utils;

import com.sobot.utils.SobotStringUtils;

//加密工具类
public class SobotEncryptionUtil {

    // Base64加密
    public static String encode(String str) {
        if (SobotStringUtils.isEmpty(str)) {
            return "";
        }
        byte[] encodeBytes = SobotBase64.getMimeEncoder().encode(str.getBytes());
        return new String(encodeBytes);
    }

    // Base64解密
    public static String decode(String str) {
        if (SobotStringUtils.isEmpty(str)) {
            return "";
        }
        byte[] decodeBytes = SobotBase64.getMimeDecoder().decode(str.getBytes());
        return new String(decodeBytes);
    }

}