package com.china.snapshot.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.china.snapshot.R;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.HttpBeanMediaDetail;
import com.china.snapshot.bean.HttpBeanMediaLikeDetail;
import com.china.snapshot.bean.LocalMediaDetailBean;
import com.china.snapshot.camera.activities.AndroidShare;
import com.china.snapshot.util.DBHttpBeanMediaDetailUtils;
import com.china.snapshot.util.DBLocalMediaDetailBeanUtils;
import com.china.snapshot.util.GsonConverter;
import com.china.snapshot.util.HttpBeanMediaLikeDetailUtils;
import com.china.snapshot.view.CustomVideoView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;

public class MediaPlayerActivity extends BaseActivity {

    @BindView(R.id.cusVideoViewMediaPlayerActivity) CustomVideoView cusVideoView;
    @BindView(R.id.ivVideoViewMediaPlayerActivity) ImageView mImageViewBg;
    @BindView(R.id.btn_share_media_player_activity) Button btnShareMediaPlayerActivity;
    @BindView(R.id.cb_like_media_player_activity) CheckBox mLikeCheckBox;
    @BindView(R.id.tv_like_media_player_activity) TextView mLikeTextView;
    @BindView(R.id.ll_like_media_player_activity) LinearLayout mLike;

    private String mediaName;
    private String mediaUrl;
    private String imageUrl;
    private List<LocalMediaDetailBean> localMediaDetailBeanList = new ArrayList<>();
    public static String INTENT_TO_MEDIAPLAYER_ACTIVITY_IMAGE_URL;
    private int likes;
    private boolean isLiked;
    private HttpBeanMediaDetail httpBeanMediaDetail;
    private BmobUser bmobUser;
    private HttpBeanMediaLikeDetail httpBeanMediaLikeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        bmobUser = BmobUser.getCurrentUser();
        httpBeanMediaDetail = GsonConverter.fromJson(getIntent().getStringExtra(INTENT_TO_MEDIAPLAYER_ACTIVITY_IMAGE_URL), HttpBeanMediaDetail.class);
        if (httpBeanMediaDetail != null) {
            mediaUrl = httpBeanMediaDetail.getMediaUrl();
            imageUrl = httpBeanMediaDetail.getThumbnailUrl();
            likes = httpBeanMediaDetail.getLikes();
            httpBeanMediaLikeDetail = HttpBeanMediaLikeDetailUtils.getInstance().queryDataByID(mediaUrl);
            if (httpBeanMediaLikeDetail == null) {
                isLiked = false;
            } else {
                isLiked = httpBeanMediaLikeDetail.getIsLike();
            }
        }

        mImageViewBg.setVisibility(View.VISIBLE);
        Glide.with(MediaPlayerActivity.this).load(imageUrl).placeholder(R.drawable.item_video_moments_pic_2).centerCrop().into(mImageViewBg);
        localMediaDetailBeanList = DBLocalMediaDetailBeanUtils.getInstance().queryDataDependMediaURL(mediaUrl);
        mLikeTextView.setText("喜欢数：" + likes);
        if (isLiked) {
            mLikeCheckBox.setChecked(true);
        } else {
            mLikeCheckBox.setChecked(false);
        }
        if (localMediaDetailBeanList.size() == 1) {
            mImageViewBg.setVisibility(View.GONE);
            startVideo(localMediaDetailBeanList.get(0).getMediaLocalPath());
        } else {
            mediaName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
            BmobFile bmobfile = new BmobFile(mediaName, "", mediaUrl);
            downloadFile(bmobfile);
        }
    }

    @OnClick(R.id.ll_like_media_player_activity)
    public void onBothClicked(View view) {
        if (mLikeCheckBox.isChecked()) {
            mLikeCheckBox.setChecked(false);
            likes = likes - 1;
        } else {
            mLikeCheckBox.setChecked(true);
            likes = likes + 1;
        }
        mLikeTextView.setText("喜欢数：" + likes);

        httpBeanMediaDetail.setLikes(likes);
        httpBeanMediaDetail.update(httpBeanMediaDetail.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
            }
        });
        DBHttpBeanMediaDetailUtils.getInstance().updateData(httpBeanMediaDetail);
        if (httpBeanMediaLikeDetail == null) {
            httpBeanMediaLikeDetail = new HttpBeanMediaLikeDetail();
            httpBeanMediaLikeDetail.setUserName(mediaUrl);
            httpBeanMediaLikeDetail.setIsLike(mLikeCheckBox.isChecked());
            HttpBeanMediaLikeDetailUtils.getInstance().insertOneData(httpBeanMediaLikeDetail);
        } else {
            httpBeanMediaLikeDetail.setIsLike(mLikeCheckBox.isChecked());
            HttpBeanMediaLikeDetailUtils.getInstance().updateData(httpBeanMediaLikeDetail);
        }
        EventBus.getDefault().post(new HttpBeanMediaDetail());
    }

    private void downloadFile(BmobFile file) {
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                //toast("开始下载...");
                showLoading();
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    dismissLoading();
                    mImageViewBg.setVisibility(View.GONE);
                    startVideo(savePath);
                    LocalMediaDetailBean localMediaDetailBean = new LocalMediaDetailBean();
                    localMediaDetailBean.setCreatTimeAsId(System.currentTimeMillis());
                    localMediaDetailBean.setMediaUrl(mediaUrl);
                    localMediaDetailBean.setThumbnailUrl(imageUrl);
                    localMediaDetailBean.setMediaLocalPath(savePath);
                    if (mLikeCheckBox != null) {
                        localMediaDetailBean.setIsLike(mLikeCheckBox.isChecked());
                    }
                    localMediaDetailBean.setLikes(likes);
                    DBLocalMediaDetailBeanUtils.getInstance().insertOneData(localMediaDetailBean);
                } else {
                    dismissLoading();
                    // toast("下载失败："+e.getErrorCode()+","+e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {

            }

        });
    }

    public void startVideo(final String videoPath) {
        cusVideoView.setVideoPath(videoPath);
        cusVideoView.start();
        cusVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(MediaPlayerActivity.this, "视频有问题，无法播放", Toast.LENGTH_SHORT).show();
                MediaPlayerActivity.this.finish();
                return false;
            }
        });
        cusVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        cusVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                cusVideoView.setVideoPath(videoPath);
                cusVideoView.start();
            }
        });
    }

    @OnClick(R.id.btn_share_media_player_activity)
    public void share() {
        AndroidShare as = new AndroidShare(this, "Share video :  " + mediaUrl, "");
        as.show();
    }
}
