package com.stericson.permissions.donate;

import android.app.ProgressDialog;
import com.stericson.RootTools.RootTools;

public class Constants {
	
	public static String TAG = "Permissions";

	// Switch for hiding the patiencedialog
	public static boolean isDialogShowing;
	public static ProgressDialog pDialog;
    public static String storagePath = "";

	public static void pDialogClose() {
		if (isDialogShowing) {
			pDialog.dismiss();
			isDialogShowing = false;
		}
	}
	
	public static String path() {
		if (RootTools.exists("/data/system/packages.xml")) {
			return "/data/system/packages.xml";
		} else if(RootTools.exists("/dbdata/system/packages.xml")) {
			return "/dbdata/system/packages.xml";
		}
		return null;
	}
}
