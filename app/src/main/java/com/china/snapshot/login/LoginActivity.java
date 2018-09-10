package com.china.snapshot.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.china.snapshot.R;
import com.china.snapshot.activity.MainActivity;
import com.china.snapshot.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.editLoginActivityUsrName)
    EditText editLoginActivityUsrName;
    @BindView(R.id.editLoginActivityPassWord)
    EditText editLoginActivityPassWord;
    @BindView(R.id.btnLoginActivityLogin)
    Button btnLoginActivityLogin;
    @BindView(R.id.tvLoginActivityRegister)
    TextView tvLoginActivityRegister;
    @BindView(R.id.llLoginActivityAll)
    LinearLayout llLoginActivityAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Bmob.initialize(this, "32896c5e940df9a96201ab7ea3bfffe7");
        getGetWritePermission();
        btnLoginActivityLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginOnclick();
            }
        });

        tvLoginActivityRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        editLoginActivityUsrName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.d("LoginActivity editLoginActivityUsrName  hasFocus = "+hasFocus);
                if (hasFocus) {
                    editLoginActivityUsrName.setCursorVisible(true);
                }

            }
        });

        editLoginActivityPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.d("LoginActivity editLoginActivityPassWord  hasFocus = "+hasFocus);
                if (hasFocus) {
                    editLoginActivityPassWord.setCursorVisible(true);
                }
            }
        });
    }

    public void getGetWritePermission() {

        PermissionUtils.permission(PermissionConstants.STORAGE)
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(final ShouldRequest shouldRequest) {
                        shouldRequest.again(true);
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {

                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever,
                                         List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtils.launchAppDetailsSettings();
                        }
                    }
                }).request();
    }

    public void setLoginOnclick() {
        String username = editLoginActivityUsrName.getText().toString();
        String password = editLoginActivityPassWord.getText().toString();
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            BmobUser userInfoBean = new BmobUser();
            userInfoBean.setUsername(username);
            userInfoBean.setPassword(password);
            userInfoBean.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser userInfoBean, BmobException e) {
                    if (e == null) {
                        BmobUser currentUser = BmobUser.getCurrentUser(BmobUser.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败 : " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
