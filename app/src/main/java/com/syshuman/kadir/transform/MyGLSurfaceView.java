package com.syshuman.kadir.transform;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.text.NoCopySpan;

/**
 * Created by kadir on 2017-10-14.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
    }
}
