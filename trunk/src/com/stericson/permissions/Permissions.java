package com.stericson.permissions;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.stericson.RootTools.RootTools;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
		//Sadly this does not work, it just comes right back.
		//menu.add("Disable Google Service Framework Permissions");
		//menu.add("Manage Shared Permissions");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		//if (item.getTitle().equals("Manage Shared Permissions")) {
		//	Intent i = new Intent(this, SharedPermissions.class);
		//	this.startActivity(i);
		//}
		if (item.getTitle().equals("About")) {
			about();
		}
		//if (item.getTitle().equals("Disable Google Service Framework Permissions")) {
		//	Intent i = new Intent(this, MasterPermissions.class);
		//	this.startActivity(i);
		//}
		// consume the event
		return true;
	}
	
	private void about() {
		final SpannableString s = new SpannableString("This app was designed and created by Stericson. \n\n" +
				"The concept came about when a community member pointed me to another app that does the exact same thing. This app is called Permission Blocker and was created by Fr4gg0r. I started the app to see if I could improve on it a bit and found I couldn't really. I will continue working on this though, as I have and idea on how to tackle this in a different manner. \n\n " +
				"If you would like to keep up on what I am doing or working on follow me on Twitter: \n\n http://www.Twitter.com/Stericson \n\n" +
				"If you would like to email me, feel free to do so at StericDroid@gmail.com");
		Linkify.addLinks(s, Linkify.ALL);
		new AlertDialog.Builder(Permissions.this).setCancelable(false)
		.setTitle("" +
				"About me!").setMessage(s)
		.setPositiveButton("Ok", null)
		.show();
	}
    private void showList() {
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
	    	for (PackageInfo info : pm.getInstalledPackages(0)) {
	    		//Only show those packages that are requesting permissions.
	    		try {
					if (pm.getPackageInfo(info.packageName, pm.GET_PERMISSIONS).requestedPermissions != null) {
						
						XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
						factory.setNamespaceAware(true);
						XmlPullParser xpp = factory.newPullParser();
						
						xpp.setInput(new FileReader("/data/system/packages.xml"));
						int eventType = xpp.getEventType();
						
						while(eventType != XmlPullParser.END_DOCUMENT) {
							if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("package")) {
								if (xpp.getAttributeValue(0).contains(info.packageName)) {
									while (eventType != XmlPullParser.END_DOCUMENT ) {
										if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("package")) {
											break;
										}
										if (xpp.getEventType() == XmlPullParser.START_TAG) {
											if (xpp.getName().contains("perms")) {
												//There are permissions that we can change.
												list.add(new Packages(info.applicationInfo.loadLabel(getPackageManager()).toString(), info.packageName, info.applicationInfo.loadIcon(getPackageManager())));
												break;
											}
										}
										eventType = xpp.next();
									}
								}
							}

							eventType = xpp.next();
						}
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
			return "done";
		}
		
		protected void onPostExecute(String result) {
			showList();
		}
		
	}
}