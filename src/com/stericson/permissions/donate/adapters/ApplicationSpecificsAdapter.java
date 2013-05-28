package com.stericson.permissions.donate.adapters;

import android.content.Context;
import android.graphics.Color;
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

public class ApplicationSpecificsAdapter extends ArrayAdapter<Permission> {

	private int[] colors = new int[] { 0xff303030, 0xff404040  };
	private View v;
	private Context context;
	private ArrayList<Permission> list;
	
	public ApplicationSpecificsAdapter(Context context, int textViewResourceId, ArrayList<Permission> permissions) {
		super(context, textViewResourceId, permissions);
		this.context = context;
		this.list = permissions;
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		v = convertView;
		final Permission o = list.get(position);

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.application_permissions_row, null);

		}
		
		if (o != null) {
			
			TextView permissionName = (TextView) v.findViewById(R.id.permission);
			TextView permissionDescription = (TextView) v.findViewById(R.id.permissionDescription);
			TextView owner = (TextView) v.findViewById(R.id.Owner2);
			ImageView icon = (ImageView) v.findViewById(R.id.packageicon);
			TextView active = (TextView) v.findViewById(R.id.active);
			LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);

			if (permissionName != null) {
				permissionName.setText(o.getPermission());
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
			if (active != null) {
				if (o.isActive()) {
					active.setTextColor(Color.GREEN);
					active.setText(context.getString(R.string.active));
				} else {
					active.setTextColor(Color.RED);
					active.setText(context.getString(R.string.disabled));
				}
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
