package com.syshuman.kadir.transform.model;

import android.opengl.GLES20;

import com.syshuman.kadir.transform.MyRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Triangle {

    private FloatBuffer vertexBuffer;
    private int mProgram;
    private int mMVPMatrixHandle;

    int COORDS_PER_VERTEX = 3;

    private byte[] indices = {0, 1, 2}; // Indices to above vertices (in CCW)

    float triangleCoordinates[] = {
            0.0f, 0.622f, 0.0f,
            -0.5f, -0.311f, 0.0f,
            0.5f, -0.311f, 0.0f
    };

    float color[] = {0.636f, 0.769f, 0.225f, 1.0f}; // set color values rgb, opaque

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = triangleCoordinates.length;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    public Triangle() {

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoordinates.length * 4); // 4 bytes per float

        byteBuffer.order(ByteOrder.nativeOrder());  // Order by devices hardware

        vertexBuffer = byteBuffer.asFloatBuffer();  // create floating point buffer

        vertexBuffer.put(triangleCoordinates); // add coordinates to FloatBuffer

        vertexBuffer.position(0); // buffer to read the first coordinate

        int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void draw(GL10 gl) {

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(COORDS_PER_VERTEX, GL10.GL_FLOAT, 0, vertexBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_BYTE, vertexBuffer);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
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

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void draw() {

        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }
}