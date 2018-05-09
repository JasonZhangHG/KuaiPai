package com.china.snapshot.base;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.china.snapshot.bean.UploadInfoState;
import com.china.snapshot.log.YiLog;
import com.china.snapshot.util.DBBeanUpLoadVideoInfoUtils;
import com.china.snapshot.util.DBHttpBeanMediaDetailUtils;
import com.china.snapshot.util.DBLocalMediaDetailBeanUtils;
import com.china.snapshot.util.HttpBeanMediaLikeDetailUtils;
import com.china.snapshot.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhangHaiLong on 2017/9/6.
 */

public class BaseApplication extends Application {
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
        Utils.init(getApplicationContext());
        HttpBeanMediaLikeDetailUtils.Init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        YiLog.d(TAG, "onTerminate");

    }

    private void verifyURL() {

    }
}
