package com.example.audioplayer;

import android.view.View;
import android.widget.Toast;

public class ErrorToaster {

    public void LogError(View view, Exception e) {
        if (view == null) {
            return;
        }

        if (e == null) {
            return;
        }

        if (e.getMessage() != null) {
            Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void LogError(View view, String s) {
        if (view == null) {
            return;
        }

        Toast.makeText(view.getContext(), s, Toast.LENGTH_LONG).show();
    }
}
