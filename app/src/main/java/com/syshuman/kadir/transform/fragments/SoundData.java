package com.syshuman.kadir.transform.fragments;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.syshuman.kadir.transform.Utils.Utils;
import com.syshuman.kadir.transform.fft.Complex;
import com.syshuman.kadir.transform.fft.FFT;

import static java.lang.Math.sqrt;

public class SoundData {

    private int bufferSize;
    private AudioRecord audioRecord;
    private Thread aThread;

    private int sgn_len;  // 1024
    private int sgn_frq; // 44100
    private boolean dyn_amp;
    private String fft_dim;

    private short[] data;
    private Complex c_data;
    private Utils utils;

    private FFT fft;
    private float sig_ = 0.0f;
    private float rad = 0.0f;
    private String method = "F";
    private double[][][] g_data;

    public SoundData(Activity activity, int sgn_len, int sgn_frq, boolean dyn_amp, String fft_dim) {

        this.sgn_len = sgn_len;
        this.sgn_frq = sgn_frq;
        this.dyn_amp = dyn_amp; // False default
        this.fft_dim = fft_dim; // 3D default
        data = new short[sgn_len];
        c_data = new Complex(sgn_len);
       // g_data = new double[sgn_len][sgn_len][sgn_len];
        utils = new Utils(activity);
    }

    public void init() {

        bufferSize = AudioRecord.getMinBufferSize(sgn_frq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE || bufferSize < 0) {
            utils.showError("Buffer size error or AudioRecord error ");
            return;
        }

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.DEFAULT,
                sgn_frq,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("Error", "Audio cant init");
            utils.showError("Auido could not initialize ");
            return;
        }

        fft = new FFT();

        aThread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (aThread != null) {
                    int size = audioRecord.read(data, 0, sgn_len);
                    if (size == 0) {
                        utils.showError("Unable to fetch data from audioRecord ....");
                        return;
                    }
                    if (method == "F") {

                        for (int i = 0; i < sgn_len; i++) {
                            c_data.d_real[i] = data[i];
                            c_data.d_imag[i] = 0.0;
                        }
                    }


                    fft.fft_real(c_data);


                    float df = (sgn_frq * 1.0f) / (sgn_len * 1.0f); // 44100 / 1024 = 43.06 Hz  is delta freq

                    if (fft_dim.equals("2D")) {
                        for (int i = 0; i < sgn_len / 2; i++) {
                            double x = i * df * 1.0;
                            double y = 0;
                            double z = Math.sqrt(c_data.d_real[i] * c_data.d_real[i] + c_data.d_imag[i] * c_data.d_imag[i]);
                            //gdata

                        }
                    } else {
                        for (int i = 0; i < sgn_len / 2; i++) {
                            double x = i * df * 1.0;
                            double y = c_data.d_real[i];
                            double z = c_data.d_imag[i];
                        }
                    }
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



}
