package com.stericson.permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

public class FixPermissions extends ListActivity {
	
	private ArrayList<Permissions_Fix> list = new ArrayList<Permissions_Fix>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fix_permissions_list);
        
        buildList();
        about();
    }
 
    public void buildList() {
		
    	//set up our progress bar
		StaticThings.patienceShowing = true;
		StaticThings.patience = ProgressDialog.show(FixPermissions.this,
				getString(R.string.working), getString(R.string.loadingPackagesAll), true);
		
		new LoadPackages().execute();
    }
	
	private void about() {
		final SpannableString s = new SpannableString(
				"Sometimes playing around with the permissions of an app can break the app. this is probably caused by the permissions for the app being " +
				"set incorrectly somehow. \n\n By choosing an app listed here the default permissions will be restored. In order to use this feature, debugging mode MUST " +
				"be turned on.");
		Linkify.addLinks(s, Linkify.ALL);
		new AlertDialog.Builder(FixPermissions.this).setCancelable(false)
		.setTitle("" +
				"FYI!").setMessage(s)
		.setPositiveButton("Ok", null)
		.show();
	}
	
    private void showList() {
    	//Organize the list
    	java.util.Collections.sort(list, new Sorter_Fix());
    	
    	setListAdapter(new PackageAdapter(this,
				R.layout.fix_permissions_list_row, list));
    	StaticThings.patience();
    }
    
	//user clicked something
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
    	//set up our progress bar
		StaticThings.patienceShowing = true;
		StaticThings.patience = ProgressDialog.show(FixPermissions.this,
				getString(R.string.working), getString(R.string.repairing), true);

		try {
			PackageManager pm = getPackageManager();
			RootTools.debugMode = true;
			//RootTools.sendShell("pm install -r " + pm.getApplicationInfo(list.get(position).packageName, 0).sourceDir);
			RootTools.sendShell("chown " + pm.getApplicationInfo(list.get(position).packageName, 0).uid + "." + pm.getApplicationInfo(list.get(position).packageName, 0).uid + " " + pm.getApplicationInfo(list.get(position).packageName, 0).dataDir);
			RootTools.sendShell("chmod 755 " + pm.getApplicationInfo(list.get(position).packageName, 0).dataDir);
			for (String f : new File(pm.getApplicationInfo(list.get(position).packageName, 0).dataDir).list() ) {
				if (!f.equals("lib")) {
					if (new File(pm.getApplicationInfo(list.get(position).packageName, 0).dataDir + "/" + f).isDirectory()) {
						RootTools.sendShell("chown " + pm.getApplicationInfo(list.get(position).packageName, 0).uid + "." + pm.getApplicationInfo(list.get(position).packageName, 0).uid + " " + pm.getApplicationInfo(list.get(position).packageName, 0).dataDir + "/" + f);
						List<String> tmp = getFiles(f, pm.getApplicationInfo(list.get(position).packageName, 0).dataDir);
						if (tmp != null) {
							for (String file : tmp) {
								RootTools.log(file);
								RootTools.sendShell("chown " + pm.getApplicationInfo(list.get(position).packageName, 0).uid + "." + pm.getApplicationInfo(list.get(position).packageName, 0).uid + " " + pm.getApplicationInfo(list.get(position).packageName, 0).dataDir + "/" + file);
							}
						}
					}
					else {
						RootTools.sendShell("chown " + pm.getApplicationInfo(list.get(position).packageName, 0).uid + "." + pm.getApplicationInfo(list.get(position).packageName, 0).uid + " " + pm.getApplicationInfo(list.get(position).packageName, 0).dataDir + "/" + f);
					}

				}
			}
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StaticThings.patience();
		Toast toast = Toast.makeText(this, "Permissions fixed!", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	//So this will go through all of the directories and return the files.
	public List<String> getFiles(String dir, String parentPath) {
		RootTools.log("Looking for files in " + parentPath + "/" + dir);
		try {
			RootTools.sendShell("chmod 755 " + parentPath + "/" + dir);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
			if (new File(parentPath + "/" + dir).list() != null) {
				for (String f : new File(parentPath + "/" + dir).list()) {
					RootTools.log(f);
					if (new File(parentPath + "/" + dir + "/" + f).isDirectory()) {
						list.add(f);
						List<String> tmp = getFiles(f, dir);
						if (tmp != null) {
							for (String file : tmp) {
								RootTools.log(file);
								list.add(file);
							}
						}
					} else {
						list.add(dir + "/" + f);
					}
				}
			}
		return list;
	}
	
	//this is where all the files get added to our listview
	public class PackageAdapter extends ArrayAdapter<Permissions_Fix> {

		private int[] colors = new int[] { 0xff303030, 0xff404040  };
		private View v;
		private Context context;
		int index = 0;

		public PackageAdapter(Context context, int textViewResourceId, ArrayList<Permissions_Fix> packages) {
			super(context, textViewResourceId, packages);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			v = convertView;
			final Permissions_Fix o = list.get(position);

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.fix_permissions_list_row, null);

			}
			
			if (o != null) {
				
				TextView appName = (TextView) v.findViewById(R.id.appname);
				TextView packageName = (TextView) v.findViewById(R.id.packagename);
				ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
				
				LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

				if (appName != null) {
					appName.setText(o.appName);
				}
				if (packageName != null) {
					packageName.setText(o.packageName);
				}
				if (icon != null) {
					icon.setImageDrawable(o.icon);
				}
				if (position % 2 == 0) {
					row.setBackgroundColor(colors[position % 2]);
				} else {
					row.setBackgroundColor(colors[position % 2]);
				}
			}
			return (v);
		}
	}
	
	//worker class to load packages.
	private class LoadPackages extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			
				PackageManager pm = getPackageManager();
		    	for (ApplicationInfo info : pm.getInstalledApplications(0)) {
		    		if ((info.flags & info.FLAG_SYSTEM) != 1) {
		    			list.add(new Permissions_Fix(info.loadLabel(pm).toString(), info.packageName, info.loadIcon(getPackageManager())));
		    		}
		    	}			
			return "done";
		}
		
		protected void onPostExecute(String result) {
			showList();
		}
		
	}
	
    @Override 
    public void onConfigurationChanged(Configuration newConfig) { 
    super.onConfigurationChanged(newConfig); 
    // We do nothing here. We're only handling this to keep orientation 
    // or keyboard hiding from causing the WebView activity to restart. 
    }
}