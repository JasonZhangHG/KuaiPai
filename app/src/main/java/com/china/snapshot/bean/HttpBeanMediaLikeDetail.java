package com.china.snapshot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HttpBeanMediaLikeDetail {
    @Id(autoincrement = false)
    //把userName作为表的ID
    public String userName;
    @Property(nameInDb = "HttpBeanMediaLikeDetail")
    public String mediaUrl;
    public boolean isLike;

    @Generated(hash = 676280247)
    public HttpBeanMediaLikeDetail(String userName, String mediaUrl,
                                   boolean isLike) {
        this.userName = userName;
        this.mediaUrl = mediaUrl;
        this.isLike = isLike;
    }

    @Generated(hash = 936039959)
    public HttpBeanMediaLikeDetail() {
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public boolean getIsLike() {
        return this.isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    @Override
    public String toString() {
        return "HttpBeanMediaLikeDetail{" +
                "userName='" + userName + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", isLike=" + isLike +
                '}';
    }
}
