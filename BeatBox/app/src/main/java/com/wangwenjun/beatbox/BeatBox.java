package com.wangwenjun.beatbox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 */

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";

    private static final int MAX_SOUNDS = 5;

    private AssetManager mAssetManager;
    private List<Sound> mSounds = new ArrayList<Sound>();
    private SoundPool mSoundPool;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BeatBox(Context context){
        mAssetManager = context.getAssets();

        //这个构造方法已经弃用，新的使用SoundPool.Builder, 为了兼容使用下面的
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        loadSounds();
    }

    public void play(Sound sound){
        Integer sounId = sound.getSoundId();
        if (sound == null){
            return;
        }
        mSoundPool.play(sounId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release(){
        mSoundPool.release();
    }

    private void loadSounds(){
        String[] soundNames;
        try {
            soundNames = mAssetManager.list(SOUNDS_FOLDER);
            Log.i(TAG, "Found " + soundNames.length + " sounds");
        } catch (IOException e) {
            Log.e(TAG, "Could not list assets", e);
            return;
        }

        for (String filename : soundNames){
            Sound sound = new Sound(SOUNDS_FOLDER + "/" + filename);
            try {
                load(sound);
            } catch (IOException e) {
                Log.e(TAG, "Could not load sound " + filename ,e);
            }
            mSounds.add(sound);
        }
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor assetFileDescriptor = mAssetManager
                .openFd(sound.getAssetPaTH());
        //加载到内存
        int soundId = mSoundPool.load(assetFileDescriptor, 1);
        sound.setSoundId(soundId);
    }


    public List<Sound> getSounds(){
        return mSounds;
    }
}
