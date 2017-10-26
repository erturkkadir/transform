package com.syshuman.kadir.transform.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class MyGLSurfaceView extends GLSurfaceView {

    private Context context;
    private final float TOUCH_SCALE_FACTOR = 180.0f/320.0f;
    private float mPreviousX;
    private float mPreviousY;

    private final MyGLRenderer myGLRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(2);
        this.context = context;
        myGLRenderer = new MyGLRenderer();
        setRenderer(myGLRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                myGLRenderer.setAngle(
                        myGLRenderer.getAngle() +
                                ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                requestRender();
                break;
            case MotionEvent.ACTION_DOWN :

                // Remember where we started
                mPreviousX = x;
                mPreviousY = y;

                break;
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }


}
