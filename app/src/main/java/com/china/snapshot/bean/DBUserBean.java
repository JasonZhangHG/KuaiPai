package com.china.snapshot.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import cn.bmob.v3.BmobUser;
@Entity
public class DBUserBean extends BmobUser {

    @Id(autoincrement = false)
    public long creatTimeAsId;//录入的具体数据的时间作为ID
    @Property(nameInDb = "DBUserBean")
    public String name;
    public String old;
    public String tellPhone;
    public String mail;
    @Generated(hash = 1736242034)
    public DBUserBean(long creatTimeAsId, String name, String old, String tellPhone,
            String mail) {
        this.creatTimeAsId = creatTimeAsId;
        this.name = name;
        this.old = old;
        this.tellPhone = tellPhone;
        this.mail = mail;
    }
    @Generated(hash = 613699683)
    public DBUserBean() {
    }
    public long getCreatTimeAsId() {
        return this.creatTimeAsId;
    }
    public void setCreatTimeAsId(long creatTimeAsId) {
        this.creatTimeAsId = creatTimeAsId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOld() {
        return this.old;
    }
    public void setOld(String old) {
        this.old = old;
    }
    public String getTellPhone() {
        return this.tellPhone;
    }
    public void setTellPhone(String tellPhone) {
        this.tellPhone = tellPhone;
    }
    public String getMail() {
        return this.mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "DBUserBean{" +
                "creatTimeAsId=" + creatTimeAsId +
                ", name='" + name + '\'' +
                ", old='" + old + '\'' +
                ", tellPhone='" + tellPhone + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}

