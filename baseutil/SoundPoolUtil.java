package com.mxnavi.mobile.utils.baseutil;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.mxnavi.mobile.R;

import static android.content.Context.AUDIO_SERVICE;

/**
 * @zhaohj Created on 2018/12/11.
 */
public class SoundPoolUtil {
    public static SoundPoolUtil soundPoolUtil;
    public final static int ENGINE_START = 1;
    public final static int DOOR_LOCK = 2;
    public final static int TRUNK = 3;
    public final static int DOOR_UNLOCK = 4;
    public SoundPool soundPool;
    private final float mAudioMaxVolumn;
    private final float mAudioCurrentVolumn;
    private final float mVolumnRatio;

    private SoundPoolUtil(Context context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(AUDIO_SERVICE);
        mAudioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mVolumnRatio = mAudioCurrentVolumn / mAudioMaxVolumn;
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        //加载音频文件
        soundPool.load(context, R.raw.engine_start, 1);
        soundPool.load(context, R.raw.door_lock, 1);
        soundPool.load(context, R.raw.trunk, 1);
        soundPool.load(context, R.raw.door_unlock, 1);
    }

    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    public void play(int number) {
        //播放音频
        soundPool.play(number, mVolumnRatio, mVolumnRatio, 0, 0, 1);
    }

}
