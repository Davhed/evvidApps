/*
 * 
 * Aviation 3D relies heavily on the Rajawali framework which can be found here:
 * 
 * https://github.com/MasDennis/Rajawali
 * 		
 * 		Rajawali --
 * 		Copyright 2011 Dennis Ippel
 * 		Licensed under the Apache License, Version 2.0 (the "License");
 * 		you may not use this file except in compliance with the License.
 * 		You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */


package com.example;


import android.content.Context;
import rajawali.wallpaper.Wallpaper;


public class WallpaperActivity extends Wallpaper {

	private WallpaperRenderer mRenderer;
	
	public Engine onCreateEngine() {
		mRenderer = new WallpaperRenderer(this);
		return new WallpaperEngine(this.getSharedPreferences(SHARED_PREFS_NAME,	Context.MODE_PRIVATE), getBaseContext(), mRenderer, true);
	}
		
	@Override
     public void onDestroy() {
        super.onDestroy();
        mRenderer.onSurfaceDestroyed();
     }
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

}

