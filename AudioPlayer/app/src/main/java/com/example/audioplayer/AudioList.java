package com.example.audioplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class AudioList {
    public ArrayList<String> mAudios = new ArrayList<>();
    public ArrayList<String> mUris = new ArrayList<>();

    public void Traverse(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    File file = files[i];

                    if (file.isDirectory()) {
                        Traverse(file);
                    } else if (file.getName().contains(".ogg") || file.getName().contains(".mp3")) {
                        mUris.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void LoadAudios(File file) {
        mAudios.clear();
        mUris.clear();

        Traverse(file);

        Collections.sort(mUris);

        mUris.forEach(uri -> {
            String[] path = uri.split("\\/");
            StringBuilder stringBuilder = new StringBuilder();
            Boolean startAppend = false;
            Boolean firstOne = true;

            for (String p : path) {
                if (startAppend) {
                    if (firstOne) {
                        stringBuilder.append(p);
                        firstOne = false;
                    } else {
                        stringBuilder.append(" " + p);
                    }
                }
                if (p.contains("Alarms")) {
                    startAppend = true;
                }
                if (p.contains("audio")) {
                    startAppend = true;
                }
                if (p.contains("AudioRecorder")) {
                    startAppend = true;
                }
                if (p.contains("Music")) {
                    startAppend = true;
                }
                if (p.contains("Notifications")) {
                    startAppend = true;
                }
                if (p.contains("Playlists")) {
                    startAppend = true;
                }
                if (p.contains("Ringtones")) {
                    startAppend = true;
                }
            }

            if (stringBuilder.length() == 0) {
                mAudios.add(uri);
            } else {
                mAudios.add(stringBuilder.toString());
            }
        });
    }
}