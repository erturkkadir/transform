package com.syshuman.kadir.transform.fragments;


import android.app.Activity;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.syshuman.kadir.transform.fft.Complex;
import com.syshuman.kadir.transform.fft.FFT;
import com.syshuman.kadir.transform.model.Preferences;

import java.util.HashMap;

public class SoundData implements Runnable {

    private int bufferSize;
    private AudioRecord audioRecord;
    private short[] data;
    private boolean shouldContinue = true;
    private Thread aThread;

    private int sgn_len;  //
    private int max_frq;
    private Complex c_data;


    public SoundData(Activity activity)  {
        Preferences preferences = new Preferences(activity);
        HashMap<String, String> settings = preferences.getFourierPrefs();
        sgn_len = Integer.valueOf(settings.get("sgn_len")); //  1024
        max_frq = Integer.valueOf(settings.get("max_frq")); // 44100
        data = new short[sgn_len];
        c_data = new Complex(sgn_len);



    }

    public void init(int sample_rate, int channel, int encoding) {
        Short[] data;


        bufferSize = AudioRecord.getMinBufferSize(sample_rate, channel, encoding);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = sample_rate * 2;
            Log.e("Error", "Buffersize error");
        }

        audioBuffer = new short[bufferSize/2];

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                sample_rate,
                channel,
                encoding,
                bufferSize);

        if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("Error", "Audio cant init");
        }

        aThread = new Thread(new Runnable() {
            @Override
            public void run() {

                audioRecord.startRecording();
                while (aThread != null) {
                    int size = audioRecord.read(data, 0, sgn_len);
                    if (size == 0) {
                        showError("Unable to get the size");
                        return;
                    }

                    FFT fft = new FFT();
                    Complex c_data = fft.fft_real(data);


                    float df = (sfreq * 1.0f) / (sgn_len * 1.0f); // 11025 / 4096 = 1.35 Hz  6000 Hz
                }

            }
        });

    }


    public void start() {

        audioRecord.startRecording();
        aThread.start();

    }

    public void stop() {

        audioRecord.stop();
        //aThread.interrupt();


    }

    public void release() {
        audioRecord.release();
    }


    @Override
    public void run() {

    }
}
