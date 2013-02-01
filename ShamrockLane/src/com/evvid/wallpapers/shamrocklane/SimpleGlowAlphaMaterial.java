package com.evvid.wallpapers.shamrocklane;

import rajawali.materials.SimpleMaterial;


public class SimpleGlowAlphaMaterial extends SimpleMaterial {
	protected static final String mFShader = 
		"precision mediump float;\n" +

		"varying vec2 vTextureCoord;\n" +
		"uniform sampler2D uDiffuseTexture;\n" +
		"uniform sampler2D uAlphaTexture;\n" +
		"varying vec4 vColor;\n" +

		"void main() {\n" +
		
		"    vec2 texel = vTextureCoord;\n" +
		"    float x = texel.x;\n" +
		"    float y = texel.y;\n" +
		
		"    float glow = 60.0 * ((1.0 + 1.0) / 2.0);\n" +
		     
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
		"	gl_FragColor.rgb = bloom.rgb + texture2D(uDiffuseTexture, vTextureCoord).rgb;\n" +
		"	gl_FragColor.a = texture2D(uAlphaTexture, vTextureCoord).r;\n" +
		"#else\n" +
		"	gl_FragColor = bloom + vColor;\n" +
		"#endif\n" +
		"}\n";
	
	public SimpleGlowAlphaMaterial() {
		super(SimpleMaterial.mVShader, mFShader);
		setShaders();
	}
	
	public SimpleGlowAlphaMaterial(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader);
		setShaders();
	}
}
