package com.stericson.permissions.donate.activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.stericson.RootTools.RootTools;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.Shared;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.JobCallback;
import com.stericson.permissions.donate.jobs.RestorePermissions;
import com.stericson.permissions.donate.service.DBService;
import com.stericson.permissions.donate.settings.Settings;

import java.util.Random;

public class BaseListActivity extends ListActivity implements JobCallback {

	public PopupWindow pw;
	public boolean endApplication;
	protected SharedPreferences sp;

	public Typeface tf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    sp = this.getSharedPreferences(Constants.TAG, this.MODE_PRIVATE);

		tf = Typeface.createFromAsset(getAssets(), "fonts/DJGROSS.ttf");
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        new DBService(this).close();
    }

	/* Creates the menu items */
	public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getString(R.string.lock));
        menu.add(getString(R.string.restorePermissions));
        menu.add(getString(R.string.restoreBackup));
        menu.add(getString(R.string.settings));
        menu.add(getString(R.string.reboot));
		return true;
	}

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.restorePermissions))) {
            new RestorePermissions(this, true).execute();
        }
        if (item.getTitle().equals(getString(R.string.restoreBackup))) {
            Shared.makeBackup(this);
        }
        if (item.getTitle().equals(getString(R.string.reboot))) {
            try
            {
                RootTools.restartAndroid();
            } catch (Exception e) {
                Toast.makeText(this, R.string.rebootFailed, Toast.LENGTH_LONG).show();
            }
        }
        if (item.getTitle().equals(getString(R.string.settings))) {
            Intent intent = new Intent(this, Settings.class);
            this.startActivity(intent);
        }
        if (item.getTitle().equals(getString(R.string.lock))) {
            if (sp.getBoolean("locked", false))
            {
                Shared.unlockPermissions(this, false);
            }
            else
            {
                Shared.lockPermissions(this, false);
            }
        }
        return true;
    }

	
	public void initiatePopupWindow(String text, boolean endApplication,
			Activity context) {
		this.endApplication = endApplication;

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate the view from a predefined XML layout
		View layout = inflater.inflate(R.layout.popupwindow, null);
		pw = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);

		context.findViewById(R.id.pop).post(new Runnable() {
			public void run() {
				pw.showAtLocation(findViewById(R.id.pop), Gravity.CENTER, 0, 0);
			}
		});

		TextView header = (TextView) layout.findViewById(R.id.header_main);
		header.setTypeface(tf);

		TextView textView = (TextView) layout.findViewById(R.id.content);
		textView.setText(text);
	}
	
	public void initiatePopupWindow(CharSequence text, boolean endApplication,
			Activity context) {
		this.endApplication = endApplication;

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate the view from a predefined XML layout
		View layout = inflater.inflate(R.layout.popupwindow, null);
		pw = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.FILL_PARENT);

		context.findViewById(R.id.pop).post(new Runnable() {
			public void run() {
				pw.showAtLocation(findViewById(R.id.pop), Gravity.CENTER, 0, 0);
			}
		});

		TextView header = (TextView) layout.findViewById(R.id.header_main);
		header.setTypeface(tf);

		TextView textView = (TextView) layout.findViewById(R.id.content);
		textView.setText(text);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			if (pw != null && pw.isShowing()) {
				pw.dismiss();
				if (endApplication) {
					finish();
					randomAnimation();
				}
			} else {
				finish();
				randomAnimation();
			}

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void randomAnimation() {
		Random random = new Random();
		switch (random.nextInt(3)) {
		case 0:
			overridePendingTransition(R.anim.enter_scalein,
					R.anim.exit_slideout);
			break;
		case 1:
			overridePendingTransition(R.anim.enter_dropin,
					R.anim.exit_dropout);
			break;
		case 2:
			overridePendingTransition(R.anim.enter_slidein,
					R.anim.exit_slideout);
			break;
		}
	}

	public void close(View v) {
		pw.dismiss();
		if (endApplication) {
			finish();
			randomAnimation();
		}
	}

	public void jobCallBack(Result result, int id) {
		if (id == RestorePermissions.Restore_job)
		{
            Toast.makeText(this, result.isSuccess() ? getString(R.string.permissionsRestored) : getString(R.string.permissionsRestoredFailed), Toast.LENGTH_LONG).show();
		}
	}
}
