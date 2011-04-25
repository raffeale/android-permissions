package com.stericson.permissions;

import android.graphics.drawable.Drawable;

public class Permissions_Specific {

	public String Permission;
	public String PermissionDescription;
	public String Owner;
	public Drawable icon;
	public boolean Active;
	
	public Permissions_Specific(String permission, String permissionDescription, String Owner, Drawable icon, boolean active) {
		this.Permission = permission;
		this.PermissionDescription = permissionDescription;
		this.Owner = Owner;
		this.icon = icon;
		this.Active = !active;
	}
}
