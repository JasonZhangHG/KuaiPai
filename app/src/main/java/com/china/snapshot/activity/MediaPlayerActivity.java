package com.china.snapshot.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.china.snapshot.R;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.LocalMediaDetailBean;
import com.china.snapshot.util.DBLocalMediaDetailBeanUtils;
import com.china.snapshot.view.CustomVideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class MediaPlayerActivity extends BaseActivity {

    @BindView(R.id.cusVideoViewMediaPlayerActivity) CustomVideoView cusVideoView;
    @BindView(R.id.ivVideoViewMediaPlayerActivity) ImageView mImageViewBg;

    public static String INTENT_TO_MEDIAPLAYER_ACTIVITY_MEDIA_URL;
    public static String INTENT_TO_MEDIAPLAYER_ACTIVITY_IMAGE_URL;

    private String mediaName;
    private String mediaUrl;
    private String imageUrl;
    private List<LocalMediaDetailBean> localMediaDetailBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        ButterKnife.bind(this);
        mediaUrl = getIntent().getStringExtra(INTENT_TO_MEDIAPLAYER_ACTIVITY_MEDIA_URL);
        imageUrl = getIntent().getStringExtra(INTENT_TO_MEDIAPLAYER_ACTIVITY_IMAGE_URL);

        mImageViewBg.setVisibility(View.VISIBLE);
        Glide.with(MediaPlayerActivity.this).load(imageUrl).placeholder(R.drawable.item_video_moments_pic_2).centerCrop().into(mImageViewBg);
        localMediaDetailBeanList = DBLocalMediaDetailBeanUtils.getInstance().queryDataDependMediaURL(mediaUrl);
        if (localMediaDetailBeanList.size() == 1) {
            mImageViewBg.setVisibility(View.GONE);
            startVideo(localMediaDetailBeanList.get(0).getMediaLocalPath());
        } else {
            mediaName = mediaUrl.substring(mediaUrl.lastIndexOf("/") + 1);
            BmobFile bmobfile = new BmobFile(mediaName, "", mediaUrl);
            downloadFile(bmobfile);
        }
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
}
