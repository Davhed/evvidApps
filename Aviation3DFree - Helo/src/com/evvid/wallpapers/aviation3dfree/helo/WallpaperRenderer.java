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

package com.evvid.wallpapers.aviation3dfree.helo;


import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.aviation3dfree.helo.R;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.lights.PointLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.PhongMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

//Serializer//
import rajawali.parser.ObjParser;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;


public class WallpaperRenderer extends RajawaliRenderer{
	
	int blockWidth = 600;
	private float  wave;
	private double waveIndex = 0;
	
	private Bitmap heloTex, rotorTex, tailRotorTex, skyTex;
	
	private PointLight pLight_key, pLight_key2, pLight_fill, pLight_fill2, pLight_fill3, pLight_rim, pLight_ground;
	
	private BaseObject3D[] cityArray;
	private BaseObject3D parentObj, heloObj, crewObj, 
	heloLightObj, heloWindowsObj, rotorObj, 
	tailRotorObj, ground, cityObj, cityObj2,
	sky, fgBuild, bgBuild, tempObj, cityBlock;

	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(30);
		setFogEnabled(true);
		
    }
		
	public void initScene() {
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
		try{
			clearChildren();
			super.onSurfaceDestroyed();
		} catch (Exception e){
			e.printStackTrace();
		}
		 heloTex.recycle();
		 rotorTex.recycle();
		 tailRotorTex.recycle();
		 skyTex.recycle();
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		frameSyncAnimation();
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
		
		
		PhongMaterial heloMat = new PhongMaterial();
		DiffuseMaterial rotorMat = new DiffuseMaterial();
		DiffuseMaterial tailRotorMat = new DiffuseMaterial();
		SimpleMaterial crewMat = new SimpleMaterial();
		SimpleMaterial skyMat = new SimpleMaterial();

		buildCity();

		parentObj = new BaseObject3D();	

		try {
			skyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.posz);
			heloTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_tex);
			rotorTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_rotor_tex);
			tailRotorTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ec145_tail_rotor_tex);

			ObjectInputStream ois;	
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.clouddome));
			sky = new BaseObject3D((SerializedObject3D)ois.readObject());
			skyMat.addTexture(mTextureManager.addTexture(skyTex));
			sky.setMaterial(skyMat);
			sky.setRotation(90, 0, 90);
			sky.setY(-1000);
			addChild(sky);
				
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
			heloLightObj.setPosition(-4.86249f, -6.0637f, -11.44f);
			heloLightObj.setRotY(-30);
			heloObj.addChild(heloLightObj);
		
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
			ground.setPosition(0, -100, 0);
			ground.addLight(pLight_ground);
			addChild(ground);
			
			int color = 0xff3200;
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bgbuildings));
			bgBuild = new BaseObject3D((SerializedObject3D)ois.readObject());
			bgBuild.setMaterial(cityMat);

			tempObj = new BaseObject3D();
			tempObj = bgBuild.clone();
			tempObj.setColor(color);
			tempObj.setPosition(-150, -100, -100);

			cityObj.addChild(tempObj);
			cityObj2.addChild(tempObj);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.block1));
			fgBuild = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgBuild.setMaterial(cityMat);
			tempObj = new BaseObject3D();
			tempObj = fgBuild.clone();
			tempObj.setColor(color);
			tempObj.setDoubleSided(true);
			tempObj.setPosition(-250, -100, -100);

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
		mCamera.setLookAt(0,0,0);
	}
		
	private void heloMovement() {
				
		wave = (float)(5*(Math.sin(waveIndex/100)));
		parentObj.getChildAt(0).setRotX(parentObj.getChildAt(0).getRotX()-70);
		parentObj.getChildAt(2).setRotY(parentObj.getChildAt(2).getRotY()+50);
		parentObj.setRotation(-wave*.5f-10, 180, wave*2);
		waveIndex++;
		
		parentObj.setY(wave/2);
		cityObj.setPosition(cityObj.getX(),cityObj.getY(),(float) (cityObj.getZ()-.5));
		cityObj2.setPosition(cityObj2.getX(),cityObj2.getY(),(float) (cityObj2.getZ()-.5));
	}
		
	private void checkBlockPositions(){
		//Handles motion right
		if(cityObj.getZ() < -(blockWidth*.75f) && cityObj2.getZ() < 0 ) cityObj.setZ(blockWidth*1.5f);
		if(cityObj2.getZ() < -(blockWidth*.75f) && cityObj.getZ() < 0 ) cityObj2.setZ(blockWidth*1.5f);

		//Handles motion left
		if(cityObj.getZ() > 0 && cityObj2.getZ() > (blockWidth) ) cityObj2.setZ(-blockWidth*1.5f);
		if(cityObj2.getZ() > 0 && cityObj.getZ() > (blockWidth) ) cityObj.setZ(-blockWidth*1.5f);
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