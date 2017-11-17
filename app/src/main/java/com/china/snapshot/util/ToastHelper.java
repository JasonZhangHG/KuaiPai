package com.china.snapshot.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastHelper {
    private static Context mContext;

    private static Toast mToast;

    private static Handler mHandler;

    private static String oldMsg;
    private static long time;

    private static final long INTERVAL_TIME = 2000;

    public static void init(Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static void showShortMessage(int text) {
        if (mContext.getString(text).equals(oldMsg) && System.currentTimeMillis() - time < INTERVAL_TIME) {
            return;
        }

        time = System.currentTimeMillis();
        oldMsg = mContext.getString(text);

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void showLongMessage(int text) {
        if (mContext.getString(text).equals(oldMsg) && System.currentTimeMillis() - time < INTERVAL_TIME) {
            return;
        }

        time = System.currentTimeMillis();
        oldMsg = mContext.getString(text);

        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        mToast.show();
    }

    public static void showShortMessage(String text) {
        if (text == null) {
            return;
        }

        if (text.equals(oldMsg) && System.currentTimeMillis() - time < INTERVAL_TIME) {
            return;
        }

        time = System.currentTimeMillis();
        oldMsg = text;


        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);

        mToast.show();
    }

    public static void showLongMessage(String text) {
        if (text == null) {
            return;
        }

        if (text.equals(oldMsg) && System.currentTimeMillis() - time < INTERVAL_TIME) {
            return;
        }

        time = System.currentTimeMillis();
        oldMsg = text;

        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);

        mToast.show();
    }

    public static void showShortMsgOnNoneUI(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showShortMessage(text);
            }
        });
    }

    public static void showShortMsgOnNoneUI(final int text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showShortMessage(text);
            }
        });
    }

    public static void showLongMsgOnNoneUI(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showLongMessage(text);
            }
        });
    }

    public static void showLongMsgOnNoneUI(final int text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showLongMessage(text);
            }
        });
    }
}
