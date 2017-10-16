package com.syshuman.kadir.transform;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by kerturkx on 2017-10-16.
 */

public class MyRenderer implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
        gl10.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
        gl10.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
        gl10.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
        gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
        gl10.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
        gl10.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // GLES20.glViewport(0,0,width, length);

        if (height == 0) height = 1;   // To prevent divide by zero
        float aspect = (float)width / height;

        // Set the viewport (display area) to cover the entire window
        gl10.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl10.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
        gl10.glLoadIdentity();                 // Reset projection matrix
        // Use perspective projection
        GLU.gluPerspective(gl10, 45, aspect, 0.1f, 100.f);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
        gl10.glLoadIdentity();                 // Reset
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }
}
