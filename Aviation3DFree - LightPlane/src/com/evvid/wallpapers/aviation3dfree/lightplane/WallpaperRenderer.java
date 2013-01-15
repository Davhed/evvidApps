/*
 * 
 * 		Aviation 3D relies heavily on the Rajawali framework which can be found here:
 * 
 * 		https://github.com/MasDennis/Rajawali
 * 		
 * 		Rajawali --
 * 		Copyright 2011 Dennis Ippel
 * 		Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *     
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 *
 */

package com.evvid.wallpapers.aviation3dfree.lightplane;

import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.aviation3dfree.lightplane.R;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.animation.Animation3D;
import rajawali.animation.Animation3DListener;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.PointLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.PhongMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.math.Number3D;
import rajawali.primitives.Plane;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;



public class WallpaperRenderer extends RajawaliRenderer {
	
	private float wave, curZPos, xpos, flickStart, xStartPos = 0;
	private double waveIndex = 0;
	
	private Bitmap skyTex, groundTex, cloudBankTex, planeTex, propTex, balloonTex;
	
	private PointLight pLight_key, pLight_key2, pLight_fill, pLight_fill2, pLight_fill3, pLight_rim, pLight_ground, pLight_balloon, pLight_balloon2;
	
	private BaseObject3D groundObj, cloudBankObj, mountainsObj, planeObj, pilotObj, windowsObj, propObj, parentObj, balloon, balloonObj;
	private Plane sky;
	
