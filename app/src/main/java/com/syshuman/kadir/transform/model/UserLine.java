package com.syshuman.kadir.transform.model;

import android.opengl.GLES20;

import com.syshuman.kadir.transform.MyRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by kerturkx on 2017-10-18.
 */

public class UserLine {

    private final String vertexShaderCode = "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    int COORDS_PER_VERTEX;
    private int vertexStride = COORDS_PER_VERTEX * 4;

    private float[] pathCords = {
            0.0f, 0.0f, 0.0f,
            3.0f,3.0f, 3.0f
    };

    private short[] pathDrawOrder = {0, 1};

    private float[] color = {1.0f, 0.0f, 0.0f, 1.0f};


    public UserLine() {
        ByteBuffer bb = ByteBuffer.allocateDirect(pathCords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(pathCords);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(pathDrawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(pathDrawOrder);
        drawListBuffer.position(0);

        int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_LINES, pathDrawOrder.length,  GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisable(mColorHandle);
    }
}
