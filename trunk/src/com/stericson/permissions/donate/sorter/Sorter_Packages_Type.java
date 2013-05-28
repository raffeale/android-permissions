package com.stericson.permissions.donate.sorter;

import com.stericson.permissions.donate.domain.AndroidPackage;

import java.util.Comparator;

public class Sorter_Packages_Type implements Comparator<AndroidPackage> {

	public int compare(AndroidPackage lhs, AndroidPackage rhs) {
		return lhs.getType().compareTo(rhs.getType());
	}
}
