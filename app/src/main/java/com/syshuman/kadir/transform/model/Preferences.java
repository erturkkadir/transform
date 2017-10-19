package com.syshuman.kadir.transform.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class Preferences  {

    private SharedPreferences sharedPreferences;
    private Context context;


    public Preferences(Activity activity) {

        this.sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);

    }


    public HashMap<String, String> getFourierPrefs() {

        HashMap<String, String> prefs = new HashMap<>();

        prefs.put("sgn_len", sharedPreferences.getString("sgn_len", "1024"));
        prefs.put("max_frq", sharedPreferences.getString("max_frq", "44100"));
        //prefs.put("show_form"  , sharedPreferences.getString("show_form"  ,    "2D"));

        return prefs;

    }


    public boolean setFourierPrefs(HashMap<String, String> prefs) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(Map.Entry<String, String> e : prefs.entrySet() ) {
            String key = e.getKey();
            String value = e.getValue();
            editor.putString(key, value);
        }
        editor.apply();
        return true;

    }
}
