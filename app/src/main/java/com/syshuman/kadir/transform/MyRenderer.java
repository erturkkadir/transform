package com.syshuman.kadir.transform;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;

import com.syshuman.kadir.transform.model.MyFunction;
import com.syshuman.kadir.transform.model.Square;
import com.syshuman.kadir.transform.model.Triangle;
import com.syshuman.kadir.transform.model.UserLine;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_LUMINANCE;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;

/**
 * Created by kerturkx on 2017-10-16.
 */

public class MyRenderer implements GLSurfaceView.Renderer {


    private Triangle triangle;
    private Square square;
    private UserLine userLine;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public MyRenderer(Context context) {


    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

       // GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);    // Set the background frame color

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

       //GLES20.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); // redraw background

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
