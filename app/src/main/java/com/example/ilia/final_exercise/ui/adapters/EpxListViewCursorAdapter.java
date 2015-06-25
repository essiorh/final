package com.example.ilia.final_exercise.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.data.model.OpenDBHelper;
import com.example.ilia.final_exercise.ui.interfaces.IActivityAdapterInteraction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilia on 16.06.15.
 * @author ilia
 */
public class EpxListViewCursorAdapter extends CursorTreeAdapter {

	protected final HashMap<Long, Integer> mGroupMap;
	private LayoutInflater mInflater;
	private IActivityAdapterInteraction mListener;

	public EpxListViewCursorAdapter(Cursor cursor, Context context, IActivityAdapterInteraction listener) {
		super(cursor, context);
		mInflater = LayoutInflater.from(context);
		mGroupMap = new HashMap<>();
		mListener = listener;
	}

	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		int groupPos = groupCursor.getPosition();
		long groupId = groupCursor.getLong(groupCursor
				.getColumnIndex(OpenDBHelper.COLUMN_ID));

		mGroupMap.put(groupId, groupPos);

		if (mListener != null) {
			mListener.getChildrenCursor(groupId);
		}

		return null;
	}

	@Override
	protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
		return mInflater.inflate(R.layout.topiclist_explistview_group, parent, false);
	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
		TextView titleTextView = (TextView) view
				.findViewById(R.id.explistview_grpup_title);

		if (titleTextView != null) {
			titleTextView.setText(cursor.getString(cursor
					.getColumnIndex(OpenDBHelper.CATEGORIES_TITLE)));
		}
	}

	@Override
	protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
		return mInflater.inflate(R.layout.topiclist_explistview_cell, parent, false);
	}

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
		TextView titleTextView = (TextView) view
				.findViewById(R.id.explistview_cell_title);

		if (titleTextView != null) {
			titleTextView.setText(cursor.getString(cursor
					.getColumnIndex(OpenDBHelper.ARTICLES_TITLE)));
		}
	}

	public Map<Long, Integer> getGroupMap() {
		return mGroupMap;
	}
}