package com.china.snapshot.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by ZhangHaiLong on 2017/9/13.
 */
//VideiView全屏
public class CustomVideoView extends VideoView {

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
// TODO Auto-generated constructor stub
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
    }

    public CustomVideoView(Context context) {
        super(context);
// TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//这里重写onMeasure的方法
// TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(0, widthMeasureSpec);//得到默认的大小（0，宽度测量规范）
        int height = getDefaultSize(0, heightMeasureSpec);//得到默认的大小（0，高度度测量规范）
        setMeasuredDimension(width, height); //设置测量尺寸,将高和宽放进去
    }
}

