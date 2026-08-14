package com.example.metars;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownServiceException;

public class HttpRequester implements Runnable {
    public Handler mHandler;
    public String mUrl;
    public StringBuilder mBuffer;

    // passing the handler in order to post messages to the looper
    public HttpRequester(Handler handler, String url) {
        mHandler = handler;
        mUrl = url;
    }

    // the run method is similar to doInBackground() for Async Threads
    @Override
    public void run() {
        URL url;

        try {
            url = new URL(mUrl);

        } catch (Exception ex) {
            return;
        }

        HttpURLConnection httpURLConnection;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36");
            httpURLConnection.setRequestProperty("Accept", "application/xml");
        } catch (Exception ex) {
            return;
        }

        InputStream inputStream = null;

        try {
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try {
                    inputStream = httpURLConnection.getInputStream();
                } catch (UnknownServiceException use) {
                    return;
                }
            }
        } catch (IOException ioe) {
            return;
        }

        InputStreamReader inputStreamReader;

        try {
            inputStreamReader = new InputStreamReader(inputStream);
        } catch (Exception ex) {
            return;
        }

        mBuffer = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                mBuffer.append(line);
            }
        } catch (Exception ex) {
            return;
        }

        Message message = new Message();

        message.what = 2;

        mHandler.sendMessage(message);
    }
}