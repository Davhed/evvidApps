package com.example;


import com.example.R;

import rajawali.wallpaper.Wallpaper;
import android.os.Bundle;
import android.preference.PreferenceActivity;

@SuppressWarnings("deprecation")
public class WallpaperSettings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(Wallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.settings);	
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy();
	}		
}