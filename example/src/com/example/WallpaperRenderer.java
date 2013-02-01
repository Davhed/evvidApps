package com.example;

import javax.microedition.khronos.opengles.GL10;

import com.example.R;

import rajawali.BaseObject3D;
import rajawali.lights.PointLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.ParticleMaterial;
import rajawali.materials.PhongMaterial;
import rajawali.primitives.Cube;
import rajawali.primitives.Plane;
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
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(30);
		setBackgroundColor(0x666666);
	}
		
	public void initScene() {
		mCamera.setPosition(0, 15, -15);
		mCamera.setLookAt(0,0,0);
		
		pointLight1 = new PointLight();
		pointLight1.setPosition(10, 15, -5);
		pointLight1.setColor(255, 0, 0);

		pointLight2 = new PointLight();
		pointLight2.setPosition(-10, 15, -5);
		pointLight2.setColor(0, 0, 255);

		pointLight3 = new PointLight();
		pointLight3.setPosition(0, 0, -10);
		pointLight3.setColor(0, 255, 0);
		pointLight3.setPower(.05f);

		waterMat = new PhongMaterial(true);
		waterMat.setUseColor(true);
		
		cube = new Cube(3);
		cube.setMaterial(waterMat);
		cube.setColor(0x666666);
		cube.addLight(pointLight1);
		cube.addLight(pointLight2);
		cube.addLight(pointLight3);

	    addChild(cube);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			cube.setRotation(cube.getRotation().add(.5f, .5f, .5f));
			frameCounter++;
	}

}