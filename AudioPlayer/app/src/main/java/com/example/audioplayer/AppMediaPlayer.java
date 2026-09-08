package com.example.audioplayer;

import android.content.SharedPreferences;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.CountDownTimer;
import android.view.View;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;

public class AppMediaPlayer extends MediaPlayer {
    public SharedPreferences mSharedPreferences;
    private MediaSession mMediaSession;
    private final ErrorToaster mErrorToaster;
    private View mView;
    public ArrayList<MediaListItem> mList;
    public Integer mCurrentlyPlaying = 0;
    private CountDownTimer mTimeRemainingTimer;
    public String mTimeRemaining;
    public MediaMetadata.Builder mMetadata;
    public PlaybackState.Builder playbackState;

    public AppMediaPlayer(ErrorToaster errorToaster) {
        mErrorToaster = errorToaster;

        mMetadata = new MediaMetadata.Builder();

        playbackState = new PlaybackState.Builder();
    }

    public void SetCurrentlyPlaying(Integer currentlyPlaying) {
        mCurrentlyPlaying = currentlyPlaying;

        if (mList == null) {
            return;
        }

        try {
            MediaListItem mediaListItem = mList.get(mCurrentlyPlaying);
            reset();
            setDataSource(mediaListItem.mUri);
        } catch (Exception e) {
            mErrorToaster.LogError(mView, e);

            return;
        }

        try {
            prepare();
        } catch (Exception e) {
            mErrorToaster.LogError(mView, e);

            return;
        }

        UpdateTimeRemaining();

        SetSessionInfo();

        if (isPlaying()) {
            UpdatePlaybackState(PlaybackState.STATE_PLAYING);
        } else {
            UpdatePlaybackState(PlaybackState.STATE_PAUSED);
        }
    }

    public void SetSessionInfo() {
        MediaListItem mediaListItem = mList.get(mCurrentlyPlaying);

        mSharedPreferences.edit()
                .putString("mArtist", mediaListItem.mArtist)
                .putString("mAlbum", mediaListItem.mAlbum)
                .putString("mTitle", mediaListItem.mTitle)
                .putString("mUri", mediaListItem.mUri)
                .putInt("mCurrentlyPlaying", mCurrentlyPlaying)
                .putInt("mDuration", getDuration())
                .apply();

        if (mMediaSession == null) {
            return;
        }

        MediaMetadata metadata = new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_ARTIST, mediaListItem.mArtist)
                .putString(MediaMetadata.METADATA_KEY_ALBUM, mediaListItem.mAlbum)
                .putString(MediaMetadata.METADATA_KEY_TITLE, mediaListItem.mTitle)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, getDuration())
                .build();

        mMediaSession.setMetadata(metadata);
    }

    public void SetSharedPreferencesEditor(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    public void SetList(ArrayList<MediaListItem> list) {
        mList = list;
    }

    public void SetMediaSession(MediaSession mediaSession) {
        mMediaSession = mediaSession;
    }

    public void SetSeek(Integer delta) {
        int milliseconds = getCurrentPosition() + delta;

        seekTo(milliseconds);

        if (isPlaying()) {
            UpdatePlaybackState(PlaybackState.STATE_PLAYING);
        } else {
            UpdatePlaybackState(PlaybackState.STATE_PAUSED);
        }
    }

    public void SetView(View view) {
        mView = view;
    }

    public MediaListItem GetCurrentMediaListItem() {
        if (mList == null) {
            return null;
        }

        try {
            return mList.get(mCurrentlyPlaying);
        } catch (Exception e) {
            mErrorToaster.LogError(mView, e);
        }

        SetCurrentlyPlaying(0);

        return mList.get(mCurrentlyPlaying);
    }

    public void NextTrack() {
        if (mList == null) {
            return;
        }

        mCurrentlyPlaying++;
        if (mCurrentlyPlaying >= mList.size()) {
            mCurrentlyPlaying = 0;
        }

        SetCurrentlyPlaying(mCurrentlyPlaying);
    }

    public void PausePlayer() {
        try {
            pause();
        } catch (Exception e) {
            mErrorToaster.LogError(mView, e);

            return;
        }

        UpdatePlaybackState(PlaybackState.STATE_PAUSED);

        UpdateTimeRemaining();
    }

    public void PreviousTrack() {
        if (mList == null) {
            return;
        }

        mCurrentlyPlaying--;
        if (mCurrentlyPlaying < 0) {
            mCurrentlyPlaying = mList.size() - 1;
        }

        SetCurrentlyPlaying(mCurrentlyPlaying);
    }

    public void Shutdown() {
        StopPlayer();

        if (mTimeRemainingTimer != null) {
            mTimeRemainingTimer.cancel();
        }

        if (mMediaSession != null) {
            SetSessionInfo();

            UpdatePlaybackState(PlaybackState.STATE_STOPPED);

            mMediaSession.setActive(false);
            mMediaSession.release();
        }

        release();
    }

    public void StartPlayer() {
        try {
            start();
        } catch (Exception e) {
            mErrorToaster.LogError(mView, e);

            return;
        }

        UpdatePlaybackState(PlaybackState.STATE_PLAYING);

        UpdateTimeRemaining();
    }

    public void StopPlayer() {
        try {
            stop();
        } catch (Exception e) {
            mErrorToaster.LogError(mView, e);

            return;
        }

        UpdatePlaybackState(PlaybackState.STATE_STOPPED);

        UpdateTimeRemaining();
    }

    public void UpdateTimeRemaining() {
        if (mTimeRemainingTimer != null) {
            mTimeRemainingTimer.start();

            return;
        }

        AppMediaPlayer appMediaPlayer = this;

        mTimeRemainingTimer = new CountDownTimer(24 * 60 * 60, 1000) {
            public void onTick(long millisUntilFinished) {
                Duration duration = Duration.ofMillis(appMediaPlayer.getDuration() - appMediaPlayer.getCurrentPosition());

                long seconds = duration.getSeconds();
                long HH = seconds / 3600;
                long MM = (seconds % 3600) / 60;
                long SS = seconds % 60;

                mTimeRemaining = String.format(Locale.getDefault(), "%02d:%02d:%02d", HH, MM, SS);
            }

            public void onFinish() {
                mTimeRemaining = "00:00:00";
            }
        }.start();
    }

    private long UpdateActions(Integer state) {
        long actions =
                PlaybackState.ACTION_PLAY_PAUSE;

        if (mList == null || mList.isEmpty()) {
            return actions;
        }

        if (state == PlaybackState.STATE_PLAYING) {
            actions |= PlaybackState.ACTION_PAUSE;
        } else {
            actions |= PlaybackState.ACTION_PLAY;
        }

        if (mCurrentlyPlaying > 0) {
            actions |= PlaybackState.ACTION_SKIP_TO_PREVIOUS;
        }

        if (mCurrentlyPlaying < mList.size() - 1) {
            actions |= PlaybackState.ACTION_SKIP_TO_NEXT;
        }

        return actions;
    }

    public void UpdatePlaybackState(Integer state) {
        if (mMediaSession == null) {
            return;
        }

        long position = PlaybackState.PLAYBACK_POSITION_UNKNOWN;
        float speed = 0.0f;

        if ((isPlaying()) || (state == PlaybackState.STATE_PLAYING)) {
            position = getCurrentPosition();
            speed = 1.0f;
        }

        playbackState
                .setActions(UpdateActions(state))
                .setState(state, position, speed);

        mMediaSession.setPlaybackState(playbackState.build());
    }
}