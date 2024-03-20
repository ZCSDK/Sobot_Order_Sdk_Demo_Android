package com.sobot.workorder.adapter;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sobot.common.login.permission.SobotPermissionManager;
import com.sobot.pictureframe.SobotBitmapUtil;
import com.sobot.utils.SobotDensityUtil;
import com.sobot.utils.SobotLogUtils;
import com.sobot.utils.SobotStringUtils;
import com.sobot.widget.ui.toast.SobotToastUtil;
import com.sobot.workorder.R;
import com.sobot.workorder.utils.SobotDateUtil;
import com.sobot.workorder.utils.SobotWorkOrderUtils;
import com.sobot.workorder.weight.dialog.SobotActionItem;
import com.sobot.workorder.weight.dialog.SobotSpeedMenu;
import com.sobot.workorder.weight.voice.AudioInfoModel;
import com.sobot.workorder.weight.voice.AudioPlayCallBack;
import com.sobot.workorder.weight.voice.AudioPlayPresenter;
import com.sobot.workorder.weight.voice.AudioTools;
import com.sobot.workorderlibrary.api.model.SobotFileItemModel;
import com.sobot.workorderlibrary.api.model.SobotOrderReplyItemModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SobotWorkOrderDetailReplyAdapter extends BaseAdapter {

    boolean isCanEdit = true;
    List<SobotOrderReplyItemModel> list;
    Context context;
    private SobotOnItemClickListener deleteListener;

    private boolean isPauseRecording;//是否暂停播放录音
    private long playingRecordingDuration;//正在播放的录音时长
    private String playingRecordingUrl;//正在播放的录音地址

    private TextView m_voice_time;
    private SeekBar m_voice_schedule;
    private ImageView m_voice_play;//播放
    private ImageView m_voice_pause;//暂停
    private Runnable runnable;
    private PopupWindow popupWindow;
    private Handler handler = new Handler();
    //语音相关， 听筒模式转换
    public AudioManager audioManager = null; // 声音管理器
    private AudioFocusRequest mFocusRequest;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private AudioAttributes mAttribute;
    AudioPlayPresenter mAudioPlayPresenter = null;
    AudioPlayCallBack mAudioPlayCallBack = null;

    public SobotWorkOrderDetailReplyAdapter(Context context, List<SobotOrderReplyItemModel> list) {
        this.isCanEdit = isCanEdit;
        this.list = list;
        this.context = context;
    }

    public void setDeleteListener(SobotOnItemClickListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public SobotWorkOrderDetailReplyAdapter(Context context, List<SobotOrderReplyItemModel> list, boolean isCanEdit) {
        this.isCanEdit = isCanEdit;
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        if (list != null) {
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.sobot_item_detail_reply, null);
            holder = new ViewHolder(context, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SobotOrderReplyItemModel bean = list.get(position);
        SobotBitmapUtil.display(context, bean.getFaceImg(), holder.ivHead, R.drawable.sobot_icon_custom_select_pic_error, R.drawable.sobot_icon_custom_select_pic_error);
        holder.tvUserName.setText(bean.getDealUserName());
        //设置时间
        long time = bean.getReplyTime();
        if (time < 10000000000l) {
            time = time * 1000;
        }
        holder.tvCreateTime.setText(SobotDateUtil.DATE_FORMAT3.format(new Date(time)));
        if (bean != null && !TextUtils.isEmpty(bean.getReplyContent())) {
            holder.tvContent.setText(Html.fromHtml(bean.getReplyContent().replace("<p>", "").replace
                    ("</p>", "").replaceAll("<img[^>]*>", "[图片]")));
            holder.tvContent.setVisibility(View.VISIBLE);
        } else {
            holder.tvContent.setText("");
            holder.tvContent.setVisibility(View.GONE);
        }
        List<SobotFileItemModel> fileList = bean.getFileList();
        holder.llFileList.removeAllViews();
        if (fileList.size() > 0) {
            // 添加列表
            SobotWorkOrderUtils.updateFileList(context, fileList, holder.llFileList);
        }

        if (bean.getDealLogType() != null && bean.getDealLogType().intValue() == 1) {
            //电话回复
            holder.tv_relay_type.setText(context.getResources().getString(R.string.sobot_call_reply_type_string));
            holder.tv_relay_type.setTextColor(context.getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
        } else {
            if (bean.getReplyType() == 1) {
                //内部备注
                holder.tv_relay_type.setText(context.getResources().getString(R.string.sobot_internal_remarks));
                holder.tv_relay_type.setTextColor(context.getResources().getColor(R.color.sobot_wo_yellow_color));
            } else {
                //所有人可见
                holder.tv_relay_type.setText(context.getResources().getString(R.string.sobot_detail_order_reply_string));
                holder.tv_relay_type.setTextColor(context.getResources().getColor(R.color.sobot_wo_new_wenzi_gray2));
            }
        }

        if (SobotPermissionManager.isHasPermission(SobotPermissionManager.USER_PERMISSION_TYPE_DEL_WORK_ORDER_REPLY) && isCanEdit) {
            //超管：3333  管理员：2222
            //超管和管理员时  删除按钮显示
            holder.iv_delete.setVisibility(View.VISIBLE);
            final ViewHolder finalHolder1 = holder;
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteListener != null)
                        deleteListener.onItemClick(bean.getDealLogId(), position, finalHolder1.iv_delete);
                }
            });
        } else {
            holder.iv_delete.setVisibility(View.GONE);
        }
        final ViewHolder finalHolder = holder;
        if (!SobotStringUtils.isEmpty(bean.getCallId())) {
            holder.tv_call.setVisibility(View.VISIBLE);
            holder.ll_call_id.setVisibility(View.VISIBLE);
            holder.tv_call_id.setText(SobotStringUtils.checkStringIsNull(bean.getCallId()));
            holder.iv_call_id_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(finalHolder.tv_call_id.getText().toString())) {
                        ClipboardManager cmb = (ClipboardManager) context.getApplicationContext().getSystemService(
                                Context.CLIPBOARD_SERVICE);
                        cmb.setText(finalHolder.tv_call_id.getText().toString());
                        cmb.getText();
                        SobotToastUtil.showCustomToast(context, context.getResources().getString(R.string.sobot_ctrl_v_success));
                    }
                }
            });
        } else {
            holder.tv_call.setVisibility(View.GONE);
            holder.ll_call_id.setVisibility(View.GONE);
        }
        //是否有录音
        if (!SobotStringUtils.isEmpty(bean.getCallRecordUrl())) {
            //有录音
            holder.ll_voice_sound.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.ll_voice_sound.getLayoutParams();
            lp.setMargins(0, 0, 0, SobotDensityUtil.dp2px(context, 12));
            final AudioInfoModel au = new AudioInfoModel();
            au.setAudioUrl(bean.getCallRecordUrl());
            au.setDuration(bean.getDuration());
            au.setSpeed(bean.getTmpSpeed());
            holder.tv_voice_time.setText("00:00/" + SobotDateUtil.formatCallTimeDurationTwo(bean.getDuration()));
            holder.iv_voice_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isPauseRecording = true;
                    finalHolder.iv_voice_play.setVisibility(View.VISIBLE);
                    finalHolder.iv_voice_pause.setVisibility(View.GONE);
                    AudioTools.getInstance().pause();
                }
            });
            holder.iv_voice_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalHolder.iv_voice_play.setVisibility(View.GONE);
                    finalHolder.iv_voice_pause.setVisibility(View.VISIBLE);
                    au.setVoideIsPlaying(true);

                    if (!isPauseRecording || (!TextUtils.isEmpty(playingRecordingUrl) && !playingRecordingUrl.equals(au.getAudioUrl()))) {
                        //播放
                        clickPlayAudio(au);
                    } else {
                        //继续播放
                        AudioTools.getInstance().start();
                    }
                    voiceRunnable(au.getAudioUrl(), au.getDuration(), finalHolder.iv_voice_play, finalHolder.iv_voice_pause, finalHolder.sb_voice_schedule, finalHolder.tv_voice_time, finalHolder.tv_voice_play_frequency);
                }
            });
            holder.tv_voice_play_frequency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<SobotActionItem> lineStatusActionItems = new ArrayList<>();

                    lineStatusActionItems.add(new SobotActionItem("0.5", false, 0.5f));
                    lineStatusActionItems.add(new SobotActionItem("1.0", false, 1.0f));
                    lineStatusActionItems.add(new SobotActionItem("1.25", false, 1.25f));
                    lineStatusActionItems.add(new SobotActionItem("1.5", false, 1.5f));
                    lineStatusActionItems.add(new SobotActionItem("2.0", false, 2.0f));
                    SobotSpeedMenu callfrequencyMenu = new SobotSpeedMenu(context, lineStatusActionItems, SobotDensityUtil.dp2px(context, 100), new SobotSpeedMenu.PopItemClick() {
                        @Override
                        public void onPopItemClick(SobotActionItem item, int index) {
                            if (item != null) {
                                bean.setTmpSpeed((Float) item.obj);
                                au.setSpeed((Float) item.obj);
                                finalHolder.tv_voice_play_frequency.setText("x " + item.mTitle);
                                //倍速播放
                                if (AudioTools.getInstance().isPlaying()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        AudioTools.getInstance().setPlaybackParams(new PlaybackParams().setSpeed((Float) item.obj));
                                    }
                                }
                            }
                            popupWindow.dismiss();
                        }
                    });
                    popupWindow = callfrequencyMenu.getPopWindow();
                    popupWindow.showAsDropDown(finalHolder.tv_voice_play_frequency, SobotDensityUtil.dp2px(context, -50), 0);
                }
            });
            holder.sb_voice_schedule.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {// 注意点
                        try {
//                                                if (!AudioTools.getInstance().isPlaying()) {
//                                                    try {
//                                                        AudioTools.getInstance().reset();
//                                                        AudioTools.getInstance().setDataSource(au.getAudioUrl());
//                                                        AudioTools.getInstance().prepareAsync();
//                                                        // mPlayer.prepare();
//                                                        AudioTools.getInstance().start();
//                                                    } catch (IllegalStateException |
//                                                             IOException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
                            AudioTools.getInstance().seekTo(seekBar.getProgress());
                        } catch (IllegalStateException e) {
                            e.printStackTrace();

                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        } else {
            //没有录音
            holder.ll_voice_sound.setVisibility(View.GONE);
        }
        return convertView;
    }


    static class ViewHolder {
        ImageView ivHead;
        TextView tvUserName;
        TextView tv_relay_type;
        TextView tvCreateTime;
        TextView tvContent;
        LinearLayout llFileList;
        ImageView iv_delete;
        //--------音频相关开始--------------
        LinearLayout ll_voice_sound;
        ImageView iv_voice_play;
        ImageView iv_voice_pause;
        SeekBar sb_voice_schedule;
        TextView tv_voice_time;
        TextView tv_voice_play_frequency;
        //----------音频相关结束------------
        TextView tv_call;
        TextView tv_call_id;
        ImageView iv_call_id_copy;
        LinearLayout ll_call_id;

        ViewHolder(Context context, View view) {
            ivHead = view.findViewById(R.id.iv_head);
            tvUserName = view.findViewById(R.id.tv_user_name);
            tvCreateTime = view.findViewById(R.id.tv_create_time);
            tv_relay_type = view.findViewById(R.id.tv_relay_type);
            tvContent = view.findViewById(R.id.tv_content);
            llFileList = view.findViewById(R.id.ll_file_container);
            iv_delete = view.findViewById(R.id.iv_delete);

            ll_voice_sound = view.findViewById(R.id.ll_voice_sound);
            iv_voice_play = view.findViewById(R.id.iv_voice_play);
            iv_voice_pause = view.findViewById(R.id.iv_voice_pause);
            sb_voice_schedule = view.findViewById(R.id.sb_voice_schedule);
            tv_voice_time = view.findViewById(R.id.tv_voice_time);
            tv_voice_play_frequency = view.findViewById(R.id.tv_voice_play_frequency);

            tv_call = view.findViewById(R.id.tv_call);
            tv_call_id = view.findViewById(R.id.tv_call_id);
            iv_call_id_copy = view.findViewById(R.id.iv_call_id_copy);
            ll_call_id = view.findViewById(R.id.ll_call_id);
        }
    }

    public void voiceRunnable(String audioUrl, long duration, ImageView iv_voice_play, ImageView iv_voice_pause, SeekBar sb_voice_schedule, TextView tv_voice_time, TextView tv_voice_play_frequency) {
        if (!TextUtils.isEmpty(playingRecordingUrl) && !playingRecordingUrl.equals(audioUrl)) {
            if (m_voice_time != null) {
                m_voice_time.setText("00:00/" + SobotDateUtil.formatCallTimeDurationTwo(playingRecordingDuration));
            }
            if (m_voice_play != null) {
                m_voice_play.setVisibility(View.VISIBLE);
            }
            if (m_voice_pause != null) {
                m_voice_pause.setVisibility(View.GONE);
            }
            if (m_voice_schedule != null) {
                m_voice_schedule.setProgress(0);
            }
        }
        //更新成新的播放控件和内容
        m_voice_time = tv_voice_time;
        m_voice_schedule = sb_voice_schedule;
        m_voice_play = iv_voice_play;
        m_voice_play.setVisibility(View.GONE);
        m_voice_pause = iv_voice_pause;
        m_voice_pause.setVisibility(View.VISIBLE);
        playingRecordingUrl = audioUrl;
        playingRecordingDuration = duration;
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            runnable = null;
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 200);
                if (AudioTools.getInstance().isPlaying()) {
                    int progress = AudioTools.getInstance().getCurrentPosition();
                    int max = AudioTools.getInstance().getDuration();
                    SobotLogUtils.i("Current position: " + progress + " duration: " + max + "播放进度");
                    m_voice_schedule.setProgress(progress);
                    m_voice_schedule.setMax(max);
                    if (playingRecordingDuration >= 0) {
                        m_voice_time.setText(parseTime(progress) + "/" + SobotDateUtil.formatCallTimeDurationTwo(playingRecordingDuration));
                    } else {
                        m_voice_time.setText(parseTime(progress) + "/" + parseTime(max));
                    }
                } else {
                    m_voice_time.setText("00:00/" + SobotDateUtil.formatCallTimeDurationTwo(playingRecordingDuration));
                    m_voice_schedule.setProgress(0);
//                    SobotLogUtils.i("播放进度" + "100%");
                }
            }
        };
        handler.postDelayed(runnable, 100);
    }


    // 点击播放录音
    public void clickPlayAudio(AudioInfoModel audioInfoModel) {
        if (AudioTools.getInstance().isPlaying()) {
            AudioTools.getInstance().stop();
        }
        abandonAudioFocus();
        initAudioManager();
        requestAudioFocus();
        float speed = audioInfoModel.getSpeed();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            AudioTools.getInstance().setPlaybackParams(new PlaybackParams().setSpeed(audioInfoModel.getSpeed()));
//        }
        if (mAudioPlayPresenter == null) {
            mAudioPlayPresenter = new AudioPlayPresenter(context.getApplicationContext());
        }
        if (mAudioPlayCallBack == null) {
            mAudioPlayCallBack = new AudioPlayCallBack() {
                @Override
                public void onPlayStart(AudioInfoModel audioInfoModel) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        AudioTools.getInstance().setPlaybackParams(new PlaybackParams().setSpeed(audioInfoModel.getSpeed()));
                    }
                }

                @Override
                public void onPlayEnd(AudioInfoModel audioInfoModel) {
                    isPauseRecording = false;
                    abandonAudioFocus();
                    if (handler != null && runnable != null) {
                        handler.removeCallbacks(runnable);
                        runnable = null;
                    }
                    m_voice_time.setText("00:00/" + SobotDateUtil.formatCallTimeDurationTwo(audioInfoModel.getDuration()));
                    m_voice_schedule.setProgress(0);
                    m_voice_play.setVisibility(View.VISIBLE);
                    m_voice_pause.setVisibility(View.GONE);
                }
            };
        }
        mAudioPlayPresenter.clickAudio(audioInfoModel, mAudioPlayCallBack);
    }

    // 设置听筒模式或者是正常模式的转换
    public void initAudioManager() {
        if (audioManager == null)
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        // TBD 继续播放
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        // TBD 停止播放
//                        if (AudioTools.getInstance().isPlaying()) {
//                            AudioTools.getInstance().stop();
//                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        // TBD 暂停播放
                        if (handler != null && runnable != null) {
                            handler.removeCallbacks(runnable);
                            runnable = null;
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // TBD 混音播放
                        break;
                    default:
                        break;
                }

            }
        };
        //android 版本 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAttribute = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
        }
        //android 版本 8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setWillPauseWhenDucked(true)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener, new Handler())
                    .setAudioAttributes(mAttribute)
                    .build();
        }
    }

    //请求音频焦点
    public void requestAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }
        if (audioManager == null)
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest != null)
                    audioManager.requestAudioFocus(mFocusRequest);
            } else {
                if (audioFocusChangeListener != null)
                    //AUDIOFOCUS_GAIN_TRANSIENT 只是短暂获得，一会就释放焦点
                    audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        }

    }

    //放弃音频焦点
    public void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (mFocusRequest != null)
                    audioManager.abandonAudioFocusRequest(mFocusRequest);
            } else {
                if (audioFocusChangeListener != null)
                    audioManager.abandonAudioFocus(audioFocusChangeListener);
            }
            audioManager = null;
        }
    }

    private String parseTime(long oldTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");// 时间格式
        String newTime = sdf.format(new Date(oldTime));
        return newTime;
    }

    public interface SobotOnItemClickListener {
        void onItemClick(Object info, int index, View view);
    }

}