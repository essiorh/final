package com.example.ilia.final_exercise.fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.adapters.ListCustomAdapter;
import com.example.ilia.final_exercise.adapters.ListExpandableAdapter;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.models.ArticleItem;
import com.example.ilia.final_exercise.models.GroupItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilia on 08.06.15.
 */
public class ListFragment extends Fragment implements Spinner.OnItemSelectedListener
        , ExpandableListView.OnChildClickListener{
    private ExpandableListView expandableListView;
    private ListView customListView;
    private Spinner mSpinner;
    private ListExpandableAdapter expandableAdapter;
    private ListAdapter customAdapter;
    private TextView mTextView;
    private List<GroupItem> groupItemList = new ArrayList<>();
    private List<ArticleItem> articleItemList = new ArrayList<>();
    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout inflateView = (LinearLayout) inflater.inflate(R.layout.list_fragment, container, false);
        expandableListView = (ExpandableListView) inflateView.findViewById(R.id.exp_list);
        mSpinner = (Spinner) inflateView.findViewById(R.id.list_spinner);
        customListView = (ListView) inflateView.findViewById(R.id.def_list);

        /*mTextView = (TextView) inflateView.findViewById(R.id.filter);
        mTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //expandableAdapter.getFilter().filter(s);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void afterTextChanged(Editable s) { }
        });
*/
        vremenka();
        expandableAdapter= new ListExpandableAdapter(getActivity(), groupItemList,articleItemList);
        expandableListView.setAdapter(expandableAdapter);
        expandableListView.setOnChildClickListener(this);
        //expandableListView.setOnGroupClickListener(this);

        customAdapter = new ListCustomAdapter(getActivity(), articleItemList);

        customListView.setAdapter(customAdapter);
        mSpinner.setOnItemSelectedListener(this);
        //customListView.setOnItemClickListener(this);

        //registerForContextMenu(customListView);
        //registerForContextMenu(expandableListView);
        return inflateView;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        IClickListener listener = (IClickListener) getActivity();
        ArticleItem articleItem=expandableAdapter.getChild(groupPosition,childPosition);
        listener.getArticleToAnotherFragment(articleItem);

        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.list_spinner:
                if (position == 1) {
                    customListView.setVisibility(View.VISIBLE);
                    expandableListView.setVisibility(View.GONE);
                } else {
                    customListView.setVisibility(View.GONE);
                    expandableListView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override public void onNothingSelected(AdapterView<?> parent) { }

    private void vremenka() {
        groupItemList = new ArrayList<>();
        groupItemList.add(new GroupItem(0,"Hello 1"));
        groupItemList.add(new GroupItem(1,"Hello 2"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        groupItemList.add(new GroupItem(2,"Hello 3"));
        articleItemList=new ArrayList<>();
        articleItemList.add(new ArticleItem(0,"Hello 11","superdiscription",true,0,"2015","2015"));
        articleItemList.add(new ArticleItem(1,"Hello 12","superdiscription",true,0,"2015","2015"));
        articleItemList.add(new ArticleItem(2,"Hello 21","superdiscription",true,1,"2015","2015"));
        articleItemList.add(new ArticleItem(3,"Hello 22","superdiscription",true,1,"2015","2015"));
        articleItemList.add(new ArticleItem(4,"Hello 31","superdiscription",true,2,"2015","2015"));
        articleItemList.add(new ArticleItem(5,"Hello 32","superdiscription",true,2,"2015","2015"));
        articleItemList.add(new ArticleItem(5,"Hello 32","superdiscription",true,2,"2015","2015"));
        articleItemList.add(new ArticleItem(5,"Hello 32","superdiscription",true,2,"2015","2015"));
        articleItemList.add(new ArticleItem(5,"Hello 32","superdiscription",true,2,"2015","2015"));
        articleItemList.add(new ArticleItem(5,"Hello 32","superdiscription",true,2,"2015","2015"));
        articleItemList.add(new ArticleItem(5,"Hello 32","superdiscription",true,2,"2015","2015"));
    }
}

  /*      implements
        CheckBox.OnClickListener, ListView.OnItemClickListener,
        Spinner.OnItemSelectedListener,
        ExpandableListView.OnGroupClickListener, ISetCurrentItem {


    private ExpandableListView expandableListView;
    private ListView defaultListView;
    private Spinner mSpinner;
    private         ExpListAdapter adapter;
    private         ListAdapter mListAdapter;
    private         ArrayList<ArrayList<ItemContainer>> groups;
    private         TextView mTextView;
    private         CheckBox checkBoxFavorite;

    public ListFragment() {
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.list_spinner:
                if (position == 1) {
                    defaultListView.setVisibility(View.VISIBLE);
                    expandableListView.setVisibility(View.GONE);
                } else {
                    defaultListView.setVisibility(View.GONE);
                    expandableListView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
        ItemContainer itemContainer =(ItemContainer) this.adapter.getChild(groupPos, childPos);
        //ItemContainer itemContainer1Default=(ItemContainer)this.mListAdapter
        adapter.deleteExpandableElement(itemContainer);
        adapter.notifyDataSetChanged();
        //String[] menuItems = getResources().getStringArray(R.array.menu);
        //String menuItemName = menuItems[menuItemIndex];
        //String listItemName = Countries[info.position];

        //TextView text = (TextView)findViewById(R.id.footer);
        //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));
        return true;
    }



    @Override
    public void setCurrentItem(int currentItem) {
        defaultListView.setSelection(currentItem);
        for (int i=0;i<groups.size();i++) {
            if (adapter.getOffset(i)>currentItem) {
                expandableListView.setSelectedChild(i - 1, currentItem - adapter.getOffset(i-1),false);
                break;
            }
        }
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        IConnectFragmentWithActivity listener = (IConnectFragmentWithActivity) getActivity();
        int off= adapter.getOffset(groupPosition);
        int pos=off+childPosition;
        listener.onItemSelected(pos);

        CheckBox checkBox=(CheckBox)v.findViewById(R.id.checkFavorite);
        checkBox.setChecked(!checkBox.isChecked());

        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        IConnectFragmentWithActivity listener = (IConnectFragmentWithActivity) getActivity();
        int off= adapter.getOffset(groupPosition);
        listener.onItemSelected(off);
        return false;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("mylog", "itemClick: position = " + position + ", id = "
                + id);

        IConnectFragmentWithActivity listener = (IConnectFragmentWithActivity) getActivity();
        int off = position;
        listener.onItemSelected(off);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.filter_favorite:
            adapter.checkFavorite(((CheckBox) v).isChecked());
                ICheckFavorite iCheckFavorite= (ICheckFavorite) mListAdapter;
                iCheckFavorite.checkFavorite(((CheckBox) v).isChecked());
                break;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}


    private int getIdResource(int counter) {
        switch (counter){
            case 0: return R.drawable.applepie;
            case 1: return R.drawable.bananabread;
            case 2: return R.drawable.cupcake;
            case 3: return R.drawable.donut;
            case 4: return R.drawable.eclair;
            case 5: return R.drawable.froyo;
            case 6: return R.drawable.gingerbread;
            case 7: return R.drawable.honeycomb;
            case 8: return R.drawable.icecreamsandwich;
            case 9: return R.drawable.jellybean;
            case 10: return R.drawable.kitkat;
            case 11: return R.drawable.lollipop;
            case 12: return R.drawable.lollipop;
            default: return R.drawable.abc_btn_default_mtrl_shape;
        }
    }


}
*/