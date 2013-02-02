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

package com.evvid.wallpapers.aviation3d.lightplane;


import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.aviation3d.lightplane.R;

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
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
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
import android.view.animation.DecelerateInterpolator;

//Serializer//
import rajawali.parser.ObjParser;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;


public class WallpaperRenderer extends RajawaliRenderer  implements SensorEventListener{
	
	//HELO VARS//
	
	//int blockWidth = 600;
	//private int  flashTimer = 0;
	//private double waveIndex = 0;
	//private String swipeMode = "manual_swipe";
	//private Number3D mAccValues = new Number3D();
	private float xTilt, yTilt, autospeed/*, homeScreenOffset, wave, curZPos, flickStart, xStartPos, xPos, yPos, yDelta, maxY*/;
	private boolean autolight, altitude, bgClouds/*, useAccel, isPortrait, camDrift, camLock, autopilot*/;
	
	private Bitmap heloTex, rotorTex, tailRotorTex, lightTex/*, skyTex, greenTex, redTex, clouddomeTex*/;
	
	private Animation3D lightAnim;
	//private BaseObject3D[] cityArray;
	private BaseObject3D heloObj, crewObj, heloLightObj, lightSource, 
	tailLight, lightRaysObj, heloWindowsObj, rotorObj, 
	tailRotorObj, ground, cityObj, cityObj2, sunObj,
	fgBuild, bgBuild, tempObj, cityBlock/*, sky, parentObj, smallLight, strobeLight, clouddomeObj*/;
	
	//private SensorManager mSensorManager;
	//private OnSharedPreferenceChangeListener mListener;
	
	//end HELO
	
	private float homeScreenOffset, wave, tilt, curZPos, xpos, flickStart, maxY, yDelta, yPos, xStartPos = 0;
	private double waveIndex = 0, seed = Math.random();
	private int numClouds, flashTimer = 0;
	private boolean useAccel, autopilot, planeMoving, isPortrait, foreClouds, backClouds, camDrift, camLock;
	private String swipeMode = "manual_swipe";
	private Number3D mAccValues;

	private Bitmap cloudTex, cloud2Tex, cloud3Tex, clouddomeTex, skyTex, groundTex, cloudBankTex, planeTex,
	propTex, balloonTex, balloon2Tex, balloon3Tex, greenTex, redTex, whiteTex;
	
	private PointLight pLight_key, pLight_key2, pLight_fill, pLight_fill2, pLight_fill3,
	pLight_rim, pLight_balloon, pLight_balloon2, pLight_balloon3, pLight_ground;
	
	private BaseObject3D groundObj, clouddomeObj, cloudBankObj, 
	cloud, cloud2, cloud3, mountainsObj, planeObj, smallLight, 
	strobeLight, strobeLight2, beaconLight, pilotObj, windowsObj,
	propObj, parentObj, balloonObj, balloon, balloon2, balloon3;
	
	private Plane sky, sky2;
	
	private ObjectInputStream ois;
	
	private SimpleMaterial cloudMat, cloud2Mat, cloud3Mat;
	
