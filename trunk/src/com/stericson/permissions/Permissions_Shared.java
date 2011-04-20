package com.stericson.permissions;

import android.graphics.drawable.Drawable;

public class Permissions_Shared {

	public Drawable icon;
	public String appName;
	public String packageName;
	public String SharedUserID;
	
	public Permissions_Shared(String appName, String packageName, String SharedUserID, Drawable icon) {
		this.appName = appName;
		this.packageName = packageName;
		this.SharedUserID = SharedUserID;
		this.icon = icon;
	}
}
