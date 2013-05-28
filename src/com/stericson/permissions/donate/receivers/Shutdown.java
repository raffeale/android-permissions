package com.stericson.permissions.donate.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.service.PreferenceService;

public class Shutdown extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    	PreferenceService p = new PreferenceService(context);

        p.setNeedsReboot(false);

        if (p.getAlwaysLock())
        {
            Shared.lockPermissions(context, false);
        }
    }
}