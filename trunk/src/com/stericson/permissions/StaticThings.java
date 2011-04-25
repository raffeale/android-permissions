package com.stericson.permissions;

import java.io.File;

import android.app.ProgressDialog;

public class StaticThings {
	
	public static String TAG = "Permissions";
	public static ProgressDialog pBarDialog;

	// Switch for hiding the patiencedialog
	public static boolean patienceShowing;
	public static ProgressDialog patience;
	public static String mymsg;

	public static void patience() {
		if (patienceShowing) {
			patience.dismiss();
			patienceShowing = false;
		}
	}
	
	public static String path() {
		if (new File("/data/system/packages.xml").exists()) {
			return "/data/system/packages.xml";
		} else if(new File("/dbdata/system/packages.xml").exists()) {
			return "/dbdata/system/packages.xml";
		}
		return null;
	}
}
