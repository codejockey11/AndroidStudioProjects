package com.example.metars;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class ActivityViewModel extends ViewModel {
    public ErrorToaster mErrorToaster;
    public SharedPreferences mSettings;
    public boolean mDisplayFormatted;
    public Integer mHandlerWaitCount = 0;
    public final double mFeetInMeters = 3.2808399;
    public final String mStationUrl = "https://aviationweather.gov/api/data/stationInfo?format=xml&ids=";
    public final String mMetarUrl = "https://aviationweather.gov/api/data/metar?format=xml&hours=3&ids=";
    public final String mTafUrl = "https://aviationweather.gov/api/data/taf?format=xml&hours=3&ids=";

    public ActivityViewModel() {
        mErrorToaster = new ErrorToaster();
    }

    @NonNull
    public String FlipTimeDate(@NonNull String timeDate) {
        String[] timeAndDateSplit = timeDate.split(" ");

        String[] date = timeAndDateSplit[0].split("-");

        return String.format("%s-%s-%s %s", date[1], date[2], date[0], timeAndDateSplit[1]);
    }
}