package com.example.ilia.final_exercise.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.database.ArticleItem;
import com.example.ilia.final_exercise.database.GroupItem;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.interfaces.IStateItemChange;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ilia.final_exercise.database.AppContentProvider.CONTENT_URI_ARTICLES;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_CATEGORY_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_DESCRIPTION;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_OWN;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_PUBLISHED;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_UPDATE_AT;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_TITLE;

/**
 * Created by ilia on 16.06.15.
 */
public class ArticleFragment extends Fragment implements IClickListener, View.OnClickListener,
                                                            IStateItemChange {
    private TextView textTitle;
    private TextView textDescription;
    private Spinner spinnerCategory;
    private ImageView imagePhoto;
    private Switch switchPublished;
    private Button buttonView;
    private Button buttonEdit;
    private Button buttonSave;
    private Uri todoUri;
    private final static String urlJsonArray = "http://editors.yozhik.sibext.ru/categories.json";
    private final static String urlJsonArrayInsert = "http://editors.yozhik.sibext.ru/articles.json";
    private final static String apiKey="bdf6064c9b5a4011ee2f36b082bb4e5d";
    private ArrayList<GroupItem> listCategory_id;
    private String[] stringsCategory;
    private ArticleItem articleItem;

    private JSONObject mArticleView;

    public ArticleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout inflateView = (LinearLayout) inflater.inflate(R.layout.article_fragment, container, false);

        textTitle = (TextView) inflateView.findViewById(R.id.editText);
        textDescription = (TextView) inflateView.findViewById(R.id.editText2);
        spinnerCategory = (Spinner) inflateView.findViewById(R.id.spinner);
        spinnerCategory.setEnabled(false);
        imagePhoto = (ImageView) inflateView.findViewById(R.id.imageView);

        switchPublished = (Switch) inflateView.findViewById(R.id.switch1);
        buttonView = (Button) inflateView.findViewById(R.id.button_view);
        buttonEdit = (Button) inflateView.findViewById(R.id.button_edit);
        buttonSave = (Button) inflateView.findViewById(R.id.button_save);

        buttonView.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        listCategory_id = new ArrayList<>();

        //fill spinner with categories
        getCategory_ids();


        return inflateView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_view:
                isOwnSetViewsEnabled(false);

                break;
            case R.id.button_edit:
                try {
                    if ((mArticleView!=null)&&(mArticleView.getBoolean("own"))) {
                        isOwnSetViewsEnabled(true);
                    } else {
                        Toast.makeText(getActivity(),getResources().getString(R.string.can_not_edit),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.button_save:
                if (mArticleView==null){
                    request();
                } else {
                    request();
                    mArticleView=null;
                }
                isOwnSetViewsEnabled(false);
                break;


        }
    }

    /**
     * This method send request to DB and receive categories for fill {@link #spinnerCategory}
     */
    private void getCategory_ids() {
        StringRequest req = new StringRequest(urlJsonArray, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrays = jsonObject.getJSONArray("categories");
                    for (int i = 0; i < jsonArrays.length(); i++) {

                        JSONObject category = (JSONObject) jsonArrays.get(i);

                        int id = category.getInt("id");
                        String title = category.getString("title");
                        GroupItem groupItem = new GroupItem(id,title);
                        listCategory_id.add(groupItem);
                    }
                    stringsCategory = new String[listCategory_id.size()];
                    for (int i=0;i< listCategory_id.size();i++) {
                        stringsCategory[i]= listCategory_id.get(i).getmTitle();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item
                            , stringsCategory);
                    spinnerCategory.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Token token=" + apiKey);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(req);
    }



    /**
     * Receive uri from another fragment and initialize {@link #mArticleView} if edit is started
     * and clear controls if add is started
     * @param uri Uri for request to DB
     */
    @Override
    public void getArticleToAnotherFragment(Uri uri) {

        todoUri=uri;
        if (fillData(todoUri)) {
            mArticleView=new JSONObject();
            try {
                mArticleView.put(COLUMN_TITLE, textTitle.getText().toString());
                mArticleView.put(ARTICLES_COLUMN_DESCRIPTION, textDescription.getText().toString());
                mArticleView.put(ARTICLES_COLUMN_PUBLISHED, true);
                int category_Id= (int) spinnerCategory.getSelectedItemId();
                int indexSelectedCategories=listCategory_id.get(category_Id).get_id();
                mArticleView.put(ARTICLES_COLUMN_OWN,switchPublished.isChecked());
                mArticleView.put(ARTICLES_COLUMN_CATEGORY_ID, indexSelectedCategories);
                if (!switchPublished.isChecked()) {
                    isOwnSetViewsEnabled(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            textTitle.setText("");
            textDescription.setText("");
            spinnerCategory.setSelection(0);
            switchPublished.setChecked(true);
            mArticleView=null;
            isOwnSetViewsEnabled(true);
        }

    }

    @Override
    public void deleteArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void updateArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void addArticleItem(Uri articleItem) {

    }

    /**
     * Fill all view controls in ArticleFragment when edit or add new article is starting
     * @param uri Uri for request to DB
     * @return Result talk about we have add or edit
     */
    private boolean fillData(Uri uri) {
        boolean result=false;
        if (uri!=null) {
            result=true;
            String[] projection = {COLUMN_ID,COLUMN_TITLE,
                    ARTICLES_COLUMN_DESCRIPTION,
                    ARTICLES_COLUMN_CATEGORY_ID,
                    ARTICLES_COLUMN_OWN};
            Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
                    null);

            if (cursor != null) {
                articleItem = new ArticleItem();

                cursor.moveToFirst();
                try {
                    articleItem = ArticleItem.fromCursor(cursor);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                textTitle.setText(articleItem.getmTitle());
                textDescription.setText(articleItem.getmDescription());
                int category_id = articleItem.getmCategory_id();
                int indexSelectedCategory = 0;
                for (int i = 0; i < stringsCategory.length; i++) {
                    if (listCategory_id.get(i).get_id() == category_id) {
                        indexSelectedCategory = i;
                        break;
                    }
                }
                spinnerCategory.setSelection(indexSelectedCategory);
                switchPublished.setChecked(articleItem.getmOwn());

                // always close the cursor
                cursor.close();
            }
        }
        return result;
    }

    /**
     * Add new article to server and DB
     */
    private void request() {

        mArticleView = new JSONObject();
        try {

            mArticleView.put("title", textTitle.getText().toString());
            mArticleView.put("description", textDescription.getText().toString());
            mArticleView.put("published", true);
            int category_id= (int) spinnerCategory.getSelectedItemId();
            int indexSelectedCategory=listCategory_id.get(category_id).get_id();

            mArticleView.put("category_id", indexSelectedCategory);

        } catch (Exception ex) {
            throw  new IllegalArgumentException(ex.getMessage());
        }

        JsonObjectRequest req = new JsonObjectRequest(urlJsonArrayInsert, mArticleView,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int id = 0;
                        String title = "";
                        String description = "";
                        String date="";
                        int category_id = 0;
                        try {
                            JSONObject jsonObject = response.getJSONObject("article");
                            id = jsonObject.getInt("id");
                            title = jsonObject.getString("title");
                            description = jsonObject.getString("description");
                            category_id = jsonObject.getInt("category_id");
                            boolean own = jsonObject.getBoolean("own");
                            date = jsonObject.getString("title");
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_ID, id);
                            values.put(COLUMN_TITLE, title);
                            values.put(ARTICLES_COLUMN_DESCRIPTION, description);
                            values.put(ARTICLES_COLUMN_CATEGORY_ID, category_id);
                            values.put(ARTICLES_COLUMN_OWN, own ? 1 : 0);
                            values.put(ARTICLES_COLUMN_UPDATE_AT,date);
                            getActivity().getContentResolver().insert(CONTENT_URI_ARTICLES, values);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Token token=" + apiKey);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(req);
    }

    /**
     * Set enabled for controls as add or edit
     * @param flag Flag for setting enable
     */
    private void isOwnSetViewsEnabled(boolean flag) {
        buttonSave.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        textTitle.setEnabled(flag);
        textDescription.setEnabled(flag);
        spinnerCategory.setEnabled(flag);
    }

}
