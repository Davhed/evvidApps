package com.example;

import rajawali.materials.AMaterial;
import rajawali.math.Number3D;
import android.graphics.Color;
import android.opengl.GLES20;


public class TintMaterial extends AMaterial {
	protected static final String mVShader = 
		"uniform mat4 uMVPMatrix;\n" +

		"attribute vec4 aPosition;\n" +
		"attribute vec2 aTextureCoord;\n" +
		"attribute vec4 aColor;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +

		"void main() {\n" +
		"	gl_Position = uMVPMatrix * aPosition;\n" +
		"	vTextureCoord = aTextureCoord;\n" +
		"	vColor = aColor;\n" +
		"}\n";

	protected static final String mFShader = 
		"precision mediump float;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +
		"uniform vec4 uTintColor;\n" +
		
		"uniform sampler2D uDiffuseTexture;\n" +
		"uniform sampler2D uAlphaTexture;\n" +
		
		"void main() {\n" +
		"#ifdef TEXTURED\n" +
		"	vec4 diffuse = texture2D(uDiffuseTexture, vTextureCoord);\n" +
		"	gl_FragColor = diffuse * uTintColor;\n" +
		
		"#else\n" +
		"	gl_FragColor = vColor;\n" +
		"#endif\n" +

		"#ifdef ALPHA_MAP\n" +
		"	float alpha = texture2D(uAlphaTexture, vTextureCoord).r;\n" +
		"	gl_FragColor.a = alpha;\n" +
		"#endif\n" +
		"}\n";

	

	protected int muTintColorHandle;
	protected float[] mTintColor;
	
	public TintMaterial() {
		this(mVShader, mFShader);
		setShaders();
		
	}

	public TintMaterial(String vertexShader, String fragmentShader) 
	{
		super(vertexShader, fragmentShader, false);
		mTintColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	}
	@Override
	public void useProgram() {
		super.useProgram();
		GLES20.glUniform4fv(muTintColorHandle, 1, mTintColor, 0);
		
	}

	public void setTintColor(float[] color) {
		mTintColor = color;
	}
	public void setTintColor(Number3D color) {
		mTintColor[0] = color.x;
		mTintColor[1] = color.y;
		mTintColor[2] = color.z;
		mTintColor[3] = 1;
	}
	public void setTintColor(float r, float g, float b, float a) {
		setTintColor(new float[] { r, g, b, a });
	}

	public void setTintColor(int color) {
		setTintColor(new float[] { Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color) });
	}

	public void setShaders(String vertexShader, String fragmentShader) {
		super.setShaders(vertexShader, fragmentShader);
		muTintColorHandle = getUniformLocation("uTintColor");
		
		
	}
	
}
