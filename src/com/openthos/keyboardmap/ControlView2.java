package com.openthos.keyboardmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ControlView2 extends FrameLayout implements View.OnClickListener {
    private Paint mPaint;
    private Context mContext;
    private ViewGroup mViewGroup;
    private Button mAddButton, mAddTrend, mAddTrendByButton, mSave;
    private Bitmap mBitmapW;
    private DragView mCurrentView;

    private Paint paint;
    private RelativeLayout mRlDirectionKey;
    private RelativeLayout.LayoutParams mRlDirectionParams;
    private int mCircleCenterX;
    private int mCircleCenterY;
    public int mDistanceFromCircleToKey;
    private int mBigCircleRadius;
    boolean mIsDrag;
    boolean mCanResize;
    private int[] mLocations = new int[2];
    ;
    private boolean mIsDirectionKey, mIsFunctionKey;
    private TextView currentTextView;


    private View mTrendView, mTrendByButtonView;
    private TextView mTvLeft, mTvUp, mTvRight, mTvDown;

    private double mDistance;
    private int mMinRadius, mMaxRadius;
    private int mCircleThick = 10;

    public ControlView2(Context context) {
        super(context);
        mPaint = new Paint();
        mContext = context;
        initView();
        setBackgroundColor(0x55ffffff);
        ViewManager.mDragViewList.clear();

        mMinRadius = MainActivity.screenHeight / 16;
        mMaxRadius = MainActivity.screenHeight / 4;
    }

    boolean isAdd = false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isAdd) {
            addView(mViewGroup);
            isAdd = true;
        }
    }

    public void initView() {
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.keyboard_map, null);
        mAddButton = (Button) mViewGroup.findViewById(R.id.add_button);
        mAddTrend = (Button) mViewGroup.findViewById(R.id.add_trend_control);
        mAddTrendByButton = (Button) mViewGroup.findViewById(R.id.add_trend_control_by_button);
        mSave = (Button) mViewGroup.findViewById(R.id.save);
        mAddButton.setOnClickListener(this);
        mAddTrend.setOnClickListener(this);
        mAddTrendByButton.setOnClickListener(this);
        mSave.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MainActivity.screenWidth, MainActivity.screenHeight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                createNewDragView("", MainActivity.screenWidth / 2, MainActivity.screenHeight / 2);
                break;
            case R.id.add_trend_control:
                break;
            case R.id.add_trend_control_by_button:
                if (mTrendByButtonView == null) {
                    mTrendByButtonView = View.inflate(mContext, R.layout.trend_view, null);
                    mRlDirectionKey = (RelativeLayout) mTrendByButtonView.findViewById(R.id.rl_direction_key);
                    mTvLeft = (TextView) mTrendByButtonView.findViewById(R.id.tv_left);
                    mTvUp = (TextView) mTrendByButtonView.findViewById(R.id.tv_up);
                    mTvRight = (TextView) mTrendByButtonView.findViewById(R.id.tv_right);
                    mTvDown = (TextView) mTrendByButtonView.findViewById(R.id.tv_down);

                    mRlDirectionParams = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    mRlDirectionKey.setLayoutParams(mRlDirectionParams);

                    DirectionKeyTouchListener touchListener = new DirectionKeyTouchListener();
                    DirectionKeyHoverListener hoverListener = new DirectionKeyHoverListener();
                    mRlDirectionKey.setOnTouchListener(touchListener);
                    mRlDirectionKey.setOnHoverListener(hoverListener);
                    mTvLeft.setOnTouchListener(touchListener);
                    mTvUp.setOnTouchListener(touchListener);
                    mTvRight.setOnTouchListener(touchListener);
                    mTvDown.setOnTouchListener(touchListener);


                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.gravity = Gravity.CENTER;
                    mViewGroup.addView(mTrendByButtonView, layoutParams);
                    ViewManager.mDirectionKeyArr[0] = KeyEvent.KEYCODE_A;
                    ViewManager.mDirectionKeyArr[1] = KeyEvent.KEYCODE_W;
                    ViewManager.mDirectionKeyArr[2] = KeyEvent.KEYCODE_D;
                    ViewManager.mDirectionKeyArr[3] = KeyEvent.KEYCODE_S;
                    }
                break;
            case R.id.save:
                MainActivity.mHandler.sendEmptyMessage(1);
                break;
        }
    }


    public Bitmap buildBitmap(String key) {
        final TextView textView = new TextView(mContext);
        textView.setText(key);
        textView.setTextSize(50);
        textView.setTextColor(Color.GREEN);
        textView.setBackgroundResource(R.drawable.mapping_key_bg_shape);
        textView.setGravity(Gravity.CENTER);
        textView.setDrawingCacheEnabled(true);
        textView.measure(0, 0);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.buildDrawingCache();
        Bitmap bitmap = textView.getDrawingCache();
        return bitmap;
    }

    public ControlView2.DragView createNewDragView(String key, int x, int y) {
        mBitmapW = buildBitmap(key);
        mCurrentView = new ControlView2.DragView(mContext, mBitmapW, x, y);
        mViewGroup.addView(mCurrentView);

//        setContentView(mViewGroup);
        return mCurrentView;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        String key = null;

        if (mIsFunctionKey) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                if (event.getSource() == (InputDevice.SOURCE_GAMEPAD | InputDevice.SOURCE_KEYBOARD)
                        | event.getSource() == InputDevice.SOURCE_JOYSTICK) {
                    key = MainActivity.mKeyMap.get(keyCode);
                    if (key != null) {
                        ViewManager.mDragViewList.remove(mCurrentView);
                        mViewGroup.removeView(mCurrentView);
                    }
                } else if (event.getSource() == InputDevice.SOURCE_KEYBOARD) {

                    boolean isPrintingKey = event.getKeyCharacterMap().isPrintingKey(keyCode);
                    ViewManager.mDragViewList.remove(mCurrentView);
                    mViewGroup.removeView(mCurrentView);

                    if (isPrintingKey) {
                        key = String.valueOf((char) event.getUnicodeChar()).toUpperCase();
                    } else {
                        key = keyCode + "";
                    }
                }
                if (key != null) {
                    DragView newDragView = createNewDragView(key, mCurrentView.mWUpX, mCurrentView.mWUpY);
                    newDragView.keyCode = keyCode;
                    ViewManager.mDragViewList.add(newDragView);
                }
            }
        } else if (mIsDirectionKey) {
            if (event.getSource() == (InputDevice.SOURCE_GAMEPAD | InputDevice.SOURCE_KEYBOARD)
                    | event.getSource() == InputDevice.SOURCE_JOYSTICK) {
                key = MainActivity.mKeyMap.get(keyCode);
            } else if (event.getSource() == InputDevice.SOURCE_KEYBOARD) {
                boolean isPrintingKey = event.getKeyCharacterMap().isPrintingKey(keyCode);
                if (isPrintingKey) {
                    key = String.valueOf((char) event.getUnicodeChar()).toUpperCase();
                } else {
                    key = keyCode + "";
                }
            }
            if (key != null) {
                currentTextView.setText(key);
                if (currentTextView == mTvLeft) {
                    ViewManager.mDirectionKeyArr[0] = keyCode;
                } else if (currentTextView == mTvUp) {
                    ViewManager.mDirectionKeyArr[1] = keyCode;
                } else if (currentTextView == mTvRight) {
                    ViewManager.mDirectionKeyArr[2] = keyCode;
                } else if (currentTextView == mTvDown) {
                    ViewManager.mDirectionKeyArr[3] = keyCode;
                }
            }


        }
        if (key != null) {
            Log.i("wwww", key);
        }
        return super.dispatchKeyEvent(event);
    }

    public class DragView extends View {
        public int mWUpX, mWUpY;
        public int mMotionX;
        public int mMotionY;
        private Paint mPaint;
        private Bitmap mBitmap;
        //        private boolean mIsCanReCreate = false;
//        public String key = "";
        public int keyCode;

        public DragView(Context context, Bitmap bitmap, int x, int y) {
            super(context);
            mPaint = new Paint();
            mBitmap = bitmap;
            mWUpX = mMotionX = x;
            mWUpY = mMotionY = y;
//            mIsCanReCreate = canReCreate;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, mMotionX, mMotionY, mPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean isMove = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsFunctionKey = true;
                    mIsDirectionKey = false;
                    requestFocus();
                    mCurrentView = this;
                    if (event.getX() <= mMotionX + mBitmap.getWidth()
                            && event.getX() >= mMotionX
                            && event.getY() <= mMotionY + mBitmap.getHeight()
                            && event.getY() >= mMotionY) {
                        isMove = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    isMove = true;
                    mMotionX = (int) event.getX() - mBitmap.getWidth() / 2;
                    mMotionY = (int) event.getY() - mBitmap.getHeight() / 2;
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:

                    mWUpX = mMotionX;
                    mWUpY = mMotionY;
                    break;
            }
            return isMove;
        }
    }

    class DirectionKeyTouchListener implements View.OnTouchListener {
        int lastX, lastY;

        int newLeft, newTop, newRight, newBottom;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsFunctionKey = false;

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    if (v.getId() != R.id.rl_direction_key) {
                        mIsDirectionKey = true;
                        if (currentTextView != null) {
                            currentTextView.setTextColor(Color.BLACK);
                        }
                        currentTextView = (TextView) v;
                        currentTextView.setTextColor(Color.RED);
                    } else if (currentTextView != null) {
                        currentTextView.setTextColor(Color.BLACK);
                    }

                    v.getLocationOnScreen(mLocations);
                    mCircleCenterX = mLocations[0] + mBigCircleRadius;
                    mCircleCenterY = mLocations[1] + mBigCircleRadius;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (v.getId() == R.id.rl_direction_key) {
                        int dX = (int) (event.getRawX() - lastX);
                        int dY = (int) (event.getRawY() - lastY);
                        if (mCanResize) {
                            mIsDrag = true;
                            mDistance = Math.sqrt(Math.pow(event.getRawX() - mCircleCenterX, 2)
                                    + Math.pow(event.getRawY() - mCircleCenterY, 2));
                            float scale = (float) (mDistance / (v.getWidth() / 2));
                            if ((mBigCircleRadius >= mMinRadius && mBigCircleRadius <= mMaxRadius)
                                    || (mBigCircleRadius < mMinRadius
                                            && (mDistance > mBigCircleRadius))
                                    || (mBigCircleRadius > mMaxRadius
                                            && (mDistance < mBigCircleRadius))) {
                                v.setScaleX(scale);
                                v.setScaleY(scale);
                                mBigCircleRadius = (int) mDistance;
                                mCircleThick = (int) (10 * scale);
                            }
                        } else {
                            newLeft = v.getLeft() + dX;
                            newTop = v.getTop() + dY;
                            newRight = v.getRight() + dX;
                            newBottom = v.getBottom() + dY;
                            v.layout(newLeft, newTop, newRight, newBottom);
                        }
                        lastX = lastX + dX;
                        lastY = lastY + dY;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mCanResize = false;
                    mIsDrag = false;
                    if (v.getId() == R.id.rl_direction_key) {
                        mRlDirectionParams.leftMargin = newLeft;
                        mRlDirectionParams.topMargin = newTop;
                        mRlDirectionParams.setMargins(v.getLeft(), v.getTop(), 0, 0);
                        v.setLayoutParams(mRlDirectionParams);
                        v.getLocationOnScreen(mLocations);
                        mCircleCenterX = mLocations[0] + mBigCircleRadius;
                        mCircleCenterY = mLocations[1] + mBigCircleRadius;

                        int top = mTvUp.getPaddingTop();
                        int size = (int) mTvUp.getTextSize();

                        mDistanceFromCircleToKey = mBigCircleRadius - top;
                        ViewManager.mDirectionKeyArr[4] = mCircleCenterX;
                        ViewManager.mDirectionKeyArr[5] = mCircleCenterY;
                        ViewManager.mDirectionKeyArr[6] = mDistanceFromCircleToKey;
                    }

                    break;
            }
            return true;
        }
    }

    private class DirectionKeyHoverListener implements View.OnHoverListener {

        @Override
        public boolean onHover(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    if (mBigCircleRadius == 0) {
                        mBigCircleRadius = mRlDirectionKey.getWidth() / 2;
                    }
                    break;
                case MotionEvent.ACTION_HOVER_MOVE:
                    if (!mIsDrag) {
                        mDistance = Math.pow(Math.abs(event.getRawX() - mCircleCenterX), 2)
                                + Math.pow(Math.abs(event.getRawY() - mCircleCenterY), 2);
                        if (mDistance <= Math.pow(mBigCircleRadius, 2)
                                && mDistance >= Math.pow(mBigCircleRadius - mCircleThick, 2)) {
                            mCanResize = true;
                        } else {
                            mCanResize = false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    break;
            }
            return false;
        }
    }
}
