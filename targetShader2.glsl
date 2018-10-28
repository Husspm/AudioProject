#version 450
#ifdef GL_ES
precision lowp float;
precision lowp int;
#endif

#define PROCESSING_TEXTURE_SHADER
#define PI 3.1415926535897932384626433832795
varying vec4 vertTexCoord;
varying vec4 offsetCoord;
varying vec4 vertColor;
uniform sampler2D texture;
uniform sampler2D texture2;
uniform float depthMod;
uniform vec2 transMod;
vec4 clr[] = vec4[](
	vec4(0.171, 0.0, 0.084, 0.0818),
	vec4(0.271, 0.0, 0.074, 0.0818)
	);
const vec2 offset = vec2(1 / 1280, 1 / 720);

float map (float value, float minV, float maxV, float newMin, float newMax){
	float perc = (value - minV) / (maxV - minV);
	float val = perc * (newMax - newMin) + newMin;
	return val;
}


void quiet(float col){
float trans = 0;
//	gl_FragDepth = map(col, 0, 1, -10, 10);
	if(col < 0.3){
		col = map(col, 0, 0.299, 0.125, 1);
		trans = map(transMod.y, 0, 2, 0, 0.5);
		gl_FragColor = vec4(col, col, col, trans);
	}else if(col <  0.6){
		trans = map(transMod.y, 0, 3, 0, 0.5);
		col = abs(map(col, 0.3, 0.599, -1, 1));
		gl_FragColor = vec4(col, col / 3, col / 9, trans);
	}else{
	trans = map(transMod.y, 0, 4, 0, 0.5);
		col = map(col, 0.6, 1, 0, 0.6);
		gl_FragColor = vec4(col, col, col, trans);
	}
}
void main() {
	vec2 pos = gl_FragCoord.xy * (offset * transMod);
	vec4 texColor = vec4(texture(texture, vertTexCoord.xy).rgb, 0.75);
	float col = max(max(texColor.r, texColor.g), texColor.b);
	if (transMod.y < (0.7)){
		quiet(col);
	}else{
	float ang = map(vertTexCoord.x, 0, 1, -PI, PI);
	vec3 altColor = texture(texture, vec2(offsetCoord.x + sin(ang), offsetCoord.y + cos(ang))).rgb - texture(texture2, pos).rgb * sin(transMod.y);
	vec2 repos = vec2(map(vertTexCoord.x, 0, 1, 1, 0), vertTexCoord.y);
	vec4 texColor2 = vec4(texture(texture, texColor.xy).rgb, 0.75);
	vec3 altColor2 = texColor.rgb - texColor2.rgb;
	gl_FragDepth = abs(noise1(vertTexCoord)) * depthMod;
	if(texColor.b < 0.201){
		float col = map(texColor.b, 0, 0.201, 0.4, 0.6);
		gl_FragColor = vec4(col, col, col, 0.951) * transMod.y;
	}else if (texColor.r > 0.28){
		gl_FragColor = vec4(altColor2 * 10, 0.821) * (transMod.x * 2);
	}else if (texColor.g > 0.45 || texColor2.g > 0.46){
		gl_FragColor = clr[int(round(vertTexCoord.x))] * sin(transMod.y);
	}else{
		gl_FragColor = vec4(vec3(mix(sin(texColor), tan(texColor2), 0.5)), 0.935);
	}
	}
}

