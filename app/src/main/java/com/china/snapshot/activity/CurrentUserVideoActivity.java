package com.china.snapshot.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.china.snapshot.R;
import com.china.snapshot.adapter.CurrentUserVideoAdapter;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.HttpBeanMediaDetail;
import com.china.snapshot.util.DBHttpBeanMediaDetailUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

public class CurrentUserVideoActivity extends BaseActivity {

    @BindView(R.id.rl_current_usr_activity) RecyclerView rlvMainActivity;
    private CurrentUserVideoAdapter currentUserVideoAdapter;
    private List<HttpBeanMediaDetail> httpBeanMediaDetailList = new ArrayList<>();
    private BmobUser bmobUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_user_video);
        ButterKnife.bind(this);
        initRecyclerView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void initRecyclerView() {
        bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            httpBeanMediaDetailList = DBHttpBeanMediaDetailUtils.getInstance().queryDataDependUserName(bmobUser.getUsername());
            if (currentUserVideoAdapter == null) {
                final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                rlvMainActivity.setLayoutManager(gridLayoutManager);
                currentUserVideoAdapter = new CurrentUserVideoAdapter(httpBeanMediaDetailList, this);
                rlvMainActivity.setAdapter(currentUserVideoAdapter);
            } else {
                rlvMainActivity.post(new Runnable() {
                    @Override
                    public void run() {
                        currentUserVideoAdapter.notifyDataSetChanged();
                    }
                });
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveAddFriendSuccessEvent(HttpBeanMediaDetail httpBeanMediaDetail) {
        initRecyclerView();
    }

}
