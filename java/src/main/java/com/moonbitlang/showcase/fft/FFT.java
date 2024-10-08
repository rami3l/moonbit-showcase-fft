package com.moonbitlang.showcase.fft;

import java.util.Arrays;

/**
 * The utility class implementing the Fast Fourier Transform (FFT) algorithm, as
 * described in E.
 * Oran Brigham's book "The Fast Fourier Transform and its Applications" (1988).
 */
public class FFT {
  /**
   * Performs the Fast Fourier Transform (FFT) algorithm.
   *
   * <p>
   * Originally the `FFT()` function from P147 of the book.
   *
   * @param ins the input signal, each item being an double[2] for its real and
   *            imaginary parts
   * @return the FFT of the input signal
   */
  public static double[][] fft(final double[][] ins) {
    var n = ins.length;
    var outs = arrayCopy2D(ins);

    var ld = Math.log(n) / Math.log(2);
    assert ld % 1 == 0 : "The input signal length must be a power of 2.";
    var nu = (int) ld;
    var n2 = n / 2;
    var nu1 = nu - 1;

    for (var l = 1; l <= nu; l++) {
      for (var k = 0; k < n; k += n2) {
        for (var i = 1; i <= n2; i++) {
          var m = k >> nu1;
          var p = bitReverse(m, nu);
          var arg = 2 * Math.PI * p / n;
          var c = Math.cos(arg);
          var s = Math.sin(arg);
          var tReal = ins[k + n2][0] * c + ins[k + n2][1] * s;
          var tImag = -ins[k + n2][0] * s + ins[k + n2][1] * c;
          outs[k + n2][0] = ins[k][0] - tReal;
          outs[k + n2][1] = ins[k][1] - tImag;
          outs[k][0] += tReal;
          outs[k][1] += tImag;
          k++;
        }
      }
      nu1--;
      n2 /= 2;
    }

    for (var k = 0; k < n; k++) {
      var r = bitReverse(k, nu);
      if (r > k) {
        arraySwap(outs, k, r);
      }
    }

    var radix = 1 / Math.sqrt(n); // Normalization factor
    for (var i = 0; i < n; i++) {
      outs[i][0] *= radix;
      outs[i][1] *= radix;
    }
    return outs;
  }

  /**
   * The bit reverse function.
   *
   * <p>
   * Originally the `IBITR()` function from P147 of the book.
   */
  private static int bitReverse(int j, int nu) {
    var k = 0;
    for (var i = 1; i <= nu; i++) {
      var j2 = j / 2;
      k = 2 * k + (j - 2 * j2);
      j = j2;
    }
    return k;
  }

  public static void arraySwap(double[][] arr, int i, int j) {
    var temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  public static double[][] arrayCopy2D(double[][] arr) {
    var copy = new double[arr.length][];
    for (int i = 0; i < arr.length; i++) {
      copy[i] = Arrays.copyOf(arr[i], arr[i].length);
    }
    return copy;
  }
}