	private SensorManager mSensorManager;
	private OnSharedPreferenceChangeListener mListener;
	
	
	//same
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(30);
		setFogEnabled(true);
		
    }
		
	public void initScene() {
		//HELO
		//mSensorManager = (SensorManager) mContext.getSystemService("sensor");
		
		setOnPreferenceChange();
		setPrefsToLocal();
		
		//don't set de lites twice you know///
		setLights();
		
		setScene();
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		
		mAccValues = new Number3D();
        mSensorManager = (SensorManager) mContext.getSystemService("sensor");
		
		PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		preferences.registerOnSharedPreferenceChangeListener(mListener);
		
		
		
        //HELO
        parentObj.getChildAt(1).getChildAt(0).setRotY(-30); //Init Light Rotation
		if(!autolight){
			lightAnim.start();
		}
		
	}
	
	//same
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { // Check for screen rotation
		super.onSurfaceChanged(gl, width, height);
		isPortrait = ((width/height)==0);
	}
	
	
	@Override
	public void onSurfaceDestroyed() {
		/*clearChildren();
		super.onSurfaceDestroyed();*/
		
		//HELO
		try{
			clearChildren();
			super.onSurfaceDestroyed();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		if(!autolight && lightAnim != null){
			lightAnim.cancel();
			lightAnim.reset();
		}
		//

		mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		preferences.unregisterOnSharedPreferenceChangeListener(mListener);
		
		skyTex.recycle();
		cloudTex.recycle();
		groundTex.recycle();
		clouddomeTex.recycle();
		cloudBankTex.recycle();
		planeTex.recycle();
		balloonTex.recycle();
		balloon2Tex.recycle();
		balloon3Tex.recycle();
		propTex.recycle();
		whiteTex.recycle();
		greenTex.recycle();
		redTex.recycle();
		cloudTex.recycle();
		cloud2Tex.recycle();
		cloud3Tex.recycle();
//		cloud4Tex.recycle();
//		cloud5Tex.recycle();
//		cloud6Tex.recycle();
		
		//recycle HELO textures--method not used in HELO
		heloTex.recycle(); 
		rotorTex.recycle();
		tailRotorTex.recycle();
		lightTex.recycle();

		System.gc();
	}

	//same
	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		frameSyncAnimation();
		//tiltCamera(mAccValues.x, mAccValues.y, mAccValues.z);
	}

    @Override  //This method moves the camera using the Android home screen swipe output. It's a better way, but not always supported
    public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
    	if(swipeMode.equals("home_screen_swipe")){
	    	float zOffset = (50*xOffset)-25;
	    	
	    	//different values in HELO
	    	mCamera.setX(25-(-((curZPos + zOffset)/50)));
	    	mCamera.setZ(curZPos + zOffset);
    	}
    }
	
	@Override //This method moves the camera using direct touch events. Tracking a flick and turning it into a procedural animation for smoothing
	public void onTouchEvent(MotionEvent me) {
		if(swipeMode.equals("manual_swipe")){
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
		
		
		//removed
		/*if(!autopilot){
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
	    	   yPos = me.getY();
	    	   planeMoving = true;
	       }
	       if (me.getAction() == MotionEvent.ACTION_MOVE) {
	    	   yDelta = (me.getY()-yPos)/1500;
	    	   float newY = parentObj.getY()-yDelta;
	     	   maxY = 5;
	    	   if(newY>maxY) newY = maxY;
	    	   if(newY<-maxY) newY = -maxY;
	   	       parentObj.setY(newY);
	   	       parentObj.setRotX((-yDelta*50));
	       }
	       if (me.getAction() == MotionEvent.ACTION_UP) {
	    	   planeMoving = false;
	       }*/
		
		//HELO
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
		   	       if(altitude){ parentObj.setY(newY);
		   	       }
	    	   }
	       }
		
	       try {
	           Thread.sleep(15);
	       } catch (Exception e) {
	       }			
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
	
	//changed
	/*public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}*/
	
	public void onAccuracyChanged(Sensor arg0, int arg1) {		
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			setAccelerometerValues(event.values[0],event.values[1],event.values[2]);
		}		
	}
	
	private void setOnPreferenceChange(){
		mListener = new OnSharedPreferenceChangeListener(){
				
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
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
					useAccel = sharedPreferences.getBoolean("accel_pref", true);		
				} 
				else if ("cam_drift".equals(key))
				{
					camDrift = sharedPreferences.getBoolean(key, true);
				} 
				else if ("cam_lock".equals(key))
				{
					camLock = sharedPreferences.getBoolean(key, true);
					//different
					mCamera.setLookAt(0,0,0);
				} 
				//different
				else if ("foreCloud_pref".equals(key))
				{
					foreClouds = sharedPreferences.getBoolean(key, true);
					if(!foreClouds) resetCloudPositions();
				} 
				//different
				else if ("cloud_num".equals(key))
				{
					numClouds = Integer.parseInt(sharedPreferences.getString(key, "2"));
					resetCloudPositions();
				} 
				//different
				else if ("backCloud_pref".equals(key))
				{
					backClouds = sharedPreferences.getBoolean(key, true);
				}
				
				else if ("autopilot_pref".equals(key))
				{
					autopilot = sharedPreferences.getBoolean(key, false);
					
					//HELO
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
		camLock = preferences.getBoolean("cam_lock", false);
		autopilot = preferences.getBoolean("autopilot_pref", false);
		
		//different
		foreClouds = preferences.getBoolean("foreCloud_pref", true);
		backClouds = preferences.getBoolean("backCloud_pref", true);
		numClouds = Integer.parseInt(preferences.getString("cloud_num", "2"));
		
		//HELO
		autospeed = ((float)(Integer.parseInt(preferences.getString("autospeed_pref", "2")))*.1f);
		altitude = preferences.getBoolean("altitude_pref", true);
		autolight = preferences.getBoolean("autolight_pref", false);
		
		
	}	
	
	private void setLights(){
		//HELO
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
		
		/*pLight_ground = new PointLight();
		pLight_ground.setPower(2500);
		pLight_ground.setAttenuation(0, 1000, .09f, .032f);
		pLight_ground.setPosition(-500, 500, 0);*/
		
		//end HELO
		
		/*pLight_key = new PointLight();
		pLight_key.setPower(4);
		pLight_key.setPosition(0, 7, 10);*/
		
		/*pLight_key2 = new PointLight();
		pLight_key2.setPower(2f);
		pLight_key2.setPosition(0, 7f, -5);*/
		
		/*pLight_fill = new PointLight();
		pLight_fill.setPower(1.5f);
		pLight_fill.setPosition(7, -1.5f, -10);*/
		
		/*pLight_fill2 = new PointLight();
		pLight_fill2.setPower(1f);
		pLight_fill2.setPosition(8, -1.5f, 1);*/
		
		/*pLight_fill3 = new PointLight();
		pLight_fill3.setPower(1f);
		pLight_fill3.setPosition(0 , 10 , 0);*/

		/*pLight_rim = new PointLight();
		pLight_rim.setPower(1.5f);
		pLight_rim.setPosition(-5, 1f, 0);*/
		
		pLight_balloon = new PointLight();
		pLight_balloon.setPower(3000);
		pLight_balloon.setPosition(0, 7, 20);
		
		pLight_balloon2 = new PointLight();
		pLight_balloon2.setPower(1500);
		pLight_balloon2.setPosition(0, 7, -20);
		
		pLight_balloon3 = new PointLight();
		pLight_balloon3.setPower(500);
		pLight_balloon3.setPosition(-400, 7, 0);
		
		pLight_ground = new PointLight();
		pLight_ground.setPower(5000);
		pLight_ground.setPosition(0, 200, 0);
	}
	
	private void setScene(){
		
		parentObj = new BaseObject3D();
		balloonObj = new BaseObject3D();
		
		mCamera.setFarPlane(200000);
		mCamera.setX(25f);
		mCamera.setFogNear(500);
		mCamera.setFogFar(850);
		mCamera.setFogColor(0x6688cb);
		mCamera.setLookAt(0,0,0);

		skyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sky_tex);
		SimpleMaterial skyMat = new SimpleMaterial();
		 
		sky = new Plane(30000, 20000, 1, 1, 1);
    	sky.setMaterial(skyMat);
    	sky.addTexture(mTextureManager.addTexture(skyTex));
    	sky.addLight(pLight_key);
    	sky.setX(-1500);
    	sky.setY(1200);
    	sky.setRotation(-90,-90,0);
        addChild(sky);
        
		sky2 = new Plane(50000, 50000, 1, 1, 1);
    	sky2.setMaterial(skyMat);
    	sky2.addTexture(mTextureManager.addTexture(skyTex));
    	sky2.addLight(pLight_key);
    	sky2.setX(-1500);
    	sky2.setY(1000);
    	sky2.setRotation(90,0,0);
        addChild(sky2);
        
      //HELO
		
      		//setLights();
      		
//      		mCamera.setFarPlane(20000);
//      		mCamera.setFogNear(0);
//      		mCamera.setFogFar(375);
//      		mCamera.setFogColor(0xf07d42);
//      		mCamera.setLookAt(0,0,0);
//      		mCamera.setX(50f);
      	//	mCamera.setPosition(0, 0, -50); //TODO: Chase cam mode
      		
      		//moved into try...
      		/*PhongMaterial heloMat = new PhongMaterial();
      		DiffuseMaterial rotorMat = new DiffuseMaterial();
      		DiffuseMaterial tailRotorMat = new DiffuseMaterial();
      		SimpleMaterial crewMat = new SimpleMaterial();
      		SimpleMaterial lightSourceMat = new SimpleMaterial();
      		SimpleMaterial lightMat = new SimpleMaterial();
      		SimpleMaterial strobeLightMat = new SimpleMaterial();
      		SimpleMaterial tailLightMat = new SimpleMaterial();
      		SimpleMaterial tailLight2Mat = new SimpleMaterial();
      		SimpleMaterial skyMat = new SimpleMaterial();
      		SimpleMaterial clouddomeMat = new SimpleMaterial();*/

      		//buildCity();
      		
      		//end HELO

		
		try {
			
			//HELO
			//skyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.posz);
			clouddomeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clouddome_tex);
			heloTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_tex);
			rotorTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_rotor_tex);
			tailRotorTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_tail_rotor_tex);
			lightTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lightrays_tex);
			//greenTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.green_light);
			//redTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_light);
			//end HELO
			
			groundTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ground);
			clouddomeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clouddome_tex);
			cloudBankTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cloudbg);
			planeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cessna_tex);
			propTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cessna_prop_tex);
			balloonTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.balloon_tex);
			balloon2Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.balloon2_tex);
			balloon3Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.balloon3_tex);
			whiteTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.white_light);
			greenTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.green_light);
			redTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_light);
			
			//HELO
			PhongMaterial heloMat = new PhongMaterial();
      		DiffuseMaterial rotorMat = new DiffuseMaterial();
      		DiffuseMaterial tailRotorMat = new DiffuseMaterial();
      		SimpleMaterial crewMat = new SimpleMaterial();
      		SimpleMaterial lightSourceMat = new SimpleMaterial();
      		SimpleMaterial lightMat = new SimpleMaterial();
      		//SimpleMaterial strobeLightMat = new SimpleMaterial();
      		//SimpleMaterial tailLightMat = new SimpleMaterial();
      		//SimpleMaterial tailLight2Mat = new SimpleMaterial();
      		//SimpleMaterial skyMat = new SimpleMaterial();
      		//SimpleMaterial clouddomeMat = new SimpleMaterial();
      		//end HELO
			
			SimpleMaterial cloudBankMat = new SimpleMaterial();
			SimpleMaterial clouddomeMat = new SimpleMaterial();
			SimpleMaterial simpleMat = new SimpleMaterial();
			SimpleMaterial strobeLightMat = new SimpleMaterial();
			SimpleMaterial tailLightMat = new SimpleMaterial();
			SimpleMaterial tailLight2Mat = new SimpleMaterial();		
			PhongMaterial planeMat = new PhongMaterial();
			PhongMaterial windowMat = new PhongMaterial();
			PhongMaterial propMat = new PhongMaterial();
			DiffuseMaterial groundMat = new DiffuseMaterial();
			DiffuseMaterial balloonMat = new DiffuseMaterial();
			DiffuseMaterial balloon2Mat = new DiffuseMaterial();
			DiffuseMaterial balloon3Mat = new DiffuseMaterial();

			if(backClouds)
			{
		    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.cloudbank));
				cloudBankObj = new BaseObject3D((SerializedObject3D)ois.readObject());
				cloudBankObj.setMaterial(cloudBankMat);
				cloudBankObj.setBlendingEnabled(true);
				cloudBankObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				cloudBankObj.addTexture(mTextureManager.addTexture(cloudBankTex));
				cloudBankObj.setDoubleSided(true);
				cloudBankObj.setScale(.065f);
				cloudBankObj.setRotation(180, 0, 0);
				cloudBankObj.setPosition(0,70,0);
				addChild(cloudBankObj);
				ois.close();
			}
					
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.mountains));
			mountainsObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			mountainsObj.setMaterial(groundMat);
			mountainsObj.addLight(pLight_ground);
			mountainsObj.setPosition(50,-100, 0);
			mountainsObj.setScale(.06f);
			addChild(mountainsObj);
			ois.close();	
			
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
			
			if(backClouds){
		    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.clouddome));
				clouddomeObj = new BaseObject3D((SerializedObject3D)ois.readObject());
				clouddomeObj.setMaterial(clouddomeMat);
				clouddomeObj.setBlendingEnabled(true);
				clouddomeObj.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				clouddomeObj.addTexture(mTextureManager.addTexture(clouddomeTex));
				clouddomeObj.setScale(.052f, .1f, .052f);
				clouddomeObj.setRotation(0, -90, 0);
				clouddomeObj.setPosition(0, -250, 0);
				addChild(clouddomeObj);
				ois.close();
			}

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.balloon));
	    	balloon = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	balloon.setMaterial(balloonMat);
	    	balloon.addTexture(mTextureManager.addTexture(balloonTex));
	    	balloon.setPosition(-250,-100,0);
	    	balloon.setRotY(90);
	    	balloon.addLight(pLight_balloon);
	    	balloon.addLight(pLight_balloon3);
			balloonObj.addChild(balloon);
			ois.close();	
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.balloon));
	    	balloon2 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	balloon2.setMaterial(balloon2Mat);
	    	balloon2.addTexture(mTextureManager.addTexture(balloon2Tex));
	    	balloon2.setPosition(-350,-80, 30);
	    	balloon2.setRotY(90);
	    	balloon2.addLight(pLight_balloon);
	    	balloon2.addLight(pLight_balloon2);
	    	balloon2.addLight(pLight_balloon3);
			balloonObj.addChild(balloon2);
			ois.close();	
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.balloon));
	    	balloon3 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	balloon3.setMaterial(balloon3Mat);
	    	balloon3.addTexture(mTextureManager.addTexture(balloon3Tex));
	    	balloon3.setPosition(-400,-110, -35);
	    	balloon3.setRotY(90);
	    	balloon3.addLight(pLight_balloon);
	    	balloon3.addLight(pLight_balloon2);
	    	balloon3.addLight(pLight_balloon3);
			balloonObj.addChild(balloon3);
			ois.close();
			
			balloonObj.setRotY(90);			
			addChild(balloonObj);
			
			//HELO
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
			//end HELO
			
			//hide plane
			/*ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.cessna));
			planeObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			planeMat.setShininess(80);
			planeMat.setAmbientColor(0x6688cb);
			planeMat.setAmbientIntensity(.15f);
			planeObj.setMaterial(planeMat);
			planeObj.addTexture(mTextureManager.addTexture(planeTex));
			planeObj.setZ(5);*/
			
			//Nav Lights
			/*smallLight = new Sphere(.25f, 5, 5);
			strobeLightMat.addTexture(mTextureManager.addTexture(whiteTex));
			smallLight.setMaterial(strobeLightMat);
			smallLight.setPosition(0f, -0.77f, -18.75f);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
			planeObj.addChild(smallLight);
			
			smallLight = new Sphere(.30f, 5, 5);
			smallLight.setMaterial(strobeLightMat);
			smallLight.setPosition(-20.8f, 2.4f, 1.25f);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			strobeLight = smallLight;
			strobeLight.setScale(new Number3D(0, 0, 0));
			planeObj.addChild(smallLight);
			
			smallLight = new Sphere(.30f, 5, 5);
			smallLight.setMaterial(strobeLightMat);
			smallLight.setPosition(20.8f, 2.4f, 1.25f);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			strobeLight2 = smallLight;
			strobeLight2.setScale(new Number3D(0, 0, 0));
			planeObj.addChild(smallLight);

			tailLightMat.addTexture(mTextureManager.addTexture(redTex));
			smallLight = new Sphere(.20f, 5, 5);
			smallLight.setMaterial(tailLightMat);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			smallLight.setRotY(0);
			smallLight.setPosition(-20.8f, 2.4f, 1.25f);
			planeObj.addChild(smallLight);

			smallLight = new Sphere(.3f, 5, 5);
			smallLight.setMaterial(tailLightMat);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			smallLight.setRotY(90);
			smallLight.setPosition(0, 5f, -17.9f);
			beaconLight = smallLight;
			beaconLight.setScale(new Number3D(0, 0, 0));
			planeObj.addChild(smallLight);
			
			smallLight = new Sphere(.20f, 50, 50);
			tailLight2Mat.addTexture(mTextureManager.addTexture(greenTex));
			smallLight.setMaterial(tailLight2Mat);
			smallLight.setBlendingEnabled(true);
			smallLight.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			smallLight.setPosition(20.8f, 2.5f, 1.25f);
			smallLight.setRotY(90);
			planeObj.addChild(smallLight);
			
			parentObj.addChild(planeObj);
			ois.close();
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.pilot));
			pilotObj = new BaseObject3D((SerializedObject3D)ois.readObject());
			simpleMat.setUseColor(true);
			pilotObj.setMaterial(simpleMat);
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
		}*/

		spawnCloudlets();
	}

	
	private void spawnCloudlets() {
		
		cloudMat = new SimpleMaterial();
		cloudTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cloud1);
		cloudMat.addTexture(mTextureManager.addTexture(cloudTex));
		
		cloud2Mat = new SimpleMaterial();
		cloud2Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cloud2);
		cloud2Mat.addTexture(mTextureManager.addTexture(cloud2Tex));

		cloud3Mat = new SimpleMaterial();
		cloud3Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.cloud3);
		cloud3Mat.addTexture(mTextureManager.addTexture(cloud3Tex));

		
		for(int i=0; i<=2; i++) {
			int minX, maxX, zMin, mScale = 0;
			double seed = Math.random();			

			if (i==0){
				minX = -100;
				maxX = -50;
				mScale = 400;
				zMin = 600;
				try {			
					ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.cloud));
					cloud = new BaseObject3D((SerializedObject3D)ois.readObject());

					cloud.setMaterial(cloudMat);
					cloud.setBlendingEnabled(true);
					cloud.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA); 
					cloud.setDoubleSided(true);
					cloud.setScale((float)((seed)*mScale)+100);
					cloud.setRotation(-90, 0, -90);	
					cloud.setPosition((float)(seed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(seed*zMin)+(zMin/3));
			
					addChild(cloud);
					ois.close();

				} catch (Exception e){
					e.printStackTrace();
				}
			}else if(i==1) {
				minX = -50;
				maxX = -20;
				mScale = 350;
				zMin = 700;
				try {			
					ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.cloud));
					cloud2 = new BaseObject3D((SerializedObject3D)ois.readObject());

					cloud2.setMaterial(cloud2Mat);
					cloud2.setBlendingEnabled(true);
					cloud2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA); 
					cloud2.setDoubleSided(true);
					cloud2.setScale((float)((seed)*mScale)+100);	
					cloud2.setRotation(-90, 0, -90);		
					cloud2.setPosition((float)(seed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(seed*zMin)+(zMin/3));
			
					addChild(cloud2);
					ois.close();
				} catch (Exception e){
					e.printStackTrace();
				}
			}else {
				minX = 0;
				maxX = 20;
				mScale = 350;
				zMin = 900;
				try {			
					ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.cloud));
					cloud3 = new BaseObject3D((SerializedObject3D)ois.readObject());

					cloud3.setMaterial(cloud3Mat);
					cloud3.setBlendingEnabled(true);
					cloud3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
					cloud3.setDoubleSided(true);
					cloud3.setScale((float)((seed)*mScale)+100);
					cloud3.setRotation(-90, 0, -90);	
					cloud3.setPosition((float)(seed*minX)+maxX, (float)(Math.random()*-10)+5, (float)(seed*zMin)+(zMin/3));
			
					addChild(cloud3);
					ois.close();
				} catch (Exception e){
					e.printStackTrace();
				}	
			}
			
		}
	}

	
	private void frameSyncAnimation() {
		heloMovement();
		//checkBlockPositions();
		
		if(backClouds && cloudBankObj != null) cloudBankObj.setRotY(cloudBankObj.getRotY() + .005f);
		if(camLock) mCamera.setLookAt(parentObj.getPosition());
		mountainsObj.setRotY(mountainsObj.getRotY() - .007f);
		groundObj.setRotY(groundObj.getRotY() - .025f);
		//planeMovement();
		balloonMovement();
		if(foreClouds) {
			seed = Math.random();			
			moveClouds();
		}else {
			resetCloudPositions();
		}
	}
	
	//HELO
	private void heloMovement() {
			
			/*if (flashTimer == 60) blink(strobeLight);
			if (flashTimer == 90) {
				blink(tailLight);
				flashTimer = 0;
			}
			flashTimer++;*/
			
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
	
	/*private void blink(BaseObject3D aLight){
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
		
	}*/
	//end HELO
	
	private void moveClouds(){ 
	
		if(cloud != null && numClouds > 0) {

			if (cloud.getZ() > -500) {
			cloud.setZ(cloud.getZ()- 1);
			} else{
				seed = Math.random();
				int minX = -100;
				int maxX = -50;
				int mScale = 400;
				int zMin = 600;
				cloud.setScale((float) (seed*mScale+100));
				cloud.setPosition((float)(seed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(seed*zMin)+(zMin/3));
			}
		}
		
		if(cloud2 != null && numClouds > 1) {

			if (cloud2.getZ() > -600) {
			cloud2.setZ(cloud2.getZ()-1);
			} else{
				seed = Math.random();
				int minX = -50;
				int maxX = -20;
				int mScale = 350;
				int zMin = 700;
				cloud2.setScale((float) (seed*mScale+100));
				cloud2.setPosition((float)(seed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(seed*zMin)+(zMin/3));
			}
		}
		
		if(cloud3 != null && numClouds > 2) {

			if (cloud3.getZ() > -800) {
			cloud3.setZ(cloud3.getZ()-1);
			} else{
				seed = Math.random();
				int minX = 5;
				int maxX = 10;
				int mScale = 350;
				int zMin = 900;
				cloud3.setScale((float) (seed*mScale+100));
				cloud3.setPosition((float)(seed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(seed*zMin)+(zMin/3));
			}
		}
	}
	
	private void resetCloudPositions(){
		if(cloud != null) {
			double mSeed = Math.random();			
			int minX = -100;
			int maxX = -50;
			int mScale = 400;
			int zMin = 600;
			cloud.setScale((float) (mSeed*mScale+100));
			cloud.setPosition((float)(mSeed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(mSeed*zMin)+200);
		}
		
		if(cloud2 != null) {
			double mSeed = Math.random();			
			int minX = -50;
			int maxX = -20;
			int mScale = 350;
			int zMin = 700;
			cloud2.setScale((float) (mSeed*mScale+100));
			cloud2.setPosition((float)(mSeed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(mSeed*zMin)+200);
		}
		
		if(cloud3 != null) {
			double mSeed = Math.random();			
			int minX = 0;
			int maxX = 20;
			int mScale = 350;
			int zMin = 900;
			cloud3.setScale((float) (mSeed*mScale+100));
			cloud3.setPosition((float)(mSeed*minX)+maxX, (float)(Math.random()*-20)+10, (float)(mSeed*zMin)+200);
		}
	}
	
	//disable plane
	/*private void planeMovement() {
		if (flashTimer == 60) blink(strobeLight);
		if (flashTimer == 90) {
			blink(strobeLight2);
			blink(beaconLight);
			flashTimer = 0;
		}
		flashTimer++;		
	
		parentObj.getChildAt(3).setRotZ(parentObj.getChildAt(3).getRotZ()-70);//Prop rotation
		
		wave = (float)(5*(Math.sin(waveIndex/100)));
		if(autopilot) {
			parentObj.setRotation(parentObj.getRotX()+wave/500, 0, parentObj.getRotZ()+wave/500);
			parentObj.setPosition((float)(1.5f*(Math.cos(waveIndex/1500))), (float)(5*(Math.sin(waveIndex/1500))), (float)(-(Math.cos(waveIndex/1500))));
		}else {
			parentObj.setRotation(parentObj.getRotX(), 0, parentObj.getRotZ()+wave/500);
		}
		waveIndex++;

		returnToLevel();
	}*/
	
	/*private void blink(final BaseObject3D aLight){
		Animation3D blinkAnim = new ScaleAnimation3D(new Number3D(1.5f,1.5f,1.5f));
		blinkAnim.setDuration(50);
		blinkAnim.setRepeatCount(1);
		blinkAnim.setRepeatMode(Animation3D.REVERSE);
		blinkAnim.setTransformable3D(aLight);
		blinkAnim.setAnimationListener(new Animation3DListener() {

			public void onAnimationEnd(Animation3D arg0) {
				arg0.cancel();
				arg0.reset();
				aLight.setScale(0);
			}

			public void onAnimationRepeat(Animation3D arg0) {				
			}

			public void onAnimationStart(Animation3D arg0) {
			}

			public void onAnimationUpdate(Animation3D animation,
					float interpolatedTime) {
				// TODO Auto-generated method stub
				
			}
			
		});
		blinkAnim.start();
		
	}*/
	
	private void returnToLevel(){
		if(!planeMoving){
			if(parentObj.getRotX() != 0){
				if(Math.round(parentObj.getRotX()) > 0){
					parentObj.setRotX(parentObj.getRotX()-.5f);
				}else if(Math.round(parentObj.getRotX()) < 0){
					parentObj.setRotX(parentObj.getRotX()+.5f);
				}else {
					parentObj.setRotX(0);
				}
			}
		}
	}
	
	private void balloonMovement(){
		balloonObj.setRotY(balloonObj.getRotY() - .020f);
		balloonObj.getChildAt(0).setY((float) (10*Math.sin(waveIndex/1000))-80);
		balloonObj.getChildAt(1).setY((float) (10*Math.sin(waveIndex/1000))-70);
		balloonObj.getChildAt(2).setY((float) (10*Math.sin(waveIndex/1000))-90);
	}
		
	/*private void tiltCamera(float xVal, float yVal, float zVal) { //This method moves the camera vertically on tilt
		if(useAccel){
			if(!isPortrait) yVal = xVal;
			
			int maxCamY = 25;
			yVal = (float) Math.round(((yVal+tilt)*.5)*10)/10;
			
			if (yVal < 0f) yVal = 0f;
			
			tilt = (float) Math.round((yVal/9.86)*1000)/1000;
			tilt = (float)(yVal)/9.8f;
			
			if ((float)zVal < 0) {
				tilt = -tilt;
				maxCamY = -maxCamY;
			}
			
			float newTiltY;
			
			if(camDrift){
				newTiltY = (float)(Math.sin(tilt)*-30)+maxCamY+(wave*.4f);
			}else {
				newTiltY = (float)(Math.sin(tilt)*-30)+maxCamY;
			}
				
			if(mCamera != null)	mCamera.setY(newTiltY);
	
			tilt = yVal;
		}else {
			mCamera.setY(0);
		}
	}*/
	
	//HELO
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
	//end HELO

	private void swipeCamera(float touchOffset) {
       float zOffset = touchOffset/(10*homeScreenOffset);// 20 - 3-screens - 50 5-screens - 100 7-screens
       curZPos = mCamera.getPosition().z;
 	   if(curZPos < -25 || curZPos > 25){
	           if(-25>curZPos){ curZPos = -25;}
	           else {curZPos = 25; }    		   
 	   }
 	   mCamera.setX(25-(-((curZPos + zOffset)/(10*homeScreenOffset))));
 	   mCamera.setZ((curZPos + (zOffset*.66f)));
	}
	
	private void flickCamera(float delta) {
		float flickTarget = 0;
	 	if (delta<0) flickTarget = flickStart + (getHomeScreenSpacing());
	 	if (delta>0) flickTarget = flickStart - (getHomeScreenSpacing());
	 	if (Math.abs(delta)<150) flickTarget = flickStart;
	 	   
	  	if(flickTarget < -25 || flickTarget > 25){
		           if(-25>flickTarget) {
		        	   flickTarget = -25;
		           } else {
		        	   flickTarget = 25;
		           }
		   }
			Number3D startPos = new Number3D(mCamera.getPosition());
			Number3D target =  new Number3D(mCamera.getX(), mCamera.getY(), flickTarget);
			TranslateAnimation3D flickAnim = new TranslateAnimation3D(startPos, target);
			flickAnim.setDuration(500);
			flickAnim.setInterpolator(new DecelerateInterpolator());
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

				public void onAnimationUpdate(Animation3D animation,
						float interpolatedTime) {
					// TODO Auto-generated method stub
					
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
}