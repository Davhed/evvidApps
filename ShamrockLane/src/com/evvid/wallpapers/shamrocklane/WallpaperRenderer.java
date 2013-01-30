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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

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
import rajawali.materials.BumpmapMaterial;
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
	
	private int frameCounter = 0, flapCounter = 0, tileIndex = 0, camSpeed = 10, camIndex, totalCount = 0, scene = 0;
	
	private Boolean sceneInit = false, moveCamera = false, moveCameraLook = false, firstTouch = true, birdDone = true;
	
	private BaseObject3D camLookNull, skydome, castle, castletowers, ground, path, walls, gate, arch, stump, largestump, stumpdecal, lilys,
	waterfall, pot, gold, rbow1, rbow2, tree, door, rocks, shrooms, shamrocks, treeferns, pondferns, fgtrees, fgferns, vines1, vines2, vines3,
	grass1, grass2, grass3, grass4, grass5, grass6, flowers1, flowers2, flowers3, flowers4, flowers5, flowers6, dirt, shadows1, shadows2, bird, fairies;
	private BaseObject3D[] waterTiles, waterfallTiles, splashTiles, splash2Tiles, splash3Tiles, branches;

	private Bitmap  pathTex, wallStumpTex, potBowTex, waterfallrockTex,
	doorGateArchTex, waterTex, splashTex;
	private Bitmap[] waterfallTex, birdTex;
	
	DiffuseMaterial castleBranchDirtMat;
	
	TextureInfo castleBranchDirtInfo;

	private PointLight pLight_ground, pLight_ground2, pLight_pot;

	private Number3D [] cameraLook, cameraPos;

	private ObjectInputStream ois;
	private InputStream is;
	private ByteBuffer buffer;
	
	public WallpaperRenderer(Context context) {
		super(context);
		setFrameRate(60);
		setBackgroundColor(0x2b426e);
    }
		
	public void initScene() {
		setOnPreferenceChange();
		setPrefsToLocal();
		setScene();
	}
	
	///////////////
	//Scene Config
	///////////////
	private void setOnPreferenceChange(){
	}
	
	private void setPrefsToLocal(){
	}

	private void setScene(){
		if(scene == 0)
			loadExterior();
		else if (scene == 1)
			loadInterior();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		super.onSurfaceCreated(gl, config);
		PreferenceManager.setDefaultValues(mContext, R.xml.settings, true);
		preferences.registerOnSharedPreferenceChangeListener(mListener);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) { // Check for screen rotation
		super.onSurfaceChanged(gl, width, height);
	}

	@Override
	public void onSurfaceDestroyed() {
		super.onSurfaceDestroyed();
		preferences.unregisterOnSharedPreferenceChangeListener(mListener);
		if(scene == 0){
//			castleTex.recycle();
			pathTex.recycle();
			wallStumpTex.recycle();
			potBowTex.recycle();
			waterfallrockTex.recycle();
			doorGateArchTex.recycle();
			waterTex.recycle();
			splashTex.recycle();
		}else if (scene == 1){
			
		}
		System.gc();
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		if(sceneInit){
			cameraControl();
			cameraMovement();
//			branches[0].setLookAt(mCamera.getX(), mCamera.getY(), mCamera.getZ()-5);
			if(scene == 0){
				waterMovement();
				birdMovement();
				fairyMovement();
			}else if (scene == 1){
				
			}
		}
	}

	@Override
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
		if(moveCamera){
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
		mCamera.setPosition(mCamera.getX()+(float) (Math.sin(totalCount/40)/400), mCamera.getY()+(float) (Math.cos(totalCount/80)/800), mCamera.getZ());
		totalCount++;
	}
	
	private void loadExterior(){
		mCamera.setFarPlane(2000);
		camLookNull = new BaseObject3D();
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
		
		mCamera.setPosition(cameraPos[0]);
		mCamera.setLookAt(cameraLook[0]);
		
		pLight_ground = new PointLight();
		pLight_ground.setPosition(6.5f, 7, 15f);
		pLight_ground.setPower(2f);
		pLight_ground.setColor(0xffff00);
		pLight_ground.setAttenuation(50, 1, 0, 0);
		
		pLight_ground2 = new PointLight();
		pLight_ground2.setPosition(50f, 15, -100f);
		pLight_ground2.setPower(2f);
		pLight_ground2.setAttenuation(50, 1, 0, 0);
		
		pLight_pot = new PointLight();
		pLight_pot.setPosition(-10f, 15, 19f);
		pLight_pot.setPower(20f);
		
		
		///////////////
		//Load Textures
		///////////////
		castleBranchDirtInfo = null;
		castleBranchDirtMat = new DiffuseMaterial();

			pathTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.path);
			wallStumpTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wall_stump);
			potBowTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pot);
			waterfallrockTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterfallrock_tex);
			doorGateArchTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.door_gate_arch);
			waterTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wateratlas);
			splashTex = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.splashatlas);
			
		    birdTex = new Bitmap[4];
			for(int i = 0; i <  birdTex.length; i++){
	    		int id = mContext.getResources().getIdentifier("bird_0" + (i), "drawable", "com.evvid.wallpapers.shamrocklane");
	    		birdTex[i] = BitmapFactory.decodeResource(mContext.getResources(), id);
			}
		    waterfallTex = new Bitmap[16];
			for(int i = 0; i <  waterfallTex.length; i++){
	    		int id = mContext.getResources().getIdentifier(i < 10 ? "wf0" + (i) : "wf" + (i), "drawable", "com.evvid.wallpapers.shamrocklane");
	    		waterfallTex[i] = BitmapFactory.decodeResource(mContext.getResources(), id);
			}
			
			castleBranchDirtInfo = mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.castle_branch_dirt_diff), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.castle_branch_dirt), TextureType.DIFFUSE);
			castleBranchDirtMat.addTexture(castleBranchDirtInfo);
