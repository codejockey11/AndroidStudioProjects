package com.example.audioplayer;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private BroadcastReceiver mBroadcastReceiver;
    private CountDownTimer mCountDownTimer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mActivityViewModel =
                new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        AttachBluetoothReceiver();

        CheckReturning();

        mBinding.buttonIcMediaPrevious.setOnClickListener(view -> {
            PreviousTrack();
        });

        mBinding.buttonIcMediaRew.setOnClickListener(view -> {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                mActivityViewModel.mMediaPlayer.seekTo(mActivityViewModel.mMediaPlayer.getCurrentPosition() - 2000);
            }
        });

        mBinding.buttonIcMediaPause.setOnClickListener(view -> {
            PlayPause();
        });

        mBinding.buttonIcMediaPlay.setOnClickListener(view -> {
            PlayPause();
        });

        mBinding.buttonIcMediaFf.setOnClickListener(view -> {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                mActivityViewModel.mMediaPlayer.seekTo(mActivityViewModel.mMediaPlayer.getCurrentPosition() + 2000);
            }
        });

        mBinding.buttonIcMediaNext.setOnClickListener(view -> {
            NextTrack();
        });

        mActivityViewModel.mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
            NextTrack();
        });

        mBinding.buttonExit.setOnClickListener(view -> {
            Exit();
        });

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

    private void Exit() {
        if (mActivityViewModel.mMediaPlayer.isPlaying()) {
            mActivityViewModel.mMediaPlayer.stop();
            mActivityViewModel.mMediaPlayer.reset();
        }

        mActivityViewModel.mMediaPlayer.release();

        getActivity().finish();
    }

    private void AttachBluetoothReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() != Intent.ACTION_MEDIA_BUTTON) {
                    return;
                }

                KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS: {
                        Toast.makeText(mBinding.getRoot().getContext(), "KEYCODE_MEDIA_PREVIOUS", Toast.LENGTH_LONG).show();
                        PreviousTrack();
                        break;
                    }
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                    case KeyEvent.KEYCODE_MEDIA_PAUSE: {
                        Toast.makeText(mBinding.getRoot().getContext(), "KEYCODE_MEDIA_PLAY_PAUSE", Toast.LENGTH_LONG).show();
                        PlayPause();
                        break;
                    }
                    case KeyEvent.KEYCODE_MEDIA_NEXT: {
                        Toast.makeText(mBinding.getRoot().getContext(), "KEYCODE_MEDIA_NEXT", Toast.LENGTH_LONG).show();
                        NextTrack();
                        break;
                    }
                }
            }
        };
    }

    private void CheckReturning() {
        if (mActivityViewModel.mMediaPlayer.isPlaying()) {
            TogglePlayPause(View.INVISIBLE);
            mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
            StartCountdown();

            return;
        }

        if (mActivityViewModel.mAudioList.mUris.isEmpty()) {
            return;
        }

        if (!mActivityViewModel.mItemSelected) {
            if (!mActivityViewModel.mAudioList.mUris.isEmpty()) {
                return;
            }

            TogglePlayPause(View.INVISIBLE);
            mBinding.textMediaPlaying.setText(mActivityViewModel.mAudioList.mAudios.get(mActivityViewModel.mCurrentlyPlaying));
            StartCountdown();

            return;
        }

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
    }

    private void PreviousTrack() {
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
    }

    private void PlayPause() {
        if (mActivityViewModel.mMediaPlayer.isPlaying()) {
            TogglePlayPause(View.VISIBLE);
            mActivityViewModel.mMediaPlayer.pause();
            return;
        }

        StartPlayer();
    }

    private void NextTrack() {
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