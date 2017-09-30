package com.example.koolmeo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.koolmeo.R;
import com.example.koolmeo.base.BaseActivity;
import com.example.koolmeo.base.BaseApplication;
import com.example.koolmeo.bean.DBBeanUpLoadVideoInfo;
import com.example.koolmeo.bean.HttpBeanMediaDetail;
import com.example.koolmeo.bean.UploadInfoState;
import com.example.koolmeo.camera.activities.ShareActivity;
import com.example.koolmeo.rxbus.RxBus;
import com.example.koolmeo.rxbus.event.VideoUpdateEvent;
import com.example.koolmeo.util.DBBeanUpLoadVideoInfoUtils;
import com.example.koolmeo.util.ToastHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.example.koolmeo.base.BaseApplication.uploadingInfoPositionList;

public class VideoManageActivity extends BaseActivity {
    @BindView(R.id.tvVideoManageActivityTitle1) TextView tvVideoManageActivityTitle1;
    @BindView(R.id.tvVideoManageActivitySelect1) TextView tvVideoManageActivitySelect1;
    @BindView(R.id.ivVideoManageActivityBack1) ImageView ivVideoManageActivityBack1;
    @BindView(R.id.rlVideoManageActivityToolbar1) RelativeLayout rlVideoManageActivityToolbar1;
    @BindView(R.id.ivVideoManageActivityBack2) ImageView ivVideoManageActivityBack2;
    @BindView(R.id.tvVideoManageActivityVideoCount2) TextView tvVideoManageActivityVideoCount2;
    @BindView(R.id.tvVideoManageActivitySelectAll2) TextView tvVideoManageActivitySelectAll2;
    @BindView(R.id.tvVideoManageActivityCancel2) TextView tvVideoManageActivityCancel2;
    @BindView(R.id.rlVideoManageActivityToolbar2) RelativeLayout rlVideoManageActivityToolbar2;
    @BindView(R.id.barTitle) Toolbar barTitle;
    @BindView(R.id.tvVideoManageActivityLine) TextView tvVideoManageActivityLine;
    @BindView(R.id.ivVideoManageActivityBlankPageImage) ImageView ivVideoManageActivityBlankPageImage;
    @BindView(R.id.tvVideoManageActivityBlankPageText) TextView tvVideoManageActivityBlankPageText;
    @BindView(R.id.rlVideoManageActivity) RecyclerView rlVideoManageActivity;
    @BindView(R.id.tvVideoManageActivityUploadLine2) TextView tvVideoManageActivityUploadLine2;
    @BindView(R.id.llVideoManageActivityUpLoad2) LinearLayout llVideoManageActivityUpLoad2;
    @BindView(R.id.llVideoManageActivityDelete2) LinearLayout llVideoManageActivityDelete2;
    @BindView(R.id.llVideoManageActivityUploadAndDelete2) LinearLayout llVideoManageActivityUploadAndDelete2;
    @BindView(R.id.ivVideoManageActivityUpLoad2) ImageView ivVideoManageActivityUpLoad2;
    @BindView(R.id.ivVideoManageActivityDelete2) ImageView ivVideoManageActivityDelete2;

