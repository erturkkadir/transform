package com.syshuman.kadir.transform.view;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import static android.R.attr.width;


public class Triangle {

    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    private final int COORDS_PER_VERTEX = 3;
    private float triangleCoords[] = {
            0.0f,  1.0f, 0.0f, // top
            1.0f,  -1.0f, 1.0f, // bottom left
            1.0f,  1.0f, 1.0f, // bottom right


    };
    private int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private float color[] = {0.03671875f, 0.76953125f, 0.22265625f, 1.0f};

    public Triangle() {
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);                                          // add program to OpenGL environment
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");    // get handle to vertex shader's vPosition member
        GLES20.glEnableVertexAttribArray(mPositionHandle);                      // Enable a handle to the triangle vertices

        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer); // Prepare the triangle coordinate data
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");         // get handle to fragment shaders vColor member

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);                         // set color for drawing the triangle

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); // get handle to shape's transformation matrix
        MyGLRenderer.checkGlError("glGetUniformLocation");

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);    // Apply the projection and view transformation
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);                   // Draw the triangle
        GLES20.glDisableVertexAttribArray(mPositionHandle);                     // Disable vertex array
    }



}
