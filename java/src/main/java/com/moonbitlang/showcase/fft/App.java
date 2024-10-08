package com.moonbitlang.showcase.fft;

import java.util.Scanner;

public class App {
  public static void main(String[] args) {
    // Read the input signal from stdin, one signal per line,
    // where `X,Y` means `X + Yi`.
    try (var scanner = new Scanner(System.in)) {
      var signals =
          scanner
              .tokens()
              .map(
                  ln -> {
                    var parts = ln.split(",");
                    return new Complex(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                  })
              .toArray(Complex[]::new);
      CooleyTukey.fft(signals);
      for (var signal : signals) {
        System.out.printf("%.2f,%.2f\n", signal.real(), signal.imag());
      }
    }
  }
}
