package com.china.snapshot.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.china.snapshot.R;
import com.china.snapshot.base.BaseActivity;
import com.china.snapshot.bean.DBUserBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class UserInfoActivity extends BaseActivity {

    @BindView(R.id.tvRegisterActivityTitle) TextView tvRegisterActivityTitle;
    @BindView(R.id.edt_user_name_user_activty) EditText edtUserNameUserActivty;
    @BindView(R.id.edt_old_user_activity) EditText edtOldUserActivity;
    @BindView(R.id.edt_tell_user_activity) EditText edtTellUserActivity;
    @BindView(R.id.edt_mail_user_activity) EditText edtMailUserActivity;
    @BindView(R.id.btn_submit_user_activity) Button btnSubmitUserActivity;
    private DBUserBean dbUserBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        dbUserBean = BmobUser.getCurrentUser(DBUserBean.class);
        if (dbUserBean != null) {
            edtUserNameUserActivty.setText(dbUserBean.getName());
            edtOldUserActivity.setText(dbUserBean.getOld());
            edtTellUserActivity.setText(dbUserBean.getTellPhone());
            edtMailUserActivity.setText(dbUserBean.getMail());
        }
    }

    @OnClick(R.id.btn_submit_user_activity)
    public void submitClick(){

        dbUserBean.setName(edtUserNameUserActivty.getText().toString());
        dbUserBean.setOld(edtOldUserActivity.getText().toString());
        dbUserBean.setMail(edtMailUserActivity.getText().toString());
        dbUserBean.setTellPhone(edtTellUserActivity.getText().toString());
        dbUserBean.update(dbUserBean.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(UserInfoActivity.this,"更新用户信息成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(UserInfoActivity.this,"更新用户信息失败",Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}
