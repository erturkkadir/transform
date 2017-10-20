package com.syshuman.kadir.transform.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.view.Gravity;
import android.widget.Toast;
import com.syshuman.kadir.transform.fft.FFT;

public class Audio implements Runnable {

    protected  boolean lock;
    private Thread thread;
    private short[] data;
    private int minBufSize;
    private int sfreq, sgn_len;
    private AudioRecord audioRecord;
    private Activity activity;

    protected Audio(Activity activity, int sgn_len, int s_freq) {
        this.activity = activity;
        data = new short[sgn_len];
        //x = new Complex(sgn_len);
        this.sgn_len = sgn_len;
        this.sfreq = s_freq;

    }

    private void start() {
        thread = new Thread(this, "Audio");
        thread.start();
    }

    @Override
    public void run() {
        processAudio();
    }

    public void stop() {
        Thread t = thread;
        thread = null;
        while(t != null && t.isAlive()) Thread.yield();
    }

    private void showError(final String error){
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

    private void showToast(String message) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void processAudio() {
        minBufSize = AudioRecord.getMinBufferSize(sfreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (minBufSize < 0) return;  // 8192

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sfreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 2*minBufSize);

        int state = audioRecord.getState();
        if (state != AudioRecord.STATE_INITIALIZED) {
            showError("Unable to initialize Audio Record!!. Make sure that your device support audio record");
            audioRecord.release();
            thread = null;
            return;
        }


        audioRecord.startRecording();
        while (thread != null) {
            int size = audioRecord.read(data, 0, sgn_len);
            if (size == 0) {
                showError("Unable to get the size");
                return;
            }

            FFT fft = new FFT();
            //fft.fft_real(c_data);


            float df = (sfreq * 1.0f) / (sgn_len * 1.0f); // 11025 / 4096 = 1.35 Hz  6000 Hz
        }
    }
}
