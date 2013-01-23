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
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import rajawali.lights.PointLight;
import rajawali.materials.PhongMaterial;
import rajawali.materials.SimpleMaterial;
import rajawali.math.Number3D;
//Serializer//
import rajawali.parser.ObjParser;
import rajawali.primitives.Plane;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;


public class WallpaperRenderer extends RajawaliRenderer{
			
	private OnSharedPreferenceChangeListener mListener;

	private float xpos;
	
	private BaseObject3D camLookNull, skydome, castle, castletowers, ground, path, walls, gate, arch, stump, largestump, stumpdecal, lilys,
	waterfall, pot, gold, rbow1, rbow2, tree, door, rocks, shrooms, shamrocks, treeferns, pondferns, fgtrees, fgferns, vines1, vines2, vines3,
	grass1, grass2, grass3, grass4, grass5, grass6, flowers1, flowers2, flowers3, flowers4, flowers5, flowers6, dirt, shadows1, shadows2;
	
	private float[] waterUVs;
	private BaseObject3D[] waterTiles, waterfallTiles, branches;

	private int frameCounter = 0, tileIndex = 0, camSpeed = 20, waveCounter = 0;

	private Boolean sceneInit = false, moveCamera = false, moveCameraLook = false, firstTouch = true;;
	
	private Bitmap skyTex, castleTex, groundTex, pathTex, wallStumpTex, potBowTex, waterfallrockTex,
	doorGateArchTex, treeTex, shroomTex, waterTex;
	
	private Bitmap[] waterfallTex;

	private PointLight pLight_ground;
	
	private int camIndex;

	private Number3D [] cameraLook, cameraPos;

