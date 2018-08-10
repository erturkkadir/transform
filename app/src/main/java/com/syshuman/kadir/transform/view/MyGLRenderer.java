package com.syshuman.kadir.transform.view;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.syshuman.kadir.transform.fragments.SoundData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private float[] modelMatrix         = new float[16];        // Model Matrix
    private float[] viewMatrix          = new float[16];        // View Matrix
    private float[] projectionMatrix    = new float[16];        // Projection Matrix
    private float[] mvpMatrix           = new float[16];        // MVP Combined Matrix
    private float   mAngle;


    private int mvpMatrixUniform;
    private int mvMatrixUniform;
    private int lightPosUniform;

    private int positionAttribute;
    private int normalAttribute;
    private int colorAttribute;

    private static final String MVP_MATRIX_UNIFORM      = "u_MVPMatrix";
    private static final String MV_MATRIX_UNIFORM       = "u_MVMatrix";
    private static final String LIGHT_POSITION_UNIFORM  = "u_LightPos";

    private static final String POSITION_ATTRIBUTE      = "a_Position";
    private static final String NORMAL_ATTRIBUTE        = "a_Normal";
    private static final String COLOR_ATTRIBUTE         = "a_Color";

    private static final int POSITION_DATA_SIZE_IN_ELEMENTS = 3;
    private static final int NORMAL_DATA_SIZE_IN_ELEMENTS   = 3;
    private static final int COLOR_DATA_SIZE_IN_ELEMENTS    = 4;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_SHORT = 2;

    private static final int STRIDE = (POSITION_DATA_SIZE_IN_ELEMENTS + NORMAL_DATA_SIZE_IN_ELEMENTS + COLOR_DATA_SIZE_IN_ELEMENTS)
            * BYTES_PER_FLOAT;


    private SoundData soundData;

    private float[] axisData;                           // Model data
    private FloatBuffer axisVertices;                   // Model vertices

    private int mMVPMatrixHandle;                        // Model position info
    private int mPositionHandle;                        // Model position info
    private int mColorHandle;                           // Model color info

    private final int mBytesPerFloat = 4;
    private final int mStrideBytes = 7 * mBytesPerFloat; // strides per vertex 6 element for each line xyz rgba

    private final int mPositionOffset = 0; // offset of position data
    private final int mPositionDataSize = 3; // size of position data
    private final int mColorOffset = 3; // offset of color data


    private int programHandle;
    private float xMin, xMax, yMin, yMax, zMin, zMax;
    private float eyeX, eyeY, eyeZ;
    private float lookX, lookY, lookZ;
    private float upX, upY, upZ;
    private int sgn_len;

    public MyGLRenderer(int sgn_len, SoundData soundData) {
        this.soundData = soundData;
        axisData = soundData.getData();
        axisVertices = ByteBuffer.allocateDirect(axisData.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        axisVertices.put(axisData).position(0);
        this.sgn_len = sgn_len;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        eyeX  = 0.5f; eyeY  = 0.5f; eyeZ  = -0.5f;
        lookX = 0.0f; lookY = 0.0f; lookZ =  0.0f;
        upX   = 0.0f; upY   = 1.0f; upZ   =  0.0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

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

        programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);       // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);     // Bind the fragment shader to the program.
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");    // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
            GLES20.glLinkProgram(programHandle);                            // Link the two shaders together into a program.
            int[] linkStatus = new int[1];                                  // Get the link status.
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
        final float far = 100.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        long time = SystemClock.uptimeMillis() % 50000L;        // Do a complete rotation every 10 seconds.
        float angleInDegrees = (360.0f / 100000.0f) * ((int) time);

        Matrix.setIdentityM(modelMatrix, 0);                   // Draw the triangle facing straight on.
        Matrix.rotateM(modelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);

        gl.glEnableClientState(GLES20.GL_VERTEX_ATTRIB_ARRAY_TYPE);

        // drawTriangle(axisVertices);

        gl.glDisableClientState(GLES20.GL_VERTEX_ATTRIB_ARRAY_TYPE);

    }

    private String getVertexShader() {

        return "uniform mat4 u_MVPMatrix;         \n"        // A constant representing the combined model/view/projection matrix.
                + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
                + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.
                + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.
                + "void main()                    \n"        // The entry point for our vertex shader.
                + "{                              \n"
                + "  v_Color = a_Color;          \n"        // Pass the color through to the fragment shader.
                + "  gl_Position = u_MVPMatrix * a_Position;\n"    // gl_Position is a special variable used to store the final position.
                + "} \n";
    }

    private String getFragmentShader() {
        return "precision mediump float;       \n"        // Set the default precision to medium. We don't need as high of a
                + "varying vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the
                + "void main()                    \n"        // The entry point for our fragment shader.
                + "{                              \n"
                + "   gl_FragColor = v_Color;     \n"        // Pass the color directly through the pipeline.
                + "}                              \n";
    }

    private void drawTriangle(FloatBuffer aTriangleBuffer) {

        aTriangleBuffer.position(0);                                  // Pass in the position information

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,  mStrideBytes, aTriangleBuffer);
        mColorHandle = GLES20.glGetUniformLocation(programHandle, "vColor");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, sgn_len);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0);          // model * view
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0);      // model * view * projection

        GLES20.glLineWidth(5f);
        GLES20.glDrawElements(GLES20.GL_LINES, 3, GLES20.GL_UNSIGNED_SHORT, aTriangleBuffer);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

    public void setData(float[] data, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
        this.xMin = xMin; this.xMax = xMax;
        this.yMin = yMin; this.yMax = yMax;
        this.zMin = zMin; this.zMax = zMax;
        axisVertices.put(data).position(0);
    }
}