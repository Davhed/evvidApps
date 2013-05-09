precision highp float;

varying vec2 vTextureCoord;
varying vec4 vColor;

uniform float uTime;
uniform vec2 uResolution;
uniform sampler2D uDiffuseTexture;

//TE scanline effect
//some code by iq, extended to make it look right

float rand(vec2 co) {
 return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}
		
void main() {
 vec2 q = gl_FragCoord.xy / uResolution.xy;

 // subtle zoom in/out 
 vec2 uv = 0.5 + (q-0.5)*(0.98 + 0.001*sin(0.95*uTime));

 vec3 oricol = texture2D(uDiffuseTexture,vec2(q.x,1.0-q.y)).xyz;
 vec3 col;

 // start with the source texture and misalign the rays it a bit
 // TODO animate misalignment upon hit or similar event
 col.r = texture2D(uDiffuseTexture,vec2(uv.x+0.003,-uv.y)).x;
 col.g = texture2D(uDiffuseTexture,vec2(uv.x+0.000,-uv.y)).y;
 col.b = texture2D(uDiffuseTexture,vec2(uv.x-0.003,-uv.y)).z;

 // contrast curve
  col = clamp(col*0.5+0.5*col*col*1.2,0.0,1.0);

 //vignette
  col *= 0.6 + 0.4*16.0*uv.x*uv.y*(1.0-uv.x)*(1.0-uv.y);

 //color tint
  col *= vec3(0.9,1.0,0.7);

 //scanline (last 2 constants are crawl speed and size)

  col *= 0.8+0.2*sin(10.0*uTime+uv.y*900.0);

 //flickering (semi-randomized)
  col *= 1.0-0.07*rand(vec2(uTime, tan(uTime)));


 gl_FragColor = vec4(col,1.0);
		
}
	