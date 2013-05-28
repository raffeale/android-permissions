package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.content.SharedPreferences;

import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.tasks.InitialChecksTask;

public class InitialChecks extends AsyncJob<Result>
{
    public static final int INITIALCHECKS = 0;
	private PermissionsActivity activity;
	private SharedPreferences sp;

	public InitialChecks(PermissionsActivity activity)
	{
		super(activity, R.string.loading, true, false);
		
	    sp = activity.getSharedPreferences(Constants.TAG, activity.MODE_PRIVATE);

		this.activity = activity;

        Constants.storagePath = activity.getFilesDir().toString();
	}

    public Context getContext() {
        return context;
    }

	@Override
    Result handle()
    {
        return InitialChecksTask.executeTask(this);
    }

    public void publishJobProgress(Object... values) {
        super.publishProgress(values);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
    }
    
	@Override
    void callback(Result result)
    {
	    activity.jobCallBack(result, INITIALCHECKS);
    }
}
