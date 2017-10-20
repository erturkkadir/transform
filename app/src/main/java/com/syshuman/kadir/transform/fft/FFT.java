package com.syshuman.kadir.transform.fft;

/**
 * Created by kerturkx on 2017-10-19.
 */

public class FFT {

    public FFT() {
    }

    public void fft_real(Complex c_data) {

        final int n = c_data.d_real.length;
        final double norm = Math.sqrt(1.0 / n);
        for (int i = 0, j = 0; i < n; i++) {
            if (j >= i) {
                double tr = c_data.d_real[j] * norm;
                c_data.d_real[j] = c_data.d_real[i] * norm;
                c_data.d_imag[j] = 0.0;
                c_data.d_real[i] = tr;
                c_data.d_imag[i] = 0.0;
            }

            int m = n / 2;
            while (m >= 1 && j >= m) {
                j -= m;
                m /= 2;
            }
            j += m;
        }

        for (int mmax = 1, istep = 2 * mmax; mmax < n; mmax = istep, istep = 2 * mmax) {
            double delta = (Math.PI / mmax);
            for (int m = 0; m < mmax; m++) {
                double w = m * delta;
                double wr = Math.cos(w);
                double wi = Math.sin(w);
                for (int i = m; i < n; i += istep) {
                    int j = i + mmax;
                    double tr = wr * c_data.d_real[j] - wi * c_data.d_imag[j];
                    double ti = wr * c_data.d_imag[j] + wi * c_data.d_real[j];
                    c_data.d_real[j] = c_data.d_real[i] - tr;
                    c_data.d_imag[j] = c_data.d_imag[i] - ti;
                    c_data.d_real[i] += tr;
                    c_data.d_imag[i] += ti;
                }
            }
        }
    }
}



