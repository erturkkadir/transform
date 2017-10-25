package com.syshuman.kadir.transform;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.syshuman.kadir.transform.fragments.SoundData;
import com.syshuman.kadir.transform.model.Preferences;
import com.syshuman.kadir.transform.utils.Utils;
import com.syshuman.kadir.transform.view.OpenGLRenderer;
import com.syshuman.kadir.transform.view.OpenGLView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.opengl.ETC1.getHeight;
import static android.opengl.ETC1.getWidth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.status) FloatingActionButton status;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.surfaceView) GLSurfaceView glTSurfaceView;


    private SoundData soundData;
    private Boolean inRecord = false;
    private Context context;

    private Preferences preferences;
    private int sgn_len, sgn_frq;
    private boolean dyn_amp = false;
    private String fft_dim = "3D";

    private Utils utils;

    private GLSurfaceView glSurfaceView;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        context = getBaseContext();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getPermissions();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        utils = new Utils(this);

        initialize();
    }

    private void initialize() {


        String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        // log_visit("http://www.golaks.ca/android/save.php?lv_id=" + deviceID);

        getDefaults(); /* set default values of each transforms */


        soundData = new SoundData(this, sgn_len, sgn_frq, dyn_amp, fft_dim);
        soundData.init();

        status.setOnClickListener(onStatusClicked);


        if (hasGLES20()) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setPreserveEGLContextOnPause(true);


        } else {
            utils.showError("Time to buy a new phone. OpenGL 2.0 not supported");
        }
    }


    public void getDefaults() {
        preferences = new Preferences(this);
        HashMap<String, String> prefs = preferences.getAllPrefs();
        sgn_len = Integer.valueOf(prefs.get("sgn_len"));
        sgn_frq = Integer.valueOf(prefs.get("sgn_frq"));
        dyn_amp = Boolean.valueOf(prefs.get("dyn_amp"));
        fft_dim = String.valueOf(prefs.get("fft_dim"));
    }

    View.OnClickListener onStatusClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            inRecord = !inRecord;
            if (inRecord) {
                soundData.start();
                status.setImageResource(R.drawable.ic_pause);
                Snackbar.make(view, "Process Started ...", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

            } else {
                soundData.stop();
                status.setImageResource(R.drawable.ic_play);
                Snackbar.make(view, "Process Stopped !!!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }
    };


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
       //glSurfaceView.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        //glSurfaceView.onPause();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings: {
                return true;
            }
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.nav_fourier:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("screen", "f");
                startActivity(intent);
                break;
            case R.id.nav_laplace:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("screen", "l");
                startActivity(intent);
                break;
            case R.id.nav_z:
                intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("screen", "z");
                startActivity(intent);
                break;

            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.INTERNET,
                Manifest.permission.RECORD_AUDIO
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }


    private boolean hasGLES20() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }
}