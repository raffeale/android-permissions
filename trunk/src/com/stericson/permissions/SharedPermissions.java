package com.stericson.permissions;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SharedPermissions extends ListActivity {
	
	private ArrayList<Permissions_Shared> list = new ArrayList<Permissions_Shared>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_permissions);
        
        buildList();
    }
 
    public void buildList() {
		
    	//set up our progress bar
		StaticThings.patienceShowing = true;
		StaticThings.patience = ProgressDialog.show(SharedPermissions.this,
				getString(R.string.working), getString(R.string.loadingPackages), true);
		
		new LoadPackages().execute();
    }

    private void showList(Set<String> SharedUserID) {
    	//Sort the list by SharedUserID
    	ArrayList<Permissions_Shared> list = new ArrayList<Permissions_Shared>();
    	for (String suid : SharedUserID) {
    		for(Permissions_Shared ps : this.list) {
    			if (ps.SharedUserID.equals(suid)) {
    				list.add(ps);
    			}
    		}
    	}
    	this.list = list;
    	setListAdapter(new PackageAdapter(this,
				R.layout.shared_permissions_row, this.list));
    	StaticThings.patience();
    }
    
	//user clicked something
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, SharedPermissionsSpecific.class);
		i.putExtra("SharedUserID", list.get(position).SharedUserID);
		i.putExtra("packageName", list.get(position).packageName);
		this.startActivity(i);
	}
	
	//this is where all the files get added to our listview
	public class PackageAdapter extends ArrayAdapter<Permissions_Shared> {

		private int[] colors = new int[] { 0xff303030, 0xff404040  };
		private View v;
		private Context context;

		public PackageAdapter(Context context, int textViewResourceId, ArrayList<Permissions_Shared> packages) {
			super(context, textViewResourceId, packages);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			v = convertView;
			
			final Permissions_Shared o = list.get(position);
		
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.shared_permissions_row, null);
			}
			
			if (o != null) {
				
				TextView appName = (TextView) v.findViewById(R.id.appname);
				TextView packageName = (TextView) v.findViewById(R.id.packagename);
				TextView shared = (TextView) v.findViewById(R.id.shared2);
				ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
				LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

				if (appName != null) {
					appName.setText(o.appName);
				}
				if (packageName != null) {
					packageName.setText(o.packageName);
				}
				if (shared != null) {
					shared.setText(o.SharedUserID);
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
	private class LoadPackages extends AsyncTask<Void, Void, Set<String>> {

		@Override
		protected Set<String> doInBackground(Void... params) {
			Set<String> SharedUserID = new HashSet<String>();

			PackageManager pm = getPackageManager();						
				XmlPullParserFactory factory;
				try {
					factory = XmlPullParserFactory.newInstance();
					factory.setNamespaceAware(true);
					XmlPullParser xpp = factory.newPullParser();
					
					xpp.setInput(new FileReader("/data/system/packages.xml"));
					int eventType = xpp.getEventType();
										
					while(eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("package") && xpp.getAttributeCount() == 6 && xpp.getAttributeName(5).equals("sharedUserId")) {
							SharedUserID.add(xpp.getAttributeValue(5));
							list.add(new Permissions_Shared(pm.getApplicationInfo(xpp.getAttributeValue(0), 0).loadLabel(getPackageManager()).toString(), pm.getApplicationInfo(xpp.getAttributeValue(0), 0).packageName, xpp.getAttributeValue(5) ,pm.getApplicationInfo(xpp.getAttributeValue(0), 0).loadIcon(getPackageManager())));
						}
						eventType = xpp.next();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			return SharedUserID;
		}
		
		protected void onPostExecute(Set<String> result) {
			showList(result);
		}
		
	}
	
    @Override 
    public void onConfigurationChanged(Configuration newConfig) { 
    super.onConfigurationChanged(newConfig); 
    // We do nothing here. We're only handling this to keep orientation 
    // or keyboard hiding from causing the WebView activity to restart. 
    }
}