	private ObjectInputStream ois;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(60);
		setFogEnabled(true);		
    }
		
	public void initScene() {		
		setLights();
		setScene();
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);

	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { // Check for screen rotation
		super.onSurfaceChanged(gl, width, height);
	}
	
	@Override
	public void onSurfaceDestroyed() {
		clearChildren();
		try{
			super.onSurfaceDestroyed();
		}catch (Exception e){
			e.printStackTrace();
		}
		skyTex.recycle();	
		groundTex.recycle();
		cloudBankTex.recycle();
		planeTex.recycle();
		propTex.recycle();
		balloonTex.recycle();
		System.gc();
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		frameSyncAnimation();
	}

	@Override //This method moves the camera using direct touch events. Tracking a flick and turning it into a procedural animation for smoothing
	public void onTouchEvent(MotionEvent me) {
	       if (me.getAction() == MotionEvent.ACTION_DOWN) {
	           xStartPos = me.getX();
	           xpos = xStartPos;
	    	   flickStart = mCamera.getZ();
	       }
	       if (me.getAction() == MotionEvent.ACTION_MOVE) {
	           float xd = xpos - me.getX();
	           swipeCamera(xd);
	           xpos = me.getX();
	       }
	       if (me.getAction() == MotionEvent.ACTION_UP) {
	    	   float xEndPos = me.getX();
	    	   float delta = xEndPos-xStartPos;
	     	   flickCamera(delta);
	       }	
	       try {
	           Thread.sleep(15);
	       } catch (Exception e) {
	       }
	}
	
	
	public float getHomeScreenSpacing() {
		float maxOffsetZ = 25;
		float nextCamZ = maxOffsetZ/(((maxOffsetZ/5) - 1) / 2);
		return nextCamZ;
	}
	
	
	private void setLights(){
		pLight_key = new PointLight();
		pLight_key.setPower(4);
		pLight_key.setColor(0xfdffcf);
		pLight_key.setPosition(0, 7, 10);

		pLight_key2 = new PointLight();
		pLight_key2.setPower(2);
		pLight_key2.setColor(0xfdffcf);
		pLight_key2.setPosition(0, 7f, -5);
		
		pLight_fill = new PointLight();
		pLight_fill.setPower(1.5f);
		pLight_fill.setColor(0x99bbcc);
		pLight_fill.setPosition(7, -1.5f, -10);
		
		pLight_fill2 = new PointLight();
		pLight_fill2.setPower(1f);
		pLight_fill2.setColor(0x79b3fc);
		pLight_fill2.setPosition(8, -1.5f, 1);
		
		pLight_fill3 = new PointLight();
		pLight_fill3.setPower(1f);
		pLight_fill3.setColor(0x79b3fc);
		pLight_fill3.setPosition(0 , 10 , 0);

		pLight_rim = new PointLight();
		pLight_rim.setPower(1.5f);
		pLight_rim.setColor(0x79b3fc);
		pLight_rim.setPosition(-5, 1f, 0);		

		pLight_balloon = new PointLight();
		pLight_balloon.setPower(3500);
		pLight_balloon.setPosition(0, 7, 20);
		
		pLight_balloon2 = new PointLight();
		pLight_balloon2.setPower(500);
		pLight_balloon2.setPosition(-400, 7, 0);
		
		pLight_ground = new PointLight();
		pLight_ground.setPower(5000);
		pLight_ground.setPosition(0, 200, 0);		
	}
	
	private void setScene(){
		parentObj = new BaseObject3D();
		balloonObj = new BaseObject3D();
		
		mCamera.setFarPlane(40000);
		mCamera.setX(25f);
		mCamera.setFogNear(500);
		mCamera.setFogFar(850);
		mCamera.setFogColor(0x6688cb);
		mCamera.setLookAt(0,0,0);

		try {
			skyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sky_tex);
			groundTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ground);
			balloonTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.balloon_tex);
			cloudBankTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cloudbg);
			planeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cessna_tex);
			propTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cessna_prop_tex);
	
			SimpleMaterial skyMat = new SimpleMaterial();
			SimpleMaterial cloudBankMat = new SimpleMaterial();
			SimpleMaterial pilotMat = new SimpleMaterial();
			DiffuseMaterial balloonMat = new DiffuseMaterial();
			PhongMaterial groundMat = new PhongMaterial();
			PhongMaterial planeMat = new PhongMaterial();
			PhongMaterial windowMat = new PhongMaterial();
			PhongMaterial propMat = new PhongMaterial();
	 
			sky = new Plane(30000, 30000, 1, 1, 1);
	    	sky.setMaterial(skyMat);
	    	sky.addTexture(mTextureManager.addTexture(skyTex));
	    	sky.setX(-1500);
	    	sky.setY(1200);
	    	sky.setRotation(-90,-90,0);
	        addChild(sky);
	           	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.ground));
			groundObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			groundMat.setAmbientColor(0x6688cb);
			groundMat.setAmbientIntensity(.15f);
			groundObj.setMaterial(groundMat);
			groundObj.addTexture(mTextureManager.addTexture(groundTex));
			groundObj.addLight(pLight_ground);
			groundObj.setPosition(50,-150, 0);
			groundObj.setScale(.06f);
			addChild(groundObj);
			ois.close();
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.cloudbank));
			cloudBankObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			cloudBankObj.setMaterial(cloudBankMat);
			cloudBankObj.setBlendingEnabled(true);
			cloudBankObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			cloudBankObj.addTexture(mTextureManager.addTexture(cloudBankTex));
			cloudBankObj.setScale(.065f);
			cloudBankObj.setRotation(180, 0, 0);
			cloudBankObj.setPosition(0,70,0);
			addChild(cloudBankObj);
			ois.close();
	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.mountains));
			mountainsObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			mountainsObj.setMaterial(groundMat);
			mountainsObj.addLight(pLight_ground);
			mountainsObj.setPosition(50,-100, 0);
			mountainsObj.setScale(.06f);
			addChild(mountainsObj);
			ois.close();
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.balloon));
	    	balloon = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	balloon.setMaterial(balloonMat);
	    	balloon.addTexture(mTextureManager.addTexture(balloonTex));
	    	balloon.setPosition(-250,-50,0);
	    	balloon.setRotY(90);
	    	balloon.addLight(pLight_balloon);
	    	balloon.addLight(pLight_balloon2);
	    	balloonObj.setRotY(60);
			balloonObj.addChild(balloon);
			addChild(balloonObj);
			ois.close();
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.cessna));
			planeObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			planeMat.setShininess(80);
			planeMat.setAmbientColor(0x6688cb);
			planeMat.setAmbientIntensity(.15f);
			planeObj.setMaterial(planeMat);
			planeObj.addTexture(mTextureManager.addTexture(planeTex));
			planeObj.setZ(5);
			parentObj.addChild(planeObj);
			ois.close();
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.pilot));
			pilotObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			pilotMat.setUseColor(true);
			pilotObj.setMaterial(pilotMat);
			pilotObj.setColor(0x000000);
			parentObj.addChild(pilotObj);
			ois.close();
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.windows));
			windowsObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			windowMat.setShininess(95);
			windowMat.setUseColor(true);
			windowsObj.setBlendingEnabled(true);
			windowsObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			windowsObj.setMaterial(windowMat);
			windowsObj.setColor(0x99bbee);
			parentObj.addChild(windowsObj);
			ois.close();
				
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.prop));
			propObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			propMat.setShininess(80);
			propObj.setMaterial(propMat);
			propObj.addTexture(mTextureManager.addTexture(propTex));
			propObj.setPosition(0f, -.25f, 14.8f);
			parentObj.addChild(propObj);
			ois.close();
			
			parentObj.addLight(pLight_key);
			parentObj.addLight(pLight_key2);
			parentObj.addLight(pLight_fill);
			parentObj.addLight(pLight_fill2);
