package com.example.audioplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PlayList {
    public ArrayList<String> mPlaylists;
    public ArrayList<MediaListItem> mMediaListItems;
    public String mActivePlaylist = "";

    public PlayList() {
        mPlaylists = new ArrayList<>();
        mMediaListItems = new ArrayList<>();
    }

    public void Traverse(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    Traverse(file);
                } else if (file.getName().contains(".pls")) {
                    mPlaylists.add(file.getName());
                }
            }
        }
    }

    public void LoadList(File path) {
        if (path == null) {
            return;
        }

        mPlaylists.clear();

        Traverse(path);

        Collections.sort(mPlaylists);
    }

    public boolean DeleteList(File file) {
        mMediaListItems.clear();
        mActivePlaylist = "";

        return file.delete();
    }

    public void AddItem(MediaListItem mediaListItem) {
        mMediaListItems.add(mediaListItem);
    }

    public void LoadMediaItems(String filename) throws IOException {
        IniFile iniFile = new IniFile();

        iniFile.load(filename);

        for (Map.Entry<String, Map<String, String>> mapEntry : iniFile.mEntries.entrySet()) {
            Map<String, String> keyValue = mapEntry.getValue();
            String numberOfEntries = keyValue.get("NumberOfEntries");

            for (int item = 1; item <= Integer.parseInt(Objects.requireNonNull(numberOfEntries)); item++) {
                String format = String.format(Locale.getDefault(), "Artist%03d", item);
                String artist = keyValue.get(format);

                format = String.format(Locale.getDefault(), "Performer%03d", item);
                String performer = keyValue.get(format);

                format = String.format(Locale.getDefault(), "Album%03d", item);
                String album = keyValue.get(format);

                format = String.format(Locale.getDefault(), "Title%03d", item);
                String title = keyValue.get(format);

                format = String.format(Locale.getDefault(), "Uri%03d", item);
                String uri = keyValue.get(format);

                MediaListItem mediaListItem =
                        new MediaListItem(artist, performer, album, title, uri);

                AddItem(mediaListItem);
            }
        }
    }

    public void SaveMediaItems(String filename) throws IOException {
        IniFile iniFile = new IniFile();

        iniFile.CreateWriter(filename);
        iniFile.WriteSection("playlist");

        int count = 1;

        for (MediaListItem mediaListItem : mMediaListItems) {
            String format = String.format(Locale.getDefault(), "Artist%03d", count);
            iniFile.WriteKeyAndValue(format, mediaListItem.mArtist);

            format = String.format(Locale.getDefault(), "Performer%03d", count);
            iniFile.WriteKeyAndValue(format, mediaListItem.mPerformer);

            format = String.format(Locale.getDefault(), "Album%03d", count);
            iniFile.WriteKeyAndValue(format, mediaListItem.mAlbum);

            format = String.format(Locale.getDefault(), "Title%03d", count);
            iniFile.WriteKeyAndValue(format, mediaListItem.mTitle);

            format = String.format(Locale.getDefault(), "Uri%03d", count);
            iniFile.WriteKeyAndValue(format, mediaListItem.mUri);

            count++;
        }

        iniFile.WriteKeyAndValue("NumberOfEntries", String.valueOf(mMediaListItems.size()));
        iniFile.WriteKeyAndValue("Version", "2");

        iniFile.CloseWriter();
    }
}