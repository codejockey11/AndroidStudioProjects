package com.example.audioplayer;

import android.content.Intent;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.ResultReceiver;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.audioplayer.databinding.PlayerFragmentBinding;

public class PlayerFragment extends Fragment {
    private PlayerFragmentBinding mBinding;
    private View mView;
    private ActivityViewModel mActivityViewModel;
    private CountDownTimer mCountDownTimer;
    private final static Integer mFF = 2000;
    private final static Integer mRW = -2000;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = PlayerFragmentBinding.inflate(inflater, container, false);

        mView = mBinding.getRoot();

        mActivityViewModel =
                new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        mActivityViewModel.mMediaPlayer.SetView(this.getView());

        SetInformational();

        if (mActivityViewModel.mMediaPlayer.isPlaying()) {
            mBinding.buttonIcMediaPlay.setVisibility(View.INVISIBLE);
            mBinding.buttonIcMediaPause.setVisibility(View.VISIBLE);

            StartCountdown();
        } else {
            mBinding.buttonIcMediaPlay.setVisibility(View.VISIBLE);
            mBinding.buttonIcMediaPause.setVisibility(View.INVISIBLE);
        }

        mBinding.buttonIcMediaPrevious.setOnClickListener(view -> PreviousTrack());

        mBinding.buttonIcMediaRew.setOnClickListener(view -> SeekTo(mRW));

        mBinding.buttonIcMediaPlay.setOnClickListener(view -> PlayPause());

        mBinding.buttonIcMediaPause.setOnClickListener(view -> PlayPause());

        mBinding.buttonIcMediaFf.setOnClickListener(view -> SeekTo(mFF));

        mBinding.buttonIcMediaNext.setOnClickListener(view -> NextTrack());

        mBinding.buttonExit.setOnClickListener(view -> Exit());


        if (mActivityViewModel.mMediaSession == null) {
            return mView;
        }

        mActivityViewModel.mMediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onCommand(@NonNull String command, Bundle args, ResultReceiver cb) {
                mActivityViewModel.mErrorToaster.LogError(mView, command);
            }

            @Override
            public boolean onMediaButtonEvent(@NonNull Intent intent) {
                String action = intent.getAction();

                if (action == null) {
                    return false;
                }

                if (!action.equals(Intent.ACTION_MEDIA_BUTTON)) {
                    return false;
                }

                KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (keyEvent == null) {
                    return false;
                }

                if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    return false;
                }

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS: {
                        PreviousTrack();

                        break;
                    }

                    case KeyEvent.KEYCODE_MEDIA_REWIND: {
                        SeekTo(mRW);

                        break;
                    }

                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                    case KeyEvent.KEYCODE_MEDIA_PAUSE: {
                        PlayPause();

                        break;
                    }

                    case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                        SeekTo(mFF);

                        break;
                    }

                    case KeyEvent.KEYCODE_MEDIA_NEXT: {
                        NextTrack();

                        break;
                    }
                }

                return true;
            }
        });

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mBinding = null;
    }

    private void Exit() {
        mActivityViewModel.mMediaPlayer.Shutdown();

        requireActivity().finish();
    }

    private void SetInformational() {
        MediaListItem mediaListItem = mActivityViewModel.mMediaPlayer.GetCurrentMediaListItem();

        if (mediaListItem != null) {
            if (mediaListItem.mArtist.contains("zEmpty")) {
                mBinding.textMediaArtist.setVisibility(View.INVISIBLE);
            } else {
                mBinding.textMediaArtist.setText(mediaListItem.mArtist);
                mBinding.textMediaArtist.setVisibility(View.VISIBLE);
            }

            if (mediaListItem.mPerformer.contains("zEmpty")) {
                if (mediaListItem.mAlbum.contains("zEmpty")) {
                    mBinding.textMediaAlbum.setVisibility(View.INVISIBLE);
                } else {
                    mBinding.textMediaAlbum.setText(mediaListItem.mAlbum);
                    mBinding.textMediaAlbum.setVisibility(View.VISIBLE);
                }
            } else {
                mBinding.textMediaAlbum.setText(String.format("%s %s",
                        mediaListItem.mPerformer, mediaListItem.mAlbum));
                mBinding.textMediaAlbum.setVisibility(View.VISIBLE);
            }

            mBinding.textMediaTitle.setText(mediaListItem.mTitle);

            if (mActivityViewModel.mPlayList.mActivePlaylist.equals("zEmpty")) {
                mBinding.textActivePlaylist.setText(null);
            } else {
                mBinding.textActivePlaylist.setText(mActivityViewModel.mPlayList.mActivePlaylist);
            }

            StartCountdown();

            mActivityViewModel.mMediaPlayer.setOnCompletionListener(mediaPlayer -> NextTrack());

            return;
        }

        mBinding.textMediaArtist.setVisibility(View.VISIBLE);
        mBinding.textMediaAlbum.setVisibility(View.VISIBLE);

        mBinding.textMediaArtist.setText(null);
        mBinding.textMediaAlbum.setText(null);
        mBinding.textMediaTitle.setText(null);
        mBinding.textActivePlaylist.setText(null);
    }

    private void SeekTo(Integer delta) {
        mActivityViewModel.mMediaPlayer.SetSeek(delta);

        mBinding.textTimeRemaining.setText(mActivityViewModel.mMediaPlayer.mTimeRemaining);
    }

    private void PreviousTrack() {
        mActivityViewModel.mMediaPlayer.PreviousTrack();

        SetInformational();

        if (mBinding.buttonIcMediaPause.getVisibility() == View.VISIBLE) {
            mActivityViewModel.mMediaPlayer.StartPlayer();

            StartCountdown();
        }
    }

    private void PlayPause() {
        if (mActivityViewModel.mMediaPlayer.GetCurrentMediaListItem() == null) {
            return;
        }

        if (mActivityViewModel.mMediaPlayer.isPlaying()) {
            mActivityViewModel.mMediaPlayer.PausePlayer();

            mBinding.buttonIcMediaPlay.setVisibility(View.VISIBLE);
            mBinding.buttonIcMediaPause.setVisibility(View.INVISIBLE);

            return;
        }

        mBinding.buttonIcMediaPlay.setVisibility(View.INVISIBLE);
        mBinding.buttonIcMediaPause.setVisibility(View.VISIBLE);

        mActivityViewModel.mMediaPlayer.StartPlayer();

        StartCountdown();
    }

    private void NextTrack() {
        mActivityViewModel.mMediaPlayer.NextTrack();

        SetInformational();

        if (mBinding.buttonIcMediaPause.getVisibility() == View.VISIBLE) {
            mActivityViewModel.mMediaPlayer.StartPlayer();

            StartCountdown();
        }
    }

    public void StartCountdown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();

            return;
        }

        mCountDownTimer = new CountDownTimer(24 * 60 * 60, 1000) {
            public void onTick(long millisUntilFinished) {
                if (mBinding == null) {
                    return;
                }

                mBinding.textTimeRemaining.setText(mActivityViewModel.mMediaPlayer.mTimeRemaining);
            }

            public void onFinish() {
            }
        }.start();
    }
}