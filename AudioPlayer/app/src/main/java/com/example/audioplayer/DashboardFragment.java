package com.example.audioplayer;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.audioplayer.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding mBinding;
    private ActivityViewModel mActivityViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mActivityViewModel =
                new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        mBinding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = mBinding.getRoot();

        final ListView listView = mBinding.listView;

        String path = String.valueOf(Environment.getRootDirectory());
        mActivityViewModel.LoadAudios(path);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                root.getContext(),
                android.R.layout.simple_list_item_1, mActivityViewModel.mAudioList.mAudios);
        listView.setAdapter(adapter);

        mActivityViewModel.mItemSelected = false;
        listView.setOnItemClickListener((parent, view, position, id) -> GoBack(position));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void GoBack(Integer position) {
        if (mActivityViewModel.mMediaPlayer.isPlaying()) {
            mActivityViewModel.mMediaPlayer.stop();
            mActivityViewModel.mMediaPlayer.reset();
        }

        mActivityViewModel.mCurrentlyPlaying = position;

        mActivityViewModel.mItemSelected = true;

        View view = getActivity().findViewById(R.id.nav_host_fragment_activity_main);
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.navigation_home);
    }
}