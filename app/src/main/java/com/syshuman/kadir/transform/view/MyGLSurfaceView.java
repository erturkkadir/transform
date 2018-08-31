package com.syshuman.kadir.transform.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView {

    private ScaleGestureDetector mDetector;
    private Context mContext;

    float mPreviousX;
    float mPreviousY;
    int lastNumFingers = 0;

    public MyGLSurfaceView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        setPreserveEGLContextOnPause(true);
//        mDetector = new ScaleGestureDetector(mContext, new ScaleListener());
 //       setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        mDetector.onTouchEvent(e);
        if (e.getActionIndex() > 1) {
            return true;
        }

        int numFingers = e.getPointerCount();
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mPreviousX = 0.0f;
                mPreviousY = 0.0f;

                //Get the average of the fingers on the screen as the current position
                for (int i = 0; i < numFingers; i++) {
                    mPreviousX += e.getX(i);
                    mPreviousY += e.getY(i);
                }

                mPreviousX /= numFingers;
                mPreviousY /= numFingers;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPreviousX = 0.0f;
                mPreviousY = 0.0f;

                //Get the average of the remaining fingers on the screen as the current position
                for (int i = 0; i < numFingers; i++) {
                    if (i == e.getActionIndex()) continue;
                    mPreviousX += e.getX(i);
                    mPreviousY += e.getY(i);
                    //Log.d("FractalSurfaceView","Pointer Up: " + String.valueOf(e.get)+ ", " +String.valueOf(e.getActionIndex()));
                }
                numFingers -= 1;
                mPreviousX /= numFingers;
                mPreviousY /= numFingers;
                break;

            case MotionEvent.ACTION_MOVE:
                float tempX = 0.0f, tempY = 0.0f;

                //Get the average of the fingers on the screen as the current position
                for (int i = 0; i < numFingers; i++) {
                    tempX += e.getX(i);
                    tempY += e.getY(i);
                }

                tempX /= numFingers;
                tempY /= numFingers;

                if (lastNumFingers == numFingers) {
                    // Sometimes a third finger doesn't register under point, so track it separately
                    //mRenderer.add(tempX - mPreviousX, tempY - mPreviousY);
                }

                mPreviousX = tempX;
                mPreviousY = tempY;

                requestRender();
                break;
        }
        lastNumFingers = numFingers;
        return true;

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //mRenderer.zoom(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            return true;
        }
    }

}
