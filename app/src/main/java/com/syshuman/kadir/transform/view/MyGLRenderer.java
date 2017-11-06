package com.syshuman.kadir.transform.view;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "FractalRenderer";
    private Fractal mFractal;

    private int mHeight;
    private int mWidth;

    //Store all values as doubles, and truncate for use as floats.
    private double mRatio;
    private double mY = 1.0f;
    private double mX = 1.0f;
    private double mZoom = 0.5f;
    private double zoomIncrease = 0.5;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mFractal = new Fractal();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float[] mMVPMatrix = new float[]{
                (float) (-1.0 / mZoom), 0.0f, 0.0f, 0.0f,
                0.0f, (float) (1.0 / (mZoom * mRatio)), 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                (float) -mX, (float) -mY, 0.0f, 1.0f};

        mFractal.draw(mMVPMatrix);

    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        mWidth = width;
        mHeight = height;

        //Set viewport to fullscreen
        GLES20.glViewport(0, 0, width, height);

        mRatio = (double) width / height;


    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void checkGlError(String glOperation) {
        int error;

        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public void add(double dx, double dy) {
        //Both are scaled by mHeight, because the ratio is taken into account by the translation matrix
        mX -= dx / (mZoom * mHeight);
        mY -= dy / (mZoom * mHeight);
    }


    public void zoom(double scaleFactor, double x, double y) {
        scaleFactor = (scaleFactor - 1) * zoomIncrease + 1;
        // Default zoom is to top corner of the screen. Thus, changes should be zeroed at that point
        x -= mWidth / 2;
        y -= mHeight / 2;

        //Note that, because mZoom changes in the add method, there is an implicit division by log(2) hidden through limit discrete summation/integration
        double scale = Math.log(scaleFactor);

        //Move towards focus
        add(-scale * x, -scale * y);

        //add(scale)
        mZoom *= scaleFactor;
    }

}