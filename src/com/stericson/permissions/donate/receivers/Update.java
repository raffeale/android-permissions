package com.stericson.permissions.donate.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.stericson.permissions.donate.service.PreferenceService;

public class Update extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        PreferenceService ps = new PreferenceService(context);

        //Don't do this for own own installation or update
        Uri uri = intent.getData();
        String pkg = uri != null ? uri.getSchemeSpecificPart() : null;

        if (pkg != null && !pkg.contains("com.stericson.permissions"))
        {
            //wipe the DB, start clean and rebuild everything as something changed.
            ps.setLoaded(false).setDeleteDatabase(true);
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }
}