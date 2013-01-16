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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.evvid.wallpapers.shamrocklane.R;

import rajawali.BaseObject3D;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import rajawali.materials.SimpleMaterial;
//Serializer//
import rajawali.parser.ObjParser;
import rajawali.primitives.Plane;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;


public class WallpaperRenderer extends RajawaliRenderer{
			
	private OnSharedPreferenceChangeListener mListener;

	private float xpos, ypos;
	
	private BaseObject3D camLookNull, skydome, castle, ground, path, walls, gate, arch, stump, stumpdecal,
	waterfall, pot, gold, rbow1, rbow2, tree, door, rocks, shrooms, shamrocks, fern, fgrass1, fgrass2,
	grass1, grass2, grass3, grass4, grass5, grass6, water1, water2;
	
	private float[] waterUVs;

	private int frameCounter = 0, tileIndex = 0;

	private Boolean sceneInit = false;
	
	private Bitmap skyTex, castleTex, groundTex, pathTex, wallStumpTex, potBowTex, waterfallrockTex,
	doorGateArchTex, treeTex, shroomTex, waterTex;
	
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
		mCamera.setPosition(0, 0, 20);
		
		camLookNull = new BaseObject3D();
	}
	
	private void loadTextures(){
		skyTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skydome);
		castleTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.castle);
		groundTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.grass2);
		pathTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.path);
		wallStumpTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wall_stump);
		potBowTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pot);
		waterfallrockTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterfallrock_tex);
		doorGateArchTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.door_gate_arch);
		treeTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btree);
		shroomTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mushroom);
		waterTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wateratlas);

		waterUVs   = new float[] {   0f, .25f, .25f, .25f,   0f,   0f, .25f,   0f };
	}
	
	private void loadObjects(){		
		try {	
			//SCENERY//
			SimpleMaterial skyMat = new SimpleMaterial();
			SimpleMaterial castleMat = new SimpleMaterial();
			SimpleMaterial groundMat = new SimpleMaterial();
			SimpleMaterial pathMat = new SimpleMaterial();
			SimpleMaterial wallStumpMat = new SimpleMaterial();
			SimpleMaterial potBowMat = new SimpleMaterial();
			SimpleMaterial waterfallrockMat = new SimpleMaterial();
			SimpleMaterial doorGateArchMat = new SimpleMaterial();
			SimpleMaterial treeMat = new SimpleMaterial();
			SimpleMaterial shroomMat = new SimpleMaterial();

			skyMat.addTexture(mTextureManager.addTexture(skyTex));
			castleMat.addTexture(mTextureManager.addTexture(castleTex));
			groundMat.addTexture(mTextureManager.addTexture(groundTex));
			pathMat.addTexture(mTextureManager.addTexture(pathTex));
			wallStumpMat.addTexture(mTextureManager.addTexture(wallStumpTex));
			potBowMat.addTexture(mTextureManager.addTexture(potBowTex));
			waterfallrockMat.addTexture(mTextureManager.addTexture(waterfallrockTex));
			doorGateArchMat.addTexture(mTextureManager.addTexture(doorGateArchTex));
			treeMat.addTexture(mTextureManager.addTexture(treeTex));
			shroomMat.addTexture(mTextureManager.addTexture(shroomTex));
			
			
			ObjParser objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.skydome_obj);
			objParser.parse();
			skydome = objParser.getParsedObject();
			skydome.setMaterial(skyMat);
			skydome.setDoubleSided(true);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.castle_obj);
			objParser.parse();
			castle = objParser.getParsedObject();
			castle.setMaterial(castleMat);
			castle.setBlendingEnabled(true);
			castle.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.ground_obj);
			objParser.parse();
			ground = objParser.getParsedObject();
			ground.setMaterial(groundMat);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.path_obj);
			objParser.parse();
			path = objParser.getParsedObject();
			path.setMaterial(pathMat);
			path.setBlendingEnabled(true);
			path.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			path.setY(.1f);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.stumpdecal_obj);
			objParser.parse();
			stumpdecal = objParser.getParsedObject();
			stumpdecal.setMaterial(pathMat);
			stumpdecal.setBlendingEnabled(true);
			stumpdecal.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			stumpdecal.setY(.1f);
			
			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.walls_obj);
			objParser.parse();
			walls = objParser.getParsedObject();
			walls.setMaterial(wallStumpMat);
			walls.setDoubleSided(true);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.gate_obj);
			objParser.parse();
			gate = objParser.getParsedObject();
			gate.setMaterial(doorGateArchMat);
			gate.setBlendingEnabled(true);
			gate.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.arch_obj);
			objParser.parse();
			arch = objParser.getParsedObject();
			arch.setMaterial(doorGateArchMat);
			
			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.stump_obj);
			objParser.parse();
			stump = objParser.getParsedObject();
			stump.setMaterial(wallStumpMat);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.waterfall_obj);
			objParser.parse();
			waterfall = objParser.getParsedObject();
			waterfall.setMaterial(waterfallrockMat);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.pot_obj);
			objParser.parse();
			pot = objParser.getParsedObject();
			pot.setMaterial(potBowMat);
			pot.setDoubleSided(true);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.gold_obj);
			objParser.parse();
			gold = objParser.getParsedObject();
			gold.setMaterial(waterfallrockMat);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.rbow1_obj);
			objParser.parse();
			rbow1 = objParser.getParsedObject();
			rbow1.setMaterial(potBowMat);
			rbow1.setDoubleSided(true);
			rbow1.setBlendingEnabled(true);
			rbow1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.rbow2_obj);
			objParser.parse();
			rbow2 = objParser.getParsedObject();
			rbow2.setMaterial(potBowMat);
			rbow2.setDoubleSided(true);
			rbow2.setBlendingEnabled(true);
			rbow2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.tree_obj);
			objParser.parse();
			tree = objParser.getParsedObject();
			tree.setMaterial(treeMat);
			
			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.door_obj);
			objParser.parse();
			door = objParser.getParsedObject();
			door.setMaterial(doorGateArchMat);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.rocks_obj);
			objParser.parse();
			rocks = objParser.getParsedObject();
			rocks.setMaterial(wallStumpMat);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.shrooms_obj);
			objParser.parse();
			shrooms = objParser.getParsedObject();
			shrooms.setMaterial(shroomMat);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.shamrocks_obj);
			objParser.parse();
			shamrocks = objParser.getParsedObject();
			shamrocks.setMaterial(waterfallrockMat);
			shamrocks.setBlendingEnabled(true);
			shamrocks.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.fern_obj);
			objParser.parse();
			fern = objParser.getParsedObject();
			fern.setMaterial(wallStumpMat);
			fern.setBlendingEnabled(true);
			fern.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.grass1_obj);
			objParser.parse();
			grass1 = objParser.getParsedObject();
			grass1.setMaterial(pathMat);
			grass1.setBlendingEnabled(true);
			grass1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.grass2_obj);
			objParser.parse();
			grass2 = objParser.getParsedObject();
			grass2.setMaterial(pathMat);
			grass2.setBlendingEnabled(true);
			grass2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.grass3_obj);
			objParser.parse();
			grass3 = objParser.getParsedObject();
			grass3.setMaterial(pathMat);
			grass3.setBlendingEnabled(true);
			grass3.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.grass4_obj);
			objParser.parse();
			grass4 = objParser.getParsedObject();
			grass4.setMaterial(pathMat);
			grass4.setBlendingEnabled(true);
			grass4.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.grass5_obj);
			objParser.parse();
			grass5 = objParser.getParsedObject();
			grass5.setMaterial(pathMat);
			grass5.setBlendingEnabled(true);
			grass5.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.grass6_obj);
			objParser.parse();
			grass6 = objParser.getParsedObject();
			grass6.setMaterial(pathMat);
			grass6.setBlendingEnabled(true);
			grass6.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.fgrass1_obj);
			objParser.parse();
			fgrass1 = objParser.getParsedObject();
			fgrass1.setMaterial(pathMat);
			fgrass1.setBlendingEnabled(true);
			fgrass1.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.fgrass2_obj);
			objParser.parse();
			fgrass2 = objParser.getParsedObject();
			fgrass2.setMaterial(pathMat);
			fgrass2.setBlendingEnabled(true);
			fgrass2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			
			water1 = new Plane(20,20,1,1);
			water1.setMaterial(new SimpleMaterial());
			water1.addTexture(mTextureManager.addTexture(waterTex));
			water1.setDoubleSided(true);
			water1.setRotation(0, 90, 90);
			water1.setPosition(9.5f, -3.25f, 14.5f);
			
			water2 = new Plane(20,20,1,1);
			water2.setMaterial(new SimpleMaterial());
			water2.addTexture(mTextureManager.addTexture(waterTex));
			water2.setDoubleSided(true);
			water2.setRotation(0, 90, 90);
			water2.setPosition(water1.getX()+.5f, water1.getY()+.01f, water1.getZ()-.5f);
			water2.setBlendingEnabled(true);
			water2.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_COLOR);
		} catch (Exception e){
			e.printStackTrace();
		}	
	}
	
	private void addObjects(){		
		addChild(skydome);			
		addChild(castle);			
		addChild(rbow1);			
		addChild(ground);			
	    addChild(water1);
	    addChild(water2);
		addChild(path);			
		addChild(walls);			
		addChild(gate);			
		addChild(arch);			
		addChild(stumpdecal);			
		addChild(stump);			
		addChild(tree);			
		addChild(door);			
		addChild(waterfall);
		addChild(rocks);
		addChild(grass1);
		addChild(grass2);
		addChild(grass3);
		addChild(grass4);
		addChild(grass5);
		addChild(grass6);
		addChild(fgrass1);
		addChild(shrooms);
		addChild(shamrocks);
		addChild(fern);
		addChild(fgrass2);
		addChild(pot);
		addChild(gold);
		addChild(rbow2);			
	    
	    sceneInit = true;
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
			if(frameCounter%4 == 0)waterMotion();
			cameraControl();
			frameCounter++;
		}
	}
	
	private void waterMotion() {
    	if(tileIndex < 16){
    		if(tileIndex%4 == 0) {
	    		waterUVs = new float[] {   0f, (.25f+((tileIndex/4)*.25f)), .25f, (.25f+((tileIndex/4)*.25f)),   0f,   (0f+((tileIndex/4)*.25f)), .25f,   (0f+((tileIndex/4)*.25f)) };
	    	}
	    	else{
	    		for(int i = 0; i < waterUVs.length; i++) {
					if ( i%2 == 0){
						waterUVs[i]+=.25f;
						if (waterUVs[i] > 1) waterUVs[i]= 0;
					}
				}
	    	}
    	}else tileIndex = -1;
		water1.getGeometry().setTextureCoords(waterUVs);
		water2.getGeometry().setTextureCoords(waterUVs);
		water1.reload();
		water2.reload();
    	tileIndex++;
	}
	
	private void cameraControl(){
		mCamera.setLookAt(camLookNull.getPosition());		
	}

	private void cameraTrack(float xOffset, float yOffset, int pointerCount) {
        xOffset = xOffset/10f;
		yOffset = yOffset/10f;
		
    	if(pointerCount > 1) {
    		camLookNull.setX((camLookNull.getX() + xOffset/2));
    		if(pointerCount == 2) camLookNull.setY((camLookNull.getY() + yOffset/2));
    		else mCamera.setZ((mCamera.getZ() + yOffset/2));
    	}
    	else {
    		mCamera.setX((mCamera.getX() + xOffset/2));
    		mCamera.setY((mCamera.getY() + yOffset/2));
    	}
	}

	@Override //This method moves the camera using direct touch events. Tracking a flick and turning it into a procedural animation for smoothing
	public void onTouchEvent(MotionEvent me) {
	       if (me.getAction() == MotionEvent.ACTION_DOWN) {
	           xpos = me.getX();
	           ypos = me.getY();
	    		   		
	       }
	       if (me.getAction() == MotionEvent.ACTION_MOVE) {
	           float xd = xpos - me.getX(0);
	           float yd = ypos - me.getY(0);
	           int pointerCount = me.getPointerCount();
	           cameraTrack(xd, yd, pointerCount);
	    	   xpos = me.getX(0);
	           ypos = me.getY(0);
          
	       }
	       if (me.getAction() == MotionEvent.ACTION_UP) {
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