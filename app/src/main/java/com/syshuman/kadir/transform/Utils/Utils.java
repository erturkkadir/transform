package com.syshuman.kadir.transform.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by kerturkx on 2017-10-20.
 */

public class Utils {

    private Activity activity;

    public Utils(Activity activity) {
        this.activity = activity;
    }

    public void showError(final String error){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Error !!!!");
                builder.setMessage(error);

                builder.setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
