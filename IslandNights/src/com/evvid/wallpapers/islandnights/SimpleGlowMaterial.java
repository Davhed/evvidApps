package com.evvid.wallpapers.islandnights;

import rajawali.materials.AMaterial;


public class SimpleGlowMaterial extends AMaterial {
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
		"uniform sampler2D uDiffuseTexture;\n" +
		"varying vec4 vColor;\n" +

		"void main() {\n" +
		
		"    vec2 texel = vTextureCoord;\n" +
		"    float x = texel.x;\n" +
		"    float y = texel.y;\n" +
		
		"    float glow = 2.0 * ((0.5 + 0.5) / 2.0);\n" +
		     
		"    vec4 bloom = vec4(0);\n" +
		     
		"    float count = 0.0;\n" +
		    
		"            x = texel.x - glow;\n" +
		"            y = texel.y - glow;\n" +
		"            bloom += (texture2D(uDiffuseTexture, vec2(x, y)) - 0.4) * 30.0;\n" +
		"            count += 1.0;\n" +
		"            y += 1.0;\n" +
		"            bloom += (texture2D(uDiffuseTexture, vec2(x, y)) - 0.4) * 30.0;\n" +
		"            count += 1.0;\n" +
		"            y += 1.0;\n" +
		"            bloom += (texture2D(uDiffuseTexture, vec2(x, y)) - 0.4) * 30.0;\n" +
		"            count += 1.0;\n" +
		"            y += 1.0;\n" +
		"            bloom += (texture2D(uDiffuseTexture, vec2(x, y)) - 0.4) * 30.0;\n" +
		"            count += 1.0;\n" +
		"            y += 1.0;\n" +
		"    bloom = clamp(bloom / (count * 30.0), 0.0, 1.0);\n" +
		
		"#ifdef TEXTURED\n" +
		"	gl_FragColor = bloom + texture2D(uDiffuseTexture, vTextureCoord);\n" +
		"#else\n" +
		"	gl_FragColor = bloom + vColor;\n" +
		"#endif\n" +
		"}\n";
	
	public SimpleGlowMaterial() {
		super(mVShader, mFShader, false);
		setShaders();
	}
	
	public SimpleGlowMaterial(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader, false);
		setShaders();
	}
}
