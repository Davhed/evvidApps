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

package com.evvid.wallpapers.aviation3d.helo;


import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.aviation3d.helo.R;

import rajawali.primitives.Sphere;
import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.animation.Animation3D;
import rajawali.animation.Animation3DListener;
import rajawali.animation.RotateAnimation3D;
import rajawali.animation.ScaleAnimation3D;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.PointLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.PhongMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.math.Number3D;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

//Serializer//
import rajawali.parser.ObjParser;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;


public class WallpaperRenderer extends RajawaliRenderer implements SensorEventListener{
	
	int blockWidth = 600;
	private int  flashTimer = 0;
	private float homeScreenOffset, wave, xTilt, yTilt, curZPos, flickStart, xStartPos, xPos, yPos, yDelta, maxY, autospeed;
	private double waveIndex = 0;
	private boolean useAccel, isPortrait, camDrift, camLock, autopilot, autolight, altitude, bgClouds;
	private String swipeMode = "manual_swipe";
	private Number3D mAccValues = new Number3D();
	
	private Bitmap heloTex, rotorTex, tailRotorTex, lightTex, skyTex, greenTex, redTex, clouddomeTex;
	
	private PointLight pLight_key, pLight_key2, pLight_fill, pLight_fill2, pLight_fill3, pLight_rim, pLight_ground;
	
	private Animation3D lightAnim;
	private BaseObject3D[] cityArray;
	private BaseObject3D parentObj, heloObj, crewObj, 
	heloLightObj, lightSource, smallLight, strobeLight, 
	tailLight, lightRaysObj, heloWindowsObj, rotorObj, 
	tailRotorObj, ground, cityObj, cityObj2, sunObj,
	sky, fgBuild, bgBuild, tempObj, cityBlock, clouddomeObj;
		
