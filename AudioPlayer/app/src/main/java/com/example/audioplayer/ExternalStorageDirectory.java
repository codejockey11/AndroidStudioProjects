/*
PICO Technology Co., Ltd.
BeiJing
https://www.picoxr.com/

https://github.com/picoxr/get-sd-card-path/blob/master/app/src/main/java/com/picovr/getsdpath/Externalstoragedirectory.java
*/

package com.example.audioplayer;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ExternalStorageDirectory {

    public static String mExternalPath1;
    public static String mExternalPath2;

    /**
     * get SD card path using reflect
     */
    public static String GetSDCard(Context mContext) {
        getMountedSDCardCount(mContext);

        return mExternalPath1;
    }

    private static int getMountedSDCardCount(Context context) {
        mExternalPath1 = null;
        mExternalPath2 = null;
        int readyCount = 0;

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        if (storageManager == null) {
            return 0;
        }

        Method method;
        Object obj;

        try {
            method = storageManager.getClass().getMethod("getVolumePaths", (Class[]) null);
            obj = method.invoke(storageManager, (Object[]) null);

            String[] paths = (String[]) obj;

            if (paths == null) {
                return 0;
            }

            method = storageManager.getClass().getMethod("getVolumeState", String.class);

            for (String path : paths) {
                obj = method.invoke(storageManager, path);

                if (Environment.MEDIA_MOUNTED.equals(obj)) {
                    readyCount++;
                    if (2 == readyCount) {
                        mExternalPath1 = path;
                    }
                    if (3 == readyCount) {
                        mExternalPath2 = path;
                    }
                }
            }
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                readyCount = 1;
            }
            return readyCount;
        }

        return readyCount;
    }

    /**
     * if the method above doesnt work, try this
     */
    public static String getPath() {
        Runtime runtime = Runtime.getRuntime();

        try {
            Process process = runtime.exec("ls /storage");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer responseBuffer = new StringBuffer();

            char[] buff = new char[1024];
            int ch = 0;

            while ((ch = bufferedReader.read(buff)) != -1) {
                responseBuffer.append(buff, 0, ch);
            }

            bufferedReader.close();

            String[] result = responseBuffer.toString().trim().split("\n");

            for (String str : result) {
                if (str.equals("emulate") || str.equals("self")) {
                    continue;
                }

                return str;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}