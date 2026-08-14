package com.example.audioplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlaylistMediaAdapter extends ArrayAdapter<MediaListItem> {
    private final Context mContext;
    private final List<MediaListItem> mMediaListItems;

    public PlaylistMediaAdapter(@NonNull Context context, ArrayList<MediaListItem> mediaListItems) {
        super(context, 0, mediaListItems);

        mContext = context;
        mMediaListItems = mediaListItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        View listView = view;

        if (listView == null) {
            listView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.playlist_fragment_media_item, parent, false);
        }

        MediaListItem currentItem = mMediaListItems.get(position);

        Button moveUp = listView.findViewById(R.id.button_move_up);

        moveUp.setOnClickListener(button -> {
            int insertPosition = position - 1;

            Collections.swap(mMediaListItems, position, insertPosition);

            notifyDataSetChanged();
        });

        Button moveDown = listView.findViewById(R.id.button_move_down);

        moveDown.setOnClickListener(button -> {
            int insertPosition = position + 1;

            Collections.swap(mMediaListItems, position, insertPosition);

            notifyDataSetChanged();
        });

        Button delete = listView.findViewById(R.id.button_delete);

        delete.setOnClickListener(button -> {
            mMediaListItems.remove(currentItem);

            notifyDataSetChanged();
        });

        TextView artist = listView.findViewById(R.id.list_artist);
        artist.setText(currentItem.mArtist);

        TextView album = listView.findViewById(R.id.list_album);

        if (currentItem.mPerformer.contains("zEmpty")) {
            album.setText(currentItem.mAlbum);
        } else {
            album.setText(String.format("%s %s", currentItem.mPerformer, currentItem.mAlbum));
        }

        TextView title = listView.findViewById(R.id.list_title);
        title.setText(currentItem.mTitle);

        return listView;
    }
}