	private SensorManager mSensorManager;
	private OnSharedPreferenceChangeListener mListener;
;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(30);
		setFogEnabled(true);
		
    }
		
	public void initScene() {
		mSensorManager = (SensorManager) mContext.getSystemService("sensor");
		setOnPreferenceChange();
		setPrefsToLocal();
		setScene();
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
        PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);
		mSensorManager.registerListener( this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		preferences.registerOnSharedPreferenceChangeListener(mListener);
		parentObj.getChildAt(1).getChildAt(0).setRotY(-30); //Init Light Rotation
		if(!autolight){
			lightAnim.start();
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { // Check for screen rotation
		super.onSurfaceChanged(gl, width, height);
		isPortrait = ((width/height)==0);
	}
	
	@Override
	public void onSurfaceDestroyed() {
		try{
			clearChildren();
			super.onSurfaceDestroyed();
		} catch (Exception e){
			e.printStackTrace();
		}
        mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		preferences.unregisterOnSharedPreferenceChangeListener(mListener);
		if(!autolight && lightAnim != null){
			lightAnim.cancel();
			lightAnim.reset();
		}
    
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
		frameSyncAnimation();
		onTilt(mAccValues.x, mAccValues.y, mAccValues.z);
	}

    @Override  //This method moves the camera using the Android home screen swipe output. It's a better way, but not always supported
    public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
    	if(swipeMode.equals("home_screen_swipe")){
	    	float zOffset = (50*xOffset)-25;
	    	mCamera.setX(50-(-((curZPos + zOffset)/50)));
	    	mCamera.setZ(curZPos + zOffset);
    	}
    }
	
	@Override //This method moves the camera using direct touch events. Tracking a flick and turning it into a procedural animation for smoothing
	public void onTouchEvent(MotionEvent me) {
		if(swipeMode.equals("manual_swipe")){
	       if (me.getAction() == MotionEvent.ACTION_DOWN) {
	           xStartPos = me.getX();
	           xPos = xStartPos;
	    	   flickStart = mCamera.getZ();
	       }
	       if (me.getAction() == MotionEvent.ACTION_MOVE) {
	           float xd = xPos - me.getX();
	           swipeCamera(xd);
	           xPos = me.getX();
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
       if (me.getAction() == MotionEvent.ACTION_DOWN) {
    	   yPos = me.getY();
       }
       if (me.getAction() == MotionEvent.ACTION_MOVE) {
    	   yDelta = (me.getY()-yPos)/1000;
    	   if (parentObj != null){
    		   float newY = parentObj.getY()-yDelta;
	     	   maxY = 15;
	    	   if(newY>maxY) newY = maxY;
	    	   if(newY<-maxY) newY = -maxY;
	   	       if(altitude){ parentObj.setY(newY);}
    	   }
       }
	
       try {
           Thread.sleep(15);
       } catch (Exception e) {
       }
	}
	
	public void setAccelerometerValues(float x, float y, float z) {
		mAccValues.setAll(x, y, z);
	}
	
	public float getHomeScreenSpacing() {
		float maxOffsetZ = 25;
		float nextCamZ = maxOffsetZ/(((maxOffsetZ/homeScreenOffset) - 1) / 2);
		return nextCamZ;
	}

	private void setOnPreferenceChange(){
		mListener = new OnSharedPreferenceChangeListener(){
				
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				
				if ("home_screen_swipe".equals(key))
				{
					if (sharedPreferences.getBoolean(key, false)){
						swipeMode = key;
					}
				}
				else if ("manual_swipe".equals(key))
				{
					if (sharedPreferences.getBoolean(key, true)) {
						swipeMode = key;
					}
				} 
				else if ("screen_count".equals(key))
				{
					homeScreenOffset = Integer.parseInt(sharedPreferences.getString(key, "5"));
				} 
				else if ("accel_pref".equals(key))
				{
					useAccel = sharedPreferences.getBoolean(key, true);		
				} 
				else if ("cam_drift".equals(key))
				{
					camDrift = sharedPreferences.getBoolean(key, true);
				}
				else if ("cam_lock".equals(key))
				{
					camLock = sharedPreferences.getBoolean(key, true);
				}
				else if ("bgClouds_pref".equals(key))
				{
					bgClouds = sharedPreferences.getBoolean(key, true);
				}
				else if ("autopilot_pref".equals(key))
				{
					autopilot = sharedPreferences.getBoolean(key, false);
				}
				else if ("autospeed_pref".equals(key))
				{
					autospeed = ((float)(Integer.parseInt(preferences.getString("autospeed_pref", "2")))*.1f);
				}
				else if ("altitude_pref".equals(key))
				{
					altitude = sharedPreferences.getBoolean(key, true);
				}
				else if ("autolight_pref".equals(key))
				{
					autolight = sharedPreferences.getBoolean(key, false);
					parentObj.getChildAt(1).getChildAt(0).setRotation(0,-30,0);
				} 
				preferences = sharedPreferences;
			}
		};		
	}
	
	private void setPrefsToLocal(){
		homeScreenOffset = Integer.parseInt(preferences.getString("screen_count", "5"));
		if((preferences.getBoolean("home_screen_swipe", false))&&(!preferences.getBoolean("manual_swipe", true))){
			swipeMode = "home_screen_swipe";
		}
		useAccel = preferences.getBoolean("accel_pref", true);	
		camDrift = preferences.getBoolean("cam_drift", true);
		camLock = preferences.getBoolean("cam_lock", true);
		autopilot = preferences.getBoolean("autopilot_pref", false);
		autospeed = ((float)(Integer.parseInt(preferences.getString("autospeed_pref", "2")))*.1f);
		altitude = preferences.getBoolean("altitude_pref", true);
		autolight = preferences.getBoolean("autolight_pref", false);
	}	
	
	private void setLights(){
		pLight_key = new PointLight();
		pLight_key.setPower(3);
		pLight_key.setColor(1,0,0);
		pLight_key.setPosition(0, 7, 10);
		
		pLight_key2 = new PointLight();
		pLight_key2.setPower(2f);
		pLight_key2.setPosition(0, 7f, -5);
		
		pLight_fill = new PointLight();
		pLight_fill.setPower(1f);
		pLight_fill.setPosition(7, -1.5f, -10);
		
		pLight_fill2 = new PointLight();
		pLight_fill2.setPower(.5f);
		pLight_fill2.setPosition(8, -1.5f, 1);
		
		pLight_fill3 = new PointLight();
		pLight_fill3.setPower(.5f);
		pLight_fill3.setPosition(0 , 10 , 0);

		pLight_rim = new PointLight();
		pLight_rim.setPower(.1f);
		pLight_rim.setPosition(-10, 50, 0);
		
		pLight_ground = new PointLight();
		pLight_ground.setPower(2500);
		pLight_ground.setAttenuation(0, 1000, .09f, .032f);
		pLight_ground.setPosition(-500, 500, 0);
	}
	
	
	private void setScene(){
					
		setLights();
		
		mCamera.setFarPlane(20000);
		mCamera.setFogNear(0);
		mCamera.setFogFar(375);
		mCamera.setFogColor(0xf07d42);
		mCamera.setLookAt(0,0,0);
		mCamera.setX(50f);
	//	mCamera.setPosition(0, 0, -50); //TODO: Chase cam mode
		
		
		PhongMaterial heloMat = new PhongMaterial();
		DiffuseMaterial rotorMat = new DiffuseMaterial();
		DiffuseMaterial tailRotorMat = new DiffuseMaterial();
		SimpleMaterial crewMat = new SimpleMaterial();
		SimpleMaterial lightSourceMat = new SimpleMaterial();
		SimpleMaterial lightMat = new SimpleMaterial();
		SimpleMaterial strobeLightMat = new SimpleMaterial();
		SimpleMaterial tailLightMat = new SimpleMaterial();
		SimpleMaterial tailLight2Mat = new SimpleMaterial();
		SimpleMaterial skyMat = new SimpleMaterial();
		SimpleMaterial clouddomeMat = new SimpleMaterial();

		buildCity();

		parentObj = new BaseObject3D();	

		try {
			skyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.posz);
			clouddomeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clouddome_tex);
			heloTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_tex);
			rotorTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_rotor_tex);
			tailRotorTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_tail_rotor_tex);
			lightTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lightrays_tex);
			greenTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.green_light);
			redTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_light);
			
			ObjectInputStream ois;	
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.clouddome));
			sky = new BaseObject3D((SerializedObject3D)ois.readObject());
			skyMat.addTexture(mTextureManager.addTexture(skyTex));
			sky.setMaterial(skyMat);
			sky.setRotation(90, 0, 90);
			sky.setY(-1000);
