package com.example.ilia.final_exercise.ui.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.ui.adapters.EpxListViewCursorAdapter;
import com.example.ilia.final_exercise.ui.interfaces.IActivityAdapterInteraction;
import com.example.ilia.final_exercise.ui.interfaces.IActivityTopicListInteractionListener;
import com.example.ilia.final_exercise.ui.interfaces.ITopicListFragmentInteraction;
import com.example.ilia.final_exercise.ui.sorts.TypeOfSortDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.ilia.final_exercise.data.model.AppContentProvider.CONTENT_URI_ARTICLES;
import static com.example.ilia.final_exercise.data.model.AppContentProvider.CONTENT_URI_CATEGORIES_NOT_EMPTY;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.ARTICLES_CATEGORY_ID;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.ARTICLES_OWN;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.ARTICLES_PUBLISHED;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.ARTICLES_TITLE;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.ARTICLES_UPDATED;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.CATEGORIES_TITLE;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.COLUMN_ID;

/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class TopicListFragment extends BaseFragment implements IActivityAdapterInteraction,
        IActivityTopicListInteractionListener, LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "TopicListFragment";
    private static final int LIST_VIEW_MODE = 0;
    private static final int EXP_LIST_VIEW_MODE = 1;

    private static final int ARTICLES_LOADER = 0;
    private static final int CATEGORIES_LOADER = 1;
    private static final int ARTICLES_CHILD_LOADER = 2;

    private static final int REQUEST_SORT_TYPE = 1;

    private static final String ARGS_SELECTION = "argsSelection";
    private static final String ARGS_SELECTION_ARGUMENTS = "argsSelectionArguments";

    private static final String ARGS_ARTICLES_SELECTION = "argsArticlesSelection";
    private static final String ARGS_ARTICLES_SELECTION_ARGUMENTS = "argsArticlesSelectionArguments";
    String[] mProjection;
    String[] mCategoryProjection;
    private ListView mListView;
    private ExpandableListView mExpListView;
    private ITopicListFragmentInteraction mListener;
    private SimpleCursorAdapter mListViewCursorAdapter;
    private EpxListViewCursorAdapter mListViewExAdapter;
    private boolean mFilterOnlyMy;
    private boolean mFilterUnpublished;
    private String mKeyword;
    private String mArticlesSortOrder;
    private String mCategoriesSortOrder;
    private String mChildArticlesSortOrder;
    private boolean mFirstLaunch;

    private TypeOfSortDialog mSortOrderDialog;

    public TopicListFragment() {
    }

    public static TopicListFragment newInstance() {
        return new TopicListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic_list, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_topic_list_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListViewItemClicked(id);
            }
        });
        registerForContextMenu(mListView);

        mExpListView = (ExpandableListView) view.findViewById(R.id.fragment_topic_list_exp_list_view);
        mExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                onListViewItemClicked(id);
                return false;
            }
        });
        view.findViewById(R.id.fragment_topic_list_button_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });

        ((Switch) view.findViewById(R.id.fragment_topic_list_switch_only_my)).setOnCheckedChangeListener(this);
        ((Switch) view.findViewById(R.id.fragment_topic_list_switch_unpublished)).setOnCheckedChangeListener(this);

        Spinner spinner = (Spinner) view.findViewById(R.id.fragment_topic_list_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item
                , new String[]{getString(R.string.list_view_caption), getString(R.string.exp_list_view_caption)});
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(this);

        EditText filterEdit = (EditText) view.findViewById(R.id.fragment_topic_list_edit_text_filter);
        filterEdit.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTextChanged(s.toString());
            }
        });

        view.findViewById(R.id.fragment_topic_list_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewArticle();
            }
        });

        mProjection = new String[]{COLUMN_ID, ARTICLES_TITLE};
        mCategoryProjection = new String[]{COLUMN_ID, CATEGORIES_TITLE};

        mSortOrderDialog = new TypeOfSortDialog();
        mSortOrderDialog.setTargetFragment(this, REQUEST_SORT_TYPE);
        mArticlesSortOrder = ARTICLES_UPDATED + " DESC ";
        mCategoriesSortOrder = CATEGORIES_TITLE + " ASC ";
        mChildArticlesSortOrder = ARTICLES_UPDATED + " DESC ";
        mFirstLaunch = true;

        fillData();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ITopicListFragmentInteraction) {

            mListener = (ITopicListFragmentInteraction) activity;
            mListener.onRegister(this);

        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void fillData() {
        String[] from = new String[]{ARTICLES_TITLE};

        int[] to = new int[]{R.id.list_view_cell_title};

        mListViewCursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.topiclist_listview_cell
                , null, from, to, 0);
        mListView.setAdapter(mListViewCursorAdapter);

        mListViewExAdapter = new EpxListViewCursorAdapter(null, getActivity(), this);
        mExpListView.setAdapter(mListViewExAdapter);

        getLoaderManager().initLoader(ARTICLES_LOADER, null, this);
        getLoaderManager().initLoader(CATEGORIES_LOADER, null, this);

        getArticlesRequest(null, null);
    }

    private void onRefresh() {
        getArticlesRequest(null, null);
    }

    private void onAddNewArticle() {
        mListener.onCreateNewArticle();
    }

    private void onListViewItemClicked(long id) {
        mListener.onItemClicked(id);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.fragment_topic_list_switch_only_my:
                mFilterOnlyMy = isChecked;
                break;

            case R.id.fragment_topic_list_switch_unpublished:
                mFilterUnpublished = isChecked;
                break;
        }
        initFilter();
    }

    private void filterTextChanged(String keyword) {
        mKeyword = keyword;
        initFilter();
    }

    private void initFilter() {

        List<String> selectionArgs = new ArrayList<>();
        StringBuilder filterSelection = new StringBuilder();
        if (mFilterOnlyMy) {
            filterSelection.append(ARTICLES_OWN);
            filterSelection.append("= ? ");
            selectionArgs.add("1");
        }
        if (mFilterUnpublished) {
            if (filterSelection.length() > 0) {
                filterSelection.append(" AND ");
            }
            filterSelection.append(ARTICLES_PUBLISHED);
            filterSelection.append("= ?");
            selectionArgs.add("1");
        }
        if (!TextUtils.isEmpty(mKeyword)) {
            if (filterSelection.length() > 0) {
                filterSelection.append(" AND ");
            }
            filterSelection.append(ARTICLES_TITLE);
            filterSelection.append(" LIKE ?");
            selectionArgs.add(mKeyword + "%");

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

    void startSortOrderDialog() {
        mSortOrderDialog.show(getFragmentManager(), TypeOfSortDialog.class.getName());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (id < ARTICLES_CHILD_LOADER) {
            switch (id) {
                case ARTICLES_LOADER:
                    if (args != null) {
                        selection = args.getString(ARGS_SELECTION);
                        selectionArgs = args.getStringArray(ARGS_SELECTION_ARGUMENTS);
                    }
                    return new CursorLoader(
                            getActivity(),
                            CONTENT_URI_ARTICLES,
                            mProjection,
                            selection,
                            selectionArgs,
                            mArticlesSortOrder
                    );
                case CATEGORIES_LOADER:
                    return new CursorLoader(
                            getActivity(),
                            CONTENT_URI_CATEGORIES_NOT_EMPTY,
                            mCategoryProjection,
                            null,
                            null,
                            mCategoriesSortOrder
                    );
                default:
                    return null;
            }
        } else {
            if (args != null) {
                selection = args.getString(ARGS_ARTICLES_SELECTION);
                selectionArgs = args.getStringArray(ARGS_ARTICLES_SELECTION_ARGUMENTS);
            }
            return new CursorLoader(
                    getActivity(),
                    CONTENT_URI_ARTICLES,
                    mProjection,
                    selection,
                    selectionArgs,
                    mChildArticlesSortOrder
            );
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() < ARTICLES_CHILD_LOADER) {
            switch (loader.getId()) {
                case ARTICLES_LOADER:
                    mListViewCursorAdapter.swapCursor(data);
                    notifyArticlePanel(data);
                    break;
                case CATEGORIES_LOADER:
                    mListViewExAdapter.setGroupCursor(data);
                    break;
            }
        } else {
            Map<Long, Integer> groupMap = mListViewExAdapter.getGroupMap();
            Integer groupIndex = groupMap.get((long) (loader.getId() - ARTICLES_CHILD_LOADER));
            if (groupIndex != null) {
                mListViewExAdapter.setChildrenCursor(groupIndex, data);
            } else {
                Log.d(TAG, "groupMap = null, id=" + loader.getId());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ARTICLES_LOADER) {
            mListViewCursorAdapter.changeCursor(null);
        }
    }

    private void notifyArticlePanel(Cursor data) {
        if (mFirstLaunch && mListener != null) {
            if (data.moveToFirst()) {
                mFirstLaunch = false;
                mListener.onItemClicked(data.getLong(data.getColumnIndex(COLUMN_ID)));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case LIST_VIEW_MODE:
                mListView.setVisibility(View.VISIBLE);
                mExpListView.setVisibility(View.GONE);
                break;

            case EXP_LIST_VIEW_MODE:
                mListView.setVisibility(View.GONE);
                mExpListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void getChildrenCursor(long categoryId) {
        Loader loader = getLoaderManager().getLoader(ARTICLES_CHILD_LOADER + (int) categoryId);
        Bundle args = new Bundle();
        args.putString(ARGS_ARTICLES_SELECTION, ARTICLES_CATEGORY_ID + " = ?");
        args.putStringArray(ARGS_ARTICLES_SELECTION_ARGUMENTS, new String[]{String.format("%d", categoryId)});
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(ARTICLES_CHILD_LOADER + (int) categoryId, args,
                    this);
        } else {
            getLoaderManager().initLoader(ARTICLES_CHILD_LOADER + (int) categoryId, args,
                    this);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.fragment_topic_list_list_view) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_delete_listview, menu);

        } else if (v.getId() == R.id.fragment_topic_list_exp_list_view) {

            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_delete_listviewex, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_list_view:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                deleteArticleRequest(info.id, new IResponseListener() {
                    @Override
                    public void onResponse(long id) {
                        if (mListener != null && id > 0) {
                            mListener.onDeleteArticle(id);
                        }
                    }
                }, null);
                return true;
            case R.id.action_delete_exp_list_view:
                return true;
            case R.id.action_sort_order:
                startSortOrderDialog();
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SORT_TYPE:
                    mArticlesSortOrder = data.getStringExtra(TypeOfSortDialog.PARAM_SORT_TYPE);
                    getLoaderManager().restartLoader(ARTICLES_LOADER, null, this);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onUnregister(this);
        mListener = null;
    }
}