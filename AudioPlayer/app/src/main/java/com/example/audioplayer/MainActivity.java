package com.example.audioplayer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.audioplayer.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ActivityViewModel mActivityViewModel;
    private View mView;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityViewModel =
                new ViewModelProvider(this).get(ActivityViewModel.class);

        checkPermission();

        mActivityViewModel.mSharedPreferences = getSharedPreferences("UserInfo", 0);

        mActivityViewModel.mMediaPlayer.SetSharedPreferencesEditor(mActivityViewModel.mSharedPreferences);

        mActivityViewModel.mPlayList.mActivePlaylist =
                mActivityViewModel.mSharedPreferences.getString("mPlaylist", "zEmpty");

        if (mActivityViewModel.mPlayList.mActivePlaylist.equals("zEmpty")) {
            mActivityViewModel.setAppState(mActivityViewModel.mAppStateMediaListActive);
            mActivityViewModel.mMediaPlayer.SetList(mActivityViewModel.mMediaList.mAudios);
        } else {
            String filename =
                    String.format("%s/%s", mActivityViewModel.mAppStorageDirectory,
                            mActivityViewModel.mPlayList.mActivePlaylist);

            try {
                mActivityViewModel.mPlayList.LoadMediaItems(filename);
            } catch (IOException e) {
                mActivityViewModel.mErrorToaster.LogError(null, e);
            }

            mActivityViewModel.setAppState(mActivityViewModel.mAppStatePlaylistListActive);
            mActivityViewModel.mMediaPlayer.SetList(mActivityViewModel.mPlayList.mMediaListItems);
        }

        mActivityViewModel.mMediaPlayer.SetCurrentlyPlaying(
                mActivityViewModel.mSharedPreferences.getInt("mCurrentlyPlaying", 0));

        mActivityViewModel.mMediaPlayer.GetCurrentMediaListItem();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                mActivityViewModel.mMediaSession =
                        new MediaSession(this, "PlayerServiceMediaSession");

                mActivityViewModel.mMediaPlayer.SetMediaSession(mActivityViewModel.mMediaSession);

                mActivityViewModel.mMediaSession.setActive(true);

                mActivityViewModel.mMediaPlayer.SetCurrentlyPlaying(
                        mActivityViewModel.mSharedPreferences.getInt("mCurrentlyPlaying", 0));
            }
        }

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        mView = binding.getRoot();

        setContentView(mView);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(
                        R.id.navigation_player,
                        R.id.navigation_playlist,
                        R.id.navigation_media_filter,
                        R.id.navigation_media_list)
                        .build();

        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            LoadMediaLists();
        } else {
            requestReadExternalStoragePermission();
        }
    }

    private void requestReadExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoadMediaLists();
            } else {
                Snackbar.make(mView, "read_external_storage_required", Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, view -> this.finish())
                        .show();
            }
        }
    }

    private void LoadMediaLists() {
        MountedVolumes mountedVolumes = new MountedVolumes(this);
        mActivityViewModel.mSDCardDirectory = mountedVolumes.mSDCardDirectory;

        ContextWrapper contextWrapper = new ContextWrapper(this);
        mActivityViewModel.mAppStorageDirectory =
                contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

        mActivityViewModel.mMediaList.LoadList(mActivityViewModel.mSDCardDirectory);
        mActivityViewModel.mPlayList.LoadList(mActivityViewModel.mAppStorageDirectory);
    }
}