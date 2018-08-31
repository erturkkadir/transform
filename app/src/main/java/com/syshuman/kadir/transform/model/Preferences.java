package com.syshuman.kadir.transform.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class Preferences {

    private SharedPreferences sharedPreferences;

    public Preferences(Activity activity) {

        this.sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);

    }


    public HashMap<String, String> getAllPrefs() {
        HashMap<String, String> prefs = new HashMap<>();

        // Fourier parameters
        prefs.put("sgn_len", sharedPreferences.getString("sgn_len", "1024"));   // Signal Length 256, 512, 1024, 2048 points
        prefs.put("sgn_frq", sharedPreferences.getString("sgn_frq", "44100"));  // Sampling rate 22050, 44100 Hz
        prefs.put("dyn_amp", sharedPreferences.getString("dyn_amp", "false"));  // Whether Mag axis to be scaled dynamically or not
        prefs.put("fft_dim", sharedPreferences.getString("fft_dim", "3D"));     // sqrt(a^2+b^2) 2D or f,r,i 3D

        // Parameters for Laplace
        prefs.put("sig_min", sharedPreferences.getString("sig_min", "-2.0"));   // Min sigma value
        prefs.put("sig_max", sharedPreferences.getString("sig_max", "+2.0"));   // Maz sigma value
        prefs.put("sig_step", sharedPreferences.getString("sig_step", "0.1"));  // Sigma steps


        // Parameters for Z
        prefs.put("rad_min", sharedPreferences.getString("sig_min", "0.0"));    // Min r value (inside the circle)
        prefs.put("rad_max", sharedPreferences.getString("sig_max", "+2.0"));   // Max r value (outside the circle)
        prefs.put("rad_step", sharedPreferences.getString("sig_step", "0.1"));  // Radius steps

        // Last screen settings
        prefs.put("show_form", sharedPreferences.getString("show_form", "2D")); // FT in dimension

        return prefs;

    }


    public boolean setFourierPrefs(int sgn_len, int sgn_frq, boolean dyn_amp, String fft_dim) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("sgn_len", sgn_len);
        editor.putInt("sgn_frq", sgn_frq);
        editor.putBoolean("dyn_amp", dyn_amp);
        editor.putString("fft_dim", String.valueOf(fft_dim));
        editor.apply();
        return true;

    }

    public HashMap<String, String> getFourierPrefs() {

        HashMap<String, String> pref_array = new HashMap<>();

        int sgn_len = sharedPreferences.getInt("sgn_len", 1024);
        int sgn_frq = sharedPreferences.getInt("sgn_frq", 44100);
        boolean dyn_amp = sharedPreferences.getBoolean("dyn_amp", false);
        String fft_dim = sharedPreferences.getString("fft_dim", "3D");

        pref_array.put("sgn_len", String.valueOf(sgn_len));  // Signal Length 256, 512, 1024, 2048 points
        pref_array.put("sgn_frq", String.valueOf(sgn_frq));
        pref_array.put("dyn_amp", String.valueOf(dyn_amp));
        pref_array.put("fft_dim", String.valueOf(fft_dim));
        return pref_array;
    }

    public boolean setLaplacePrefs(float sig_min, float sig_max, float sig_step) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("sig_min", sig_min);
        editor.putFloat("sig_max", sig_max);
        editor.putFloat("sig_step", sig_step);
        editor.apply();
        return true;
    }

    public HashMap<String, String> getLaplacePrefs() {

        HashMap<String, String> pref_array = new HashMap<>();

        float sig_min = sharedPreferences.getFloat("sig_min", -2.0f);
        float sig_max = sharedPreferences.getFloat("sig_max",  2.0f);
        float sig_step = sharedPreferences.getFloat("sig_step",  0.1f);

        pref_array.put("sig_min", String.valueOf(sig_min));
        pref_array.put("sig_max", String.valueOf(sig_max));
        pref_array.put("sig_step", String.valueOf(sig_step));

        return pref_array;
    }

    public boolean setZPrefs(float rad_min, float rad_max, float rad_step) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("rad_min", rad_min);
        editor.putFloat("rad_max", rad_max);
        editor.putFloat("rad_step", rad_step);
        editor.apply();
        return true;
    }

    public HashMap<String, String> getZPrefs() {

        HashMap<String, String> pref_array = new HashMap<>();

        float rad_min = sharedPreferences.getFloat("rad_min", 0.0f);
        float rad_max = sharedPreferences.getFloat("rad_max",  2.0f);
        float rad_step = sharedPreferences.getFloat("rad_step",  0.1f);

        pref_array.put("rad_min", String.valueOf(rad_min));
        pref_array.put("rad_max", String.valueOf(rad_max));
        pref_array.put("rad_step", String.valueOf(rad_step));

        return pref_array;
    }
}
