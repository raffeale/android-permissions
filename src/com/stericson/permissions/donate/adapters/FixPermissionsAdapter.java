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
import com.stericson.permissions.donate.domain.Permissions_Fix;

import java.util.ArrayList;

public class FixPermissionsAdapter extends ArrayAdapter<Permissions_Fix> {

	private int[] colors = new int[] { 0xff303030, 0xff404040  };
	private View v;
	private Context context;
	private ArrayList<Permissions_Fix> packages = new ArrayList<Permissions_Fix>();

	public FixPermissionsAdapter(Context context, int textViewResourceId, ArrayList<Permissions_Fix> packages) {
		super(context, textViewResourceId, packages);
		this.context = context;
		this.packages.addAll(packages);
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		v = convertView;
		final Permissions_Fix o = packages.get(position);

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fix_permissions_list_row, null);

		}
		
		if (o != null) {
			
			TextView appName = (TextView) v.findViewById(R.id.appname);
			TextView packageName = (TextView) v.findViewById(R.id.packagename);
			ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
			
			LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

			if (appName != null) {
				appName.setText(o.appName);
			}
			if (packageName != null) {
				packageName.setText(o.packageName);
			}
			if (icon != null) {
				icon.setImageDrawable(o.icon);
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
