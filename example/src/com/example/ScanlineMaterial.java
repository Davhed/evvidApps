package com.example;

import com.monyetmabuk.livewallpapers.photosdof.R;

import android.opengl.GLES20;
import rajawali.materials.SimpleMaterial;


public class ScanlineMaterial extends SimpleMaterial {
	
	protected static final String mFShader = 
		"precision highp float;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +

		"uniform float uTime;\n" +
		"uniform vec2 uResolution;\n" +
		"uniform sampler2D uDiffuseTexture;\n" +

//TE scanline effect
//some code by iq, extended to make it look right

		"float rand(vec2 co) {\n" +
		" return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n" +
		"}\n" +
		
		"void main() {\n" +
		" vec2 q = gl_FragCoord.xy / uResolution.xy;\n" +

 // subtle zoom in/out 
		" vec2 uv = 0.5 + (q-0.5)*(0.98 + 0.001*sin(0.95*uTime));\n" +

		" vec3 oricol = texture2D(uDiffuseTexture,vec2(q.x,1.0-q.y)).xyz;\n" +
		" vec3 col;\n" +

 // start with the source texture and misalign the rays it a bit
 // TODO animate misalignment upon hit or similar event
		" col.r = texture2D(uDiffuseTexture,vec2(uv.x+0.003,-uv.y)).x;\n" +
		" col.g = texture2D(uDiffuseTexture,vec2(uv.x+0.000,-uv.y)).y;\n" +
		" col.b = texture2D(uDiffuseTexture,vec2(uv.x-0.003,-uv.y)).z;\n" +

 // contrast curve
 		" col = clamp(col*0.5+0.5*col*col*1.2,0.0,1.0);\n" +

 //vignette
 		" col *= 0.6 + 0.4*16.0*uv.x*uv.y*(1.0-uv.x)*(1.0-uv.y);\n" +

 //color tint
 		" col *= vec3(0.9,1.0,0.7);\n" +

 //scanline (last 2 constants are crawl speed and size)
 //TODO make size dependent on viewport
 		" col *= 0.8+0.2*sin(10.0*uTime+uv.y*900.0);\n" +

 //flickering (semi-randomized)
 		" col *= 1.0-0.07*rand(vec2(uTime, tan(uTime)));\n" +

 //smoothen
//		" float comp = smoothstep( 0.2, 0.7, sin(uTime) );\n" +
//		" col = mix( col, oricol, clamp(-2.0+2.0*q.x+3.0*comp,0.0,1.0) );\n" +

		" gl_FragColor = vec4(col,1.0);\n" +
		
		"}\n";
	
	protected int muTimeHandle;
	protected int muResHandle;
	protected float mTime;
	protected float[] mRes;
	
	public ScanlineMaterial() {
	}
	
	public ScanlineMaterial(int vertexShader, int fragmentShader) {
		super(vertexShader, fragmentShader);
		setShaders();
	}

	public void setTime(float time) {
		mTime = time;
		GLES20.glUniform1f(muTimeHandle, mTime);
	}
	
	public void setTexRes(float width, float height){
		float [] res = new float[] {width, height};
		mRes = res;
		GLES20.glUniform2fv(muResHandle, 1, mRes, 0);
	}
	
	@Override
	public void useProgram() {
		super.useProgram();
	}
	
	@Override
	public void setShaders()
	{
		super.setShaders();
		muTimeHandle = getUniformLocation("uTime");
		muResHandle = getUniformLocation("uResolution");
	}
}
