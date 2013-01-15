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

package com.evvid.wallpapers.aviation3d.lightplane;

import com.evvid.wallpapers.aviation3d.lightplane.R;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class WallpaperPicker extends Activity {
	
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		Intent intent = new Intent();

		if (Build.VERSION.SDK_INT >= 16) {
			/*
			 * Open live wallpaper preview (API Level 16 or greater).
			 */
			intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
			String pkg = WallpaperActivity.class.getPackage().getName();
			String cls = WallpaperActivity.class.getCanonicalName();
			intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
					new ComponentName(pkg, cls));
		} else {
			/*
			 * Open live wallpaper picker (API Level 15 or lower).
			 * 
			 * Display a quick little message (toast) with instructions.
			 */
			intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
			Resources res = getResources();
			String hint = res.getString(R.string.picker_toast_prefix)
					+ res.getString(R.string.lwp_name)
					+ res.getString(R.string.picker_toast_suffix);
			Toast toast = Toast.makeText(this, hint, Toast.LENGTH_LONG);
			toast.show();
		}

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		finish();
	}

	
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {		
		super.onDestroy();
	}

	
}
