package com.syshuman.kadir.transform.model;

import static java.lang.Math.exp;
import static java.lang.Math.round;

/**
 * Created by kerturkx on 2017-10-18.
 */

public class MyFunction {

    public MyFunction() {

    }


    public int[][] getData() {
        int N = 512;
        int data[][] = new int[N][N];

        for(int i=0; i<N; i++) {
            for(int j=0; j<N; j++) {
                float x = (i - N / 2.0f) / (N / 2.0f);
                float y = (j - N / 2.0f) / (N / 2.0f);
                float t = (float) Math.sqrt(x*x + y*y) * 4.0f;
                float z = (1.0f - t * t) * (float) exp(t * t / -2.0f);
                data[i][j] = round(z * 127 + 128);
            }
        }
        return data;
    }
}
