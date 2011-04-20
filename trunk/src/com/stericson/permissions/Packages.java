package com.stericson.permissions;

import android.graphics.drawable.Drawable;

public class Packages {

	public Drawable icon;
	public String appName;
	public String packageName;
	
	public Packages(String appName, String packageName, Drawable icon) {
		this.appName = appName;
		this.packageName = packageName;
		this.icon = icon;
	}
}
