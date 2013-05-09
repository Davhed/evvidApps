package com.evvid.wallpapers.islandnights;

import android.opengl.GLES20;
import rajawali.materials.AMaterial;


public class WaterMaterial extends AMaterial {
	protected static final String mVShader = 
		"uniform mat4 uMVPMatrix;\n" +
		"uniform mat4 uMMatrix;\n" +
		"uniform vec3 uCameraPosition;\n" +
		"uniform float uTime;\n" +

		"attribute vec4 aPosition;\n" +
		"attribute vec3 aNormal;\n" +
		"attribute vec2 aTextureCoord;\n" +
		"attribute vec4 aColor;\n" +
		
		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +		
		
		"void main() {\n" +
		"	vec4 position = aPosition;\n" +
		"	vec3 normal = aNormal;\n" +
		"   float scale = 0.5;\n" +
		"   float s = sin( (uTime+2.0*position.y)*scale )+sin( (uTime+4.0*position.y)*scale )+sin( (uTime+6.0*position.y)*scale )+sin( (uTime+8.0*position.y)*scale );\n" +
		"   float c = cos( (uTime+4.0*position.x)*scale )+ cos( (uTime+3.0*position.x)*scale )+ cos( (uTime+6.0*position.x)*scale )+ cos( (uTime+2.0*position.x)*scale );\n" +
		"   float z = .5 * s * c;\n" +
		"   vec3 v = position.xyz + vec3(normal.xy * z, 0.0);\n" +
		"   gl_Position = uMVPMatrix * vec4( v, 1.0 );\n" +
		"	vTextureCoord = aTextureCoord;\n" +
		"	vColor = aColor;\n" +
		"}\n";
	
	protected static final String mFShader = 
		"precision mediump float;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +

		"uniform sampler2D uDiffuseTexture;\n" +
		"uniform sampler2D uAlphaTexture;\n" +

		"void main() {\n" +
		"#ifdef TEXTURED\n" +
		"	gl_FragColor = texture2D(uDiffuseTexture, vTextureCoord);\n" +
		"#else\n" +
		"	gl_FragColor = vColor;\n" +
		"#endif\n" +
		
		"#ifdef ALPHA_MAP\n" +
		"	float alpha = texture2D(uAlphaTexture, vTextureCoord).r;\n" +
		"	gl_FragColor.a = alpha;\n" +
		"#else\n" +
		"	gl_FragColor.a = 0.8;\n" +
		"#endif\n" +
		"}\n";
	
	protected int muTimeHandle;
	protected float mTime;

	public void setTime(float time) {
		mTime = time;
	}
	
	@Override
	public void useProgram() {
		super.useProgram();
		GLES20.glUniform1f(muTimeHandle, mTime);
	}
	
	public WaterMaterial() {
		this(mVShader, mFShader);
	}
	
	public WaterMaterial(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader, false);
		setShaders();
		muTimeHandle = getUniformLocation("uTime");
	}
}
