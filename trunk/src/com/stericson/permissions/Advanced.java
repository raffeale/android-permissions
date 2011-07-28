package com.stericson.permissions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

public class Advanced extends Activity {
	
	Button restore;
	Button master;
	Button repair;
	Button backup;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.advanced);
        
	    restore = (Button) findViewById(R.id.restore);
	    restore.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	        	//set up our progress bar
	    		StaticThings.patienceShowing = true;
	    		StaticThings.patience = ProgressDialog.show(Advanced.this,
	    				getString(R.string.working), getString(R.string.restoringpermissions), true);
	    		
	    		new RestorePermissions().execute();
	    	}
	    });
	    
	    master = (Button) findViewById(R.id.master);
	    master.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		master();
	    	}
	    });
	    
	    repair = (Button) findViewById(R.id.repair);
	    repair.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		repair();
	    	}
	    });
	    
	    backup = (Button) findViewById(R.id.backup);
	    backup.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		backup();
	    	}
	    });
    }
    
    private void master() {
    	Intent i = new Intent(this, MasterPermissions.class);
    	this.startActivity(i);
    	this.finish();
    }
    
    private void repair() {
    	Intent i = new Intent(this, FixPermissions.class);
    	this.startActivity(i);
    	this.finish();
    }
    
    private void backup() {
    	if (new File(Environment.getExternalStorageDirectory() + "/packages.xml").exists()) {
    		final SpannableString s = new SpannableString("Would you like to create or restore your backup? \n\n (Creating a backup will overwrite the existing backup)");
    		Linkify.addLinks(s, Linkify.ALL);
    		new AlertDialog.Builder(Advanced.this).setCancelable(false)
    		.setTitle("Up to you...").setMessage(s)
    		.setPositiveButton("Create",
    			new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,
    						int whichButton) {
    					try {
    						RootTools.sendShell("dd if=" + StaticThings.path() + " of=" + Environment.getExternalStorageDirectory() + "/packages.xml");
    						Toast toast = Toast.makeText(Advanced.this, "Backup Created!", Toast.LENGTH_SHORT);
    						toast.show();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
    			})
    		.setNegativeButton("Restore",
        			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					try {
						RootTools.sendShell("dd if=" + Environment.getExternalStorageDirectory() + "/packages.xml of=" + StaticThings.path());
						Toast toast = Toast.makeText(Advanced.this, "Backup Restored! \n\n Reboot required for changes to take place.", Toast.LENGTH_SHORT);
						toast.show();
						
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			})
    		.show();
    	} else {
    		final SpannableString s = new SpannableString("No existing backup found, please create one....");
    		Linkify.addLinks(s, Linkify.ALL);
    		new AlertDialog.Builder(Advanced.this).setCancelable(false)
    		.setTitle("Create backup...").setMessage(s)
    		.setPositiveButton("Create",
    			new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,
    						int whichButton) {
    					try {
    						RootTools.sendShell("dd if=" + StaticThings.path() + " of=" + Environment.getExternalStorageDirectory() + "/packages.xml");
    						Toast toast = Toast.makeText(Advanced.this, "Backup Created!", Toast.LENGTH_SHORT);
    						toast.show();
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
    				}
				})
    		.show();
    	}
    }
    
	public void genericError() {
		final SpannableString s = new SpannableString("We could not perform the requested operation! \n\n Please email me about this: StericDroid@gmail.com");
		Linkify.addLinks(s, Linkify.ALL);
		new AlertDialog.Builder(Advanced.this).setCancelable(false)
		.setTitle("OOOOPS!").setMessage(s)
		.setPositiveButton("Ok",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					Advanced.this.finish();
				}
			})
		.show();
	}
	//worker class to change permissions
	private class RestorePermissions extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... permission) {
			try {
				RootTools.sendShell("dd if=" + StaticThings.path() + " of=/data/local/packages1.xml");
				RootTools.sendShell("dd if=" + StaticThings.path() + " of=/data/local/packages.xml");
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
			            if (line.contains("stericson.disabled.")) {
            				String tmp = line.replace("stericson.disabled.", "");
	            			fw.write(tmp + "\n");
	            			line = lnr.readLine();
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
					RootTools.sendShell("dd if=/data/local/packages.xml of=" + StaticThings.path());
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
	
    @Override 
    public void onConfigurationChanged(Configuration newConfig) { 
    super.onConfigurationChanged(newConfig); 
    // We do nothing here. We're only handling this to keep orientation 
    // or keyboard hiding from causing the WebView activity to restart. 
    }
    
}