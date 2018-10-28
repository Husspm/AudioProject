package audioGLSL;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.opengl.PJOGL;
import processing.opengl.PShader;
import processing.video.Movie;

public class AudioProject extends PApplet {
	Audio IO;
	PShader shade, shade2, shade3, shade4, bcsShade, mirror, mirror2;
	Movie film;
	Particle p, p2;
	PGraphics target, subTarget, finalTarget;
	PImage img, overlay;
	public float rotOff = 0;
	public boolean open = true;
	public int pulses = 0;
	public int selector = 1;
	public int shadeSwitch = 1;
	public int layerOneSwitch = 0;
	public int layerTwoSwitch = 0;
	public int finalRenderSwitch = 0;
	public float sum, bass, mid, hi;
	public boolean useOverlay = true;

	public void settings() {
//		delay(5000);
		size(1280, 720, P3D);
		PJOGL.profile = 4;
	}

	public void setup() {
//		frameRate(60);
		film = new Movie(this, "film.mp4");
		target = createGraphics(width, height, P3D);
		subTarget = createGraphics(width, height, P3D);
		finalTarget = createGraphics(width, height, P3D);
		img = loadImage("screen-0068.tif");
		img.resize(width, height);
		overlay = loadImage("overlay.png");
		shade = loadShader("partShader2.glsl");
		shade2 = loadShader("targetShader.glsl", "targetVert.vert");
		shade3 = loadShader("partShader.glsl");
		shade4 = loadShader("targetShader2.glsl", "targetVert.vert");
		bcsShade = loadShader("brcosa.glsl");
		mirror = loadShader("mirrorImage.glsl");
		mirror2 = loadShader("mirrorImageVertical.glsl");
		IO = new Audio(this);
		film.loop();
		film.volume(0);
		p = new Particle(this);
		p2 = new Particle(this);
	}

	public void movieEvent(Movie m) {
		m.read();
	}

	public int DIR(int t) {
		return floor(random(t)) + 1 == t ? -1 : 1;
	}

	public void routineOne() {
		IO.getFFT();
		sum = IO.getVol() * 10;
		rotOff += 0.00001;
		target.beginDraw();
		bass = IO.getBass(18);
		mid = IO.getMid(55);
		hi = IO.getHi(40);
		surface.setTitle("FPS: " + floor(frameRate) + " res: " + width + "x" + height + " bassSum: " + bass
				+ " midSum: " + mid + " hiSum " + hi + " volSum: " + sum);
		p.move(bass);
		p2.move(mid / 15);
		this.layerOne(layerOneSwitch);
		this.layerTwo(layerTwoSwitch);
		this.finalRender(finalRenderSwitch);
//		resetShader();
//		image(overlay, 0, -200);
	}

	public void routineTwo() {
		background(0);
		textSize(30);
		text("PLEASE WAIT", random(width), random(height));
		if (film.available() && img.isLoaded() && IO.init) {
			selector = 0;
		}
	}

