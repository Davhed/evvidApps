package com.example;

import rajawali.materials.SimpleMaterial;
import rajawali.materials.TextureAtlas;
import rajawali.materials.TexturePacker;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;

public class WallpaperRenderer extends RajawaliRenderer{
	
	public WallpaperRenderer(Context context) {
	    super(context);
	    setFrameRate(60);
	    setBackgroundColor(0xff0000);
	}

	public void initScene() {
	    TextureAtlas mAtlas = new TexturePacker(mContext).packTexturesFromAssets("AwesomeAtlas", 1024, 1024, 0, false, "atlas");
	    
	    Plane mPlane = new Plane (2,2,1,1);
	    mPlane.setMaterial(new SimpleMaterial());
	    mPlane.addTexture(mTextureManager.addTexture(mAtlas.getTile("becks").getPage()));
	    addChild(mPlane);
	}
}