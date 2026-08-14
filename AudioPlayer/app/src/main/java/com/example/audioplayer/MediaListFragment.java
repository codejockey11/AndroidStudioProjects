package com.example.audioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.audioplayer.databinding.MediaListFragmentBinding;

import java.util.Objects;

public class MediaListFragment extends Fragment {
    private MediaListFragmentBinding mBinding;
    private ActivityViewModel mActivityViewModel;
    public MediaListAdapter mMediaListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = MediaListFragmentBinding.inflate(inflater, container, false);

        View root = mBinding.getRoot();

        mActivityViewModel =
                new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        mActivityViewModel.mMediaPlayer.SetView(this.getView());

        mBinding.mediaList.setOnItemClickListener((parent, view, position, id) -> {
            if (Objects.equals(mActivityViewModel.getAppState(),
                    mActivityViewModel.mAppStatePlaylistMediaListActive)) {
                mActivityViewModel.mPlayList.AddItem(mMediaListAdapter.getItem(position));

                return;
            }

            if (mActivityViewModel.mMediaPlayer.isPlaying()) {
                mActivityViewModel.mMediaPlayer.StopPlayer();
            }

            mActivityViewModel.mPlayList.mActivePlaylist = "zEmpty";

            mActivityViewModel.mSharedPreferences.edit()
                    .putString("mPlaylist", "zEmpty")
                    .apply();

            mActivityViewModel.mMediaPlayer.SetList(mActivityViewModel.mMediaList.mAudios);
            mActivityViewModel.mMediaPlayer.SetCurrentlyPlaying(
                    Objects.requireNonNull(mMediaListAdapter.getItem(position)).mIndex);
            mActivityViewModel.mMediaPlayer.StartPlayer();

            mActivityViewModel.setAppState(mActivityViewModel.mAppStateMediaListActive);

            View v = requireActivity().findViewById(R.id.nav_host_fragment_activity_main);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_player);
        });

        if ((mActivityViewModel.mMediaList.mSubset == null) ||
                ((mActivityViewModel.mMediaList.mSubset.isEmpty()))) {
            mMediaListAdapter =
                    new MediaListAdapter(this.requireContext(), mActivityViewModel.mMediaList.mAudios);
        } else {
            mMediaListAdapter =
                    new MediaListAdapter(this.requireContext(), mActivityViewModel.mMediaList.mSubset);
        }

        mBinding.mediaList.setAdapter(mMediaListAdapter);

        if (mActivityViewModel.mMediaSession != null) {
            mActivityViewModel.mMediaSession.setCallback(null);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}