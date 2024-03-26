package com.example.audioplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

public class PermissionRequest {

    public static final int mPermissionRequestReadExternalStorage = 0; //Arbitrary >= 0

    public void checkPermissions(Context context, View view, String[] requestedPermissions) {
        for (String requestedPermission : requestedPermissions) {
            if (requestedPermission.compareTo(android.Manifest.permission.READ_EXTERNAL_STORAGE) == 0) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is missing and must be requested.
                    requestReadExternalStoragePermission((Activity) context, view);
                }
            }
        }
    }

    private void requestReadExternalStoragePermission(Activity activity, View layout) {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(layout, "read_external_storage_permission_required",
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok,
                    view -> {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                mPermissionRequestReadExternalStorage);
                    }).show();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    mPermissionRequestReadExternalStorage);
        }
    }
}
