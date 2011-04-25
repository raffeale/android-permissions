package com.stericson.permissions;

import android.graphics.drawable.Drawable;

public class Permissions_Master {

	public String Permission;
	public String PermissionDescription;
	public String Owner;
	public Drawable icon;
	
	public Permissions_Master(String permission, String permissionDescription, String Owner, Drawable icon) {
		this.Permission = permission;
		this.PermissionDescription = permissionDescription;
		this.Owner = Owner;
		this.icon = icon;
	}
}
