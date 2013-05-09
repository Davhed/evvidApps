/*
 * 
 * 		Island Nights relies heavily on the Rajawali framework which can be found here:
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
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.islandnights.R;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

import rajawali.animation.Animation3D;
import rajawali.animation.Animation3DListener;
import rajawali.animation.ScaleAnimation3D;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.PhongMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.materials.TextureInfo;
import rajawali.materials.TextureManager.TextureType;
import rajawali.math.Number3D;

public class WallpaperRenderer extends RajawaliRenderer{
	
	private float 
		xpos,
		sWave,
		cWave, 
		camWave,
		worldRot;
	
	private int 
		flyTimer = 0, 
		starTimer = 0,
		flameCounter = 0, 
		smokeCounter = 0, 
		camIndex = 0, 
		worldIndex = 0,
		totalCount = 0;
	
	private double 
		waveIndex = 0, 
		camWaveIndex = 0, 
		lastDistance;
	
	private boolean 
		isVisible = false,
		shooting = false, 
		sceneInit = false, 
		firstTouch = true, 
		moveCamera = false, 
		moveCameraLook = false;
	
	private String signText = "Customize this text!";
	
	private Number3D [] cameraLook, worldPos;
	
	private DirectionalLight dLight_amb;
	
	private ObjectInputStream ois;
	
	private Timer timer = new Timer();
		
	private BaseObject3D parentObj, skydome, moon, ground, god, godlight, crater, volcano, mountains, water,trees1, trees2, trees3, bush1, bush2, bush3, bush4, bush5,
	bigplants, bigplants2, bigplants3, fgplants1, fgplants2, grass, totems, boat, interiors, castLight1, castLight2, castLight3, castLight4, castLight5, castLight6,
	dock, lilypads, mask, castLight7, castLight8, castLight9, castLight10, castLight11, lodge, lodgeRoof, hut, hutRoof, fruit, torches, palm1, palm2, palm3, palm4, palm5,
	flySwarm, flySwarm2, shootingStar, flames, torch1, torch2, torch3, torch4, torch5, torch6, torch7, torch8, torch9, torch10, torch11, torch12, shadows, depthShadow,
	smokePlume, bbq, net1, sign, signPost, cameraLookNull;
	
	private TextureInfo skydomeInfo, groundInfo, interPropInfo, godInfo, godNormInfo, waterDockInfo, waterDockAlphaInfo, shootingStarInfo, shootingStarAlphaInfo, fireflyInfo, fireflyAlphaInfo, mntVolTreesInfo, mntVolTreesAlphaInfo,
	lightingInfo, lightingAlphaInfo, hutTotemInfo, hutTotemAlphaInfo, lodgeTorchInfo, lodgeTorchAlphaInfo, plantsInfo, plantsAlphaInfo, netInfo, netAlphaInfo;
	
	private TextureInfo[] flameInfo, smokeInfo, moonInfo, waterInfo;
	
	private SimpleMaterial[] moonMat;
	
	private String[] alphaNum = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	
	private OnSharedPreferenceChangeListener mListener;
	private int performance = 30, starMode = 1, moonPhase = 2, camSpeed = 5, lastMoon = -1;
	private boolean prefChanged, showSmoke, showFlames, lightFlicker, showFlies, showPlants, showSign, showMoon;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(performance);
    }
		
	public void initScene() {
		setOnPreferenceChange();
		setPrefsToLocal();
		initTimer();
		
		lightsCam();
		loadTextures();
		loadObjects();

		addObjects();
	}
	
	private void lightsCam() {
		dLight_amb = new DirectionalLight();
		dLight_amb.setDirection(-1, -.1f, .5f);
		dLight_amb.setPower(8);
		
		cameraLookNull = new BaseObject3D();
		
		cameraLook = new Number3D[6];
		cameraLook[0] = new Number3D(-10,0,0);
		cameraLook[1] = new Number3D(-10, 0, 0);
		cameraLook[2] = new Number3D(-10, -1.5f, -6);
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
		skydomeInfo =  mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.skydome_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skydome_tex));
		groundInfo =  mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.ground_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ground_tex));
		interPropInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.boat_int_prp_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.boat_int_prp_tex));
		godInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.god_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.god_tex)); 
		godNormInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.god_nrml), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.god_tex),TextureType.BUMP); 

		mntVolTreesInfo =  mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.mnt_vlcn_bgplm_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mnt_vlcn_bgplm_tex));
		mntVolTreesAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.mnt_vlcn_bgplm_tex_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mnt_vlcn_bgplm_tex), TextureType.ALPHA);
		lightingInfo =  mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.lights_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lights_tex)); 
		lightingAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.lights_tex_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.lights_tex), TextureType.ALPHA);
		shootingStarInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.shootingstar_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shootingstar_tex)); 
		shootingStarAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.shootingstar_tex_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shootingstar_tex), TextureType.ALPHA);
		fireflyInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.green_light), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.green_light)); 
		fireflyAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.green_light_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.green_light), TextureType.ALPHA);
		waterDockInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.waterdock_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterdock_tex)); 
		waterDockAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.waterdock_tex_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterdock_tex), TextureType.ALPHA);
		netInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.net), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.net)); 
		netAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.net_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.net), TextureType.ALPHA);

		hutTotemInfo = mTextureManager.addEtc1Texture(mContext,
                new int[] { R.raw.hut_tex_mip_0,
                    R.raw.hut_tex_mip_1,
                    R.raw.hut_tex_mip_2,
                    R.raw.hut_tex_mip_3,
                    R.raw.hut_tex_mip_4,
                    R.raw.hut_tex_mip_5,
                    R.raw.hut_tex_mip_6,
                    R.raw.hut_tex_mip_7,
                    R.raw.hut_tex_mip_8,
                    R.raw.hut_tex_mip_9,
                    R.raw.hut_tex_mip_10,
                }, TextureType.DIFFUSE);	
		hutTotemAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.hut_ttm_tex_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.hut_ttm_tex), TextureType.ALPHA);

		lodgeTorchInfo = mTextureManager.addEtc1Texture(mContext,
                new int[] { R.raw.lodge_tex_mip_0,
                    R.raw.lodge_tex_mip_1,
                    R.raw.lodge_tex_mip_2,
                    R.raw.lodge_tex_mip_3,
                    R.raw.lodge_tex_mip_4,
                    R.raw.lodge_tex_mip_5,
                    R.raw.lodge_tex_mip_6,
                    R.raw.lodge_tex_mip_7,
                    R.raw.lodge_tex_mip_8,
                    R.raw.lodge_tex_mip_9,
                    R.raw.lodge_tex_mip_10,
                }, TextureType.DIFFUSE);	
		lodgeTorchAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.ldg_plm_trch_tex_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ldg_plm_trch_tex), TextureType.ALPHA);

		plantsInfo = mTextureManager.addEtc1Texture(mContext,
                new int[] { R.raw.plants_tex_mip_0,
                    R.raw.plants_tex_mip_1,
                    R.raw.plants_tex_mip_2,
                    R.raw.plants_tex_mip_3,
                    R.raw.plants_tex_mip_4,
                    R.raw.plants_tex_mip_5,
                    R.raw.plants_tex_mip_6,
                    R.raw.plants_tex_mip_7,
                    R.raw.plants_tex_mip_8,
                    R.raw.plants_tex_mip_9,
                    R.raw.plants_tex_mip_10,
                }, TextureType.DIFFUSE);	
		plantsAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.plants_tex_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plants_tex), TextureType.ALPHA);

		moonInfo = new TextureInfo[10]; //Arrays are used to store image sequences. If they have alpha they need to be double sized arrays
		for(int i = 0; i <  moonInfo.length*.5; i++){ // so I divide the length by 2 in the for loop
    		int etc = mContext.getResources().getIdentifier("moon" + (i), "raw", "com.evvid.wallpapers.islandnights");
    		int alph = mContext.getResources().getIdentifier("moon" + (i) + "_alpha", "raw", "com.evvid.wallpapers.islandnights");
    		int bmp = mContext.getResources().getIdentifier("moon" + (i), "drawable", "com.evvid.wallpapers.islandnights");
    		moonInfo[2*i] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(etc), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.DIFFUSE);
    		moonInfo[2*i+1] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(alph), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.ALPHA);
		}
		
		waterInfo = new TextureInfo[1];
		for(int i = 0; i <  1; i++){
    		int etc = mContext.getResources().getIdentifier(i < 10 ? "w0" + (i) : "w" + (i), "raw", "com.evvid.wallpapers.islandnights");
    		int bmp = mContext.getResources().getIdentifier("w" + (i), "drawable", "com.evvid.wallpapers.islandnights");
    		waterInfo[i] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(etc), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.DIFFUSE);
		}
		
		flameInfo = new TextureInfo[14];
		for(int i = 0; i <  flameInfo.length*.5; i++){
    		int etc = mContext.getResources().getIdentifier("f" + (alphaNum[i]), "raw", "com.evvid.wallpapers.islandnights");
    		int alph = mContext.getResources().getIdentifier("f" + (alphaNum[i]) + "_alpha", "raw", "com.evvid.wallpapers.islandnights");
    		int bmp = mContext.getResources().getIdentifier("f" + (alphaNum[i]), "drawable", "com.evvid.wallpapers.islandnights");
    		flameInfo[2*i] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(etc), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.DIFFUSE);
    		flameInfo[2*i+1] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(alph), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.ALPHA);
		}
		
		smokeInfo = new TextureInfo[40];
		for(int i = 0; i <  smokeInfo.length*.5; i++){
    		int etc = mContext.getResources().getIdentifier("s" + (alphaNum[i]), "raw", "com.evvid.wallpapers.islandnights");
    		int alph = mContext.getResources().getIdentifier("s" + (alphaNum[i]) + "_alpha", "raw", "com.evvid.wallpapers.islandnights");
    		int bmp = mContext.getResources().getIdentifier("s" + (alphaNum[i]), "drawable", "com.evvid.wallpapers.islandnights");
    		smokeInfo[2*i] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(etc), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.DIFFUSE);
    		smokeInfo[2*i+1] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(alph), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.ALPHA);
		}
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
			PhongMaterial plantsMat = new PhongMaterial();

			SimpleGlowMaterial bbqMat = new SimpleGlowMaterial();
			PhongGlowMaterial godMat = new PhongGlowMaterial();
			
			moonMat = new SimpleMaterial[(int) (moonInfo.length*.5)];
			for(int i = 0; i < moonMat.length; i++){
				moonMat[i] = new SimpleMaterial();
				moonMat[i].addTexture(moonInfo[2*i]);			
				moonMat[i].addTexture(moonInfo[2*i+1]);			
			}
			
			skydomeMat.addTexture(skydomeInfo);
			groundMat.addTexture(groundInfo);			
			interPropMat.addTexture(interPropInfo);
			bbqMat.addTexture(interPropInfo);
			waterDockMat.addTexture(waterDockInfo);
			waterDockMat.addTexture(waterDockAlphaInfo);
			shootingStarMat.addTexture(shootingStarInfo);			
			shootingStarMat.addTexture(shootingStarAlphaInfo);			
			mntVolTreesMat.addTexture(mntVolTreesInfo);			
			mntVolTreesMat.addTexture(mntVolTreesAlphaInfo);			
			lightingMat.addTexture(lightingInfo);			
			lightingMat.addTexture(lightingAlphaInfo);			
			shadowMat.addTexture(lightingInfo);			
			shadowMat.addTexture(lightingAlphaInfo);			
			hutTotemMat.addTexture(hutTotemInfo);			
			hutTotemMat.addTexture(hutTotemAlphaInfo);			
			lodgeTorchMat.addTexture(lodgeTorchInfo);			
			lodgeTorchMat.addTexture(lodgeTorchAlphaInfo);			
			palmMat.addTexture(lodgeTorchInfo);
			palmMat.addTexture(lodgeTorchAlphaInfo);
			netMat.addTexture(netInfo);
			netMat.addTexture(netAlphaInfo);
			
			plantsMat.addTexture(plantsInfo);
			plantsMat.addTexture(plantsAlphaInfo);
			plantsMat.setSpecularColor(.5f, .5f, 1, .5f);
			
			godMat.addTexture(godInfo);
			godMat.addTexture(godNormInfo);
			godMat.setShininess(10);
			godMat.setSpecularColor(.05f, .05f, .1f, 1);			
			
			//SCENERY//
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.skydome));
			skydome = new BaseObject3D((SerializedObject3D)ois.readObject());
			skydome.setPosition(150, 50, 0);
			skydome.setMaterial(skydomeMat);
			
			moon = new Plane(400, 400, 1, 1);
			moon.setPosition(-2500, 700, 500);
			moon.setMaterial(new SimpleMaterial());
			moon.addTexture(moonInfo[2 * moonPhase]);
			moon.addTexture(moonInfo[2 * moonPhase + 1]);
			moon.setBlendingEnabled(true);
			moon.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			moon.setLookAt(mCamera.getX()-moon.getX(), mCamera.getY()-moon.getY(), mCamera.getZ()-moon.getZ());
			
			shootingStar = new Plane(10, 10, 1, 1);
			shootingStar.setRotation(-45, -90, 0);
			shootingStar.setPosition(-550, 300, -300);
			shootingStar.setMaterial(shootingStarMat);
			shootingStar.setBlendingEnabled(true);
			shootingStar.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.crater));
			crater = new BaseObject3D((SerializedObject3D)ois.readObject());
			crater.setMaterial(mntVolTreesMat);
			crater.setBlendingEnabled(true);
			crater.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			crater.setScale(1, 1, 1.35f);
			crater.setPosition(-20, 0, -5);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.volcano));
			volcano = new BaseObject3D((SerializedObject3D)ois.readObject());
			volcano.setMaterial(mntVolTreesMat);
			volcano.setBlendingEnabled(true);
			volcano.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.mountains));
			mountains = new BaseObject3D((SerializedObject3D)ois.readObject());
			mountains.setMaterial(mntVolTreesMat);
			mountains.setBlendingEnabled(true);
			mountains.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.ground));
			ground = new BaseObject3D((SerializedObject3D)ois.readObject());
			ground.setMaterial(groundMat);
			
			//FOLIAGE//
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.trees1));
			trees1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			trees1.setMaterial(mntVolTreesMat);
			trees1.setBlendingEnabled(true);
			trees1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.trees2));
			trees2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			trees2.setMaterial(mntVolTreesMat);
			trees2.setBlendingEnabled(true);
			trees2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.trees3));
			trees3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			trees3.setMaterial(mntVolTreesMat);
			trees3.setBlendingEnabled(true);
			trees3.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush1));
			bush1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush1.setMaterial(plantsMat);
			bush1.setBlendingEnabled(true);
			bush1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush1.setDoubleSided(true);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush2));
			bush2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush2.setMaterial(plantsMat);
			bush2.setBlendingEnabled(true);
			bush2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush2.setDoubleSided(true);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush3));
			bush3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush3.setMaterial(plantsMat);
			bush3.setBlendingEnabled(true);
			bush3.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush3.setDoubleSided(true);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush4));
			bush4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush4.setMaterial(plantsMat);
			bush4.setBlendingEnabled(true);
			bush4.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush4.setDoubleSided(true);
			bush4.addLight(dLight_amb);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bush5));
			bush5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bush5.setMaterial(plantsMat);
			bush5.setBlendingEnabled(true);
			bush5.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bush5.setDoubleSided(true);
			
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
			bigplants.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bigplants.setDoubleSided(true);
			bigplants.addLight(dLight_amb);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bigplants2));
			bigplants2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bigplants2.setMaterial(plantsMat);
			bigplants2.setBlendingEnabled(true);
			bigplants2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bigplants2.setDoubleSided(true);
			bigplants2.addLight(dLight_amb);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.bigplants3));
			bigplants3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			bigplants3.setMaterial(plantsMat);
			bigplants3.setBlendingEnabled(true);
			bigplants3.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			bigplants3.setDoubleSided(true);
			bigplants3.addLight(dLight_amb);
			bigplants3.setPosition(5, 1, 0);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass));
			grass = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass.setMaterial(plantsMat);
			grass.setBlendingEnabled(true);
			grass.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			grass.setDoubleSided(true);
			grass.addLight(dLight_amb);
					
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgplants1));
			fgplants1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgplants1.setMaterial(plantsMat);
			fgplants1.setBlendingEnabled(true);
			fgplants1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			fgplants1.setDoubleSided(true);
			fgplants1.addLight(dLight_amb);
			
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgplants2));
			fgplants2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgplants2.setMaterial(plantsMat);
			fgplants2.setBlendingEnabled(true);
			fgplants2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
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
			lodgeRoof.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			lodgeRoof.setMaterial(lodgeTorchMat);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.hut));
			hut = new BaseObject3D((SerializedObject3D)ois.readObject());
			hut.setBlendingEnabled(true);
			hut.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			hut.setMaterial(hutTotemMat);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.hutroof));
			hutRoof = new BaseObject3D((SerializedObject3D)ois.readObject());
			hutRoof.setY(.25f);
			hutRoof.setBlendingEnabled(true);
			hutRoof.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
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
			bbq.setMaterial(bbqMat);
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
			net1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
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
			godlight.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows));
			shadows = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows.setMaterial(shadowMat);
			shadows.setY(-.1f);
			shadows.setBlendingEnabled(true);
			shadows.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.dshadow));
	    	depthShadow = new BaseObject3D((SerializedObject3D)ois.readObject());
			depthShadow.setMaterial(shadowMat);
			depthShadow.setBlendingEnabled(true);
			depthShadow.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl1));
			castLight1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight1.setMaterial(lightingMat);
			castLight1.setBlendingEnabled(true);
			castLight1.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			castLight1.setY(.5f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl2));
			castLight2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight2.setMaterial(lightingMat);
			castLight2.setBlendingEnabled(true);
			castLight2.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			castLight2.setY(.25f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl3));
			castLight3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight3.setMaterial(lightingMat);
			castLight3.setBlendingEnabled(true);
			castLight3.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl4));
			castLight4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight4.setMaterial(lightingMat);
			castLight4.setBlendingEnabled(true);
			castLight4.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			castLight4.setY(.75f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl5));
			castLight5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight5.setMaterial(lightingMat);
			castLight5.setBlendingEnabled(true);
			castLight5.setY(.5f);
			castLight5.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl6));
			castLight6 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight6.setMaterial(lightingMat);
			castLight6.setBlendingEnabled(true);
			castLight6.setY(1.2f);
			castLight6.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
				
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl7));
			castLight7 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight7.setY(.25f);
			castLight7.setMaterial(lightingMat);
			castLight7.setBlendingEnabled(true);
			castLight7.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl8));
			castLight8 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight8.setMaterial(lightingMat);
			castLight8.setPosition(5, -.2f, 1);
			castLight8.setBlendingEnabled(true);
			castLight8.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl9));
			castLight9 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight9.setMaterial(lightingMat);
			castLight9.setBlendingEnabled(true);
			castLight9.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tl10));
			castLight10 = new BaseObject3D((SerializedObject3D)ois.readObject());
			castLight10.setMaterial(lightingMat);
			castLight10.setBlendingEnabled(true);
			castLight10.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			castLight11 = castLight8.clone();
			castLight11.setPosition(20f, -5f, -10f);
			castLight11.setMaterial(lightingMat);
			castLight11.setBlendingEnabled(true);
			castLight11.setBlendFunc(GLES20.GL_SRC_ALPHA,  GLES20.GL_ONE_MINUS_SRC_ALPHA);

			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.signpost));
			signPost = new BaseObject3D((SerializedObject3D)ois.readObject());
			signPost.setMaterial(waterDockMat);
			signPost.setPosition(0.5f, 0, 1.5f);
						
			ois.close();
			
		} catch (Exception e){
			e.printStackTrace();
		}
		
		createFlySwarms();
		createTorchFlames();
		createSmokePlume();
		createSign();
		createWater();
	}

	private void createSign(){
		try{
			signPost.removeChild(castLight11);
			signPost.removeChild(sign);
			signPost.removeChild(torch11);
			signPost.removeChild(torch12);
		}catch(Exception e) {e.printStackTrace();}
		Bitmap signTex = textAsBitmap(signText);
		SimpleMaterial signMat = new SimpleMaterial();
		signMat.addTexture(mTextureManager.addTexture(signTex));
		sign = new Plane(5.4f, 5.4f, 1, 1, 1, true);
		sign.setDoubleSided(true);
		sign.setBlendingEnabled(true);
		sign.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);
		sign.setMaterial(signMat);
		sign.setRotation(5, 248, 0);
		sign.setPosition(-51.5f, 11.3f, -41.5f);
		signPost.addChild(castLight11);
		signPost.addChild(sign);
		signPost.addChild(torch11);
		signPost.addChild(torch12);
	}
	
	private void createWater(){
		water = new BaseObject3D();
		
		for(int i = 0; i < 1; i++){
			BaseObject3D waterTile = new Plane(80, 200, 1, 40, 40);
			waterTile.setMaterial(new WaterMaterial());
			waterTile.addTexture(waterInfo[i]);
			waterTile.setTransparent(true);
			waterTile.setVisible(false);
			waterTile.setPosition(-17.5f, 2.25f, -15f);
			waterTile.setRotation(-90, 0, 0);
			water.addChild(waterTile);
		}
	}
	
	private void createFlySwarms(){
		flySwarm = new BaseObject3D();
		flySwarm2 = new BaseObject3D();
		int numChildren = 16;

		SimpleMaterial fireflyMat = new SimpleMaterial();
		fireflyMat.addTexture(fireflyInfo);
		fireflyMat.addTexture(fireflyAlphaInfo);
		
		for(int i = 0; i < numChildren*2; ++i){
			BaseObject3D fly = new Sphere(.3f, 5, 5);
			fly.setMaterial(fireflyMat);
			fly.setBlendingEnabled(true);
			fly.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			fly.setScale(0);
			if(i < numChildren)
				flySwarm.addChild(fly);
			else
				flySwarm2.addChild(fly);
		}
		
		flySwarm.getChildAt(0).setPosition(-20,  12,  0);
		flySwarm.getChildAt(1).setPosition(-35, 13, 10);
		flySwarm.getChildAt(2).setPosition(-20, 9, -8);
		flySwarm.getChildAt(3).setPosition(-40, 10, 5);
		flySwarm.getChildAt(4).setPosition(-60, 11, 15);
		flySwarm.getChildAt(5).setPosition(-30, 8, -16);
		
		flySwarm2.getChildAt(0).setPosition(-20,  12,  0);
		flySwarm2.getChildAt(1).setPosition(-35, 13, 10);
		flySwarm2.getChildAt(2).setPosition(-20, 9, -8);
		flySwarm2.getChildAt(3).setPosition(-40, 10, 5);
		flySwarm2.getChildAt(4).setPosition(-60, 11, 15);
		flySwarm2.getChildAt(5).setPosition(-30, 8, -16);
				
		flySwarm.setPosition(-30, 20, 20);
		flySwarm2.setPosition(5, 25, 40);
	}
	
	private void createTorchFlames() {
		flames = new BaseObject3D();
		
		for(int i = 0; i < flameInfo.length*.5; i++){
			BaseObject3D flame = new Plane(2, 2, 1, 1);
			flame.setMaterial(new SimpleMaterial());
			flame.addTexture(flameInfo[2 * i]);
			flame.addTexture(flameInfo[2 * i+1]);
			flame.setBlendingEnabled(true);
			flame.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			flame.setVisible(false);
			flames.addChild(flame);
		}
				
		torch1 = new BaseObject3D();
		torch2 = new BaseObject3D();
		torch3 = new BaseObject3D();
		torch4 = new BaseObject3D();
		torch5 = new BaseObject3D();
		torch6 = new BaseObject3D();
		torch7 = new BaseObject3D();
		torch8 = new BaseObject3D();
		torch9 = new BaseObject3D();
		torch10 = new BaseObject3D();
		torch11 = new BaseObject3D();
		torch12 = new BaseObject3D();
	
		torch1.addChild(flames);
		torch2.addChild(flames);
		torch3.addChild(flames);
		torch4.addChild(flames);
		torch5.addChild(flames);
		torch6.addChild(flames);
		torch7.addChild(flames);
		torch8.addChild(flames);
		torch9.addChild(flames);
		torch10.addChild(flames);
		torch11.addChild(flames);
		torch12.addChild(flames);
				
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
		torch11.setPosition(-45.6801f, 9.80159f+1, -45.4376f);
		torch12.setPosition(-47.0211f, 9.24686f+1, -43.1889f);
		
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
		torch11.setLookAt(mCamera.getX()-torch10.getX(), mCamera.getY()-torch10.getY(), mCamera.getZ()-torch10.getZ());
		torch12.setLookAt(mCamera.getX()-torch10.getX(), mCamera.getY()-torch10.getY(), mCamera.getZ()-torch10.getZ());
	}

	private void createSmokePlume(){
		smokePlume = new BaseObject3D();
		
		for(int i = 0; i < smokeInfo.length*.5; i++){
			BaseObject3D smokelet = new Plane(190, 190, 1, 1);
			smokelet.setMaterial(new SimpleMaterial());
			smokelet.addTexture(smokeInfo[2 * i]);
			smokelet.addTexture(smokeInfo[2 * i + 1]);
			smokelet.setBlendingEnabled(true);
			smokelet.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			smokePlume.addChild(smokelet);			
		}
						
		smokePlume.setPosition(-520, 170, -3);
		smokePlume.setScaleZ(1.02f);
		smokePlume.setLookAt(mCamera.getX()-smokePlume.getX(), mCamera.getY()-smokePlume.getY(), mCamera.getZ()-smokePlume.getZ());	
	}
	
	private void addObjects(){
		parentObj = new BaseObject3D();
		parentObj.addChild(skydome);
		parentObj.addChild(ground);
		parentObj.addChild(dock);
		parentObj.addChild(boat);
		parentObj.addChild(god);
		parentObj.addChild(totems);
		parentObj.addChild(lodge);
		parentObj.addChild(interiors);
		parentObj.addChild(hut);
		parentObj.addChild(fruit);
		parentObj.addChild(mask);
		parentObj.addChild(bbq);

		parentObj.addChild(moon);
		parentObj.addChild(shootingStar);
		parentObj.addChild(crater);		
		parentObj.addChild(smokePlume);		
		parentObj.addChild(volcano);
		parentObj.addChild(mountains);
		parentObj.addChild(water);
		parentObj.addChild(depthShadow);
		parentObj.addChild(shadows);
		parentObj.addChild(trees1);
		parentObj.addChild(trees2);
		parentObj.addChild(trees3);
		parentObj.addChild(palm1);
		parentObj.addChild(bush1);
		parentObj.addChild(bush2);
		parentObj.addChild(bush3);
		parentObj.addChild(grass);
		parentObj.addChild(castLight1);
		parentObj.addChild(castLight2);
		parentObj.addChild(godlight);
		parentObj.addChild(castLight3);
		parentObj.addChild(castLight5);
		parentObj.addChild(palm2);
		parentObj.addChild(palm3);
		parentObj.addChild(castLight4);
		parentObj.addChild(castLight6);
		parentObj.addChild(lodgeRoof);
		parentObj.addChild(bush4);
		parentObj.addChild(bigplants);
		parentObj.addChild(torches);		
		parentObj.addChild(torch9);		
		parentObj.addChild(torch10);
		parentObj.addChild(bigplants2);
		parentObj.addChild(palm5);	
		parentObj.addChild(torch7);		
		parentObj.addChild(castLight8);
		parentObj.addChild(palm4);
		parentObj.addChild(bush5);
		parentObj.addChild(hutRoof);
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
		parentObj.addChild(signPost);
		parentObj.addChild(bigplants3);
		parentObj.addChild(fgplants1);
		parentObj.addChild(lilypads);
		parentObj.addChild(fgplants2);
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
				else if ("sign_pref".equals(key))
				{
					showSign = sharedPreferences.getBoolean(key, true);
				}
				else if ("signText_pref".equals(key))
				{
					signText = sharedPreferences.getString(key, "Customize this text!");
					createSign();
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
				else if ("moon_pref".equals(key))
				{
					showMoon = sharedPreferences.getBoolean(key, true);
				}
				else if ("moon_phase".equals(key))
				{
					lastMoon = moonPhase;
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
		showSign = preferences.getBoolean("sign_pref", true);
		signText = preferences.getString("signText_pref", "Customize this text!");
		showSmoke = preferences.getBoolean("smokePlume_pref", true);
		showFlames = preferences.getBoolean("torches_pref", true);
		lightFlicker = preferences.getBoolean("flicker_pref", true);
		showFlies = preferences.getBoolean("fireflies_pref", true);
		showPlants = preferences.getBoolean("plants_pref", true);
		showMoon = preferences.getBoolean("moon_pref", true);
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
//        PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);
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
			timer.cancel(); // cancel the timer animation
		}catch (Exception e){
			e.printStackTrace();
		}		

		preferences.unregisterOnSharedPreferenceChangeListener(mListener);
		sceneInit = false;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		
	}
	
	@Override
    public void onVisibilityChanged(boolean visible) {//Here we start/stop our timer on visibility change. 
        super.onVisibilityChanged(visible);
    	isVisible = visible;
    	if(isVisible)
    		initTimer();
    }
	
	private void initTimer(){
		TimerTask tTask = new TimerTask() {
			public void run() {
				onTimerTick();//This is called on each update, so simulates `onDrawFrame` while being clock based
				if (isVisible == false)
					this.cancel();
			}
		};
		timer.schedule(tTask, 10, 10); //33ms delay after the scene was created, 33ms delay there after
	}

	private void onTimerTick() {//Custom animation fired from here
		if(sceneInit){
			if(totalCount%3 == 0){
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
			totalCount++;
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
			torch11.setVisible(true);
			torch12.setVisible(true);
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
			castLight11.setVisible(true);
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
			torch11.setVisible(false);
			torch12.setVisible(false);
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
			castLight11.setVisible(false);
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
		
		if(showMoon){
			moon.setVisible(true);
		}else{
			moon.setVisible(false);
		}
		
		if(showSign){
			signPost.setVisible(true);
		}else{
			signPost.setVisible(false);
		}
	}
	
	private void checkEngine(){
		if(mWallpaperEngine.isVisible() && prefChanged){
			prefChanged = false;
			if(lastMoon != -1){
				moon.removeTexture(moonInfo[2 * lastMoon]);
				moon.removeTexture(moonInfo[2 * lastMoon + 1]);
			}
			moon.getTextureInfoList().clear();
			moon.addTexture(moonInfo[2 * moonPhase]);
			moon.addTexture(moonInfo[2 * moonPhase + 1]);
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
		water.getChildAt(0).setVisible(true);
		
		water.setPosition(water.getX()+(cWave/400), (water.getY()+(cWave/400)), water.getZ()+(sWave/400));
		depthShadow.setY(depthShadow.getY()+(cWave/400));
		if(showPlants)lilypads.setPosition(lilypads.getX()+(cWave/80), lilypads.getY()+(cWave/400), lilypads.getZ()+(sWave/100));
		
		for(int i = 0; i < water.getNumChildren(); i++){
			WaterMaterial tempMat =  (WaterMaterial) water.getChildAt(i).getMaterial();
			tempMat.setTime(totalCount*.01f);
		}
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
					}
					
				});
				starAnim.start();
			}
		}
		starTimer++;
	}
	
	private void flyMovement(){
		if (flyTimer == 60) {
			if(Math.random() > .75) 
				blink(flySwarm2.getChildAt(3));
			else if(Math.random() > .5)
				blink(flySwarm.getChildAt(3));
			else if(Math.random() > .66) 
				blink(flySwarm2.getChildAt(0));
			else if(Math.random() > .33) 
				blink(flySwarm.getChildAt(0));
		}
		else if (flyTimer == 90) {
			if(Math.random() > .75) 
				blink(flySwarm2.getChildAt(4));
			else if(Math.random() > .5)
				blink(flySwarm.getChildAt(4));
			else if(Math.random() > .66) 
				blink(flySwarm2.getChildAt(1));
			else if(Math.random() > .33)
				blink(flySwarm.getChildAt(1));
		}
		else if (flyTimer == 120) {
			if(Math.random() > .66) 
				blink(flySwarm2.getChildAt(2));
			else if(Math.random() > .33) 
				blink(flySwarm.getChildAt(2));		
		}
		else if (flyTimer == 140) {
			if(Math.random() > .66) 
				blink(flySwarm2.getChildAt(5));
			else if(Math.random() > .33)
				blink(flySwarm.getChildAt(5));
		
			flyTimer = 0;
		}
		flyTimer++;
		
		for(int i = 0; i < flySwarm.getNumChildren(); i++){
			flySwarm.getChildAt(i) .setPosition(
					(float) (flySwarm.getChildAt(i).getX()-(((Math.random()*5)*sWave*.05f)-(sWave*.01f))),
					(float) (flySwarm.getChildAt(i).getY()-(((Math.random()*5)*sWave*.01f)-(cWave*.005f))), 
					(float) (flySwarm.getChildAt(i).getZ()-(((Math.random()*5)*sWave*.05f)-(sWave*.01f))));
			flySwarm2.getChildAt(i) .setPosition(
					(float) (flySwarm2.getChildAt(i).getX()-(((Math.random()*5)*sWave*.05f)-(sWave*.05f))),
					(float) (flySwarm2.getChildAt(i).getY()-(((Math.random()*5)*sWave*.01f)-(cWave*.005f))), 
					(float) (flySwarm2.getChildAt(i).getZ()-(((Math.random()*5)*sWave*.05f)-(sWave*.01f))));
		}
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
		castLight11.setScale(flickerSeed(), 1, flickerSeed());
	}
	
	private float flickerSeed(){
		float seed = (float) Math.random();
		while (seed < .99f){
			 seed = (float) Math.random();
		}
		return seed;		
	}
	
	private void torchFlameMovement(){
		if(flameCounter % 2 == 0){
			flames.getChildAt(flameCounter/2).setVisible(true);
			
			if(flameCounter == 0)
				flames.getChildAt(6).setVisible(false);
			else
				flames.getChildAt(flameCounter/2-1).setVisible(false);
		}

		if(flameCounter++ >= 11)
			flameCounter = 0;

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
		torch11.setLookAt(mCamera.getX()-torch11.getX(), mCamera.getY()-torch11.getY(), mCamera.getZ()-torch11.getZ());
		torch12.setLookAt(mCamera.getX()-torch12.getX(), mCamera.getY()-torch12.getY(), mCamera.getZ()-torch12.getZ());
	}
	
	private void smokePlumeMovement(){
		if(smokeCounter % 2 == 0){
			smokePlume.getChildAt(smokeCounter/2).setVisible(true);
			
			if(smokeCounter == 0)
				smokePlume.getChildAt(19).setVisible(false);
			else
				smokePlume.getChildAt(smokeCounter/2-1).setVisible(false);
		}

		if(smokeCounter++ >= 37)
			smokeCounter = 0;

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
				
			}
			
		});
		blinkAnim.start();
	}
	
	@Override //This method moves the camera using direct touch events. Tracking a flick and turning it into a procedural animation for smoothing
	public void onTouchEvent(MotionEvent me) {
	       if (me.getAction() == MotionEvent.ACTION_DOWN) {
	           xpos = me.getX();
	    	   lastDistance = 0;
	       }
	       if (me.getAction() == MotionEvent.ACTION_MOVE) {
	           float xd = xpos - me.getX(0);

	    	   if(me.getPointerCount()==1 && firstTouch) {
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
	    	   else if (me.getPointerCount()==2 && firstTouch) {
		           double distance = Math.sqrt(((me.getX(1)-me.getX(0))*(me.getX(1)-me.getX(0)))+((me.getY(1)-me.getY(0))*(me.getY(1)-me.getY(0))));
	    		   if (lastDistance == 0) lastDistance = distance;
		           lastDistance = distance;
	    	   }
	    	   xpos = me.getX(0);          
	       }
	       if (me.getAction() == MotionEvent.ACTION_UP) {
	    	   firstTouch = true;
	       }	
	       try {
	           Thread.sleep(15);
	       } catch (Exception e) {
	       }
	}
			
	public Bitmap textAsBitmap(String text) {
		TextPaint paint = new TextPaint();
	    paint.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "mondobeyondobb_reg.ttf"));
	    paint.setTextSize(18);
	    paint.setAntiAlias(true);
	    paint.setColor(0xff986a3e);
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setShadowLayer(2, 1, 1, 0xff000000);
	    paint.setTextAlign(Paint.Align.CENTER);
	    Bitmap image = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
	    float x = image.getWidth();
	    float y = image.getHeight();
	    int tl = 25;
	    if(text.length() < tl)
	    	tl = text.length();
	    CharSequence cs = text.subSequence(0, tl);
	    StaticLayout layout = new StaticLayout(cs, paint, (int) x, Layout.Alignment.ALIGN_NORMAL, 1.3f, 0, false);
	    float yOffset = .225f+(.225f / layout.getLineCount());
	    if(layout.getLineCount() >= 3) yOffset = .225f;
	    Canvas canvas = new Canvas(image);
	    canvas.translate(x*.5f, y*yOffset); //position the text
	    layout.draw(canvas);
	    return image;
	}
}