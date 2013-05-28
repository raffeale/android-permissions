package com.stericson.permissions.donate.listeners;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.domain.Permissions_Fix;
import com.stericson.permissions.donate.jobs.FixPerms;

import java.util.ArrayList;

public class FixPermissionsListItemClick implements OnItemClickListener {

	private PermissionsActivity context;
	private ArrayList<Permissions_Fix> list;
	
	public FixPermissionsListItemClick(PermissionsActivity context, ArrayList<Permissions_Fix> list)
	{
		this.context = context;
		this.list = list;
	}
	
	public void onItemClick(AdapterView<?> arg1, View view, int position, long id) {
    	//set up our progress bar
		Constants.isDialogShowing = true;
		Constants.pDialog = ProgressDialog.show(context,
				context.getString(R.string.working), context.getString(R.string.repairing), true);

		FixPerms.fix(context, list.get(position).packageName);
		
		Constants.pDialogClose();
		Toast toast = Toast.makeText(context, context.getString(R.string.permissionsFixed), Toast.LENGTH_SHORT);
		toast.show();
	}

}
