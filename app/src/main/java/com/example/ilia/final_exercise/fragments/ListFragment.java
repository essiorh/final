package com.example.ilia.final_exercise.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.activities.MainActivity;
import com.example.ilia.final_exercise.adapters.ListExpandableAdapter;
import com.example.ilia.final_exercise.database.AppContentProvider;
import com.example.ilia.final_exercise.database.AppSQLiteOpenHelper;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.interfaces.IStateItemChange;
import com.example.ilia.final_exercise.database.ArticleItem;
import com.example.ilia.final_exercise.database.GroupItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ilia.final_exercise.database.AppContentProvider.*;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.*;

/**
 * Created by ilia on 08.06.15.
 */
public class ListFragment extends Fragment implements Spinner.OnItemSelectedListener,
        ExpandableListView.OnChildClickListener, ListView.OnItemClickListener,
        IStateItemChange, LoaderManager.LoaderCallbacks<Cursor> {
    private ExpandableListView expandableListView;
    private ListView customListView;
    private Spinner mSpinner;
    private ListExpandableAdapter expandableAdapter;
    //private ListCustomAdapter customAdapter;
    private TextView mTextView;
    private List<GroupItem> groupItemList = new ArrayList<>();
    private List<ArticleItem> articleItemList = new ArrayList<>();
    private SimpleCursorAdapter cursorAdapter;

    // json array response url
    private String urlJsonArry = "http://api.androidhive.info/volley/person_array.json";
    // temporary string to show the parsed response
    private String jsonResponse;

    private static String TAG = MainActivity.class.getSimpleName();

    private final static String urlJsonArray = "http://editors.yozhik.sibext.ru/articles.json";
    private final static String apiKey="bdf6064c9b5a4011ee2f36b082bb4e5d";

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

        expandableAdapter = new ListExpandableAdapter(getActivity(), groupItemList, articleItemList);
        expandableListView.setAdapter(expandableAdapter);
        expandableListView.setOnChildClickListener(this);
        //expandableListView.setOnGroupClickListener(this);

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{AppSQLiteOpenHelper.COLUMN_TITLE};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.textChild};

        //customAdapter = new ListCustomAdapter(getActivity(), articleItemList);

        getLoaderManager().initLoader(0, null, this);

        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.child_view, null, from, to, 0);
        customListView.setAdapter(cursorAdapter);
        request();
        mSpinner.setOnItemSelectedListener(this);
        customListView.setOnItemClickListener(this);

        //registerForContextMenu(customListView);
        //registerForContextMenu(expandableListView);
        return inflateView;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        IClickListener listener = (IClickListener) getActivity();
        ArticleItem articleItem = expandableAdapter.getChild(groupPosition, childPosition);
        //listener.getArticleToAnotherFragment(articleItem);

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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void request() {
        getActivity().getContentResolver().delete(CONTENT_URI_ARTICLES,null,null);
        StringRequest req = new StringRequest(urlJsonArray, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonResponse = "";
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArrays= jsonObject.getJSONArray("articles");
                    for (int i = 0; i < jsonArrays.length(); i++) {

                        JSONObject articles = (JSONObject) jsonArrays.get(i);

                        int id = articles.getInt("id");
                        String title = articles.getString("title");
                        String description = articles.getString("description");
                                    /*JSONObject phone = categories
                                            .getJSONObject("phone");
                                    String home = phone.getString("home");
                                    String mobile = phone.getString("mobile");*/

                        jsonResponse += "id: " + id + "\n\n";
                        jsonResponse += "title: " + title + "\n\n";
                        ContentValues values = new ContentValues();
                        values.put(COLUMN_TITLE, 		title);
                        values.put(ARTICLES_COLUMN_DESCRIPTION, 	description);
                        values.put(ARTICLES_COLUMN_CATEGORY_ID, 	0);

                        getActivity().getContentResolver().insert(CONTENT_URI_ARTICLES, values);
                    }


                    VolleyLog.v("Response:%n %s", response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("Authorization","Token token="+ apiKey);
                return params;
            }
        };


        AppController.getInstance().addToRequestQueue(req);
        /*groupItemList = new ArrayList<>();
        groupItemList.add(new GroupItem(0, "Hello 1"));
        groupItemList.add(new GroupItem(1, "Hello 1"));
        groupItemList.add(new GroupItem(2, "Hello 1"));

        articleItemList = new ArrayList<>();
        articleItemList.add(new ArticleItem(0, "Hello 00", "superdiscription", true, 0, "2015", "2015", false, null));
        articleItemList.add(new ArticleItem(1, "Hello 01", "superdiscription", true, 0, "2015", "2015", false, null));
        articleItemList.add(new ArticleItem(2, "Hello 10", "superdiscription", true, 1, "2015", "2015", false, null));
        articleItemList.add(new ArticleItem(3, "Hello 11", "superdiscription", true, 1, "2015", "2015", false, null));
        articleItemList.add(new ArticleItem(4, "Hello 20", "superdiscription", true, 2, "2015", "2015", false, null));
        articleItemList.add(new ArticleItem(5, "Hello 21", "superdiscription", true, 2, "2015", "2015", false, null));
    */

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IClickListener listener = (IClickListener) getActivity();
        Uri uri = Uri.parse(CONTENT_URI_ARTICLES + "/"
                + id);
        //ArticleItem articleItem = cursorAdapter.getItem(position);
        listener.getArticleToAnotherFragment(uri);

    }

    @Override
    public void deleteArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void updateArticleItem(ArticleItem articleItem) {
        /*for(int i=0;i<articleItemList.size();i++) {
            if (customAdapter.getItem(i).get_id()==articleItem.get_id()) {
                articleItemList.get(i).setmCategory_id(articleItem.getmCategory_id());
                articleItemList.get(i).setmPublished(articleItem.ismPublished());
                articleItemList.get(i).setmCreate_at(articleItem.getmCreate_at());
                articleItemList.get(i).setmDescription(articleItem.getmDescription());
                articleItemList.get(i).setmOwn(articleItem.getmOwn());
                articleItemList.get(i).setmPhoto(articleItem.getmPhoto());
                articleItemList.get(i).setmTitle(articleItem.getmTitle());
                articleItemList.get(i).setmUpdate_at(articleItem.getmUpdate_at());
                customAdapter.setArticleItemList(articleItemList);
                customAdapter.notifyDataSetChanged();

            }
        }*/
    }

    @Override
    public void addArticleItem(ArticleItem articleItem) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { COLUMN_ID, COLUMN_TITLE };
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                CONTENT_URI_ARTICLES,
                projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.def_list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        } /*else if(v.getId() == R.id.exp_list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }*/
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(CONTENT_URI_ARTICLES + "/" + info.id);
        String[] projection = { COLUMN_TITLE,
                ARTICLES_COLUMN_DESCRIPTION };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();

            String title=cursor.getString(cursor
                    .getColumnIndexOrThrow(COLUMN_TITLE));
            int id=cursor.getInt(cursor
                    .getColumnIndexOrThrow(COLUMN_ID));

            // always close the cursor
            cursor.close();
        }


                getActivity().getContentResolver().delete(uri, null, null);

        return true;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
/*
        JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object
                            jsonResponse = "";
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response
                                        .get(i);

                                String name = person.getString("name");
                                String email = person.getString("email");
                                JSONObject phone = person
                                        .getJSONObject("phone");
                                String home = phone.getString("home");
                                String mobile = phone.getString("mobile");

                                jsonResponse += "Name: " + name + "\n\n";
                                jsonResponse += "Email: " + email + "\n\n";
                                jsonResponse += "Home: " + home + "\n\n";
                                jsonResponse += "Mobile: " + mobile + "\n\n\n";

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

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