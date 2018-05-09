package com.china.snapshot.util;

import android.content.Context;

import com.aidebar.greendaotest.gen.DaoManager;
import com.aidebar.greendaotest.gen.HttpBeanMediaLikeDetailDao;
import com.china.snapshot.bean.HttpBeanMediaLikeDetail;

import java.util.List;

public class HttpBeanMediaLikeDetailUtils {

    private HttpBeanMediaLikeDetailDao dbBeanCommentDetailDao;

    public HttpBeanMediaLikeDetailUtils(Context context) {
        dbBeanCommentDetailDao = DaoManager.getInstance(context).getNewSession().getHttpBeanMediaLikeDetailDao();
    }

    //使用单例获取操作数据库的单例
    private static HttpBeanMediaLikeDetailUtils dbBeanCommentDetailUtils = null;

    public static HttpBeanMediaLikeDetailUtils getInstance() {
        return dbBeanCommentDetailUtils;
    }

    public static void Init(Context context) {
        if (dbBeanCommentDetailUtils == null) {
            dbBeanCommentDetailUtils = new HttpBeanMediaLikeDetailUtils(context);
        }
    }

    /**
     * 完成对数据库中插入一条数据操作
     *
     * @param dbBeanCommentDetail
     * @return
     */
    public void insertOneData(HttpBeanMediaLikeDetail dbBeanCommentDetail) {
        dbBeanCommentDetailDao.insertOrReplace(dbBeanCommentDetail);
    }

    /**
     * 完成对数据库中插入多条数据操作
     *
     * @param dbBeanCommentDetailList
     * @return
     */
    public boolean insertManyData(List<HttpBeanMediaLikeDetail> dbBeanCommentDetailList) {
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
    public boolean deleteOneData(HttpBeanMediaLikeDetail dbBeanCommentDetail) {
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
    public boolean deleteOneDataByKey(String id) {
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
    public boolean deleteManData(List<HttpBeanMediaLikeDetail> dbBeanCommentDetailList) {
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
    public boolean updateData(HttpBeanMediaLikeDetail dbBeanCommentDetail) {
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
    public boolean updateManData(List<HttpBeanMediaLikeDetail> dbBeanCommentDetailList) {
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
     * 完成对数据库按照userName查询数据操作
     *
     * @return
     */
    public List<HttpBeanMediaLikeDetail> queryDataDependmediaUrl(String mediaUrl) {
        return dbBeanCommentDetailDao.queryBuilder().where(HttpBeanMediaLikeDetailDao.Properties.MediaUrl.like(mediaUrl)).build().list();
    }

    /**
     * 完成对数据库查询数据操作
     *
     * @return
     */
    public HttpBeanMediaLikeDetail queryDataByID(String id) {
        return dbBeanCommentDetailDao.load(id);
    }
}
