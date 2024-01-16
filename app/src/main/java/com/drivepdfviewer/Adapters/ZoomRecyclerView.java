package com.drivepdfviewer.Adapters;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.recyclerview.widget.RecyclerView;

public class ZoomRecyclerView extends RecyclerView {
    private static final int INVALID_POINTER_ID = -1;
    private static final float MAX_SCALE = 10.0f;
    private static final float MAX_ZOOM = 10.0f;

    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private float mScaleFactor = 1f;
    private boolean mIsZoomEnabled = true;
    private float mMaxZoom = MAX_ZOOM;
    private float maxWidth = 0.0f;
    private float maxHeight = 0.0f;
    private float mLastTouchX = 0f;
    private float mLastTouchY = 0f;
    private float mPosX = 0f;
    private float mPosY = 0f;

    public ZoomRecyclerView(Context context) {
        super(context);
        initializeScaleDetector(context);
    }

    public ZoomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeScaleDetector(context);
    }

    public ZoomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeScaleDetector(context);
    }

    private void initializeScaleDetector(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxWidth = (float) getMeasuredWidth();
        maxHeight = (float) getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean superHandled = super.onTouchEvent(ev);
        mGestureDetector.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = ev.getX();
                mLastTouchY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerIndex = ev.findPointerIndex(mActivePointerId);
                float x = ev.getX(pointerIndex);
                float y = ev.getY(pointerIndex);
                if (mScaleFactor > 1f) {
                    float dx = x - mLastTouchX;
                    float dy = y - mLastTouchY;
                    mPosX += dx;
                    mPosY += dy;
                    mPosX = Math.max(maxWidth - getWidth() * mScaleFactor, Math.min(mPosX, 0f));
                    mPosY = Math.max(maxHeight - getHeight() * mScaleFactor, Math.min(mPosY, 0f));
                }
                mLastTouchX = x;
                mLastTouchY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int pointerIndex2 = ev.getActionIndex();
                int pointerId = ev.getPointerId(pointerIndex2);
                if (pointerId == mActivePointerId) {
                    int newPointerIndex = pointerIndex2 == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
        }

        return superHandled || mScaleFactor > 1f;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = Math.max(1f, Math.min(mScaleFactor * detector.getScaleFactor(), MAX_SCALE));
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            if (scaleFactor != mScaleFactor) {
                float scaleDelta = scaleFactor / mScaleFactor;
                mPosX -= (focusX - mPosX) * (1 - scaleDelta);
                mPosY -= (focusY - mPosY) * (1 - scaleDelta);
                mScaleFactor = scaleFactor;
                mPosX = Math.max(maxWidth - getWidth() * mScaleFactor, Math.min(mPosX, 0f));
                mPosY = Math.max(maxHeight - getHeight() * mScaleFactor, Math.min(mPosY, 0f));
                invalidate();
            }

            return true;
        }
    }

    private void resetZoom() {
        mScaleFactor = 1f;
        mPosX = 0f;
        mPosY = 0f;
    }

    private void clampPosition() {
        float maxPosX = maxWidth - (getWidth() * mScaleFactor);
        float maxPosY = maxHeight - (getHeight() * mScaleFactor);
        mPosX = Math.max(maxPosX, Math.min(mPosX, 0f));
        mPosY = Math.max(maxPosY, Math.min(mPosY, 0f));
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mIsZoomEnabled) {
                if (mScaleFactor > 1f) {
                    resetZoom();
                } else {
                    mScaleFactor = mMaxZoom;
                    mPosX = -(e.getX() * (mMaxZoom - 1f));
                    mPosY = -(e.getY() * (mMaxZoom - 1f));
                    clampPosition();
                }
                invalidate();
            }
            return true;
        }
    }
}


