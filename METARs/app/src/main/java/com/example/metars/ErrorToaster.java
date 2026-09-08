package com.example.metars;

import android.view.View;
import android.widget.Toast;

public class ErrorToaster {

    public void LogError(View view, Exception e) {
        if (e == null) {
            return;
        }

        if (e.getMessage() != null) {
            Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void LogError(View view, String s) {
        Toast.makeText(view.getContext(), s, Toast.LENGTH_LONG).show();
    }
}
