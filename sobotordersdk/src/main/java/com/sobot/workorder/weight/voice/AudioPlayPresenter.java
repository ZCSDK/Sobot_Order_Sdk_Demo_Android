package com.sobot.workorder.weight.voice;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.sobot.utils.SobotLogUtils;

import java.io.File;

public class AudioPlayPresenter {

    private Context mContext;
    private AudioInfoModel mAudioInfoModel;
    private AudioPlayCallBack mCallbak;

    public AudioPlayPresenter(Context context) {
        this.mContext = context;
    }

    public synchronized void clickAudio(final AudioInfoModel message, AudioPlayCallBack callbak) {
        if (AudioTools.getInstance().isPlaying()) {
            AudioTools.stop();// 停止语音的播放
        }
        this.mCallbak = callbak;
        if (mAudioInfoModel != null) {
            mAudioInfoModel.setVoideIsPlaying(false);
            if (mCallbak != null) {
                mCallbak.onPlayEnd(mAudioInfoModel);
                mAudioInfoModel = null;
            }
        }
//            playVoiceByPath(message);
        playVoice(message);
    }


    private void playVoice(final AudioInfoModel audioInfoModel, File voidePath) {
        try {
            AudioTools.getInstance();
            if (AudioTools.getIsPlaying()) {
                AudioTools.stop();
            }
            AudioTools.getInstance().setAudioStreamType(
                    AudioManager.STREAM_MUSIC);

            AudioTools.getInstance().reset();
            // 设置要播放的文件的路径
            AudioTools.getInstance().setDataSource(voidePath.toString());
            // 准备播放
            // AudioTools.getInstance().prepare();
            AudioTools.getInstance().prepareAsync();
            // 开始播放
            // mMediaPlayer.start();
            AudioTools.getInstance().setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            audioInfoModel.setVoideIsPlaying(true);
                            if (mCallbak != null) {
                                mAudioInfoModel = audioInfoModel;
                                mCallbak.onPlayStart(audioInfoModel);
                            }
                        }
                    });
            // 这在播放的动画
            AudioTools.getInstance().setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer arg0) {
                            // 停止播放
                            audioInfoModel.setVoideIsPlaying(false);
                            SobotLogUtils.i("----语音播放完毕----");
                            if (mCallbak != null) {
                                mCallbak.onPlayEnd(audioInfoModel);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            SobotLogUtils.i("音频播放失败");
            audioInfoModel.setVoideIsPlaying(false);
            if (mCallbak != null) {
                mCallbak.onPlayEnd(audioInfoModel);
            }
        }
    }

    private void playVoice(final AudioInfoModel audioInfoModel) {
        try {
            AudioTools.getInstance();
            if (AudioTools.getIsPlaying()) {
                AudioTools.stop();
            }
            AudioTools.getInstance().setAudioStreamType(
                    AudioManager.STREAM_MUSIC);

            AudioTools.getInstance().reset();
            // 设置要播放的文件的路径
            AudioTools.getInstance().setDataSource(audioInfoModel.getAudioUrl());
            // 准备播放
            // AudioTools.getInstance().prepare();
            AudioTools.getInstance().prepareAsync();
            // 开始播放
            // mMediaPlayer.start();
            AudioTools.getInstance().setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                            audioInfoModel.setVoideIsPlaying(true);
                            if (mCallbak != null) {
                                mAudioInfoModel = audioInfoModel;
                                mCallbak.onPlayStart(audioInfoModel);
                            }
                        }
                    });
            // 这在播放的动画
            AudioTools.getInstance().setOnCompletionListener(
                    new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer arg0) {
                            // 停止播放
                            audioInfoModel.setVoideIsPlaying(false);
                            SobotLogUtils.i("----语音播放完毕----");
                            if (mCallbak != null) {
                                mCallbak.onPlayEnd(audioInfoModel);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            SobotLogUtils.i("音频播放失败");
            audioInfoModel.setVoideIsPlaying(false);
            if (mCallbak != null) {
                mCallbak.onPlayEnd(audioInfoModel);
            }
        }
    }
}