    private VideoManageActivityAdapter videoManageActivityAdapter;
    private List<DBBeanUpLoadVideoInfo> dbBeanUpLoadVideoInfoList;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private HashMap<Integer, Boolean> isSelectedHashMap;
    private Subscription rxSubscription;
    private boolean uploadingStatus = false;
    private boolean selectStatus = false;
    private int checkNum = 0; // 记录选中的条目数量
    private int status = 1;//记录
    private int state = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_manage);
        ButterKnife.bind(this);
        showLoading();
        rlVideoManageActivity.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        tvVideoManageActivityVideoCount2.setText(checkNum + " " + getResources().getString(R.string.video_manage_activity_video_count));
        getDataFromDB();
        refreshMediaUpdateEvent();
    }

    public void getDataFromDB() {
        Observable.create(new Observable.OnSubscribe<List<Bitmap>>() {
            @Override
            public void call(Subscriber<? super List<Bitmap>> subscriber) {
                dbBeanUpLoadVideoInfoList = DBBeanUpLoadVideoInfoUtils.getInstance().queryData();
                for (int i = 0; i < dbBeanUpLoadVideoInfoList.size(); i++) {
                    Bitmap b = BitmapFactory.decodeFile(dbBeanUpLoadVideoInfoList.get(i).getBitmapPath());
                    if (b != null) {
                        bitmapList.add(b);
                    }
                }
                subscriber.onNext(bitmapList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<Bitmap>>() {
            @Override
            public void onCompleted() {
                dismissLoading();
            }

            @Override
            public void onError(Throwable e) {
                dismissLoading();
            }

            @Override
            public void onNext(List<Bitmap> bitmaps) {
                dismissLoading();
                if (bitmaps.size() == 0) {
                    tvVideoManageActivitySelect1.setTextColor(ContextCompat.getColor(VideoManageActivity.this, R.color.media_manage_activity_select));
                    tvVideoManageActivitySelect1.setClickable(false);
                    ivVideoManageActivityBlankPageImage.setVisibility(View.VISIBLE);
                    tvVideoManageActivityBlankPageText.setVisibility(View.VISIBLE);
                    tvVideoManageActivityBlankPageText.setText(getResources().getString(R.string.you_do_not_have_not_upload_the_video));

                } else {
                    ivVideoManageActivityBlankPageImage.setVisibility(View.GONE);
                    tvVideoManageActivityBlankPageText.setVisibility(View.GONE);
                    videoManageActivityAdapter = new VideoManageActivityAdapter(bitmaps);
                    rlVideoManageActivity.setAdapter(videoManageActivityAdapter);
                }
            }
        });
    }

    //设置点击事件
    @OnClick({R.id.ivVideoManageActivityBack1, R.id.ivVideoManageActivityBack2, R.id.tvVideoManageActivitySelect1, R.id.tvVideoManageActivitySelectAll2, R.id.tvVideoManageActivityCancel2, R.id.llVideoManageActivityUpLoad2, R.id.llVideoManageActivityDelete2})
    public void onClick(View view) {
        switch (view.getId()) {
            //点击返回
            case R.id.ivVideoManageActivityBack1:
            case R.id.ivVideoManageActivityBack2:
                VideoManageActivity.this.finish();
                break;

            //处理点击选择点击事件
            case R.id.tvVideoManageActivitySelect1:
                rlVideoManageActivityToolbar1.setVisibility(View.GONE);
                rlVideoManageActivityToolbar2.setVisibility(View.VISIBLE);
                tvVideoManageActivityUploadLine2.setVisibility(View.VISIBLE);
                llVideoManageActivityUploadAndDelete2.setVisibility(View.VISIBLE);
                state = 2;
                videoManageActivityAdapter.notifyDataSetChanged();
                break;
            case R.id.tvVideoManageActivitySelectAll2:
                if (status == 1) {
                    for (int i = 0; i < bitmapList.size(); i++) {
                        if ((uploadingInfoPositionList != null) && (uploadingInfoPositionList.size() > 0)) {
                            for (int j = 0; j < uploadingInfoPositionList.size(); j++) {
                                if (dbBeanUpLoadVideoInfoList.get(i).getCreatTimeAsId() == uploadingInfoPositionList.get(j).id) {
                                    selectStatus = true;
                                }
                            }
                            if (selectStatus) {
                                videoManageActivityAdapter.getIsSelected().put(i, false);
                                selectStatus = false;
                            } else {
                                videoManageActivityAdapter.getIsSelected().put(i, true);
                            }
                        } else {
                            videoManageActivityAdapter.getIsSelected().put(i, true);
                        }
                    }
                    if ((uploadingInfoPositionList != null) && (uploadingInfoPositionList.size() > 0)) {
                        checkNum = (bitmapList.size()) - (uploadingInfoPositionList.size());
                    } else {
                        checkNum = bitmapList.size();
                    }
                    dataChanged();
                    tvVideoManageActivitySelectAll2.setText(getResources().getString(R.string.video_manage_activity_cancel_all));
                    status = 2;
                    if (checkNum > 0) {
                        ivVideoManageActivityUpLoad2.setPressed(true);
                        ivVideoManageActivityDelete2.setPressed(true);
                    } else {
                        ivVideoManageActivityUpLoad2.setPressed(false);
                        ivVideoManageActivityDelete2.setPressed(false);
                    }
                } else if (status == 2) {
                    for (int i = 0; i < bitmapList.size(); i++) {
                        if (videoManageActivityAdapter.getIsSelected().get(i)) {
                            videoManageActivityAdapter.getIsSelected().put(i, false);
                            checkNum--;// 数量减1
                        }
                    }
                    dataChanged();
                    tvVideoManageActivitySelectAll2.setText(getResources().getString(R.string.video_manage_activity_select_all));
                    status = 1;
                    if (checkNum > 0) {
                        ivVideoManageActivityUpLoad2.setPressed(true);
                        ivVideoManageActivityDelete2.setPressed(true);
                    } else {
                        ivVideoManageActivityUpLoad2.setPressed(false);
                        ivVideoManageActivityDelete2.setPressed(false);
                    }
                }
                break;
            case R.id.tvVideoManageActivityCancel2:
                rlVideoManageActivityToolbar1.setVisibility(View.VISIBLE);
                rlVideoManageActivityToolbar2.setVisibility(View.GONE);
                tvVideoManageActivityUploadLine2.setVisibility(View.GONE);
                llVideoManageActivityUploadAndDelete2.setVisibility(View.GONE);
                state = 1;
                videoManageActivityAdapter.notifyDataSetChanged();
                break;
            case R.id.llVideoManageActivityUpLoad2:
                isSelectedHashMap = videoManageActivityAdapter.getIsSelected();
                for (int i = 0; i < isSelectedHashMap.size(); i++) {
                    if (isSelectedHashMap.get(i)) {
                        DBBeanUpLoadVideoInfo dbBeanUpLoadVideoInfo = dbBeanUpLoadVideoInfoList.get(i);

                        videoManageActivityAdapter.upLoadVideo(dbBeanUpLoadVideoInfo);
                        UploadInfoState uploadInfoState = new UploadInfoState();//记录上传状态
                        uploadInfoState.id = dbBeanUpLoadVideoInfoList.get(i).getCreatTimeAsId();
                        uploadInfoState.state = 1;
                        BaseApplication.uploadingInfoPositionList.add(uploadInfoState);
                    }
                }
                break;
            case R.id.llVideoManageActivityDelete2:
                isSelectedHashMap = videoManageActivityAdapter.getIsSelected();
                for (int i = isSelectedHashMap.size() - 1; i >= 0; i--) {
                    if (isSelectedHashMap.get(i) == true) {
                        bitmapList.remove(i);
                        DBBeanUpLoadVideoInfo dbBeanUpLoadVideoInfo = dbBeanUpLoadVideoInfoList.get(i);
                        dbBeanUpLoadVideoInfoList.remove(i);
                        File bitMapFile = new File(dbBeanUpLoadVideoInfo.getBitmapPath());
                        File videoFile = new File(dbBeanUpLoadVideoInfo.getMediaLocalPath());
                        if (bitMapFile.exists()) {
                            bitMapFile.delete();
                        }
                        if (videoFile.exists()) {
                            videoFile.delete();
                        }
                        DBBeanUpLoadVideoInfoUtils.getInstance().deleteOneData(dbBeanUpLoadVideoInfo);
                        checkNum--;
                    }
                }
                isSelectedHashMap.clear();
                videoManageActivityAdapter.initDate();
                dataChanged();
                if (dbBeanUpLoadVideoInfoList.size() == 0) {
                    rlVideoManageActivityToolbar1.setVisibility(View.VISIBLE);
                    rlVideoManageActivityToolbar2.setVisibility(View.GONE);
                    tvVideoManageActivityUploadLine2.setVisibility(View.GONE);
                    llVideoManageActivityUploadAndDelete2.setVisibility(View.GONE);
                    tvVideoManageActivitySelect1.setTextColor(ContextCompat.getColor(VideoManageActivity.this, R.color.media_manage_activity_select));
                    tvVideoManageActivitySelect1.setClickable(false);
                    ivVideoManageActivityBlankPageImage.setVisibility(View.VISIBLE);
                    tvVideoManageActivityBlankPageText.setVisibility(View.VISIBLE);
                    tvVideoManageActivityBlankPageText.setText(getResources().getString(R.string.you_do_not_have_not_upload_the_video));
                    state = 1;
                    videoManageActivityAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    public void dataChanged() {
        videoManageActivityAdapter.notifyDataSetChanged();
        tvVideoManageActivityVideoCount2.setText(checkNum + " " + getResources().getString(R.string.video_manage_activity_video_count));
    }

    public void refreshMediaUpdateEvent() {
        rxSubscription = RxBus.getDefault().toObservable(VideoUpdateEvent.class).subscribe(new Action1<VideoUpdateEvent>() {
            @Override
            public void call(VideoUpdateEvent mediaUpdateEvent) {
                if (mediaUpdateEvent.isVideoUploadFailed()) {
                    bitmapList.clear();
                    dbBeanUpLoadVideoInfoList.clear();
                    dbBeanUpLoadVideoInfoList = DBBeanUpLoadVideoInfoUtils.getInstance().queryData();
                    for (int i = 0; i < dbBeanUpLoadVideoInfoList.size(); i++) {
                        Bitmap b = BitmapFactory.decodeFile(dbBeanUpLoadVideoInfoList.get(i).getBitmapPath());
                        if (b != null) {
                            bitmapList.add(b);
                        }
                    }
                    if (bitmapList.size() == 0) {
                        tvVideoManageActivitySelect1.setTextColor(ContextCompat.getColor(VideoManageActivity.this, R.color.media_manage_activity_select));
                        tvVideoManageActivitySelect1.setClickable(false);
                        ivVideoManageActivityBlankPageImage.setVisibility(View.VISIBLE);
                        tvVideoManageActivityBlankPageText.setVisibility(View.VISIBLE);
                        tvVideoManageActivityBlankPageText.setText(getResources().getString(R.string.you_do_not_have_not_upload_the_video));

                    } else {
                        ivVideoManageActivityBlankPageImage.setVisibility(View.GONE);
                        tvVideoManageActivityBlankPageText.setVisibility(View.GONE);
                        videoManageActivityAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxSubscription != null && !rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }

    public class VideoManageActivityAdapter extends RecyclerView.Adapter<VideoManageActivityAdapter.VideoManageActivityViewHolder> {


        private List<Bitmap> bitmapList;

        // 用来控制CheckBox的选中状况
        private HashMap<Integer, Boolean> isSelected;

        // 构造器
        public VideoManageActivityAdapter(List<Bitmap> bitmapList) {

            this.bitmapList = bitmapList;
            isSelected = new HashMap<Integer, Boolean>();
            // 初始化数据
            initDate();
        }

        // 初始化isSelected的数据
        private void initDate() {
            for (int i = 0; i < bitmapList.size(); i++) {
                getIsSelected().put(i, false);
            }
        }

        @Override
        public VideoManageActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(VideoManageActivity.this).inflate(R.layout.item_activity_video_manage, parent, false);
            VideoManageActivityViewHolder holder = new VideoManageActivityViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final VideoManageActivityViewHolder holder, final int position) {
            if ((uploadingInfoPositionList != null) && (uploadingInfoPositionList.size() > 0)) {
                for (int i = 0; i < uploadingInfoPositionList.size(); i++) {
                    if (dbBeanUpLoadVideoInfoList.get(position).getCreatTimeAsId() == uploadingInfoPositionList.get(i).id) {
                        uploadingStatus = true;
                    }
                }
                if (uploadingStatus) {
                    holder.ivVideoManageActivityItemPicture.setImageBitmap(bitmapList.get(position));
                    holder.llVideoManageActivityItemUpload1.setVisibility(View.GONE);
                    holder.llVideoManageActivityItemSelected2.setVisibility(View.GONE);
                    holder.tvVideoManageActivityItemUploadingText.setVisibility(View.VISIBLE);
                    holder.ivVideoManageActivityItemUploadCover.setVisibility(View.VISIBLE);
                    holder.crvVideoManageActivityItem.setClickable(false);
                    uploadingStatus = false;
                } else {
                    doOnBindViewHolder(holder, position);
                }
            } else {
                doOnBindViewHolder(holder, position);
            }
        }

        public void doOnBindViewHolder(final VideoManageActivityViewHolder holder, final int position) {
            if (state == 1) {
                holder.llVideoManageActivityItemUpload1.setVisibility(View.VISIBLE);
                holder.llVideoManageActivityItemSelected2.setVisibility(View.GONE);
                holder.ivVideoManageActivityItemPicture.setImageBitmap(bitmapList.get(position));
                holder.ivVideoManageActivityItemPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VideoManageActivity.this, ShareActivity.class);
                        intent.putExtra(ShareActivity.KEY_FILE_PATH, dbBeanUpLoadVideoInfoList.get(position).getMediaLocalPath());
                        intent.putExtra(ShareActivity.KEY_PICTURE_PATH, dbBeanUpLoadVideoInfoList.get(position).getBitmapPath());
                        startActivity(intent);
                    }
                });

                holder.cbVideoManageActivityItemUpload1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            UploadInfoState uploadInfoState = new UploadInfoState();
                            uploadInfoState.id = dbBeanUpLoadVideoInfoList.get(position).getCreatTimeAsId();
                            uploadInfoState.state = 1;
                            BaseApplication.uploadingInfoPositionList.add(uploadInfoState);
                            upLoadVideo(dbBeanUpLoadVideoInfoList.get(position));
                        }
                    }
                });
            } else if (state == 2) {
                holder.llVideoManageActivityItemUpload1.setVisibility(View.GONE);
                holder.llVideoManageActivityItemSelected2.setVisibility(View.VISIBLE);
                holder.ivVideoManageActivityItemPicture.setImageBitmap(bitmapList.get(position));
                holder.ivVideoManageActivityItemPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(VideoManageActivity.this, ShareActivity.class);
                        intent.putExtra(ShareActivity.KEY_FILE_PATH, dbBeanUpLoadVideoInfoList.get(position).getMediaLocalPath());
                        intent.putExtra(ShareActivity.KEY_PICTURE_PATH, dbBeanUpLoadVideoInfoList.get(position).getBitmapPath());
                        startActivity(intent);
                    }
                });
                holder.cbVideoManageActivityItemSelected2.setChecked(isSelected.get(position));
                holder.llVideoManageActivityItemSelected2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.cbVideoManageActivityItemSelected2.isChecked()) {
                            holder.cbVideoManageActivityItemSelected2.setChecked(false);
                            getIsSelected().put(position, false);
                            checkNum--;
                            if (checkNum > 0) {
                                ivVideoManageActivityUpLoad2.setPressed(true);
                                ivVideoManageActivityDelete2.setPressed(true);
                            } else {
                                ivVideoManageActivityUpLoad2.setPressed(false);
                                ivVideoManageActivityDelete2.setPressed(false);
                            }
                        } else {
                            holder.cbVideoManageActivityItemSelected2.setChecked(true);
                            getIsSelected().put(position, true);
                            checkNum++;
                            if (checkNum > 0) {
                                ivVideoManageActivityUpLoad2.setPressed(true);
                                ivVideoManageActivityDelete2.setPressed(true);
                            } else {
                                ivVideoManageActivityUpLoad2.setPressed(false);
                                ivVideoManageActivityDelete2.setPressed(false);
                            }
                        }
                        tvVideoManageActivityVideoCount2.setText(checkNum + " " + getResources().getString(R.string.video_manage_activity_video_count));
                    }
                });
            }
        }


        public void upLoadVideo(final DBBeanUpLoadVideoInfo dbBeanUpLoadVideoInfo) {

            final String[] filePaths = new String[2];
            String mediaPath = dbBeanUpLoadVideoInfo.getMediaLocalPath();
            filePaths[0] = dbBeanUpLoadVideoInfo.getBitmapPath();
            filePaths[1] = mediaPath;
            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> files, List<String> urls) {
                    if (urls.size() == filePaths.length) {//如果数量相等，则代表文件全部上传完成
                        HttpBeanMediaDetail httpBeanMediaDetail = new HttpBeanMediaDetail();
                        httpBeanMediaDetail.setCreatTimeAsId(System.currentTimeMillis());
                        httpBeanMediaDetail.setThumbnailUrl(urls.get(0));
                        httpBeanMediaDetail.setMediaUrl(urls.get(1));
                        httpBeanMediaDetail.setLocationDesc(dbBeanUpLoadVideoInfo.getLocationDesc());
                        httpBeanMediaDetail.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId, BmobException e) {
                                if (e == null) {
                                    ToastHelper.showLongMessage("视频上传成功");
                                    RxBus.getDefault().post(new VideoUpdateEvent(VideoUpdateEvent.TYPE_VIDEO_UPLOAD_SUCCESS));
                                    DBBeanUpLoadVideoInfoUtils.getInstance().deleteOneDataByKey(dbBeanUpLoadVideoInfo.getCreatTimeAsId());
                                    CancelUploadingState(dbBeanUpLoadVideoInfo);
                                } else {
                                    ToastHelper.showLongMessage("视频上传失败");
                                    CancelUploadingState(dbBeanUpLoadVideoInfo);
                                }
                            }
                        });

                    }
                }

                @Override
                public void onError(int statuscode, String errormsg) {
                    ToastHelper.showLongMessage("视频上传失败");
                    CancelUploadingState(dbBeanUpLoadVideoInfo);
                }

                @Override
                public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                    //flikerBar.setProgress(totalPercent);
                }
            });
        }

        public void CancelUploadingState(DBBeanUpLoadVideoInfo dbBeanUpLoadVideoInfo){
            for (int i=0;i<BaseApplication.uploadingInfoPositionList.size();i++){
                if (dbBeanUpLoadVideoInfo.getCreatTimeAsId() == BaseApplication.uploadingInfoPositionList.get(i).id){
                    UploadInfoState uploadInfoState = BaseApplication.uploadingInfoPositionList.get(i);
                    BaseApplication.uploadingInfoPositionList.remove(uploadInfoState);
                }
            }
        }
        @Override
        public int getItemCount() {
            return bitmapList.size();
        }

        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelected;
        }

        class VideoManageActivityViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.ivVideoManageActivityItemPicture) ImageView ivVideoManageActivityItemPicture;
            @BindView(R.id.ivVideoManageActivityItemUploadCover) ImageView ivVideoManageActivityItemUploadCover;
            @BindView(R.id.cbVideoManageActivityItemUpload1) CheckBox cbVideoManageActivityItemUpload1;
            @BindView(R.id.llVideoManageActivityItemUpload1) LinearLayout llVideoManageActivityItemUpload1;
            @BindView(R.id.cbVideoManageActivityItemSelected2) CheckBox cbVideoManageActivityItemSelected2;
            @BindView(R.id.llVideoManageActivityItemSelected2) LinearLayout llVideoManageActivityItemSelected2;
            @BindView(R.id.tvVideoManageActivityItemUploadingText) TextView tvVideoManageActivityItemUploadingText;
            @BindView(R.id.crvVideoManageActivityItem) CardView crvVideoManageActivityItem;

            public VideoManageActivityViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
