package com.syshuman.kadir.transform.fft;

/**
 * Created by kerturkx on 2017-10-19.
 */

public class Complex {
    
    public double d_real[];
    public double d_imag[];

    public Complex(short[] data) {
        d_real = new double[data.length];
        d_imag = new double[data.length];
        for(int i=0; i<data.length; i++) {
            d_real[i] = (double) data[i];
            d_imag[i] = 0.0;
        }
    }

}
