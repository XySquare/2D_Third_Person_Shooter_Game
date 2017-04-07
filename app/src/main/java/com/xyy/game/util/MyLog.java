package com.xyy.game.util;

import android.util.Log;

import com.xyy.game.framework.FileIO;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 应用日志
 * Created by ${XYY} on ${2016/10/22}.
 */
public class MyLog {

    private static DataOutputStream out = null;
    private static Date date;
    private static DateFormat format;


    public static void initialize(FileIO fileIO) {
        try {
            out = new DataOutputStream(
                    new BufferedOutputStream(fileIO.writeExternalStorage("Log.html", true)));
            Log.i("MyLog", "Log Opened.");
        } catch (IOException ignored) {
            Log.e("MyLog", "Failed to open Log file.");
        }
        date = new Date();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    }

    public static void i(String str) {
        //更新时间
        date.setTime(System.currentTimeMillis());
        String output = "<!--" + format.format(date) + "-->" + str + '\n';
        Log.i("MyLog", output);
        if (out != null) {
            try {
                out.write(output.getBytes());
            } catch (IOException ignored) {
                Log.e("MyLog", "Failed to write Log.");
            }
        } else
            Log.e("MyLog", "Log file is NOT opened, writing failed.");

    }

    public static void close() {
        if (out != null)
            try {
                out.close();
                Log.i("MyLog", "Closed.");
            } catch (IOException ignored) {
                Log.e("MyLog", "Failed to close Log file.");
            }
    }
}
