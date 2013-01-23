package com.example;

import javax.microedition.khronos.opengles.GL10;

import com.example.R;


import rajawali.BaseObject3D;
import rajawali.materials.SimpleMaterial;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



public class WallpaperRenderer_BU extends RajawaliRenderer{
	
	private BaseObject3D water1;
	private float[] waterUVs;
	private float[][] UVTiles;

	private int tileIndex = 0, frameCounter = 0;
	
	public WallpaperRenderer_BU(Context context) {
		super(context);
		setFrameRate(30);
		setBackgroundColor(0x666666);
	}
		
	public void initScene() {		
		Bitmap waterTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wateratlas);
		water1 = new Plane(20,20,1,1);
		water1.setMaterial(new SimpleMaterial());
		water1.addTexture(mTextureManager.addTexture(waterTex));
		water1.setRotation(0, 90, -90);

		waterUVs   = new float[] {   0f, .25f, .25f, .25f,   0f,   0f, .25f,   0f };
		UVTiles    = new float [16][];
		
		for(int j = 0; j <  UVTiles.length; j++){
    		if(j%4 == 0) {
    			waterUVs = new float[] {   0f, (.25f+((j/4)*.25f)), .25f, (.25f+((j/4)*.25f)),   0f,   (0f+((j/4)*.25f)), .25f,   (0f+((j/4)*.25f)) };
	    	}else{
	    		for(int i = 0; i < waterUVs.length; i++) {
					if ( i%2 == 0){
						waterUVs[i]+=.25f;
						if (waterUVs[i] > 1) waterUVs[i]= 0;
					}
				}
	    	}
    		UVTiles[j] = new float[] { waterUVs[0], waterUVs[1], waterUVs[2], waterUVs[3], waterUVs[4], waterUVs[5], waterUVs[6], waterUVs[7] };
    	}
		
	    addChild(water1);

		mCamera.setPosition(0, 15, -15);
		mCamera.setLookAt(0,0,0);
	}
	
	@Override
	public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			if(frameCounter%4 == 0) waterMotion();
			frameCounter++;
	    	if(frameCounter == 16*4) frameCounter = 0;
	}
	
	private void waterMotion() {
    	if(tileIndex == 16) tileIndex = 0;
		water1.getGeometry().setTextureCoords(UVTiles[tileIndex]);
//		System.out.println(tileIndex+")  "+UVTiles[tileIndex][0]+", "+UVTiles[tileIndex][1]+", "+UVTiles[tileIndex][2]+", "+UVTiles[tileIndex][3]+", "+UVTiles[tileIndex][4]+", "+UVTiles[tileIndex][5]+", "+UVTiles[tileIndex][6]+", "+UVTiles[tileIndex][7]);
		water1.reload();
    	tileIndex++;

	}
	

}