package com.example;

import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.materials.SimpleMaterial;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;

public class WallpaperRenderer extends RajawaliRenderer{
	
	private BaseObject3D mSphere;

	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(30);
		setBackgroundColor(0x666666);
	}
		
	@Override
	public void initScene() {
		mCamera.setFarPlane(10000);	
	    mSphere = new Sphere(400, 8, 8);
	    mSphere.setMaterial(new SimpleMaterial());
	    mSphere.getMaterial().setUseColor(true);
	    mSphere.setColor(0xff0000);
	    mSphere.setDoubleSided(true);
	    addChild(mSphere);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
	    super.onDrawFrame(glUnused);
	}
}