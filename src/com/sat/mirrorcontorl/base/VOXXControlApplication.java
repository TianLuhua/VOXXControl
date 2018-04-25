package com.sat.mirrorcontorl.base;

import com.sat.mirrorcontorl.utils.LogUtils;

import android.app.Application;

public class VOXXControlApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// debug ¿ª¹Ø
		LogUtils.isDebug = true;
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}
}
