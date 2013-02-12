package com.example;

import rajawali.materials.AMaterial;


public class TestMaterial extends AMaterial {
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
	
		"uniform vec2 vTextureCoord;\n" +
		"void main( void ) {\n" +
	
		"vec2 position = ( gl_FragCoord.xy -  vTextureCoord.xy*.5 ) / vTextureCoord.x;\n" +
		// 256 angle steps
		"float angle = atan(position.y,position.x)/(1.*3.14159265359);\n" +
		"angle -= floor(angle);\n" +
		"float rad = length(position);\n" +
	
		"float color = 0.0;\n" +
		"for (int i = 0; i < 10; i++) {\n" +
		"    float angleFract = fract(angle*256.);\n" +
		"    float angleRnd = floor(angle*256.)+1.;\n" +
		"    float angleRnd1 = fract(angleRnd*fract(angleRnd*.7235)*45.1);\n" +
		"    float angleRnd2 = fract(angleRnd*fract(angleRnd*.82657)*13.724);\n" +
		"    float t = angleRnd1*10.;\n" +
		"    float radDist = sqrt(angleRnd2+float(i));\n" +
	
		"    float adist = radDist/rad*.1;\n" +
		"    float dist = (t*.1+adist);\n" +
		"    dist = abs(fract(dist)-.5);\n" +
		"    color += max(0.,.5-dist*40./adist)*(.5-abs(angleFract-.5))*5./adist/radDist;\n" +
	
		"    angle = fract(angle+.61);\n" +
		"}" +
	
		"gl_FragColor = vec4( color )*.1;\n" +
	
		"gl_FragColor = vec4( color )*.1;\n" +
		"}\n";
	
	public TestMaterial() {
		super(mVShader, mFShader, false);
		setShaders();
	}
	
	public TestMaterial(String vertexShader, String fragmentShader) {
		super(vertexShader, fragmentShader, false);
		setShaders();
	}
}
