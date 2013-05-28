package com.stericson.permissions.donate.listeners;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.activities.ApplicationSpecifics;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.domain.AndroidPackage;

import java.util.List;

public class PackagesListItemClick implements OnItemClickListener {

	private PermissionsActivity context;
	private List<AndroidPackage> list;
	
	public PackagesListItemClick(PermissionsActivity context, List<AndroidPackage> list)
	{
		this.context = context;
		this.list = list;
	}
	
	public void onItemClick(AdapterView<?> arg1, View view, int position, long id) {
		Intent i = new Intent(context, ApplicationSpecifics.class);
		
		App.getInstance().setPackage(list.get(position));
		
		context.startActivity(i);
	}

}
