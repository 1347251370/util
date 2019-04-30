package com.mxnavi.mobile.utils.baseutil;

import android.util.Log;

/**
 * Created by Admin on 2017/3/4.
 *  Log 输出类
 */
public class L {

    /**
     * 调试模式：是否开启调试；false:关闭调试；true:打开调试
     */
    public static final boolean DEBUG = true;
    /**
     * 标记调试信息字段,开发人员可以设置便于调试
     */
    public static String TAG = "autoui";

    /**
     *
     * @Description: TODO 调试正常信息
     * @param msg
     */
    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    /**
     *
     * @Description: TODO 调试异常
     * @param msg
     */
    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

    /**
     *
     * @Description: TODO 调试所有信息
     * @param msg
     */
    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    /**
     *
     * @Description: TODO 调试警告
     * @param msg
     */
    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }

    /**
     *
     * @Description: TODO 调试
     * @param msg
     */
    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

}
