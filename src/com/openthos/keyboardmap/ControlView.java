package com.openthos.keyboardmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ControlView extends LinearLayout {
    private Paint paint;
    private Context mContext;
    public static ViewGroup mViewGroup;
    private RelativeLayout mRlDirectionKey;
    private DragView mCurrentView;
    private RelativeLayout.LayoutParams mRlDirectionParams;
    private int mCircleCenterX;
    private int mCircleCenterY;
    private int mBigCircleRadius;
    boolean mIsDrag;
    boolean mCanResize;
    private Bitmap mBitmapW;
    private int[] mLocations = new int[2];;


    public ControlView(Context context) {
        super(context);
        paint = new Paint();
        mContext = context;
        initView();
        setBackgroundColor(0xffffffff);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addView(mViewGroup);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_F1:
                MainActivity.mHandler.sendEmptyMessage(0);
        }
        return true;
    }



    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MainActivity.screenWidth, MainActivity.screenHeight);
    }


    class DirectionKeyTouchListener implements View.OnTouchListener {
        boolean isClick;
        int lastX, lastY;
        TextView currentTextView;
        int newLeft, newTop, newRight, newBottom;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isClick = true;
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    if (v.getId() != R.id.rl_direction_key) {
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
                    isClick = false;
                    if (v.getId() == R.id.rl_direction_key) {
                        int dX = (int) (event.getRawX() - lastX);
                        int dY = (int) (event.getRawY() - lastY);
                        if (mCanResize) {
                            mIsDrag = true;
                            if (mBigCircleRadius >= 80 && mBigCircleRadius <= 500) {
                                double distance = Math.sqrt(Math.pow(event.getRawX() - mCircleCenterX, 2) + Math.pow(event.getRawY() - mCircleCenterY, 2));
                                float scale = (float) (distance / (v.getWidth() / 2));
                                v.setScaleX(scale);
                                v.setScaleY(scale);
                                mBigCircleRadius = (int) distance;
                            }
                        } else {
                            newLeft = v.getLeft() + dX;
                            newTop = v.getTop() + dY;
                            newRight = v.getRight() + dX;
                            newBottom = v.getBottom() + dY;

                            v.layout(newLeft, newTop, newRight, newBottom);

//                            v.postInvalidate();

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
                    }
                    v.getLocationOnScreen(mLocations);
                    mCircleCenterX = mLocations[0] + mBigCircleRadius;
                    mCircleCenterY = mLocations[1] + mBigCircleRadius;

                    break;
            }
            return true;
        }
    }

    class DirectionKeyHoverListener implements View.OnHoverListener {

        @Override
        public boolean onHover(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    mBigCircleRadius = mBigCircleRadius == 0 ? mRlDirectionKey.getWidth() / 2 : mBigCircleRadius;
                    break;
                case MotionEvent.ACTION_HOVER_MOVE:
//                    int[] location = new int[2];
//                    v.getLocationOnScreen(location);
//                    mCircleCenterX = location[0] + mBigCircleRadius;
//                    mCircleCenterY = location[1] + mBigCircleRadius;
                    if (!mIsDrag) {
                        double length = Math.pow(Math.abs(event.getRawX() - mCircleCenterX), 2) + Math.pow(Math.abs(event.getRawY() - mCircleCenterY), 2);
                        if (length <= Math.pow(mBigCircleRadius, 2)
                                && length >= Math.pow(mBigCircleRadius - 10, 2)) {
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

    public void initView() {
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.mapping_keyboard, null);
        mRlDirectionKey = (RelativeLayout) mViewGroup.findViewById(R.id.rl_direction_key);
        TextView tvLeft = (TextView) mViewGroup.findViewById(R.id.tv_left);
        TextView tvUp = (TextView) mViewGroup.findViewById(R.id.tv_up);
        TextView tvRight = (TextView) mViewGroup.findViewById(R.id.tv_right);
        TextView tvDown = (TextView) mViewGroup.findViewById(R.id.tv_down);

        mRlDirectionParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mRlDirectionKey.setLayoutParams(mRlDirectionParams);

        DirectionKeyTouchListener touchListener = new DirectionKeyTouchListener();
        DirectionKeyHoverListener hoverListener = new DirectionKeyHoverListener();
        mRlDirectionKey.setOnTouchListener(touchListener);
        mRlDirectionKey.setOnHoverListener(hoverListener);
        tvLeft.setOnTouchListener(touchListener);
        tvUp.setOnTouchListener(touchListener);
        tvRight.setOnTouchListener(touchListener);
        tvDown.setOnTouchListener(touchListener);

        createNewDragView("", 0, 0, true);
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

    public DragView createNewDragView(String key, int x, int y, boolean isCanReCreate) {
        mBitmapW = buildBitmap(key);
        mCurrentView = new DragView(mContext, mBitmapW, x, y, isCanReCreate);
        mViewGroup.addView(mCurrentView);
//        setContentView(mViewGroup);
        return mCurrentView;
    }

    public int mWUpX, mWUpY;
    public class DragView extends View {
        public int mMotionX;
        public int mMotionY;
        private Paint mPaint;
        private Bitmap mBitmap;
        private boolean mIsCanReCreate = false;
        public String  key = "";

        public DragView(Context context, Bitmap bitmap, int x, int y, boolean canReCreate) {
            super(context);
            mPaint = new Paint();
            mBitmap = bitmap;
            mMotionX = x;
            mMotionY = y;
            mIsCanReCreate = canReCreate;
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
                    if (mIsCanReCreate) {
                        createNewDragView("", 0, 0, mIsCanReCreate);
                    }
                    mWUpX = mMotionX;
                    mWUpY = mMotionY;
                    break;
            }
            return isMove;
        }
    }
}