//			castleBranchDirtMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.castle_branch_dirt_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.castle_branch_dirt), TextureType.ALPHA));
		
//		}
//		else if("ATC".equals(this.mCompressionType)){
//			int texWidth = 1024, texHeight = 1024, headerByteCount = 128;
//			byte[] bytes = new byte[((texWidth+3)/4) * ((texHeight+3)/4) * 16 + headerByteCount];
//			is = mContext.getResources().openRawResource(R.raw.castle_branch_dirt);
//			try {is.read(bytes);} catch (IOException e) {e.printStackTrace();}
//			buffer = ByteBuffer.wrap(bytes, headerByteCount, bytes.length-headerByteCount);
//			buffer.position(0);
//
//			castleBranchDirtInfo = mTextureManager.addTexture(mTextureManager.addAtcTexture(buffer, 1024, 1024, TextureType.DIFFUSE, TextureManager.AtcFormat.RGBA_INTERPOLATED));
//
//		}
		

		///////////////
		//Create Materials
		///////////////
		
		SimpleMaterial skyMat = new SimpleMaterial();
		SimpleMaterial pathMat = new SimpleMaterial();
		SimpleMaterial fernWallMat = new SimpleMaterial();
		SimpleMaterial bowMat = new SimpleMaterial();
		SimpleMaterial doorGateArchMat = new SimpleMaterial();

		BumpmapMaterial waterfallrockMat = new BumpmapMaterial();
		
		PhongMaterial goldMat = new PhongMaterial();
		goldMat.setShininess(92.3f);

		PhongMaterial potMat = new PhongMaterial();
		potMat.setShininess(92.3f);

		PhongMaterial treeMat = new PhongMaterial();
		treeMat.setShininess(100);
		
		PhongMaterial groundMat = new PhongMaterial();
		groundMat.setShininess(100);
		
		PhongMaterial wallStumpMat = new PhongMaterial();
		wallStumpMat.setShininess(80);

		PhongMaterial shroomMat = new PhongMaterial();
		shroomMat.setShininess(70);

		BumpmapMaterial bigtreeMat = new BumpmapMaterial();


//		castleBranchDirtMat.addTexture(castleBranchDirtInfo);
		pathMat.addTexture(mTextureManager.addTexture(pathTex));
		fernWallMat.addTexture(mTextureManager.addTexture(wallStumpTex));
		wallStumpMat.addTexture(mTextureManager.addTexture(wallStumpTex));
		potMat.addTexture(mTextureManager.addTexture(potBowTex));
		bowMat.addTexture(mTextureManager.addTexture(potBowTex));
		waterfallrockMat.addTexture(mTextureManager.addTexture(waterfallrockTex));
		waterfallrockMat.addTexture(mTextureManager.addTexture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.waterfallrock_tex_norm), TextureType.BUMP));
		goldMat.addTexture(mTextureManager.addTexture(waterfallrockTex));
		doorGateArchMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.door_gate_arch), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.door_gate_arch), TextureType.DIFFUSE));
