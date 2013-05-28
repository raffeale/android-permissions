package com.stericson.permissions.donate.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.Shared;

public class PreferenceService
{	
	private SharedPreferences sharedPreferences = null;
	private Editor editor = null;
	private Context context;
	
	public PreferenceService(Context context)
	{
		sharedPreferences = context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE);
		this.context = context;
	}


    public void commit()
    {
        getEditor().commit();
    }

    public boolean getAlwaysLock()
    {
        return context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE).getBoolean("alwaysLock", false);
    }

    public boolean getDeleteDatabase()
    {
        return context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE).getBoolean("deletedb", false);
    }

    private Editor getEditor()
    {
        if (editor == null)
            editor = sharedPreferences.edit();
        return editor;
    }

    public boolean getNotifyLock()
    {
        return context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE).getBoolean("notifyLock", true);
    }

    public boolean getNeedsReboot()
    {
        return context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE).getBoolean("needsReboot", true);
    }

    public boolean isLoaded()
    {
        return context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE).getBoolean("loaded", false);
    }

    public boolean isLocked()
    {
        return context.getSharedPreferences(Constants.TAG, context.MODE_PRIVATE).getBoolean("locked", false);
    }

    public PreferenceService setAlwaysLocked(boolean locked)
    {
        sharedPreferences.edit().putBoolean("alwaysLock", locked).commit();
        return this;
    }

    public PreferenceService setDeleteDatabase(boolean delete)
    {
        sharedPreferences.edit().putBoolean("deletedb", delete).commit();
        return this;
    }

    public PreferenceService setLoaded(boolean loaded)
    {
        sharedPreferences.edit().putBoolean("loaded", loaded).commit();
        return this;
    }

    public PreferenceService setLocked(boolean locked)
    {
        sharedPreferences.edit().putBoolean("locked", locked).commit();
        Shared.updateWidget(context);
        Shared.updateStatus(context);
        return this;
    }

    public PreferenceService setNotifyLocked(boolean notify)
    {
        sharedPreferences.edit().putBoolean("notifyLock", notify).commit();
        return this;
    }

    public PreferenceService setNeedsReboot(boolean needsReboot)
    {
        sharedPreferences.edit().putBoolean("needsReboot", needsReboot).commit();
        return this;
    }
}
