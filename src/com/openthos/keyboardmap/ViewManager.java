package com.openthos.keyboardmap;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asd on 1/1/2017.
 */

public class ViewManager {
    BaseView mBaseView;
    ViewGroup mControlView;
    WindowManager windowManager;
    public static ViewManager manager;
    Context context;
    private WindowManager.LayoutParams mBaseViewParams;
    private WindowManager.LayoutParams mControlViewParams;
    public static List<ControlView.DragView> mDragViewList = new ArrayList<>();
    public static Integer[] mDirectionKeyArr = new Integer[]{-1, -1, -1, -1, -1, -1, -1};

    private ViewManager(Context context) {
        this.context = context;
    }

    public static ViewManager getInstance(Context context) {
        if (manager == null) {
            manager = new ViewManager(context);
        }
        return manager;
    }

    public void showBase() {
        hideControl();
        mBaseView = new BaseView(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (mBaseViewParams == null) {
            mBaseViewParams = new WindowManager.LayoutParams();
            mBaseViewParams.width = MainActivity.screenWidth;
            mBaseViewParams.height = MainActivity.screenHeight;
            mBaseViewParams.gravity = Gravity.TOP | Gravity.LEFT;
            mBaseViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mBaseViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            mBaseViewParams.format = PixelFormat.RGBA_8888;
        }
        windowManager.addView(mBaseView, mBaseViewParams);
    }

    void hideView() {
        if (mBaseView != null) {
            windowManager.removeView(mBaseView);
            mBaseView = null;
        }

    }

    void hideControl() {
        if (mControlView != null) {
            windowManager.removeView(mControlView);
            mControlView = null;
        }
    }

    public void showControl() {
        hideView();
        mControlView  = new ControlView(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (mControlViewParams == null) {
            mControlViewParams = new WindowManager.LayoutParams();
            mControlViewParams.width = MainActivity.screenWidth;
            mControlViewParams.height = MainActivity.screenHeight;
            mControlViewParams.gravity = Gravity.TOP | Gravity.LEFT;
            mControlViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            mControlViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            mControlViewParams.format = PixelFormat.RGBA_8888;
        }
        windowManager.addView(mControlView, mControlViewParams);
    }
}
