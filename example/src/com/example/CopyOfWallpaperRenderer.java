//package com.example;
//
//import javax.microedition.khronos.opengles.GL10;
//
//import com.example.R;
//
//import rajawali.BaseObject3D;
//import rajawali.materials.SimpleAnimatedMaterial;
//import rajawali.primitives.Plane;
//import rajawali.renderer.RajawaliRenderer;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//
//public class CopyOfWallpaperRenderer extends RajawaliRenderer{
//	
//	private BaseObject3D water1;
//	private SimpleAnimatedMaterial waterMat;
//
//	private int frameCounter = 1;
//	
//	public CopyOfWallpaperRenderer(Context context) {
//		super(context);
//		setFrameRate(30);
//		setBackgroundColor(0x666666);
//	}
//		
//	public void initScene() {
//		mCamera.setPosition(0, 15, -15);
//		mCamera.setLookAt(0,0,0);
//		
//		Bitmap waterTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wateratlas);
//		
//		waterMat = new SimpleAnimatedMaterial(true);
//		waterMat.setTileSize(1/4f);
//		waterMat.setNumTileRows(4);
//		waterMat.addTexture(mTextureManager.addTexture(waterTex));
//		
//		water1 = new Plane(10,10,1,1);
//		water1.getGeometry().setTextureCoords(new float[] { 0, 1, .25f, 1, 0, .75f, .25f, .75f });
//		water1.setMaterial(waterMat);
//		water1.setRotation(-90, 0, 0);
//
//	    addChild(water1);
//	}
//	
//	@Override
//	public void onDrawFrame(GL10 glUnused) {
//			super.onDrawFrame(glUnused);
//			waterMat.setCurrentFrame(frameCounter);
//			frameCounter++;
//	    	if(frameCounter++ > 16*4)
//	    		frameCounter = 1;
//	}
//
//}