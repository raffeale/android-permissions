package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.widget.TextView;
import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.BaseActivity;
import com.stericson.permissions.donate.activities.BaseListActivity;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.JobCallback;
import com.stericson.permissions.donate.jobs.tasks.RestorePermissionsTask;

public class RestorePermissions extends AsyncJob<Result>
{
	private Context context;
	private JobCallback jcb;
	public static int Restore_job = 125;
	
	public RestorePermissions(BaseListActivity jcb, boolean show)
	{
		super(jcb, R.string.restoringpermissions, show, false);
		
		this.context = jcb;
		this.jcb = jcb;
	}
	
	public RestorePermissions(BaseActivity jcb, boolean show)
	{
		super(jcb, R.string.restoringpermissions, show, false);
		
		this.context = jcb;
		this.jcb = jcb;
	}

    public Context getContext() {
        return context;
    }

	@Override
    Result handle()
    {
        return RestorePermissionsTask.executeTask(this);
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
		jcb.jobCallBack(result, Restore_job);
    }
}
