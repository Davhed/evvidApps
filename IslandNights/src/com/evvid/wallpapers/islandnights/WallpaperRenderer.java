/*
 * 
 * 		Tiki 3D relies heavily on the Rajawali framework which can be found here:
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

package com.evvid.wallpapers.islandnights;

import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.islandnights.R;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import rajawali.animation.Animation3D;
import rajawali.animation.Animation3DListener;
import rajawali.animation.ScaleAnimation3D;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.SimpleMaterial;
import rajawali.math.Number3D;
//Serializer//
import rajawali.parser.ObjParser;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;


public class WallpaperRenderer extends RajawaliRenderer{
	
	private float xpos, ypos, sWave, cWave, camWave, worldRot;
	private int flyTimer = 0, starTimer = 0, flameCounter = 0, smokeCounter = 0, camIndex = 0, worldIndex = 0;
	private double waveIndex = 0, camWaveIndex = 0,  lastDistance;
	private boolean shooting =  false, sceneInit = false, firstTouch = true, moveCamera = false, moveCameraLook = false, freeMove = false;
	private Number3D [] cameraLook, worldPos;
	
	private DirectionalLight dLight_amb;
	
	private ObjectInputStream ois;
	
	private Plane flame1, flame2, flame3, flame4, flame5, flame6, flame7;
	
	private BaseObject3D parentObj, skydome, moon, ground, god, godlight, crater, volcano, mountains, water1, water2,
	trees1, trees2, trees3, bush1, bush2, bush3, bush4, bush5, bigplants, bigplants2, bigplants3, fgplants1, fgplants2, grass, totems, boat,
	interiors, castLight1, castLight2, castLight3, castLight4, castLight5, castLight6, dock, lilypads, mask,
	castLight7, castLight8, castLight9, castLight10,lodge, lodgeRoof, hut, hutRoof, fruit, torches, palm1, palm2, palm3, palm4, palm5,
	firefly, firefly2, firefly3, firefly4, firefly5, firefly6, flySwarm, flySwarm2, shootingStar,
	torch1, torch2, torch3, torch4, torch5, torch6, torch7, torch8, torch9, torch10, shadows, depthShadow,
	smoke1, smoke2, smoke3, smoke4, smoke5, smoke6, smoke7, smoke8, smoke9, smoke10, smoke11, smoke12,
	smoke13, smoke14, smoke15, smoke16, smoke17, smoke18, smoke19, smoke20, smokePlume, bbq, net1, cameraLookNull;
	
	private Bitmap moonTex, moon2Tex, moon3Tex, moon4Tex, moon5Tex, shootingStarTex, fireflyTex, skydomeTex, groundTex, mntVolTreesTex, lightingTex, waterDockTex,
	hutTotemTex, interPropTex, lodgeTorchTex, plantsTex, godTex, flameTex1, flameTex2, flameTex3, flameTex4, flameTex5, flameTex6, flameTex7,
	smokeTex1, smokeTex2, smokeTex3, smokeTex4, smokeTex5, smokeTex6, smokeTex7, smokeTex8, smokeTex9, smokeTex10, smokeTex11, smokeTex12,
	smokeTex13, smokeTex14, smokeTex15, smokeTex16, smokeTex17, smokeTex18, smokeTex19, smokeTex20, netTex;
	
	
	private SimpleMaterial flameMat1, flameMat2, flameMat3, flameMat4, flameMat5, flameMat6, flameMat7,
	smokeMat1, smokeMat2, smokeMat3, smokeMat4, smokeMat5, smokeMat6, smokeMat7, smokeMat8, smokeMat9,
	smokeMat10, smokeMat11, smokeMat12, smokeMat13, smokeMat14, smokeMat15, smokeMat16, smokeMat17, smokeMat18,
	smokeMat19, smokeMat20, moonMat, moon2Mat, moon3Mat, moon4Mat, moon5Mat; 
	
	private OnSharedPreferenceChangeListener mListener;
	private int performance = 30, starMode = 1, moonPhase = 2, camSpeed = 5;
	private boolean prefChanged, showSmoke, showFlames, lightFlicker, showFlies, showPlants;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(performance);
    }
		
	public void initScene() {
			
		setOnPreferenceChange();
		setPrefsToLocal();
		
		lightsCam();
		loadTextures();
		loadObjects();

		createFlySwarms();
		createTorchFlames();
		createSmokePlume();
		
		addObjects();
	}
	
	private void lightsCam(){

		dLight_amb = new DirectionalLight();
		dLight_amb.setDirection(-1, -.1f, .5f);
		dLight_amb.setPower(8);
		
		cameraLookNull = new BaseObject3D();
		
		cameraLook = new Number3D[6];
		cameraLook[0] = new Number3D(-10,0,0);
		cameraLook[1] = new Number3D(-10, 0, 0);
		cameraLook[2] = new Number3D(-10, -1, -4);
		cameraLook[3] = new Number3D(-10, .34f, .854f);
		cameraLook[4] = new Number3D(-30.4f, -.7f, 13);
		cameraLook[5] = new Number3D(-30.4f, -.7f, 11);
				
		worldPos = new Number3D[6];
		worldPos[0] = new Number3D(-65, -25, 5);
		worldPos[1] = new Number3D(-30, -10, 30);
		worldPos[2] = new Number3D(8, -15, 10);
		worldPos[3] = new Number3D(70, -12, -8);
		worldPos[4] = new Number3D(33.8f, -15f, 8f);
		worldPos[5] = new Number3D(10.3f, -15, -13.5f);

		worldRot = 0f;
		
		mCamera.setFarPlane(10000);
		mCamera.setPosition(0,0,0);
		mCamera.setLookAt(cameraLook[camIndex]);
		cameraLookNull.setPosition(cameraLook[camIndex]);

	}
	
	private void loadTextures(){
		moonTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.newmoon_tex);					
		moon2Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.slivermoon_tex);
		moon3Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.crescentmoon_tex);
		moon4Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.halfmoon_tex);
		moon5Tex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fullmoon_tex);
		skydomeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skydome_tex);
		groundTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ground_tex); 
		mntVolTreesTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mnt_vlcn_bgplm_tex); 
		lightingTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lights_tex); 
		hutTotemTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hut_ttm_tex); 
		interPropTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.boat_int_prp_tex); 
		lodgeTorchTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ldg_plm_trch_tex); 
		shootingStarTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shootingstar_tex);
		fireflyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.green_light);
		godTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.god_tex);
		plantsTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plants_tex);
		waterDockTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterdock_tex);
		netTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.net);
	}
	
	private void loadObjects(){		
		try {	
			SimpleMaterial skydomeMat = new SimpleMaterial();
			SimpleMaterial waterDockMat = new SimpleMaterial();
			SimpleMaterial shootingStarMat = new SimpleMaterial();
			SimpleMaterial groundMat = new SimpleMaterial();
			SimpleMaterial mntVolTreesMat = new SimpleMaterial();
			SimpleMaterial lightingMat = new SimpleMaterial();
			SimpleMaterial shadowMat = new SimpleMaterial();
			SimpleMaterial hutTotemMat = new SimpleMaterial();
			SimpleMaterial interPropMat = new SimpleMaterial();
			SimpleMaterial lodgeTorchMat = new SimpleMaterial();
			SimpleMaterial palmMat = new SimpleMaterial();
			SimpleMaterial netMat = new SimpleMaterial();
			MyPhongMaterial godMat = new MyPhongMaterial();
			MyPhongMaterial plantsMat = new MyPhongMaterial();
			moonMat = new SimpleMaterial();
			moon2Mat = new SimpleMaterial();
			moon3Mat = new SimpleMaterial();
			moon4Mat = new SimpleMaterial();
			moon5Mat = new SimpleMaterial();

			
			skydomeMat.addTexture(mTextureManager.addTexture(skydomeTex));
			moonMat.addTexture(mTextureManager.addTexture(moonTex));			
			moon2Mat.addTexture(mTextureManager.addTexture(moon2Tex));			
			moon3Mat.addTexture(mTextureManager.addTexture(moon3Tex));			
			moon4Mat.addTexture(mTextureManager.addTexture(moon4Tex));			
			moon5Mat.addTexture(mTextureManager.addTexture(moon5Tex));			
			waterDockMat.addTexture(mTextureManager.addTexture(waterDockTex));
			shootingStarMat.addTexture(mTextureManager.addTexture(shootingStarTex));			
			groundMat.addTexture(mTextureManager.addTexture(groundTex));			
			mntVolTreesMat.addTexture(mTextureManager.addTexture(mntVolTreesTex));			
			lightingMat.addTexture(mTextureManager.addTexture(lightingTex));			
			shadowMat.addTexture(mTextureManager.addTexture(lightingTex));			
			hutTotemMat.addTexture(mTextureManager.addTexture(hutTotemTex));			
			interPropMat.addTexture(mTextureManager.addTexture(interPropTex));			
			lodgeTorchMat.addTexture(mTextureManager.addTexture(lodgeTorchTex));			
			palmMat.addTexture(mTextureManager.addTexture(lodgeTorchTex));
			netMat.addTexture(mTextureManager.addTexture(netTex));
			godMat.addTexture(mTextureManager.addTexture(godTex));
			plantsMat.addTexture(mTextureManager.addTexture(plantsTex));
			
			godMat.setShininess(10);
			godMat.setSpecularColor(.05f, .05f, .1f, 1);			
			plantsMat.setSpecularColor(.5f, .5f, 1, .5f);
			
			//SCENERY//
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.skydome));
			skydome = new BaseObject3D((SerializedObject3D)ois.readObject());
			skydome.setPosition(150, 50, 0);
			skydome.setMaterial(skydomeMat);
			
			moon = new Plane(400, 400, 1, 1);
			moon.setRotation(-90, -90, 0);
			moon.setPosition(-2500, 700, 500);
			switch(moonPhase){
				case 0 :
					moon.setMaterial(moonMat);
					break;
				case 1 :
					moon.setMaterial(moon2Mat);
					break;
				case 2 :
					moon.setMaterial(moon3Mat);
					break;
				case 3 :
					moon.setMaterial(moon4Mat);
					break;
				case 4 :
					moon.setMaterial(moon5Mat);
					break;
				default :
					moon.setMaterial(moon3Mat);
					break;
			}
			moon.setBlendingEnabled(true);
			moon.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			moon.setLookAt(mCamera.getX()-moon.getX(), mCamera.getY()-moon.getY(), mCamera.getZ()-moon.getZ());
			
			shootingStar = new Plane(10, 10, 1, 1);
			shootingStar.setRotation(-45, -90, 0);
			shootingStar.setPosition(-550, 300, -300);
			shootingStar.setMaterial(shootingStarMat);
			shootingStar.setBlendingEnabled(true);
			shootingStar.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.crater));
			crater = new BaseObject3D((SerializedObject3D)ois.readObject());
			crater.setBlendingEnabled(true);
			crater.setScale(1, 1, 1.35f);
			crater.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
			crater.setMaterial(mntVolTreesMat);
			crater.setPosition(-20, 0, -5);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.volcano));
			volcano = new BaseObject3D((SerializedObject3D)ois.readObject());
			volcano.setBlendingEnabled(true);
			volcano.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			volcano.setMaterial(mntVolTreesMat);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.mountains));
			mountains = new BaseObject3D((SerializedObject3D)ois.readObject());
			mountains.setMaterial(mntVolTreesMat);
			mountains.setBlendingEnabled(true);
			mountains.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.ground));
			ground = new BaseObject3D((SerializedObject3D)ois.readObject());
			ground.setMaterial(groundMat);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.water1));
			water1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			water1.setBlendingEnabled(true);
			water1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			water1.setMaterial(waterDockMat);
	    	water1.setPosition(0, .25f, 0);
	    	water1.setScaleX(1.2f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.water2));
	    	water2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			water2.setBlendingEnabled(true);
			water2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
	    	water2.setMaterial(waterDockMat);
	    	water2.setPosition(0, 2.5f, 0);
	    	water2.setScaleX(1.2f);

			//FOLIAGE//
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.trees1));
			trees1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			trees1.setMaterial(mntVolTreesMat);
			trees1.setBlendingEnabled(true);
			trees1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.trees2));
			trees2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			trees2.setMaterial(mntVolTreesMat);
			trees2.setBlendingEnabled(true);
			trees2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.trees3));
			trees3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			trees3.setMaterial(mntVolTreesMat);
			trees3.setBlendingEnabled(true);
			trees3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush1));
			bush1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush1.setMaterial(plantsMat);
			bush1.setBlendingEnabled(true);
			bush1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush1.setDoubleSided(true);
			bush1.addLight(dLight_amb);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush2));
			bush2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush2.setMaterial(plantsMat);
			bush2.setBlendingEnabled(true);
			bush2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush2.setDoubleSided(true);
			bush2.addLight(dLight_amb);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush3));
			bush3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush3.setMaterial(plantsMat);
			bush3.setBlendingEnabled(true);
			bush3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush3.setDoubleSided(true);
			bush3.addLight(dLight_amb);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush4));
			bush4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush4.setMaterial(plantsMat);
			bush4.setBlendingEnabled(true);
			bush4.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush4.setDoubleSided(true);
			bush4.addLight(dLight_amb);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush5));
			bush5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush5.setMaterial(plantsMat);
			bush5.setBlendingEnabled(true);
			bush5.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush5.setDoubleSided(true);
			bush5.addLight(dLight_amb);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.palm1));
			palm1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			palm1.setMaterial(palmMat);
			palm1.setBlendingEnabled(true);
			palm1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			palm1.setDoubleSided(true);
			palm1.setPosition(-148.325f, 0, -1.08725f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.palm2));
			palm2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			palm2.setMaterial(palmMat);
			palm2.setBlendingEnabled(true);
			palm2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			palm2.setDoubleSided(true);
			palm2.setPosition(-126.725f, 0, 27.4999f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.palm3));
			palm3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			palm3.setMaterial(palmMat);
			palm3.setBlendingEnabled(true);
			palm3.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			palm3.setDoubleSided(true);
			palm3.setPosition(-90.0123f, 0, 16.7998f);
			palm3.setRotY(90);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.palm4));
			palm4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			palm4.setMaterial(palmMat);
			palm4.setBlendingEnabled(true);
			palm4.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			palm4.setDoubleSided(true);
			palm4.setPosition(-61.2587f, 0, -17.5232f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.palm5));
			palm5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			palm5.setMaterial(palmMat);
			palm5.setBlendingEnabled(true);
			palm5.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			palm5.setDoubleSided(true);
			palm5.setPosition(-108.085f, 0, -29.3138f);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bigplants));
			bigplants = new BaseObject3D((SerializedObject3D)ois.readObject());
			bigplants.setMaterial(plantsMat);
			bigplants.setBlendingEnabled(true);
			bigplants.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bigplants.setDoubleSided(true);
			bigplants.addLight(dLight_amb);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bigplants2));
			bigplants2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bigplants2.setMaterial(plantsMat);
			bigplants2.setBlendingEnabled(true);
			bigplants2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bigplants2.setDoubleSided(true);
			bigplants2.addLight(dLight_amb);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bigplants3));
			bigplants3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bigplants3.setMaterial(plantsMat);
			bigplants3.setBlendingEnabled(true);
			bigplants3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bigplants3.setDoubleSided(true);
			bigplants3.addLight(dLight_amb);
			bigplants3.setPosition(5, 1, 0);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass));
			grass = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass.setMaterial(plantsMat);
			grass.setBlendingEnabled(true);
			grass.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			grass.setDoubleSided(true);
			grass.addLight(dLight_amb);
					
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgplants1));
			fgplants1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgplants1.setMaterial(plantsMat);
			fgplants1.setBlendingEnabled(true);
			fgplants1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			fgplants1.setDoubleSided(true);
			fgplants1.addLight(dLight_amb);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgplants2));
			fgplants2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgplants2.setMaterial(plantsMat);
			fgplants2.setBlendingEnabled(true);
			fgplants2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			fgplants2.setDoubleSided(true);
			fgplants2.addLight(dLight_amb);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.lilypads));
			lilypads = new BaseObject3D((SerializedObject3D)ois.readObject());
			lilypads.setMaterial(plantsMat);
			lilypads.setBlendingEnabled(true);
			lilypads.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			lilypads.setPosition(1, .7f, -1);
			lilypads.addLight(dLight_amb);
 
			//BUILDINGS//
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.lodge));
			lodge = new BaseObject3D((SerializedObject3D)ois.readObject());
			lodge.setDoubleSided(true);
			lodge.setMaterial(lodgeTorchMat);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.lodgeroof));
			lodgeRoof = new BaseObject3D((SerializedObject3D)ois.readObject());
			lodgeRoof.setY(.25f);
			lodgeRoof.setDoubleSided(true);
			lodgeRoof.setBlendingEnabled(true);
			lodgeRoof.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			lodgeRoof.setMaterial(lodgeTorchMat);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.hut));
			hut = new BaseObject3D((SerializedObject3D)ois.readObject());
			hut.setBlendingEnabled(true);
			hut.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			hut.setMaterial(hutTotemMat);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.hutroof));
			hutRoof = new BaseObject3D((SerializedObject3D)ois.readObject());
			hutRoof.setY(.25f);
			hutRoof.setBlendingEnabled(true);
			hutRoof.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			hutRoof.setMaterial(hutTotemMat);
						
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.interiors));
			interiors = new BaseObject3D((SerializedObject3D)ois.readObject());
			interiors.setMaterial(interPropMat);
			
			//PROPS//
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fruit));
			fruit = new BaseObject3D((SerializedObject3D)ois.readObject());
			fruit.setMaterial(interPropMat);
			fruit.setDoubleSided(true);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.mask));
			mask = new BaseObject3D((SerializedObject3D)ois.readObject());
			mask.setMaterial(lodgeTorchMat);
					
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bbq));
			bbq = new BaseObject3D((SerializedObject3D)ois.readObject());
			bbq.setMaterial(interPropMat);
			bbq.setDoubleSided(true);
			bbq.setBlendingEnabled(true);
			bbq.setPosition(1, -1, 2);
			bbq.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.boat));
			boat = new BaseObject3D((SerializedObject3D)ois.readObject());
			boat.setMaterial(interPropMat);
			boat.setPosition(0, 0f, 4);
			boat.setRotY(-5);
			boat.setRotZ(-1.75f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.dock));
			dock = new BaseObject3D((SerializedObject3D)ois.readObject());
			dock.setMaterial(waterDockMat);
			dock.setY(1);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.net1));
	    	net1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			net1.setMaterial(netMat);
			net1.setBlendingEnabled(true);
			net1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			net1.setDoubleSided(true);
			net1.setY(1);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.torches));
			torches = new BaseObject3D((SerializedObject3D)ois.readObject());
			torches.setMaterial(lodgeTorchMat);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.totems));
			totems = new BaseObject3D((SerializedObject3D)ois.readObject());
			totems.setMaterial(hutTotemMat);
			totems.setDoubleSided(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.god));
			god = new BaseObject3D((SerializedObject3D)ois.readObject());
			god.setMaterial(godMat);
			god.setDoubleSided(true);
			god.addLight(dLight_amb);
			
			//LIGHTING//
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.godlight));
			godlight = new BaseObject3D((SerializedObject3D)ois.readObject());
			godlight.setMaterial(lightingMat);
			godlight.setBlendingEnabled(true);
			godlight.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows));
			shadows = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows.setMaterial(shadowMat);
			shadows.setY(-.1f);
			shadows.setBlendingEnabled(true);
			shadows.setBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.dshadow));
	    	depthShadow = new BaseObject3D((SerializedObject3D)ois.readObject());
			depthShadow.setMaterial(shadowMat);
			depthShadow.setBlendingEnabled(true);
			depthShadow.setBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl1));
			castLight1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight1.setMaterial(lightingMat);
			castLight1.setBlendingEnabled(true);
			castLight1.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			castLight1.setY(.5f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl2));
			castLight2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight2.setMaterial(lightingMat);
			castLight2.setBlendingEnabled(true);
			castLight2.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			castLight2.setY(.25f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl3));
			castLight3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight3.setMaterial(lightingMat);
			castLight3.setBlendingEnabled(true);
			castLight3.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl4));
			castLight4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight4.setMaterial(lightingMat);
			castLight4.setBlendingEnabled(true);
			castLight4.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			castLight4.setY(.75f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl5));
			castLight5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight5.setMaterial(lightingMat);
			castLight5.setBlendingEnabled(true);
			castLight5.setY(.5f);
			castLight5.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl6));
			castLight6 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight6.setMaterial(lightingMat);
			castLight6.setBlendingEnabled(true);
			castLight6.setY(1.2f);
			castLight6.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
				
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl7));
			castLight7 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight7.setY(.25f);
			castLight7.setMaterial(lightingMat);
			castLight7.setBlendingEnabled(true);
			castLight7.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl8));
			castLight8 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight8.setMaterial(lightingMat);
			castLight8.setPosition(5, -.2f, 1);
			castLight8.setBlendingEnabled(true);
			castLight8.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl9));
			castLight9 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight9.setMaterial(lightingMat);
			castLight9.setBlendingEnabled(true);
			castLight9.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl10));
			castLight10 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight10.setMaterial(lightingMat);
			castLight10.setBlendingEnabled(true);
			castLight10.setBlendFunc(GLES20.GL_ONE,  GLES20.GL_ONE_MINUS_SRC_COLOR);
			
			ois.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	private void createFlySwarms(){
		
		flySwarm = new BaseObject3D();
		
		SimpleMaterial fireflyMat = new SimpleMaterial();
		fireflyMat.addTexture(mTextureManager.addTexture(fireflyTex));
		
		firefly = new Sphere(.3f, 5, 5);
		firefly.setPosition(-20, 12, 0);
		firefly.setMaterial(fireflyMat);
		firefly.setBlendingEnabled(true);
		firefly.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		flySwarm.addChild(firefly);
		
		firefly2 = new Sphere(.3f, 5, 5);
		firefly2.setPosition(-35, 13, 10);
		firefly2.setMaterial(fireflyMat);
		firefly2.setBlendingEnabled(true);
		firefly2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		flySwarm.addChild(firefly2);
		
		firefly3 = new Sphere(.3f, 5, 5);
		firefly3.setPosition(-20, 9, -8);
		firefly3.setMaterial(fireflyMat);
		firefly3.setBlendingEnabled(true);
		firefly3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		flySwarm.addChild(firefly3);
		
		firefly4 = new Sphere(.3f, 5, 5);
		firefly4.setPosition(-40, 10, 5);
		firefly4.setMaterial(fireflyMat);
		firefly4.setBlendingEnabled(true);
		firefly4.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		flySwarm.addChild(firefly4);
		
		firefly5 = new Sphere(.3f, 5, 5);
		firefly5.setPosition(-60, 11, 15);
		firefly5.setMaterial(fireflyMat);
		firefly5.setBlendingEnabled(true);
		firefly5.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		flySwarm.addChild(firefly5);
		
		firefly6 = new Sphere(.3f, 5, 5);
		firefly6.setPosition(-30, 8, -16);
		firefly6.setMaterial(fireflyMat);
		firefly6.setBlendingEnabled(true);
		firefly6.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		flySwarm.addChild(firefly6);
		
		flySwarm.setPosition(-90, 0, -12);
		
		flySwarm2 = new BaseObject3D();
		
		for (int i = 0; i < (flySwarm.getNumChildren()); i++){	
			flySwarm.getChildAt(i).setScale(0,0,0);
			flySwarm2.addChild(flySwarm.getChildAt(i));
		}
		flySwarm2.setPosition(-30, 5, 25);
		flySwarm2.setScale(-1);
	
		firefly .setScale(0,0,0);
		firefly2.setScale(0,0,0);
		firefly3.setScale(0,0,0);
		firefly4.setScale(0,0,0);
		firefly5.setScale(0,0,0);
		firefly6.setScale(0,0,0);
	}
	
	private void createTorchFlames() {
		
		flameMat1 = new SimpleMaterial();
		flameMat2 = new SimpleMaterial();
		flameMat3 = new SimpleMaterial();
		flameMat4 = new SimpleMaterial();
		flameMat5 = new SimpleMaterial();
		flameMat6 = new SimpleMaterial();
		flameMat7 = new SimpleMaterial();

		flameTex1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fa);
		flameTex2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fb);
		flameTex3 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fc);
		flameTex4 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fd);
		flameTex5 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fe);
		flameTex6 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ff);
		flameTex7 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fg);
		
		torch1 = new BaseObject3D();
		
		flame1 = new Plane(2, 2, 1, 1);
		flameMat1.addTexture(mTextureManager.addTexture(flameTex1));
		flame1.setMaterial(flameMat1);
		flame1.setBlendingEnabled(true);
		flame1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		torch1.addChild(flame1);
		
		flame2 = new Plane(2, 2, 1, 1);
		flameMat2.addTexture(mTextureManager.addTexture(flameTex2));
		flame2.setMaterial(flameMat2);
		flame2.setBlendingEnabled(true);
		flame2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		torch1.addChild(flame2);
		
		flame3 = new Plane(2, 2, 1, 1);
		flameMat3.addTexture(mTextureManager.addTexture(flameTex3));
		flame3.setMaterial(flameMat3);
		flame3.setBlendingEnabled(true);
		flame3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		torch1.addChild(flame3);
		
		flame4 = new Plane(2, 2, 1, 1);
		flameMat4.addTexture(mTextureManager.addTexture(flameTex4));
		flame4.setMaterial(flameMat4);
		flame4.setBlendingEnabled(true);
		flame4.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		torch1.addChild(flame4);
		
		flame5 = new Plane(2, 2, 1, 1);
		flameMat5.addTexture(mTextureManager.addTexture(flameTex5));
		flame5.setMaterial(flameMat5);
		flame5.setBlendingEnabled(true);
		flame5.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		torch1.addChild(flame5);
		
		flame6 = new Plane(2, 2, 1, 1);
		flameMat6.addTexture(mTextureManager.addTexture(flameTex6));
		flame6.setMaterial(flameMat6);
		flame6.setBlendingEnabled(true);
		flame6.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		torch1.addChild(flame6);
		
		flame7 = new Plane(2, 2, 1, 1);
		flameMat7.addTexture(mTextureManager.addTexture(flameTex7));
		flame7.setMaterial(flameMat7);
		flame7.setBlendingEnabled(true);
		flame7.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		torch1.addChild(flame7);

		torch2 = new BaseObject3D();
		torch3 = new BaseObject3D();
		torch4 = new BaseObject3D();
		torch5 = new BaseObject3D();
		torch6 = new BaseObject3D();
		torch7 = new BaseObject3D();
		torch8 = new BaseObject3D();
		torch9 = new BaseObject3D();
		torch10 = new BaseObject3D();
		
		for (int i = 0; i < (torch1.getNumChildren()); i++){
			torch2.addChild(torch1.getChildAt(i));
			torch3.addChild(torch1.getChildAt(i));
			torch4.addChild(torch1.getChildAt(i));
			torch5.addChild(torch1.getChildAt(i));
			torch6.addChild(torch1.getChildAt(i));
			torch7.addChild(torch1.getChildAt(i));
			torch8.addChild(torch1.getChildAt(i));
			torch9.addChild(torch1.getChildAt(i));
			torch10.addChild(torch1.getChildAt(i));
		}
				
		torch1.setPosition(-38.1272f, 12.2953f+1, 30.236f);
		torch2.setPosition(-39.1312f, 11.0866f+1, 29.7689f);
		torch3.setPosition(-66.3349f, 13.9632f+1, -28.9282f);
		torch4.setPosition(-66.5845f, 15.052f+1, -30.2583f);
		torch5.setPosition(-74.1546f, 10.7302f+1, 4.34096f);
		torch6.setPosition(-85.7436f, 9.17131f+1, -4.48734f);
		torch7.setPosition(-90.4692f, 8.8846f+1, 10.1487f);
		torch8.setPosition(-102.391f, 8.7768f+1, -1.97802f);
		torch9.setPosition(-108.042f, 10.3061f+1, 29.9747f);
		torch10.setPosition(-127.889f, 9.37944f+1, 6.90424f);
		
		torch1.setLookAt(mCamera.getX()-torch1.getX(), mCamera.getY()-torch1.getY(), mCamera.getZ()-torch1.getZ());
		torch2.setLookAt(mCamera.getX()-torch2.getX(), mCamera.getY()-torch2.getY(), mCamera.getZ()-torch2.getZ());
		torch3.setLookAt(mCamera.getX()-torch3.getX(), mCamera.getY()-torch3.getY(), mCamera.getZ()-torch3.getZ());
		torch4.setLookAt(mCamera.getX()-torch4.getX(), mCamera.getY()-torch4.getY(), mCamera.getZ()-torch4.getZ());
		torch5.setLookAt(mCamera.getX()-torch5.getX(), mCamera.getY()-torch5.getY(), mCamera.getZ()-torch5.getZ());
		torch6.setLookAt(mCamera.getX()-torch6.getX(), mCamera.getY()-torch6.getY(), mCamera.getZ()-torch6.getZ());
		torch7.setLookAt(mCamera.getX()-torch7.getX(), mCamera.getY()-torch7.getY(), mCamera.getZ()-torch7.getZ());
		torch8.setLookAt(mCamera.getX()-torch8.getX(), mCamera.getY()-torch8.getY(), mCamera.getZ()-torch8.getZ());
		torch9.setLookAt(mCamera.getX()-torch9.getX(), mCamera.getY()-torch9.getY(), mCamera.getZ()-torch9.getZ());
		torch10.setLookAt(mCamera.getX()-torch10.getX(), mCamera.getY()-torch10.getY(), mCamera.getZ()-torch10.getZ());

	}

	private void createSmokePlume(){
		smokePlume = new BaseObject3D();
		smokeMat1 = new SimpleMaterial();
		smokeMat2 = new SimpleMaterial();
		smokeMat3 = new SimpleMaterial();
		smokeMat4 = new SimpleMaterial();
		smokeMat5 = new SimpleMaterial();
		smokeMat6 = new SimpleMaterial();
		smokeMat7 = new SimpleMaterial();
		smokeMat8 = new SimpleMaterial();
		smokeMat9 = new SimpleMaterial();
		smokeMat10 = new SimpleMaterial();
		smokeMat11 = new SimpleMaterial();
		smokeMat12 = new SimpleMaterial();
		smokeMat13 = new SimpleMaterial();
		smokeMat14 = new SimpleMaterial();
		smokeMat15 = new SimpleMaterial();
		smokeMat16 = new SimpleMaterial();
		smokeMat17 = new SimpleMaterial();
		smokeMat18 = new SimpleMaterial();
		smokeMat19 = new SimpleMaterial();
		smokeMat20 = new SimpleMaterial();
		
		smokeTex1 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sa);
		smokeTex2 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sb);
		smokeTex3 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sc);
		smokeTex4 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sd);
		smokeTex5 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.se);
		smokeTex6 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sf);
		smokeTex7 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sg);
		smokeTex8 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sh);
		smokeTex9 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.si);
		smokeTex10 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sj);
		smokeTex11 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sk);
		smokeTex12 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sl);
		smokeTex13 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sm);
		smokeTex14 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sn);
		smokeTex15 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.so);
		smokeTex16 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sp);
		smokeTex17 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sq);
		smokeTex18 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sr);
		smokeTex19 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ss);
		smokeTex20 = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.st);
		
		float pW = 190;
		float pH = 190;
		
		smoke1 = new Plane(pW, pH, 1, 1);
		smokeMat1.addTexture(mTextureManager.addTexture(smokeTex1));
		smoke1.setMaterial(smokeMat1);
		smoke1.setBlendingEnabled(true);
		smoke1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke1);
		
		smoke2 = new Plane(pW, pH, 1, 1);
		smokeMat2.addTexture(mTextureManager.addTexture(smokeTex2));
		smoke2.setMaterial(smokeMat2);
		smoke2.setBlendingEnabled(true);
		smoke2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke2);
		
		smoke3 = new Plane(pW, pH, 1, 1);
		smokeMat3.addTexture(mTextureManager.addTexture(smokeTex3));
		smoke3.setMaterial(smokeMat3);
		smoke3.setBlendingEnabled(true);
		smoke3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke3);
		
		smoke4 = new Plane(pW, pH, 1, 1);
		smokeMat4.addTexture(mTextureManager.addTexture(smokeTex4));
		smoke4.setMaterial(smokeMat4);
		smoke4.setBlendingEnabled(true);
		smoke4.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke4);
		
		smoke5 = new Plane(pW, pH, 1, 1);
		smokeMat5.addTexture(mTextureManager.addTexture(smokeTex5));
		smoke5.setMaterial(smokeMat5);
		smoke5.setBlendingEnabled(true);
		smoke5.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke5);
		
		smoke6 = new Plane(pW, pH, 1, 1);
		smokeMat6.addTexture(mTextureManager.addTexture(smokeTex6));
		smoke6.setMaterial(smokeMat6);
		smoke6.setBlendingEnabled(true);
		smoke6.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke6);
		
		smoke7 = new Plane(pW, pH, 1, 1);
		smokeMat7.addTexture(mTextureManager.addTexture(smokeTex7));
		smoke7.setMaterial(smokeMat7);
		smoke7.setBlendingEnabled(true);
		smoke7.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke7);
		
		smoke8 = new Plane(pW, pH, 1, 1);
		smokeMat8.addTexture(mTextureManager.addTexture(smokeTex8));
		smoke8.setMaterial(smokeMat8);
		smoke8.setBlendingEnabled(true);
		smoke8.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke8);
		
		smoke9 = new Plane(pW, pH, 1, 1);
		smokeMat9.addTexture(mTextureManager.addTexture(smokeTex9));
		smoke9.setMaterial(smokeMat9);
		smoke9.setBlendingEnabled(true);
		smoke9.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke9);
		
		smoke10 = new Plane(pW, pH, 1, 1);
		smokeMat10.addTexture(mTextureManager.addTexture(smokeTex10));
		smoke10.setMaterial(smokeMat10);
		smoke10.setBlendingEnabled(true);
		smoke10.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke10);
		
		smoke11 = new Plane(pW, pH, 1, 1);
		smokeMat11.addTexture(mTextureManager.addTexture(smokeTex11));
		smoke11.setMaterial(smokeMat11);
		smoke11.setBlendingEnabled(true);
		smoke11.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke11);
		
		smoke12 = new Plane(pW, pH, 1, 1);
		smokeMat12.addTexture(mTextureManager.addTexture(smokeTex12));
		smoke12.setMaterial(smokeMat12);
		smoke12.setBlendingEnabled(true);
		smoke12.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke12);
		
		smoke13 = new Plane(pW, pH, 1, 1);
		smokeMat13.addTexture(mTextureManager.addTexture(smokeTex13));
		smoke13.setMaterial(smokeMat13);
		smoke13.setBlendingEnabled(true);
		smoke13.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke13);
		
		smoke14 = new Plane(pW, pH, 1, 1);
		smokeMat14.addTexture(mTextureManager.addTexture(smokeTex14));
		smoke14.setMaterial(smokeMat14);
		smoke14.setBlendingEnabled(true);
		smoke14.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke14);
		
		smoke15 = new Plane(pW, pH, 1, 1);
		smokeMat15.addTexture(mTextureManager.addTexture(smokeTex15));
		smoke15.setMaterial(smokeMat15);
		smoke15.setBlendingEnabled(true);
		smoke15.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke15);
		
		smoke16 = new Plane(pW, pH, 1, 1);
		smokeMat16.addTexture(mTextureManager.addTexture(smokeTex16));
		smoke16.setMaterial(smokeMat16);
		smoke16.setBlendingEnabled(true);
		smoke16.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke16);
		
		smoke17 = new Plane(pW, pH, 1, 1);
		smokeMat17.addTexture(mTextureManager.addTexture(smokeTex17));
		smoke17.setMaterial(smokeMat17);
		smoke17.setBlendingEnabled(true);
		smoke17.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke17);
		
		smoke18 = new Plane(pW, pH, 1, 1);
		smokeMat18.addTexture(mTextureManager.addTexture(smokeTex18));
		smoke18.setMaterial(smokeMat18);
		smoke18.setBlendingEnabled(true);
		smoke18.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke18);
		
		smoke19 = new Plane(pW, pH, 1, 1);
		smokeMat19.addTexture(mTextureManager.addTexture(smokeTex19));
		smoke19.setMaterial(smokeMat19);
		smoke19.setBlendingEnabled(true);
		smoke19.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke19);
		
		smoke20 = new Plane(pW, pH, 1, 1);
		smokeMat20.addTexture(mTextureManager.addTexture(smokeTex20));
		smoke20.setMaterial(smokeMat20);
		smoke20.setBlendingEnabled(true);
		smoke20.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		smokePlume.addChild(smoke20);
		
		smokePlume.setPosition(-520, 175, -3);
		smokePlume.setScaleZ(1.02f);
		smokePlume.setLookAt(mCamera.getX()-smokePlume.getX(), mCamera.getY()-smokePlume.getY(), mCamera.getZ()-smokePlume.getZ());	
	}
	
	private void addObjects(){
		parentObj = new BaseObject3D();
		parentObj.addChild(skydome);
		parentObj.addChild(moon);
		parentObj.addChild(shootingStar);
		parentObj.addChild(crater);		
		parentObj.addChild(smokePlume);		
		parentObj.addChild(volcano);
		parentObj.addChild(mountains);
		parentObj.addChild(ground);
		parentObj.addChild(dock);
		parentObj.addChild(boat);
		parentObj.addChild(water1);
		parentObj.addChild(depthShadow);
		parentObj.addChild(water2);
		parentObj.addChild(shadows);
		parentObj.addChild(trees1);
		parentObj.addChild(trees2);
		parentObj.addChild(trees3);
		parentObj.addChild(palm1);
		parentObj.addChild(bush1);
		parentObj.addChild(bush2);
		parentObj.addChild(bush3);
		parentObj.addChild(grass);
		parentObj.addChild(god);
		parentObj.addChild(castLight1);
		parentObj.addChild(castLight2);
		parentObj.addChild(godlight);
		parentObj.addChild(castLight3);
		parentObj.addChild(castLight5);
		parentObj.addChild(palm2);
		parentObj.addChild(palm3);
		parentObj.addChild(totems);
		parentObj.addChild(castLight4);
		parentObj.addChild(castLight6);
		parentObj.addChild(lodge);
		parentObj.addChild(lodgeRoof);
		parentObj.addChild(interiors);
		parentObj.addChild(fruit);
		parentObj.addChild(bush4);
		parentObj.addChild(bigplants);
		parentObj.addChild(torches);		
		parentObj.addChild(torch9);		
		parentObj.addChild(torch10);
		parentObj.addChild(bigplants2);
		parentObj.addChild(palm5);	
		parentObj.addChild(torch7);		
		parentObj.addChild(castLight8);
		parentObj.addChild(hut);
		parentObj.addChild(palm4);
		parentObj.addChild(bush5);
		parentObj.addChild(hutRoof);
		parentObj.addChild(mask);
		parentObj.addChild(castLight7);
		parentObj.addChild(castLight9);
		parentObj.addChild(castLight10);
		parentObj.addChild(torch8);
		parentObj.addChild(flySwarm);
		parentObj.addChild(flySwarm2);		
		parentObj.addChild(torch6);
		parentObj.addChild(torch5);
		parentObj.addChild(torch4);
		parentObj.addChild(torch3);
		parentObj.addChild(torch2);
		parentObj.addChild(torch1);		
		parentObj.addChild(net1);
		parentObj.addChild(bbq);
		parentObj.addChild(bigplants3);
		parentObj.addChild(fgplants1);
		parentObj.addChild(fgplants2);
		parentObj.addChild(lilypads);
		addChild(parentObj);
		
		parentObj.setRotY(worldRot);
		parentObj.setPosition(worldPos[worldIndex]);
		
		sceneInit = true;
	}
	
	private void setOnPreferenceChange(){
		mListener = new OnSharedPreferenceChangeListener(){
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if ("camSpeed_pref".equals(key))
				{
					camSpeed = Integer.parseInt(sharedPreferences.getString(key, "5"));
				} 
				else if ("performance".equals(key))
				{
					performance = Integer.parseInt(sharedPreferences.getString(key, "30"));
					setFrameRate(performance);
				} 
				else if ("smokePlume_pref".equals(key))
				{
					showSmoke = sharedPreferences.getBoolean(key, true);
				} 
				else if ("torches_pref".equals(key))
				{
					showFlames = sharedPreferences.getBoolean(key, true);
				}
				else if ("flicker_pref".equals(key))
				{
					lightFlicker = preferences.getBoolean(key, true);
				}
				else if ("fireflies_pref".equals(key))
				{
					showFlies = sharedPreferences.getBoolean(key, true);
				}
				else if ("plants_pref".equals(key))
				{
					showPlants = sharedPreferences.getBoolean(key, true);
				}
				else if ("moon_phase".equals(key))
				{
					moonPhase = Integer.parseInt(sharedPreferences.getString(key, "2"));
					prefChanged = true;
				}
				else if ("shootingstar".equals(key))
				{
					starMode = Integer.parseInt(sharedPreferences.getString(key, "1"));
				}
				preferences = sharedPreferences;
			}
		};		
	}
	
	private void setPrefsToLocal(){
		prefChanged = false;
		camSpeed = Integer.parseInt(preferences.getString("camSpeed_pref", "5"));
		performance = Integer.parseInt(preferences.getString("performance", "30"));
		showSmoke = preferences.getBoolean("smokePlume_pref", true);
		showFlames = preferences.getBoolean("torches_pref", true);
		lightFlicker = preferences.getBoolean("flicker_pref", true);
		showFlies = preferences.getBoolean("fireflies_pref", true);
		showPlants = preferences.getBoolean("plants_pref", true);
		moonPhase = Integer.parseInt(preferences.getString("moon_phase", "2"));
		starMode = Integer.parseInt(preferences.getString("shootingstar", "1"));
	}
		
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		try{
			super.onSurfaceCreated(gl, config);
		}catch (Exception e){
			e.printStackTrace();
			initScene();
		}
        PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);
		preferences.registerOnSharedPreferenceChangeListener(mListener);
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

		preferences.unregisterOnSharedPreferenceChangeListener(mListener);
		recycleTextures();	
		sceneInit = false;
	}
	
	private void recycleTextures(){
		try{
			skydomeTex.recycle();
			groundTex.recycle();
			mntVolTreesTex.recycle();
			lightingTex.recycle(); 
			hutTotemTex.recycle(); 
			interPropTex.recycle();
			lodgeTorchTex.recycle();
			moonTex.recycle();
			moon2Tex.recycle();
			moon3Tex.recycle();
			moon4Tex.recycle();
			moon5Tex.recycle();
			shootingStarTex.recycle();
			fireflyTex.recycle();
			godTex.recycle();
			plantsTex.recycle();
			waterDockTex.recycle();
			netTex.recycle();
			flameTex1.recycle();
			flameTex2.recycle();
			flameTex3.recycle();
			flameTex4.recycle();
			flameTex5.recycle();
			flameTex6.recycle();
			smokeTex1.recycle();
			smokeTex2.recycle();
			smokeTex3.recycle();
			smokeTex4.recycle();
			smokeTex5.recycle();
			smokeTex6.recycle();
			smokeTex7.recycle();
			smokeTex8.recycle();
			smokeTex9.recycle();
			smokeTex10.recycle();
			smokeTex11.recycle();
			smokeTex12.recycle();
			smokeTex13.recycle();
			smokeTex14.recycle();
			smokeTex15.recycle();
			smokeTex16.recycle();
			smokeTex17.recycle();
			smokeTex18.recycle();
			smokeTex19.recycle();
			smokeTex20.recycle();
			System.gc();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		try{
			super.onDrawFrame(glUnused);
			if(sceneInit){
				showHide();
				waveGenerator();
				checkEngine();
				camControl();
				if(moveCamera)cameraMovement();
				if(camIndex == 0 && !moveCamera) cameraLookNull.setZ(cameraLook[camIndex].z+camWave);
				if(showFlies)flyMovement();
				if(starMode!=0)shootingStarMovement();
				if(showFlames)torchFlameMovement();
				if(showFlames && lightFlicker)castLightMovement();
				if(showSmoke)smokePlumeMovement();
				waterMovement();
				boatMovement();
				treeMovement();
				grassMovement();
				moon.setLookAt(mCamera.getX()-moon.getX(), mCamera.getY()-moon.getY(), mCamera.getZ()-moon.getZ());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showHide() {
		if(showSmoke) smokePlume.setVisible(true);
		else smokePlume.setVisible(false);

		if(starMode != 0 ) shootingStar.setVisible(true);
		else shootingStar.setVisible(false);		
		
		if(showFlames) {
			torch1.setVisible(true);
			torch2.setVisible(true);
			torch3.setVisible(true);
			torch4.setVisible(true);
			torch5.setVisible(true);
			torch6.setVisible(true);
			torch7.setVisible(true);
			torch8.setVisible(true);
			torch9.setVisible(true);
			torch10.setVisible(true);
			castLight1.setVisible(true);
			castLight2.setVisible(true);
			castLight3.setVisible(true);
			castLight4.setVisible(true);
			castLight5.setVisible(true);
			castLight6.setVisible(true);
			castLight7.setVisible(true);
			castLight8.setVisible(true);
			castLight9.setVisible(true);
			castLight10.setVisible(true);
		} else {
			torch1.setVisible(false);
			torch2.setVisible(false);
			torch3.setVisible(false);
			torch4.setVisible(false);
			torch5.setVisible(false);
			torch6.setVisible(false);
			torch7.setVisible(false);
			torch8.setVisible(false);
			torch9.setVisible(false);
			torch10.setVisible(false);
			castLight1.setVisible(false);
			castLight2.setVisible(false);
			castLight3.setVisible(false);
			castLight4.setVisible(false);
			castLight5.setVisible(false);
			castLight6.setVisible(false);
			castLight7.setVisible(false);
			castLight8.setVisible(false);
			castLight9.setVisible(false);
			castLight10.setVisible(false);
		}
		
		if(showFlies) {
			flySwarm.setVisible(true);
			flySwarm2.setVisible(true);
		} else {
			flySwarm.setVisible(false);
			flySwarm2.setVisible(false);			
		}
			
		if(showPlants){
			palm1.setVisible(true);
			bush3.setVisible(true);
			palm3.setVisible(true);
			bush4.setVisible(true);
			bush5.setVisible(true);
			palm4.setVisible(true);
			fgplants2.setVisible(true);
			lilypads.setVisible(true);
		} else{
			palm1.setVisible(false);
			bush3.setVisible(false);
			palm3.setVisible(false);
			bush4.setVisible(false);
			bush5.setVisible(false);
			palm4.setVisible(false);
			fgplants2.setVisible(false);
			lilypads.setVisible(false);	
		}
	}
	
	private void checkEngine(){
		if(mWallpaperEngine.isPreview() && mWallpaperEngine.isVisible() && prefChanged){
			prefChanged = false;
			switch(moonPhase){
				case 0 :
					moon.setMaterial(moonMat);
					moon.reload();
					break;
				case 1 :
					moon.setMaterial(moon2Mat);
					break;
				case 2 :
					moon.setMaterial(moon3Mat);
					break;
				case 3 :
					moon.setMaterial(moon4Mat);
					break;
				case 4 :
					moon.setMaterial(moon5Mat);
					break;
			}
			moon.reload();
		}
		if(!mWallpaperEngine.isPreview() && mWallpaperEngine.isVisible()  && prefChanged){
			prefChanged = false;
			switch(moonPhase){
			case 0 :
				moon.setMaterial(moonMat);
				break;
			case 1 :
				moon.setMaterial(moon2Mat);
				break;
			case 2 :
				moon.setMaterial(moon3Mat);
				break;
			case 3 :
				moon.setMaterial(moon4Mat);
				break;
			case 4 :
				moon.setMaterial(moon5Mat);
				break;
		}
			moon.reload();
		}

	}
	
	private void waveGenerator(){
		sWave = (float)((Math.sin(waveIndex/100)));
		cWave = (float)((Math.cos(waveIndex/100)));
		waveIndex++;
		camWave = (float)((Math.sin(camWaveIndex/100)));
		camWaveIndex++;
	}
		
	private void cameraPose() {
		if(parentObj.getPosition() != worldPos[camIndex]){
			moveCamera = true;	
		}
		if(cameraLookNull.getPosition() != cameraLook[camIndex]){
			moveCameraLook = true;	
		}
	}
	
	private void camControl() {
		mCamera.setLookAt(cameraLookNull.getPosition());
	}

	private void cameraTrack(float xOffset, float yOffset) {
        xOffset = xOffset/10f;
		yOffset = yOffset/10f;
		if (mCamera.getY() < 20) mCamera.setY(20);
		if (mCamera.getY() > 75) mCamera.setY(75);
  	    if (mCamera.getY() > 15 || (mCamera.getY() + yOffset) > 0) mCamera.setY((mCamera.getY() + yOffset));

		if (mCamera.getZ() < -100) mCamera.setZ(-100);
		if (mCamera.getZ() > 75) mCamera.setZ(75);
   	    mCamera.setZ((mCamera.getZ() + xOffset));
	}

	private void cameraDolly(double offset) {
	//	Number3D newPosition = new Number3D();
	//	if(offset < 0){
	//    	newPosition = cameraLook[camIndex];
	//    	newPosition.add(5, 0, -5);
	//	    }else if (offset > 0){
	//	    	newPosition = cameraPos[camIndex];
	//	    }
	//	Animation3D zoomAnim = new TranslateAnimation3D(newPosition);
	//	zoomAnim.setDuration(200);
	//	zoomAnim.setRepeatCount(0);
	//	zoomAnim.setTransformable3D(mCamera);
	//	zoomAnim.setAnimationListener(new Animation3DListener() {
	//
	//		public void onAnimationEnd(Animation3D anim) {
	//			anim.cancel();
	//			anim.reset();
	//			firstTouch = false;
	//		}
	//
	//		public void onAnimationRepeat(Animation3D anim) {				
	//		}
	//
	//		public void onAnimationStart(Animation3D anim) {
	//		}
	//		
	//	});
	//	zoomAnim.start();
	
	//	if (mCamera.getX() < -50) mCamera.setX(-50); 
	//	if (mCamera.getX() > 35) mCamera.setX(35); 
	//	if (mCamera.getX() >= -50) mCamera.setX((float) (mCamera.getX()+(offset*.05)));
	}

	private void cameraMovement() {
		float xDif = worldPos[camIndex].x - parentObj.getX();
		float yDif = worldPos[camIndex].y - parentObj.getY();
		float zDif = worldPos[camIndex].z - parentObj.getZ();
		
		float newX = parentObj.getX()+(xDif/camSpeed);
		float newY = parentObj.getY()+(yDif/camSpeed);
		float newZ = parentObj.getZ()+(zDif/camSpeed);
		
		float xLookDif = cameraLook[camIndex].x - cameraLookNull.getX();
		float yLookDif = cameraLook[camIndex].y - cameraLookNull.getY();
		float zLookDif = cameraLook[camIndex].z - cameraLookNull.getZ();
		
		float newLookX = cameraLookNull.getX()+(xLookDif/(camSpeed*2));
		float newLookY = cameraLookNull.getY()+(yLookDif/(camSpeed*2));
		float newLookZ = cameraLookNull.getZ()+(zLookDif/(camSpeed*2));
		
		cameraLookNull.setPosition(newLookX, newLookY, newLookZ);
		parentObj.setPosition(newX, newY, newZ);
		if(moveCameraLook){
			camWaveIndex = 0;
			if (Math.abs(xLookDif)<.0001f && Math.abs(yLookDif)<.0001f && Math.abs(zLookDif)<.0001f) {
				cameraLookNull.setPosition(cameraLook[camIndex]);
				moveCameraLook = false;
			}
		}
		if (moveCamera && Math.abs(xDif)<.001f && Math.abs(yDif)<.001f && Math.abs(zDif)<.001f) {
			parentObj.setPosition(worldPos[camIndex]);
			moveCamera = false;	
		}
	}
		
	private void waterMovement(){
		water1.setPosition(water1.getX()+(sWave/40), (water1.getY()+(cWave/400)), water1.getZ()+(sWave/40));
		water2.setPosition(water2.getX()+(cWave/40), (water2.getY()+(cWave/400)), water2.getZ()+(sWave/40));
		depthShadow.setY(depthShadow.getY()+(cWave/400));
		if(showPlants)lilypads.setPosition(lilypads.getX()+(cWave/80), lilypads.getY()+(cWave/400), lilypads.getZ()+(sWave/100));
	}
	
	private void boatMovement(){
		boat.setPosition(boat.getX(), boat.getY()+(cWave/400), boat.getZ());
	}

	private void treeMovement(){
		if(showPlants){
			palm1.setRotX((float) (palm1.getRotX()+(Math.sin(waveIndex/30)/30)));
			palm3.setRotX((float) (palm3.getRotX()+(Math.sin(waveIndex/32)/32)));
			palm4.setRotX((float) (palm4.getRotX()+(Math.sin(waveIndex/36)/36)));
		}
		palm2.setRotX((float) (palm2.getRotX()+(Math.sin(waveIndex/35)/35)));
		palm5.setRotX((float) (palm5.getRotX()+(Math.sin(waveIndex/28)/28)));
	}
	
	private void grassMovement(){
		fgplants1.setRotation((float) (fgplants1.getRotX()+(Math.sin(waveIndex/60)/120)), fgplants1.getRotY(), (float) (fgplants1.getRotZ()+(Math.sin(waveIndex/20)/80)));
		if(showPlants)fgplants2.setRotation((float) (fgplants2.getRotX()+(Math.cos(waveIndex/60)/120)), fgplants2.getRotY(), (float) (fgplants2.getRotZ()+(Math.cos(waveIndex/20)/80)));
	}

	private void shootingStarMovement(){
		if(starMode==1){
	
			if (Math.random() >= .997f && !shooting && starTimer > 3600){
				shooting = true;
				Animation3D starAnim = new TranslateAnimation3D(new Number3D(-550, 10, 60));
				starAnim.setDuration(1500);
				starAnim.setRepeatCount(0);
				starAnim.setRepeatMode(Animation3D.RESTART);
				starAnim.setTransformable3D(shootingStar);
				starAnim.setAnimationListener(new Animation3DListener() {
		
					public void onAnimationEnd(Animation3D anim) {
						anim.cancel();
						anim.reset();
						shootingStar.setPosition(-550, 300, -300);
						shooting = false;
						starTimer = 0;
					}
		
					public void onAnimationRepeat(Animation3D anim) {				
					}
		
					public void onAnimationStart(Animation3D anim) {
					}

					public void onAnimationUpdate(Animation3D animation,
							float interpolatedTime) {
						// TODO Auto-generated method stub
						
					}					
				});
				starAnim.start();
			}else if (!shooting && starTimer > 2700){
				starTimer = 0;			
			}
		}
		else if(starMode==2)
		{			
			if (Math.random() >= .97f && !shooting && starTimer > 80){
				final float seed = (float) Math.random();
				shooting = true;
				Animation3D starAnim = new TranslateAnimation3D(new Number3D(-550, 10, (seed*620)-(seed*200)));
				starAnim.setDuration((long)(seed*2)*1750);
				starAnim.setRepeatCount(0);
				starAnim.setRepeatMode(Animation3D.RESTART);
				starAnim.setTransformable3D(shootingStar);
				starAnim.setAnimationListener(new Animation3DListener() {
		
					public void onAnimationEnd(Animation3D anim) {
						anim.cancel();
						anim.reset();
						shootingStar.setPosition(-550, 350, (seed*-650));
						starTimer = 0;
						shooting = false;
					}
		
					public void onAnimationRepeat(Animation3D anim) {				
					}
		
					public void onAnimationStart(Animation3D anim) {
					}

					public void onAnimationUpdate(Animation3D animation,
							float interpolatedTime) {
						// TODO Auto-generated method stub
						
					}
					
				});
				starAnim.start();
			}
		}
		
		starTimer++;
	}
	
	private void flyMovement(){
		if (flyTimer == 60) {
			if(Math.random() > .33) blink(firefly);
			if(Math.random() > .5) blink(firefly4);
		}
		else if (flyTimer == 90) {
			if(Math.random() > .33) blink(firefly2);
			if(Math.random() > .5) blink(firefly5);
		}
		else if (flyTimer == 120) {
			if(Math.random() > .33)  blink(firefly3);
		}
		else if (flyTimer == 140) {
			if(Math.random() > .33)  blink(firefly6);
			flyTimer = 0;
		}
		flyTimer++;
		firefly .setPosition((float) (firefly .getX()-(((Math.random()*5)*sWave/10)-(sWave/20))), (float) (firefly .getY()-(((Math.random()*5)*sWave/10)-(sWave/20))), (float) (firefly .getZ()-(((Math.random()*5)*sWave/10)-(sWave/20))));
		firefly2.setPosition((float) (firefly2.getX()-(((Math.random()*5)*cWave/10)-(sWave/20))), (float) (firefly2.getY()-(((Math.random()*5)*cWave/10)-(sWave/20))), (float) (firefly2.getZ()-(((Math.random()*5)*cWave/10)-(sWave/20))));
		firefly3.setPosition((float) (firefly3.getX()-(((Math.random()*5)*cWave/10)-(cWave/20))), (float) (firefly3.getY()-(((Math.random()*5)*cWave/10)-(cWave/20))), (float) (firefly3.getZ()-(((Math.random()*5)*cWave/10)-(cWave/20))));
		firefly4.setPosition((float) (firefly4.getX()+(((Math.random()*5)*sWave/10)-(sWave/20))), (float) (firefly4.getY()+(((Math.random()*5)*sWave/10)-(sWave/20))), (float) (firefly4.getZ()+(((Math.random()*5)*sWave/10)-(sWave/20))));
		firefly5.setPosition((float) (firefly5.getX()+(((Math.random()*5)*cWave/10)-(sWave/20))), (float) (firefly5.getY()+(((Math.random()*5)*cWave/10)-(sWave/20))), (float) (firefly5.getZ()+(((Math.random()*5)*cWave/10)-(sWave/20))));
		firefly6.setPosition((float) (firefly6.getX()+(((Math.random()*5)*cWave/10)-(cWave/20))), (float) (firefly6.getY()+(((Math.random()*5)*cWave/10)-(cWave/20))), (float) (firefly6.getZ()+(((Math.random()*5)*cWave/10)-(cWave/20))));
	}
	
	private void castLightMovement(){ // REMOVED 12/20/2012 and re-added 1/3/2013
		castLight1.setScale(flickerSeed(), 1, flickerSeed());
		castLight2.setScale(flickerSeed(), 1, flickerSeed());
		castLight3.setScale(flickerSeed(), 1, flickerSeed());
		castLight4.setScale(flickerSeed(), 1, flickerSeed());
		castLight5.setScale(flickerSeed(), 1, flickerSeed());
		castLight6.setScale(flickerSeed(), 1, flickerSeed());
		castLight7.setScale(flickerSeed(), 1, flickerSeed());
		castLight8.setScale(flickerSeed(), 1, flickerSeed());
		castLight9.setScale(flickerSeed(), 1, flickerSeed());
		castLight10.setScale(flickerSeed(), 1, flickerSeed());
	}
	
	private float flickerSeed(){
		float seed = (float) Math.random();
		while (seed < .99f){
			 seed = (float) Math.random();
		}
		return seed;		
	}
	
	private void torchFlameMovement(){
		switch (flameCounter){
			case 0: flame1.setVisible(true);
			flame7.setVisible(false);
			flameCounter++;	
			break;
			case 2: flame2.setVisible(true);
			flame1.setVisible(false);
			flameCounter++;	
			break;
			case 4: flame3.setVisible(true);
			flame2.setVisible(false);
			flameCounter++;	
			break;
			case 6: flame4.setVisible(true);
			flame3.setVisible(false);
			flameCounter++;	
			break;
			case 8: flame5.setVisible(true);
			flame4.setVisible(false);
			flameCounter++;	
			break;
			case 10: flame6.setVisible(true);
			flame5.setVisible(false);
			flameCounter++;	
			break;
			case 12: flame7.setVisible(true);
			flame6.setVisible(false);
			flameCounter=0;
			break;
			default: flameCounter++;	
			break;
		}
		torch1.setLookAt(mCamera.getX()-torch1.getX(), mCamera.getY()-torch1.getY(), mCamera.getZ()-torch1.getZ());
		torch2.setLookAt(mCamera.getX()-torch2.getX(), mCamera.getY()-torch2.getY(), mCamera.getZ()-torch2.getZ());
		torch3.setLookAt(mCamera.getX()-torch3.getX(), mCamera.getY()-torch3.getY(), mCamera.getZ()-torch3.getZ());
		torch4.setLookAt(mCamera.getX()-torch4.getX(), mCamera.getY()-torch4.getY(), mCamera.getZ()-torch4.getZ());
		torch5.setLookAt(mCamera.getX()-torch5.getX(), mCamera.getY()-torch5.getY(), mCamera.getZ()-torch5.getZ());
		torch6.setLookAt(mCamera.getX()-torch6.getX(), mCamera.getY()-torch6.getY(), mCamera.getZ()-torch6.getZ());
		torch7.setLookAt(mCamera.getX()-torch7.getX(), mCamera.getY()-torch7.getY(), mCamera.getZ()-torch7.getZ());
		torch8.setLookAt(mCamera.getX()-torch8.getX(), mCamera.getY()-torch8.getY(), mCamera.getZ()-torch8.getZ());
		torch9.setLookAt(mCamera.getX()-torch9.getX(), mCamera.getY()-torch9.getY(), mCamera.getZ()-torch9.getZ());
		torch10.setLookAt(mCamera.getX()-torch10.getX(), mCamera.getY()-torch10.getY(), mCamera.getZ()-torch10.getZ());
		
	}
	
	private void smokePlumeMovement(){
		switch(smokeCounter){
		case 0: smoke1.setVisible(true);
		smoke20.setVisible(false);
		smokeCounter++;
		break; 
		case 2: smoke2.setVisible(true);
		smoke1.setVisible(false);
		smokeCounter++;
		break; 
		case 4: smoke3.setVisible(true);
		smoke2.setVisible(false);
		smokeCounter++;
		break;
		case 6: smoke4.setVisible(true);
		smoke3.setVisible(false);
		smokeCounter++;
		break; 
		case 8: smoke5.setVisible(true);
		smoke4.setVisible(false);
		smokeCounter++;
		break; 
		case 10: smoke6.setVisible(true);
		smoke5.setVisible(false);
		smokeCounter++;
		break; 
		case 12: smoke7.setVisible(true);
		smoke6.setVisible(false);
		smokeCounter++;
		break;
		case 14: smoke8.setVisible(true);
		smoke7.setVisible(false);
		smokeCounter++;
		break;
		case 16: smoke9.setVisible(true);
		smoke8.setVisible(false);
		smokeCounter++;
		break;
		case 18: smoke10.setVisible(true);
		smoke9.setVisible(false);
		smokeCounter++;
		break;
		case 20: smoke11.setVisible(true);
		smoke10.setVisible(false);
		smokeCounter++;
		break; 
		case 22: smoke12.setVisible(true);
		smoke11.setVisible(false);
		smokeCounter++;
		break; 
		case 24: smoke13.setVisible(true);
		smoke12.setVisible(false);
		smokeCounter++;
		break;
		case 26: smoke14.setVisible(true);
		smoke13.setVisible(false);
		smokeCounter++;
		break; 
		case 28: smoke15.setVisible(true);
		smoke14.setVisible(false);
		smokeCounter++;
		break; 
		case 30: smoke16.setVisible(true);
		smoke15.setVisible(false);
		smokeCounter++;
		break; 
		case 32: smoke17.setVisible(true);
		smoke16.setVisible(false);
		smokeCounter++;
		break;
		case 34: smoke18.setVisible(true);
		smoke17.setVisible(false);
		smokeCounter++;
		break;
		case 36: smoke19.setVisible(true);
		smoke18.setVisible(false);
		smokeCounter++;
		break;
		case 38: smoke20.setVisible(true);
		smoke19.setVisible(false);
		smokeCounter = 0;
		break;
		default: smokeCounter++;
		break;
		}
		smokePlume.setLookAt(mCamera.getX()-smokePlume.getX(), mCamera.getY()-smokePlume.getY(), mCamera.getZ()-smokePlume.getZ());	
		smokePlume.setScaleZ(smokePlume.getScaleZ()+(sWave/600));
	}
	
	private void blink(final BaseObject3D aLight){
		Animation3D blinkAnim = new ScaleAnimation3D(new Number3D(1, 1, 1));
		blinkAnim.setDuration(500);
		blinkAnim.setRepeatCount(1);
		blinkAnim.setRepeatMode(Animation3D.REVERSE);
		blinkAnim.setTransformable3D(aLight);
		blinkAnim.setAnimationListener(new Animation3DListener() {

			public void onAnimationEnd(Animation3D anim) {
				anim.cancel();
				anim.reset();
				aLight.setScale(0);
			}

			public void onAnimationRepeat(Animation3D anim) {				
			}

			public void onAnimationStart(Animation3D anim) {
			}

			public void onAnimationUpdate(Animation3D animation,
					float interpolatedTime) {
				// TODO Auto-generated method stub
				
			}
			
		});
		blinkAnim.start();
	}
	
	@Override //This method moves the camera using direct touch events. Tracking a flick and turning it into a procedural animation for smoothing
	public void onTouchEvent(MotionEvent me) {
	       if (me.getAction() == MotionEvent.ACTION_DOWN) {
	           xpos = me.getX();
	           ypos = me.getY();
	    	   lastDistance = 0;
	    		   		
	       }
	       if (me.getAction() == MotionEvent.ACTION_MOVE) {
	           float xd = xpos - me.getX(0);
	           float yd = ypos - me.getY(0);

	    	   if(me.getPointerCount()==1 && firstTouch) {
	    		   if(freeMove) cameraTrack(xd, yd);//Use free motion code if enabled otherwise select next camIndex and use programatic animation
	    		   else{
		    		   if(xd>5){
		    			   camIndex++;
		    			   if(camIndex > (cameraLook.length - 1)) camIndex = 0;		    			   
			    		   firstTouch = false;
		    		   }
		    		   else if (xd<-5){
		    			   camIndex--;
		    			   if(camIndex < 0) camIndex = (cameraLook.length - 1);
			    		   firstTouch = false;
		    		   }
			    	   if(camIndex >= (worldPos.length)) camIndex--;
			    	   else if(camIndex < 0) camIndex++;
	    			   if(sceneInit)cameraPose();
	    		   }
	    	   }
	    	   else if (me.getPointerCount()==2 && firstTouch) {
		           double distance = Math.sqrt(((me.getX(1)-me.getX(0))*(me.getX(1)-me.getX(0)))+((me.getY(1)-me.getY(0))*(me.getY(1)-me.getY(0))));
	    		   if (lastDistance == 0) lastDistance = distance;
		           double delta = lastDistance - distance;
		           cameraDolly(delta);
		           lastDistance = distance;
	    	   }
	    	   xpos = me.getX(0);
	           ypos = me.getY(0);
          
	       }
	       if (me.getAction() == MotionEvent.ACTION_UP) {
	    	   firstTouch = true;
	       }	
	       try {
	           Thread.sleep(15);
	       } catch (Exception e) {
	       }
	}
			
	public Bitmap textAsBitmap(String text) {// For later usage
	    Paint paint = new Paint();
	    paint.setTextSize(16);
	    paint.setColor(0x666666);
	    paint.setUnderlineText(true);
	    paint.setTextAlign(Paint.Align.CENTER);
	    int width = (int) (paint.measureText(text) + 0.5f); // round
	    float baseline = (int) (paint.ascent() + 0.5f);
	    int height = (int) (baseline + paint.descent() + 0.5f);
	    Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(image);
	    canvas.drawText(text, 0, baseline, paint);
	    return image;
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