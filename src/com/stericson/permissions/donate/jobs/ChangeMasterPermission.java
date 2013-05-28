package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.widget.TextView;
import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.tasks.ChangeMasterPermissionTask;

public class ChangeMasterPermission extends AsyncJob<Result>
{
    public static final int CHANGE_MASTER_PERMISSIONS = 3;
	private PermissionsActivity activity;
	private boolean disable;
	private String permission;

	public ChangeMasterPermission(PermissionsActivity activity, boolean disable, String permission)
	{
		super(activity, R.string.changingPermission, true, false);

		this.activity = activity;
		this.disable = disable;
		this.permission = permission;
	}

    public Context getContext() {
        return context;
    }

	@Override
    Result handle()
    {
		return ChangeMasterPermissionTask.executeTask(this, permission, disable);
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
	    activity.jobCallBack(result, CHANGE_MASTER_PERMISSIONS);
    }
}
