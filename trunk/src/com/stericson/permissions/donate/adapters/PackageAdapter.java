package com.stericson.permissions.donate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.domain.AndroidPackage;

import java.util.List;

public class PackageAdapter extends ArrayAdapter<AndroidPackage> {

	private int[] colors = new int[] { 0xff303030, 0xff404040  };
	private View v;
	private Context context;
	int index = 0;
	private List<AndroidPackage> list;

	public PackageAdapter(Context context, int textViewResourceId, List<AndroidPackage> packages) {
		super(context, textViewResourceId, packages);
		this.context = context;
		this.list = packages;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		v = convertView;
		final AndroidPackage o = list.get(position);

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.packages_row, null);

		}
		
		if (o != null) {
			
			TextView appName = (TextView) v.findViewById(R.id.appname);
			TextView packageName = (TextView) v.findViewById(R.id.packagename);
			TextView found = (TextView) v.findViewById(R.id.found);
			TextView active = (TextView) v.findViewById(R.id.active2);
			TextView denied = (TextView) v.findViewById(R.id.disabled2);
			TextView type = (TextView) v.findViewById(R.id.type);
			TextView warning = (TextView) v.findViewById(R.id.warning);
			ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
			
			LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

			if (o.isShared())
			{
				type.setText(" " + o.getType() + " (" + o.getUserID() + ")");
				warning.setVisibility(View.VISIBLE);
				warning.setText(context.getString(R.string.affectsall));

				try {
					icon.setImageDrawable(o.getIcons().get(0));
				}
				catch (Exception e)
				{
					icon.setImageResource(R.drawable.caution);
				}
				
				String apps = "";
				for (String name: o.getAppNames())
				{
					apps += name + "\n";
				}
				appName.setText(apps.substring(0, (apps.length() > 0 ? apps.length() - 1 : apps.length())));
			}
			else
			{
				warning.setVisibility(View.GONE);
				type.setText(" " + o.getType());
				
				//we have to add some things....
				if (appName != null) {
					appName.setText(o.getAppName());
				}
				if (icon != null) {
					icon.setImageDrawable(o.getIcon());
				}					
			}
			
			if (packageName != null) {
				packageName.setText(o.getPackageName());
			}
			if (found != null) {
				found.setText(context.getString(R.string.permissionsFound) + " " + o.getPermissionCount());
			}
			if (active != null) {
				active.setText(Integer.toString(o.getActiveCount()));
			}
			if (denied != null) {
				denied.setText(Integer.toString(o.getDeniedCount()));
			}
			if (position % 2 == 0) {
				row.setBackgroundColor(colors[position % 2]);
			} else {
				row.setBackgroundColor(colors[position % 2]);
			}

		}
		return (v);
	}
}
