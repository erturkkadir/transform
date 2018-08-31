package com.syshuman.kadir.transform.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.syshuman.kadir.transform.utils.Utils;
import com.syshuman.kadir.transform.fft.Complex;
import com.syshuman.kadir.transform.fft.FFT;
import com.syshuman.kadir.transform.view.MyGLRenderer;

public class SoundData {

    //private int bufferSize;
    private AudioRecord audioRecord;

    private int sgn_len;  // 1024
    private int sgn_frq; // 44100
    private int bufferSize;

    private int channel;
    private int format;
    private int source;
    private int bytesPerElement = 2; // 2 bytes in 16 bit format

    private float[] dataBuffer;

    private short[] audioData;
    private Complex c_data;
    private Utils utils;
    private boolean drawGraph = false;

    private boolean dyn_amp;
    private String fft_dim;
    private Boolean isRecording = false;
    private Thread recordingThread;

    private FFT fft;
    private Context context;

    public float xMin = 0, xMax = 0, yMin = 0, yMax = 0, zMin = 0, zMax = 0;

    public SoundData(Context context) {
        this.context = context;

    }

    public boolean init(Activity activity, int sgn_len, int sgn_frq, boolean dyn_amp, String fft_dim) {

        this.sgn_len = sgn_len; // 1024 points
        this.sgn_frq = sgn_frq; // 44100 Hz ==> Sample Rate
        this.dyn_amp = dyn_amp; // False default
        this.fft_dim = fft_dim; // 3D default

        dataBuffer = new float[sgn_len];
        for(int i=0; i<sgn_len;i++) dataBuffer[i] = 1.1f;

        audioData = new short[sgn_len];
        c_data = new Complex(sgn_len);

        utils = new Utils(activity);

        channel = AudioFormat.CHANNEL_IN_MONO;
        format = AudioFormat.ENCODING_PCM_16BIT;
        source = MediaRecorder.AudioSource.MIC;

        bufferSize = AudioRecord.getMinBufferSize(sgn_frq, channel, format); /* 3584  44100 Hz, Mono, 16 bit*/

        if (sgn_len > bufferSize) {
            utils.showError("Signal length must smaller than buffer size. Signal Length : " + sgn_len + " buffer size : " + bufferSize);
            return false;
        }

        if ( (bufferSize == AudioRecord.ERROR_BAD_VALUE) || (bufferSize < 0) ) {
            utils.showError("Buffer size error or AudioRecord error ");
            return false;
        }

        startRecording();

        return true;
    }


    private void startRecording() {

        audioRecord =  new AudioRecord(source, sgn_frq, channel, format,bytesPerElement*sgn_len);

        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            utils.showError("Audio could not initialize ");
            return;
        }

        audioRecord.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int size = audioRecord.read(audioData, 0, sgn_len);

            }
        });
        recordingThread.start();
    }


    public void start() {

        audioRecord.startRecording();
        drawGraph = true;

    }

    public void stop() {
        audioRecord.stop();
    }


    private void callGraph(short[] data) {

        for (int i = 0; i < sgn_len; i++) {
            c_data.d_real[i] = data[i];
            c_data.d_imag[i] = 0.0;
        }

        fft = new FFT();
        fft.fft_real(c_data);

        float df = (sgn_frq * 1.0f) / (sgn_len * 1.0f); // 44100 / 1024 = 43.06 Hz  is delta freq

        for (int i = 0; i < sgn_len/2; i++) { /* First Half  0 - 511 */
            double x = i * df * 1.0;
            double y = Math.sqrt(c_data.d_imag[i] * c_data.d_imag[i] + c_data.d_real[i] * c_data.d_real[i]);
            double z = 0; // c_data.d_real[i];
            dataBuffer[7 * i + 0] = (float) x;
            dataBuffer[7 * i + 1] = (float) y;
            dataBuffer[7 * i + 2] = (float) z;
            dataBuffer[7 * i + 3] = 0.0f;
            dataBuffer[7 * i + 4] = 0.0f;
            dataBuffer[7 * i + 5] = 1.0f;
            dataBuffer[7 * i + 6] = 1.0f;
            if(x < xMin) xMin = (float) x; if(x > xMax) xMax = (float) x;
            if(y < yMin) yMin = (float) y; if(y > yMax) yMax = (float) y;
            if(z < zMin) zMin = (float) z; if(z > zMax) zMax = (float) z;

        }
        for(int i = sgn_len/2; i< sgn_len; i++) { /* Second half  411 - 1023 */
            dataBuffer[7 * i + 0] = 0;
            dataBuffer[7 * i + 1] = 0;
            dataBuffer[7 * i + 2] = 0;
            dataBuffer[7 * i + 3] = 0.0f;
            dataBuffer[7 * i + 4] = 0.0f;
            dataBuffer[7 * i + 5] = 1.0f;
            dataBuffer[7 * i + 6] = 1.0f;
        }
    }


    public float[] getData() {
        return dataBuffer;
    }


}
