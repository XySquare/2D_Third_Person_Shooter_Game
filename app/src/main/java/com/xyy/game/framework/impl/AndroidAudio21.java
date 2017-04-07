package com.xyy.game.framework.impl;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;


import com.xyy.game.framework.Audio;
import com.xyy.game.framework.Music;
import com.xyy.game.framework.Sound;

import java.io.IOException;

/**
 * 用于API21(android 5.0)及以上版本
 * Created by ${XYY} on ${2016/3/5}.
 */
public final class AndroidAudio21 implements Audio {
    private AssetManager assets;
    private SoundPool soundPool;

    @TargetApi(21)
    public AndroidAudio21(Activity activity) {
        //设置媒体流的音量控制
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.assets = activity.getAssets();

        SoundPool.Builder soundPoolBuilder = new SoundPool.Builder();
        soundPoolBuilder.setMaxStreams(20);
        soundPoolBuilder.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME).build());
        soundPool = soundPoolBuilder.build();
    }

    @Override
    public Music newMusic(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            return new AndroidMusic(assetDescriptor);
        } catch (IOException e) {
            //该错误无法恢复，因此以RuntimeException代替IOException抛出
            throw new RuntimeException("Couldn't load music '" + filename + "'");
        }
    }

    @Override
    public Sound newSound(String filename) {
        try {
            AssetFileDescriptor assetDescriptor = assets.openFd(filename);
            int soundId = soundPool.load(assetDescriptor, 1);
            return new AndroidSound(soundPool, soundId);
        } catch (IOException e) {
            //该错误无法恢复，因此以RuntimeException代替IOException抛出
            throw new RuntimeException("Couldn't load sound '" + filename + "'");
        }
    }
}
