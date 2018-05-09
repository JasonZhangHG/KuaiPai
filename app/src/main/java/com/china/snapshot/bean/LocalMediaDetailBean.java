package com.china.snapshot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ZhangHaiLong on 2017/9/16.
 */
@Entity // 标识实体类，greenDAO会映射成sqlite的一个表，表名为实体类名的大写形式
public class LocalMediaDetailBean {
    @Id(autoincrement = false) public long creatTimeAsId;  //把创建时间作为表的ID
    @Property(nameInDb = "LocalMediaDetailBean")
    public String locationDesc;
    public String mediaUrl;
    public String thumbnailUrl;
    public String mediaLocalPath;
    public int likes;
    public boolean isLike;

    @Generated(hash = 441034480)
    public LocalMediaDetailBean(long creatTimeAsId, String locationDesc, String mediaUrl, String thumbnailUrl, String mediaLocalPath, int likes, boolean isLike) {
        this.creatTimeAsId = creatTimeAsId;
        this.locationDesc = locationDesc;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.mediaLocalPath = mediaLocalPath;
        this.likes = likes;
        this.isLike = isLike;
    }

    @Generated(hash = 1689000022)
    public LocalMediaDetailBean() {
    }

    public long getCreatTimeAsId() {
        return this.creatTimeAsId;
    }

    public void setCreatTimeAsId(long creatTimeAsId) {
        this.creatTimeAsId = creatTimeAsId;
    }

    public String getLocationDesc() {
        return this.locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getThumbnailUrl() {
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getMediaLocalPath() {
        return this.mediaLocalPath;
    }

    public void setMediaLocalPath(String mediaLocalPath) {
        this.mediaLocalPath = mediaLocalPath;
    }


    public int getLikes() {
        return this.likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean getIsLike() {
        return this.isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    @Override
    public String toString() {
        return "LocalMediaDetailBean{" +
                "creatTimeAsId=" + creatTimeAsId +
                ", locationDesc='" + locationDesc + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", mediaLocalPath='" + mediaLocalPath + '\'' +
                ", likes=" + likes +
                ", isLike=" + isLike +
                '}';
    }
}