//			parentObj.addLight(pLight_fill3);
			parentObj.setScale(.3f,.3f,.3f);
			addChild(parentObj);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void frameSyncAnimation() {
		cloudBankObj.setRotY(cloudBankObj.getRotY() + .005f);
		mountainsObj.setRotY(mountainsObj.getRotY() - .007f);
		groundObj.setRotY(groundObj.getRotY() - .025f);
		planeMovement();
		balloonMovement();
	}
	
	private void balloonMovement(){
		balloonObj.setRotY(balloonObj.getRotY() - .010f);
		balloon.setRotY((float) (balloon.getRotY() - Math.sin(.020f)));
	}
	private void planeMovement() {
		parentObj.getChildAt(3).setRotZ(parentObj.getChildAt(3).getRotZ()-70);//Prop rotation

		wave = (float)(5*(Math.sin(waveIndex/100)));
		parentObj.setRotation(wave*.5f,0f,wave);
		parentObj.setPosition((float)(1.5f*(Math.cos(waveIndex/1500))), (float)(3*(Math.sin(waveIndex/1500))), (float)(-(Math.cos(waveIndex/1500))));
		waveIndex++;
	}
	
	private void swipeCamera(float touchOffset) {
       float zOffset = touchOffset/(10*5);// 20 - 3-screens - 50 5-screens - 100 7-screens
       curZPos = mCamera.getPosition().z;
 	   if(curZPos < -25 || curZPos > 25){
	           if(-25>curZPos){ curZPos = -25;}
	           else {curZPos = 25; }    		   
 	   }
 	   mCamera.setX(25-(-((curZPos + zOffset)/(10*5))));
 	   mCamera.setZ((curZPos + (zOffset*.66f)));
	}
	
	private void flickCamera(float delta) {
		float flickTarget = 0;
	 	if (delta<0) flickTarget = flickStart + (getHomeScreenSpacing());
	 	if (delta>0) flickTarget = flickStart - (getHomeScreenSpacing());
	 	   
	  	if(flickTarget < -25 || flickTarget > 25){
		           if(-25>flickTarget){ flickTarget = -25;}
		           else {flickTarget = 25; }    		   
		   }
			Number3D startPos = new Number3D(mCamera.getPosition());
			Number3D target =  new Number3D(mCamera.getX(), mCamera.getY(), flickTarget);
			TranslateAnimation3D flickAnim = new TranslateAnimation3D(startPos, target);
			flickAnim.setDuration(500);
			flickAnim.setRepeatCount(0);
			flickAnim.setInterpolator(new DecelerateInterpolator());
			flickAnim.setTransformable3D(mCamera);
			flickAnim.setAnimationListener(new Animation3DListener() {
				
				public void onAnimationStart(Animation3D animation) {
				}
				
				public void onAnimationRepeat(Animation3D animation) {
				}
				
				public void onAnimationEnd(Animation3D animation) {
					animation.cancel();
					animation.reset();
				}

				public void onAnimationUpdate(Animation3D animation,
						float interpolatedTime) {
					// TODO Auto-generated method stub
					
				}
			});
		flickAnim.start();		
	}
}