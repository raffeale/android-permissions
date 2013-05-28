package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.widget.TextView;

import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.BaseListActivity;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.JobCallback;
import com.stericson.permissions.donate.jobs.tasks.ChangePermissionTask;

public class ChangePermission extends AsyncJob<Result>
{
    public static final int CHANGE_PERMISSION = 34;
	private Context context;
	private JobCallback jcb;
    private String permission = "";
    private String packageName = "";

	public ChangePermission(BaseListActivity jcb, boolean show, String permission, String packageName)
	{
		super(jcb, R.string.changingPermission, show, false);

        this.packageName = packageName;
        this.permission = permission;
		this.context = jcb;
		this.jcb = jcb;
	}

    public Context getContext() {
        return context;
    }

	@Override
    Result handle()
    {
        return ChangePermissionTask.executeTask(this, permission, packageName);
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
		jcb.jobCallBack(result, CHANGE_PERMISSION);
    }
}