	public void layerOne(int L1) {
		switch (L1) {
		case 0:
			shade3.set("cMod", sum);
			shade3.set("midVol", mid);
			shade3.set("offset", p.pos);
			shade3.set("offset2", p2.pos);
			target.shader(shade3);
			float wMod = noise(rotOff) * (sum > 1 ? sum : 1) + 1;
			target.image(target, 0, 0, width, height);
//			target.filter(bcsShade);
			target.image(img, 0, 0, width * wMod, height);
			target.image(film, 0, 0, width, height);
			target.endDraw();
			break;
		case 1:
			shade.set("cMod", sum);
			shade.set("midVol", mid);
			shade.set("offset", p.pos);
			shade.set("offset2", p2.pos);
			target.shader(shade);
			float wMod1 = noise(rotOff) * (sum > 1 ? sum : 1) + 1;
			target.image(target, 0, 0, width, height);
//			target.filter(bcsShade);
			target.image(img, 0, 0, width * wMod1, height);
			target.image(film, 0, 0, width, height);
			target.endDraw();
			break;
		}
	}
	public void layerTwo(int L2) {
		switch(L2) {
		case 0:
			subTarget.shader(shade4);
			shade4.set("transformMatrix", (PMatrix3D) target.getMatrix());
			shade4.set("texMatrix", (PMatrix3D) target.getMatrix());
			shade4.set("depthMod", sum / 100);
			shade4.set("transMod", bass, mid / 10);
			subTarget.beginDraw();
			subTarget.image(target, 0, 0, width, height);
			subTarget.endDraw();
			finalTarget.beginDraw();
			bcsShade.set("contrast", hi);
			finalTarget.shader(bcsShade);
			finalTarget.image(subTarget, 0, 0);
			finalTarget.endDraw();
			break;
		case 1:
			subTarget.shader(shade2);
			shade2.set("transformMatrix", (PMatrix3D) target.getMatrix());
			shade2.set("texMatrix", (PMatrix3D) target.getMatrix());
			shade2.set("depthMod", sum / 100);
			shade2.set("transMod", bass, mid / 10);
			subTarget.beginDraw();
			subTarget.image(target, 0, 0, width, height);
			subTarget.endDraw();
			finalTarget.beginDraw();
			bcsShade.set("contrast", hi);
			finalTarget.shader(bcsShade);
			finalTarget.image(subTarget, 0, 0);
			finalTarget.endDraw();
			break;
		}
	}
	public void finalRender(int FR) {
		switch (FR) {
		case 0: // mirror horizontal
			resetShader();
			image(finalTarget, 0, 0, width / 2, height);
			shader(mirror);
			image(finalTarget, width / 2, 0, width / 2, height);
			break;
		case 1: // right side upside down
			image(finalTarget, 0, 0, width / 2, height);
			pushMatrix();
			translate(width, height);
			rotate(-PI);
			image(finalTarget, 0, 0, width / 2, height);
			popMatrix();
			break;
		case 2: // fullScreen
			filter(mirror);
			image(finalTarget, 0, 0, width, height);
			break;
		case 3: // mirror vertical
			resetShader();
			image(finalTarget, 0, 0, width, height / 2);
			shader(mirror2);
			image(finalTarget, 0, height / 2, width, height / 2);
			break;
		case 4:
			resetShader();
			image(finalTarget, 0, 0, width / 2, height / 2);
			resetShader();
			shader(mirror);
			image(finalTarget, width / 2, 0, width / 2, height / 2);
			resetShader();
			pushMatrix();
			translate(width / 2, height);
			rotate(-PI);
			shader(mirror);
			image(finalTarget, 0, 0, width / 2, height / 2);
			popMatrix();
			resetShader();
			pushMatrix();
			translate(width, height);
			rotate(-PI);
//			shader(mirror2);
			image(finalTarget, 0, 0, width / 2, height / 2);
			popMatrix();
			break;
		}
		overlayExists(useOverlay);
	}
	public void overlayExists(boolean yes) {
		if (yes) {
			resetShader();
			image(overlay, 0, 0, width, overlay.height);
		}else {
			return;
		}
	}
	public void draw() {
		switch (selector) {
		case 0:
			this.routineOne();
			break;
		case 1:
			this.routineTwo();
			break;
		}
	}

	public void keyPressed() {
		switch (key) {
		case 'a':
			if (layerOneSwitch == 1) {
				layerOneSwitch = 0;
			} else {
				layerOneSwitch = 1;
			}
			break;
		case 'z':
			if (layerTwoSwitch == 1) {
				layerTwoSwitch = 0;
			} else {
				layerTwoSwitch = 1;
			}
			break;
		case 's':
			finalRenderSwitch++;
			if (finalRenderSwitch > 4) {
				finalRenderSwitch = 0;
			}
			break;
		case 'o':
			useOverlay = !useOverlay;
		}
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { AudioProject.class.getName() });
	}
}