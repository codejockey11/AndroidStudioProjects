package com.example.audioplayer;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.audioplayer.databinding.PlaylistFragmentBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

public class PlaylistFragment extends Fragment {
    private PlaylistFragmentBinding mBinding;
    private ActivityViewModel mActivityViewModel;
    private PlaylistMediaAdapter mPlaylistMediaAdapter;
    private PlaylistListAdapter mPlaylistListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = PlaylistFragmentBinding.inflate(inflater, container, false);

        View root = mBinding.getRoot();

        mActivityViewModel =
                new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        mActivityViewModel.mMediaPlayer.SetView(this.getView());

        mPlaylistMediaAdapter =
                new PlaylistMediaAdapter(this.requireContext(),
                        mActivityViewModel.mPlayList.mMediaListItems);

        mPlaylistListAdapter =
                new PlaylistListAdapter(mBinding.getRoot().getContext(),
                        mActivityViewModel.mPlayList.mPlaylists);

        InitializeFragment();

        if (Objects.equals(mActivityViewModel.getAppState(), mActivityViewModel.mAppStatePlaylistMediaListActive)) {
            mBinding.playlistName.setText(mActivityViewModel.mPlayList.mActivePlaylist);
            mBinding.playlistList.setAdapter(mPlaylistMediaAdapter);
        } else {
            mBinding.playlistList.setAdapter(mPlaylistListAdapter);
        }

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

    public void InitializeFragment() {
        mBinding.playlistName.setOnEditorActionListener((view, id, event) -> {
            if (id == EditorInfo.IME_ACTION_GO) {
                mActivityViewModel.mPlayList.mActivePlaylist =
                        Objects.requireNonNull(mBinding.playlistName.getText()).toString();

                InputMethodManager inputMethodManager =
                        (InputMethodManager) requireActivity().getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mBinding.getRoot().getWindowToken(), 0);

                ActivatePlaylistByName();

                return true;
            }

            return false;
        });

        mBinding.buttonSaveList.setOnClickListener(button -> SaveList());

        mBinding.buttonDeleteList.setOnClickListener(button -> DeleteList());

        mBinding.buttonList.setOnClickListener(button -> ShowList());

        mBinding.buttonExitPlaylistMode.setOnClickListener(button -> ExitPlaylistMode());

        mBinding.playlistList.setOnItemClickListener(
                (parent, view, position, id) -> ActivatePlaylistByState(position));
    }

    public void SaveList() {
        mActivityViewModel.setAppState(mActivityViewModel.mAppStatePlaylistListActive);

        String name = Objects.requireNonNull(mBinding.playlistName.getText()).toString();

        String filename = String.format("%s/%s", mActivityViewModel.mAppStorageDirectory, name);

        try {
            mActivityViewModel.mPlayList.SaveMediaItems(filename);
        } catch (IOException e) {
            mActivityViewModel.mErrorToaster.LogError(this.getView(), e);

            return;
        }

        mActivityViewModel.mPlayList.mActivePlaylist = "zEmpty";

        mActivityViewModel.mSharedPreferences.edit()
                .putString("mPlaylist", "zEmpty")
                .apply();

        mActivityViewModel.mPlayList.LoadList(mActivityViewModel.mAppStorageDirectory);

        mBinding.playlistList.setAdapter(mPlaylistListAdapter);
    }

    public void DeleteList() {
        String name = Objects.requireNonNull(mBinding.playlistName.getText()).toString();

        File file = new File(mActivityViewModel.mAppStorageDirectory, name);

        mBinding.playlistName.setText("");

        if (mActivityViewModel.mPlayList.DeleteList(file)) {
            ShowList();
        }

        mActivityViewModel.setAppState(mActivityViewModel.mAppStatePlaylistListActive);

        mActivityViewModel.mPlayList.mActivePlaylist = "zEmpty";

        mActivityViewModel.mSharedPreferences.edit()
                .putString("mPlaylist", "zEmpty")
                .apply();
    }

    public void ShowList() {
        mActivityViewModel.setAppState(mActivityViewModel.mAppStatePlaylistListActive);

        mActivityViewModel.mPlayList.LoadList(mActivityViewModel.mAppStorageDirectory);

        mBinding.playlistList.setAdapter(mPlaylistListAdapter);
    }

    public void ExitPlaylistMode() {
        mActivityViewModel.mMediaPlayer.StopPlayer();
        mActivityViewModel.mMediaPlayer.SetList(mActivityViewModel.mMediaList.mAudios);
        mActivityViewModel.mMediaPlayer.SetCurrentlyPlaying(0);

        mActivityViewModel.setAppState(mActivityViewModel.mAppStateMediaListActive);

        mActivityViewModel.mPlayList.mActivePlaylist = "zEmpty";

        mActivityViewModel.mSharedPreferences.edit()
                .putString("mPlaylist", "zEmpty")
                .apply();

        View v = requireActivity().findViewById(R.id.nav_host_fragment_activity_main);
        NavController navController = Navigation.findNavController(v);
        navController.navigate(R.id.navigation_player);
    }

    public void ActivatePlaylistByState(Integer position) {
        if (Objects.equals(mActivityViewModel.getAppState(), mActivityViewModel.mAppStatePlaylistMediaListActive)) {
            if (mActivityViewModel.mPlayList.mMediaListItems.isEmpty()) {
                return;
            }

            if (mActivityViewModel.mMediaPlayer.isPlaying()) {
                mActivityViewModel.mMediaPlayer.StopPlayer();
            }

            mActivityViewModel.mMediaPlayer.SetList(mActivityViewModel.mPlayList.mMediaListItems);
            mActivityViewModel.mMediaPlayer.SetCurrentlyPlaying(position);
            mActivityViewModel.mMediaPlayer.StartPlayer();

            mActivityViewModel.mPlayList.mActivePlaylist =
                    Objects.requireNonNull(mBinding.playlistName.getText()).toString();

            mActivityViewModel.mSharedPreferences.edit()
                    .putString("mPlaylist", mActivityViewModel.mPlayList.mActivePlaylist)
                    .apply();


            View v = requireActivity().findViewById(R.id.nav_host_fragment_activity_main);
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_player);

            return;
        }


        String path = mActivityViewModel.mPlayList.mPlaylists.get(position);
        String[] paths = path.split("/");

        mBinding.playlistName.setText(paths[paths.length - 1]);

        mActivityViewModel.mPlayList.mActivePlaylist = paths[paths.length - 1];

        ActivatePlaylistByName();
    }

    public void ActivatePlaylistByName() {
        mActivityViewModel.mPlayList.mMediaListItems.clear();

        mActivityViewModel.setAppState(mActivityViewModel.mAppStatePlaylistMediaListActive);

        String filename = String.format(Locale.getDefault(), "%s/%s",
                mActivityViewModel.mAppStorageDirectory, mActivityViewModel.mPlayList.mActivePlaylist);

        try {
            mActivityViewModel.mPlayList.LoadMediaItems(filename);
        } catch (FileNotFoundException e) {
            SaveList();

            mActivityViewModel.setAppState(mActivityViewModel.mAppStatePlaylistMediaListActive);
        } catch (IOException e) {
            mActivityViewModel.mErrorToaster.LogError(this.getView(), e);

            return;
        }

        mActivityViewModel.mPlayList.mActivePlaylist = "zEmpty";

        mActivityViewModel.mSharedPreferences.edit()
                .putString("mPlaylist", "zEmpty")
                .apply();

        mBinding.playlistList.setAdapter(mPlaylistMediaAdapter);
    }
}