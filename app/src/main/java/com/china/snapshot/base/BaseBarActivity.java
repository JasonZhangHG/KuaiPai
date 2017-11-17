package com.china.snapshot.base;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.china.snapshot.R;

public class BaseBarActivity extends BaseActivity implements View.OnClickListener{

    protected Toolbar titleBar;
    private TextView tvTitle;
    private TextView tvAction;
    private ImageView ivNavigation;
    private View baseLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_bar);
        titleBar = findView(R.id.barTitle);
        ivNavigation = (ImageView) titleBar.findViewById(R.id.ivNavigation);
        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationIconClick(v);
            }
        });
        tvTitle = (TextView) titleBar.findViewById(R.id.tvTitle);
        tvAction = (TextView) titleBar.findViewById(R.id.tvAction);
        baseLine = findView(R.id.baseLine);
        setSupportActionBar(titleBar);
        tvAction.setVisibility(View.GONE);
        tvAction.setOnClickListener(this);
    }

    /**
     * 设置titlebar下面直线的颜色
     *
     * @param id
     */

    public void setBaseLineColor(int id) {
        baseLine.setBackgroundColor(id);
    }

    /**
     * 设置是否显示titlebar下面的直线
     *
     * @param isHide
     */

    public void hideBaseLine(boolean isHide) {
        baseLine.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    public void setBackground(int id) {
        findView(R.id.rlBase).setBackgroundResource(id);
    }

    public void setBackgroundColor(int color) {
        findView(R.id.rlBase).setBackgroundColor(color);
    }

    public void setTitleBarBackground(int id) {
        titleBar.setBackgroundResource(id);
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewStub vs = findView(R.id.baseContent);
        vs.setLayoutResource(layoutResID);
        vs.inflate();
    }

    @Override
    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        tvTitle.setText(titleId);
    }

    /**
     * 设置title字体的颜色
     *
     * @param color
     */

    public void setTitleTextColor(int color) {
        tvTitle.setTextColor(color);
    }

    /**
     * 隐藏titleBar
     *
     * @param isHide
     */
    public void hideTitleBar(boolean isHide) {
        titleBar.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置返回按钮（title上左边的按钮）
     *
     * @param resID
     */
    public void setNavigationIcon(int resID) {
        ivNavigation.setImageResource(resID);
    }

    /**
     * 隐藏或显示返回按钮
     *
     * @param isHide
     */
    public void hideNavigationIcon(boolean isHide) {
        ivNavigation.setVisibility(isHide ? View.GONE : View.VISIBLE);
    }

    /**
     * 返回按钮（title上左边的按钮）点击事件的处理
     *
     * @param v
     */
    public void onNavigationIconClick(View v) {
        finish();
    }

    public void setActionText(CharSequence title) {
        tvAction.setVisibility(View.VISIBLE);
        tvAction.setText(title);
    }

    public void setActionText(int titleId) {
        tvAction.setVisibility(View.VISIBLE);
        tvAction.setText(titleId);
    }

    public void setActionTextColor(int color) {
        tvAction.setVisibility(View.VISIBLE);
        tvAction.setTextColor(color);
    }

    public void setActionEnabled(boolean isEnabled) {
        tvAction.setEnabled(isEnabled);
    }

    public void hideActionText() {
        tvAction.setVisibility(View.GONE);
    }

    public void setBaseLineTitleBarColor(int color) {
        titleBar.setBackgroundColor(color);
        setBaseLineColor(color);
    }

    @Override
    public void onClick(View v) {
    }
}
