package com.example.koolmeo.base;

import android.app.Application;

import com.example.koolmeo.bean.UploadInfoState;
import com.example.koolmeo.log.YiLog;
import com.example.koolmeo.util.DBBeanUpLoadVideoInfoUtils;
import com.example.koolmeo.util.DBHttpBeanMediaDetailUtils;
import com.example.koolmeo.util.DBLocalMediaDetailBeanUtils;
import com.example.koolmeo.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangHaiLong on 2017/9/6.
 */

public class BaseApplication extends Application{
    public static final String TAG = "YiApplication";

    public static List<UploadInfoState> uploadingInfoPositionList = new ArrayList<UploadInfoState>();
    @Override
    public void onCreate() {
        super.onCreate();
        YiLog.d(TAG, "onCreate");
        ToastHelper.init(this);
        DBHttpBeanMediaDetailUtils.Init(getApplicationContext());
        DBLocalMediaDetailBeanUtils.Init(getApplicationContext());
        DBBeanUpLoadVideoInfoUtils.Init(getApplicationContext());
        verifyURL();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        YiLog.d(TAG, "onTerminate");

    }

    private void verifyURL() {

    }
}
