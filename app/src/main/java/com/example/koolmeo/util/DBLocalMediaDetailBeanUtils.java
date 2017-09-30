package com.example.koolmeo.util;

import android.content.Context;

import com.aidebar.greendaotest.gen.DaoManager;
import com.aidebar.greendaotest.gen.LocalMediaDetailBeanDao;
import com.example.koolmeo.bean.LocalMediaDetailBean;

import java.util.List;

/**
 * Created by ZhangHaiLong on 2017/9/16.
 */

public class DBLocalMediaDetailBeanUtils {

    private LocalMediaDetailBeanDao dbBeanCommentDetailDao;

    public DBLocalMediaDetailBeanUtils  (Context context){
        dbBeanCommentDetailDao = DaoManager.getInstance(context).getNewSession().getLocalMediaDetailBeanDao();
    }

    //使用单例获取操作数据库的单例
    private static DBLocalMediaDetailBeanUtils dbBeanCommentDetailUtils=null;
    public static DBLocalMediaDetailBeanUtils getInstance(){
        return  dbBeanCommentDetailUtils;
    }
    public static void Init(Context context){
        if(dbBeanCommentDetailUtils==null){
            dbBeanCommentDetailUtils=new DBLocalMediaDetailBeanUtils(context);
        }
    }

    /**
     * 完成对数据库中插入一条数据操作
     * @param dbBeanCommentDetail
     * @return
     */
    public void insertOneData(LocalMediaDetailBean dbBeanCommentDetail){
        dbBeanCommentDetailDao.insertOrReplace(dbBeanCommentDetail);
    }

    /**
     * 完成对数据库中插入多条数据操作
     * @param dbBeanCommentDetailList
     * @return
     */
    public boolean insertManyData( List<LocalMediaDetailBean> dbBeanCommentDetailList){
        boolean flag = false;
        try{
            dbBeanCommentDetailDao.insertOrReplaceInTx(dbBeanCommentDetailList);
            flag = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库中删除一条数据操作
     * @param dbBeanCommentDetail
     * @return
     */
    public boolean deleteOneData(LocalMediaDetailBean dbBeanCommentDetail){
        boolean flag = false;
        try{
            dbBeanCommentDetailDao.delete(dbBeanCommentDetail);
            flag = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库中删除一条数据 ByKey操作
     * @return
     */
    public boolean deleteOneDataByKey(long id){
        boolean flag = false;
        try{
            dbBeanCommentDetailDao.deleteByKey(id);
            flag = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库中批量删除数据操作
     * @return
     */
    public boolean deleteManData(List<LocalMediaDetailBean> dbBeanCommentDetailList){
        boolean flag = false;
        try{
            dbBeanCommentDetailDao.deleteInTx(dbBeanCommentDetailList);
            flag = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库更新数据操作
     * @return
     */
    public boolean updateData(LocalMediaDetailBean dbBeanCommentDetail){
        boolean flag = false;
        try{
            dbBeanCommentDetailDao.update(dbBeanCommentDetail);
            flag = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库批量更新数据操作
     * @return
     */
    public boolean updateManData(List<LocalMediaDetailBean> dbBeanCommentDetailList){
        boolean flag = false;
        try{
            dbBeanCommentDetailDao.updateInTx(dbBeanCommentDetailList);
            flag = true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 完成对数据库查询数据操作
     * @return
     */
    public LocalMediaDetailBean queryDataByID(long commentID) {
        return dbBeanCommentDetailDao.load(commentID);
    }

    /**
     * 完成对数据库按照MediaID查询数据操作
     * @return
     */
    public List<LocalMediaDetailBean> queryDataDependMediaURL(String mediaURL) {

        return dbBeanCommentDetailDao.queryBuilder().where(LocalMediaDetailBeanDao.Properties.MediaUrl.eq(mediaURL)).build().list();

    }


    /**
     * 完成对数据库查询所有数据操作
     * @return
     */
    public List<LocalMediaDetailBean> queryData() {

        return dbBeanCommentDetailDao.loadAll();
    }

}