//		doorGateArchMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.door_gate_arch_alpha), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.door_gate_arch), TextureType.ALPHA));
		bigtreeMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.btree), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btree), TextureType.DIFFUSE));
		bigtreeMat.addTexture(mTextureManager.addTexture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btree_norm), TextureType.BUMP));
		groundMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.grass), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.grass)));
		skyMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.skydome_diff), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skydome)));
		treeMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.btree), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btree)));
		shroomMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.mushroom), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.mushroom)));
		
		///////////////
		// Create Objects
		///////////////
		try {	
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.skydome));
	    	skydome = new BaseObject3D((SerializedObject3D)ois.readObject());
			skydome.setMaterial(skyMat);
			skydome.setY(20);
			skydome.setRotY(5);
			skydome.setDoubleSided(true);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.castleb));
	    	castletowers = new BaseObject3D((SerializedObject3D)ois.readObject());
			castletowers.setMaterial(castleBranchDirtMat);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.castle));
	    	castle = new BaseObject3D((SerializedObject3D)ois.readObject());
			castle.setMaterial(castleBranchDirtMat);
			castle.setBlendingEnabled(true);
			castle.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.ground));
	    	ground = new BaseObject3D((SerializedObject3D)ois.readObject());
			ground.setMaterial(groundMat);
			ground.addLight(pLight_ground);
			ground.addLight(pLight_ground2);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.dirt));
	    	dirt = new BaseObject3D((SerializedObject3D)ois.readObject());
			dirt.setMaterial(castleBranchDirtMat);
			dirt.setY(.1f);
			dirt.setBlendingEnabled(true);
			dirt.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows1));
	    	shadows1 = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows1.setMaterial(pathMat);
			shadows1.setBlendingEnabled(true);
			shadows1.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.shadows2));
	    	shadows2 = new BaseObject3D((SerializedObject3D)ois.readObject());
			shadows2.setMaterial(castleBranchDirtMat);
			shadows2.setBlendingEnabled(true);
			shadows2.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

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
			largestump.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.waterfall));
	    	waterfall = new BaseObject3D((SerializedObject3D)ois.readObject());
			waterfall.setMaterial(waterfallrockMat);
			waterfall.addLight(pLight_ground);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.pot));
	    	pot = new BaseObject3D((SerializedObject3D)ois.readObject());
			pot.setMaterial(potMat);
			pot.setDoubleSided(true);
			pot.addLight(pLight_pot);

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.gold));
	    	gold = new BaseObject3D((SerializedObject3D)ois.readObject());
			gold.setMaterial(waterfallrockMat);
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
			tree.addLight(pLight_ground);
			
	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.door));
	    	door = new BaseObject3D((SerializedObject3D)ois.readObject());
			door.setMaterial(doorGateArchMat);
			door.addLight(pLight_ground);

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
			shamrocks.setMaterial(goldMat);
			shamrocks.setBlendingEnabled(true);
			shamrocks.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
			shamrocks.addLight(pLight_ground);

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
		/////////////
		//Add Objects
		/////////////
		addChild(skydome);
		addChild(castletowers);
		addChild(castle);
		addChild(rbow1);
		addChild(ground);

		addBird();

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

		addWaterFall();


		addChild(shamrocks);
		addChild(treeferns);
		addChild(pondferns);
		addChild(pot);		
		addChild(gold);
		addFairies();
		addChild(fgtrees);
		addChild(fgferns);
		
		addBranches();
		sceneInit = true;
	}

	private void addBird(){
		bird = new BaseObject3D();
		
		for(int i = 0; i <  birdTex.length; i++){
			Plane birdFrame = new Plane(1,1,1,1,1);		
			birdFrame.setMaterial(new SimpleMaterial());
			birdFrame.addTexture(mTextureManager.addTexture(birdTex[i]));
			birdFrame.setDoubleSided(true);
			birdFrame.setBlendingEnabled(true);
			birdFrame.setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			birdFrame.setRotZ(-90);
			bird.addChild(birdFrame);
		}
		
		bird.setPosition(-50, 8, -7);
		addChild(bird);
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
		addChild(waterfall);//Add rocky background geometry
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
			waterfallTiles[i].addTexture(mTextureManager.addTexture(waterfallTex[i]));
			waterfallTiles[i].setDoubleSided(true);
			waterfallTiles[i].setBlendingEnabled(true);
			waterfallTiles[i].setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			waterfallTiles[i].setVisible(false);
			addChild(waterfallTiles[i]);
		}
		
		int numRows = 8;
		float tileWidth  = .125f;
		float[] splashUVs = new float[32];
		
		splashTiles = new BaseObject3D [32];
		splash2Tiles = new BaseObject3D [32];
		splash3Tiles = new BaseObject3D [32];
		
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
			splashTiles[i].setMaterial(new SimpleMaterial());
			splashTiles[i].addTexture(mTextureManager.addTexture(splashTex));
			splashTiles[i].setRotation(-90, -95, -10);
			splashTiles[i].setPosition(16f, 0.5f, 11.7f);
			splashTiles[i].setDoubleSided(true);
			splashTiles[i].getGeometry().setTextureCoords(splashUVs);
			splashTiles[i].setBlendingEnabled(true);
			splashTiles[i].setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			splashTiles[i].setVisible(false);
			addChild(splashTiles[i]);

			splash2Tiles[i] = new Plane(5,5,1,1);
			splash2Tiles[i].setMaterial(new SimpleMaterial());
			splash2Tiles[i].addTexture(mTextureManager.addTexture(splashTex));
			splash2Tiles[i].setRotation(-90, -90, -25);
			splash2Tiles[i].setPosition(15.3f, -2f, 14f);
			splash2Tiles[i].setDoubleSided(true);
			splash2Tiles[i].getGeometry().setTextureCoords(splashUVs);
			splash2Tiles[i].setBlendingEnabled(true);
			splash2Tiles[i].setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			splash2Tiles[i].setVisible(false);
			addChild(splash2Tiles[i]);

			splash3Tiles[i] = new Plane(5,5,1,1);
			splash3Tiles[i].setMaterial(new SimpleMaterial());
			splash3Tiles[i].addTexture(mTextureManager.addTexture(splashTex));
			splash3Tiles[i].setRotation(-90, -90, 25);
			splash3Tiles[i].setPosition(15f, -2.8f, 9.5f);
			splash3Tiles[i].setDoubleSided(true);
			splash3Tiles[i].getGeometry().setTextureCoords(splashUVs);
			splash3Tiles[i].setBlendingEnabled(true);
			splash3Tiles[i].setBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			splash3Tiles[i].setVisible(false);
			addChild(splash3Tiles[i]);
		}
	}
	
	private void addBranches(){
		branches = new BaseObject3D [15];
		float[] uvCoords =  new float[] {.5f, 1f, .5f, .5f, 0.01f, 1f, 0.01f, .5f};

		for(int i = 0; i < branches.length; i++){
			branches[i] = new TexturedPlane(10, 10, 1, 1, 1, uvCoords);
			branches[i].setDoubleSided(true);
			branches[i].setBlendingEnabled(true);
			branches[i].addLight(pLight_ground);
			branches[i].setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
			if(i>0)branches[i].setLookAt(mCamera.getPosition());
			branches[i].setMaterial(castleBranchDirtMat);
//			branches[i].addTexture(castleBranchDirtInfo);
		}

		branches[0].setPosition(-13f, 6.5f, 7f);
		branches[0].setRotY(-90);
		addChild(branches[0]);

		branches[1].setPosition(-12.5f, 5f, 13f);
		branches[1].setRotY(90);
		addChild(branches[1]);

		branches[2].setPosition(13, 6f, 10);
		addChild(branches[2]);

		branches[3].setPosition(11, 6, 16);
		addChild(branches[3]);

		addChild(rbow2);			

		branches[4].setPosition(13.5f, 5, 20);
		addChild(branches[4]);

		branches[5].setPosition(7, 6.5f, 22);
		addChild(branches[5]);

		branches[6].setPosition(3, 5.5f, 25);
		addChild(branches[6]);	
		
		branches[7].setPosition(-13f, 7f, 18f);
		addChild(branches[7]);
		
		branches[8].setPosition(-7.5f, 8, 22);
		addChild(branches[8]);
		
		branches[9].setPosition(-12f, 7, 30);
		addChild(branches[9]);
		
		branches[10].setPosition(0f, 6, 30);
		addChild(branches[10]);
		
		branches[11].setPosition(-6f, 7.5f, 32);
		addChild(branches[11]);
		
		branches[12].setPosition(-8f, 7, 33);
		addChild(branches[12]);
		
		branches[13].setPosition(-7f, 6, 34);
		addChild(branches[13]);
		
		branches[14].setPosition(4, 5, 35);
		addChild(branches[14]);
	}
	
	private void waterMovement() {
		if(frameCounter%4 == 0) {
	    	waterTiles[tileIndex].setVisible(true);
	    	waterfallTiles[tileIndex].setVisible(true);
	    	splashTiles[tileIndex].setVisible(true);
	    	splash2Tiles[tileIndex].setVisible(true);
	    	splash3Tiles[tileIndex].setVisible(true);
	   	
	    	if(tileIndex>0){
	    		waterTiles[tileIndex-1].setVisible(false);
				waterfallTiles[tileIndex-1].setVisible(false);
		    	splashTiles[tileIndex-1].setVisible(false);
		    	splash2Tiles[tileIndex-1].setVisible(false);
		    	splash3Tiles[tileIndex-1].setVisible(false);
			} else {
	    		waterTiles[waterTiles.length-1].setVisible(false);
				waterfallTiles[waterTiles.length-1].setVisible(false);
		    	splashTiles[splashTiles.length-1].setVisible(false);
		    	splash2Tiles[splashTiles.length-1].setVisible(false);
		    	splash3Tiles[splashTiles.length-1].setVisible(false);
			}
	    	
	    	if (tileIndex++ == waterTiles.length-1) 
	    		tileIndex = 0;
		}
    	if(frameCounter++ == 64) frameCounter = 0;
	}

	private void addFairies(){
		fairies = new BaseObject3D();
		fairies.setPosition(-6, -.25f, 19.5f);
		int numChildren = 8;

		SimpleMaterial fairyMat = new SimpleMaterial();
		fairyMat.addTexture(mTextureManager.addTexture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fairy_tex)));
		
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
		addChild(fairies);
	}

	private void birdMovement(){
		if(Math.random() > .75f && totalCount % 300 == 0 ) birdDone = false;
		
		if(frameCounter%2 == 0 && !birdDone){
			bird.getChildAt(flapCounter).setVisible(true);
			if(flapCounter > 0)
				bird.getChildAt(flapCounter-1).setVisible(false);
			else
				bird.getChildAt(bird.getNumChildren()-1).setVisible(false);
			
			if(flapCounter++ == 3) flapCounter = 0;
		}
		
		if(bird.getX() > 50 && !birdDone){
			bird.setX(-50);
			birdDone = true;
		}else if(!birdDone)
			bird.setX(bird.getX()+.5f);
		
		bird.setLookAt(mCamera.getPosition());
	}
	
	private void fairyMovement(){
		fairies.setRotation(fairies.getRotation().add(0, .1f, 0));
		Number3D scaleOffset = new Number3D(Math.sin(totalCount),Math.sin(totalCount),Math.sin(totalCount));
		
		for(int i = 0; i < fairies.getNumChildren(); ++i){
			BaseObject3D objPointer = fairies.getChildAt(i);
			objPointer.setPosition(
					(float) (objPointer.getX()+(Math.sin(totalCount*.05)*(Math.random()*(.01*i)))), 
					(float) (objPointer.getY()+(Math.cos(totalCount*.05)*(Math.random()*(.01*i)))), 
					(float) (objPointer.getZ()+(Math.sin(totalCount*.05)*(Math.random()*(.01*i))))
					);
			objPointer.setRotation((objPointer.getRotation().add((float)(Math.random()*5),(float)(Math.random()*3),(float)(Math.random()*5))));
			
			objPointer.setScale(objPointer.getScale().add(scaleOffset));
		}
	}
		
	private void loadInterior(){
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
		pLight_ground.setPosition(6.5f, 15, 15f);
		pLight_ground.setPower(2f);
		pLight_ground.setAttenuation(50, 1, 0, 0);	
		
		///////////////
		//Load Textures
		///////////////

		
		///////////////
		// Create Materials and Objects
		///////////////
		try {	
			SimpleMaterial skyMat = new SimpleMaterial();

			skyMat.addTexture(mTextureManager.addEtc1Texture(mContext.getResources().openRawResource(R.raw.skydome_diff), BitmapFactory.decodeResource(mContext.getResources(), R.drawable.skydome)));

	    	ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.skydome));
	    	skydome = new BaseObject3D((SerializedObject3D)ois.readObject());
			skydome.setMaterial(skyMat);
			skydome.setY(20);
			skydome.setRotY(5);
			skydome.setDoubleSided(true);


			ois.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		/////////////
		//Add Objects
		/////////////
		addChild(skydome);

		sceneInit = true;
	}
	

}