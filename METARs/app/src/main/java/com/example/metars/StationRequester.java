package com.example.metars;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;

public class StationRequester {
    public final ActivityViewModel mActivityViewModel;
    public final View mView;
    public final HttpRequester mHttpRequester;
    public StringBuilder mStringBuilder;

    public StationRequester(ActivityViewModel activityViewModel, View view, String handlerName, String url, String stationId) {
        mActivityViewModel = activityViewModel;
        mView = view;

        HandlerThread handlerThread = new HandlerThread(handlerName);
        handlerThread.start();

        Looper looper = handlerThread.getLooper();

        Handler handler = new Handler(looper) {
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == 2) {
                    ParseBuffer();

                    mActivityViewModel.mHandlerWaitCount++;
                }
            }
        };

        mHttpRequester = new HttpRequester(handler, url + stationId);
        handler.post(mHttpRequester);
    }

    public void ParseBuffer() {
    }
}
