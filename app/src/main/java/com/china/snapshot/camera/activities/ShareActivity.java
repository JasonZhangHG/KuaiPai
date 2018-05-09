package com.china.snapshot.camera.activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.china.snapshot.R;
import com.china.snapshot.activity.MainActivity;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.DBBeanUpLoadVideoInfo;
import com.china.snapshot.bean.HttpBeanMediaDetail;
import com.china.snapshot.rxbus.RxBus;
import com.china.snapshot.rxbus.event.VideoUpdateEvent;
import com.china.snapshot.util.DBBeanUpLoadVideoInfoUtils;
import com.china.snapshot.util.ToastHelper;
import com.china.snapshot.view.CustomVideoView;
import com.china.snapshot.view.FlikerProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class ShareActivity extends BaseActivity {

    @BindView(R.id.CustomVideoViewShareActivity) CustomVideoView mCustomVideoView;
    @BindView(R.id.tvBackShareActivity) TextView mBackTextView;
    @BindView(R.id.tvFinishShareActivity) TextView mFinishTextView;
    @BindView(R.id.descriptionShareActivity) EditText mDescriptionEditText;
    @BindView(R.id.titleBar) RelativeLayout titleBar;
    @BindView(R.id.flikerBarShareActivity) FlikerProgressBar flikerBar;
    public static final String KEY_FILE_PATH = "KEY_FILE_PATH";
    public static final String KEY_PICTURE_PATH = "KEY_PICTURE_PATH";
    private String filePath;
    private String description;
    private String picturePath;
    private BmobUser bmobUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        filePath = getIntent().getStringExtra(KEY_FILE_PATH);
        picturePath = getIntent().getStringExtra(KEY_PICTURE_PATH);
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(this, this.getString(R.string.camera_video_format_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        startVideo(filePath);
        bmobUser = BmobUser.getCurrentUser();
    }

    public void startVideo(final String videoPath) {
        mCustomVideoView.setVideoPath(videoPath);
        mCustomVideoView.start();
        mCustomVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(ShareActivity.this, "视频有问题，无法播放", Toast.LENGTH_SHORT).show();
                ShareActivity.this.finish();
                return false;
            }
        });
        mCustomVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        mCustomVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mCustomVideoView.setVideoPath(videoPath);
                mCustomVideoView.start();
            }
        });
    }

    @OnClick({R.id.tvBackShareActivity, R.id.tvFinishShareActivity})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tvBackShareActivity:
                ShareActivity.this.finish();
                break;
            case R.id.tvFinishShareActivity:
                mFinishTextView.setClickable(false);
                flikerBar.setVisibility(View.VISIBLE);
                upLoadVideo();
                break;
            default:
                break;
        }

    }

    public void upLoadVideo() {

        final String[] filePaths = new String[2];
        String mediaPath = filePath;
        filePaths[0] = picturePath;
        filePaths[1] = mediaPath;
        description = mDescriptionEditText.getText().toString();

        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                if (urls.size() == filePaths.length) {//如果数量相等，则代表文件全部上传完成
                    HttpBeanMediaDetail httpBeanMediaDetail = new HttpBeanMediaDetail();
                    httpBeanMediaDetail.setCreatTimeAsId(getTime());
                    httpBeanMediaDetail.setThumbnailUrl(urls.get(0));
                    httpBeanMediaDetail.setMediaUrl(urls.get(1));
                    httpBeanMediaDetail.setLocationDesc(description);
                    httpBeanMediaDetail.setLikes(0);
                    if (bmobUser != null) {
                        httpBeanMediaDetail.setUploadUserName(bmobUser.getUsername());
                    }
                    httpBeanMediaDetail.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                //  ToastHelper.showLongMessage("视频上传成功");
                                RxBus.getDefault().post(new VideoUpdateEvent(VideoUpdateEvent.TYPE_VIDEO_UPLOAD_SUCCESS));
                                flikerBar.setVisibility(View.GONE);
                                toActivity(MainActivity.class);
                                finish();
                            } else {
                                flikerBar.setVisibility(View.GONE);
                                mFinishTextView.setClickable(true);
                                DBBeanUpLoadVideoInfo dbBeanUpLoadVideoInfo = new DBBeanUpLoadVideoInfo();
                                dbBeanUpLoadVideoInfo.setCreatTimeAsId(System.currentTimeMillis());
                                dbBeanUpLoadVideoInfo.setMediaLocalPath(filePath);
                                dbBeanUpLoadVideoInfo.setBitmapPath(picturePath);
                                dbBeanUpLoadVideoInfo.setLocationDesc(description);
                                DBBeanUpLoadVideoInfoUtils.getInstance().insertOneData(dbBeanUpLoadVideoInfo);
                                RxBus.getDefault().post(new VideoUpdateEvent(VideoUpdateEvent.KEY_NEW_MEDIA_UPLOAD_EVENT_UPLOAD_VIDEO_FAILED));
                                ToastHelper.showLongMessage("视频上传失败");
                                finish();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                flikerBar.setVisibility(View.GONE);
                mFinishTextView.setClickable(true);
                DBBeanUpLoadVideoInfo dbBeanUpLoadVideoInfo = new DBBeanUpLoadVideoInfo();
                dbBeanUpLoadVideoInfo.setCreatTimeAsId(System.currentTimeMillis());
                dbBeanUpLoadVideoInfo.setMediaLocalPath(filePath);
                dbBeanUpLoadVideoInfo.setBitmapPath(picturePath);
                dbBeanUpLoadVideoInfo.setLocationDesc(description);
                DBBeanUpLoadVideoInfoUtils.getInstance().insertOneData(dbBeanUpLoadVideoInfo);
                RxBus.getDefault().post(new VideoUpdateEvent(VideoUpdateEvent.KEY_NEW_MEDIA_UPLOAD_EVENT_UPLOAD_VIDEO_FAILED));
                ToastHelper.showLongMessage("视频上传失败");
                finish();
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                flikerBar.setProgress(totalPercent);
            }
        });
    }

//    public boolean saveBitmapToSd(Bitmap bitmap, String filePath) {
//        FileOutputStream outputStream = null;
//        try {
//            File file = new File(filePath);
//            if (file.exists() || file.isDirectory()) {
//                file.delete();
//            }
//            file.createNewFile();
//            outputStream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//        } catch (IOException e) {
//            return false;
//        } finally {
//            if (outputStream != null) {
//                try {
//                    outputStream.flush();
//                    outputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return true;
//    }

    public long getTime() {
        return System.currentTimeMillis();//获取系统时间戳
    }

}
