typealias Complex = @lib.Complex

fn main {
  // try {
  // let in_str = @fs.read_file_to_string!(path="/dev/stdin")
  // Read the input signal from stdin, one signal per line,
  // where `X,Y` means `X + Yi`.
  // let signals : Array[Complex] = []
  // for line in in_str.split("\n") {
  //   let parts = line.split(",").take(2).to_array()
  //   let real = @strconv.parse_double!(parts[0])
  //   let imag = @strconv.parse_double!(parts[1])
  //   signals.push({ real, imag })
  // }
  let signals = generate_inputs(4096)
  @lib.fft(signals)
  for signal in signals {
    println("\{round(signal.real)},\{round(signal.imag)}")
  }
  // } catch {
  //   _err => abort("ERROR")
  // }
}

fn round(n : Double) -> Double {
  // precision = 2
  (n * 100.0).round() / 100.0
}

fn generate_inputs(len : Int) -> Array[Complex] {
  let res : Array[Complex] = []
  for i in 0..<len {
    let theta = i.to_double() / len.to_double() * @math.pi
    let re = 1.0 * @math.cos(10.0 * theta) + 0.5 * @math.cos(25.0 * theta)
    let im = 1.0 * @math.sin(10.0 * theta) + 0.5 * @math.sin(25.0 * theta)
    res.push({ real: round(re), imag: round(im) })
  }
  res
}
