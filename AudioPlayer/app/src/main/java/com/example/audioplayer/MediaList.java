package com.example.audioplayer;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class MediaList {
    public ArrayList<MediaListItem> mAudios;
    public ArrayList<MediaListItem> mSubset;
    public String mCurrentFilter = "";

    public MediaList() {
        mAudios = new ArrayList<>();
    }

    public void Traverse(File dir) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    Traverse(file);
                } else if (file.getName().contains(".ogg") || file.getName().contains(".mp3")) {
                    AddToList(file.getAbsolutePath());
                }
            }
        }
    }

    public void LoadList(File file) {
        mAudios.clear();

        Traverse(file);

        Comparator<MediaListItem> comparator = Comparator
                .comparing((MediaListItem mediaListItem) -> mediaListItem.mArtist)
                .thenComparing((MediaListItem mediaListItem) -> mediaListItem.mPerformer)
                .thenComparing((MediaListItem mediaListItem) -> mediaListItem.mAlbum)
                .thenComparing(mediaListItem -> mediaListItem.mTitle);

        mAudios.sort(comparator);

        int count = 0;
        for (MediaListItem mli : mAudios) {
            mli.mIndex = count;

            count++;
        }
    }

    public void CreateSubset(char letter) {
        if (mSubset != null) {
            mSubset.clear();
        } else {
            mSubset = new ArrayList<>();
        }

        for (MediaListItem mli : mAudios) {
            char[] chars = mli.mArtist.toCharArray();

            if (chars[0] == letter) {
                mSubset.add(mli);
            }
        }
    }

    private void AddToList(String uri) {
        String parsedUri = ParseUri(uri);

        String[] audioItems = parsedUri.split("\\|");

        String artist = "zEmpty";
        String performer = "zEmpty";
        String album = "zEmpty";
        String title = "zEmpty";

        if (audioItems.length == 1) {
            title = audioItems[0];
        }

        if (audioItems.length == 2) {
            album = audioItems[0];
            title = audioItems[1];
        }

        if (audioItems.length == 3) {
            artist = audioItems[0];
            album = audioItems[1];
            title = audioItems[2];
        }

        if (audioItems.length == 4) {
            artist = audioItems[0];
            performer = audioItems[1];
            album = audioItems[2];
            title = audioItems[3];
        }

        mAudios.add(new MediaListItem(artist, performer, album, title, uri));
    }

    @NonNull
    private static String ParseUri(String uri) {
        String[] parts = uri.split("/");
        StringBuilder stringBuilder = new StringBuilder();
        boolean startAppend = false;
        boolean firstOne = true;

        for (String part : parts) {
            if (startAppend) {
                if (firstOne) {
                    stringBuilder.append(part);
                    firstOne = false;
                } else {
                    stringBuilder.append("|");
                    stringBuilder.append(part);
                }
            }

            if (part.toUpperCase().contains("MUSIC")) {
                startAppend = true;
            }
        }

        return stringBuilder.toString();
    }
}