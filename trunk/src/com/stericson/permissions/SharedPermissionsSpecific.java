package com.stericson.permissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;

public class SharedPermissionsSpecific extends ListActivity {

	private ImageView icon;
	private TextView appName;
	private String packageName;
	private String SharedUserID;


	private ArrayList<Permissions_Shared_Specific> list = new ArrayList<Permissions_Shared_Specific>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_permissions);
    
        icon = (ImageView) this.findViewById(R.id.icon);
        appName = (TextView) this.findViewById(R.id.appName);
        
        findPermissions();
    }
    
    public void findPermissions() {
    	
    	Bundle extras = getIntent().getExtras();
    	packageName = extras.getString("packageName");
    	SharedUserID = extras.getString("SharedUserID");
    	
    	PackageManager pm = getPackageManager();
    	
		try {
			icon.setImageDrawable(pm.getApplicationIcon(packageName));
			appName.setText(pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	
		//set up our progress bar
		StaticThings.patienceShowing = true;
		StaticThings.patience = ProgressDialog.show(SharedPermissionsSpecific.this,
				getString(R.string.working), getString(R.string.loadingPermissions), true);
		
		new LoadPackages().execute();
    }
 
    private void showList() {
    	setListAdapter(new PermissionsAdapter(this,
				R.layout.application_permissions_row, list));
    	StaticThings.patience();
    }
    
	//user clicked something
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		list.get(position).Active = !list.get(position).Active;
		
		((TextView) v.findViewById(R.id.permissionis)).setText("This permission will be ");
		if (list.get(position).Active) {
			((TextView) v.findViewById(R.id.active)).setTextColor(Color.GREEN);
			((TextView) v.findViewById(R.id.active)).setText("Active AFTER a reboot");
		} else {
			((TextView) v.findViewById(R.id.active)).setTextColor(Color.RED);
			((TextView) v.findViewById(R.id.active)).setText("Disabled AFTER a reboot");	
		}
		
		changePermissions(list.get(position).Permission);
	}
	
	public void changePermissions(String permission) {
		if (!RootTools.isAccessGiven()) {
			new AlertDialog.Builder(SharedPermissionsSpecific.this).setCancelable(false)
			.setTitle("OOOOPS!").setMessage("We could not get root access, therefore we cannot make the needed changes to adjust this apps permissions." +
					"\n\n Check that you have root, that you have given the app root access via SuperUser, and try again.")
			.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						SharedPermissionsSpecific.this.finish();
					}
				})
			.show();
		} else {
	    	//set up our progress bar
			StaticThings.patienceShowing = true;
			StaticThings.patience = ProgressDialog.show(SharedPermissionsSpecific.this,
					getString(R.string.working), getString(R.string.changingPermission), true);
			
			new ChangePermissions().execute(permission);
		}
	}
	
	public void genericError() {
		final SpannableString s = new SpannableString("We could not perform the requested operation! \n\n Please email me about this: StericDroid@gmail.com");
		Linkify.addLinks(s, Linkify.ALL);
		new AlertDialog.Builder(SharedPermissionsSpecific.this).setCancelable(false)
		.setTitle("OOOOPS!").setMessage(s)
		.setPositiveButton("Ok",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					SharedPermissionsSpecific.this.finish();
				}
			})
		.show();	
	}
	
	//this is where all the files get added to our listview
	public class PermissionsAdapter extends ArrayAdapter<Permissions_Shared_Specific> {
		
		private int[] colors = new int[] { 0xff303030, 0xff404040  };
		private View v;
		private Context context;

		public PermissionsAdapter(Context context, int textViewResourceId, ArrayList<Permissions_Shared_Specific> permissions) {
			super(context, textViewResourceId, permissions);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			v = convertView;
			final Permissions_Shared_Specific o = list.get(position);

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.application_permissions_row, null);

			}
			
			if (o != null) {
				
				TextView permissionName = (TextView) v.findViewById(R.id.permission);
				TextView permissionDescription = (TextView) v.findViewById(R.id.permissionDescription);
				TextView owner = (TextView) v.findViewById(R.id.Owner2);
				ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
				TextView active = (TextView) v.findViewById(R.id.active);
				LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

				if (permissionName != null) {
					permissionName.setText(o.Permission);
				}
				if (permissionDescription != null) {
					permissionDescription.setText(o.PermissionDescription);
				}
				if (owner != null) {
					owner.setText(o.Owner);
				}
				if (icon != null) {
					icon.setImageDrawable(o.icon);
				}
				if (active != null) {
					if (o.Active) {
						active.setTextColor(Color.GREEN);
						active.setText("Active");
					} else {
						active.setTextColor(Color.RED);
						active.setText("Disabled");	
					}
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
    		try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				
				xpp.setInput(new FileReader("/data/system/packages.xml"));
				int eventType = xpp.getEventType();
				
				while(eventType != XmlPullParser.END_DOCUMENT) {
					if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("shared-user") && xpp.getAttributeValue(1).equals(SharedUserID)) {
						while (eventType != XmlPullParser.END_DOCUMENT ) {
							if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("shared-user")) {
								break;
							}
							if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("item")) {
								
								//everything in here is a permission
								String description;
								if (pm.getPermissionInfo(xpp.getAttributeValue(0).toString().replace("stericson.disable.", ""), 0).loadDescription(pm) == null) {
									description = "No description available for this permission";
								} else {
									description = pm.getPermissionInfo(xpp.getAttributeValue(0).toString().replace("stericson.disable.", ""), 0).loadDescription(pm).toString();
								}
								
								list.add(new Permissions_Shared_Specific(xpp.getAttributeValue(0).replace("stericson.disable.", ""), description, pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disable.", ""), 0).packageName, pm.getApplicationInfo(pm.getPermissionInfo(xpp.getAttributeValue(0).replace("stericson.disable.", ""), 0).packageName, 0).loadIcon(getPackageManager()), xpp.getAttributeValue(0).contains("stericson.disabled.")));
							}
							eventType = xpp.next();
						}
					}
					eventType = xpp.next();
				}
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "done";
		}
		
		protected void onPostExecute(String result) {
			showList();
		}
	}

	//worker class to change permissions
	private class ChangePermissions extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... permission) {
			PackageManager pm = getPackageManager();
			try {
				RootTools.sendShell("dd if=/data/system/packages.xml of=/data/local/packages1.xml");
				RootTools.sendShell("dd if=/data/system/packages.xml of=/data/local/packages.xml");
				RootTools.sendShell("chmod 0777 /data/local/packages1.xml");
				RootTools.sendShell("chmod 0777 /data/local/packages.xml");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (new File("/data/local/packages1.xml").exists()) {
				//Time to read the file, find the line we are looking for.
				try {
					String readTarget = "/data/local/packages1.xml";
					String writeTarget = "/data/local/packages.xml";
			        LineNumberReader lnr = new LineNumberReader( new FileReader( readTarget ) );
			        FileWriter fw = new FileWriter( writeTarget );
			        String line;
			        while( (line = lnr.readLine()) != null ){
			        	//Found the package name
			            if (line.contains("shared-user") && line.contains("userId=\"" + SharedUserID + "\"")) {
			            	RootTools.debugMode = true;
			            	RootTools.log(line);
			            	fw.write(line + "\n");
			            	//Looking for the permission
			            	while( (line = lnr.readLine()) != null ){
			            		//Found the permission
			            		if (line.contains(permission[0])) {
					            	RootTools.log(line);
			            			String tmp;
			            			if (line.contains("stericson.disabled.")) {
			            				tmp = line.replace("stericson.disabled.", "");
			            			} else {
			            				tmp = line.replace("name=\"", "name=\"stericson.disabled.");
			            			}
			            			fw.write(tmp + "\n");
			            			line = lnr.readLine();
			            			break;
			            		} else {
			            			fw.write(line + "\n");
			            		}
			            	}
			            }
			            fw.write(line + "\n");
			        }
			        fw.close();
			        lnr.close();
				} catch (IOException e) {
					e.printStackTrace();
					return 1;
				}
				
				try {
					RootTools.sendShell("dd if=/data/local/packages.xml of=/data/system/packages.xml");
					//Dont be messy, clean up!
					RootTools.sendShell("rm /data/local/packages1.xml");
					RootTools.sendShell("rm /data/local/packages.xml");
					
					return 2;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				return 1;
			}
			return 2;
		}
		
		protected void onPostExecute(Integer result) {
			switch (result) {
			case 1:
				genericError();
				break;
			case 2:
				StaticThings.patience();
				break;
			}
		}
	}
}
