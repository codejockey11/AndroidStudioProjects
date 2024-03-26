package com.example.audioplayer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.audioplayer.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.time.Duration;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding mBinding;
    private ActivityViewModel mActivityViewModel;
    private CountDownTimer mCountDownTimer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mActivityViewModel =
                new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        if (!mActivityViewModel.mMediaPlayer.isPlaying()) {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                if(mActivityViewModel.mItemSelected) {
                    try {
                        mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
                        mActivityViewModel.mMediaPlayer.reset();
                        mActivityViewModel.mMediaPlayer.setDataSource(mActivityViewModel.mAudioList.mUris.get(mActivityViewModel.mCurrentlyPlaying));
                    } catch (IOException e) {
                        LogError(e);
                    }

                    try {
                        mActivityViewModel.mMediaPlayer.prepare();
                    } catch (IOException e) {
                        LogError(e);
                    }

                    StartPlayer();
                } else {
                    TogglePlayPause(View.INVISIBLE);
                    mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
                    StartCountdown();
                }
            }
        } else {
            TogglePlayPause(View.INVISIBLE);
            mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
            StartCountdown();
        }

        mBinding.buttonIcMediaPrevious.setOnClickListener(view -> {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                try {
                    mActivityViewModel.DecrementCurrentlyPlaying();
                    mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
                    mActivityViewModel.mMediaPlayer.reset();
                    mActivityViewModel.mMediaPlayer.setDataSource(mActivityViewModel.mAudioList.mUris.get(mActivityViewModel.mCurrentlyPlaying));
                } catch (IOException e) {
                    LogError(e);
                }

                try {
                    mActivityViewModel.mMediaPlayer.prepare();
                } catch (IOException e) {
                    LogError(e);
                }

                StartPlayer();
            }
        });

        mBinding.buttonIcMediaRew.setOnClickListener(view -> {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                mActivityViewModel.mMediaPlayer.seekTo(mActivityViewModel.mMediaPlayer.getCurrentPosition() - 2000);
            }
        });

        mBinding.buttonIcMediaPause.setOnClickListener(view -> {
            TogglePlayPause(View.VISIBLE);
            mActivityViewModel.mMediaPlayer.pause();
        });

        mBinding.buttonIcMediaPlay.setOnClickListener(view ->
                StartPlayer());

        mBinding.buttonIcMediaFf.setOnClickListener(view -> {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                mActivityViewModel.mMediaPlayer.seekTo(mActivityViewModel.mMediaPlayer.getCurrentPosition() + 2000);
            }
        });

        mBinding.buttonIcMediaNext.setOnClickListener(view -> {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                try {
                    mActivityViewModel.IncrementCurrentlyPlaying();
                    mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
                    mActivityViewModel.mMediaPlayer.reset();
                    mActivityViewModel.mMediaPlayer.setDataSource(mActivityViewModel.mAudioList.mUris.get(mActivityViewModel.mCurrentlyPlaying));
                } catch (IOException e) {
                    LogError(e);
                }

                try {
                    mActivityViewModel.mMediaPlayer.prepare();
                } catch (IOException e) {
                    LogError(e);
                }

                StartPlayer();
            }
        });

        mActivityViewModel.mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            try {
                mActivityViewModel.IncrementCurrentlyPlaying();
                mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
                mActivityViewModel.mMediaPlayer.reset();
                mActivityViewModel.mMediaPlayer.setDataSource(mActivityViewModel.mAudioList.mUris.get(mActivityViewModel.mCurrentlyPlaying));
            } catch (IOException e) {
                LogError(e);
            }
            try {
                mActivityViewModel.mMediaPlayer.prepare();
            } catch (IOException e) {
                LogError(e);
            }

            StartPlayer();
        });

        mBinding.buttonExit.setOnClickListener(view -> {
                    if (mActivityViewModel.mMediaPlayer.isPlaying()) {
                        mActivityViewModel.mMediaPlayer.stop();
                        mActivityViewModel.mMediaPlayer.reset();
                    }

                    mActivityViewModel.mMediaPlayer.release();

                    getActivity().finish();
                }
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        mBinding = null;
    }

    private void TogglePlayPause(int visibility) {
        if (mBinding.buttonIcMediaPlay.getVisibility() == visibility) {
            return;
        }

        if (mBinding.buttonIcMediaPlay.getVisibility() == View.INVISIBLE) {
            mBinding.buttonIcMediaPlay.setVisibility(View.VISIBLE);
            mBinding.buttonIcMediaPause.setVisibility(View.INVISIBLE);
        } else {
            mBinding.buttonIcMediaPlay.setVisibility(View.INVISIBLE);
            mBinding.buttonIcMediaPause.setVisibility(View.VISIBLE);
        }
    }

    private void StartCountdown() {
        mCountDownTimer = new CountDownTimer(
                mActivityViewModel.mMediaPlayer.getDuration(), 50) {
            public void onTick(long millisUntilFinished) {
                if (mBinding == null) {
                    return;
                }
                Duration duration = Duration.ofMillis(mActivityViewModel.mMediaPlayer.getDuration() - mActivityViewModel.mMediaPlayer.getCurrentPosition());
                long seconds = duration.getSeconds();
                long HH = seconds / 3600;
                long MM = (seconds % 3600) / 60;
                long SS = seconds % 60;
                String timeInHHMMSS = String.format(Locale.getDefault(), "%02d:%02d:%02d", HH, MM, SS);
                mBinding.textTimeRemaining.setText(timeInHHMMSS);
            }

            public void onFinish() {
                if (mBinding == null) {
                    return;
                }
                mBinding.textTimeRemaining.setText(R.string.done);
            }
        }.start();
    }

    private void StartPlayer() {
        if (mActivityViewModel.mAudioList.mUris.isEmpty()) {
            return;
        }

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }

        TogglePlayPause(View.INVISIBLE);

        mActivityViewModel.mMediaPlayer.start();

        StartCountdown();
    }

    private void LogError(Exception e) {
        if (e == null) {
            return;
        }

        if (e.getMessage() != null) {
            android.util.Log.e("Exception", e.getMessage());
        }
    }
}