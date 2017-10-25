package com.syshuman.kadir.transform.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.jar.Attributes;

/**
 * Created by kerturkx on 2017-10-20.
 */

public class OpenGLView extends GLSurfaceView {

    private Context context;
    private final float TOUCH_SCALE_FACTOR = 180.0f/320.0f;
    private float mPreviousX;
    private float mPreviousY;

    private OpenGLRenderer renderer;


    public OpenGLView(Context context, Attributes attrs) {

        super(context);

        this.context = context;
        renderer = new OpenGLRenderer();
        setRenderer(renderer);

    }



    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                if(y>getHeight()/2) dx = dx * -1;
                if(x<getWidth()/2)  dy = dy * -1;
                renderer.setAngle(
                        renderer.getAngle() + ((dx+dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

}
