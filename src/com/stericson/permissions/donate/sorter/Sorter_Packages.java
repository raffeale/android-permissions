package com.stericson.permissions.donate.sorter;

import com.stericson.permissions.donate.domain.AndroidPackage;

import java.util.Comparator;

public class Sorter_Packages implements Comparator<AndroidPackage>{

	public int compare(AndroidPackage arg0, AndroidPackage arg1) {
		return arg0.getAppName().compareToIgnoreCase(arg1.getAppName());
	}
}
