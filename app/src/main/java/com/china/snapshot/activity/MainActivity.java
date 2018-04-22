package com.china.snapshot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.china.snapshot.R;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.HttpBeanMediaDetail;
import com.china.snapshot.camera.capture.FilterCameraActivity;
import com.china.snapshot.login.LoginActivity;
import com.china.snapshot.rxbus.RxBus;
import com.china.snapshot.rxbus.event.VideoUpdateEvent;
import com.china.snapshot.util.DBHttpBeanMediaDetailUtils;
import com.china.snapshot.util.ToastHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ivMainActivityMenu) ImageView ivMainActivityMenu;
    @BindView(R.id.ivMainActivityCamera) ImageView ivMainActivityCamera;
    @BindView(R.id.barTitle) Toolbar barTitle;
    @BindView(R.id.rlvMainActivity) RecyclerView rlvMainActivity;
    @BindView(R.id.nvMainActivity) NavigationView nvMainActivity;
    @BindView(R.id.dlMain) DrawerLayout dlMain;
    @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;

    private Subscription rxSubscription;
    private MainActivityAdapter mainActivityAdapter;
    private List<HttpBeanMediaDetail> httpBeanMediaDetails = new ArrayList<>();


    private ImageView ivMainDrawerBg;
    private ImageView ivMainDrawerUserAvatar;
    private ImageView ivMainDrawerSex;
    private TextView tvMainDrawerNickname;
    private TextView tvMainDrawerNotUploadVideoCount;
    private TextView tvMainDrawerAttention;
    private LinearLayout llMainDrawerVideo;
    private View headView;
    private ImageView ivMainDrawerNotLoginUserAvatar;

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 10; // 每页的数据是10条
    private int curPage = 0; // 当前页的编号，从0开始
    private String lastTime = null;
    private long firstBack = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initRecyclerView();
        queryData(0, STATE_REFRESH);
        registerRxBus();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterRxBus();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initView() {
        headView = nvMainActivity.inflateHeaderView(R.layout.main_activity_drawerlayout_header_layout);
        ivMainDrawerNotLoginUserAvatar = (ImageView) headView.findViewById(R.id.ivMainDrawerNotLoginUserAvatar);
        ivMainDrawerUserAvatar = (ImageView) headView.findViewById(R.id.ivMainDrawerUserAvatar);
        ivMainDrawerSex = (ImageView) headView.findViewById(R.id.ivMainDrawerSex);
        tvMainDrawerNickname = (TextView) headView.findViewById(R.id.tvMainDrawerNickname);
        tvMainDrawerNotUploadVideoCount = (TextView) headView.findViewById(R.id.tvMainDrawerNotUploadVideoCount);
        tvMainDrawerAttention = (TextView) headView.findViewById(R.id.tvMainDrawerAttention);
        llMainDrawerVideo = (LinearLayout) headView.findViewById(R.id.llMainDrawerVideo);

        headView.findViewById(R.id.flMainDrawerUser).setOnClickListener(this);
        llMainDrawerVideo.setOnClickListener(this);
        headView.findViewById(R.id.llMainDrawerNews).setOnClickListener(this);
        headView.findViewById(R.id.llMainDrawerFeedback).setOnClickListener(this);
        headView.findViewById(R.id.llMainDrawerSetting).setOnClickListener(this);
        headView.findViewById(R.id.ivMainDrawerUserAvatar).setOnClickListener(this);
        headView.findViewById(R.id.llMainDrawerLogin).setOnClickListener(this);
        ivMainActivityMenu.setOnClickListener(this);
        ivMainActivityCamera.setOnClickListener(this);

        BmobUser bmobUser = BmobUser.getCurrentUser();
        if (bmobUser != null) {
            tvMainDrawerNickname.setText(bmobUser.getUsername());
        }
    }

    public void initRecyclerView() {
        httpBeanMediaDetails = DBHttpBeanMediaDetailUtils.getInstance().queryData();
        if (mainActivityAdapter == null) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            rlvMainActivity.setLayoutManager(gridLayoutManager);
            mainActivityAdapter = new MainActivityAdapter();
            rlvMainActivity.setAdapter(mainActivityAdapter);
            refresh();
        } else {
            mainActivityAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveAddFriendSuccessEvent(HttpBeanMediaDetail httpBeanMediaDetail) {
        initRecyclerView();
    }

    public void refresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(5000);
                // 下拉刷新(从第一页开始装载数据)
                queryData(0, STATE_REFRESH);

            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(5000);
                // 上拉加载更多(加载下一页数据)
                queryData(curPage, STATE_MORE);
            }
        });
    }

    /**
     * 分页获取数据
     *
     * @param page       页码
     * @param actionType ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryData(int page, final int actionType) {
        try {
            BmobQuery<HttpBeanMediaDetail> query = new BmobQuery<>();
            // 按时间降序查询
            query.order("-createdAt");
            int count = 0;
            // 如果是加载更多
            if (actionType == STATE_MORE) {
                // 处理时间查询
                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = sdf.parse(lastTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 只查询小于等于最后一个item发表时间的数据
                query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
                // 跳过之前页数并去掉重复数据
                query.setSkip(page * count + 1);
            } else {
                // 下拉刷新
                page = 0;
                query.setSkip(page);
            }
            // 设置每页数据个数
            query.setLimit(limit);
            // 查找数据
            query.findObjects(new FindListener<HttpBeanMediaDetail>() {
                @Override
                public void done(List<HttpBeanMediaDetail> list, BmobException e) {
                    if (e == null) {
                        if (list.size() > 0) {

                            if (actionType == STATE_REFRESH) {
                                // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                                curPage = 0;
                                httpBeanMediaDetails.clear();
                                // 获取最后时间
                                lastTime = list.get(list.size() - 1).getCreatedAt();
                            }

                            // 将本次查询的数据添加到bankCards中
                            for (HttpBeanMediaDetail td : list) {
                                httpBeanMediaDetails.add(td);
                            }
                            mainActivityAdapter.notifyDataSetChanged();
                            DBHttpBeanMediaDetailUtils.getInstance().insertManyData(httpBeanMediaDetails);
                            // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                            curPage++;
                            finishRefresh(actionType);

                        } else if (actionType == STATE_MORE) {
                            ToastHelper.showLongMessage("没有更多数据了");
                            finishRefresh(actionType);
                        } else if (actionType == STATE_REFRESH) {
                            ToastHelper.showLongMessage("没有数据");
                            finishRefresh(actionType);
                        }
                        // refreshLayout.finishRefresh();
                    } else {
                        ToastHelper.showLongMessage("查询失败:");
                        finishRefresh(actionType);
                        // refreshLayout.finishRefresh();
                    }
                }
            });
        } catch (Exception e) {

        }

    }


    public void finishRefresh(final int actionType) {
        if (actionType == STATE_REFRESH) {
            refreshLayout.finishRefresh();
        } else if (actionType == STATE_REFRESH) {
            refreshLayout.finishLoadmore();
        }
    }

    private void unregisterRxBus() {
        if (rxSubscription != null && !rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }


    // Subscription rxSubscription;
    private void registerRxBus() {
        // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObservable(VideoUpdateEvent.class).subscribe(new Action1<VideoUpdateEvent>() {
            @Override
            public void call(VideoUpdateEvent videoUpdateEvent) {
                if (videoUpdateEvent.isVideoUploadSuccess()) {
                    // 下拉刷新(从第一页开始装载数据)
                    queryData(0, STATE_REFRESH);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMainActivityMenu:
                openDrawer();
                break;
            case R.id.ivMainActivityCamera:
                toActivity(FilterCameraActivity.class);
                break;
            case R.id.flMainDrawerUser:
                //进入自己的主页
                closeDrawer();
                break;
            case R.id.ivMainDrawerUserAvatar:
                //进入自己的主页
                closeDrawer();
                break;
            case R.id.llMainDrawerVideo:
                //视频管理页
                toActivity(CurrentUserVideoActivity.class);
                closeDrawer();
                break;
            case R.id.llMainDrawerNews:
                //消息通知页
                // toActivity(MessageActivity
                // .class);
                closeDrawer();
                break;
            case R.id.llMainDrawerFeedback:
                //系统反馈
                toActivity(FeedbackActivity.class);
                closeDrawer();
                break;
            case R.id.llMainDrawerSetting:
                //个人页
                  toActivity(UserInfoActivity.class);
                closeDrawer();
                break;
            case R.id.llMainDrawerLogin:
                //登陆页
                 toActivity(LoginActivity.class);
                closeDrawer();
                break;
            default:
                break;

        }
    }

    private boolean isDrawerOpen() {
        return dlMain.isDrawerOpen(Gravity.LEFT);
    }

    private void openDrawer() {
        //打开抽屉
        if (!isDrawerOpen()) {
            dlMain.openDrawer(Gravity.LEFT);
        }
    }


    private void closeDrawer() {
        doInUI(new Runnable() {
            @Override
            public void run() {
                //关闭抽屉
                if (isDrawerOpen()) {
                    dlMain.closeDrawer(Gravity.LEFT);
                    dlMain.closeDrawers();
                }
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            if (System.currentTimeMillis() - firstBack < 2000) {
                super.onBackPressed();
            } else {
                firstBack = System.currentTimeMillis();
                ToastHelper.showShortMessage(R.string.quit_app);
            }
        }
    }

    public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> {

        @Override
        public MainActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_main_activity, parent, false);
            MainActivityViewHolder holder = new MainActivityViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MainActivityViewHolder holder, final int position) {
            if (!TextUtils.isEmpty(httpBeanMediaDetails.get(position).getThumbnailUrl())) {
                Glide.with(MainActivity.this).load(httpBeanMediaDetails.get(position).getThumbnailUrl()).placeholder(R.drawable.item_video_moments_pic_2).centerCrop().into(holder.ivVideoItemMainActivity);
                holder.ivVideoItemMainActivity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
                        intent.putExtra(MediaPlayerActivity.INTENT_TO_MEDIAPLAYER_ACTIVITY_IMAGE_URL, httpBeanMediaDetails.get(position).getThumbnailUrl());
                        intent.putExtra(MediaPlayerActivity.INTENT_TO_MEDIAPLAYER_ACTIVITY_MEDIA_URL, httpBeanMediaDetails.get(position).getMediaUrl());
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return httpBeanMediaDetails.size();
        }

        public class MainActivityViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ivVideoItemMainActivity) ImageView ivVideoItemMainActivity;

            public MainActivityViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}