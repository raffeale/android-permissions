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
import android.content.res.Configuration;
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

public class MasterPermissions extends ListActivity {
	
	private ArrayList<Permissions_Master> list = new ArrayList<Permissions_Master>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_list);
        
        buildList();
        warn();
    }
	
    public void warn() {
		new AlertDialog.Builder(MasterPermissions.this).setCancelable(false)
		.setTitle("WARNING!!").setMessage("Making changes to these permissions can have really bizarre effects! \n\n" +
				"ONLY play with these AFTER making a backup of your device first! \n\n" +
				"While changing any permissions can cause strange effects, these permissions are used by applications throughout " +
				"your entire phone, which means that by changing these permissions you COULD cause your device to bootloop! \n\n" +
				"You have been warned!")
		.setPositiveButton("ok",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
				}
			})
		.show();
    }
    public void buildList() {
		
    	//set up our progress bar
		StaticThings.patienceShowing = true;
		StaticThings.patience = ProgressDialog.show(MasterPermissions.this,
				getString(R.string.working), getString(R.string.loadingPermissions), true);
		
		new LoadPackages().execute();
    }
	
    private void showList() {
    	setListAdapter(new PermissionsAdapter(this,
				R.layout.master_permissions_row, list));
    	StaticThings.patience();
    }
    
	//user clicked something
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (!RootTools.isAccessGiven()) {
			new AlertDialog.Builder(MasterPermissions.this).setCancelable(false)
			.setTitle("OOOOPS!").setMessage("We could not get root access, therefore we cannot make the needed changes to adjust this apps permissions." +
					"\n\n Check that you have root, that you have given the app root access via SuperUser, and try again.")
			.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						MasterPermissions.this.finish();
					}
				})
			.show();
		} else {
			
			list.get(position).Active = !list.get(position).Active;

			((TextView) v.findViewById(R.id.permissionis)).setText("This permission will be ");
			if (list.get(position).Active) {
				((TextView) v.findViewById(R.id.active)).setTextColor(Color.GREEN);
				((TextView) v.findViewById(R.id.active)).setText("Active AFTER a reboot");
			} else {
				((TextView) v.findViewById(R.id.active)).setTextColor(Color.RED);
				((TextView) v.findViewById(R.id.active)).setText("Disabled AFTER a reboot");	
			}
			
	    	//set up our progress bar
			StaticThings.patienceShowing = true;
			StaticThings.patience = ProgressDialog.show(MasterPermissions.this,
					getString(R.string.working), getString(R.string.changingPermission), true);
			
			new ChangePermissions().execute(list.get(position).Permission);
		}
	}
	
	//this is where all the files get added to our listview
	private class PermissionsAdapter extends ArrayAdapter<Permissions_Master> {

		private int[] colors = new int[] { 0xff303030, 0xff404040  };
		private View v;
		private Context context;
		int index = 0;

		public PermissionsAdapter(Context context, int textViewResourceId, ArrayList<Permissions_Master> packages) {
			super(context, textViewResourceId, packages);
			this.context = context;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			v = convertView;
			final Permissions_Master o = list.get(position);

			if (v == null) {
				LayoutInflater vi = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.master_permissions_row, null);

			}
			
			if (o != null) {
				
				TextView permission = (TextView) v.findViewById(R.id.permission);
				TextView permissionDescription = (TextView) v.findViewById(R.id.permissionDescription);
				TextView owner = (TextView) v.findViewById(R.id.Owner2);
				ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
				TextView active = (TextView) v.findViewById(R.id.active);
				LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

				if (permission != null) {
					permission.setText(o.Permission);
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

	public void genericError() {
		final SpannableString s = new SpannableString("We could not perform the requested operation! \n\n Please email me about this: StericDroid@gmail.com");
		Linkify.addLinks(s, Linkify.ALL);
		new AlertDialog.Builder(MasterPermissions.this).setCancelable(false)
		.setTitle("OOOOPS!").setMessage(s)
		.setPositiveButton("Ok",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					MasterPermissions.this.finish();
				}
			})
		.show();	
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
					if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("permissions")) {
						while (eventType != XmlPullParser.END_DOCUMENT ) {
							if (eventType == XmlPullParser.END_TAG && xpp.getName().equals("permissions")) {
								break;
							}
							if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("item")) {
								
								//Make sure we don't show permissions with Stericson.disable in them, they aren't real!
								if (!xpp.getAttributeValue(0).contains("stericson.disable.")) {
									//everything in here is a permission
									String description;
									if (pm.getPermissionInfo(xpp.getAttributeValue(0).toString(), 0).loadDescription(pm) == null) {
										description = "No description available for this permission";
									} else {
										description = pm.getPermissionInfo(xpp.getAttributeValue(0).toString(), 0).loadDescription(pm).toString();
									}
									
									list.add(new Permissions_Master(xpp.getAttributeValue(0), description, pm.getApplicationInfo(xpp.getAttributeValue(1), 0).loadLabel(getPackageManager()).toString(), pm.getApplicationInfo(xpp.getAttributeValue(1), 0).loadIcon(getPackageManager()), xpp.getAttributeValue(0).contains("stericson.disabled.")));
								}
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

	//worker class to load packages.
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
			        	if (line.contains("</permissions>")) {
			        		break;
			        	}
			        	//Found the permissions
			            if (line.contains("<permissions>")) {
			            	fw.write(line + "\n");
			            	//Looking for the permission
			            	while( (line = lnr.readLine()) != null ){
			            		//Found the permission
			            		if (line.contains(permission[0])) {
			            			String tmp;
			            			if (line.contains("stericson.disabled.")) {
			            				tmp = line.replace("stericson.disabled.", "");
			            			} else {
			            				tmp = line.replace("name=\"", "name=\"stericson.disabled.");
			            			}
			            			fw.write(tmp + "\n");
			            			line = lnr.readLine();
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
					//Dont be messy, clean the fucking shit up!
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
	
    @Override 
    public void onConfigurationChanged(Configuration newConfig) { 
    super.onConfigurationChanged(newConfig); 
    // We do nothing here. We're only handling this to keep orientation 
    // or keyboard hiding from causing the WebView activity to restart. 
    }
}