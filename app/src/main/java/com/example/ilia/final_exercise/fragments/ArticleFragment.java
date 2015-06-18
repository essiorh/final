package com.example.ilia.final_exercise.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.activities.MainActivity;
import com.example.ilia.final_exercise.database.ArticleItem;
import com.example.ilia.final_exercise.interfaces.IClickListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.ilia.final_exercise.database.AppContentProvider.CONTENT_URI_ARTICLES;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_CATEGORY_ID;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_CREATE_AT;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_DESCRIPTION;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_PUBLISHED;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.ARTICLES_COLUMN_UPDATE_AT;
import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.COLUMN_TITLE;

/**
 * Created by ilia on 16.06.15.
 */
public class ArticleFragment extends Fragment implements IClickListener, View.OnClickListener,
                                                        Switch.OnCheckedChangeListener,
                                                        Spinner.OnItemClickListener {
    private TextView textTitle;
    private TextView textDescription;
    private Spinner spinnerCategory;
    private ImageView imagePhoto;
    private Switch switchPublished;
    private Button buttonView;
    private Button buttonEdit;
    private Button buttonSave;
    private ArticleItem mArticleItem;
    private Uri todoUri;
    private final static String urlJsonArray = "http://editors.yozhik.sibext.ru/categories.json";
    private final static String urlJsonArrayInsert = "http://editors.yozhik.sibext.ru/articles.json";
    private final static String apiKey="bdf6064c9b5a4011ee2f36b082bb4e5d";
    private String jsonResponse;
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

        switchPublished.setOnCheckedChangeListener(this);
        buttonView.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        buttonSave.setOnClickListener(this);



        return inflateView;
    }

    private boolean request() {
        boolean result = false;
        HttpClient hc = new DefaultHttpClient();
        String message;

        HttpPost p = new HttpPost(urlJsonArrayInsert);
        JSONObject object = new JSONObject();
        try {

            object.put("title", textTitle.getText().toString());
            object.put("description", textDescription.getText().toString());
            object.put("published",true);
            object.put("category_id",1);


        } catch (Exception ex) {

        }

        /*try {
            message = object.toString();

            p.setHeader("Content-Type", "application/json");
            p.setHeader("Authorization", "Token token=" + apiKey);
            HttpResponse resp = hc.execute(p);
            if (resp != null) {
                if (resp.getStatusLine().getStatusCode() == 200)
                    result = true;
            }

            Log.d("Status line", "" + resp.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        JsonObjectRequest req = new JsonObjectRequest(urlJsonArrayInsert, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        jsonResponse = "";


                        int id = 0;
                        String title = "";
                        try {
                            id = response.getInt("id");
                            title = response.getString("title");
                            jsonResponse += "id: " + id + "\n\n";
                            jsonResponse += "title: " + title + "\n\n";

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                                    /*JSONObject phone = person
                                            .getJSONObject("phone");
                                    String home = phone.getString("home");
                                    String mobile = phone.getString("mobile");*/


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

        return result;
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
                if (request()) {
                    String title		=  textTitle.getText().toString();
                    String description	= textDescription.getText().toString();
                    long updated		= (new Date()).getTime();
                    //long categoryId		= mCategories.get(mSpinner.getSelectedItemPosition()).getId();
                    //if (mCreatedDate == 0) mCreatedDate = updated;
                    boolean isPublished	= switchPublished.isChecked();
                    // only save if title or description
                    // is available

                    /*if (description.length() == 0 || title.length() == 0) {
                        return;
                    }*/

                    StringRequest req = new StringRequest(urlJsonArray, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            jsonResponse = "";
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                JSONArray jsonArrays= jsonObject.getJSONArray("categories");
                                for (int i = 0; i < jsonArrays.length(); i++) {

                                    JSONObject person = (JSONObject) jsonArrays.get(i);

                                    int id = person.getInt("id");
                                    String title = person.getString("title");
                                    /*JSONObject phone = person
                                            .getJSONObject("phone");
                                    String home = phone.getString("home");
                                    String mobile = phone.getString("mobile");*/

                                    jsonResponse += "id: " + id + "\n\n";
                                    jsonResponse += "title: " + title + "\n\n";
                                }
                                textDescription.setText(jsonResponse);

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


                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TITLE, 		title);
                    values.put(ARTICLES_COLUMN_DESCRIPTION, 	description);
                    values.put(ARTICLES_COLUMN_CATEGORY_ID, 	0);
                    values.put(ARTICLES_COLUMN_CREATE_AT, 		updated);
                    values.put(ARTICLES_COLUMN_UPDATE_AT, updated);
                    values.put(ARTICLES_COLUMN_PUBLISHED, isPublished);
                    getActivity().getContentResolver().insert(CONTENT_URI_ARTICLES, values);

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
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch1:
                if (mArticleItem!=null) {
                    mArticleItem.setmPublished(isChecked);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner:

                break;
        }
    }
}
