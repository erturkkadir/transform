package com.syshuman.kadir.transform.view;

import android.opengl.GLES20;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private float[] mModelMatrix = new float[16]; // Model Matrix
    private float[] mViewMatrix = new float[16];  // View Matrix
    private float[] mProjectionMatrix = new float[16]; // Projection Matrix
    private float[] mMVPMatrix = new float[16]; // MVP Combined Matrix

    private final FloatBuffer mTriangle1Vertices; // Model data
    private int mMVPMatrixHandle; // Transformation Matrix

    private int mPositionHandle; // Model position info
    private int mColorHandle;   // Model color info

    private final int mBytesPerFloat = 4;
    private final int mStrideBytes = 7 * mBytesPerFloat; // How many element per vertex

    private final int mPositionOffset = 0; // offset of position data
    private final int mPositionDataSize = 3; // size of position data
    private final int mColorOffset = 3; // offset of color data
    private final int mColorDataSize = 4; // size of color data


    public MyGLRenderer() {

        final float[] triangle1VerticesData = {
            // X, Y, Z, R, G, B, A
            -0.5f, -0.25f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.25f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.55f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f
        };

        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle1Vertices.put(triangle1VerticesData).position(0);
    }



    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background clear color to gray.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 1.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 1.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        String vertexShader = getVertexShader();
        String fragmentShader = getFragmentShader();


        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vertexShaderHandle != 0) {
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);    // Pass in the shader source.
            GLES20.glCompileShader(vertexShaderHandle);     // Compile the shader.
            int[] compileStatus = new int[1];     // Get the compilation status.
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {     // If the compilation failed, delete the shader.
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        } else
            throw new RuntimeException("Error creating vertex shader.");


        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fragmentShaderHandle != 0) {
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);    // Pass in the shader source.
            GLES20.glCompileShader(fragmentShaderHandle);       // Compile the shader.
            int[] compileStatus = new int[1];         // Get the compilation status.
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {     // If the compilation failed, delete the shader.
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        } else
            throw new RuntimeException("Error creating fragment shader.");


        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0)  {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);       // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);     // Bind the fragment shader to the program.
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");    // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
            GLES20.glLinkProgram(programHandle);                            // Link the two shaders together into a program.
            int[] linkStatus = new int[1];                            // Get the link status.
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {                                       // If the link failed, delete the program.
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        } else throw new RuntimeException("Error creating program.");


        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        GLES20.glUseProgram(programHandle);                 // Tell OpenGL to use this program when rendering.
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);             // Set the OpenGL viewport to the same size as the surface.
        final float ratio = (float) width / height;         // Create a new perspective projection matrix. The height will stay the same
        final float left = -ratio;                          // while the width will vary as per aspect ratio.
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangle1Vertices);
    }

    private String getVertexShader() {

        return "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.

                + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
                + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.

                + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

                + "void main()                    \n"		// The entry point for our vertex shader.
                + "{                              \n"
                + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
                // It will be interpolated across the triangle.
                + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
                + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                + "}                              \n";    // normalized screen coordinates.
    }

    private String getFragmentShader() {
        return "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                // precision in the fragment shader.
                + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
                // triangle per fragment.
                + "void main()                    \n"		// The entry point for our fragment shader.
                + "{                              \n"
                + "   gl_FragColor = v_Color;     \n"		// Pass the color directly through the pipeline.
                + "}                              \n";
    }

    private void drawTriangle(final FloatBuffer aTriangleBuffer)
    {
        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}

