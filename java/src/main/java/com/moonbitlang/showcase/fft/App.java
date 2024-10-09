package com.moonbitlang.showcase.fft;

public class App {
  public static void main(String[] args) {
    // // Read the input signal from stdin, one signal per line,
    // // where `X,Y` means `X + Yi`.
    // try (var scanner = new Scanner(System.in)) {
    //   var signals =
    //       scanner
    //           .tokens()
    //           .map(
    //               ln -> {
    //                 var parts = ln.split(",");
    //                 return new Complex(Double.parseDouble(parts[0]),
    // Double.parseDouble(parts[1]));
    //               })
    //           .toArray(Complex[]::new);
    var signals = generateInputs(16384);
    CooleyTukey.fft(signals);
    for (var signal : signals) {
      // System.out.printf("%.2f,%.2f\n", signal.real(), signal.imag());
      System.out.printf("%f,%f\n", round(signal.real()), round(signal.imag()));
    }
  }

  private static double round(double n) {
    // precision = 2
    return Math.round(n * 100.0) / 100.0;
  }

  private static Complex[] generateInputs(int len) {
    var res = new Complex[len];
    for (var i = 0; i < len; i++) {
      var theta = (double) i / (double) len * Math.PI;
      var re = 1.0 * Math.cos(10.0 * theta) + 0.5 * Math.cos(25.0 * theta);
      var im = 1.0 * Math.sin(10.0 * theta) + 0.5 * Math.sin(25.0 * theta);
      res[i] = new Complex(round(re), round(im));
    }
    return res;
  }
}
