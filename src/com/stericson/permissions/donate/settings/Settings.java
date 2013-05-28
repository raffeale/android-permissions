package com.stericson.permissions.donate.settings;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.interfaces.Choice;
import com.stericson.permissions.donate.service.PreferenceService;

public class Settings extends PreferenceActivity implements Choice {

    public static final int WIPE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		CheckBoxPreference alwaysLock = (CheckBoxPreference) findPreference("alwaysLock");
		alwaysLock.setChecked(new PreferenceService(this).getAlwaysLock());

		alwaysLock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {            
		    public boolean onPreferenceChange(Preference preference, Object newValue) {
		        if(newValue instanceof Boolean){
		            Boolean boolVal = (Boolean)newValue;
		            
		            if (boolVal)
		            {
		            	AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
		            	alertDialog.setMessage(getString(R.string.lockwarning));
		            	alertDialog.show();
		            }
		            
		            PreferenceService p = new PreferenceService(Settings.this);
	            	p.setAlwaysLocked(boolVal);
		        }
		        return true;
		    }
		}); 
		
		CheckBoxPreference notifyLock = (CheckBoxPreference) findPreference("notifyLock");
		notifyLock.setChecked(new PreferenceService(this).getNotifyLock());

		notifyLock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {            
		    public boolean onPreferenceChange(Preference preference, Object newValue) {
		        if(newValue instanceof Boolean){
		            Boolean boolVal = (Boolean)newValue;

                    PreferenceService p = new PreferenceService(Settings.this);

                    p.setNotifyLocked(boolVal);

                    if (!boolVal) {
                        String ns = Context.NOTIFICATION_SERVICE;
                        NotificationManager mNotificationManager = (NotificationManager) Settings.this.getSystemService(ns);
                        mNotificationManager.cancelAll();
                    } else {
                        Shared.updateStatus(Settings.this);
                    }
		        }
		        return true;
		    }
		});

        Preference wipe = (Preference) findPreference("wipe");

        wipe.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Shared.makeChoice(Settings.this, WIPE, R.string.confirm, R.string.confirm_wipe, R.string.yes, R.string.cancel, Settings.this);
                return false;
            }
        });
	}

    @Override
    public void choiceMade(boolean choice, int id) {
        if (id == WIPE) {
            if (choice) {
                new PreferenceService(this).setLoaded(false).setDeleteDatabase(true);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    @Override
    public void choiceCancelled(int id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}