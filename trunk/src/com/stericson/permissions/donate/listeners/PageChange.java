package com.stericson.permissions.donate.listeners;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.activities.PermissionsActivity;

public class PageChange implements OnPageChangeListener {

	private PermissionsActivity context;
	private ViewPager view;
	
	public PageChange(PermissionsActivity context, ViewPager view)
	{
		this.context = context;
		this.view = view;
		
		TextView tv = (TextView) context.findViewById(R.id.info);
		tv.setText(context.getString(R.string.pager_changePermission));

	}

	public void onPageScrollStateChanged(int arg0) {
		
	}

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	public void onPageSelected(int position) {
		TextView tv = (TextView) context.findViewById(R.id.info);
		if (position == 0)
		{
			tv.setText(context.getString(R.string.pager_changePermission));
		}
		else if (position == 1)
		{
			tv.setText(context.getString(R.string.pager_seePermission));
		}
		else if (position == 2)
		{
			tv.setText(context.getString(R.string.pager_masterpermissions));
		}
		else if (position == 3)
		{
			tv.setText(context.getString(R.string.pager_permissionsfix));
		}
		else if (position == 4)
		{
			tv.setText(context.getString(R.string.pager_faq_title));
		}
	}

}
