package com.china.snapshot.camera.capture;
/*
 * AudioVideoRecordingSample
 * Sample project to cature audio and video from internal mic/camera and save as MPEG4 file.
 *
 * Copyright (c) 2014-2015 saki t_saki@serenegiant.com
 *
 * File name: FilterCameraActivity.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * All files in the folder are under this Apache License, Version 2.0.
*/

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.china.snapshot.R;
import com.china.snapshot.camera.glutils.CameraFile;

public class FilterCameraActivity extends Activity {

    //处理申请权限的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                int gsize=grantResults.length;
                int grantResult = grantResults[0];
                int flag=0;
                for(int i=0;i<gsize;i++)
                {
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                        flag=1;
                    }
                }
                if (flag==0) {
                    startjump();
                } else {
                    Toast.makeText(FilterCameraActivity.this, "you refused the camera function", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
        }
    }

    public void callCamera() {
        if(Build.VERSION.SDK_INT>21){
            String callPhone = Manifest.permission.CAMERA;
            String writestorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            String readstorage = Manifest.permission.READ_EXTERNAL_STORAGE;
            String readsound = Manifest.permission.RECORD_AUDIO;
            String[] permissions = new String[]{callPhone,writestorage,readstorage,readsound};
            int selfPermission = ActivityCompat.checkSelfPermission(this, callPhone);
            int selfwrite = ActivityCompat.checkSelfPermission(this, writestorage);
            int selfread = ActivityCompat.checkSelfPermission(this, readstorage);
            int selfsound = ActivityCompat.checkSelfPermission(this, readsound);
            if (selfPermission != PackageManager.PERMISSION_GRANTED || selfwrite != PackageManager.PERMISSION_GRANTED || selfread != PackageManager.PERMISSION_GRANTED || selfsound!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 1);
            } else {
                startjump();
            }
        }else
        {
            startjump();
        }

    }

    public void startjump() {
        getFragmentManager().beginTransaction().add(R.id.container, new CameraFragment()).commit();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_main);
        CameraFile.cxt=this.getApplicationContext();
        CameraFile.filterindex=0;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("delcamera");

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (savedInstanceState == null) {
            callCamera();
        }
        registerReceiver(mRefreshBroadcastReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    AlertDialog ad;
    public void showDialog(Context context) {
        View view=this.getLayoutInflater().inflate(R.layout.camera_droplayout,null);
        view.findViewById(R.id.camera_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ad!=null)
                    ad.dismiss();
                FilterCameraActivity.this.finish();
            }
        });
        view.findViewById(R.id.camera_cancal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ad!=null)
                    ad.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        ad=builder.show();
    }

    private BroadcastReceiver mRefreshBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("delcamera")) {
                FilterCameraActivity.this.finish();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        CameraFile.filterindex=0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraFile.delFileVideo();
        unregisterReceiver(mRefreshBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        showDialog(this);
    }
}
