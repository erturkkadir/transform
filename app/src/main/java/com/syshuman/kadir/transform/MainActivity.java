package com.syshuman.kadir.transform;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.syshuman.kadir.transform.fragments.SoundData;
import com.syshuman.kadir.transform.model.Preferences;
import com.syshuman.kadir.transform.view.MyGLRenderer;
import com.syshuman.kadir.transform.view.MyGLSurfaceView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.status) FloatingActionButton status;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.glSurfaceView) MyGLSurfaceView glSurfaceView;

    private SoundData soundData;
    private Boolean inRecord = false;
    private Context context;

    Preferences preferences;
    private int sgn_len = 1024;
    private int sgn_frq = 44100;
    private boolean dyn_amp = false;
    private String fft_dim = "3D";


    private MyGLRenderer renderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        context = getBaseContext();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getPermissions();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initialize();
    }

    private void initialize() {

         String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
         Log.d("MainActivity", "http://www.golaks.ca/android/save.php?lv_id=" + deviceID);

        getDefaults();          /* set default values of each transforms */

        soundData = new SoundData(context);
        if (soundData.init(this, sgn_len, sgn_frq, dyn_amp, fft_dim)) {
            renderer = new MyGLRenderer(sgn_len, soundData);
            glSurfaceView.setRenderer(renderer);
            enableProgram();
        } else {
            disableProgram();
        }
    }

    private void disableProgram() {
        status.setOnClickListener(null);
        status.setEnabled(false);
        status.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
    }

    private void enableProgram() {
        status.setOnClickListener(onStatusClicked);
        status.setEnabled(true);
        status.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimary)));
    }

    private void getDefaults() {
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
        glSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        glSurfaceView.onPause();
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