package com.example.audioplayer;

import android.content.SharedPreferences;
import android.media.session.MediaSession;

import androidx.lifecycle.ViewModel;

import java.io.File;

public class ActivityViewModel extends ViewModel {
    public ErrorToaster mErrorToaster;
    public File mSDCardDirectory;
    public File mAppStorageDirectory;
    public MediaSession mMediaSession;
    public AppMediaPlayer mMediaPlayer;
    public PlayList mPlayList;
    public MediaList mMediaList;
    private Integer mAppState = 0;
    public final Integer mAppStateMediaListActive = 0;
    public final Integer mAppStatePlaylistListActive = 1;
    public final Integer mAppStatePlaylistMediaListActive = 2;
    public SharedPreferences mSharedPreferences;

    public ActivityViewModel() {
        mErrorToaster = new ErrorToaster();

        mMediaPlayer = new AppMediaPlayer(mErrorToaster);
        mMediaPlayer.SetSharedPreferencesEditor(mSharedPreferences);

        mPlayList = new PlayList();
        mMediaList = new MediaList();
    }

    public void setAppState(Integer i) {
        mAppState = i;
    }

    public Integer getAppState() {
        return mAppState;
    }
}