package com.openthos.keyboardmap;

import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends Activity {

    private Button btn_openSetting, btn_openView;
    private TextView mInfo;
    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static Handler mHandler;
    public static HashMap<Integer, String> mKeyMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initKey();


        WallpaperManager wm = WallpaperManager.getInstance(this);
        screenWidth = wm.getDesiredMinimumWidth();
        screenHeight = wm.getDesiredMinimumHeight();

        btn_openSetting = (Button) findViewById(R.id.btn_openSetting);
        btn_openView = (Button) findViewById(R.id.btn_openFloatingBall);
        mInfo = (TextView) findViewById(R.id.info);

        btn_openSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });

        btn_openView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewManager.getInstance(MainActivity.this).showBase();
                v.setVisibility(View.GONE);
                mInfo.setText(MainActivity.this.getString(R.string.info));
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        ViewManager.getInstance(MainActivity.this).showControl();
                        break;
                    case 1:
                        ViewManager.getInstance(MainActivity.this).showBase();
                }
            }
        };


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
}