//			sky.setZ(-1000);  //TODO: Chase cam mode
//			sky.setX(5000);
			addChild(sky);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.clouddome));
			clouddomeObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			clouddomeMat.addTexture(mTextureManager.addTexture(clouddomeTex));
			clouddomeObj.setMaterial(clouddomeMat);
			clouddomeObj.setBlendingEnabled(true);
			clouddomeObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			clouddomeObj.setScale(.052f, .1f, .052f);
			clouddomeObj.setRotation(0, -90, 20);
			clouddomeObj.setPosition(0, -250, 0);
			addChild(clouddomeObj);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.helotailrotor));
			tailRotorObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			tailRotorMat.addTexture(mTextureManager.addTexture(tailRotorTex));
			tailRotorObj.setMaterial(tailRotorMat);
			tailRotorObj.setPosition(1.07161f,7.6472f, 33.1733f);
			parentObj.addChild(tailRotorObj);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.helo));
			heloObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			heloMat.setAmbientIntensity(.25f);
			heloMat.setAmbientColor(.9f, .5f, .3f, 1);
			heloMat.addTexture(mTextureManager.addTexture(heloTex));
			heloObj.setMaterial(heloMat);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.helolight));
			heloLightObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			heloLightObj.setMaterial(heloMat);

			lightSource = new Sphere(.70f, 5, 5);
			lightSourceMat.setUseColor(true);
			lightSource.setZ(-.5f);
			lightSource.setColor(0xffffff);
			lightSource.setBlendingEnabled(true);
			lightSource.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
			lightSource.setMaterial(lightSourceMat);
			heloLightObj.addChild(lightSource);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.lightrays));
			lightRaysObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			lightRaysObj.setTransparent(true);
			lightRaysObj.setBlendingEnabled(true);
			lightRaysObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
			lightMat.addTexture(mTextureManager.addTexture(lightTex));
			lightRaysObj.setMaterial(lightMat);
			heloLightObj.addChild(lightRaysObj);
			
			heloLightObj.setPosition(-4.86249f, -6.0637f, -11.44f);
			heloLightObj.setRotY(-30);
			heloObj.addChild(heloLightObj);

			smallLight = new Sphere(.45f, 5, 5);
			strobeLightMat.setUseColor(true);
			smallLight.setMaterial(strobeLightMat);
			smallLight.setPosition(1 ,-3.5f, -16.5f);
			smallLight.setColor(0xaaaaff);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
			strobeLight = smallLight;
			strobeLight.setScale(0);
			heloObj.addChild(smallLight);

			tailLightMat.addTexture(mTextureManager.addTexture(redTex));
			smallLight = new Sphere(.35f, 5, 5);
			smallLight.setMaterial(tailLightMat);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			smallLight.setRotY(90);
			smallLight.setScale(1.25f);
			smallLight.setPosition(6.5f, 2.6f, 28f);
			heloObj.addChild(smallLight);

			smallLight = new Sphere(.4f, 5, 5);
			smallLight.setMaterial(tailLightMat);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			smallLight.setRotY(90);
			smallLight.setPosition(0.5f, 7.6f, 31);
			tailLight = smallLight;
			tailLight.setScale(0);
			heloObj.addChild(smallLight);
			
			smallLight = new Sphere(.35f, 5, 5);
			tailLight2Mat.addTexture(mTextureManager.addTexture(greenTex));
			smallLight.setMaterial(tailLight2Mat);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			smallLight.setPosition(-5.3f, 2.4f, 28f);
			smallLight.setScale(1.25f);
			smallLight.setRotY(90);
			smallLight.setColor(0x00ff00);
			heloObj.addChild(smallLight);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.helocrew));
			crewObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			crewMat.setUseColor(true);
			crewObj.setMaterial(crewMat);
			crewObj.setColor(0x000000);
			heloObj.addChild(crewObj);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.helowindows));
			heloWindowsObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			heloMat.setUseColor(true);
			heloWindowsObj.setBlendingEnabled(true);
			heloWindowsObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			heloWindowsObj.setMaterial(heloMat);
			heloWindowsObj.setColor(0xffffff);
			heloObj.addChild(heloWindowsObj);
			parentObj.addChild(heloObj);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.helorotor));
			rotorObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			rotorMat.addTexture(mTextureManager.addTexture(rotorTex));
			rotorObj.setMaterial(rotorMat);
			parentObj.addChild(rotorObj);
			
			ois.close();
			
			parentObj.addLight(pLight_key);
			parentObj.addLight(pLight_key2);
			parentObj.addLight(pLight_fill);
			parentObj.addLight(pLight_fill2);
