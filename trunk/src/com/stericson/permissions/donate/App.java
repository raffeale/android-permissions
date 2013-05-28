package com.stericson.permissions.donate;

import android.view.View;

import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;

import java.util.ArrayList;
import java.util.List;

public class App
{
	private static App instance = null;
	private View popupView;
	private ArrayList<AndroidPackage> list = null;
	private Permission permission;
	private AndroidPackage packages;

		
	public void setPopupView(View view)
	{
		this.popupView = view;
	}
		
	public void setList(List<AndroidPackage> list)
	{
		this.list = new ArrayList<AndroidPackage>();
		this.list.clear();
		this.list.addAll(list);
	}
	
	public void setPermission(Permission permission)
	{
		this.permission = permission;
	}
	
	public void setPackage(AndroidPackage packages)
	{
		this.packages = packages;
	}
	
	public Permission getPermission()
	{
		return this.permission;
	}
	
	public AndroidPackage getPackage()
	{
		return this.packages;
	}
	
	public ArrayList<AndroidPackage> getList()
	{
		return list;
	}
	
	public View getPopupView()
	{
		return this.popupView;
	}
	
	public static App getInstance()
	{
		if (instance == null)
			instance = new App();
		return instance;
	}


}
