struct Complex {
  real : Double
  image : Double
}

fn Complex::add(self : Complex, other : Complex) -> Complex {
  { real: self.real + other.real, image: self.image + other.image }
}

fn Complex::sub(self : Complex, other : Complex) -> Complex {
  { real: self.real - other.real, image: self.image - other.image }
}

fn Complex::mul(self : Complex, other : Complex) -> Complex {
  {
    real: self.real * other.real - self.image * other.image,
    image: self.real * other.image + self.image * other.real,
  }
}

fn Complex::mul_scalar(self : Complex, scalar : Double) -> Complex {
  { real: self.real * scalar, image: self.image * scalar }
}
