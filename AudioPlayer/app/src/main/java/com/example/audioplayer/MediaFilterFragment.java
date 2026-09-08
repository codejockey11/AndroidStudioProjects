package com.example.audioplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.audioplayer.databinding.MediaFilterFragmentBinding;
import com.google.android.material.chip.Chip;

public class MediaFilterFragment extends Fragment {
    private MediaFilterFragmentBinding mBinding;
    private ActivityViewModel mActivityViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mActivityViewModel =
                new ViewModelProvider(requireActivity()).get(ActivityViewModel.class);

        mBinding = MediaFilterFragmentBinding.inflate(inflater, container, false);

        View root = mBinding.getRoot();

        if (!mActivityViewModel.mMediaList.mCurrentFilter.isEmpty()) {
            mBinding.currentFilter.setText(mActivityViewModel.mMediaList.mCurrentFilter);
        }

        if (mActivityViewModel.mMediaSession != null) {
            mActivityViewModel.mMediaSession.setCallback(null);
        }

        TableLayout tableLayout = mBinding.tableLayout;
        TableRow tableRow = new TableRow(root.getContext());
        Chip chip = new Chip(root.getContext());

        char letter = 65;

        for (int row = 0; row < 6; row++) {
            tableRow.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            tableLayout.addView(tableRow);

            for (int column = 0; column < 5; column++) {
                chip.setText(String.valueOf(letter));
                chip.setTextAppearance(R.style.chipText);

                tableRow.addView(chip);

                final char l = letter;

                chip.setOnClickListener(tab -> {
                    mActivityViewModel.mMediaList.CreateSubset(l);

                    if (mActivityViewModel.mMediaList.mSubset.isEmpty()) {
                        mActivityViewModel.mMediaList.mCurrentFilter = "Current Filter";
                    } else {
                        mActivityViewModel.mMediaList.mCurrentFilter = "Current Filter:" + l;
                    }

                    mBinding.currentFilter.setText(mActivityViewModel.mMediaList.mCurrentFilter);
                });

                chip = new Chip(root.getContext());

                if (letter == 'Z') {
                    tableLayout = mBinding.tableLayoutLast;
                    tableRow = new TableRow(root.getContext());
                    tableLayout.addView(tableRow);

                    chip.setText(R.string.misc);
                    chip.setTextAppearance(R.style.chipText);

                    tableRow.addView(chip);

                    chip.setOnClickListener(tab -> {
                        mActivityViewModel.mMediaList.CreateSubset('z');
                        mActivityViewModel.mMediaList.mCurrentFilter = "Current Filter:Misc";

                        mBinding.currentFilter.setText(mActivityViewModel.mMediaList.mCurrentFilter);
                    });

                    chip = new Chip(root.getContext());

                    chip.setText(R.string.clear);
                    chip.setTextAppearance(R.style.chipText);

                    tableRow.addView(chip);

                    chip.setOnClickListener(tab -> {
                        if (mActivityViewModel.mMediaList.mSubset != null) {
                            mActivityViewModel.mMediaList.mSubset.clear();
                        }

                        mActivityViewModel.mMediaList.mCurrentFilter = "Current Filter";

                        mBinding.currentFilter.setText(mActivityViewModel.mMediaList.mCurrentFilter);
                    });

                    return root;
                }

                letter++;
            }

            tableRow = new TableRow(root.getContext());
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}