package com.sobot.workorder.callback;

/**
 * @author: Sobot
 * 回调接口
 * 2022/5/5
 */
public interface SobotResultBlock {
    void resultBolok(SobotResultCode code,String msg,Object obj);
}
