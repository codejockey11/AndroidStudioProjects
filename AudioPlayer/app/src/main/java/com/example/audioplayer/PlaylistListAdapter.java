package com.example.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlaylistListAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final List<String> mPlaylistListItems;

    public PlaylistListAdapter(@NonNull Context context, ArrayList<String> playlistListItems) {
        super(context, 0, playlistListItems);

        mContext = context;
        mPlaylistListItems = playlistListItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        View listView = view;

        if (listView == null) {
            listView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.playlist_fragment_list_item, parent, false);
        }

        String currentItem = mPlaylistListItems.get(position);

        TextView name = listView.findViewById(R.id.list_name);
        name.setText(currentItem);

        return listView;
    }
}