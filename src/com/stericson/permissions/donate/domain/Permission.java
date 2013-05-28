package com.stericson.permissions.donate.domain;

import android.graphics.drawable.Drawable;

public class Permission {

	private String Permission;
    private String PermissionDescription;
    private String Owner;
    private Drawable icon;
    private boolean Active;
    private String packageName;

    public Permission() {}

	public Permission(String permission, String permissionDescription, String Owner, Drawable icon, boolean active, String packageName) {
		this.Permission = permission;
		this.PermissionDescription = permissionDescription;
		this.Owner = Owner;
		this.icon = icon;
		this.Active = !active;
        this.packageName = packageName;
	}

    public Drawable getIcon() {
        return icon;
    }

    public String getOwner() {
        return Owner;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPermission() {
        return Permission;
    }

    public String getPermissionDescription() {
        return PermissionDescription;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPermission(String permission) {
        Permission = permission;
    }

    public void setPermissionDescription(String permissionDescription) {
        PermissionDescription = permissionDescription;
    }
}
