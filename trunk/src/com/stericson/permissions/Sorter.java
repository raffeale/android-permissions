package com.stericson.permissions;

import java.util.Comparator;

public class Sorter implements Comparator<Packages>{

	@Override
	public int compare(Packages arg0, Packages arg1) {
		return arg0.appName.compareToIgnoreCase(arg1.appName);
	}

}
