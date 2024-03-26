package com.example.audioplayer;

import android.media.MediaPlayer;

import androidx.lifecycle.ViewModel;

import java.io.File;

public class ActivityViewModel extends ViewModel {
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    public PlayList mPlayList = new PlayList();
    public AudioList mAudioList = new AudioList();
    public Integer mCurrentlyPlaying = 0;
    public Boolean mItemSelected = false;

    public void IncrementCurrentlyPlaying() {
        mCurrentlyPlaying++;
        if (mCurrentlyPlaying == mAudioList.mAudios.size()) {
            mCurrentlyPlaying = 0;
        }
    }

    public void DecrementCurrentlyPlaying() {
        mCurrentlyPlaying--;
        if (mCurrentlyPlaying < 0) {
            mCurrentlyPlaying = mAudioList.mAudios.size() - 1;
        }
    }

    public void LoadAudios(String path) {
        mAudioList.LoadAudios(new File(path));
    }

    public void LoadPlayList(String path) {
        mPlayList.LoadList(new File(path));
    }
}