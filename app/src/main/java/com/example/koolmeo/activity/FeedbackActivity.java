package com.example.koolmeo.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.koolmeo.R;
import com.example.koolmeo.base.BaseBarActivity;
import com.example.koolmeo.bean.FeedBackBean;
import com.example.koolmeo.util.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseBarActivity {

    @BindView(R.id.edtFeedbackActivityFeedback) EditText edtFeedbackActivityFeedback;
    @BindView(R.id.edtFeedbackActivityEmail) EditText edtFeedbackActivityEmail;
    @BindView(R.id.llFeedbackActivityCommit) LinearLayout llFeedbackActivityCommit;
    @BindView(R.id.cbFeedbackActivitySendBlogIcon) CheckBox cbFeedbackActivitySendBlogIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        setTitle(R.string.feedback_activity_title);

        setCheckBoxClick();
    }

    //处理是否同意发送日志
    public void setCheckBoxClick() {
        cbFeedbackActivitySendBlogIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Toast.makeText(FeedbackActivity.this, "Agree Send Blog", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FeedbackActivity.this, "Disagree Send Blog", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick({R.id.llFeedbackActivityCommit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llFeedbackActivityCommit:
                llFeedbackActivityCommit.setClickable(false);
                //打开抽屉
                String feedbackContent = edtFeedbackActivityFeedback.getText().toString();
                String email = edtFeedbackActivityEmail.getText().toString();
                //将用户填写的反馈意见和用户邮箱上传到服务器
                if (TextUtils.isEmpty(feedbackContent) && TextUtils.isEmpty(email)) {
                    Toast.makeText(FeedbackActivity.this, "Please provide feedback", Toast.LENGTH_SHORT).show();
                } else {
                    FeedBackBean feedBackBean = new FeedBackBean(System.currentTimeMillis(), feedbackContent, email);
                    feedBackBean.save(new SaveListener<String>() {

                        @Override
                        public void done(String objectId, BmobException e) {
                            if (e == null) {
                                llFeedbackActivityCommit.setClickable(true);
                                ToastHelper.showLongMessage("很高兴收到您的反馈，我们会尽快处理");
                                finish();
                            } else {
                                llFeedbackActivityCommit.setClickable(true);
                                ToastHelper.showLongMessage("很遗憾 反馈失败 可重新提交反馈");
                            }
                        }
                    });
                }
                break;
        }
    }
}

