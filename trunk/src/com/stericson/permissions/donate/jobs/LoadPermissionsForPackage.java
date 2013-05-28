package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.widget.TextView;

import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.BaseListActivity;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.JobCallback;
import com.stericson.permissions.donate.jobs.tasks.LoadPermissionsForPackageTask;

public class LoadPermissionsForPackage extends AsyncJob<Result>
{
    public static final int LOAD_PERMISSIONS_PACKAGE = 35;
	private Context context;
	private JobCallback jcb;
    private String packageName = "";

	public LoadPermissionsForPackage(BaseListActivity jcb, boolean show, String packageName)
	{
		super(jcb, R.string.loadingPermissions, show, false);

        this.packageName = packageName;
		this.context = jcb;
		this.jcb = jcb;
	}

    public Context getContext() {
        return context;
    }

	@Override
    Result handle()
    {
        return new LoadPermissionsForPackageTask(this, packageName).executeTask();
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
		jcb.jobCallBack(result, LOAD_PERMISSIONS_PACKAGE);
    }
}
