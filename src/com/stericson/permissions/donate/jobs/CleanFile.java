package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.widget.TextView;

import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.tasks.CleanFileTask;

public class CleanFile extends AsyncJob<Result>
{
    public static final int CLEANFILE = 1;
	private PermissionsActivity context;

	public CleanFile(PermissionsActivity context)
	{
		super(context, R.string.loadingPackages, true, false);

		this.context = context;
	}

    public Context getContext() {
        return context;
    }

    @Override
    Result handle()
    {
        return CleanFileTask.executeTask(this);
    }

    public void publishJobProgress(Object... values) {
        super.publishProgress(values);
    }

	@Override
    protected void onProgressUpdate(Object... values) {
		super.onProgressUpdate(values);
		TextView header = (TextView) App.getInstance().getPopupView().findViewById(R.id.header);
		header.setText((String) values[0]);
    }
    
	@Override
    void callback(Result result)
    {
	    context.jobCallBack(result, CLEANFILE);
    }
}
