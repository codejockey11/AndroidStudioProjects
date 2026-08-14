package com.example.metars;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.metars.databinding.ActivityMainBinding;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private ActivityViewModel mActivityViewModel;
    private EditText mStationId;
    private TextView mTextOut;
    private StationInfo mStationInfo;
    private MetarInfo mMetarInfo;
    private TafInfo mTafInfo;
    private CountDownTimer mRequestTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mActivityViewModel =
                new ViewModelProvider(this).get(ActivityViewModel.class);

        mActivityViewModel.mSettings = getSharedPreferences("UserInfo", 0);

        mActivityViewModel.mDisplayFormatted = mActivityViewModel.mSettings.getBoolean("displayFormatted", false);

        mStationId = findViewById(R.id.station_id);
        mTextOut = findViewById(R.id.textOut);

        mRequestTimer = new CountDownTimer(5000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mActivityViewModel.mHandlerWaitCount == 3) {
                    updateView();
                }
            }

            @Override
            public void onFinish() {
                if (mActivityViewModel.mHandlerWaitCount == 3) {
                    return;
                }

                mTextOut.setText(R.string.request_timed_out);
            }
        };

        mStationId = findViewById(R.id.station_id);

        mStationId.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_GO) {
                textView.setText(textView.getText().toString().toUpperCase());

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);

                RequestInfo();

                return true;
            }

            return false;
        });

        if (mStationId.length() == 0) {
            String defaultStation = mActivityViewModel.mSettings.getString("defaultStation", null);

            if (defaultStation != null) {
                if (!defaultStation.isEmpty()) {
                    mStationId.setText(defaultStation);

                    RequestInfo();
                }
            }
        }
    }

    private void RequestInfo() {
        mActivityViewModel.mHandlerWaitCount = 0;

        mTextOut.setText(null);

        mStationInfo = new StationInfo(mActivityViewModel,
                mBinding.getRoot(),
                "StationInfo",
                mActivityViewModel.mStationUrl,
                String.valueOf(mStationId.getText()));

        mMetarInfo = new MetarInfo(mActivityViewModel,
                mBinding.getRoot(),
                "MetarInfo",
                mActivityViewModel.mMetarUrl,
                String.valueOf(mStationId.getText()));

        mTafInfo = new TafInfo(mActivityViewModel,
                mBinding.getRoot(),
                "TafInfo",
                mActivityViewModel.mTafUrl,
                String.valueOf(mStationId.getText()));

        mRequestTimer.start();
    }

    public void updateView() {
        if (mStationInfo.mSite == null) {
            mTextOut.setText(R.string.stationNotFound);

            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (mActivityViewModel.mDisplayFormatted) {
            stringBuilder.append("Location:");
            stringBuilder.append(mStationInfo.mSite);
            stringBuilder.append(" ");
            stringBuilder.append(mStationInfo.mState);

            stringBuilder.append("\n\nATMOSPHERE");

            stringBuilder.append(String.format(Locale.ENGLISH, "\n\nAltitude:%.2f", mStationInfo.mElevationMeters * mActivityViewModel.mFeetInMeters));

            if (!mMetarInfo.mStringBuilder.toString().isEmpty()) {
                stringBuilder.append(mMetarInfo.FormatAtmosphereData(mStationInfo.mElevationMeters));
            }

            stringBuilder.append("\n\nMETAR");
        } else {
            stringBuilder.append("ATMOSPHERE");

            stringBuilder.append(String.format(Locale.ENGLISH, "\n\nAltitude:%.2f", mStationInfo.mElevationMeters * mActivityViewModel.mFeetInMeters));

            if (!mMetarInfo.mStringBuilder.toString().isEmpty()) {
                stringBuilder.append(mMetarInfo.FormatAtmosphereData(mStationInfo.mElevationMeters));
            }
        }

        stringBuilder.append(mMetarInfo.mStringBuilder.toString());

        if (mActivityViewModel.mDisplayFormatted) {
            stringBuilder.append("\n\nTAF");
        }

        stringBuilder.append(mTafInfo.mStringBuilder.toString());

        stringBuilder.append("\n\n\n");

        mTextOut.setText(stringBuilder.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.type_save_station) {
            if (mStationId.getText().length() != 0) {
                mActivityViewModel.mSettings.edit()
                        .putString("defaultStation", String.valueOf(mStationId.getText()))
                        .apply();

            }

            return true;
        }

        if (id == R.id.type_raw) {
            mActivityViewModel.mDisplayFormatted = false;

            saveDisplayFormatted();

            if (mStationId.getText().length() != 0) {
                mMetarInfo.ParseBuffer();
                mTafInfo.ParseBuffer();

                updateView();
            }

            return true;
        }

        if (id == R.id.type_formatted) {
            mActivityViewModel.mDisplayFormatted = true;

            saveDisplayFormatted();

            if (mStationId.getText().length() != 0) {
                mMetarInfo.ParseBuffer();
                mTafInfo.ParseBuffer();

                updateView();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveDisplayFormatted() {
        mActivityViewModel.mSettings.edit()
                .putBoolean("displayFormatted", mActivityViewModel.mDisplayFormatted)
                .apply();
    }
}