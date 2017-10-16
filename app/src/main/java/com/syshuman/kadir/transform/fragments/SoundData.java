package com.syshuman.kadir.transform.fragments;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class SoundData {

    int bufferSize;
    AudioRecord audioRecord;

    short[] audioBuffer;
    private boolean shouldContinue = true;

    Thread aThread;



    public SoundData()  {


    }

    public void init(int sample_rate, int channel, int encoding) {


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

                long shortsRead = 0;
                while (shouldContinue) {
                    int numberOfShort = audioRecord.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;
                    // Do something with the audioBuffer
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
