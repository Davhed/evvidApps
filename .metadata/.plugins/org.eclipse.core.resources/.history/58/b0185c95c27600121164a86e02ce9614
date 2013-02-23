package com.example;

import javax.microedition.khronos.opengles.GL10;

import com.example.R;

import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.ParticleMaterial;
import rajawali.materials.PhongMaterial;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

public class WallpaperRenderer extends RajawaliRenderer{
	
	private Cube cube;
	private PhongMaterial waterMat;

	private int frameCounter = 0;
	private PointLight pointLight1, pointLight2, pointLight3;
	private DirectionalLight mLight;
	private Sphere mSphere;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(30);
		setBackgroundColor(0x666666);
	}
		
	   @Override
	    public void initScene() {
	    mLight = new DirectionalLight(0.1f, 0.2f, 1.0f); // set the direction
	    mLight.setPower(1.5f);
	    Bitmap bg = BitmapFactory.decodeResource(mContext.getResources(),
	            R.drawable.earthtruecolor_nasa_big);
	    mSphere = new Sphere(1, 12, 12);
	    TestMaterial material = new TestMaterial();
	    mSphere.setMaterial(material);
	    mSphere.setLight(mLight);
	    mSphere.addTexture(mTextureManager.addTexture(bg));
	    addChild(mSphere);
	    mCamera.setZ(-4.2f);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
	    super.onDrawFrame(glUnused);
	    mSphere.setRotY(mSphere.getRotY() + 1);
	}
}