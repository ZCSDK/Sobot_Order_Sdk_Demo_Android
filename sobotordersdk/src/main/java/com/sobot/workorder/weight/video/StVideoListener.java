package com.sobot.workorder.weight.video;

public interface StVideoListener {

    void onStart();
    void onEnd();
    void onError();
    void onCancel();
}
