package com.mxnavi.mobile.utils.baseutil;

/**
 * Created by P on 2018/6/12.
 */

import android.content.Context;
import android.os.Environment;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;

public class AppLogHandler implements UncaughtExceptionHandler {

    public static final boolean DEBUG = true;

    public static final String AGR_LOG_DIRECOTORY = "agrlog";

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static AppLogHandler instance;

    private AppLogHandler() {
    }

    public static AppLogHandler getInstance() {
        if (instance == null) {
            instance = new AppLogHandler();
        }
        return instance;
    }

    public void init(Context context) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    private boolean handleException(final Throwable ex) {
        if (DEBUG) {
            if (ex == null) {
                return false;
            }
            final StackTraceElement[] stack = ex.getStackTrace();
            final String message = ex.getMessage();
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    createLogDirectory();
                    String fileName = AGR_LOG_DIRECOTORY + "/crash-" + System.currentTimeMillis() + ".log";
                    File file = new File(
                            Environment.getExternalStorageDirectory(), fileName);
                    try {
                        FileOutputStream fos = new FileOutputStream(file, true);
                        fos.write(message.getBytes());
                        for (int i = 0; i < stack.length; i++) {
                            fos.write(stack[i].toString().getBytes());
                        }
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                    }
                    Looper.loop();
                }
            }.start();
        }
        return false;
    }

    private void createLogDirectory() {
        File file = new File(Environment.getExternalStorageDirectory(), AGR_LOG_DIRECOTORY);
        try {
            if (!file.exists() || !file.isDirectory()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}