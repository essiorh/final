package com.example.ilia.final_exercise.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.interfaces.IActivityAdapterInteraction;

import java.util.HashMap;
import java.util.Map;

import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_TITLE;

/**
 * Created by ilia on 16.06.15.
 */
public class ListExpandableAdapter extends CursorTreeAdapter {

        protected final HashMap<Long, Integer> mGroupMap;
        private LayoutInflater mInflater;
        private IActivityAdapterInteraction mListener;

        public ListExpandableAdapter(Cursor cursor, Context context, IActivityAdapterInteraction listener) {
            super(cursor, context);
            mInflater = LayoutInflater.from(context);
            mGroupMap = new HashMap<Long, Integer>();
            mListener = listener;
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // Given the group, we return a cursor for all the children within that
            // group
            int groupPos = groupCursor.getPosition();
            long groupId = groupCursor.getLong(groupCursor
                    .getColumnIndex(COLUMN_ID));

            mGroupMap.put(groupId, groupPos);

            if (mListener != null) {
                mListener.getChildrenCursor(groupId);
            }

            return null;
        }

        @Override
        protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.group_view, parent, false);
            return view;
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            TextView titleTextView = (TextView) view
                    .findViewById(R.id.textGroup);

            if (titleTextView != null) {
                titleTextView.setText(cursor.getString(cursor
                        .getColumnIndex(COLUMN_TITLE)));
            }
        }

        @Override
        protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.child_view, parent, false);

            return view;
        }

        @Override
        protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
            TextView titleTextView = (TextView) view
                    .findViewById(R.id.textChild);

            if (titleTextView != null) {
                titleTextView.setText(cursor.getString(cursor
                        .getColumnIndex(COLUMN_TITLE)));
            }
        }

        public Map<Long, Integer> getGroupMap() {
            return mGroupMap;
        }
    }