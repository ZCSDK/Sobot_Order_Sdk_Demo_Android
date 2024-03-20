package com.sobot.workorder.weight.voice;

public class AudioInfoModel {
    //语音播放地址
    private String audioUrl;
    private long duration;//播放时长
    private boolean voideIsPlaying = false;
    private long startPosition;//播放开始位置
    private float speed = 1.0f;//播放速率

    public boolean isVoideIsPlaying() {
        return voideIsPlaying;
    }

    public void setVoideIsPlaying(boolean voideIsPlaying) {
        this.voideIsPlaying = voideIsPlaying;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }
}
