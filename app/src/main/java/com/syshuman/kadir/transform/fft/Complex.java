package com.syshuman.kadir.transform.fft;


public class Complex {
    
    public double[] d_real;
    public double[] d_imag;

    public Complex(int sgn_len) {
        d_real = new double[sgn_len];
        d_imag = new double[sgn_len];
    }

}
