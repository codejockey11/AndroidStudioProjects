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

public class MediaListAdapter extends ArrayAdapter<MediaListItem> {
    private final Context mContext;
    private final List<MediaListItem> mList;

    public MediaListAdapter(@NonNull Context context, ArrayList<MediaListItem> list) {
        super(context, 0, list);
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        View listView = view;

        if (listView == null) {
            listView = LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.media_list_fragment_item, parent, false);
        }

        MediaListItem currentItem = mList.get(position);

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