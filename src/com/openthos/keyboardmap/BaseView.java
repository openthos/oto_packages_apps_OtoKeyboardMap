package com.openthos.keyboardmap;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseView extends View {
    private Paint paint;
    private final List<Integer> mFunctionKeys = new ArrayList<>();
    private int mPressedDirectionKeyCount;
    private Instrumentation mInstrumentation = new Instrumentation();
    private int mCircleCenterX, mCircleCenterY, mCurrentDirectionX,
            mCurrentDirectionY, mDistanceFromCircleToKey;
    public List<Integer> mDirectionKeys = new ArrayList<>();
    private boolean is = false;
    private List<Integer> mTextViewTag;

    public BaseView(Context context) {
        super(context);
        paint = new Paint();
        mFunctionKeys.clear();
        mDirectionKeys.clear();
        for (TextView dragView : ViewManager.mDragViewList) {
            mTextViewTag = (List<Integer>) dragView.getTag();
            mFunctionKeys.add(mTextViewTag.get(0));
        }
        mDirectionKeys = Arrays.asList(ViewManager.mDirectionKeyArr);
        if (mDirectionKeys.size() > 0) {
            mCircleCenterX = mDirectionKeys.get(4);
            mCircleCenterY = mDirectionKeys.get(5);
        }
    }

    public void processDirectionKeyMapping(final Instrumentation in, final int eventType,
                                           final float x, final float y, final boolean needDown,
                                           final float downX, final float downY) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                long time = SystemClock.uptimeMillis();
                if (needDown) {
                    in.sendPointerSync(MotionEvent.obtain(time, time,
                            MotionEvent.ACTION_DOWN, downX, downY, 0));
                    in.sendPointerSync(MotionEvent.obtain(time, time, eventType, x, y, 0));
                } else {
                    in.sendPointerSync(MotionEvent.obtain(time, time, eventType, x, y, 0));
                }
            }
        }.start();
    }

    public void processFunctionKeyMapping(final Instrumentation in, final int eventType,
                                          final float x, final float y, final boolean needMove,
                                          final float downX, final float downY, final float moveX,
                                          final float moveY) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                long time = SystemClock.uptimeMillis();
                if (needMove) {
                    in.sendPointerSync(MotionEvent.obtain(time, time, eventType, x, y, 0));
                    in.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN,
                                                          downX, downY, 0));
                    in.sendPointerSync(MotionEvent.obtain(time, time, MotionEvent.ACTION_MOVE,
                                                          moveX, moveY, 0));
                } else {
                    in.sendPointerSync(MotionEvent.obtain(time, time, eventType, x, y, 0));
                }
            }
        }.start();
    }

    private float mFx = 0.0f;
    private float mFy = 0.0f;

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (mCircleCenterX == -1 && mCircleCenterY == -1)
            return super.dispatchGenericMotionEvent(event);
        double fx = ((int) (event.getX() * 10)) / 10.0;
        double fy = ((int) (event.getY() * 10)) / 10.0;
        if (fx != 0 && fy != 0) {
            mFx = (float) (mCircleCenterX + fx*100);
            mFy = (float) (mCircleCenterY + fy*100);
            processDirectionKeyMapping(mInstrumentation, MotionEvent.ACTION_MOVE,
                    mFx, mFy, !is, mCircleCenterX, mCircleCenterY);
            if (!is)
                is = true;
        }
        if (is &&fx == 0 && fy == 0 ){
            is = false;
            mFx = mFy = 0.0f;
            processDirectionKeyMapping(mInstrumentation, MotionEvent.ACTION_UP,
                                       (float)( mCircleCenterX+ fx*100),
                                       (float)(mCircleCenterY+ fy*100) ,
                                       false, mCircleCenterX, mCircleCenterY);
        }

        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event) {
        final int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_F12:
                KeymapService.mHandler.sendEmptyMessage(0);
                return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0) {
            if (mDirectionKeys.contains(keyCode)) {
                if ((mCurrentDirectionX < mCircleCenterX - mDistanceFromCircleToKey)
                        || (mCurrentDirectionX > mCircleCenterX + mDistanceFromCircleToKey)
                        || (mCurrentDirectionY < mCircleCenterY - mDistanceFromCircleToKey)
                        || (mCurrentDirectionY > mCircleCenterY + mDistanceFromCircleToKey)) {
                    mCurrentDirectionX = 0;
                    mCurrentDirectionY = 0;
                    mPressedDirectionKeyCount = 0;
                }

                mPressedDirectionKeyCount++;
                if (mPressedDirectionKeyCount == 1) {
                    // todo down circle center
                    mDistanceFromCircleToKey = mDirectionKeys.get(6);
                    mCurrentDirectionX = mCircleCenterX;
                    mCurrentDirectionY = mCircleCenterY;
                }
                // todo move direction
                if (event.getKeyCode() == mDirectionKeys.get(0)) {
                    // direction left
                    mCurrentDirectionX = mCurrentDirectionX - mDistanceFromCircleToKey;
                } else if (event.getKeyCode() == mDirectionKeys.get(1)) {
                    // direction up
                    mCurrentDirectionY = mCurrentDirectionY - mDistanceFromCircleToKey;

                } else if (event.getKeyCode() == mDirectionKeys.get(2)) {
                    // direction right
                    mCurrentDirectionX = mCurrentDirectionX + mDistanceFromCircleToKey;

                } else if (event.getKeyCode() == mDirectionKeys.get(3)) {
                    // direction down
                    mCurrentDirectionY = mCurrentDirectionY + mDistanceFromCircleToKey;
                }
                if (mPressedDirectionKeyCount == 1) {
                    processDirectionKeyMapping(mInstrumentation, MotionEvent.ACTION_MOVE,
                            mCurrentDirectionX, mCurrentDirectionY, true,
                            mCircleCenterX, mCircleCenterY);
                } else {
                    processDirectionKeyMapping(mInstrumentation, MotionEvent.ACTION_MOVE,
                            mCurrentDirectionX, mCurrentDirectionY, false, 0, 0);
                }
            } else if (mFunctionKeys.contains(event.getKeyCode())) {
                // todo down functionkey
                int index = mFunctionKeys.indexOf(keyCode);
                mTextViewTag = (List<Integer>) ViewManager.mDragViewList.get(index).getTag();
                if (is == true) {
                    processFunctionKeyMapping(mInstrumentation, MotionEvent.ACTION_DOWN,
                            mTextViewTag.get(1) + 45, mTextViewTag.get(2) + 45,
                            true, mCircleCenterX, mCircleCenterY, mFx, mFy);
                } else if (mPressedDirectionKeyCount == 0) {
                    processFunctionKeyMapping(mInstrumentation, MotionEvent.ACTION_DOWN,
                            mTextViewTag.get(1) + 45, mTextViewTag.get(2) + 45, false, 0, 0, 0, 0);
                } else {
                    processFunctionKeyMapping(mInstrumentation, MotionEvent.ACTION_DOWN,
                            mTextViewTag.get(1) + 45, mTextViewTag.get(2) + 45,
                            true, mCircleCenterX, mCircleCenterY,
                            mCurrentDirectionX, mCurrentDirectionY);
                }
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            if (mDirectionKeys.contains(event.getKeyCode())) {
                mPressedDirectionKeyCount--;
                if ((mCurrentDirectionX < mCircleCenterX - mDistanceFromCircleToKey)
                        || (mCurrentDirectionX > mCircleCenterX + mDistanceFromCircleToKey)
                        || (mCurrentDirectionY < mCircleCenterY - mDistanceFromCircleToKey)
                        || (mCurrentDirectionY > mCircleCenterY + mDistanceFromCircleToKey)) {
                    mCurrentDirectionX = 0;
                    mCurrentDirectionY = 0;
                    mPressedDirectionKeyCount = 0;
                }
                // todo move direction
                if (event.getKeyCode() == mDirectionKeys.get(0)) {
                    // direction left
                    mCurrentDirectionX = mCurrentDirectionX + mDistanceFromCircleToKey;
                } else if (event.getKeyCode() == mDirectionKeys.get(1)) {
                    // direction up
                    mCurrentDirectionY = mCurrentDirectionY + mDistanceFromCircleToKey;

                } else if (event.getKeyCode() == mDirectionKeys.get(2)) {
                    // direction right
                    mCurrentDirectionX = mCurrentDirectionX - mDistanceFromCircleToKey;

                } else if (event.getKeyCode() == mDirectionKeys.get(3)) {
                    // direction down
                    mCurrentDirectionY = mCurrentDirectionY - mDistanceFromCircleToKey;
                }

                if (mPressedDirectionKeyCount == 0) {
                    processDirectionKeyMapping(mInstrumentation, MotionEvent.ACTION_UP,
                            mCurrentDirectionX, mCurrentDirectionY, false, 0, 0);
                } else {
                    processDirectionKeyMapping(mInstrumentation, MotionEvent.ACTION_MOVE,
                            mCurrentDirectionX, mCurrentDirectionY, false, 0, 0);
                }
            } else if (mFunctionKeys.contains(event.getKeyCode())) {
                // todo up functionkey
                int index = mFunctionKeys.indexOf(keyCode);
                mTextViewTag = (List<Integer>) ViewManager.mDragViewList.get(index).getTag();
                if (is == true) {
                    processFunctionKeyMapping(mInstrumentation, MotionEvent.ACTION_UP,
                            mTextViewTag.get(1) + 45, mTextViewTag.get(2) + 45,
                            true, mCircleCenterX, mCircleCenterY, mFx, mFy);
                } else if (mPressedDirectionKeyCount == 0) {
                    processFunctionKeyMapping(mInstrumentation, MotionEvent.ACTION_UP,
                            mTextViewTag.get(1) + 45, mTextViewTag.get(2) + 45, false, 0, 0, 0, 0);
                } else {
                    processFunctionKeyMapping(mInstrumentation, MotionEvent.ACTION_UP,
                            mTextViewTag.get(1) + 45, mTextViewTag.get(2) + 45,
                            true, mCircleCenterX, mCircleCenterY,
                            mCurrentDirectionX, mCurrentDirectionY);
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(KeymapService.screenWidth, KeymapService.screenHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0x00ffffff);
        canvas.drawRect(0, 0, KeymapService.screenWidth, KeymapService.screenHeight, paint);
    }
}
