package com.stericson.permissions.donate.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.stericson.permissions.donate.Constants;
import com.stericson.permissions.donate.R;
import com.stericson.permissions.donate.domain.Permissions_Fix;

import java.util.ArrayList;
import java.util.Arrays;

public class FAQAdapter extends ArrayAdapter<Permissions_Fix> {

	private int[] colors = new int[] { 0xff303030, 0xff404040  };
	private View v;
	private Context context;
    private String[] questions = null;
    private String[] answers = null;

	public FAQAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId, new ArrayList(Arrays.asList(context.getResources().getStringArray(R.array.questions))));
		this.context = context;
        this.questions = context.getResources().getStringArray(R.array.questions);
        this.answers = context.getResources().getStringArray(R.array.answers);
	}

	@Override
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.faq_row, null);

		}
		
		LinearLayout row = (LinearLayout) v.findViewById(R.id.rowMain);
		TextView question = (TextView) v.findViewById(R.id.question);
		TextView answer = (TextView) v.findViewById(R.id.answer);
		
		question.setText(questions[position]);
		answer.setText(answers[position]);

		if (position % 2 == 0) {
			row.setBackgroundColor(colors[position % 2]);
		} else {
			row.setBackgroundColor(colors[position % 2]);
		}
		
		return (v);
	}
}
