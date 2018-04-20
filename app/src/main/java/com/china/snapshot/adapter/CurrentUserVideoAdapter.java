package com.china.snapshot.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.china.snapshot.R;
import com.china.snapshot.activity.CurrentUserVideoActivity;
import com.china.snapshot.bean.HttpBeanMediaDetail;
import com.china.snapshot.util.DBHttpBeanMediaDetailUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class CurrentUserVideoAdapter extends RecyclerView.Adapter<CurrentUserVideoAdapter.CurrentUserVideoAdapterViewHolder> {

    private List<HttpBeanMediaDetail> httpBeanMediaDetails = new ArrayList<>();
    private CurrentUserVideoActivity mActivity;

    public CurrentUserVideoAdapter(List<HttpBeanMediaDetail> httpBeanMediaDetails, CurrentUserVideoActivity mActivity) {
        this.httpBeanMediaDetails = httpBeanMediaDetails;
        this.mActivity = mActivity;
    }

    @Override
    public CurrentUserVideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_current_user_activity, parent, false);
        CurrentUserVideoAdapterViewHolder holder = new CurrentUserVideoAdapterViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CurrentUserVideoAdapterViewHolder holder, final int position) {
        if (!TextUtils.isEmpty(httpBeanMediaDetails.get(position).getThumbnailUrl())) {
            Glide.with(mActivity).load(httpBeanMediaDetails.get(position).getThumbnailUrl()).placeholder(R.drawable.item_video_moments_pic_2).centerCrop().into(holder.ivVideoItemMainActivity);
            holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HttpBeanMediaDetail gameScore = new HttpBeanMediaDetail();
                    gameScore.setObjectId(httpBeanMediaDetails.get(position).getObjectId());
                    gameScore.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("bmob", "成功");
                                DBHttpBeanMediaDetailUtils.getInstance().deleteOneData(httpBeanMediaDetails.get(position));
                                EventBus.getDefault().post(httpBeanMediaDetails.get(position));
                                httpBeanMediaDetails.remove(position);
                                notifyDataSetChanged();
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return httpBeanMediaDetails.size();
    }

    public class CurrentUserVideoAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivVideoItemCurrentVideoActivity) ImageView ivVideoItemMainActivity;
        @BindView(R.id.btn_delete_video_current_user_activity) Button mDeleteButton;

        public CurrentUserVideoAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

