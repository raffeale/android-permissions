package com.stericson.permissions.donate.jobs;

import android.content.Context;
import android.widget.TextView;
import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.BaseListActivity;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.interfaces.JobCallback;
import com.stericson.permissions.donate.jobs.tasks.LoadPackagesTask;

public class LoadPackages extends AsyncJob<Result>
{
    public static final int LOAD_PACKAGES = 21;
	private Context context;
	private JobCallback jcb;
    private Permission permission;

	public LoadPackages(BaseListActivity jcb, boolean show, Permission permission)
	{
		super(jcb, R.string.loadingPermissions, show, false);

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
        return new LoadPackagesTask(this, permission).executeTask();
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
		jcb.jobCallBack(result, LOAD_PACKAGES);
    }
}
