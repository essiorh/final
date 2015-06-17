package com.example.ilia.final_exercise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.models.ArticleItem;
import com.example.ilia.final_exercise.models.GroupItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilia on 16.06.15.
 */
public class ListExpandableAdapter extends BaseExpandableListAdapter {

    private List<GroupItem> groupItemList = new ArrayList<>();
    private List<ArticleItem> articleItemList = new ArrayList<>();
    private List<List<ArticleItem>> arrayGroupsAndArticles=new ArrayList<>();
    private LayoutInflater inflater;
    private Context mContext;

    public ListExpandableAdapter(Context context,List<GroupItem> groups,List<ArticleItem> articles) {
        mContext = context;
        groupItemList = groups;
        articleItemList=articles;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        connectGroupsWithArticles(groups, articles);
    }

    @Override
    public int getGroupCount() {
        return groupItemList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //добавить логику для вложености
        return arrayGroupsAndArticles.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupItemList.get(groupPosition);
    }

    @Override
    public ArticleItem getChild(int groupPosition, int childPosition) {
        //добавить логику для вложенности
        return arrayGroupsAndArticles.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        //добавить логику для вложенности
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        TextView textGroup = (TextView) convertView.findViewById(R.id.textGroup);
        textGroup.setText(groupItemList.get(groupPosition).getmTitle());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_view, null);
        }
        TextView textChild = (TextView) convertView.findViewById(R.id.textChild);
        textChild.setText(arrayGroupsAndArticles.get(groupPosition).get(childPosition).getmTitle());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void connectGroupsWithArticles(List<GroupItem> groups, List<ArticleItem> articles) {
        for (int i=0;i<groups.size();i++) {
            List<ArticleItem> currentList=new ArrayList<>();
            for (int j=0;j<articles.size();j++) {
                if (groups.get(i).get_id()==articles.get(j).getmCategory_id()) {
                    currentList.add(articles.get(j));
                }
            }
            if (currentList.size()>0) {
                arrayGroupsAndArticles.add(currentList);
            }
        }
    }
}
