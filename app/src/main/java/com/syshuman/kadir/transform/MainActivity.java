package com.syshuman.kadir.transform;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private GLSurfaceView.Renderer renderer;

    @BindView(R.id.surfaceView) GLSurfaceView surfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        surfaceView.setEGLContextClientVersion(2);

        renderer = new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
                gl10.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);  // Set color's clear-value to black
                gl10.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
                gl10.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
                gl10.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
                gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
                gl10.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
                gl10.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance
                surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

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
        };

        surfaceView.setRenderer(renderer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        surfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
