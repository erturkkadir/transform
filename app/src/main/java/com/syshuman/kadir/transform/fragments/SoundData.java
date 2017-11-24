package com.syshuman.kadir.transform.fragments;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.opengl.GLES31;

import com.syshuman.kadir.transform.utils.Utils;
import com.syshuman.kadir.transform.fft.Complex;
import com.syshuman.kadir.transform.fft.FFT;
import com.syshuman.kadir.transform.view.MyGLRenderer;

import java.nio.FloatBuffer;
import java.util.Arrays;

public class SoundData {

    //private int bufferSize;
    private AudioRecord audioRecord;
    private Thread aThread;

    private int sgn_len;  // 1024
    private int sgn_frq; // 44100
    private int bufferSize;


    private short[] audioData;
    private Complex c_data;
    private Utils utils;
    private boolean drawGraph = false;

    private boolean dyn_amp;
    private String fft_dim;

    private FFT fft;
    private MyGLRenderer renderer;








    public SoundData(MyGLRenderer renderer) {
        this.renderer = renderer;

        aThread = new Thread() {
            @Override
            public void run() {
                try {
                    recordAudio();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        aThread.start();
    }

    public boolean init(Activity activity, int sgn_len, int sgn_frq, boolean dyn_amp, String fft_dim) {

        this.sgn_len = sgn_len;
        this.sgn_frq = sgn_frq;
        this.dyn_amp = dyn_amp; // False default
        this.fft_dim = fft_dim; // 3D default
        audioData = new short[sgn_len];
        c_data = new Complex(sgn_len);
        // g_data = new double[sgn_len][sgn_len][sgn_len];
        utils = new Utils(activity);


        int channel = AudioFormat.CHANNEL_IN_MONO;
        int format = AudioFormat.ENCODING_PCM_16BIT;
        int source = MediaRecorder.AudioSource.MIC;

        bufferSize = AudioRecord.getMinBufferSize(sgn_frq, channel, format);

        if (sgn_len > bufferSize) {
            utils.showError("Signal length must smaller than buffer size. Signal Length : " + sgn_len + " buffer size : " + bufferSize);
            return false;
        }

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE || bufferSize < 0) {
            utils.showError("Buffer size error or AudioRecord error ");
            return false;
        }

        audioRecord = new AudioRecord(source,
                sgn_frq,
                channel,
                format,
                2 * bufferSize);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            utils.showError("Audio could not initialize ");
            return false;
        }
        return true;
    }


    public void start() {

        audioRecord.startRecording();
        drawGraph = true;

    }

    public void stop() {

        audioRecord.stop();
        drawGraph = false;

    }


    private void recordAudio() throws InterruptedException {

        Thread.sleep(50);
        int offset = 0, size;
        while (offset < bufferSize) {
            size = audioRecord.read(audioData, 0, sgn_len);
            offset += size;
        }
        callGraph(audioData);

    }

    private void callGraph(short[] data) {
        float[] dataBuffer;

        dataBuffer = new float[sgn_len * 3];

        for (int i = 0; i < sgn_len; i++) {
            c_data.d_real[i] = data[i];
            c_data.d_imag[i] = 0.0;
        }

        fft = new FFT();
        fft.fft_real(c_data);

        float df = (sgn_frq * 1.0f) / (sgn_len * 1.0f); // 44100 / 1024 = 43.06 Hz  is delta freq

        for (int i = 0; i < sgn_len / 2; i++) {
            double x = i * df * 1.0;
            double y = 0;
            double z = Math.sqrt(c_data.d_real[i] * c_data.d_real[i] + c_data.d_imag[i] * c_data.d_imag[i]) / 1024.0;
            dataBuffer[6 * i + 0] = (float) x;
            dataBuffer[6 * i + 1] = (float) y;
            dataBuffer[6 * i + 2] = (float) z;
            dataBuffer[6 * i + 3] = 0.5f;
            dataBuffer[6 * i + 4] = 0.5f;
            dataBuffer[6 * i + 5] = 0.5f;
        }

        renderer.setData(dataBuffer);
        //renderer.setDrawStatus(true);

    }


}
