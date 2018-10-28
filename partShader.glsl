#version 450
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER
#define PI 3.1415926535897932384626433832795


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
//	vec2 repos = vec2(vertTexCoord.x * abs(noise1(vertTexCoord.xy * 0.5) - 0.2), vertTexCoord.y);
//	vec3 texColor = texture2D(texture, repos + (offset.xy / 10000)).rgb * cMod;
//	vec3 texColor2 = texture2D(texture, vertTexCoord.xy + (offset2.xy / 10000)).rgb * cMod * 10;
	float ang = map(vertTexCoord.y, 0, 1, -PI * 2, PI * 2);
	vec2 wavelines = vec2(vertTexCoord.x - (sin(ang * vertTexCoord.x) / (cMod * max(2, midVol / 1.5))), vertTexCoord.y * cos(ang) / distance(vertTexCoord.xy, offset2.xy));
	vec3 mixColors = texture2D(texture, wavelines / lightDist).rgb * lightDist;
	vec3 mixColors2 = texture2D(texture, vec2(map(wavelines.x, 0, 1, 1, 0), wavelines.y)).rgb * lightDist2;
	gl_FragColor = vec4(mix(mixColors, mixColors2, 0.35), 0.35);
//	gl_FragColor = vec4(mixColors2, 0.35);
}
