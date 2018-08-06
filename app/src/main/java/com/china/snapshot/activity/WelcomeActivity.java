package com.china.snapshot.activity;

import android.os.Bundle;

import com.china.snapshot.R;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.HttpBeanMediaDetail;
import com.china.snapshot.login.LoginActivity;
import com.china.snapshot.util.DBHttpBeanMediaDetailUtils;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
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
                BmobUser bmobUser = BmobUser.getCurrentUser();
                getMediaData();
                if (bmobUser != null) {
                    // 允许用户使用应用
                    toActivity(MainActivity.class);
                    WelcomeActivity.this.finish();
                } else {
                    toActivity(LoginActivity.class);
                    WelcomeActivity.this.finish();
                }
            }
        }, 10);
    }

    public void getMediaData() {
        BmobQuery<HttpBeanMediaDetail> query = new BmobQuery<HttpBeanMediaDetail>();
        // 按时间降序查询
        query.order("-createdAt");
        query.setLimit(50);
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