	private ObjectInputStream ois;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(60);
		setBackgroundColor(0x2b426e);
    }
		
	public void initScene() {
		setOnPreferenceChange();
		setPrefsToLocal();
		
		lightsCam();
		loadTextures();
		loadObjects();
		
		addObjects();
	}
	
	private void lightsCam(){
		mCamera.setFarPlane(2000);
		camLookNull = new BaseObject3D();
		cameraPos = new Number3D [8];
		
		cameraPos[0] = new Number3D(-19, 3, 37);
		cameraPos[1] = new Number3D(5, 1, 42);
		cameraPos[2] = new Number3D(9, 0, 31);
		cameraPos[3] = new Number3D(6, 2, 21);
		cameraPos[4] = new Number3D(0, 1, 12);
		cameraPos[5] = new Number3D(-9, 2, 15);
		cameraPos[6] = new Number3D(-4, 0, 19);
		cameraPos[7] = new Number3D(-13, 0, 23);
		
		cameraLook = new Number3D [8];
		cameraLook[0] = new Number3D(2, 1, -1);
		cameraLook[1] = new Number3D(-6, 0, 16);
		cameraLook[2] = new Number3D(13, -1, 14);
		cameraLook[3] = new Number3D(3, 0, 8);
		cameraLook[4] = new Number3D(-14, -1, 9.5f);
		cameraLook[5] = new Number3D(-9, 1, 9);
		cameraLook[6] = new Number3D(3, 0, 9);
		cameraLook[7] = new Number3D(9, -3, 12);
		
		mCamera.setPosition(cameraPos[0]);
		mCamera.setLookAt(cameraLook[0]);
		
		pLight_ground = new PointLight();
		pLight_ground.setPosition(-6.5f, 18, 18.5f);
		pLight_ground.setPower(2);
		pLight_ground.setAttenuation(100, 1, 0, 0);	
	}
	
	private void loadTextures(){
		skyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skydome);
		castleTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.castle_branch_dirt);
		groundTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.grass);
		pathTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.path);
		wallStumpTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wall_stump);
		potBowTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pot);
		waterfallrockTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterfallrock_tex);
		doorGateArchTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.door_gate_arch);
		treeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btree);
		shroomTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mushroom);		
		waterTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wateratlas);
	}
	
	private void loadObjects(){		
		try {	
			SimpleMaterial skyMat = new SimpleMaterial();
			SimpleMaterial castleBranchDirtMat = new SimpleMaterial();
			SimpleMaterial pathMat = new SimpleMaterial();
			SimpleMaterial fernWallMat = new SimpleMaterial();
			SimpleMaterial bowMat = new SimpleMaterial();
			SimpleMaterial waterfallrockMat = new SimpleMaterial();
			SimpleMaterial doorGateArchMat = new SimpleMaterial();
			SimpleMaterial bigtreeMat = new SimpleMaterial();

			PhongMaterial goldMat = new PhongMaterial();
			goldMat.setShininess(92.3f);

			PhongMaterial potMat = new PhongMaterial();
			potMat.setShininess(92.3f);

			PhongMaterial treeMat = new PhongMaterial();
			treeMat.setShininess(100);
			
			PhongMaterial shroomMat = new PhongMaterial();
			shroomMat.setShininess(70);

			PhongMaterial groundMat = new PhongMaterial();
			groundMat.setShininess(80);
			
			PhongMaterial wallStumpMat = new PhongMaterial();
			wallStumpMat.setShininess(80);
			
			skyMat.addTexture(mTextureManager.addTexture(skyTex));
			castleBranchDirtMat.addTexture(mTextureManager.addTexture(castleTex));
			groundMat.addTexture(mTextureManager.addTexture(groundTex));
			pathMat.addTexture(mTextureManager.addTexture(pathTex));
			fernWallMat.addTexture(mTextureManager.addTexture(wallStumpTex));
			wallStumpMat.addTexture(mTextureManager.addTexture(wallStumpTex));
			potMat.addTexture(mTextureManager.addTexture(potBowTex));
			bowMat.addTexture(mTextureManager.addTexture(potBowTex));
			waterfallrockMat.addTexture(mTextureManager.addTexture(waterfallrockTex));
			goldMat.addTexture(mTextureManager.addTexture(waterfallrockTex));
			doorGateArchMat.addTexture(mTextureManager.addTexture(doorGateArchTex));
			bigtreeMat.addTexture(mTextureManager.addTexture(treeTex));
			treeMat.addTexture(mTextureManager.addTexture(treeTex));
			shroomMat.addTexture(mTextureManager.addTexture(shroomTex));
					
//			objSerializer(R.raw.skydome_obj, "skydome.ser");
//			objSerializer(R.raw.castleb_obj, "castleb.ser");
//			objSerializer(R.raw.castle_obj, "castle.ser");
//			objSerializer(R.raw.ground_obj, "ground.ser");
//			objSerializer(R.raw.dirt_obj, "dirt.ser");
//			objSerializer(R.raw.shadows1_obj, "shadows1.ser");
//			objSerializer(R.raw.shadows2_obj, "shadows2.ser");
//
//			objSerializer(R.raw.path_obj, "path.ser");
//			objSerializer(R.raw.lilys_obj, "lilys.ser");
//			objSerializer(R.raw.flowers1_obj, "flowers1.ser");
//			objSerializer(R.raw.flowers2_obj, "flowers2.ser");
//			objSerializer(R.raw.flowers3_obj, "flowers3.ser");
//			objSerializer(R.raw.flowers4_obj, "flowers4.ser");
//			objSerializer(R.raw.flowers5_obj, "flowers5.ser");
//			objSerializer(R.raw.flowers6_obj, "flowers6.ser");
//
//			objSerializer(R.raw.vines1_obj, "vines1.ser");
//			objSerializer(R.raw.vines2_obj, "vines2.ser");
//			objSerializer(R.raw.vines3_obj, "vines3.ser");
//			objSerializer(R.raw.stumpdecal_obj, "stumpdecal.ser");
//			objSerializer(R.raw.walls_obj, "walls.ser");
//			objSerializer(R.raw.gate_obj, "gate.ser");
//			objSerializer(R.raw.arch_obj, "arch.ser");
//			objSerializer(R.raw.stump_obj, "stump.ser");
//
//			objSerializer(R.raw.largestump_obj, "largestump.ser");
//			objSerializer(R.raw.waterfall_obj, "waterfall.ser");
//			objSerializer(R.raw.pot_obj, "pot.ser");
//			objSerializer(R.raw.gold_obj, "gold.ser");
//			objSerializer(R.raw.rbow1_obj, "rbow1.ser");
//			objSerializer(R.raw.rbow2_obj, "rbow2.ser");
//			objSerializer(R.raw.tree_obj, "tree.ser");
//			objSerializer(R.raw.door_obj, "door.ser");
//
//			objSerializer(R.raw.rocks_obj, "rocks.ser");
//			objSerializer(R.raw.shrooms_obj, "shrooms.ser");
//			objSerializer(R.raw.shamrocks_obj, "shamrocks.ser");
//			objSerializer(R.raw.treeferns_obj, "treeferns.ser");
//			objSerializer(R.raw.pondferns_obj, "pondferns.ser");
//			objSerializer(R.raw.grass1_obj, "grass1.ser");
//			objSerializer(R.raw.grass2_obj, "grass2.ser");
//			objSerializer(R.raw.grass3_obj, "grass3.ser");
//			objSerializer(R.raw.grass4_obj, "grass4.ser");
//			objSerializer(R.raw.grass5_obj, "grass5.ser");
//			objSerializer(R.raw.grass6_obj, "grass6.ser");
//			objSerializer(R.raw.waterfallsprite_obj, "waterfallsprite.ser");
//			objSerializer(R.raw.fgtrees_obj, "fgtrees.ser");
//			objSerializer(R.raw.fgferns_obj, "fgferns.ser");

			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.skydome));
	    	skydome = new BaseObject3D((SerializedObject3D)ois.readObject());
			skydome.setMaterial(skyMat);
			skydome.setDoubleSided(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.castleb));
	    	castletowers = new BaseObject3D((SerializedObject3D)ois.readObject());
			castletowers.setMaterial(castleBranchDirtMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.castle));
	    	castle = new BaseObject3D((SerializedObject3D)ois.readObject());
			castle.setMaterial(castleBranchDirtMat);
			castle.setBlendingEnabled(true);
			castle.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.ground));
	    	ground = new BaseObject3D((SerializedObject3D)ois.readObject());
			ground.setMaterial(groundMat);
			ground.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.dirt));
	    	dirt = new BaseObject3D((SerializedObject3D)ois.readObject());
			dirt.setMaterial(castleBranchDirtMat);
			dirt.setY(.1f);
			dirt.setBlendingEnabled(true);
			dirt.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows1));
	    	shadows1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows1.setMaterial(pathMat);
			shadows1.setBlendingEnabled(true);
			shadows1.setBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows2));
	    	shadows2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows2.setMaterial(castleBranchDirtMat);
			shadows2.setBlendingEnabled(true);
			shadows2.setBlendFunc(GLES20.GL_DST_COLOR, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.path));
	    	path = new BaseObject3D((SerializedObject3D)ois.readObject());
			path.setMaterial(pathMat);
			path.setBlendingEnabled(true);
			path.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			path.setY(.1f);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.lilys));
	    	lilys = new BaseObject3D((SerializedObject3D)ois.readObject());
			lilys.setMaterial(pathMat);
			lilys.setBlendingEnabled(true);
			lilys.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			lilys.setY(.1f);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers1));
	    	flowers1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers1.setMaterial(pathMat);
			flowers1.setDoubleSided(true);
			flowers1.setBlendingEnabled(true);
			flowers1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers2));
	    	flowers2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers2.setMaterial(pathMat);
			flowers2.setDoubleSided(true);
			flowers2.setBlendingEnabled(true);
			flowers2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers3));
	    	flowers3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers3.setMaterial(pathMat);
			flowers3.setDoubleSided(true);
			flowers3.setBlendingEnabled(true);
			flowers3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers4));
	    	flowers4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers4.setMaterial(pathMat);
			flowers4.setDoubleSided(true);
			flowers4.setBlendingEnabled(true);
			flowers4.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers5));
	    	flowers5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers5.setMaterial(pathMat);
			flowers5.setDoubleSided(true);
			flowers5.setBlendingEnabled(true);
			flowers5.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.flowers6));
	    	flowers6 = new BaseObject3D((SerializedObject3D)ois.readObject());
			flowers6.setMaterial(pathMat);
			flowers6.setDoubleSided(true);
			flowers6.setBlendingEnabled(true);
			flowers6.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.vines1));
	    	vines1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			vines1.setMaterial(pathMat);
			vines1.setDoubleSided(true);
			vines1.setBlendingEnabled(true);
			vines1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.vines2));
	    	vines2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			vines2.setMaterial(pathMat);
			vines2.setDoubleSided(true);
			vines2.setBlendingEnabled(true);
			vines2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.vines3));
	    	vines3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			vines3.setMaterial(pathMat);
			vines3.setDoubleSided(true);
			vines3.setBlendingEnabled(true);
			vines3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.stumpdecal));
	    	stumpdecal = new BaseObject3D((SerializedObject3D)ois.readObject());
			stumpdecal.setMaterial(pathMat);
			stumpdecal.setBlendingEnabled(true);
			stumpdecal.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			stumpdecal.setY(.1f);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.walls));
	    	walls = new BaseObject3D((SerializedObject3D)ois.readObject());
			walls.setMaterial(fernWallMat);
			walls.setDoubleSided(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.gate));
	    	gate = new BaseObject3D((SerializedObject3D)ois.readObject());
			gate.setMaterial(doorGateArchMat);
			gate.setBlendingEnabled(true);
			gate.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.arch));
	    	arch = new BaseObject3D((SerializedObject3D)ois.readObject());
			arch.setMaterial(doorGateArchMat);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.stump));
	    	stump = new BaseObject3D((SerializedObject3D)ois.readObject());
			stump.setMaterial(wallStumpMat);
			stump.addLight(pLight_ground);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.largestump));
	    	largestump = new BaseObject3D((SerializedObject3D)ois.readObject());
			largestump.setMaterial(bigtreeMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.waterfall));
	    	waterfall = new BaseObject3D((SerializedObject3D)ois.readObject());
			waterfall.setMaterial(waterfallrockMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.pot));
	    	pot = new BaseObject3D((SerializedObject3D)ois.readObject());
			pot.setMaterial(potMat);
			pot.setDoubleSided(true);
			pot.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.gold));
	    	gold = new BaseObject3D((SerializedObject3D)ois.readObject());
			gold.setMaterial(goldMat);
			gold.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.rbow1));
	    	rbow1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			rbow1.setMaterial(bowMat);
			rbow1.setDoubleSided(true);
			rbow1.setBlendingEnabled(true);
			rbow1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.rbow2));
	    	rbow2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			rbow2.setMaterial(bowMat);
			rbow2.setDoubleSided(true);
			rbow2.setBlendingEnabled(true);
			rbow2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.tree));
	    	tree = new BaseObject3D((SerializedObject3D)ois.readObject());
			tree.setMaterial(bigtreeMat);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.door));
	    	door = new BaseObject3D((SerializedObject3D)ois.readObject());
			door.setMaterial(doorGateArchMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.rocks));
	    	rocks = new BaseObject3D((SerializedObject3D)ois.readObject());
			rocks.setMaterial(wallStumpMat);
			rocks.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shrooms));
	    	shrooms = new BaseObject3D((SerializedObject3D)ois.readObject());
			shrooms.setMaterial(shroomMat);
			shrooms.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shamrocks));
	    	shamrocks = new BaseObject3D((SerializedObject3D)ois.readObject());
			shamrocks.setMaterial(waterfallrockMat);
			shamrocks.setBlendingEnabled(true);
			shamrocks.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.treeferns));
	    	treeferns = new BaseObject3D((SerializedObject3D)ois.readObject());
			treeferns.setMaterial(fernWallMat);
			treeferns.setDoubleSided(true);
			treeferns.setBlendingEnabled(true);
			treeferns.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.pondferns));
	    	pondferns = new BaseObject3D((SerializedObject3D)ois.readObject());
			pondferns.setMaterial(fernWallMat);
			pondferns.setDoubleSided(true);
			pondferns.setBlendingEnabled(true);
			pondferns.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass1));
	    	grass1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass1.setMaterial(pathMat);
			grass1.setBlendingEnabled(true);
			grass1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass2));
	    	grass2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass2.setMaterial(pathMat);
			grass2.setBlendingEnabled(true);
			grass2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass3));
	    	grass3 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass3.setMaterial(pathMat);
			grass3.setBlendingEnabled(true);
			grass3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass4));
	    	grass4 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass4.setMaterial(pathMat);
			grass4.setBlendingEnabled(true);
			grass4.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass5));
	    	grass5 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass5.setMaterial(pathMat);
			grass5.setBlendingEnabled(true);
			grass5.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.grass6));
	    	grass6 = new BaseObject3D((SerializedObject3D)ois.readObject());
			grass6.setMaterial(pathMat);
			grass6.setBlendingEnabled(true);
			grass6.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgtrees));
	    	fgtrees = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgtrees.setMaterial(treeMat);
			fgtrees.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.fgferns));
	    	fgferns = new BaseObject3D((SerializedObject3D)ois.readObject());
			fgferns.setMaterial(fernWallMat);
			fgferns.setDoubleSided(true);
			fgferns.setBlendingEnabled(true);
			fgferns.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			ois.close();
		} catch (Exception e){
			e.printStackTrace();
		}	
	}

	private void addObjects(){		
		addChild(skydome);			
		addChild(castletowers);			
		addChild(castle);			
		addChild(rbow1);			
		addChild(ground);			
		addChild(dirt);			
		addChild(shadows1);			
		addChild(shadows2);		
		addChild(rocks);

		addWaterTiles();
		
		addChild(lilys);
		addChild(path);			
		addChild(walls);			
		addChild(gate);			
		addChild(arch);			
		addChild(largestump);			
		addChild(stumpdecal);			
		addChild(stump);			
		addChild(tree);
		addChild(shrooms);
		addChild(door);		
		addChild(vines1);			
		addChild(vines2);	
		
		addWaterFall();
		
		addChild(grass1);
		addChild(grass2);
		addChild(grass3);
		addChild(grass4);
		addChild(grass5);
		addChild(grass6);
		addChild(flowers1);
		addChild(flowers2);
		addChild(flowers3);
		addChild(flowers4);
		addChild(flowers5);
		addChild(flowers6);
		addChild(vines3);			
		addChild(shamrocks);
		addChild(treeferns);
		addChild(pondferns);
		addChild(pot);
		addChild(gold);
		addChild(fgtrees);
		addChild(fgferns);
		
		addBranches();

		sceneInit = true;
	}
	
	private void addWaterTiles(){
		float tileWidth  = .25f;
		waterTiles = new BaseObject3D [16];
		
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
			waterTiles[i].setMaterial(new SimpleMaterial());
			waterTiles[i].addTexture(mTextureManager.addTexture(waterTex));
			waterTiles[i].setRotation(0, 90, 90);
			waterTiles[i].setPosition(9.5f, -3.25f, 14.5f);
			waterTiles[i].setDoubleSided(true);
			waterTiles[i].getGeometry().setTextureCoords(waterUVs);
			waterTiles[i].setBlendingEnabled(true);
			waterTiles[i].setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			waterTiles[i].setVisible(false);
			addChild(waterTiles[i]);
    	}
	}
	
	private void addWaterFall(){
		addChild(waterfall);
		BaseObject3D waterfallsprite = new BaseObject3D();
    	try {
			ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.waterfallsprite));
			waterfallsprite = new BaseObject3D((SerializedObject3D)ois.readObject());
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		waterfallTiles = new BaseObject3D [16];
	    waterfallTex = new Bitmap[16];

		for(int i = 0; i <  waterfallTiles.length; i++){
    		int identifier = mContext.getResources().getIdentifier("wf" + (i+1), "drawable", "com.evvid.wallpapers.shamrocklane");
    		waterfallTex[i] = BitmapFactory.decodeResource(mContext.getResources(), identifier);
    		
			waterfallTiles[i] = waterfallsprite.clone();
			waterfallTiles[i].setMaterial(new SimpleMaterial());
			waterfallTiles[i].addTexture(mTextureManager.addTexture(waterfallTex[i]));
			waterfallTiles[i].setDoubleSided(true);
			waterfallTiles[i].setBlendingEnabled(true);
			waterfallTiles[i].setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			waterfallTiles[i].setVisible(false);
			addChild(waterfallTiles[i]);
		}


	}
	
	private void addBranches(){
		branches = new BaseObject3D [15];
		
		for(int i = 0; i < branches.length; i++){
			branches[i] = new Plane(10,10,1,1);
			branches[i].getGeometry().setTextureCoords(new float[] {.5f, 1f, .5f, .5f, 0.01f, 1f, 0.01f, .5f});
			branches[i].setDoubleSided(true);
			branches[i].setBlendingEnabled(true);
			branches[i].setVisible(false);
			branches[i].setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			branches[i].setLookAt(mCamera.getPosition());
			branches[i].setMaterial(new SimpleMaterial());
			branches[i].addTexture(mTextureManager.addTexture(castleTex));
		}
		
		branches[0].setPosition(-13f, 11f, 4f);
		addChild(branches[0]);
		
		branches[1].setPosition(-12.5f, 8.5f, 13f);
		addChild(branches[1]);
		
		branches[2].setPosition(18, 6f, 10);
		addChild(branches[2]);
		
		branches[3].setPosition(11, 6, 16);
		addChild(branches[3]);
		
		addChild(rbow2);			
		
		branches[4].setPosition(-13f, 7f, 18f);
		addChild(branches[4]);
		
		branches[5].setPosition(-7.5f, 8, 22);
		addChild(branches[5]);
		
		branches[6].setPosition(-12f, 7, 30);
		addChild(branches[6]);
		
		branches[7].setPosition(0f, 6, 30);
		addChild(branches[7]);
		
		branches[8].setPosition(-6f, 7.5f, 32);
		addChild(branches[8]);
		
		branches[9].setPosition(-8f, 7, 33);
		addChild(branches[9]);
		
		branches[10].setPosition(-7f, 6, 34);
		addChild(branches[10]);
		
		branches[11].setPosition(4, 5, 35);
		addChild(branches[11]);

		branches[12].setPosition(13.5f, 5, 20);
		addChild(branches[12]);

		branches[13].setPosition(7, 6.5f, 22);
		addChild(branches[13]);

		branches[14].setPosition(3, 5.5f, 25);
		addChild(branches[14]);	
	}
	
	private void setOnPreferenceChange(){
	}
	
	private void setPrefsToLocal(){
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
	}
	
	private void recycleTextures(){
		try{
			skyTex.recycle();
			castleTex.recycle();
			groundTex.recycle();
			pathTex.recycle();
			wallStumpTex.recycle();
			potBowTex.recycle();
			waterfallrockTex.recycle();
			doorGateArchTex.recycle();
			treeTex.recycle();
			shroomTex.recycle();
			waterTex.recycle();
			System.gc();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		try{
			super.onDrawFrame(glUnused);
		}catch (Exception e) {
			e.printStackTrace();
		}
		if(sceneInit){
			if(!branches[0].isVisible())showBranches();
	    	branchMotion();
			if(frameCounter%4 == 0) {
				waterMotion();
			}
			if(moveCamera)cameraMovement();
			cameraControl();
	    	if(frameCounter++ == 64) frameCounter = 0;
			waveCounter++;
		}
	}
	
	private void showBranches(){
		for(int i = 0; i < branches.length; i++){
			branches[i].setVisible(true);
		}
	}
	
	private void branchMotion(){
		for(int i = 0; i < branches.length; i++){
			branches[i].setRotZ((float) (branches[i].getRotZ()+Math.sin(waveCounter)));
		}
	}
	
	private void waterMotion() {
    	waterTiles[tileIndex].setVisible(true);
    	waterfallTiles[tileIndex].setVisible(true);
   	
    	if(tileIndex>0){
    		waterTiles[tileIndex-1].setVisible(false);
			waterfallTiles[tileIndex-1].setVisible(false);
		} else {
    		waterTiles[waterTiles.length-1].setVisible(false);
			waterfallTiles[waterTiles.length-1].setVisible(false);
		}
    	
    	if (tileIndex++ == waterTiles.length-1) 
    		tileIndex = 0;
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
		float xDif = cameraPos[camIndex].x - mCamera.getX();
		float yDif = cameraPos[camIndex].y - mCamera.getY();
		float zDif = cameraPos[camIndex].z - mCamera.getZ();
		
		float newX = mCamera.getX()+(xDif/camSpeed);
		float newY = mCamera.getY()+(yDif/camSpeed);
		float newZ = mCamera.getZ()+(zDif/camSpeed);
		
		float xLookDif = cameraLook[camIndex].x - camLookNull.getX();
		float yLookDif = cameraLook[camIndex].y - camLookNull.getY();
		float zLookDif = cameraLook[camIndex].z - camLookNull.getZ();
		
		float newLookX = camLookNull.getX()+(xLookDif/(camSpeed*2));
		float newLookY = camLookNull.getY()+(yLookDif/(camSpeed*2));
		float newLookZ = camLookNull.getZ()+(zLookDif/(camSpeed*2));
		
		camLookNull.setPosition(newLookX, newLookY, newLookZ);
		mCamera.setPosition(newX, newY, newZ);
		if(moveCameraLook){
			if (Math.abs(xLookDif)<.0001f && Math.abs(yLookDif)<.0001f && Math.abs(zLookDif)<.0001f) {
				camLookNull.setPosition(cameraLook[camIndex]);
				moveCameraLook = false;
			}
		}
		if (moveCamera && Math.abs(xDif)<.001f && Math.abs(yDif)<.001f && Math.abs(zDif)<.001f) {
			mCamera.setPosition(cameraPos[camIndex]);
			moveCamera = false;	
		}
	}

	@Override //This method moves the camera using direct touch events. Tracking a flick and turning it into a procedural animation for smoothing
	public void onTouchEvent(MotionEvent me) {
	       if (me.getAction() == MotionEvent.ACTION_DOWN) {
	           xpos = me.getX();
	       }
	       if (me.getAction() == MotionEvent.ACTION_MOVE) {
	           float xd = xpos - me.getX(0);

	    	   if(me.getPointerCount()==1 && firstTouch) {
		    		   if(xd > 8){
		    			   camIndex--;
		    			   if(camIndex < 0) camIndex = (cameraLook.length - 1);
			    		   firstTouch = false;
		    		   }
		    		   else if (xd < -8){
		    			   camIndex++;
		    			   if(camIndex > (cameraLook.length - 1)) camIndex = 0;		    			   
			    		   firstTouch = false;
		    		   }
			    	   if(camIndex >= (cameraPos.length)) camIndex--;
			    	   else if(camIndex < 0) camIndex++;
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
			
	@SuppressWarnings("unused")
	private void objSerializer(int resourceId, String outputName){ //example Resource ID --> R.raw.myShape_Obj
		ObjParser objParser = new ObjParser(mContext.getResources(), mTextureManager, resourceId);
		objParser.parse();
		BaseObject3D serializer = objParser.getParsedObject();
		MeshExporter exporter = new MeshExporter(serializer);
		exporter.export(outputName, ExportType.SERIALIZED);	
	}
}