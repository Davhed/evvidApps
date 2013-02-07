/*
 * 
 * 		Shamrock Lane relies heavily on the Rajawali framework which can be found here:
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

package com.evvid.wallpapers.shamrocklane;

import java.io.ObjectInputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.shamrocklane.R;

import rajawali.BaseObject3D;
import rajawali.SerializedObject3D;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import rajawali.animation.Animation3D;
import rajawali.animation.Animation3DListener;
import rajawali.animation.CatmullRomPath3D;
import rajawali.animation.RotationAnimation3D;
import rajawali.animation.ScaleAnimation3D;
import rajawali.animation.TranslateAnimation3D;
import rajawali.lights.PointLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.PhongMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.materials.TextureInfo;
import rajawali.materials.TextureManager.TextureType;
import rajawali.math.Number3D;
import rajawali.primitives.Plane;
import rajawali.primitives.Sphere;

public class WallpaperRenderer extends RajawaliRenderer{
			
	private OnSharedPreferenceChangeListener mListener;

	private float xpos;
	private float[] waterUVs;
	
	private int 
			waterCounter = 0,
			flapCounter = 0, 
			waterIndex = 0,
			flameCounter = 0,
			flameIndex = 0,			
			camSpeed = 10, 
			camIndex = 0, 
			totalCount = 0, 
			scene = 0,
			performance = 30;
	
	private Boolean 
			sceneInit = false,
			redrawScene = false,
			moveCamera = false, 
			moveCameraLook = false, 
			firstTouch = true, 
			birdDone = true,
			timerRun = true;

	private PointLight pLight_ground, pLight_ground2, pLight_pot, pLight_pot2, pLight_branches, pLight_candle1, pLight_candle2, pLight_candle3;
	
	//Exterior
	
	private BaseObject3D exterior, camLookNull, skydome, clouddome, sun, castle, castletowers, ground, path, walls, gate, arch, stump, largestump, stumpdecal, lilys, water, branches,
	waterfallrock, waterfall, pot, gold, rbow1, rbow2, tree, door, rocks, shrooms, shamrock, shamrocks, treeferns, pondferns, fgtrees, fgferns, vines1, vines2, vines3,
	grass1, grass2, grass3, grass4, grass5, flowers1, flowers2, flowers3, flowers4, flowers5, flowers6, dirt, shadows1, shadows2, bird, fairies;
	private BaseObject3D[] waterTiles, waterfallTiles, splashTiles, splash2Tiles, splash3Tiles, branchTiles;

	private TextureInfo skyInfo, sunInfo, sunAlphaInfo, clouddomeInfo, clouddomeAlphaInfo, doorGoldArchInfo, waterfallrockTextureInfo, wallStumpRockInfo, pathDirtCloverPlantsInfo, pathDirtCloverPlantsAlphaInfo,
	potGateFernRainbowTextureInfo, potGateFernRainbowTextureAlphaInfo, castleBranchDirtInfo, castleBranchDirtAlphaInfo, treeInfo, treeBumpInfo, grassInfo,
	waterInfo, waterAlphaInfo, fairyInfo;

	private TextureInfo[] waterfallTex, birdTex, intCandleTex;
		
	private RotationAnimation3D timer;
	
	private Number3D [] cameraLook, cameraPos;

	private ObjectInputStream ois;

	private int fairyTimer = 0;

	//Interior
	
	private BaseObject3D interior, int_tree, int_floor, int_door, int_stairs, int_door_window, int_candles, int_coatrack, int_shoes, int_hat, int_buckles, int_window1,
	int_dirt, int_board, int_arches, int_clovers1, int_clovers2, int_clovers3, int_clovers4, int_clovers5, int_clovers6, int_clovers7, int_chair, int_large_table,
	int_tablerunner, int_mug, int_pipe, int_gold, int_window2, int_window2_sill, int_vase1, int_clover_table, int_vase2, int_stone_pad, int_stove, int_logs, timerNull,
	int_chimneyring, int_shelf, int_books_rocker, int_rug, int_small_table, int_open_book, int_lamp1, int_lamp2, int_tapestry, int_tapestry_rod, int_candleFlame1, int_candleFlame2, int_candleFlame3;
	
	private TextureInfo intAlphaInfo, intAlphaAlphaInfo, intWallsinfo, intFloorInfo, intDoorStoveInfo, intArchShelfOBookStepWsCandleBannerPoleInfo, intHatStandBoardShoebuckleInfo,
	intLogTablesRunnerMugPipeCoinInfo, intChairsBookcaseBooksInfo;

	private CatmullRomPath3D birdPath1;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(performance);
		setFogEnabled(true);
    }
		
	public void initScene() {
		setOnPreferenceChange();
		setPrefsToLocal();
		
		loadTextures();
		loadExterior();
		loadInterior();
		addObjects();
		initTimer();
		setUpCamera();
		setScene();
	}
	
	///////////////
	//Scene Config
	///////////////
	private void setOnPreferenceChange(){
		mListener = new OnSharedPreferenceChangeListener(){
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if ("camSpeed_pref".equals(key))
				{
					camSpeed = Integer.parseInt(sharedPreferences.getString(key, "5"));
				} 
				else if ("performance_pref".equals(key))
				{
					performance = Integer.parseInt(sharedPreferences.getString(key, "30"));
					setFrameRate(performance);
				} 
				else if ("scene_pref".equals(key))
				{
					int i = Integer.parseInt(sharedPreferences.getString(key, "0"));
					if(scene != i){
						scene = i;
						redrawScene = true;
						camIndex = 0;
					}
				} 
				else if ("waterfall_pref".equals(key))
				{
//					showFalls = sharedPreferences.getBoolean(key, true);
				}
				else if ("fairies_pref".equals(key))
				{
//					showFairies = sharedPreferences.getBoolean(key, true);
				}
				else if ("birds_pref".equals(key))
				{
//					showBirds = sharedPreferences.getBoolean(key, true);
				}
				preferences = sharedPreferences;
			}
		};		
	}
	
	private void setPrefsToLocal(){
		camSpeed = Integer.parseInt(preferences.getString("camSpeed_pref", "5"));
		performance = Integer.parseInt(preferences.getString("performance_pref", "30"));
		scene = Integer.parseInt(preferences.getString("scene_pref", "0"));
	}

	private void setScene(){
		if(scene == 0){
			interior.setVisible(false);
			setUpCamera();
			exterior.setVisible(true);
		}
		else if (scene == 1){
			exterior.setVisible(false);
			setUpCamera();
			interior.setVisible(true);
		}
		sceneInit = true;
	}
	
	private void checkScene(){
		if(redrawScene){
			sceneInit = false;
			setScene();
		}
	}	
	
	private void loadTextures(){
		
		//EXTERIOR
		skyInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.skydome_tex), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skydome));
		sunInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.sun), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sun));
		sunAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.sun_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sun), TextureType.ALPHA);
		clouddomeInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.clouddome), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clouddome));
		clouddomeAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.clouddome_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clouddome), TextureType.ALPHA);
		doorGoldArchInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.door_gold_arch), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.door_gold_arch), TextureType.DIFFUSE));
		waterfallrockTextureInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.waterfallrock_mushroom), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterfallrock_mushroom), TextureType.DIFFUSE));
		wallStumpRockInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.wall_stump_rock), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wall_stump_rock));
		castleBranchDirtInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.castle_branch_dirt), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.castle_branch_dirt), TextureType.DIFFUSE));
		castleBranchDirtAlphaInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.castle_branch_dirt_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.path_dirt_clover_plants), TextureType.ALPHA));
		pathDirtCloverPlantsInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.path_dirt_clover_plants), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.path_dirt_clover_plants), TextureType.DIFFUSE));
		pathDirtCloverPlantsAlphaInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.path_dirt_clover_plants_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.path_dirt_clover_plants), TextureType.ALPHA));
		potGateFernRainbowTextureInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.pot_gate_fern_rainbow), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pot_gate_fern_rainbow), TextureType.DIFFUSE));
		potGateFernRainbowTextureAlphaInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.pot_gate_fern_rainbow_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pot_gate_fern_rainbow), TextureType.ALPHA));
		treeInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.btree), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btree));
		grassInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.grass), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.grass));
		treeBumpInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.btree_norm), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btree_norm), TextureType.BUMP);
		waterInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.wateratlas), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wateratlas), TextureType.DIFFUSE));
		waterAlphaInfo = mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.wateratlas_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wateratlas), TextureType.ALPHA));
		fairyInfo = mTextureManager.addTexture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fairy_tex));
		
		birdTex = new TextureInfo[8];
		for(int i = 0; i <  birdTex.length/2; i++){
    		int etc = mContext.getResources().getIdentifier("bird_0" + (i), "raw", "com.evvid.wallpapers.shamrocklane");
    		int alph = mContext.getResources().getIdentifier("bird_0" + (i) + "_alpha", "raw", "com.evvid.wallpapers.shamrocklane");
    		int bmp = mContext.getResources().getIdentifier("bird_0" + (i), "drawable", "com.evvid.wallpapers.shamrocklane");
    		birdTex[2*i] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(etc), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.DIFFUSE);
    		birdTex[2*i+1] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(alph), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.ALPHA);
		}
	    waterfallTex = new TextureInfo[32];
		for(int i = 0; i <  waterfallTex.length/2; i++){
    		int etc = mContext.getResources().getIdentifier(i < 10 ? "wf0" + (i) : "wf" + (i), "raw", "com.evvid.wallpapers.shamrocklane");
    		int alph = mContext.getResources().getIdentifier(i < 10 ? "wf0" + (i) + "_alpha" : "wf" + (i) + "_alpha", "raw", "com.evvid.wallpapers.shamrocklane");
    		int bmp = mContext.getResources().getIdentifier(i < 10 ? "wf0" + (i) : "wf" + (i), "drawable", "com.evvid.wallpapers.shamrocklane");
    		waterfallTex[2*i] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(etc), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.DIFFUSE);
    		waterfallTex[2*i+1] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(alph), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.ALPHA);
		}
		
		//INTERIOR
		intAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_alpha));
		intAlphaAlphaInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_alpha_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_alpha), TextureType.ALPHA);
		intWallsinfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_walls), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_walls));
		intFloorInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_floor_diff), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_floor));
		intDoorStoveInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_door_stove), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_door_stove));
		intArchShelfOBookStepWsCandleBannerPoleInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_arch_shelf_obook_step_ws_candle_banner_pole), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_arch_shelf_obook_step_ws_candle_banner_pole));
		intHatStandBoardShoebuckleInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_hat_stand_board_shoebuckle), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_hat_stand_board_shoebuckle));
		intLogTablesRunnerMugPipeCoinInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_log_tables_runner_mug_pipe_coin), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_log_tables_runner_mug_pipe_coin));
		intChairsBookcaseBooksInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.int_chairs_bookcase_books), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.int_chairs_bookcase_books));

		intCandleTex = new TextureInfo[32];
		for(int i = 0; i < intCandleTex.length/2; i++){
    		int etc = mContext.getResources().getIdentifier(i < 10 ? "cf0" + (i) : "cf" + (i), "raw", "com.evvid.wallpapers.shamrocklane");
    		int alph = mContext.getResources().getIdentifier(i < 10 ? "cf0" + (i) + "_alpha" : "cf" + (i) + "_alpha", "raw", "com.evvid.wallpapers.shamrocklane");
    		int bmp = mContext.getResources().getIdentifier(i < 10 ? "cf0" + (i) : "cf" + (i), "drawable", "com.evvid.wallpapers.shamrocklane");
    		intCandleTex[2*i] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(etc), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.DIFFUSE);
    		intCandleTex[2*i+1] = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(alph), BitmapFactory.decodeResource(mContext.getResources(), bmp), TextureType.ALPHA);
		}
	}

	private void loadExterior(){
		pLight_ground = new PointLight();
		pLight_ground.setPosition(6.5f, 7, 15);
		pLight_ground.setPower(2f);
		pLight_ground.setColor(0xffff99);
		pLight_ground.setAttenuation(50, 1, 0, 0);
			
		pLight_ground2 = new PointLight();
		pLight_ground2.setPosition(20, 40, -55);
		pLight_ground2.setPower(1.25f);
		pLight_ground2.setAttenuation(50, 1, 0, 0);
		
		pLight_branches = new PointLight();
		pLight_branches.setPosition(-14.86f, 5, 9.7f);
		pLight_branches.setPower(3);
		
		pLight_pot = new PointLight();
		pLight_pot.setPosition(-10f, 15, 19f);
		pLight_pot.setPower(20f);
		
		pLight_pot2 = new PointLight();
		pLight_pot2.setPosition(-5.8f, 0f, 19.48f);
		pLight_pot2.setColor(0xf4ce52);
		pLight_pot2.setPower(2f);
		
		///////////////
		//Create Materials
		///////////////
		
		SimpleMaterial skyMat = new SimpleMaterial();
			skyMat.addTexture(skyInfo);

		SimpleMaterial sunMat = new SimpleMaterial();
			sunMat.addTexture(sunInfo);
			sunMat.addTexture(sunAlphaInfo);

		SimpleMaterial clouddomeMat = new SimpleMaterial();
			clouddomeMat.addTexture(clouddomeInfo);
			clouddomeMat.addTexture(clouddomeAlphaInfo);

		SimpleMaterial doorArchMat = new SimpleMaterial();
			doorArchMat.addTexture(doorGoldArchInfo);

		SimpleMaterial waterfallrockMat = new SimpleMaterial();
			waterfallrockMat.addTexture(waterfallrockTextureInfo);
		
		SimpleMaterial wallMat = new SimpleMaterial();
			wallMat.addTexture(wallStumpRockInfo);
		
		SimpleMaterial castleDirtMat = new SimpleMaterial();
			castleDirtMat.addTexture(castleBranchDirtInfo);
			castleDirtMat.addTexture(castleBranchDirtAlphaInfo);

		SimpleMaterial pathDirtCloverPlantsMat = new SimpleMaterial();
			pathDirtCloverPlantsMat.addTexture(pathDirtCloverPlantsInfo);
			pathDirtCloverPlantsMat.addTexture(pathDirtCloverPlantsAlphaInfo);

		SimpleMaterial gateFernRainbowMat = new SimpleMaterial();
			gateFernRainbowMat.addTexture(potGateFernRainbowTextureInfo);
			gateFernRainbowMat.addTexture(potGateFernRainbowTextureAlphaInfo);
		
		PhongMaterial shroomMat = new PhongMaterial();
			shroomMat.setShininess(70.0f);
			shroomMat.addTexture(waterfallrockTextureInfo);

		SimpleGlowMaterial goldMat = new SimpleGlowMaterial();
			goldMat.addTexture(doorGoldArchInfo);

		PhongMaterial potMat = new PhongMaterial();
			potMat.setShininess(92.3f);
			potMat.addTexture(potGateFernRainbowTextureInfo);

		PhongMaterial treeMat = new PhongMaterial();
			treeMat.setShininess(100.0f);
			treeMat.addTexture(treeInfo);
		
		PhongMaterial grassMat = new PhongMaterial();
			grassMat.setShininess(100.0f);
			grassMat.addTexture(grassInfo);
		
		PhongMaterial stumpRockMat = new PhongMaterial();
			stumpRockMat.setShininess(80.0f);
			stumpRockMat.addTexture(wallStumpRockInfo);
			
		DiffuseMaterial bigtreeMat = new DiffuseMaterial();
			bigtreeMat.addTexture(treeInfo);
			bigtreeMat.addTexture(treeBumpInfo);


		///////////////
		// Create Objects
		///////////////
		try {	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.skydome));
	    	skydome = new BaseObject3D((SerializedObject3D)ois.readObject());
			skydome.setMaterial(skyMat);
			skydome.setY(20);
			skydome.setRotY(5);
			skydome.setScale(1.25f);
			skydome.setDoubleSided(true);

	    	clouddome = skydome.clone(false);
	    	clouddome.setMaterial(clouddomeMat);
	    	clouddome.setPosition(0, 10, 0);
	    	clouddome.setRotY(5);
	    	clouddome.setScale(.50f);
	    	clouddome.setDoubleSided(true);
	    	clouddome.setBlendingEnabled(true);
	    	clouddome.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			sun = new Plane(50, 50, 1, 1 , 1);
			sun.setMaterial(sunMat);
			sun.setPosition(200, 50, -100);
			sun.setRotation(0, 100, 0);
			sun.setBlendingEnabled(true);
			sun.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.castle_towers));
	    	castletowers = new BaseObject3D((SerializedObject3D)ois.readObject());
			castletowers.setMaterial(castleDirtMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.castle));
	    	castle = new BaseObject3D((SerializedObject3D)ois.readObject());
			castle.setMaterial(castleDirtMat);
			castle.setBlendingEnabled(true);
			castle.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.ground));
	    	ground = new BaseObject3D((SerializedObject3D)ois.readObject());
			ground.setMaterial(grassMat);
			ground.addLight(pLight_ground);
			ground.addLight(pLight_ground2);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.dirt));
	    	dirt = new BaseObject3D((SerializedObject3D)ois.readObject());
			dirt.setMaterial(castleDirtMat);
			dirt.setY(.1f);
			dirt.setBlendingEnabled(true);
			dirt.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows1));
	    	shadows1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows1.setMaterial(pathDirtCloverPlantsMat);
			shadows1.setBlendingEnabled(true);
			shadows1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows2));
	    	shadows2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows2.setMaterial(castleDirtMat);
			shadows2.setBlendingEnabled(true);
			shadows2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.path));
	    	path = new BaseObject3D((SerializedObject3D)ois.readObject());
			path.setMaterial(pathDirtCloverPlantsMat);
			path.setBlendingEnabled(true);
			path.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.lilys));
	    	lilys = new BaseObject3D((SerializedObject3D)ois.readObject());
			lilys.setMaterial(pathDirtCloverPlantsMat);
			lilys.setBlendingEnabled(true);
			lilys.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			lilys.setY(.1f);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers1));
	    	flowers1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers1.setMaterial(pathDirtCloverPlantsMat);
			flowers1.setDoubleSided(true);
			flowers1.setBlendingEnabled(true);
			flowers1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers2));
	    	flowers2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers2.setMaterial(pathDirtCloverPlantsMat);
			flowers2.setDoubleSided(true);
			flowers2.setBlendingEnabled(true);
			flowers2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers3));
	    	flowers3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers3.setMaterial(pathDirtCloverPlantsMat);
			flowers3.setDoubleSided(true);
			flowers3.setBlendingEnabled(true);
			flowers3.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers4));
	    	flowers4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers4.setMaterial(pathDirtCloverPlantsMat);
			flowers4.setDoubleSided(true);
			flowers4.setBlendingEnabled(true);
			flowers4.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers5));
	    	flowers5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers5.setMaterial(pathDirtCloverPlantsMat);
			flowers5.setDoubleSided(true);
			flowers5.setBlendingEnabled(true);
			flowers5.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers6));
	    	flowers6 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers6.setMaterial(pathDirtCloverPlantsMat);
			flowers6.setDoubleSided(true);
			flowers6.setBlendingEnabled(true);
			flowers6.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.vines1));
	    	vines1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			vines1.setMaterial(pathDirtCloverPlantsMat);
			vines1.setDoubleSided(true);
			vines1.setBlendingEnabled(true);
			vines1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.vines2));
	    	vines2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			vines2.setMaterial(pathDirtCloverPlantsMat);
			vines2.setDoubleSided(true);
			vines2.setBlendingEnabled(true);
			vines2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.vines3));
	    	vines3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			vines3.setMaterial(pathDirtCloverPlantsMat);
			vines3.setDoubleSided(true);
			vines3.setBlendingEnabled(true);
			vines3.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.walls));
	    	walls = new BaseObject3D((SerializedObject3D)ois.readObject());
			walls.setMaterial(wallMat);
			walls.setDoubleSided(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.gate));
	    	gate = new BaseObject3D((SerializedObject3D)ois.readObject());
			gate.setMaterial(gateFernRainbowMat);
			gate.setBlendingEnabled(true);
			gate.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.arch));
	    	arch = new BaseObject3D((SerializedObject3D)ois.readObject());
			arch.setMaterial(doorArchMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.stump));
	    	stump = new BaseObject3D((SerializedObject3D)ois.readObject());
			stump.setMaterial(stumpRockMat);
			stump.addLight(pLight_ground);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.stumpdecal));
	    	stumpdecal = new BaseObject3D((SerializedObject3D)ois.readObject());
			stumpdecal.setMaterial(pathDirtCloverPlantsMat);
			stumpdecal.setBlendingEnabled(true);
			stumpdecal.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			stumpdecal.setY(.15f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.largestump));
	    	largestump = new BaseObject3D((SerializedObject3D)ois.readObject());
			largestump.setMaterial(bigtreeMat);
			largestump.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.waterfall));
	    	waterfallrock = new BaseObject3D((SerializedObject3D)ois.readObject());
			waterfallrock.setMaterial(waterfallrockMat);
			waterfallrock.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.pot));
	    	pot = new BaseObject3D((SerializedObject3D)ois.readObject());
			pot.setMaterial(potMat);
			pot.setDoubleSided(true);
			pot.addLight(pLight_pot);
			pot.addLight(pLight_pot2);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.gold));
	    	gold = new BaseObject3D((SerializedObject3D)ois.readObject());
			gold.setMaterial(goldMat);
			gold.setDoubleSided(true);
			gold.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.rbow1));
	    	rbow1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			rbow1.setMaterial(gateFernRainbowMat);
			rbow1.setDoubleSided(true);
			rbow1.setBlendingEnabled(true);
			rbow1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.rbow2));
	    	rbow2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			rbow2.setMaterial(gateFernRainbowMat);
			rbow2.setDoubleSided(true);
			rbow2.setBlendingEnabled(true);
			rbow2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tree));
	    	tree = new BaseObject3D((SerializedObject3D)ois.readObject());
			tree.setMaterial(bigtreeMat);
			tree.addLight(pLight_ground);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.door));
	    	door = new BaseObject3D((SerializedObject3D)ois.readObject());
			door.setMaterial(doorArchMat);
			door.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.rocks));
	    	rocks = new BaseObject3D((SerializedObject3D)ois.readObject());
			rocks.setMaterial(stumpRockMat);
			rocks.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shrooms));
	    	shrooms = new BaseObject3D((SerializedObject3D)ois.readObject());
			shrooms.setMaterial(shroomMat);
			shrooms.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shamrock));
	    	shamrock = new BaseObject3D((SerializedObject3D)ois.readObject());
			shamrock.setMaterial(pathDirtCloverPlantsMat);
			shamrock.setBlendingEnabled(true);
			shamrock.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			shamrock.addLight(pLight_ground);
			shamrock.setPosition(-7.8f, -2.1f, -3.2f);
			shamrocks = new BaseObject3D();
			shamrocks.addChild(shamrock);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.treeferns));
	    	treeferns = new BaseObject3D((SerializedObject3D)ois.readObject());
			treeferns.setMaterial(gateFernRainbowMat);
			treeferns.setDoubleSided(true);
			treeferns.setBlendingEnabled(true);
			treeferns.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.pondferns));
	    	pondferns = new BaseObject3D((SerializedObject3D)ois.readObject());
			pondferns.setMaterial(gateFernRainbowMat);
			pondferns.setDoubleSided(true);
			pondferns.setBlendingEnabled(true);
			pondferns.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass1));
	    	grass1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass1.setMaterial(pathDirtCloverPlantsMat);
			grass1.setBlendingEnabled(true);
			grass1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass2));
	    	grass2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass2.setMaterial(pathDirtCloverPlantsMat);
			grass2.setBlendingEnabled(true);
			grass2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass3));
	    	grass3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass3.setMaterial(pathDirtCloverPlantsMat);
			grass3.setBlendingEnabled(true);
			grass3.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass4));
	    	grass4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass4.setMaterial(pathDirtCloverPlantsMat);
			grass4.setBlendingEnabled(true);
			grass4.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass5));
	    	grass5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass5.setMaterial(pathDirtCloverPlantsMat);
			grass5.setBlendingEnabled(true);
			grass5.setPosition(.2f, .2f, .2f);
			grass5.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgtrees));
	    	fgtrees = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgtrees.setMaterial(treeMat);
			fgtrees.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgferns));
	    	fgferns = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgferns.setMaterial(gateFernRainbowMat);
			fgferns.setDoubleSided(true);
			fgferns.setBlendingEnabled(true);
			fgferns.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			ois.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		createBird();
		createWater();
		createWaterfall();
		createFairies();
		createBranches();
	}	

	private void loadInterior(){
		pLight_candle1 = new PointLight();
		pLight_candle1.setPosition(13.88f, 4.25f, -2.5f);
		pLight_candle1.setPower(4f);
		pLight_candle1.setColor(0xfedccc);

		pLight_candle2 = new PointLight();
		pLight_candle2.setPosition(2.5f, 4.25f, -10.5f);
		pLight_candle2.setPower(4f);
		pLight_candle2.setColor(0xfedccc);

		pLight_candle3 = new PointLight();
		pLight_candle3.setPosition(-2.5f, 4.25f, 13.5f);
		pLight_candle3.setPower(4f);
		pLight_candle3.setColor(0xfedccc);
				
		///////////////
		// Create Materials
		///////////////
		SimpleMaterial intAlphaMat = new SimpleMaterial();
			intAlphaMat.addTexture(intAlphaInfo);
			intAlphaMat.addTexture(intAlphaAlphaInfo);
		
		DiffuseMaterial intWallsMat = new DiffuseMaterial();
			intWallsMat.addTexture(intWallsinfo);
	
		SimpleMaterial intFloorMat = new SimpleMaterial();
			intFloorMat.addTexture(intFloorInfo);
	
		SimpleMaterial intDoorStoveMat = new SimpleMaterial();
			intDoorStoveMat.addTexture(intDoorStoveInfo);
	
		DiffuseMaterial intArchShelfOBookStepWsCandleBannerPoleMat = new DiffuseMaterial();
			intArchShelfOBookStepWsCandleBannerPoleMat.addTexture(intArchShelfOBookStepWsCandleBannerPoleInfo);
		
		SimpleMaterial intBoardMat = new SimpleMaterial();
			intBoardMat.addTexture(intHatStandBoardShoebuckleInfo);
			
		DiffuseMaterial intHatStandMat = new DiffuseMaterial();
			intHatStandMat.addTexture(intHatStandBoardShoebuckleInfo);
			
		PhongMaterial intShoebuckleMat = new PhongMaterial();
			intShoebuckleMat.addTexture(intHatStandBoardShoebuckleInfo);
		
		SimpleMaterial intLogTablesRunnerMugPipeCoinMat = new SimpleMaterial();
			intLogTablesRunnerMugPipeCoinMat.addTexture(intLogTablesRunnerMugPipeCoinInfo);
	
		SimpleGlowMaterial intCoinMat = new SimpleGlowMaterial();
			intCoinMat.addTexture(intLogTablesRunnerMugPipeCoinInfo);
	
			SimpleMaterial intChairsBookcaseBooksMat = new SimpleMaterial();
			intChairsBookcaseBooksMat.addTexture(intChairsBookcaseBooksInfo);
		
		PhongMaterial vaseMat = new PhongMaterial();
			vaseMat.setUseColor(true);
			vaseMat.setShininess(50f);
				
		///////////////
		// Create Objects
		///////////////
		try {	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_tree));
	    	int_tree = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_tree.setMaterial(intWallsMat);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_floor));
	    	int_floor = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_floor.setMaterial(intFloorMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_door));
	    	int_door = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_door.setMaterial(intDoorStoveMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_door_window));
	    	int_door_window = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_door_window.setMaterial(intAlphaMat);
	    	int_door_window.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_stairs));
	    	int_stairs = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_stairs.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_coatrack));
	    	int_coatrack = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_coatrack.setMaterial(intHatStandMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_shoes));
	    	int_shoes = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_shoes.setMaterial(intShoebuckleMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_hat));
	    	int_hat = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_hat.setMaterial(intHatStandMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_buckles));
	    	int_buckles = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_buckles.setMaterial(intShoebuckleMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_candles));
	    	int_candles = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_candles.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_window1));
	    	int_window1 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_window1.setMaterial(intAlphaMat);
	    	int_window1.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_dirt));
	    	int_dirt = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_dirt.setMaterial(intWallsMat);
	    	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clovers1));
	    	int_clovers1 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clovers1.setMaterial(intAlphaMat);
	    	int_clovers1.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clovers2));
	    	int_clovers2 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clovers2.setMaterial(intAlphaMat);
	    	int_clovers2.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clovers3));
	    	int_clovers3 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clovers3.setMaterial(intAlphaMat);
	    	int_clovers3.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clovers4));
	    	int_clovers4 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clovers4.setMaterial(intAlphaMat);
	    	int_clovers4.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clovers5));
	    	int_clovers5 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clovers5.setMaterial(intAlphaMat);
	    	int_clovers5.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clovers6));
	    	int_clovers6 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clovers6.setMaterial(intAlphaMat);
	    	int_clovers6.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clovers7));
	    	int_clovers7 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clovers7.setMaterial(intAlphaMat);
	    	int_clovers7.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_board));
	    	int_board = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_board.setMaterial(intBoardMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_arches));
	    	int_arches = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_arches.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_window2));
	    	int_window2 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_window2.setMaterial(intAlphaMat);
	    	int_window2.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_window2_sill));
	    	int_window2_sill = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_window2_sill.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);
	    	int_window2_sill.setDoubleSided(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_shelf));
	    	int_shelf = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_shelf.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_chair));
	    	int_chair = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_chair.setMaterial(intChairsBookcaseBooksMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_large_table));
	    	int_large_table = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_large_table.setMaterial(intLogTablesRunnerMugPipeCoinMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_tablerunner));
	    	int_tablerunner = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_tablerunner.setMaterial(intLogTablesRunnerMugPipeCoinMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_mug));
	    	int_mug = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_mug.setMaterial(intLogTablesRunnerMugPipeCoinMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_pipe));
	    	int_pipe = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_pipe.setMaterial(intLogTablesRunnerMugPipeCoinMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_gold));
	    	int_gold = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_gold.setMaterial(intCoinMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_vase1));
	    	int_vase1 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_vase1.setMaterial(vaseMat);
	    	int_vase1.setTransparent(true);
	    	int_vase1.setColor(0x3366ff);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_clover_table));
	    	int_clover_table = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_clover_table.setMaterial(intAlphaMat);
	    	int_clover_table.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_vase2));
	    	int_vase2 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_vase2.setMaterial(vaseMat);
	    	int_vase2.setTransparent(true);
	    	int_vase2.setColor(0x3366ff);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_stone_pad));
	    	int_stone_pad = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_stone_pad.setMaterial(intDoorStoveMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_stove));
	    	int_stove = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_stove.setMaterial(intDoorStoveMat);
	    	int_stove.setDoubleSided(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_chimneyring));
	    	int_chimneyring = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_chimneyring.setMaterial(intDoorStoveMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_logs));
	    	int_logs = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_logs.setMaterial(intLogTablesRunnerMugPipeCoinMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_rug));
	    	int_rug = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_rug.setMaterial(intAlphaMat);
	    	int_rug.setTransparent(true);
	    	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_books_rocker));
	    	int_books_rocker = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_books_rocker.setMaterial(intChairsBookcaseBooksMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_small_table));
	    	int_small_table = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_small_table.setMaterial(intLogTablesRunnerMugPipeCoinMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_open_book));
	    	int_open_book = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_open_book.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);
	    	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_lamp1));
	    	int_lamp1 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_lamp1.setMaterial(intAlphaMat);
	    	int_lamp1.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_lamp2));
	    	int_lamp2 = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_lamp2.setMaterial(intAlphaMat);
	    	int_lamp2.setTransparent(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_tapestry_rod));
	    	int_tapestry_rod = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_tapestry_rod.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.int_tapestry));
	    	int_tapestry = new BaseObject3D((SerializedObject3D)ois.readObject());
	    	int_tapestry.setMaterial(intArchShelfOBookStepWsCandleBannerPoleMat);

			ois.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		createFlames();
	}
	
	private void createBird(){
		bird = new BaseObject3D();
		
		for(int i = 0; i <  birdTex.length/2; i++){
			Plane birdFrame = new Plane(1,1,1,1,1);		
			birdFrame.setMaterial(new SimpleMaterial());
			birdFrame.addTexture(birdTex[2*i]);
			birdFrame.addTexture(birdTex[2*i+1]);
			birdFrame.setDoubleSided(true);
			birdFrame.setBlendingEnabled(true);
			birdFrame.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			birdFrame.setRotZ(-90);
			bird.addChild(birdFrame);
		}
		
		bird.setPosition(-50, 8, -7);
		bird.setVisible(false);
		
		birdPath1 = new CatmullRomPath3D();
		birdPath1.addPoint(new Number3D(-111, 50, -30));
		birdPath1.addPoint(new Number3D(-63, 24, -56));
		birdPath1.addPoint(new Number3D(8, 14.5f, -45.5f));
		birdPath1.addPoint(new Number3D(64.5f, 25.5f, -29));
		birdPath1.addPoint(new Number3D(90.5f, 28, 20.5f));
		birdPath1.addPoint(new Number3D(80, 12, 69));
		birdPath1.addPoint(new Number3D(103.5f, 1.5f, 109.5f));
	}

	private void createFlames(){
		int_candleFlame1 = new BaseObject3D();
		int_candleFlame3 = new BaseObject3D();
		int_candleFlame2 = new BaseObject3D();
		
		for(int i = 0; i <  intCandleTex.length/2; i++){
			Plane flameFrame = new Plane(1,1,1,1,1, true);		
			flameFrame.setMaterial(new SimpleMaterial());
			flameFrame.addTexture(intCandleTex[2*i]);
			flameFrame.addTexture(intCandleTex[2*i+1]);
			flameFrame.setDoubleSided(true);
			flameFrame.setBlendingEnabled(true);
			flameFrame.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			flameFrame.setLookAt(mCamera.getPosition());
			flameFrame.setScale(.75f);
			int_candleFlame1.addChild(flameFrame);
			int_candleFlame2.addChild(flameFrame);
			int_candleFlame3.addChild(flameFrame);
		}
		
		int_candleFlame1.setPosition(-2.95f, 4.5f, 14f);
		int_candleFlame2.setPosition(14.888f, 4.5f, -3.127f);
		int_candleFlame3.setPosition(2.744f, 4.5f, -11.28f);
	}

	private void createWater(){
		float tileWidth  = .25f;
		water = new BaseObject3D();
		waterTiles = new BaseObject3D [16];
		SimpleMaterial waterMat = new SimpleMaterial();
		waterMat.addTexture(waterInfo);
		waterMat.addTexture(waterAlphaInfo);
		
		for(int i = 0; i <  waterTiles.length; i++){
    		if(i%4 == 0) {
    			waterUVs = new float[] {   0f, (tileWidth+((i/4)*tileWidth)), tileWidth, (tileWidth+((i/4)*tileWidth)),   0f,   (0f+((i/4)*tileWidth)), .25f,   (0f+((i/4)*tileWidth)) };
	    	}else{
	    		for(int j = 0; j < waterUVs.length; j++) {
					if ( j%2 == 0){
						waterUVs[j]+=tileWidth;
						if (waterUVs[j] > 1) waterUVs[j]= 0;
					}
				}
	    	}
    		
			waterTiles[i] = new Plane(20,20,1,1);
			waterTiles[i].setMaterial(waterMat);
			waterTiles[i].setRotation(0, 90, 90);
			waterTiles[i].setPosition(9.5f, -3.25f, 14.5f);
			waterTiles[i].setDoubleSided(true);
			waterTiles[i].getGeometry().setTextureCoords(waterUVs);
			waterTiles[i].setBlendingEnabled(true);
			waterTiles[i].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			if(i > 0 )waterTiles[i].setVisible(false);
			water.addChild(waterTiles[i]);
    	}
	}
	
	private void createWaterfall(){
		waterfall = new BaseObject3D();
		BaseObject3D waterfallsprite = new BaseObject3D();
    	try {
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.waterfallsprite));
			waterfallsprite = new BaseObject3D((SerializedObject3D)ois.readObject());
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		waterfallTiles = new BaseObject3D [16];
				
		for(int i = 0; i <  waterfallTiles.length; i++){
			waterfallTiles[i] = waterfallsprite.clone();
			waterfallTiles[i].setMaterial(new SimpleMaterial());
			waterfallTiles[i].addTexture(waterfallTex[2*i]);
			waterfallTiles[i].addTexture(waterfallTex[2*i+1]);
			waterfallTiles[i].setDoubleSided(true);
			waterfallTiles[i].setBlendingEnabled(true);
			waterfallTiles[i].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			if(i > 0 )waterfallTiles[i].setVisible(false);
			waterfall.addChild(waterfallTiles[i]);
		}
		
		int numRows = 8;
		float tileWidth  = .125f;
		float[] splashUVs = new float[32];
		
		splashTiles = new BaseObject3D [32];
		splash2Tiles = new BaseObject3D [32];
		splash3Tiles = new BaseObject3D [32];
		
		SimpleMaterial splashMat = new SimpleMaterial();
		splashMat.addTexture(mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.splashatlas), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.splashatlas), TextureType.DIFFUSE)));
		splashMat.addTexture(mTextureManager.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.splashatlas_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.splashatlas), TextureType.ALPHA)));
		
		for(int i = 0; i <  splashTiles.length; i++){
    		if(i%numRows == 0) {
    			splashUVs = new float[] {   0f, (tileWidth+((i/numRows)*tileWidth)), tileWidth, (tileWidth+((i/numRows)*tileWidth)),   0f,   (0f+((i/numRows)*tileWidth)), tileWidth,   (0f+((i/numRows)*tileWidth)) };
	    	}else{
	    		for(int j = 0; j < splashUVs.length; j++) {
					if ( j%2 == 0){
						splashUVs[j]+=tileWidth;
						if (splashUVs[j] > 1) splashUVs[j]= 0;
					}
				}
	    	}
    		
			splashTiles[i] = new Plane(5f,7f,1,1);
			splashTiles[i].setMaterial(splashMat);
			splashTiles[i].setRotation(-90, -95, -10);
			splashTiles[i].setPosition(16f, 0.5f, 11.7f);
			splashTiles[i].setDoubleSided(true);
			splashTiles[i].getGeometry().setTextureCoords(splashUVs);
			splashTiles[i].setBlendingEnabled(true);
			splashTiles[i].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			splashTiles[i].setVisible(false);
			waterfall.addChild(splashTiles[i]);

			splash2Tiles[i] = new Plane(5,5,1,1);
			splash2Tiles[i].setMaterial(splashMat);
			splash2Tiles[i].setRotation(-90, -90, -25);
			splash2Tiles[i].setPosition(15.3f, -2f, 14f);
			splash2Tiles[i].setDoubleSided(true);
			splash2Tiles[i].getGeometry().setTextureCoords(splashUVs);
			splash2Tiles[i].setBlendingEnabled(true);
			splash2Tiles[i].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			splash2Tiles[i].setVisible(false);
			waterfall.addChild(splash2Tiles[i]);

			splash3Tiles[i] = new Plane(5,5,1,1);
			splash3Tiles[i].setMaterial(splashMat);
			splash3Tiles[i].setRotation(-90, -90, 25);
			splash3Tiles[i].setPosition(15f, -2.8f, 9.5f);
			splash3Tiles[i].setDoubleSided(true);
			splash3Tiles[i].getGeometry().setTextureCoords(splashUVs);
			splash3Tiles[i].setBlendingEnabled(true);
			splash3Tiles[i].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			splash3Tiles[i].setVisible(false);
			waterfall.addChild(splash3Tiles[i]);
		}
	}

	private void createFairies(){
		fairies = new BaseObject3D();
		fairies.setPosition(-6, -.25f, 19.5f);
		int numChildren = 16;

		SimpleGlowMaterial fairyMat = new SimpleGlowMaterial();
		fairyMat.addTexture(fairyInfo);
		
		for(int i = 0; i < numChildren; ++i){
			BaseObject3D fairy = new Sphere(.05f, 20,20);
			fairy.setPosition((float) (Math.random()*2)-1, (float) (Math.random()*2)-1, (float) (Math.random()*2)-1);
			fairy.setMaterial(fairyMat);
			fairy.setDoubleSided(true);
			fairy.setBlendingEnabled(true);
			fairy.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
			fairy.setScale(0);
			fairies.addChild(fairy);
		}
	}
	
	private void createBranches(){
		branches = new BaseObject3D();
		branchTiles = new BaseObject3D [15];
		float[] uvCoords =  new float[] {.5f, 1f, .5f, .5f, 0.01f, 1f, 0.01f, .5f};

		for(int i = 0; i < branchTiles.length; i++){
			branchTiles[i] = new TexturedPlane(10, 10, 1, 1, 1, uvCoords);
			branchTiles[i].setDoubleSided(true);
			branchTiles[i].setBlendingEnabled(true);
			branchTiles[i].addLight(pLight_ground);
			branchTiles[i].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			if(i > 0)branchTiles[i].setLookAt(new Number3D(-19, 3, 37)); // Point most branches at initial camera position
			branchTiles[i].setMaterial(new SimpleMaterial());
			branchTiles[i].addTexture(castleBranchDirtInfo);
			branchTiles[i].addTexture(castleBranchDirtAlphaInfo);
			branchTiles[i].addLight(pLight_branches);
		}

		branchTiles[0].setPosition(-13f, 6.5f, 7f); //Far side of tree house
		branchTiles[0].setRotY(-90);
		branches.addChild(branchTiles[0]);

		branchTiles[1].setPosition(-12.5f, 5f, 13f); //Near side of treehouse
		branchTiles[1].setRotY(90);
		branches.addChild(branchTiles[1]);

		branchTiles[2].setPosition(13, 6f, 10); //Right side of waterfall
		branches.addChild(branchTiles[2]);

		branchTiles[3].setPosition(11, 6, 16); //Left Side of waterfall
		branches.addChild(branchTiles[3]);

		branches.addChild(rbow2);			// Had to add the rainbow segment to branches because of layering issues

		branchTiles[4].setPosition(13.5f, 5, 20);
		branches.addChild(branchTiles[4]);

		branchTiles[5].setPosition(7, 6.5f, 22);
		branches.addChild(branchTiles[5]);

		branchTiles[6].setPosition(3, 5.5f, 25);
		branches.addChild(branchTiles[6]);	
		
		branchTiles[7].setPosition(-13f, 7f, 18f);
		branches.addChild(branchTiles[7]);
		
		branchTiles[8].setPosition(-7.5f, 8, 22);
		branches.addChild(branchTiles[8]);
		
		branchTiles[9].setPosition(-12f, 7, 30);
		branches.addChild(branchTiles[9]);
		
		branchTiles[10].setPosition(0f, 6, 30);
		branches.addChild(branchTiles[10]);
		
		branchTiles[11].setPosition(-6f, 7.5f, 32);
		branches.addChild(branchTiles[11]);
		
		branchTiles[12].setPosition(-8f, 7, 33);
		branches.addChild(branchTiles[12]);
		
		branchTiles[13].setPosition(-7f, 6, 34);
		branches.addChild(branchTiles[13]);
		
		branchTiles[14].setPosition(4, 5, 35);
		branches.addChild(branchTiles[14]);
	}
	
	private void addObjects(){
		exterior = new BaseObject3D();
		interior = new BaseObject3D();
		/////////////
		//Add Exterior
		/////////////
		exterior.addChild(skydome);
		exterior.addChild(sun);
		exterior.addChild(clouddome);
		exterior.addChild(rbow1);
		exterior.addChild(castletowers);
		exterior.addChild(castle);
		exterior.addChild(ground);
		exterior.addChild(path);
		exterior.addChild(bird);
		exterior.addChild(dirt);
		exterior.addChild(tree);
		exterior.addChild(shadows1);
		exterior.addChild(shadows2);
		exterior.addChild(rocks);
		exterior.addChild(water);
		exterior.addChild(lilys);
		exterior.addChild(walls);
		exterior.addChild(gate);
		exterior.addChild(arch);
		exterior.addChild(largestump);
		exterior.addChild(stumpdecal);
		exterior.addChild(stump);
		exterior.addChild(shrooms);
		exterior.addChild(door);
		exterior.addChild(vines1);
		exterior.addChild(vines2);
		exterior.addChild(grass1);
		exterior.addChild(grass2);
		exterior.addChild(grass3);
		exterior.addChild(grass4);
		exterior.addChild(grass5);
		exterior.addChild(flowers1);
		exterior.addChild(flowers2);
		exterior.addChild(flowers3);
		exterior.addChild(flowers4);
		exterior.addChild(flowers5);
		exterior.addChild(flowers6);
		exterior.addChild(vines3);
		exterior.addChild(waterfallrock);
		exterior.addChild(waterfall);
		exterior.addChild(shamrocks);
		exterior.addChild(treeferns);
		exterior.addChild(pondferns);
		exterior.addChild(pot);		
		exterior.addChild(gold);
		exterior.addChild(fairies);
		exterior.addChild(fgtrees);
		exterior.addChild(fgferns);
		exterior.addChild(branches);
		exterior.setVisible(false);
		addChild(exterior);
		/////////////
		//Add Interior
		/////////////
		interior.addChild(int_tree);
		interior.addChild(int_floor);
		interior.addChild(int_door);
		interior.addChild(int_door_window);
		interior.addChild(int_stairs);
		interior.addChild(int_coatrack);
		interior.addChild(int_hat);
		interior.addChild(int_shoes);
		interior.addChild(int_buckles);
		interior.addChild(int_candles);
		interior.addChild(int_window1);
		interior.addChild(int_dirt);
		interior.addChild(int_clovers1);
		interior.addChild(int_clovers2);
		interior.addChild(int_clovers3);
		interior.addChild(int_clovers4);
		interior.addChild(int_clovers5);
		interior.addChild(int_clovers6);
		interior.addChild(int_clovers7);
		interior.addChild(int_board);
		interior.addChild(int_arches);
		interior.addChild(int_window2);
		interior.addChild(int_window2_sill);
		interior.addChild(int_shelf);
		interior.addChild(int_chair);
		interior.addChild(int_large_table);
		interior.addChild(int_tablerunner);
		interior.addChild(int_mug);
		interior.addChild(int_pipe);
		interior.addChild(int_gold);
		interior.addChild(int_vase1);
		interior.addChild(int_clover_table);
		interior.addChild(int_vase2);
		interior.addChild(int_stone_pad);
		interior.addChild(int_stove);
		interior.addChild(int_chimneyring);
		interior.addChild(int_logs);
		interior.addChild(int_rug);
		interior.addChild(int_books_rocker);
		interior.addChild(int_small_table);
		interior.addChild(int_open_book);
		interior.addChild(int_lamp1);
		interior.addChild(int_lamp2);
		interior.addChild(int_tapestry_rod);
		interior.addChild(int_tapestry);
		interior.addChild(int_candleFlame1);
		interior.addChild(int_candleFlame2);
		interior.addChild(int_candleFlame3);
		interior.setVisible(false);
		addChild(interior);
		interior.addLight(pLight_candle1);
		interior.addLight(pLight_candle2);
		interior.addLight(pLight_candle3);
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);
		preferences.registerOnSharedPreferenceChangeListener(mListener);
        if(timer != null)timer.start();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { // Check for screen rotation
		super.onSurfaceChanged(gl, width, height);
	}

	@Override
	public void onSurfaceDestroyed() {
		super.onSurfaceDestroyed();
		preferences.unregisterOnSharedPreferenceChangeListener(mListener);
		timer.cancel();
	}
	
	@Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if(!visible)
        {
            if(timer != null)timer.cancel();
        }else if(visible){
            if(timer != null)timer.start();
        }
    }

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		checkScene();
		if(sceneInit && !redrawScene){
			if(scene == 0){
				if(Math.random() > .75f && totalCount % 300 == 0 && birdDone) 
					birdMovement();
				fairyMovement();
			}else if (scene == 1){
				//TODO: Pipe Smoke
				//TODO: Light Rays
				//TODO: Leprechaun
			}
		}
		totalCount++;
	}
	
	@Override
	public void onTouchEvent(MotionEvent me) {
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			xpos = me.getX();
		}
		if (me.getAction() == MotionEvent.ACTION_MOVE) {
			float xd = xpos - me.getX(0);

			if(me.getPointerCount()==1 && firstTouch) {
				if(xd > 8){//If the pointer movement is left->right and of some significance
					camIndex--;
					if(camIndex < 0) camIndex = (cameraLook.length - 1); //this creates our camera loop in the negative direction
					firstTouch = false;// this prevents over calling the camera position function while the pointer is still down
				}
				else if (xd < -8){//If the pointer movement is right->left and of some significance
					camIndex++;
					if(camIndex > (cameraLook.length - 1)) camIndex = 0;//this creates our camera loop in the positive direction
					firstTouch = false;
				}
				if(sceneInit)cameraPose();
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
	
	///////////////
	/////Animation and Conditionals
	///////////////
	private void initTimer() {
		timerNull = new BaseObject3D();

		timer = new RotationAnimation3D(0, 1, 0);
		timer.setDuration(1000);
		timer.setRepeatCount(Animation3D.INFINITE);
		timer.setRepeatMode(Animation3D.RESTART);
		timer.setTransformable3D(timerNull);
		timer.setAnimationListener(new Animation3DListener(){

			public void onAnimationEnd(Animation3D anim) {
			}

			public void onAnimationRepeat(Animation3D anim) {				
			}

			public void onAnimationStart(Animation3D anim) {
			}

			public void onAnimationUpdate(Animation3D animation, float interpolatedTime) {
				onTimerTick();
			}
		});
		timer.start();
	}

	private void onTimerTick() {
		if(sceneInit){
			cameraControl();
			cameraMovement();
			if(scene == 0){
				waterMovement();
			}else if(scene == 1){
				flameMovement();
			}
		}
	}
	
	private void waterMovement(){
		int speed = 8;
		if(waterCounter%speed == 0) {
	    	waterTiles[waterIndex].setVisible(true);
	    	waterfallTiles[waterIndex].setVisible(true);
	    	splashTiles[waterIndex].setVisible(true);
	    	splash2Tiles[waterIndex].setVisible(true);
	    	splash3Tiles[waterIndex].setVisible(true);
	   	
	    	if(waterIndex>0){
	    		waterTiles[waterIndex-1].setVisible(false);
				waterfallTiles[waterIndex-1].setVisible(false);
		    	splashTiles[waterIndex-1].setVisible(false);
		    	splash2Tiles[waterIndex-1].setVisible(false);
		    	splash3Tiles[waterIndex-1].setVisible(false);
			} else {
	    		waterTiles[waterTiles.length-1].setVisible(false);
				waterfallTiles[waterTiles.length-1].setVisible(false);
		    	splashTiles[splashTiles.length-1].setVisible(false);
		    	splash2Tiles[splashTiles.length-1].setVisible(false);
		    	splash3Tiles[splashTiles.length-1].setVisible(false);
			}
	    	
	    	if (waterIndex++ == waterTiles.length-1) 
	    		waterIndex = 0;
		}
    	if(waterCounter++ == speed*16) waterCounter = 0;
	}
	
	private void flameMovement(){
		if(flameCounter%4 == 0) {
	    	int_candleFlame1.getChildAt(flameIndex).setVisible(true);
	    	int_candleFlame2.getChildAt(flameIndex).setVisible(true);
	    	int_candleFlame3.getChildAt(flameIndex).setVisible(true);
	   	
	    	if(flameIndex>0){
	    		int_candleFlame1.getChildAt(flameIndex-1).setVisible(false);
	    		int_candleFlame2.getChildAt(flameIndex-1).setVisible(false);
	    		int_candleFlame3.getChildAt(flameIndex-1).setVisible(false);
			} else {
				int_candleFlame1.getChildAt(int_candleFlame1.getNumChildren()-1).setVisible(false);
				int_candleFlame2.getChildAt(int_candleFlame2.getNumChildren()-1).setVisible(false);
				int_candleFlame3.getChildAt(int_candleFlame3.getNumChildren()-1).setVisible(false);
			}
	    	
	    	if (flameIndex++ == int_candleFlame1.getNumChildren()-1) 
	    		flameIndex = 0;
		}
    	if(flameCounter++ == 64) flameCounter = 0;
	}

	private void fairyMovement(){
		if (fairyTimer == 10) {
			if(Math.random() > .33) blink(fairies.getChildAt(0));
			if(Math.random() > .5) blink(fairies.getChildAt(4));
			if(Math.random() > .7) blink(fairies.getChildAt(8));
			if(Math.random() > .9) blink(fairies.getChildAt(12));
		}
		else if (fairyTimer == 20) {
			if(Math.random() > .33) blink(fairies.getChildAt(1));
			if(Math.random() > .5) blink(fairies.getChildAt(5));
			if(Math.random() > .7) blink(fairies.getChildAt(9));
			if(Math.random() > .9) blink(fairies.getChildAt(13));
		}
		else if (fairyTimer == 30) {
			if(Math.random() > .33)  blink(fairies.getChildAt(2));
			if(Math.random() > .5) blink(fairies.getChildAt(6));
			if(Math.random() > .7) blink(fairies.getChildAt(10));
			if(Math.random() > .9) blink(fairies.getChildAt(14));
		}
		else if (fairyTimer == 40) {
			if(Math.random() > .33)  blink(fairies.getChildAt(3));
			if(Math.random() > .5) blink(fairies.getChildAt(7));
			if(Math.random() > .7) blink(fairies.getChildAt(11));
			if(Math.random() > .9) blink(fairies.getChildAt(15));
			fairyTimer = 0;
		}
		fairyTimer++;

		
		fairies.setRotation(fairies.getRotation().add(0, .1f, 0));

		for(int i = 0; i < fairies.getNumChildren(); ++i){
			BaseObject3D objPointer = fairies.getChildAt(i);
			
			float theta = (float) (totalCount*.005);
			float constant = .001f * i;
			
			objPointer.setPosition(
					(float) (objPointer.getX()+(Math.sin(theta)*(Math.random()*constant))), 
					(float) (objPointer.getY()+(Math.cos(theta)*(Math.random()*constant))), 
					(float) (objPointer.getZ()+(Math.sin(theta)*(Math.random()*constant)))
					);
			objPointer.setRotation((objPointer.getRotation().add((float)(Math.random()*5),(float)(Math.random()*3),(float)(Math.random()*5))));
		}
	}

	private void birdMovement(){
		TranslateAnimation3D birdFlight = new TranslateAnimation3D(birdPath1);
		birdFlight.setDuration(8000);
		birdFlight.setOrientToPath(true);
		birdFlight.setTransformable3D(bird);
		birdFlight.setAnimationListener(new Animation3DListener(){

			public void onAnimationEnd(Animation3D anim) {
				anim.cancel();
				anim.reset();
				birdDone = true;
				bird.setVisible(false);
			}

			public void onAnimationRepeat(Animation3D anim) {				
			}

			public void onAnimationStart(Animation3D anim) {
				birdDone = false;
				bird.setVisible(true);
			}

			public void onAnimationUpdate(Animation3D animation, float interpolatedTime) {
				bird.getChildAt(flapCounter).setVisible(true);
				if(flapCounter > 0)
					bird.getChildAt(flapCounter-1).setVisible(false);
				else
					bird.getChildAt(bird.getNumChildren()-1).setVisible(false);
				bird.setLookAt(mCamera.getPosition());			
				if(flapCounter++ == 3) flapCounter = 0;
				}
			
		});
		birdFlight.start();
//		if(Math.random() > .75f && totalCount % 300 == 0 ) birdDone = false;
//		
//		if(frameCounter%2 == 0 && !birdDone){
//			bird.setVisible(true);
//			bird.getChildAt(flapCounter).setVisible(true);
//			if(flapCounter > 0)
//				bird.getChildAt(flapCounter-1).setVisible(false);
//			else
//				bird.getChildAt(bird.getNumChildren()-1).setVisible(false);
//			bird.setLookAt(mCamera.getPosition());			
//			if(flapCounter++ == 3) flapCounter = 0;
//		}
//		
//		if(bird.getX() > 50 && !birdDone){
//			bird.setX(-50);
//			bird.setVisible(false);
//			birdDone = true;
//		}else if(!birdDone){
//			bird.setX(bird.getX()+.5f);
//		}
	}
		
	private void blink(final BaseObject3D blinkerObj){
		Animation3D blinkAnim = new ScaleAnimation3D(new Number3D(1, 1, 1));
		blinkAnim.setDuration(500);
		blinkAnim.setRepeatCount(1);
		blinkAnim.setRepeatMode(Animation3D.REVERSE);
		blinkAnim.setTransformable3D(blinkerObj);
		blinkAnim.setAnimationListener(new Animation3DListener() {

			public void onAnimationEnd(Animation3D anim) {
				anim.cancel();
				anim.reset();
				blinkerObj.setScale(0);
			}

			public void onAnimationRepeat(Animation3D anim) {				
			}

			public void onAnimationStart(Animation3D anim) {
			}

			public void onAnimationUpdate(Animation3D animation, float interpolatedTime) {
				
			}
			
		});
		blinkAnim.start();
	}

	///////////////
	/////Camera Mechanics
	///////////////
	private void setUpCamera(){
		if(!redrawScene){
			camLookNull = new BaseObject3D();
			camLookNull = new BaseObject3D();
		}
		if(scene == 0){
			mCamera.setFarPlane(2000);
			mCamera.setFogEnabled(true);
			mCamera.setFogColor(0x7d98da);
			mCamera.setFogFar(200);
			mCamera.setFogNear(20);
			cameraPos = new Number3D [7];
			cameraPos[0] = new Number3D(-19, 3, 37);
			cameraPos[1] = new Number3D(5, 1, 42);
			cameraPos[2] = new Number3D(9, 0, 31);
			cameraPos[3] = new Number3D(14, 0, 33);
			cameraPos[4] = new Number3D(0, 1, 12);
			cameraPos[5] = new Number3D(-9, 2, 15);
			cameraPos[6] = new Number3D(-13, 0, 23);
			
			cameraLook = new Number3D [7];
			cameraLook[0] = new Number3D(2, 1, -1);
			cameraLook[1] = new Number3D(-6, 0, 16);
			cameraLook[2] = new Number3D(13, -1, 14);
			cameraLook[3] = new Number3D(2, -1, 5);
			cameraLook[4] = new Number3D(-14, -1, 9.5f);
			cameraLook[5] = new Number3D(0, 0, 0);
			cameraLook[6] = new Number3D(9, -3, 12);			
		} else {
			mCamera.setFarPlane(1000);
			mCamera.setFogEnabled(false);
			cameraPos = new Number3D [5];
			cameraPos[4] = new Number3D( 7f, 2, -1);
			cameraPos[3] = new Number3D( 7f, 2, -1);
			cameraPos[2] = new Number3D( 2f, 3, 2);
			cameraPos[1] = new Number3D( 5f, 3, 8);
			cameraPos[0] = new Number3D( 0, 4, 0);

			cameraLook = new Number3D [5];
			cameraLook[4] = new Number3D( -1.5f, .8f, 15);
			cameraLook[3] = new Number3D(-10, .78f, 5.7f);
			cameraLook[2] = new Number3D(-5, .7f, -13);
			cameraLook[1] = new Number3D(12, 1.7f, -16);
			cameraLook[0] = new Number3D(15, 1.7f, 3);
		}
		camLookNull.setPosition(cameraLook[0]);
		mCamera.setPosition(cameraPos[0]);
		mCamera.setLookAt(camLookNull.getPosition());
		redrawScene = false;
	}

	private void cameraControl(){
		mCamera.setLookAt(camLookNull.getPosition());
	}

	private void cameraPose() {
		if(mCamera.getPosition() != cameraPos[camIndex]){
			moveCamera = true;
		}
		if(camLookNull.getPosition() != cameraLook[camIndex]){
			moveCameraLook = true;
		}
	}

	private void cameraMovement() {
		if(moveCamera){// When onTouchEvent reports a touch "moveCamera" is set to true
			float xDif = cameraPos[camIndex].x - mCamera.getX();  //Use the next camera position as a target, and find the current distance from it
			float yDif = cameraPos[camIndex].y - mCamera.getY();
			float zDif = cameraPos[camIndex].z - mCamera.getZ();

			float newX = mCamera.getX()+(xDif/camSpeed); //set a new coordinate that becomes smaller as the camera gets closer to the target
			float newY = mCamera.getY()+(yDif/camSpeed); //gives a trade mark easing effect
			float newZ = mCamera.getZ()+(zDif/camSpeed);

			float xLookDif = cameraLook[camIndex].x - camLookNull.getX(); //same thing for the "look at null"
			float yLookDif = cameraLook[camIndex].y - camLookNull.getY();
			float zLookDif = cameraLook[camIndex].z - camLookNull.getZ();

			float newLookX = camLookNull.getX()+(xLookDif/(camSpeed*2)); //but at half of the speed of the position
			float newLookY = camLookNull.getY()+(yLookDif/(camSpeed*2)); //provides a feeling of animated follow through motion
			float newLookZ = camLookNull.getZ()+(zLookDif/(camSpeed*2));

			mCamera.setPosition(newX, newY, newZ);
			camLookNull.setPosition(newLookX, newLookY, newLookZ); //Set new Camera and Look locations
			
			//Because the above easing equation will calculate infinitely we set some limits
			if (moveCamera && Math.abs(xDif)<.001f && Math.abs(yDif)<.001f && Math.abs(zDif)<.001f) {
				mCamera.setPosition(cameraPos[camIndex]);
				moveCamera = false;	
			}
			if(moveCameraLook){
				if (Math.abs(xLookDif)<.0001f && Math.abs(yLookDif)<.0001f && Math.abs(zLookDif)<.0001f) {
					camLookNull.setPosition(cameraLook[camIndex]); //Once teh limit is reached set the values explicitly
					moveCameraLook = false;//Disallow animation 
				}
			}
		}
//		mCamera.setPosition(mCamera.getX()+(float) (Math.sin(totalCount/40)/400), mCamera.getY()+(float) (Math.cos(totalCount/80)/800), mCamera.getZ()); // Wiggle the camera view
	}
}