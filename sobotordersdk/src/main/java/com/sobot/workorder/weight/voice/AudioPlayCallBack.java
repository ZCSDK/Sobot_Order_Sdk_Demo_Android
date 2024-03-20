package com.sobot.workorder.weight.voice;

public interface AudioPlayCallBack {

    void onPlayStart(AudioInfoModel audioInfoModel);
    void onPlayEnd(AudioInfoModel audioInfoModel);
}