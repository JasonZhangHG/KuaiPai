package com.example.koolmeo.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by ZhangHaiLong on 2017/9/16.
 */
@Entity // 标识实体类，greenDAO会映射成sqlite的一个表，表名为实体类名的大写形式
public class DBBeanUpLoadVideoInfo {
    @Id(autoincrement = false) public long creatTimeAsId;  //把创建时间作为表的ID
    @Property(nameInDb = "DBBeanUpLoadVideoInfo")
    public String locationDesc;
    private String bitmapPath;
    public String mediaLocalPath;
    @Generated(hash = 534420608)
    public DBBeanUpLoadVideoInfo(long creatTimeAsId, String locationDesc,
            String bitmapPath, String mediaLocalPath) {
        this.creatTimeAsId = creatTimeAsId;
        this.locationDesc = locationDesc;
        this.bitmapPath = bitmapPath;
        this.mediaLocalPath = mediaLocalPath;
    }
    @Generated(hash = 337207699)
    public DBBeanUpLoadVideoInfo() {
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
    public String getBitmapPath() {
        return this.bitmapPath;
    }
    public void setBitmapPath(String bitmapPath) {
        this.bitmapPath = bitmapPath;
    }
    public String getMediaLocalPath() {
        return this.mediaLocalPath;
    }
    public void setMediaLocalPath(String mediaLocalPath) {
        this.mediaLocalPath = mediaLocalPath;
    }

    @Override
    public String toString() {
        return "DBBeanUpLoadVideoInfo{" + "creatTimeAsId=" + creatTimeAsId + ", locationDesc='" + locationDesc + '\'' + ", bitmapPath='" + bitmapPath + '\'' + ", mediaLocalPath='" + mediaLocalPath + '\'' + '}';
    }
}
