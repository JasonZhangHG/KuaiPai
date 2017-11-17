package com.china.snapshot.http;

import com.china.snapshot.bean.HttpBeanMediaDetail;
import com.china.snapshot.rxbus.RxBus;
import com.china.snapshot.rxbus.event.VideoUpdateEvent;
import com.china.snapshot.util.ToastHelper;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Created by ZhangHaiLong on 2017/9/16.
 */

public class HttpMediaManage {

    public static void upLoadVideo(String filePath, String picturePath, final String description) {

        final String[] filePaths = new String[2];
        String mediaPath = filePath;
        filePaths[0] = picturePath;
        filePaths[1] = mediaPath;
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> files, List<String> urls) {
                if (urls.size() == filePaths.length) {//如果数量相等，则代表文件全部上传完成
                    HttpBeanMediaDetail httpBeanMediaDetail = new HttpBeanMediaDetail();
                    httpBeanMediaDetail.setCreatTimeAsId(System.currentTimeMillis());
                    httpBeanMediaDetail.setThumbnailUrl(urls.get(0));
                    httpBeanMediaDetail.setMediaUrl(urls.get(1));
                    httpBeanMediaDetail.setLocationDesc(description);
                    httpBeanMediaDetail.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                //  ToastHelper.showLongMessage("视频上传成功");
                                RxBus.getDefault().post(new VideoUpdateEvent(VideoUpdateEvent.TYPE_VIDEO_UPLOAD_SUCCESS));
//                                flikerBar.setVisibility(View.GONE);
//                                toActivity(MainActivity.class);
                            } else {
//                                flikerBar.setVisibility(View.GONE);
                                ToastHelper.showLongMessage("视频上传失败");
//                                mFinishTextView.setClickable(true);

                            }
                        }
                    });

                }
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                //flikerBar.setVisibility(View.GONE);
                ToastHelper.showLongMessage("视频上传失败");
                //mFinishTextView.setClickable(true);
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                //flikerBar.setProgress(totalPercent);
            }
        });
    }
}
