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

package com.evvid.wallpapers.aviation3d.helo;


import com.evvid.wallpapers.aviation3d.helo.R;

import rajawali.wallpaper.Wallpaper;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

@SuppressWarnings("deprecation")
public class WallpaperSettings extends PreferenceActivity 
						implements  OnPreferenceChangeListener {
	
	private CheckBoxPreference homeScreenCheck, manualCheck;
	private PreferenceScreen prefSet;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(Wallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.settings);	
		
		prefSet = getPreferenceScreen();
        homeScreenCheck = (CheckBoxPreference) prefSet.findPreference("home_screen_swipe");
        manualCheck = (CheckBoxPreference) prefSet.findPreference("manual_swipe");
        
        homeScreenCheck.setOnPreferenceChangeListener(this);
        manualCheck.setOnPreferenceChangeListener(this);
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy();
		homeScreenCheck.setOnPreferenceChangeListener(null);
        manualCheck.setOnPreferenceChangeListener(null);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = (preference.getKey());
		if(newValue.equals(true))
		{
			if("home_screen_swipe".equals(key))
			{
				manualCheck.setChecked(false);
				homeScreenCheck.setChecked(true);
				return true;
			} 
			else if("manual_swipe".equals(key)) 
			{
				homeScreenCheck.setChecked(false);
				manualCheck.setChecked(true);
				return true;
			} 
			else 
			{
				return false;
			}
		}else 
		{
			return false;
		}
	}
		
}