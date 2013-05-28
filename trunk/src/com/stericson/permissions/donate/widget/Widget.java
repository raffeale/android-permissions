package com.stericson.permissions.donate.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.Shared;


public class Widget extends AppWidgetProvider {

	public static String ACTION_UPDATE = "UPDATE";

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		for (int i=0; i< appWidgetIds.length; i++) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
			
			//Create an Intent to launch Update
			Intent intent = new Intent(context, Widget.class);
            intent.setAction(ACTION_UPDATE);
            intent.putExtra("ID", appWidgetIds[i]);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetIds[i], intent, PendingIntent.FLAG_UPDATE_CURRENT);

			views.setOnClickPendingIntent(R.id.widget, pendingIntent);
						
			appWidgetManager.updateAppWidget(appWidgetIds[i], views);

		}
	}
	
	public void onReceive(Context context, Intent intent) {
		try {

			final String action = intent.getAction();
			AppWidgetManager awm = AppWidgetManager.getInstance(context);
			RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget);
	
			if (action.equals(ACTION_UPDATE)) {
				int appWidgetId = intent.getExtras().getInt("ID");

				updateWidget(remoteView, context, appWidgetId);
				
				awm.updateAppWidget(appWidgetId, remoteView);
			}
			else
			{			
				SharedPreferences sp = context.getSharedPreferences(Constants.TAG, Context.MODE_PRIVATE);
	
				if (sp.getBoolean("locked", false))
				{
					remoteView.setTextViewText(R.id.text, context.getString(R.string.permissionsLocked));
				}
				else
				{
					remoteView.setTextViewText(R.id.text, context.getString(R.string.permissionsUnlocked));
					
				}
				
				awm.updateAppWidget(new ComponentName(context.getPackageName(), Widget.class.getName()), remoteView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onReceive(context, intent);
	}
	
	private void updateWidget(RemoteViews views, Context context, int appWidgetId) {

		SharedPreferences sp = context.getSharedPreferences(Constants.TAG, Context.MODE_PRIVATE);
		
		if (sp.getBoolean("locked", false))
		{
	    	if (Shared.unlockPermissions(context, false))
            {
			    views.setTextViewText(R.id.text, context.getString(R.string.permissionsUnlocked));
            }
		}
		else
		{
	    	if (Shared.lockPermissions(context, false))
            {
                views.setTextViewText(R.id.text, context.getString(R.string.permissionsLocked));
            }
		}
	}
}
