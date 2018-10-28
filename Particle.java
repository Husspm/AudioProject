package audioGLSL;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

class Particle {
	  public PVector pos, acc, vel;
	  private float noiseScale = 0.0005f;
	  private float off;
	  private PApplet p;
	  Particle(PApplet p) {
		this.p = p;
	    pos = new PVector (p.width / 2, p.height / 2);
	    acc = new PVector (p.random(0.3f, 0.6f), p.random(0.7f, 0.97f));
	    off = 0;
	  }
	  void move(float vol) {
	    off += 0.001;
	    pos.z += 0.001;
	    float angle = p.noise(pos.x * noiseScale, pos.y * noiseScale, off);
	    PVector newAcc = acc.copy().mult(vol);
	    newAcc.rotate(PApplet.map(angle, 0, 1, -PConstants.PI, PConstants.PI));
	    pos.add(newAcc);
	    if (pos.x > p.width || pos.x < 0) {
	      pos.x = p.width / 2;
	    }
	    if (pos.y > p.height || pos.y < 0) {
	      pos.y = p.height / 2;
	      acc.y *= -1;
	    }
	  }
}