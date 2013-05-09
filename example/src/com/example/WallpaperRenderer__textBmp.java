package com.example;

import javax.microedition.khronos.opengles.GL10;

import rajawali.BaseObject3D;
import rajawali.materials.SimpleMaterial;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

public class WallpaperRenderer__textBmp extends RajawaliRenderer{
	private BaseObject3D sign;
		
	private String signText = "Tiki Town";
		
    private float x, y;
    private Bitmap signTex;

	public WallpaperRenderer__textBmp(Context context) {
		super(context);
		setFrameRate(30);
		setBackgroundColor(0x666666);
	}
		
	@Override
	public void initScene() {
		createSign();
		mCamera.setZ(-10);
	}
	
	private void createSign(){
		signTex = textAsBitmap(signText);
		SimpleMaterial signMat = new SimpleMaterial();
		signMat.addTexture(mTextureManager.addTexture(signTex));
		sign = new Plane(2, 2, 1, 1);
		sign.setTransparent(true);
		sign.setMaterial(signMat);
		sign.setRotZ(-90);
//		sign.setPosition(0, -10, 100);
//		sign.addLight(pLight);
		addChild(sign);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
	    super.onDrawFrame(glUnused);
	}
	
	private Bitmap textAsBitmap(String text) {// For later usage TODO:Make sign with editable text
	    Paint paint = new Paint();
	    paint.setTypeface(Typeface.DEFAULT_BOLD);
	    paint.setTextSize(14);
	    paint.setAntiAlias(true);
	    paint.setColor(0xff000000);
	    paint.setTextAlign(Paint.Align.CENTER);
	    float baseline = (int) (paint.ascent());
	    Bitmap image = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
	    x = image.getWidth();
	    y = image.getHeight();
	    Canvas canvas = new Canvas(image);
//	    canvas.drawARGB(255, 255, 255, 255);
	    canvas.drawText(text, x*.5f, (y-baseline)*.5f, paint);
	    return image;
	}
}