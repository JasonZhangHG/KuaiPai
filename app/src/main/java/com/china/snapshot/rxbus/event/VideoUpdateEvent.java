package com.china.snapshot.rxbus.event;

/**
 * Created by ZhangHaiLong on 2017/9/14.
 */

public class VideoUpdateEvent {

    public final static int TYPE_VIDEO_UPLOAD_SUCCESS = 1;

    public final static int KEY_NEW_MEDIA_UPLOAD_EVENT_UPLOAD_VIDEO_FAILED = 2;

    public int type;

    public VideoUpdateEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public boolean isVideoUploadSuccess() {
        return (getType() == TYPE_VIDEO_UPLOAD_SUCCESS);
    }

    public boolean isVideoUploadFailed() {
        return (getType() == KEY_NEW_MEDIA_UPLOAD_EVENT_UPLOAD_VIDEO_FAILED);
    }
}
