package com.example.ilia.final_exercise.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.adapters.ListExpandableAdapter;
import com.example.ilia.final_exercise.database.AppContentProvider;
import com.example.ilia.final_exercise.database.ArticleItem;
import com.example.ilia.final_exercise.database.GroupItem;
import com.example.ilia.final_exercise.interfaces.IActivityAdapterInteraction;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.interfaces.IStateItemChange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ilia.final_exercise.database.AppContentProvider.CONTENT_URI_ARTICLES;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_CATEGORY_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_DESCRIPTION;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_OWN;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_PHOTO;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_PUBLISHED;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_UPDATE_AT;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_TITLE;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.TABLE_ARTICLES;

/**
 * Created by ilia on 08.06.15.
 */
public class ListFragment extends Fragment implements Spinner.OnItemSelectedListener,
        ExpandableListView.OnChildClickListener, ListView.OnItemClickListener,
        IStateItemChange, LoaderManager.LoaderCallbacks<Cursor>, Button.OnClickListener,
        IActivityAdapterInteraction {

    private static final String ARGS_SELECTION 				= "argsSelection";
    private static final String ARGS_SELECTION_ARGUMENTS 	= "argsSelectionArguments";

    private static final String ARGS_ARTICLES_SELECTION 	= "argsArticlesSelection";
    private static final String ARGS_ARTICLES_SELECTION_ARGUMENTS 	= "argsArticlesSelectionArguments";
    public static final int ARTICLES_CHILD_LOADER	= 2;
    private static final int ARTICLES_LOADER = 1;
    public static final int INIT_LOADER = 0;
    private ExpandableListView expandableListView;
    private ListView customListView;
    private Spinner mSpinner;
    private ListExpandableAdapter expandableAdapter;
    private List<GroupItem> groupItemList = new ArrayList<>();
    private List<ArticleItem> articleItemList = new ArrayList<>();
    private SimpleCursorAdapter cursorAdapter;
    private EditText mEitTextFilter;
    private ImageButton imageButtonRefresh;
    private ImageButton imageButtonFilter;
    private Switch mSwitchOnlyMy;

    private final static String urlJsonArray = "http://editors.yozhik.sibext.ru/";
    private final static String apiKey = "bdf6064c9b5a4011ee2f36b082bb4e5d";

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout inflateView = (LinearLayout) inflater.inflate(R.layout.list_fragment, container, false);
        expandableListView = (ExpandableListView) inflateView.findViewById(R.id.exp_list);
        mSpinner = (Spinner) inflateView.findViewById(R.id.list_spinner);
        customListView = (ListView) inflateView.findViewById(R.id.def_list);
        mEitTextFilter = (EditText) inflateView.findViewById(R.id.filter);

        mSwitchOnlyMy = (Switch) inflateView.findViewById(R.id.switch_only_my);

        imageButtonFilter= (ImageButton) inflateView.findViewById(R.id.start_filter);
        imageButtonFilter.setOnClickListener(this);
        imageButtonRefresh = (ImageButton) inflateView.findViewById(R.id.refresh);
        imageButtonRefresh.setOnClickListener(this);
        Button addNewArticleButton = (Button) inflateView.findViewById(R.id.add_new_article);
        addNewArticleButton.setOnClickListener(this);

        expandableAdapter = new ListExpandableAdapter(null,getActivity(), this);
        expandableListView.setAdapter(expandableAdapter);
        expandableListView.setOnChildClickListener(this);

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[]{COLUMN_TITLE};
        // Fields on the UI to which we map
        int[] to = new int[]{R.id.textChild};

        getLoaderManager().initLoader(INIT_LOADER, null, this);

        cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.child_view, null, from, to, 0);
        customListView.setAdapter(cursorAdapter);

        //receive all articles from server and add to DB for cash
        receiveArticlesFromServer();
        mSpinner.setOnItemSelectedListener(this);

        customListView.setOnItemClickListener(this);

        registerForContextMenu(customListView);
        return inflateView;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IClickListener listener = (IClickListener) getActivity();
        Uri uri = Uri.parse(CONTENT_URI_ARTICLES + "/"
                + id);
        //ArticleItem articleItem = cursorAdapter.getItem(position);
        listener.getArticleToAnotherFragment(uri);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        /*if (id < ARTICLES_CHILD_LOADER) {
            switch (id) {
                case ARTICLES_LOADER:
                    // filter items

                    if (args != null) {
                        selection = args.getString(ARGS_SELECTION);
                        selectionArgs = args.getStringArray(ARGS_SELECTION_ARGUMENTS);
                    }

                    // Returns a new CursorLoader
                    return new CursorLoader(
                            getActivity(),   // Parent activity context
                            AppContentProvider.CONTENT_URI_ARTICLES, // Table to query
                            mProjection,     // Projection to return
                            selection,            // No selection clause
                            selectionArgs,            // No selection arguments
                            mArticlesSortOrder             // Default sort order
                    );
                case CATEGORIES_LOADER:
                    // Returns a new CursorLoader
                    return new CursorLoader(
                            getActivity(),   // Parent activity context
                            AppContentProvider.CONTENT_URI_CATEGORIES_NOT_EMPTY, // Table to query
                            mCategoryProjection,     // Projection to return
                            null,            // No selection clause
                            null,            // No selection arguments
                            mCategoriesSortOrder  // Default sort order
                    );
                default:
                    // An invalid id was passed in
                    return null;
            }
        } else {
            // child loaders
            // filter items
            String selection = null;
            String[] selectionArgs = null;
            if (args != null) {
                selection = args.getString(ARGS_ARTICLES_SELECTION);
                selectionArgs = args.getStringArray(ARGS_ARTICLES_SELECTION_ARGUMENTS);
            }

            // Returns a new CursorLoader
            return new CursorLoader(
                    getActivity(),   // Parent activity context
                    AppContentProvider.CONTENT_URI_ARTICLES, // Table to query
                    mProjection,     // Projection to return
                    selection,            // No selection clause
                    selectionArgs,            // No selection arguments
                    mChildArticlesSortOrder             // Default sort order
            );
        }*/
        return  null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.def_list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        final Uri uri = Uri.parse(CONTENT_URI_ARTICLES + "/" + info.id);

        AppController.getInstance().addToRequestQueue(getJsonObjectRequest(info, uri));
        return true;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case INIT_LOADER:
                cursorAdapter.swapCursor(data);
                break;
            case ARTICLES_LOADER:
                cursorAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case INIT_LOADER:
                cursorAdapter.swapCursor(null);
                break;
            case ARTICLES_LOADER:
                cursorAdapter.swapCursor(null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_new_article:
                IClickListener listener = (IClickListener) getActivity();
                listener.getArticleToAnotherFragment(null);
                break;
            case R.id.refresh:
                mEitTextFilter.setText("");
                receiveArticlesFromServer();
                break;
            case R.id.start_filter:
                initFilter();
                break;
        }
    }

    /**
     * Request for delete article from server
     * @param info info with id deleting article
     * @param uri uri need to deleting from DB
     * @return jsonObjectRequest
     */
    private JsonObjectRequest getJsonObjectRequest(final AdapterView.AdapterContextMenuInfo info, final Uri uri) {
        return new JsonObjectRequest(Request.Method.DELETE,
                urlJsonArray + "articles/" + info.id + ".json", null,
                getResponseForDeleteFromDB(uri), getErrorListenerForDeleteArticles()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getStringStringMap();
            }
        };
    }

    /**
     * Receive all articles from server and add to DB for cash
     */
    private void receiveArticlesFromServer() {
        getActivity().getContentResolver().delete(CONTENT_URI_ARTICLES, null, null);

        StringRequest req = new StringRequest(urlJsonArray + "articles.json",
                getResponseForReceiveArticlesFormServer(), getErrorListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getStringStringMap();
            }
        };

        AppController.getInstance().addToRequestQueue(req);
    }

    /**
     * Response for deleting from DB
     * @return
     */
    private Response.ErrorListener getErrorListenerForDeleteArticles() {
        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), getResources().getString(R.string.can_not_delete), Toast.LENGTH_SHORT).show();
                VolleyLog.e("Error: ", error.getMessage());

            }
        };
    }

    /**
     * error listener for requests
     * @return
     */
    private Response.ErrorListener getErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        };
    }

    /**
     * Response listener for receive article from server
     */
    private Response.Listener<String> getResponseForReceiveArticlesFormServer() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrays = jsonObject.getJSONArray(TABLE_ARTICLES);
                    for (int i = 0; i < jsonArrays.length(); i++) {

                        JSONObject articles = (JSONObject) jsonArrays.get(i);

                        int id = articles.getInt("id");
                        String title = articles.getString(COLUMN_TITLE);
                        String description = articles.getString(ARTICLES_COLUMN_DESCRIPTION);
                        int category_id = articles.getInt(ARTICLES_COLUMN_CATEGORY_ID);
                        boolean own = articles.getBoolean(ARTICLES_COLUMN_OWN);
                        boolean isPhotoExists=articles.isNull(ARTICLES_COLUMN_PHOTO);
                        JSONObject jsonPhoto=null;
                        String uri="";

                        if (!isPhotoExists) {
                            jsonPhoto = articles.getJSONObject(ARTICLES_COLUMN_PHOTO);
                            uri = jsonPhoto.getString("url");
                        }


                        ContentValues values = new ContentValues();
                        values.put(COLUMN_ID, id);
                        values.put(COLUMN_TITLE, title);
                        values.put(ARTICLES_COLUMN_DESCRIPTION, description);
                        values.put(ARTICLES_COLUMN_CATEGORY_ID, category_id);
                        values.put(ARTICLES_COLUMN_OWN, own ? 1 : 0);
                        values.put(ARTICLES_COLUMN_PHOTO,uri);

                        getActivity().getContentResolver().insert(CONTENT_URI_ARTICLES, values);
                    }
                    VolleyLog.v("Response:%n %s", response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * resonce listener for delete selected article from DB
     * @param uri Uri for delete article
     * @return
     */
    private Response.Listener<JSONObject> getResponseForDeleteFromDB(final Uri uri) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                VolleyLog.v("Response:%n %s", response);
                getActivity().getContentResolver().delete(uri, null, null);
            }
        };
    }

    /**
     * Set hash map for requests
     * @return
     */
    private Map<String, String> getStringStringMap() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", "application/json");
        params.put("Authorization", "Token token=" + apiKey);
        return params;
    }

    /**
     * initialize filter for LiseView
     */
    private void initFilter() {

        List<String> selectionArgs = new ArrayList<>();
        StringBuilder filterSelection = new StringBuilder();
        if (mSwitchOnlyMy.isChecked()) {
            filterSelection.append(ARTICLES_COLUMN_OWN);
            filterSelection.append("= ? ");
            selectionArgs.add("1");
        }
        if (false/*mFilterUnpublished*/) {
            if (filterSelection.length() > 0) {
                filterSelection.append(" AND ");
            }
            filterSelection.append(ARTICLES_COLUMN_PUBLISHED);
            filterSelection.append("= ?");
            selectionArgs.add("1");
        }
        if (!TextUtils.isEmpty(mEitTextFilter.getText())) {
            if (filterSelection.length() > 0) {
                filterSelection.append(" AND ");
            }
            filterSelection.append(COLUMN_TITLE);
            filterSelection.append(" LIKE ?");
            selectionArgs.add(mEitTextFilter.getText()+ "%");

        }

        Bundle args = null;
        if (filterSelection.length() > 0) {
            args = new Bundle();
            args.putString(ARGS_SELECTION, filterSelection.toString());
            args.putStringArray(ARGS_SELECTION_ARGUMENTS
                    , selectionArgs.toArray(new String[selectionArgs.size()]));
        }

        getLoaderManager().restartLoader(ARTICLES_LOADER, args, this);
    }
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void deleteArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void updateArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void addArticleItem() {
        receiveArticlesFromServer();
    }



    @Override
    public void getChildrenCursor(long categoryId) {
        Loader loader	= getLoaderManager().getLoader(ARTICLES_CHILD_LOADER+(int)categoryId);
        Bundle args		= new Bundle();
        args.putString(ARGS_ARTICLES_SELECTION , COLUMN_ID+" = ?");
        args.putStringArray(ARGS_ARTICLES_SELECTION_ARGUMENTS, new String[]{ String.format("%d",categoryId)})
        ;
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(ARTICLES_CHILD_LOADER+(int)categoryId, args,
                    this);
        } else {
            getLoaderManager().initLoader(ARTICLES_CHILD_LOADER+(int)categoryId, args,
                    this);
        }
    }
}