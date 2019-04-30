package com.mxnavi.mobile.utils.baseutil;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.api.maps.model.LatLng;
import com.mxnavi.mobile.R;
import com.mxnavi.mobile.core.MobileApplication;
import com.mxnavi.mobile.utils.projectutil.FeedBackUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import mobi.cangol.mobile.actionbar.ActionBarActivity;

public class CommonUtil {


    public static String API_WECHAT = "com.tencent.mm";
    public static String API_ALIPAY = "com.eg.android.AlipayGphone";

    private static final int MIN_DELAY_TIME = 1000;  // 两次点击间隔不能少于1000ms
    private static long lastClickTime;

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Activity activity) {
        try {
            PackageManager manager = activity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return activity.getString(R.string.can_not_find_version_name);
        }
    }

    /**
     * 获取设备IMEI码
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        if (null == context) {
            return "";
        }
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        return telephonyManager.getDeviceId();
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getAppPackageName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return null;
    }

    /**
     * 获取当前时间,默认"yyyy-MM-dd-hh:mm:ss"格式
     *
     * @return
     */
    public static String getCurrentByFormat(String format) {
        SimpleDateFormat df = new SimpleDateFormat(TextUtils.isEmpty(format) ? "yyyy-MM-dd-hh:mm:ss" : format);//设置日期格式
        return df.format(new Date());
    }

    /**
     * 获取当位置
     */
    private static Location getLocation(ActionBarActivity actionBarActivity, Activity activity) {
        L.d("getLocation--");
        final LocationManager locationManager = (LocationManager) actionBarActivity
                .getSystemService(Context
                        .LOCATION_SERVICE);
        //api适配
        if (ActivityCompat.checkSelfPermission(actionBarActivity, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(activity, Manifest.permission
                        .ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }


    /**
     * 获取经纬度
     * @param context
     * @return
     */
    public static LatLng getLngAndLat(Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        LatLng latLng = null;
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //从gps获取经纬度
        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(MobileApplication.getContextObject(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MobileApplication.getContextObject(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                latLng = new LatLng(latitude, longitude);
            } else {//当GPS信号弱没获取到位置的时候又从网络获取
                return getLngAndLatWithNetwork(mLocationManager);
            }
        } else {    //从网络获取经纬度
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new LocationListener() {

                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {

                }

                // Provider被disable时触发此函数，比如GPS被关闭
                @Override
                public void onProviderDisabled(String provider) {

                }

                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                @Override
                public void onLocationChanged(Location location) {
                }

                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }
            });
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                latLng = new LatLng(latitude, longitude);
            }
        }
        return latLng;
    }

    /**
     * 从网络获取经纬度
     * @return
     */
    private static LatLng getLngAndLatWithNetwork(LocationManager mLocationManager) {
        double latitude = 0.0;
        double longitude = 0.0;
        LatLng latLng = null;
        mLocationManager = (LocationManager) MobileApplication.getContextObject().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MobileApplication.getContextObject(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MobileApplication.getContextObject(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, new LocationListener() {

            // Provider被enable时触发此函数，比如GPS被打开
            @Override
            public void onProviderEnabled(String provider) {

            }

            // Provider被disable时触发此函数，比如GPS被关闭
            @Override
            public void onProviderDisabled(String provider) {

            }

            //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
            @Override
            public void onLocationChanged(Location location) {
            }

            // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        });
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latLng = new LatLng(latitude, longitude);
        }
        return latLng;
    }

    /**
     * 通过字符串id转换成字符串
     *
     * @param context
     * @param id
     */
    public static String getResString(Context context, int id) {
        return context.getResources().getString(id);
    }

    /**
     * 得到指定日期的向后或向前X天的日期
     *
     * @param day
     * @param Num
     * @return
     */
    public static String getSpecifiedDate(String day, int Num, String operator) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = "+".equals(operator) ?
                new Date(nowDate.getTime() + (long) Num * 24 * 60 * 60 * 1000) :
                new Date(nowDate.getTime() - (long) Num * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    /**
     * 设置系统状态栏颜色
     *
     * @param activity
     * @param color
     */
    public static void setSystemStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().setStatusBarColor(color);   //这里可以动态设置状态栏的颜色
        }
    }

    /**
     * Fixed number of items to display listview
     *
     * @param listView
     * @param count    显示条数
     */
    public static void setListViewHeightBasedOnChildren(ListView listView, int count) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (listAdapter.getCount() < count) {
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                //listItem.notify();
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        } else {
            View listItem = listAdapter.getView(0, null, listView);
            listItem.measure(0, 0);
            params.height = listItem.getMeasuredHeight() * count + listView.getDividerHeight() * count;
        }
        listView.setLayoutParams(params);
    }

    /**
     * 是否安装第三方应用
     *
     * @param context
     * @param url
     * @return
     */
    public static boolean isAppAvilible(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (url.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断网络是否连接，连接返回true，否则返回false
     *
     * @param context
     * @return
     */

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }

    /**
     * 判断网络是否连接.
     */
    public static boolean isNetworkConnected() {
        String TAG = "判断网络";
        ConnectivityManager connectivity = (ConnectivityManager) MobileApplication
                .getContextObject().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo ni : info) {
                    if (ni.getState() == NetworkInfo.State.CONNECTED) {
                        Log.d(TAG, "type = " + (ni.getType() == 0 ? "mobile" : ((ni.getType() ==
                                1) ? "wifi" : "none")));
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 防止连续点击按钮处理
     *
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }

    public static boolean isYesterday(String day) {
        if (TextUtils.isEmpty(day) || day.length() < 10) {
            return false;
        }
        String currentTime = FeedBackUtil.getNowTime_y_m_d_h_m();
        if (currentTime.substring(0, 8).equals(day.substring(0, 8))) {
            int str1 = Integer.parseInt(currentTime.substring(8, 10));
            int str2 = Integer.parseInt(day.substring(8, 10));
            if (str1 - str2 == 1) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static boolean isToday(String day) {
        if (TextUtils.isEmpty(day) || day.length() < 10) {
            return false;
        }
        day = day.substring(0, 10);
        String currentTime = FeedBackUtil.getNowTime_y_m_d_h_m();
        currentTime = currentTime.substring(0, 10);
        if (currentTime.equals(day)) {
            return true;
        }
        return false;

    }

    /**
     * 判断是否是全面屏
     */
    public static boolean isAllScreenDevice(Context context) {
        boolean mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }

    /**
     * 格式化小数，默认小数点后保留一位
     *
     * @param c
     * @param format
     * @return
     */
    public static String formatDecimals(double c, String format) {
        java.text.DecimalFormat myformat = new java.text.DecimalFormat(TextUtils.isEmpty(format) ? "0.0" :
                format);
        String str = myformat.format(c);
        return str;
    }

    /**
     * 将名称修改为 *称 保留最后一个字 其余为*
     *
     * @param UserName
     * @return
     */
    public static String formatToEncrypateString(String UserName) {
        String EncrypatedName = "";
        if (null != UserName && UserName.length() > 0) {
            for (int i = 0; i < UserName.length() - 1; i++) {
                EncrypatedName = EncrypatedName + "*";
            }
            EncrypatedName = EncrypatedName + UserName.substring(UserName.length() - 1);
        }

        return EncrypatedName;
    }


    /**
     * 身份证号替换，隐藏startHide-endHide位
     * <p>
     * 如果身份证号为空 或者 null ,返回null ；否则，返回替换后的字符串；
     *
     * @param idCard 身份证号
     * @return
     */
    public static String formatIdCardToEncrypateString(String idCard, int startHide, int endHide) {
        if (TextUtils.isEmpty(idCard)) {
            return null;
        }
        if (idCard.length() < startHide) {
            return idCard;
        }
        if (startHide <= idCard.length() && idCard.length() < endHide + 1) {
            String frontStr = idCard.substring(0, startHide - 1);
            String dailStr = "";
            for (int i = 0; i < idCard.length() - startHide - 1; i++) {
                dailStr = dailStr + "*";
            }
            return frontStr + dailStr;
        }
        StringBuilder hideStr = null;
        for (int i = 0; i < idCard.length() - startHide + 1; i++) {
            hideStr = hideStr.append("*");
        }
        return idCard.substring(0, startHide - 1) + hideStr;
    }

    /**
     * 跳到应用市场
     *
     * @param context
     */
    public static void goToMarket(Context context, String url) {
        Uri uri = Uri.parse("market://details?id=" + (TextUtils.isEmpty(url) ? API_WECHAT : url));
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 入参：分享的title，text，图片等，暂写死测试功能
     *
     * @param activity
     */
    public static void go2Share(final Activity activity) {
        final String TAG = "share begin";
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("让我来接你");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("https://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我来接你了，点击这里告诉我你在哪？");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        //如果imagePath和imageUrl同时存在，imageUrl将被忽略。
//        oks.setImageUrl("http://user-auth.mxnavi.com/photoImg/372df471e9314be7b7ae2e15ea17d334/1535508436499.jpg");//确保SDcard下面存在此张图片
        Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_deck);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        bmp.recycle();
        oks.setImageData(thumbBmp);
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("https://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(activity);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d(TAG, " onComplete ");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d(TAG, " onError ");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d(TAG, " onCancel ");
            }
        });
    }

    /**
     * 飞行模式判断
     *
     * @param context
     * @return
     */
    public static boolean IsAirModeOn(Context context) {
        return (Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 1);
    }

    /**
     * 验证手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean telIsIllegal(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][345678]\\d{9}";
        //"[1]"代表第1位为数字1，"[345678]"代表第二位可以为345678中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }


}
