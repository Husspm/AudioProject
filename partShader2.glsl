#version 450
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER


varying vec4 vertTexCoord;
uniform sampler2D texture;
uniform float cMod;
uniform float midVol;
uniform vec3 offset;
uniform vec3 offset2;
uniform vec3 [] off;

float map (float value, float minV, float maxV, float newMin, float newMax){
	float perc = (value - minV) / (maxV - minV);
	float val = perc * (newMax - newMin) + newMin;
	return val;
}

void main() {
	gl_FragDepth = max(distance(vertTexCoord.xy, offset.xy), distance(vertTexCoord.xy, offset2.xy));
	float lightDist =  normalize(distance(vertTexCoord.xy, offset.xy));
	float lightDist2 =  normalize(distance(vertTexCoord.xy, offset2.xy));
	vec2 repos = vec2(vertTexCoord.x * abs(noise1(vertTexCoord.xy * 0.5) + 0.79), vertTexCoord.y * 0.62 - (cMod / 100));
	vec3 texColor = texture2D(texture, repos + (offset.xy / 10000) / lightDist).rgb * (midVol * lightDist / 6);
	vec3 texColor2 = texture2D(texture, repos + (offset2.xy / 10000) / lightDist2).rgb * (cMod * lightDist2 / 4);
	gl_FragColor = vec4(mix(texColor,texColor2, cMod *4), 0.0925 );
}


