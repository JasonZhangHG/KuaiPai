package com.china.snapshot.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by ZhangHaiLong on 2017/9/17.
 */

public class FeedBackBean extends BmobObject {
    private long creatTimeAsId;
    private String feedbackContent;
    private String email;

    public FeedBackBean(long creatTimeAsId, String feedbackContent, String email) {
        this.creatTimeAsId = creatTimeAsId;
        this.feedbackContent = feedbackContent;
        this.email = email;
    }

    public long getCreatTimeAsId() {
        return creatTimeAsId;
    }

    public void setCreatTimeAsId(long creatTimeAsId) {
        this.creatTimeAsId = creatTimeAsId;
    }

    public String getFeedbackContent() {
        return feedbackContent;
    }

    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "FeedBackBean{" + "creatTimeAsId=" + creatTimeAsId + ", feedbackContent='" + feedbackContent + '\'' + ", email='" + email + '\'' + '}';
    }
}
