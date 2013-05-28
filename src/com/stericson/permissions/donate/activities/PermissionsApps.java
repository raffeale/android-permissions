package com.stericson.permissions.donate.activities;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.adapters.PermissionsAppsAdapter;
import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.ChangePermission;
import com.stericson.permissions.donate.jobs.LoadPackages;
import com.stericson.permissions.donate.sorter.Sorter_Packages;

import java.util.ArrayList;

public class PermissionsApps extends BaseListActivity {

	private ImageView icon;
	private TextView appName;
	private TextView permissionDescription;
	private Permission permission;

	private ArrayList<AndroidPackage> pcks = new ArrayList<AndroidPackage>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.permissions_app);
    
	    sp = this.getSharedPreferences(Constants.TAG, MODE_PRIVATE);

        TextView header = (TextView) findViewById(R.id.header_main);
		header.setTypeface(tf);
		
        icon = (ImageView) this.findViewById(R.id.icon);
        appName = (TextView) this.findViewById(R.id.permissionName);
        permissionDescription = (TextView) this.findViewById(R.id.permissionDescription);
        
        findPermissions();
    }

    public void findPermissions() {

        this.permission = App.getInstance().getPermission();

        //set up our progress bar
        Constants.isDialogShowing = true;
        Constants.pDialog = ProgressDialog.show(PermissionsApps.this,
                getString(R.string.working), getString(R.string.loadingPermissions), true);

        new LoadPackages(this, true, permission).execute();

        icon.setImageDrawable(this.permission.getIcon());
        appName.setText(permission.getPermission());
        permissionDescription.setText(permission.getPermissionDescription());
    }

    public void jobCallBack(Result result, int id)
    {
        if (id == ChangePermission.CHANGE_PERMISSION)
        {
            Toast.makeText(this, result.isSuccess() ? getString(R.string.permissionsChanged) : getString(R.string.permissionsChangeFailed), Toast.LENGTH_LONG).show();
        }
        else if (id == LoadPackages.LOAD_PACKAGES)
        {
            pcks.addAll((ArrayList<AndroidPackage>) result.getList());
            //Organize the list
            java.util.Collections.sort(pcks, new Sorter_Packages());

            showList();
        }
    }

    //user clicked something
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        pcks.get(position).setActive(!pcks.get(position).isActive());

        if (pcks.get(position).isActive()) {
            ((TextView) v.findViewById(R.id.active2)).setTextColor(Color.GREEN);
            ((TextView) v.findViewById(R.id.active2)).setText(" " + getString(R.string.active));
        } else {
            ((TextView) v.findViewById(R.id.active2)).setTextColor(Color.RED);
            ((TextView) v.findViewById(R.id.active2)).setText(" " + getString(R.string.disabled));
        }

        Toast.makeText(this, getString(R.string.afterReboot), Toast.LENGTH_LONG).show();
        new ChangePermission(this, true, permission.getPermission(), pcks.get(position).getPackageName()).execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) { 
    super.onConfigurationChanged(newConfig); 
    // We do nothing here. We're only handling this to keep orientation 
    // or keyboard hiding from causing the WebView activity to restart. 
    }

    private void showList() {
    	setListAdapter(new PermissionsAppsAdapter(this,
				R.layout.application_permissions_row, pcks));
    	Constants.pDialogClose();
    }
}
