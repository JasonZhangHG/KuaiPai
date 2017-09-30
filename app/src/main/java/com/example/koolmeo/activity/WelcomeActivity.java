package com.example.koolmeo.activity;

import android.os.Bundle;

import com.example.koolmeo.R;
import com.example.koolmeo.base.BaseActivity;
import com.example.koolmeo.bean.HttpBeanMediaDetail;
import com.example.koolmeo.util.DBHttpBeanMediaDetailUtils;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //第一：默认初始化
        Bmob.initialize(this, "32896c5e940df9a96201ab7ea3bfffe7");
        doInUI(new Runnable() {
            @Override
            public void run() {
                getMediaData();
                toActivity(MainActivity.class);
                WelcomeActivity.this.finish();
            }
        }, 10);
    }

    public void getMediaData() {
        BmobQuery<HttpBeanMediaDetail> query = new BmobQuery<HttpBeanMediaDetail>();
        // 按时间降序查询
        query.order("-createdAt");
        query.setLimit(10);
        query.findObjects(new FindListener<HttpBeanMediaDetail>() {
            @Override
            public void done(List<HttpBeanMediaDetail> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        DBHttpBeanMediaDetailUtils.getInstance().insertManyData(list);
                    }
                }
            }
        });
    }
}

