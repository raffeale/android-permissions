package com.stericson.permissions.donate.sorter;

import com.stericson.permissions.donate.domain.Permissions_Fix;

import java.util.Comparator;

public class Sorter_Fix implements Comparator<Permissions_Fix>{

	public int compare(Permissions_Fix arg0, Permissions_Fix arg1) {
		return arg0.appName.compareToIgnoreCase(arg1.appName);
	}

}
