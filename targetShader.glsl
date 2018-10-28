#version 450
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

#define PROCESSING_TEXTURE_SHADER

in vec4 offsetCoord;
varying vec4 vertTexCoord;
uniform sampler2D texture;
vec4 clr[] = vec4[](
	vec4(0.15, 0.24, 0.14, 0.28),
	vec4(0.16, 0.25, 0.16, 0.28)
	);
float map (float value, float minV, float maxV, float newMin, float newMax){
	float perc = (value - minV) / (maxV - minV);
	float val = perc * (newMax - newMin) + newMin;
	return val;
}

void main() {
	vec4 texColor = vec4(texture2D(texture, offsetCoord.xy).rgb, 0.075);
	vec2 repos = vec2(map(vertTexCoord.x, 0, 1, 1, 0), vertTexCoord.y);
	vec4 texColor2 = vec4(texture2D(texture, repos).rgb, 0.75);
	if(texColor2.b > 0.39){
	gl_FragColor = vec4(map(vertTexCoord.y, 0, 1, 1, 0) / 2.0, 0, 0, 0.579);
	}else if (texColor.r > 0.276){
	gl_FragColor = mix(texColor, vec4(1,1,1,0.0479), 0.005);
	}else if (texColor.g > 0.45 || texColor2.g > 0.46){
	gl_FragColor = clr[int(round(vertTexCoord.x))];
	}
	else{
	gl_FragColor = vec4(vec3(mix(texColor, texColor2, 0.5)), 0.925);
	}
}

