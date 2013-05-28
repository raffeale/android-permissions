package com.stericson.permissions.donate.sorter;

import com.stericson.permissions.donate.domain.Permission;

import java.util.Comparator;

public class Sorter_Permissions implements Comparator<Permission>{

	public int compare(Permission lhs, Permission lhs2) {
		return lhs.getPermission().compareToIgnoreCase(lhs2.getPermission());
	}
}
