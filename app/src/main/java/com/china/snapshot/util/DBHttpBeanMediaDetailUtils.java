package com.china.snapshot.util;

import android.content.Context;

import com.aidebar.greendaotest.gen.DaoManager;
import com.aidebar.greendaotest.gen.HttpBeanMediaDetailDao;
import com.china.snapshot.bean.HttpBeanMediaDetail;

import java.util.List;

/**
 * Created by ZhangHaiLong on 2017/9/14.
 */

public class DBHttpBeanMediaDetailUtils {

    private HttpBeanMediaDetailDao dbBeanCommentDetailDao;

    public DBHttpBeanMediaDetailUtils(Context context) {
        dbBeanCommentDetailDao = DaoManager.getInstance(context).getNewSession().getHttpBeanMediaDetailDao();
    }

    //使用单例获取操作数据库的单例
    private static DBHttpBeanMediaDetailUtils dbBeanCommentDetailUtils = null;

    public static DBHttpBeanMediaDetailUtils getInstance() {
        return dbBeanCommentDetailUtils;
    }

    public static void Init(Context context) {
        if (dbBeanCommentDetailUtils == null) {
            dbBeanCommentDetailUtils = new DBHttpBeanMediaDetailUtils(context);
        }
    }

    /**
     * 完成对数据库中插入一条数据操作
     *
     * @param dbBeanCommentDetail
     * @return
     */
    public void insertOneData(HttpBeanMediaDetail dbBeanCommentDetail) {
        dbBeanCommentDetailDao.insertOrReplace(dbBeanCommentDetail);
    }

    /**
     * 完成对数据库中插入多条数据操作
     *
     * @param dbBeanCommentDetailList
     * @return
     */
    public boolean insertManyData(List<HttpBeanMediaDetail> dbBeanCommentDetailList) {
        boolean flag = false;
        try {
            dbBeanCommentDetailDao.insertOrReplaceInTx(dbBeanCommentDetailList);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库中删除一条数据操作
     *
     * @param dbBeanCommentDetail
     * @return
     */
    public boolean deleteOneData(HttpBeanMediaDetail dbBeanCommentDetail) {
        boolean flag = false;
        try {
            dbBeanCommentDetailDao.delete(dbBeanCommentDetail);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库中删除一条数据 ByKey操作
     *
     * @return
     */
    public boolean deleteOneDataByKey(long id) {
        boolean flag = false;
        try {
            dbBeanCommentDetailDao.deleteByKey(id);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库中批量删除数据操作
     *
     * @return
     */
    public boolean deleteManData(List<HttpBeanMediaDetail> dbBeanCommentDetailList) {
        boolean flag = false;
        try {
            dbBeanCommentDetailDao.deleteInTx(dbBeanCommentDetailList);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库更新数据操作
     *
     * @return
     */
    public boolean updateData(HttpBeanMediaDetail dbBeanCommentDetail) {
        boolean flag = false;
        try {
            dbBeanCommentDetailDao.update(dbBeanCommentDetail);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库批量更新数据操作
     *
     * @return
     */
    public boolean updateManData(List<HttpBeanMediaDetail> dbBeanCommentDetailList) {
        boolean flag = false;
        try {
            dbBeanCommentDetailDao.updateInTx(dbBeanCommentDetailList);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库查询数据操作
     *
     * @return
     */
    public HttpBeanMediaDetail queryDataByID(long commentID) {
        return dbBeanCommentDetailDao.load(commentID);
    }

    /**
     * 完成对数据库按照userName查询数据操作
     *
     * @return
     */
    public List<HttpBeanMediaDetail> queryDataDependUserName(String userName) {

        return dbBeanCommentDetailDao.queryBuilder().where(HttpBeanMediaDetailDao.Properties.UploadUserName.like(userName)).build().list();
    }


    /**
     * 完成对数据库查询所有数据操作
     *
     * @return
     */
    public List<HttpBeanMediaDetail> queryData() {

        return dbBeanCommentDetailDao.loadAll();
    }

    /**
     * 完成对数据库查询所有数据操作
     *
     * @return
     */
    public List<HttpBeanMediaDetail> queryData(String userName) {

        return dbBeanCommentDetailDao.loadAll();
    }

}

