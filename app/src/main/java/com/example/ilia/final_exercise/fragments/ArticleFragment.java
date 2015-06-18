package com.example.ilia.final_exercise.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.activities.MainActivity;
import com.example.ilia.final_exercise.database.ArticleItem;
import com.example.ilia.final_exercise.database.GroupItem;
import com.example.ilia.final_exercise.interfaces.IClickListener;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.ilia.final_exercise.database.AppContentProvider.CONTENT_URI_ARTICLES;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_CATEGORY_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_CREATE_AT;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_DESCRIPTION;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_PUBLISHED;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_UPDATE_AT;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_TITLE;

/**
 * Created by ilia on 16.06.15.
 */
public class ArticleFragment extends Fragment implements IClickListener, View.OnClickListener,
                                                        Spinner.OnItemClickListener {
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
    private String jsonResponse;
    private List<GroupItem> listCategoty_id;
    private JSONObject mArticleView;

    private static String TAG = MainActivity.class.getSimpleName();


    public ArticleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout inflateView = (LinearLayout) inflater.inflate(R.layout.article_fragment, container, false);

        textTitle = (TextView) inflateView.findViewById(R.id.editText);
        textDescription = (TextView) inflateView.findViewById(R.id.editText2);
        spinnerCategory = (Spinner) inflateView.findViewById(R.id.spinner);
        imagePhoto = (ImageView) inflateView.findViewById(R.id.imageView);

        switchPublished = (Switch) inflateView.findViewById(R.id.switch1);
        buttonView = (Button) inflateView.findViewById(R.id.button_view);
        buttonEdit = (Button) inflateView.findViewById(R.id.button_edit);
        buttonSave = (Button) inflateView.findViewById(R.id.button_save);

        buttonView.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        if (listCategoty_id==null){
            listCategoty_id=new ArrayList<>();
            getCategory_ids();
        }

        return inflateView;
    }

    private void request() {
        HttpClient hc = new DefaultHttpClient();
        String message;

        HttpPost p = new HttpPost(urlJsonArrayInsert);
        mArticleView = new JSONObject();
        try {

            mArticleView.put("title", textTitle.getText().toString());
            mArticleView.put("description", textDescription.getText().toString());
            mArticleView.put("published", true);
            mArticleView.put("category_id", spinnerCategory.getSelectedItemId());

        } catch (Exception ex) {

        }

        JsonObjectRequest req = new JsonObjectRequest(urlJsonArrayInsert, mArticleView,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int id = 0;
                        String title = "";
                        String description = "";
                        int category_id = 0;
                        try {
                            JSONArray jsonArray = response.getJSONArray("article");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                id = jsonObject.getInt("id");
                                title = jsonObject.getString("title");
                                description = jsonObject.getString("description");
                                category_id = jsonObject.getInt("category_id");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonResponse = "";
                        ContentValues values = new ContentValues();
                        values.put(COLUMN_ID, id);
                        values.put(COLUMN_TITLE, title);
                        values.put(ARTICLES_COLUMN_DESCRIPTION, description);
                        values.put(ARTICLES_COLUMN_CATEGORY_ID, category_id);
                        values.put(ARTICLES_COLUMN_CREATE_AT, "");
                        values.put(ARTICLES_COLUMN_UPDATE_AT, "");
                        values.put(ARTICLES_COLUMN_PUBLISHED, true);
                        getActivity().getContentResolver().insert(CONTENT_URI_ARTICLES, values);
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

    @Override
    public void getArticleToAnotherFragment(Uri uri) {

        todoUri=uri;
        fillData(todoUri);

    }

    private void fillData(Uri uri) {
        String[] projection = { COLUMN_TITLE,
                ARTICLES_COLUMN_DESCRIPTION };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();

            textTitle.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(COLUMN_TITLE)));
            textDescription.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(ARTICLES_COLUMN_DESCRIPTION)));

            // always close the cursor
            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_view:

                buttonSave.setVisibility(View.INVISIBLE);
                spinnerCategory.setVisibility(View.INVISIBLE);
                textTitle.setEnabled(false);
                textDescription.setEnabled(false);
                spinnerCategory.setEnabled(false);
                break;
            case R.id.button_edit:
                spinnerCategory.setVisibility(View.VISIBLE);
                buttonSave.setVisibility(View.VISIBLE);
                textTitle.setEnabled(true);
                textDescription.setEnabled(true);
                spinnerCategory.setEnabled(true);
                break;
            case R.id.button_save:
                /*if (mArticleItem!=null) {
                    mArticleItem.setmTitle(textTitle.getText().toString());
                    IStateItemChange iStateItemChange=(IStateItemChange)getActivity();
                    iStateItemChange.updateArticleItem(mArticleItem);*/
                if (mArticleView==null)
                {
                    request();
                } else {

                }

                    /*if (mArticleUri == null) {
                        mArticleUri = AppController.getAppContext().getContentResolver()
                                .insert(AppContentProvider.CONTENT_URI_ARTICLES, values);
                    } else {
                        AppController.getAppContext().getContentResolver()
                                .update(mArticleUri, values, null, null);
                    }
*/
                //}
                spinnerCategory.setVisibility(View.INVISIBLE);
                buttonSave.setVisibility(View.INVISIBLE);
                textTitle.setEnabled(false);
                textDescription.setEnabled(false);
                spinnerCategory.setEnabled(false);
                break;


        }
    }

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
                        listCategoty_id.add(groupItem);
                    }
                    String[] strings=new String[listCategoty_id.size()];
                    for (int i=0;i<listCategoty_id.size();i++) {
                        strings[i]=listCategoty_id.get(i).getmTitle();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item
                            , strings);
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner:

                break;
        }
    }


}
