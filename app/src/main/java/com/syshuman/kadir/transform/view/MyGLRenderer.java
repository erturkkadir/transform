package com.syshuman.kadir.transform.view;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import static android.R.attr.width;

public class MyGLRenderer implements GLSurfaceView.Renderer {


    private Context context;
    private Triangle triangle;
    private static final String TAG = "MyGLRenderer";
    private MyGraph mTriangle;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float mAngle;

    private Torus torus;

    public MyGLRenderer(Context context) {
        this.context = context;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        float[] data = {0.1f, 0.1f, 0.1f,
                -0.1f, -0.1f, -0.1f };


        // mTriangle = new MyGraph();
        torus = new Torus(context, data, data, data, data);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        float[] scratch = new float[16];

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);  // Draw background color


        Matrix.setLookAtM(mViewMatrix, 0, 2f, 2f, 2f, 0f, 0f, 0f, 0f, 0.0f, 1.0f);  // Set the camera position (View matrix)
        /*
         * Defines a viewing transformation in terms of an eye point, a center of
         * view, and an up vector.
         *
         * @param rm returns the result
         * @param rmOffset index into rm where the result matrix starts
         * @param eyeX eye point X
         * @param eyeY eye point Y
         * @param eyeZ eye point Z
         * @param centerX center of view X
         * @param centerY center of view Y
         * @param centerZ center of view Z
         * @param upX up vector X
         * @param upY up vector Y
         * @param upZ up vector Z
         */

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);  // Calculate the projection and view transformation

        /**
         * Multiplies a 4 element vector by a 4x4 matrix and stores the result in a
         * 4-element column vector. In matrix notation: result = lhs x rhs
         * <p>
         * The same float array may be passed for resultVec, lhsMat, and/or rhsVec.
         * However, the resultVec element values are undefined if the resultVec
         * elements overlap either the lhsMat or rhsVec elements.
         *
         * @param resultVec The float array that holds the result vector.
         * @param resultVecOffset The offset into the result array where the result
         *        vector is stored.
         * @param lhsMat The float array that holds the left-hand-side matrix.
         * @param lhsMatOffset The offset into the lhs array where the lhs is stored
         * @param rhsVec The float array that holds the right-hand-side vector.
         * @param rhsVecOffset The offset into the rhs vector where the rhs vector
         *        is stored.
         *
         * @throws IllegalArgumentException if resultVec, lhsMat,
         * or rhsVec are null, or if resultVecOffset + 4 > resultVec.length
         * or lhsMatOffset + 16 > lhsMat.length or
         * rhsVecOffset + 4 > rhsVec.length.
         */

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

       /**
         * Creates a matrix for rotation by angle a (in degrees)
         * around the axis (x, y, z).
         * <p>
         * An optimized path will be used for rotation about a major axis
         * (e.g. x=1.0f y=0.0f z=0.0f).
         *
         * @param rm returns the result
         * @param rmOffset index into rm where the result matrix starts
         * @param a angle to rotate in degrees
         * @param x X axis component
         * @param y Y axis component
         * @param z Z axis component
         */

        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        /**
         * Multiplies a 4 element vector by a 4x4 matrix and stores the result in a
         * 4-element column vector. In matrix notation: result = lhs x rhs
         * <p>
         * The same float array may be passed for resultVec, lhsMat, and/or rhsVec.
         * However, the resultVec element values are undefined if the resultVec
         * elements overlap either the lhsMat or rhsVec elements.
         *
         * @param resultVec The float array that holds the result vector.
         * @param resultVecOffset The offset into the result array where the result
         *        vector is stored.
         * @param lhsMat The float array that holds the left-hand-side matrix.
         * @param lhsMatOffset The offset into the lhs array where the lhs is stored
         * @param rhsVec The float array that holds the right-hand-side vector.
         * @param rhsVecOffset The offset into the rhs vector where the rhs vector
         *        is stored.
         *
         * @throws IllegalArgumentException if resultVec, lhsMat,
         * or rhsVec are null, or if resultVecOffset + 4 > resultVec.length
         * or lhsMatOffset + 16 > lhsMat.length or
         * rhsVecOffset + 4 > rhsVec.length.
         */
        // Draw triangle
        // mTriangle.draw();
        torus.draw();


    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height; // 0.6880 for Samsung
        float zoom = 2f;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, ratio, -ratio, 3, 5);

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

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("sdf", glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


    public float getAngle() {
        return mAngle;
    }

    public void setAngle(float angle) {
        mAngle = angle;
    }





}