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
import com.stericson.permissions.donate.domain.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionsAdapter extends ArrayAdapter<Permission> {

	private int[] colors = new int[] { 0xff303030, 0xff404040  };
	private View v;
	private Context context;
	private List<Permission> list = new ArrayList<Permission>();


	public PermissionsAdapter(Context context, int textViewResourceId, List<Permission> permissions) {
		super(context, textViewResourceId, permissions);
		this.context = context;
		list = permissions;
	}

	@Override
	public View getView(int position, View convertView,
			ViewGroup parent) {
		v = convertView;
		Permission o = list.get(position);

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.master_permissions_row, null);

		}
		
		if (o != null) {
			
			TextView permission = (TextView) v.findViewById(R.id.permission);
			TextView permissionDescription = (TextView) v.findViewById(R.id.permissionDescription);
			TextView owner = (TextView) v.findViewById(R.id.Owner2);
			ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
			LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

			if (permission != null) {
				permission.setText(o.getPermission());
			}
			if (permissionDescription != null) {
				permissionDescription.setText(o.getPermissionDescription());
			}
			if (owner != null) {
				owner.setText(o.getOwner());
			}
			if (icon != null) {
				icon.setImageDrawable(o.getIcon());
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
