package com.example.mymetronome;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText bpmView;

    private Button buttonStartStop;

    private Boolean wasStarted;

    private int bpm;
    private int bpmMin;
    private int bpmMax;
    private int bpmMillis;

    private Thread clickSoundThread;
    private long currentMillis;
    private long previousMillis;
    private long elapsedMillis;

    private MediaPlayer mediaPlayer;
    private int soundId;

    private SharedPreferences mSettings;
    public String settingSound;
    public String settingTempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        CreateListView();

        CreateBpmView();

        CreateButtonDown();

        CreateButtonUp();

        CreateButtonStartStop();

        GetSettings();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    protected void onDestroy() {
        super.onDestroy();

        if (clickSoundThread != null) {
            if (clickSoundThread.isAlive()) {
                clickSoundThread.interrupt();

                clickSoundThread = null;
            }
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();

            mediaPlayer.release();

            mediaPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        SetMetronomeSound(id);

        settingSound = Objects.requireNonNull(menuItem.getTitle()).toString();

        mSettings.edit()
                .putString("sound", settingSound)
                .apply();

        return true;
    }

    private void CreateListView() {
        ListView tempoListView = findViewById(R.id.tempoListView);

        ArrayList<String> tempoItems = new ArrayList<>();

        ArrayAdapter<String> tempoListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tempoItems);

        tempoListView.setAdapter(tempoListAdapter);

        tempoItems.add("Grave");
        tempoItems.add("Lento/Largo");
        tempoItems.add("Larghetto");
        tempoItems.add("Adagio");
        tempoItems.add("Andante");
        tempoItems.add("Moderato");
        tempoItems.add("Allegretto");
        tempoItems.add("Allegro");
        tempoItems.add("Vivace");
        tempoItems.add("Presto");

        tempoListAdapter.notifyDataSetChanged();

        tempoListView.setOnItemClickListener((adapterView, view, i, l) -> {
            TextView bpmView = findViewById(R.id.bpm);

            switch (i) {
                case 0: {
                    bpmView.setText(R.string._20);

                    bpm = 20;

                    bpmMin = 20;
                    bpmMax = 40;

                    break;
                }
                case 1: {
                    bpmView.setText(R.string._40);

                    bpm = 40;

                    bpmMin = 40;
                    bpmMax = 60;

                    break;
                }
                case 2: {
                    bpmView.setText(R.string._60);

                    bpm = 60;

                    bpmMin = 60;
                    bpmMax = 66;

                    break;
                }
                case 3: {
                    bpmView.setText(R.string._66);

                    bpm = 66;

                    bpmMin = 66;
                    bpmMax = 76;

                    break;
                }
                case 4: {
                    bpmView.setText(R.string._76);

                    bpm = 76;

                    bpmMin = 76;
                    bpmMax = 108;

                    break;
                }
                case 5: {
                    bpmView.setText(R.string._108);

                    bpm = 108;

                    bpmMin = 108;
                    bpmMax = 112;

                    break;
                }
                case 6: {
                    bpmView.setText(R.string._112);

                    bpm = 112;

                    bpmMin = 112;
                    bpmMax = 120;

                    break;
                }
                case 7: {
                    bpmView.setText(R.string._120);

                    bpm = 120;

                    bpmMin = 120;
                    bpmMax = 168;

                    break;
                }
                case 8:
                case 9: {
                    bpmView.setText(R.string._168);

                    bpm = 168;

                    bpmMin = 168;
                    bpmMax = 999;

                    break;
                }
            }

            mSettings.edit()
                    .putString("tempo", bpmView.getText().toString())
                    .apply();
        });
    }

    private void SetMetronomeSound(int id) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();

            mediaPlayer.release();

        }

        mediaPlayer = new MediaPlayer();

        AssetFileDescriptor afd;

        if (id == R.id.click1) {
            afd = getResources().openRawResourceFd(R.raw.click1);
        } else if (id == R.id.click2) {
            afd = getResources().openRawResourceFd(R.raw.click2);
        } else if (id == R.id.click3) {
            afd = getResources().openRawResourceFd(R.raw.click3);
        } else if (id == R.id.click4) {
            afd = getResources().openRawResourceFd(R.raw.click4);
        } else if (id == R.id.click5) {
            afd = getResources().openRawResourceFd(R.raw.click5);
        } else {
            return;
        }

        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        try {
            afd.close();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void CreateBpmView() {
        bpmView = findViewById(R.id.bpm);

        bpmView.setOnClickListener(v -> {
            bpmView.selectAll();
        });

        bpmView.setOnEditorActionListener((textView, i, keyEvent) ->
        {
            if (i == EditorInfo.IME_ACTION_DONE) {
                bpm = Integer.parseInt(bpmView.getText().toString());

                bpmMin = bpm - 10;
                bpmMax = bpm + 10;

                mSettings.edit()
                        .putString("tempo", bpmView.getText().toString())
                        .apply();

                textView.clearFocus();
            }

            return false;
        });
    }

    private void CreateButtonDown() {
        Button buttonDown = findViewById(R.id.bpmDown);

        buttonDown.setOnClickListener(v ->
        {
            bpm--;

            if (bpm < bpmMin) {
                bpm = bpmMin;
            }

            if (bpm > bpmMax) {
                bpm = bpmMax;
            }

            bpmView.setText(String.valueOf(bpm));

            mSettings.edit()
                    .putString("tempo", bpmView.getText().toString())
                    .apply();
        });
    }

    private void CreateButtonUp() {
        Button buttonUp = findViewById(R.id.bpmUp);

        buttonUp.setOnClickListener(v ->
        {
            bpm++;

            if (bpm < bpmMin) {
                bpm = bpmMin;
            }

            if (bpm > bpmMax) {
                bpm = bpmMax;
            }

            bpmView.setText(String.valueOf(bpm));

            mSettings.edit()
                    .putString("tempo", bpmView.getText().toString())
                    .apply();
        });
    }

    private void CreateButtonStartStop() {
        buttonStartStop = findViewById(R.id.startStopButton);

        buttonStartStop.setOnClickListener(v ->
        {
            if (wasStarted != Boolean.TRUE) {
                buttonStartStop.setText(R.string.stop);

                currentMillis = System.currentTimeMillis();
                previousMillis = currentMillis;
                elapsedMillis = 0;

                wasStarted = Boolean.TRUE;

                clickSoundThread = new Thread(() ->
                {
                    while (wasStarted) {
                        previousMillis = currentMillis;

                        currentMillis = System.currentTimeMillis();

                        elapsedMillis += currentMillis - previousMillis;

                        bpmMillis = (int) ((60.0 / (float) bpm) * 1000.0);

                        if (elapsedMillis >= bpmMillis) {
                            elapsedMillis = 0;

                            mediaPlayer.start();
                        }
                    }
                });

                clickSoundThread.start();
            } else {
                wasStarted = Boolean.FALSE;

                clickSoundThread.interrupt();

                clickSoundThread = null;

                buttonStartStop.setText(R.string.start);

                mediaPlayer.pause();
            }
        });
    }

    private void GetSettings() {
        mSettings = getSharedPreferences("UserInfo", 0);

        settingSound = mSettings.getString("sound", "click1");

        if (Objects.equals(settingSound, "click1")) {
            soundId = R.id.click1;
        } else if (Objects.equals(settingSound, "click2")) {
            soundId = R.id.click2;
        } else if (Objects.equals(settingSound, "click3")) {
            soundId = R.id.click3;
        } else if (Objects.equals(settingSound, "click4")) {
            soundId = R.id.click4;
        } else if (Objects.equals(settingSound, "click5")) {
            soundId = R.id.click5;
        }

        SetMetronomeSound(soundId);

        settingTempo = mSettings.getString("tempo", "60");

        bpmView.setText(settingTempo);

        bpm = Integer.parseInt(bpmView.getText().toString());
    }
}