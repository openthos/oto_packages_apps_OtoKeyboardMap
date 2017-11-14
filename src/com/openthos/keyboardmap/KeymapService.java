package com.openthos.keyboardmap;

import android.app.Activity;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import java.util.HashMap;

public class KeymapService extends Service {

    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static Handler mHandler;
    public static HashMap<Integer, String> mKeyMap = new HashMap();

    @Override
    public void onCreate() {
        initKey();
        WallpaperManager wm = WallpaperManager.getInstance(this);
        screenWidth = wm.getDesiredMinimumWidth();
        screenHeight = wm.getDesiredMinimumHeight();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        ViewManager.getInstance(KeymapService.this).showControl();
                        break;
                    case 1:
                        ViewManager.getInstance(KeymapService.this).showBase();
                        break;
                    case 2:
                        ViewManager.getInstance(KeymapService.this).exit();

                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        ViewManager.getInstance(KeymapService.this).showControl();
        return super.onStartCommand(intent, flags, startId);
    }


    private void initKey() {
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_X, "X");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_Y, "Y");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_A, "A");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_B, "B");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_L1, "L1");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_L2, "L2");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_SELECT, "select");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_START, "start");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_THUMBL, "thumb-left");
        mKeyMap.put(KeyEvent.KEYCODE_BUTTON_THUMBR, "thumb-right");

        mKeyMap.put(KeyEvent.KEYCODE_DPAD_LEFT, getString(R.string.left));
        mKeyMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, getString(R.string.right));
        mKeyMap.put(KeyEvent.KEYCODE_DPAD_UP, getString(R.string.up));
        mKeyMap.put(KeyEvent.KEYCODE_DPAD_DOWN, getString(R.string.down));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
