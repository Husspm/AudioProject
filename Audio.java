package audioGLSL;

import processing.core.PApplet;
import processing.sound.Amplitude;
import processing.sound.AudioIn;
import processing.sound.FFT;
import processing.sound.Sound;


public class Audio extends Sound{
	public int bands = 1024;
	public float fftSmooth = 0.65f;
	public float volSmooth = 0.45f;
	private float [] sums = new float[bands];
	private float newVol = 0;
	private AudioIn [] in = new AudioIn[2];
	private Amplitude vol;
	private FFT fft;
	private PApplet p;
	public boolean init = false;
	public Audio(PApplet p){
		super(p);
		this.p = p;
		in[0] = new AudioIn(this.p, 0);
		in[1] = new AudioIn(this.p, 1);
		vol = new Amplitude(this.p);
		fft = new FFT(this.p, bands);
		in[0].start();
		in[1].start();
		vol.input(in[0]);
		fft.input(in[1]);
		init = true;
		this.p.displayDensity(4);
	}
	public float getVol() {
		return newVol += (vol.analyze() - newVol) * volSmooth;
	}
	public void getFFT() {
		fft.analyze();
		for (int i  = 0; i < sums.length; i++) {
			sums[i] += (fft.spectrum[i] - sums[i]) * fftSmooth;
		}
	}
	public float getBass(int width) {
		float bass = 0;
		for (int i = 2; i < 2 + width; i++) {
			bass += sums[i];
		}
		return bass;
	}
	public float getMid(int width) {
		float mid = 0;
		for (int i = 20; i < 20 + width; i++) {
			mid += sums[i] * 10;
		}
		return mid;
	}
	public float getHi(int width) {
		float hi = 0;
		for (int i = 140; i < 140 + width; i++) {
			hi += sums[i] * 10;
		}
		return hi;
	}
}