package rajawali.materials;

import android.opengl.GLES20;

public class SimpleAnimatedMaterial extends AMaterial {
	protected static final String mVShader = 
		"uniform mat4 uMVPMatrix;\n" +
		
		"#ifdef ANIMATED\n" +
		"uniform float uCurrentFrame;\n" +
		"uniform float uTileSize;\n" +
		"uniform float uNumTileRows;\n" +
		"#endif\n" +

		"attribute vec4 aPosition;\n" +
		"attribute vec2 aTextureCoord;\n" +
		"attribute vec4 aColor;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +		
		
		"void main() {\n" +
		"	gl_Position = uMVPMatrix * aPosition;\n" +
		"	#ifdef ANIMATED\n" +
		"		vTextureCoord.s = mod(uCurrentFrame, uNumTileRows) * uTileSize;" +
		"		vTextureCoord.t = uTileSize * floor(uCurrentFrame  / uNumTileRows);\n" +
		"	#else\n" +
		"		vTextureCoord = aTextureCoord;\n" +
		"	#endif\n" +
		"	vColor = aColor;\n" +
		"}\n";
	
	protected static final String mFShader = 
		"precision mediump float;\n" +

		"uniform sampler2D uDiffuseTexture;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying vec4 vColor;\n" +

		"#ifdef ANIMATED\n" +
		"	uniform float uTileSize;\n" +
		"	uniform float uNumTileRows;\n" +
		"#endif\n" +

		"void main() {\n" +
		"#ifdef TEXTURED\n" +
		"	#ifdef ANIMATED\n" +
		"		vec2 realTexCoord = vTextureCoord + (vTextureCoord / uNumTileRows);" +
		"		gl_FragColor = texture2D(uDiffuseTexture, realTexCoord);\n" +
		"	#else\n" +
		"		gl_FragColor = texture2D(uDiffuseTexture, vTextureCoord);\n" +
		"	#endif\n" +
		"#else\n" +
		"	gl_FragColor = vColor;\n" +
		"#endif\n" +
		"}\n";
	
	protected int muCurrentFrameHandle;
	protected int muTileSizeHandle;
	protected int muNumTileRowsHandle;
	
	protected int mCurrentFrame;
	protected float mTileSize;
	protected float mNumTileRows;
	protected boolean mIsAnimated;
	
	public SimpleAnimatedMaterial() {
		this(false);
	}

	public SimpleAnimatedMaterial(boolean isAnimated) {
		this(mVShader, mFShader, isAnimated);
	}

	public SimpleAnimatedMaterial(String vertexShader, String fragmentShader, boolean isAnimated) {
		super(vertexShader, fragmentShader, NONE);
		mIsAnimated = isAnimated;
		if(mIsAnimated) {
			mUntouchedVertexShader = "\n#define ANIMATED\n" + mUntouchedVertexShader;
			mUntouchedFragmentShader = "\n#define ANIMATED\n" + mUntouchedFragmentShader;
		}
		setShaders(mUntouchedVertexShader, mUntouchedFragmentShader);
	}

	@Override
	public void useProgram() {
		super.useProgram();
	}

	@Override
	public void setShaders(String vertexShader, String fragmentShader)
	{
		super.setShaders(vertexShader, fragmentShader);
		muCurrentFrameHandle = getUniformLocation("uCurrentFrame");
		muTileSizeHandle = getUniformLocation("uTileSize");
		muNumTileRowsHandle = getUniformLocation("uNumTileRows");
	}
	
	public void setCurrentFrame(int currentFrame) {
		mCurrentFrame = currentFrame;
		GLES20.glUniform1f(muCurrentFrameHandle, mCurrentFrame);
	}
	
	public void setTileSize(float tileSize) {
		mTileSize = tileSize;
		GLES20.glUniform1f(muTileSizeHandle, mTileSize);
	}
	
	public void setNumTileRows(int numTileRows) {
		mNumTileRows = numTileRows;
		GLES20.glUniform1f(muNumTileRowsHandle, mNumTileRows);
	}

}