package com.china.snapshot.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.china.snapshot.R;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.DBUserBean;
import com.china.snapshot.util.ToastHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.tvRegisterActivityTitle)
    TextView tvRegisterActivityTitle;
    @BindView(R.id.edtRegisterActivityUserName)
    EditText edtRegisterActivityUserName;
    @BindView(R.id.edtRegisterActivityPassWord1)
    EditText edtRegisterActivityPassWord1;
    @BindView(R.id.edtRegisterActivityPassWord2)
    EditText edtRegisterActivityPassWord2;
    @BindView(R.id.btnRegisterActivitySubmit)
    Button btnRegisterActivitySubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "32896c5e940df9a96201ab7ea3bfffe7");
        setContentView(R.layout.activity_student_regiest);
        ButterKnife.bind(this);
        btnRegisterActivitySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRegisterClick();
            }
        });
    }

    public void setRegisterClick() {
        String userName = edtRegisterActivityUserName.getText().toString();
        String passWord1 = edtRegisterActivityPassWord1.getText().toString();
        String passWord2 = edtRegisterActivityPassWord2.getText().toString();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord1) || TextUtils.isEmpty(passWord2)) {
            ToastHelper.showShortMessage("请输入完整信息后再注册");
        } else {
            if (passWord1.equals(passWord2)) {
                final DBUserBean dbUserInfoBean = new DBUserBean();
                dbUserInfoBean.setCreatTimeAsId(getTime());
                dbUserInfoBean.setUsername(userName);
                dbUserInfoBean.setPassword(passWord1);
                dbUserInfoBean.signUp(new SaveListener<DBUserBean>() {
                    @Override
                    public void done(DBUserBean dbUserBean, BmobException e) {
                        if (e == null) {
                            ToastHelper.showShortMessage("恭喜您，注册成功");
                            RegisterActivity.this.finish();
                        } else {
                            ToastHelper.showShortMessage("注册失败 : " + e);
                        }
                    }
                });

            } else {
                ToastHelper.showShortMessage("两次密码输入不一致 ");
            }
        }
    }

    public long getTime() {
        return System.currentTimeMillis();//获取系统时间戳
    }

}
