package com.stericson.permissions.donate.adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.PermissionsActivity;
import com.stericson.permissions.donate.domain.AndroidPackage;
import com.stericson.permissions.donate.domain.Permission;
import com.stericson.permissions.donate.domain.Permissions_Fix;
import com.stericson.permissions.donate.listeners.FixPermissionsListItemClick;
import com.stericson.permissions.donate.listeners.MasterPermissionsListItemClick;
import com.stericson.permissions.donate.listeners.PackagesListItemClick;
import com.stericson.permissions.donate.listeners.PermissionsListItemClick;
import com.stericson.permissions.donate.sorter.Sorter_Fix;
import com.viewpagerindicator.TitleProvider;

import java.util.ArrayList;
import java.util.List;
 
public class PageAdapter extends PagerAdapter implements TitleProvider
{
    private static String[] titles;
    
    private final PermissionsActivity context;
    private List<AndroidPackage> list;
	private List<Permission> permissions;

 
    public PageAdapter(PermissionsActivity context, List<AndroidPackage> list)
    {
        this.context = context;
        this.list = list;

        titles = new String[]
                {
                        context.getString(R.string.pager_packages),
                        context.getString(R.string.pager_permissions),
                        context.getString(R.string.pager_mastercontrol),
                        context.getString(R.string.pager_fixPermissions),
                        context.getString(R.string.pager_faq)
                };
    }
  
    @Override
    public int getCount()
    {
        return titles.length;
    }
 
    @Override
    public Object instantiateItem(View pager, int position)
    {
    	View view = null;
	    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 	    
	    view = inflater.inflate(R.layout.generic_list, null);
	    ((ViewPager) pager).addView(view,0);
	    ListView listView = (ListView) view.findViewById(R.id.list);
	    this.permissions = context.getPermissionsList();

		
	    if (position == 0)
	    {
		    List<AndroidPackage> tmpList = new ArrayList<AndroidPackage>();
		    
		    if (position == 0)
		    {
		    	for (AndroidPackage pk : list)
		    	{
	    			tmpList.add(pk);
		    	}		    	
		    }
		    		    
		    listView.setOnItemClickListener(new PackagesListItemClick(context, list));
	    	
	    	listView.setAdapter(new PackageAdapter(context, R.layout.packages_row, tmpList));
	    	Constants.pDialogClose();
	    }
	    else if (position == 1)
	    {
	    	
		    listView.setOnItemClickListener(new PermissionsListItemClick(context, permissions));
	    	
		    listView.setAdapter(new PermissionsAdapter(context, R.layout.master_permissions_row, permissions));
	    	
		    Constants.pDialogClose();
	    }
	    else if (position == 2)
	    {
	    	
		    listView.setOnItemClickListener(new MasterPermissionsListItemClick(context));
	    	
		    listView.setAdapter(new PermissionsAdapter(context, R.layout.master_permissions_row, permissions));
	    	
		    Constants.pDialogClose();
	    }
	    else if (position == 3)
	    {
	    	ArrayList<Permissions_Fix> packages = new ArrayList<Permissions_Fix>();

	    	PackageManager pm = context.getPackageManager();
	    	for (ApplicationInfo info : pm.getInstalledApplications(0)) {
	    		if ((info.flags & info.FLAG_SYSTEM) != 1) {
	    			packages.add(new Permissions_Fix(info.loadLabel(pm).toString(), info.packageName, info.loadIcon(pm)));
	    		}
	    	}
	    	
	    	java.util.Collections.sort(packages, new Sorter_Fix());

		    listView.setOnItemClickListener(new FixPermissionsListItemClick(context, packages));
	    		    	
	    	listView.setAdapter(new FixPermissionsAdapter(context,
					R.layout.fix_permissions_list_row, packages));
	    	
	    	Constants.pDialogClose();
	    }
	    else if (position == 4)
	    {
	    	listView.setAdapter(new FAQAdapter(context,
					R.layout.fix_permissions_list_row));
	    	
	    	Constants.pDialogClose();
	    }
	    else if (position == 5)
	    {
	    	listView.setAdapter(new FAQAdapter(context,
					R.layout.fix_permissions_list_row));
	    	
	    	Constants.pDialogClose();
	    }
	    
	    return view;
    }
    
    @Override
    public void destroyItem( View pager, int position, Object view )
    {
        //((ViewPager)pager).removeViewAt(position);
    }
 
    @Override
    public boolean isViewFromObject( View view, Object object )
    {
        return view.equals( object );
    }
 
    @Override
    public void finishUpdate( View view ) {}
 
    @Override
    public void restoreState( Parcelable p, ClassLoader c ) {}
 
    @Override
    public Parcelable saveState() {
        return null;
    }
 
    @Override
    public void startUpdate( View view ) {}

	public String getTitle(int position) {
		return titles[ position ];
	}
	
}
