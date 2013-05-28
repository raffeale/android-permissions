package com.stericson.permissions.donate.listeners;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.jobs.ChangeMasterPermission;

public class MasterPermissionsListItemClick implements OnItemClickListener {

	private PermissionsActivity context;
	
	public MasterPermissionsListItemClick(PermissionsActivity context) {
		this.context = context;
	}

	public void onItemClick(AdapterView<?> arg1,final View view, int position, long id) {

        new AlertDialog.Builder(context)
        .setCancelable(true)
        .setMessage(context.getString(R.string.diablePermission))
        .setPositiveButton(context.getString(R.string.enable),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                        int whichButton) {

                    new ChangeMasterPermission(context, false, ((TextView) view.findViewById(R.id.permission)).getText().toString()).execute();
                }
            })
        .setNegativeButton(context.getString(R.string.disable),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                        int whichButton) {

                    new ChangeMasterPermission(context, true, ((TextView) view.findViewById(R.id.permission)).getText().toString()).execute();
                }
            })
        .show();
	    }

}
