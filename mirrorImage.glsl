#version 450
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

varying vec4 vertTexCoord;
uniform sampler2D texture;

float map (float value, float minV, float maxV, float newMin, float newMax){
	float perc = (value - minV) / (maxV - minV);
	float val = perc * (newMax - newMin) + newMin;
	return val;
}

void main() {
	vec3 texColor = texture2D(texture, vec2(map(vertTexCoord.x, 0, 1, 1, 0), vertTexCoord.y)).rgb;
	gl_FragColor = vec4(texColor, 0.61);
}


