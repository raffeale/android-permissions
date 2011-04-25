package com.stericson.permissions;

import android.graphics.drawable.Drawable;

public class Packages {

	public Drawable icon;
	public String appName;
	public String packageName;
	public int permissionCount;
	public int activeCount;
	public int deniedCount;
	
	public Packages(String appName, String packageName, Drawable icon, int permissionCount, int activeCount, int deniedCount) {
		this.appName = appName;
		this.packageName = packageName;
		this.icon = icon;
		this.permissionCount = permissionCount;
		this.activeCount = activeCount;
		this.deniedCount = deniedCount;
	}
}
