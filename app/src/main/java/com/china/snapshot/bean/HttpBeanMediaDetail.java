package com.china.snapshot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import cn.bmob.v3.BmobObject;

/**
 * Created by ZhangHaiLong on 2017/9/13.
 */

@Entity // 标识实体类，greenDAO会映射成sqlite的一个表，表名为实体类名的大写形式
public class HttpBeanMediaDetail extends BmobObject {
    @Id(autoincrement = false) public long creatTimeAsId;  //把创建时间作为表的ID
    @Property(nameInDb = "HttpBeanMediaDetail")
    public String locationDesc;
    public String mediaUrl;
    public String thumbnailUrl;
    public String uploadUserName;

    @Generated(hash = 408243406)
    public HttpBeanMediaDetail(long creatTimeAsId, String locationDesc, String mediaUrl, String thumbnailUrl, String uploadUserName) {
        this.creatTimeAsId = creatTimeAsId;
        this.locationDesc = locationDesc;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.uploadUserName = uploadUserName;
    }

    @Generated(hash = 32068878)
    public HttpBeanMediaDetail() {
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

    @Override
    public String toString() {
        return "HttpBeanMediaDetail{" + "creatTimeAsId=" + creatTimeAsId + ", locationDesc='" + locationDesc + '\'' + ", mediaUrl='" + mediaUrl + '\'' + ", thumbnailUrl='" + thumbnailUrl + '\'' + '}';
    }

    public String getUploadUserName() {
        return this.uploadUserName;
    }

    public void setUploadUserName(String uploadUserName) {
        this.uploadUserName = uploadUserName;
    }
}
