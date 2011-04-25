package com.stericson.permissions;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Permissions extends ListActivity {
	
	private ArrayList<Packages> list = new ArrayList<Packages>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.package_list);
        
        buildList();
    }
 
    public void buildList() {
		
    	//set up our progress bar
		StaticThings.patienceShowing = true;
		StaticThings.patience = ProgressDialog.show(Permissions.this,
				getString(R.string.working), getString(R.string.loadingPackages), true);
		
		new LoadPackages().execute();
    }

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("About");
		menu.add("Reboot");
		menu.add("Advanced Options");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("About")) {
			about();
		}
		if (item.getTitle().equals("Reboot")) {
			Reboot();
		}
		if (item.getTitle().equals("Advanced Options")) {
			Intent i = new Intent(this, Advanced.class);
			this.startActivity(i);
		}
		return true;
	}
	
	private void about() {
		final SpannableString s = new SpannableString(
				"This app is fully open sourced, you can find the source for it at: \n\n" +
				"http://code.google.com/p/android-permissions/ \n\n" +
				"The original concept for this app came about by another app called Permission Blocker which was created by Fr4gg0r. \n\n I started the app to see if I could improve on it a bit and to just tinker with the permissions. \n\n" +
				"Twitter: \n\n http://www.Twitter.com/Stericson \n\n" +
				"Email: \n\n StericDroid@gmail.com");
		Linkify.addLinks(s, Linkify.ALL);
		new AlertDialog.Builder(Permissions.this).setCancelable(false)
		.setTitle("" +
				"About me!").setMessage(s)
		.setPositiveButton("Ok", null)
		.show();
	}
	
	private void Reboot() {
		new AlertDialog.Builder(Permissions.this).setCancelable(false)
		.setTitle("You sure?").setMessage("Are you sure you want to reboot?")
		.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					RootTools.sendShell("reboot");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RootToolsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).setNegativeButton("No", null)
		.show();
	}
	
    private void showList() {
    	//Organize the list
    	java.util.Collections.sort(list, new Sorter());
    	
    	setListAdapter(new PackageAdapter(this,
				R.layout.packages_row, list));
    	StaticThings.patience();
    }
    
	//user clicked something
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, ApplicationSpecifics.class);
		i.putExtra("packageName", list.get(position).packageName);
		this.startActivity(i);
	}
	
	//this is where all the files get added to our listview
	public class PackageAdapter extends ArrayAdapter<Packages> {

		private int[] colors = new int[] { 0xff303030, 0xff404040  };
		private View v;
		private Context context;
		int index = 0;

		public PackageAdapter(Context context, int textViewResourceId, ArrayList<Packages> packages) {
			super(context, textViewResourceId, packages);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			v = convertView;
			final Packages o = list.get(position);

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.packages_row, null);

			}
			
			if (o != null) {
				
				TextView appName = (TextView) v.findViewById(R.id.appname);
				TextView packageName = (TextView) v.findViewById(R.id.packagename);
				TextView found = (TextView) v.findViewById(R.id.found);
				TextView active = (TextView) v.findViewById(R.id.active2);
				TextView denied = (TextView) v.findViewById(R.id.disabled2);
				ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
				
				LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

				if (appName != null) {
					appName.setText(o.appName);
				}
				if (packageName != null) {
					packageName.setText(o.packageName);
				}
				if (found != null) {
					found.setText("Permissions found: " + o.permissionCount);
				}
				if (active != null) {
					active.setText(Integer.toString(o.activeCount));
				}
				if (denied != null) {
					denied.setText(Integer.toString(o.deniedCount));
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
			
			try {
				ArrayList<Packages> tmpList = new ArrayList<Packages>();
				
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				
				xpp.setInput(new FileReader(StaticThings.path()));
				int eventType = xpp.getEventType();
				
				while(eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("package")) {
						
						String packageName = xpp.getAttributeValue(0);
						Packages tmp = new Packages("", "", null, 0, 0, 0);
						
						while (eventType != XmlPullParser.END_DOCUMENT ) {
							if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("package")) {
								if (!tmp.packageName.equals("") && tmp.permissionCount != 0) {
									tmpList.add(tmp);
								}
								break;
							}
							if (xpp.getEventType() == XmlPullParser.START_TAG) {
								if (xpp.getName().contains("perms")) {
									//There are permissions that we can change.
									tmp.packageName = packageName;
								}
								else if (xpp.getName().equals("item")) {
									//count the permissions
									tmp.permissionCount++;
									if (xpp.getAttributeValue(0).contains("stericson.disabled.")) {
										tmp.deniedCount++;
									} else {
										tmp.activeCount++;
									}
								}
							}
							eventType = xpp.next();
						}
					}
					eventType = xpp.next();
				}
				
				PackageManager pm = getPackageManager();
		    	for (PackageInfo info : pm.getInstalledPackages(0)) {
		    		//Only show those packages that are requesting permissions.
					if (pm.getPackageInfo(info.packageName, pm.GET_PERMISSIONS).requestedPermissions != null) {
						for (Packages p : tmpList) {
							if (p.packageName.equals(info.packageName)) {
								list.add(new Packages(info.applicationInfo.loadLabel(getPackageManager()).toString(), info.packageName, info.applicationInfo.loadIcon(getPackageManager()), p.permissionCount, p.activeCount, p.deniedCount ));
							}
						}
					}
		    	}
			} catch (Exception e) {
				e.printStackTrace();
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