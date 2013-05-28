package com.stericson.permissions.donate.domain;

import android.graphics.drawable.Drawable;

public class Permissions_Fix {

	public Drawable icon;
	public String appName;
	public String packageName;
	
	public Permissions_Fix(String appName, String packageName, Drawable icon) {
		this.appName = appName;
		this.packageName = packageName;
		this.icon = icon;
	}
}