//			parentObj.addLight(pLight_fill3);
			parentObj.addLight(pLight_rim);
			parentObj.setScale(.3f,.3f,.3f);
			parentObj.setRotation(0,180,0);
			addChild(parentObj);
			
		} catch (Exception e){
			e.printStackTrace();
		}
			
		SimpleMaterial sphereMat = new SimpleMaterial();
		sphereMat.setUseColor(true);
		
		sunObj = new Sphere(100, 30, 30);
		sunObj.setX(-600);
		sunObj.setY(-70);
		sunObj.setScale(.05f, 1, 1);
		sunObj.setMaterial(sphereMat);
		sunObj.setColor(0x999999);
		sunObj.setBlendingEnabled(true);
		sunObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		addChild(sunObj);
		
		Number3D lightAxis = new Number3D(1, 2, 0);
		lightAxis.normalize();
		lightAnim = new RotateAnimation3D(lightAxis, 100);
		lightAnim.setDuration(24000);
		lightAnim.setRepeatMode(Animation3D.REVERSE);
		lightAnim.setRepeatCount(Animation3D.INFINITE);
		lightAnim.setTransformable3D(parentObj.getChildAt(1).getChildAt(0));
		if(!autolight){
			lightAnim.start();
		}
		heloLightObj = parentObj.getChildAt(1).getChildAt(0);
	}
	
	private void buildCity() {
								
		cityObj  = new BaseObject3D();
		cityObj2  = new BaseObject3D();
		cityArray = new BaseObject3D[2];		//Make an array so itteration through the blocks is possible
				
		DiffuseMaterial cityMat = new DiffuseMaterial();
		cityMat.setUseColor(true);

		try {
			ObjectInputStream ois;	
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.groundplane));
			ground = new BaseObject3D((SerializedObject3D)ois.readObject());
			ground.setMaterial(cityMat);
			ground.setColor(0xffffff);
			ground.setRotation(0, 0, 0);
			ground.setPosition(0, -70, 0);
			ground.addLight(pLight_ground);
			addChild(ground);
			
			int color = 0xff3200;
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bgbuildings));
			bgBuild = new BaseObject3D((SerializedObject3D)ois.readObject());
			bgBuild.setMaterial(cityMat);

			tempObj = new BaseObject3D();
			tempObj = bgBuild.clone();
			tempObj.setColor(color);
			tempObj.setPosition(-150, -70, -100);

			cityObj.addChild(tempObj);
			cityObj2.addChild(tempObj);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.block1));
			fgBuild = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgBuild.setMaterial(cityMat);
			tempObj = new BaseObject3D();
			tempObj = fgBuild.clone();
			tempObj.setColor(color);
			tempObj.setDoubleSided(true);
			tempObj.setPosition(-250, -70, -100);

			cityObj.addLight(pLight_ground);
			cityObj2.addLight(pLight_ground);

			cityObj.addChild(tempObj);
			cityObj2.addChild(tempObj);
			
			for(int b = 0; b < 2; b++){
				cityBlock = new BaseObject3D();

				cityArray[b] = cityBlock;
				if(b==0){
					cityObj.addChild(cityArray[b]);
					addChild(cityObj);
				}else if(b==1){
					cityObj2.addChild(cityArray[b]);
					cityObj2.setZ(blockWidth);
					addChild(cityObj2);		
				}
			}		
			ois.close();	
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void frameSyncAnimation() {
		heloMovement();
		checkBlockPositions();
		if(!bgClouds) clouddomeObj.setVisible(false);
		else if (bgClouds) clouddomeObj.setVisible(true);
		if(camLock){
			mCamera.setLookAt(parentObj.getPosition());
		}else {
			mCamera.setLookAt(0,0,0);
		}
	}
		
	private void heloMovement() {
		
		if (flashTimer == 60) blink(strobeLight);
		if (flashTimer == 90) {
			blink(tailLight);
			flashTimer = 0;
		}
		flashTimer++;
		
		wave = (float)(5*(Math.sin(waveIndex/100)));
		parentObj.getChildAt(0).setRotX(parentObj.getChildAt(0).getRotX()-70);
		parentObj.getChildAt(2).setRotY(parentObj.getChildAt(2).getRotY()+50);
		parentObj.setRotation(-wave*.5f-10, 180, wave*2);
		waveIndex++;
		
		if(autopilot){
			if(!altitude)parentObj.setY(wave/2);
			cityObj.setPosition(cityObj.getX(),cityObj.getY(),(float) (cityObj.getZ()-autospeed));
			cityObj2.setPosition(cityObj2.getX(),cityObj2.getY(),(float) (cityObj2.getZ()-autospeed));
		}
	}
	
	private void blink(BaseObject3D aLight){
		Animation3D blinkAnim = new ScaleAnimation3D(new Number3D(1.5f,1.5f,1.5f));
		blinkAnim.setDuration(50);
		blinkAnim.setRepeatCount(1);
		blinkAnim.setRepeatMode(Animation3D.REVERSE);
		blinkAnim.setTransformable3D(aLight);
		blinkAnim.setAnimationListener(new Animation3DListener() {

			public void onAnimationEnd(Animation3D anim) {
				anim.cancel();
				anim.reset();
			}

			public void onAnimationRepeat(Animation3D anim) {				
			}

			public void onAnimationStart(Animation3D anim) {
			}
			
		});
		blinkAnim.start();
		
	}
	
	private void checkBlockPositions(){
		//Handles motion right
		if(cityObj.getZ() < -(blockWidth*.75f) && cityObj2.getZ() < 0 ) cityObj.setZ(blockWidth*1.5f);
		if(cityObj2.getZ() < -(blockWidth*.75f) && cityObj.getZ() < 0 ) cityObj2.setZ(blockWidth*1.5f);

		//Handles motion left
		if(cityObj.getZ() > 0 && cityObj2.getZ() > (blockWidth) ) cityObj2.setZ(-blockWidth*1.5f);
		if(cityObj2.getZ() > 0 && cityObj.getZ() > (blockWidth) ) cityObj.setZ(-blockWidth*1.5f);
	}
	
	private void onTilt(float xVal, float yVal, float zVal) { //This method moves the camera vertically and the helo horizontally on tilt
		boolean checkY = (yVal<0); 
		if(useAccel){
			if(!isPortrait){
				yVal = xVal;
				xVal = (xVal-9.86f);
				if(checkY) xVal=-xVal;
			}else {
			}
						
			//Camera movement calculations
			int maxCamY = 15;
			yVal = (float) Math.round(((yVal+yTilt)*.5)*10)/10;
			
			if (yVal < 0f) yVal = 0f;
			
			yTilt = (float)(yVal)/9.8f;
			
			if ((float)zVal < 0) {
				yTilt = -yTilt;
				maxCamY = -maxCamY;
			}
			
			float newTiltY;
			
			if(camDrift){
				newTiltY = (float)(Math.sin(yTilt)*-20)+maxCamY+(wave*.4f);
			}else {
				newTiltY = (float)(Math.sin(yTilt)*-20)+maxCamY;
			}
			
			//Spotlight movement calculations
			xVal = (float) Math.round(((xVal+xTilt)*.5)*10)/10;
			
			xTilt =(float) (xVal)/9.8f;
			
			float newTiltX = (float) (Math.sin(xTilt)*-20);
			
			//Spotlight movement
			if(!autolight){
				if(mCamera != null)	mCamera.setY(newTiltY);
			}else {
//				xVal = (float) Math.round(xVal*10)/10;
				lightAnim.cancel();
				lightAnim.reset();
				heloLightObj.setRotX((-newTiltX*5)+50);
				heloLightObj.setRotY((-newTiltY*10)+10);				
			}
			xTilt = xVal;
			yTilt = yVal;
			
			//Helo movement
			if(!autopilot){
				parentObj.setRotX((xVal*2.5f));
				
				//Scene motion			
				cityObj.setPosition(cityObj.getX(),cityObj.getY(),cityObj.getZ()+(xVal/15));
				cityObj2.setPosition(cityObj2.getX(),cityObj2.getY(),cityObj2.getZ()+(xVal/15));
			}
			
		}else {
			mCamera.setY(0);
		}
	}

	private void swipeCamera(float touchOffset) {
       float zOffset = touchOffset/(10*homeScreenOffset);// 20 - 3-screens - 50 5-screens - 100 7-screens
       curZPos = mCamera.getPosition().z;
 	   if(curZPos < -25 || curZPos > 25){
	           if(-25>curZPos){ curZPos = -25;}
	           else {curZPos = 25; }    		   
 	   }
 	   mCamera.setX(50 -(-((curZPos + zOffset)/(10*homeScreenOffset))));
 	   mCamera.setZ((curZPos + (zOffset*.66f)));
	}
	
	private void flickCamera(float delta) {
		float flickTarget = 0;
	 	if (delta<0) flickTarget = flickStart + (getHomeScreenSpacing());
	 	if (delta>0) flickTarget = flickStart - (getHomeScreenSpacing());
	 	if (Math.abs(delta) < 200) flickTarget = flickStart;
	 	   
	  	if(flickTarget < -25 || flickTarget > 25){// 25 is the max we want the camera to go. Changing this will require changing additional hard coded parameters
		           if(-25>flickTarget){ flickTarget = -25;}
		           else {flickTarget = 25; }    		   
		   }
			Number3D startPos = new Number3D(mCamera.getPosition());
			Number3D target =  new Number3D(mCamera.getX(), mCamera.getY(), flickTarget);
			TranslateAnimation3D flickAnim = new TranslateAnimation3D(startPos, target);
			flickAnim.setDuration(300);
			flickAnim.setRepeatCount(0);
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
			});
		flickAnim.start();		
	}
	

	@SuppressWarnings("unused")
	private void objSerializer(int resourceId, String outputName){ //example Resource ID --> R.raw.myShape_Obj
		ObjParser objParser = new ObjParser(mContext.getResources(), mTextureManager, resourceId);
		objParser.parse();
		BaseObject3D serializer = objParser.getParsedObject();
		MeshExporter exporter = new MeshExporter(serializer);
		exporter.export(outputName, ExportType.SERIALIZED);	
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {		
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			setAccelerometerValues(event.values[0],event.values[1],event.values[2]);
		}		
	}
}