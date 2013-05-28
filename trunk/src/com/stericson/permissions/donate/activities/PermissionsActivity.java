package com.stericson.permissions.donate.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.permissions.donate.App;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.adapters.PageAdapter;
import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Result;
import com.stericson.permissions.donate.jobs.ChangeMasterPermission;
import com.stericson.permissions.donate.jobs.CleanFile;
import com.stericson.permissions.donate.jobs.InitialChecks;
import com.stericson.permissions.donate.jobs.LoadAllPermissions;
import com.stericson.permissions.donate.jobs.ReadFixandCheck;
import com.stericson.permissions.donate.listeners.PageChange;
import com.stericson.permissions.donate.service.PreferenceService;
import com.stericson.permissions.donate.sorter.Sorter_Packages;
import com.stericson.permissions.donate.sorter.Sorter_Packages_Type;
import com.stericson.permissions.donate.sorter.Sorter_Permissions;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PermissionsActivity extends BaseActivity {

	private List<AndroidPackage> list = null;
	private List<Permission> permissions = null;
	private ViewPager pager;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
	    super.onCreate( savedInstanceState );
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.main);

        if (!sp.contains("locked"))
	    {
			new PreferenceService(this).setLocked(false);
	    }

	    TextView header = (TextView) findViewById(R.id.header_main);
		header.setTypeface(tf);

        new InitialChecks(this).execute();
	}

    public List<Permission> getPermissionsList()
    {
        return this.permissions;
    }

    public void initiatePager()
    {
        if (permissions != null && list != null && pager == null)
        {
            pager = (ViewPager)findViewById(R.id.viewpager);
            TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);

            PageAdapter adapter = new PageAdapter(PermissionsActivity.this, list);
            indicator.setOnPageChangeListener(new PageChange(PermissionsActivity.this, pager));
            pager.setAdapter(adapter);
            indicator.setViewPager(pager);
        }
    }

    @SuppressWarnings("unchecked")
    public void jobCallBack(Result result, int id)
    {
        if (id == InitialChecks.INITIALCHECKS) {
            if (result.isSuccess()) {
                //Clean the file....
                new CleanFile(this).execute();
            } else {
                this.initiatePopupWindow(result.getError(), true, this);
            }
        } else if (id == CleanFile.CLEANFILE) {
            new LoadAllPermissions(this, false).execute();
            new ReadFixandCheck(this, true).execute();
        } else if (id == ChangeMasterPermission.CHANGE_MASTER_PERMISSIONS)
        {
            Toast.makeText(this, getString(R.string.afterReboot), Toast.LENGTH_LONG).show();
        }
        else if (id == LoadAllPermissions.LOAD_PERMISSIONS)
        {
            this.permissions = (ArrayList<Permission>) result.getList();

            java.util.Collections.sort(permissions, new Sorter_Permissions());

            initiatePager();
        } else if (id == ReadFixandCheck.READFIXANDCHECK)
        {
            //if there is a message, let the user know
            if (!result.getMessage().isEmpty()) {
                this.initiatePopupWindow(result.getMessage(), false, this);
            }

            list = new ArrayList<AndroidPackage>();
            this.list.addAll((Collection<? extends AndroidPackage>) result.getList());
            App.getInstance().setList(list);

            //Organize the list
            java.util.Collections.sort(list, new Sorter_Packages());
            java.util.Collections.sort(list, new Sorter_Packages_Type());

            new PreferenceService(this).setLoaded(true);

            initiatePager();

            if (!sp.contains("remember2"))
            {
                initiatePopupWindow(PermissionsActivity.this.getText(R.string.rememberrestart), false, PermissionsActivity.this);
                sp.edit().putBoolean("remember2", true).commit();
            }
        }

        super.jobCallBack(result, id);
    }

	@Override
    public void onConfigurationChanged(Configuration newConfig) { 
    super.onConfigurationChanged(newConfig); 
    // We do nothing here. We're only handling this to keep orientation 
    // or keyboard hiding from causing the WebView activity to restart. 
    }
}
