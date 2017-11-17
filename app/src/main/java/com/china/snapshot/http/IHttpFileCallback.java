package com.china.snapshot.http;

/**
 * Created by Chuanlong on 2016/12/15.
 */

public interface IHttpFileCallback {

    public void onSuccess(String path);

    public void onFailure(int errorCode, String errorMessage);

    public void onProgress(int progress, long current, long total);

}
