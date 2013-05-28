package com.stericson.permissions.donate.listeners;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.activities.PermissionsApps;
import com.stericson.permissions.donate.domain.Permission;

import java.util.List;

public class PermissionsListItemClick implements OnItemClickListener {

	private PermissionsActivity context;
	private List<Permission> permissions;
	
	public PermissionsListItemClick(PermissionsActivity context, List<Permission> permissions)
	{
		this.context = context;
		this.permissions = permissions;
	}
	
	public void onItemClick(AdapterView<?> arg1, View view, int position, long id) {
		
		App.getInstance().setPermission(permissions.get(position));
		Intent i = new Intent(context, PermissionsApps.class);		
		context.startActivity(i);
	}

}
