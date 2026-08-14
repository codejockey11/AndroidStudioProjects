package com.example.audioplayer;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MountedVolumes {
    public ArrayList<String> mMountedVolumes;
    private static final int mSimCardAlwaysInSlot1 = 0;
    private static final int mMicroSDCard = 1;
    public File mSimCardDirectory;
    public File mSDCardDirectory;

    public MountedVolumes(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        if (storageManager == null) {
            return;
        }

        mMountedVolumes = new ArrayList<>();

        List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();

        for (StorageVolume storageVolume : storageVolumes) {
            if (storageVolume.getState().equals(Environment.MEDIA_MOUNTED)) {
                Parcel parcel = Parcel.obtain();

                storageVolume.writeToParcel(parcel, 0);

                parcel.setDataPosition(0);

                String skip = parcel.readString();
                String path = parcel.readString();

                parcel.recycle();

                mMountedVolumes.add(path);
            }
        }

        mSimCardDirectory = new File(mMountedVolumes.get(mSimCardAlwaysInSlot1));
        mSDCardDirectory = new File(mMountedVolumes.get(mMicroSDCard));
    }
}