package com.evvid.wallpapers.shamrocklane;

import android.opengl.GLES20;
import rajawali.materials.AMaterial;
import rajawali.math.Number3D;


public class TunnelMaterial extends AMaterial {
	protected static final String mVShader = 
		"precision mediump float;\n" +
	
		"uniform mat4 uMVPMatrix;\n" +
		"uniform float uTime;\n" +
		"uniform float uTunnelLength;\n" +
		"uniform vec2 uZDiv;\n" +

		"attribute vec4 aPosition;\n" +
		"attribute vec2 aTextureCoord;\n" +
		"attribute vec4 aColor;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +		
		"varying float AO;\n" +		
		
		"void main() {\n" +
		"   vec4 pos = aPosition;\n" +
		"   pos.x += cos(time + (aPosition.z/uZDiv.x));\n" +
		"   pos.y += sin(time + (aPosition.z/uZDiv.y));\n" +

		"   AO = (((pos.z*2.0) + uTunnelLength) * 0.5) / uTunnelLength;\n" +
		"   gl_Position = uMVPMatrix * pos;\n" +
		"   vec2 uv = aTextureCoord * 4.0;\n" +
		"   uv.y -= uTime*0.2;\n" +
		"   uv.x += uTime*0.1;\n" +
		"	vTextureCoord = uv;\n" +
		
		"	vColor = aColor;\n" +
		"}\n";
	
	protected static final String mFShader = 
		"precision mediump float;\n" +

		"uniform sampler2D uDiffuseTexture;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +
		"varying float AO;\n" +

		"void main() {\n" +
		
		"   vec2 uv = vTextureCoord.xy;\n" +
		
		"#ifdef TEXTURED\n" +
		"   vec3 diffuse = texture2D(uDiffuseTexture, vTextureCoord).rgb * AO;\n" +
		"   gl_FragColor = vec4(diffuse, 1.0);\n" +
		"#else\n" +
		"   vec3 diffuse = vColor.rgb * AO;\n" +
		"   gl_FragColor = vec4(diffuse, 1.0);\n" +
		"#endif\n" +
		"}\n";
	
	protected int muTimeHandle;
	protected int muTunnelLengthHandle;
	protected int muZDivHandle;
	
	protected float mTime;	
	protected float mTunnelLength;	
	protected float[] mZDiv;	

	public TunnelMaterial() {
		super(mVShader, mFShader, false);
		setShaders();
	}
	
	public TunnelMaterial(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader, false);
		setShaders();
	}
	
	public void setTime(float time) {
		mTime = time;
		GLES20.glUniform1f(muTimeHandle, mTime);
	}

	public void setTunnelLength(float length) {
		mTunnelLength = length;
		GLES20.glUniform1f(muTunnelLengthHandle, mTunnelLength);
	}

	public void setZDiv(Number3D div) {
		mZDiv[0] = div.x;
		mZDiv[1] = div.y;
		GLES20.glUniform2fv(muZDivHandle, 1, mZDiv, 0);
	}

}
