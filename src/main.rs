use anyhow::{Context, Result};
use pretty_assertions::assert_eq;
use rustfft::{algorithm::Radix4, num_complex::Complex, Fft, FftDirection};
use std::{
    env,
    fs::{self, File},
    io::{prelude::*, LineWriter},
    path::Path,
    process::Command,
};

const INPUT_PRECISION: usize = 2;

fn generate_inputs(len: usize) -> Vec<Complex<f64>> {
    let round = |n: f64, m| (n * 10.0_f64.powi(m)).round() / 10.0_f64.powi(m);
    let precision = INPUT_PRECISION as i32;
    (0..len)
        .map(|i| {
            let theta = i as f64 / len as f64 * std::f64::consts::PI;
            let re = 1.0 * (10.0 * theta).cos() + 0.5 * (25.0 * theta).cos();
            let im = 1.0 * (10.0 * theta).sin() + 0.5 * (25.0 * theta).sin();
            Complex {
                re: round(re, precision),
                im: round(im, precision),
            }
        })
        .collect()
}

fn fft(mut buf: Vec<Complex<f64>>) -> Vec<Complex<f64>> {
    let fft = Radix4::new(buf.len().next_power_of_two(), FftDirection::Forward);
    fft.process(&mut buf);
    let factor = 1. / (buf.len() as f64).sqrt();
    for datum in &mut buf {
        *datum *= factor;
    }
    buf
}

fn generate_data(data_dir: &Path, len: usize) -> Result<()> {
    assert!(len.is_power_of_two(), "len must be a power of two");
    eprintln!("INFO: generating test data where size={len:.<8}");

    let inputs = generate_inputs(len);
    {
        let inputs_dir = data_dir.join("inputs");
        let fout = File::create(inputs_dir.join(format!("{len}.dat")))?;
        let mut fout = LineWriter::new(fout);
        for datum in &inputs {
            writeln!(
                fout,
                "{:.p$},{:.p$}",
                datum.re,
                datum.im,
                p = INPUT_PRECISION
            )?;
        }
        fout.flush()?;
    }
    {
        let outputs_dir = data_dir.join("outputs");
        let fout = File::create(outputs_dir.join(format!("{len}.dat")))?;
        let mut fout = LineWriter::new(fout);
        for datum in fft(inputs) {
            writeln!(
                fout,
                "{:.p$},{:.p$}",
                datum.re,
                datum.im,
                p = INPUT_PRECISION
            )?;
        }
        fout.flush()?;
    }
    Ok(())
}

fn main() -> Result<()> {
    let sizes = &[4, 64, 256, 1024, 4096, 16384];

    let cwd = env::current_dir()?;

    eprintln!("INFO: generating test data...");
    let data_dir = cwd.join("data");
    for &size in sizes {
        generate_data(&data_dir, size)?;
    }

    let bins_dir = cwd.join("bins");

    eprintln!("INFO: compiling the Java FFT demo...");
    let java_dir = cwd.join("java");
    let status = Command::new("mvn")
        .arg("package")
        .current_dir(&java_dir)
        .status()?;
    assert!(status.success());

    let jar_path = bins_dir.join("java.jar");
    eprintln!("INFO: copying the .jar to `{}`...", jar_path.display());
    let jar = fs::read_dir(java_dir.join("target"))?
        .find(|e| {
            e.as_ref().is_ok_and(|e| {
                let name = e.file_name();
                let name = name.to_string_lossy();
                name.starts_with("fft") && name.ends_with(".jar")
            })
        })
        .context("jar not found")??;
    fs::copy(jar.path(), &jar_path)?;

    eprintln!("INFO: testing the Java FFT demo...");
    for &size in sizes {
        eprint!("INFO: testing the Java FFT demo where size={size:.<8}");
        let out = Command::new("java")
            .arg("-jar")
            .arg(&jar_path)
            .stdin(File::open(
                data_dir.join("inputs").join(format!("{size}.dat")),
            )?)
            .output()?;
        assert_eq!(
            std::str::from_utf8(&out.stdout),
            std::str::from_utf8(&fs::read(
                data_dir.join("outputs").join(format!("{size}.dat"))
            )?),
        );
        eprintln!("\tOK");
    }

    Ok(())
}
