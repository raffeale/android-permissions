package com.stericson.permissions.donate.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.stericson.permissions.donate.adapters.ApplicationSpecificsAdapter;
import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.ChangePermission;
import com.stericson.permissions.donate.jobs.LoadPermissionsForPackage;

import java.util.ArrayList;

public class ApplicationSpecifics extends BaseListActivity {

	private ImageView icon;
	private TextView appName;
	private String packageName;
	private ArrayList<Permission> list = new ArrayList<Permission>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.application_permissions);
    
		tf = Typeface.createFromAsset(getAssets(), "fonts/DJGROSS.ttf");

        TextView header = (TextView) findViewById(R.id.header_main);
		header.setTypeface(tf);
		
        icon = (ImageView) this.findViewById(R.id.icon);
        appName = (TextView) this.findViewById(R.id.appName);

        findPermissions();
    }

    public void findPermissions() {
    	
    	AndroidPackage aPackage = App.getInstance().getPackage();
    	packageName = aPackage.getPackageName();
    	
  		new LoadPermissionsForPackage(this, true, packageName).execute();
  		
    	if (aPackage.isShared())
    	{    		
    		String apps = "";
    		for (String string : aPackage.getAppNames())
    		{
				apps += string + "\n";
    		}
    		apps = apps.substring(0, apps.length() - 1);
			icon.setImageResource(R.drawable.caution);

			appName.setText(aPackage.getPackageName() + " (" + aPackage.getUserID() + ")");
  		
    	}
    	else
    	{
			icon.setImageDrawable(aPackage.getIcon());
			appName.setText(aPackage.getAppName());
    	}
    }

    @Override
    public void jobCallBack(Result result, int id)
    {
        if (id == ChangePermission.CHANGE_PERMISSION)
        {
            if (!sp.contains("remember3"))
            {
                initiatePopupWindow(ApplicationSpecifics.this.getText(R.string.warn2), false, ApplicationSpecifics.this);
                sp.edit().putBoolean("remember3", true).commit();
            }
        }
        else if (id == LoadPermissionsForPackage.LOAD_PERMISSIONS_PACKAGE)
        {
            list = (ArrayList<Permission>) result.getList();
            showList();
        }

    }

    private void showList() {
    	setListAdapter(new ApplicationSpecificsAdapter(this,
				R.layout.application_permissions_row, list));
    	Constants.pDialogClose();
    }
    	
	//user clicked something
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		list.get(position).setActive(!list.get(position).isActive());
		
		if (list.get(position).isActive()) {
			((TextView) v.findViewById(R.id.active)).setTextColor(Color.GREEN);
			((TextView) v.findViewById(R.id.active)).setText(" " + getString(R.string.active));
		} else {
			((TextView) v.findViewById(R.id.active)).setTextColor(Color.RED);
			((TextView) v.findViewById(R.id.active)).setText(" " + getString(R.string.disabled));
		}
		
		Toast.makeText(this, getString(R.string.afterReboot), Toast.LENGTH_LONG).show();

        new ChangePermission(this, true, list.get(position).getPermission(), packageName).execute();
	}
	

    @Override 
    public void onConfigurationChanged(Configuration newConfig) { 
    super.onConfigurationChanged(newConfig); 
    // We do nothing here. We're only handling this to keep orientation 
    // or keyboard hiding from causing the WebView activity to restart. 
    }
}
