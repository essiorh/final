package com.example.ilia.final_exercise.ui.fragments;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.ilia.final_exercise.AppControllerMy;
import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.data.containers.ArticleItem;
import com.example.ilia.final_exercise.data.containers.GroupItem;
import com.example.ilia.final_exercise.ui.imageupload.MultipartRequest;
import com.example.ilia.final_exercise.ui.interfaces.IClickListener;
import com.example.ilia.final_exercise.ui.interfaces.IStateItemChange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.ilia.final_exercise.data.model.AppContentProvider.*;
import static com.example.ilia.final_exercise.data.model.AppSQLiteOpenHelper.*;

/**
 * Created by ilia on 16.06.15.
 */
public class ArticleFragment extends Fragment implements IClickListener, View.OnClickListener,
                                                            IStateItemChange {
    private static final int PICK_IMAGE_REQUEST = 10;
    private static final String HTTP_EDITORS_YOZHIK_SIBEXT_RU_ARTICLES = "http://editors.yozhik.sibext.ru/articles/";
    private static final String PHOTOS_JSON = "/photos.json";
    private static final String JSON = ".json";
    private static final String DIALOG_TITLE = "dialog title";
    private static final String DIALOG_MESSAGE = "dialog message";
    private static final String PHOTO_IMAGE = "photo[image]";
    private static final String CONTENT_DOWNLOADS_PUBLIC_DOWNLOADS = "content://downloads/public_downloads";
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private static final String AUDIO = "audio";
    private static final String SELECTION_ID = "_id=?";
    private static final String CONTENT = "content";
    private static final String FILE = "file";
    private static final String COM_ANDROID_EXTERNALSTORAGE_DOCUMENTS = "com.android.externalstorage.documents";
    private static final String COM_ANDROID_PROVIDERS_DOWNLOADS_DOCUMENTS = "com.android.providers.downloads.documents";
    private static final String COM_ANDROID_PROVIDERS_MEDIA_DOCUMENTS = "com.android.providers.media.documents";
    private static final String COM_GOOGLE_ANDROID_APPS_PHOTOS_CONTENT = "com.google.android.apps.photos.content";
    private static final String DATA = "_data";
    private static final String IMAGE_TYPE = "image/*";
    private static final String PRIMARY = "primary";
    private static final String URL_JSON_ARRAY = "http://editors.yozhik.sibext.ru/categories.json";
    private static final String URL_JSON_ARRAY_INSERT = "http://editors.yozhik.sibext.ru/articles.json";
    private static final String apiKey="bdf6064c9b5a4011ee2f36b082bb4e5d";
    private TextView textTitle;
    private TextView textDescription;
    private Spinner spinnerCategory;
    private ImageView imagePhoto;
    private Switch switchPublished;
    private Button buttonView;
    private Button buttonEdit;
    private Button buttonSave;
    private Uri todoUri;
    private boolean isChangePhotoEdittable;


    private ArrayList<GroupItem> listCategory_id;
    private String[] stringsCategory;
    private ArticleItem articleItem;
    private Uri imageUri;

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

        imagePhoto.setOnClickListener(this);
        buttonView.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        listCategory_id = new ArrayList<>();

        //fill spinner with categories
        getCategory_ids();
        isChangePhotoEdittable=false;

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
                    if ((mArticleView!=null)&&(mArticleView.getBoolean(ARTICLES_COLUMN_OWN))) {
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
                    addArticleToServer();
                } else {
                    editArticleIntoServer();

                }
                isOwnSetViewsEnabled(false);
                break;
            case R.id.imageView:
                showImageGallery();

        }
    }

    void sendRequestSavePhoto(long articleID, Uri photoUri){
        //getArticleItemFromCursor(null, -1);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), DIALOG_TITLE,
                DIALOG_MESSAGE, true);

        File photo = new File(getPath(getActivity(), photoUri));
        MultipartRequest req= new MultipartRequest(
                HTTP_EDITORS_YOZHIK_SIBEXT_RU_ARTICLES + articleID + PHOTOS_JSON
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObjectPhoto=new JSONObject(response);
                    mArticleView.put(ARTICLES_COLUMN_PHOTO, jsonObjectPhoto.getJSONObject(ARTICLES_COLUMN_PHOTO).getString("url"));


                    addPhotoToArticleOnServer();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, photo
                , photo.length(),
                null,
                null,
                PHOTO_IMAGE,
                new MultipartRequest.MultipartProgressListener() {
                    @Override
                    public void transferred(long transfered, int progress) {
                        progressDialog.incrementProgressBy(progress - progressDialog.getProgress());
                    }
                });
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.show();
        AppControllerMy.getInstance().addToRequestQueue(req);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if (PRIMARY.equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse(CONTENT_DOWNLOADS_PUBLIC_DOWNLOADS), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if (IMAGE.equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if (VIDEO.equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if (AUDIO.equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = SELECTION_ID;
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if (CONTENT.equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if (FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return COM_ANDROID_EXTERNALSTORAGE_DOCUMENTS.equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return COM_ANDROID_PROVIDERS_DOWNLOADS_DOCUMENTS.equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return COM_ANDROID_PROVIDERS_MEDIA_DOCUMENTS.equals(uri.getAuthority());
    }

    private boolean isGooglePhotosUri(Uri uri) {
        return COM_GOOGLE_ANDROID_APPS_PHOTOS_CONTENT.equals(uri.getAuthority());
    }

    private String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = DATA;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                // Log.d(TAG, String.valueOf(bitmap));
                imagePhoto.setImageBitmap(bitmap);
                //mArticle.setImageUri(getPath(getActivity(), uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * start dialog with add image to our ImageView
     */
    private void showImageGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType(IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
    }

    /**
     * This method send request to DB and receive categories for fill {@link #spinnerCategory}
     */
    private void getCategory_ids() {
        StringRequest req = new StringRequest(URL_JSON_ARRAY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArrays = jsonObject.getJSONArray(TABLE_CATEGORIES);
                    for (int i = 0; i < jsonArrays.length(); i++) {

                        JSONObject category = (JSONObject) jsonArrays.get(i);

                        int id = category.getInt("id");
                        String title = category.getString(COLUMN_TITLE);
                        GroupItem groupItem = new GroupItem(id,title);
                        listCategory_id.add(groupItem);
                    }
                    stringsCategory = new String[listCategory_id.size()];
                    for (int i=0;i< listCategory_id.size();i++) {
                        stringsCategory[i]= listCategory_id.get(i).getmTitle();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item, stringsCategory);
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
        AppControllerMy.getInstance().addToRequestQueue(req);
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
            Cursor cursor = getArticleItemFromCursor(uri,-1);
            textTitle.setText("");
            textDescription.setText("");
            spinnerCategory.setSelection(0);
            switchPublished.setChecked(true);
            mArticleView=null;
            isOwnSetViewsEnabled(true);
        }

    }

    /**
     * Fill all view controls in ArticleFragment when edit or add new article is starting
     * @param uri Uri for request to DB
     * @return Result talk about we have add or edit
     */
    private boolean fillData(Uri uri) {
        boolean result=false;
        if (uri!=null) {
            result = true;
            Cursor cursor = getArticleItemFromCursor(uri, -1);
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
            if (!TextUtils.isEmpty(articleItem.getmPhoto()))
            {
                sendRequestPhoto(articleItem.getmPhoto());
            } else {
                imagePhoto.setImageDrawable(null);
            }
            // always close the cursor
            cursor.close();
        }
        return result;
    }

    private Cursor getArticleItemFromCursor(Uri uri,long id) {
        String[] projection = {COLUMN_ID, COLUMN_TITLE,
                ARTICLES_COLUMN_DESCRIPTION,
                ARTICLES_COLUMN_CATEGORY_ID,
                ARTICLES_COLUMN_PHOTO,
                ARTICLES_COLUMN_OWN};
        Cursor cursor=null;

        if (uri == null) {
            if (id != -1) {
                if (mArticleView != null) {
                    uri = Uri.parse(CONTENT_URI_ARTICLES + "/"
                            + id);
                }
            }
        }
        if (uri!=null) {
            cursor = getActivity().getContentResolver().query(uri, projection, null, null,
                    null);
            articleItem = new ArticleItem();

            cursor.moveToFirst();
            try {
                articleItem = ArticleItem.fromCursor(cursor);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return cursor;
    }
    /**
     * Add new article to server and DB
     */
    private void addArticleToServer() {

        setFieldsTomArticleView();

        JsonObjectRequest req = new JsonObjectRequest(URL_JSON_ARRAY_INSERT, mArticleView,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int id = 0;
                        String title = "";
                        String description = "";
                        String date = "";
                        int category_id = 0;
                        boolean own = false;
                        try {
                            JSONObject jsonObject = response.getJSONObject("article");
                            id = jsonObject.getInt("id");
                            title = jsonObject.getString(COLUMN_TITLE);
                            description = jsonObject.getString(ARTICLES_COLUMN_DESCRIPTION);
                            category_id = jsonObject.getInt(ARTICLES_COLUMN_CATEGORY_ID);
                            own = jsonObject.getBoolean(ARTICLES_COLUMN_OWN);
                            //date = jsonObject.getString(ARTICLES_COLUMN_UPDATE_AT);
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_ID, id);
                            values.put(COLUMN_TITLE, title);
                            values.put(ARTICLES_COLUMN_DESCRIPTION, description);
                            values.put(ARTICLES_COLUMN_CATEGORY_ID, category_id);
                            values.put(ARTICLES_COLUMN_OWN, own ? 1 : 0);
                            values.put(ARTICLES_COLUMN_UPDATE_AT, date);
                            todoUri = getActivity().getContentResolver().insert(CONTENT_URI_ARTICLES, values);

                            articleItem = new ArticleItem(title, description, true,
                                    category_id, "", "", own, "");
                            articleItem.set_id(id);



                            if (imageUri != null) {
                                sendRequestSavePhoto(id, imageUri);
                            }



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
        AppControllerMy.getInstance().addToRequestQueue(req);
    }

    private void editArticleIntoServer() {


        setFieldsTomArticleView();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                HTTP_EDITORS_YOZHIK_SIBEXT_RU_ARTICLES + articleItem.get_id()+JSON, mArticleView,
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
                            if (isChangePhotoEdittable) {
                                sendRequestSavePhoto(id, imageUri);
                            }
                            title = jsonObject.getString(COLUMN_TITLE);
                            description = jsonObject.getString(ARTICLES_COLUMN_DESCRIPTION);
                            category_id = jsonObject.getInt(ARTICLES_COLUMN_CATEGORY_ID);
                            boolean own = jsonObject.getBoolean(ARTICLES_COLUMN_OWN);
                            date = jsonObject.getString(ARTICLES_COLUMN_UPDATE_AT);
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
        AppControllerMy.getInstance().addToRequestQueue(req);
        addPhotoToArticleOnServer();
    }

    private void addPhotoToArticleOnServer() {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                HTTP_EDITORS_YOZHIK_SIBEXT_RU_ARTICLES + articleItem.get_id()+JSON, mArticleView,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int id = 0;
                        String title = "";
                        String description = "";
                        String date="";
                        String url="";
                        int category_id = 0;
                        IStateItemChange iStateItemChange=(IStateItemChange)getActivity();
                        iStateItemChange.addArticleItem();
                        /*try {
                            JSONObject jsonObject = response.getJSONObject("article");
                            id = jsonObject.getInt("id");
                            sendRequestSavePhoto(id, imageUri);
                            title = jsonObject.getString(COLUMN_TITLE);
                            description = jsonObject.getString(ARTICLES_COLUMN_DESCRIPTION);
                            category_id = jsonObject.getInt(ARTICLES_COLUMN_CATEGORY_ID);
                            boolean own = jsonObject.getBoolean(ARTICLES_COLUMN_OWN);
                            date = jsonObject.getString(ARTICLES_COLUMN_UPDATE_AT);
                            url = jsonObject.getJSONObject(ARTICLES_COLUMN_PHOTO).getString("url");

                            ContentValues values = new ContentValues();
                            values.put(COLUMN_ID, id);
                            values.put(COLUMN_TITLE, title);
                            values.put(ARTICLES_COLUMN_DESCRIPTION, description);
                            values.put(ARTICLES_COLUMN_CATEGORY_ID, category_id);
                            values.put(ARTICLES_COLUMN_OWN, own ? 1 : 0);
                            values.put(ARTICLES_COLUMN_UPDATE_AT,date);
                            values.put(ARTICLES_COLUMN_PHOTO,url);
                            getActivity().getContentResolver().insert(todoUri, values);

                            //todo do it !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            //getActivity().getContentResolver().update(CONTENT_URI_ARTICLES, values);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
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
        AppControllerMy.getInstance().addToRequestQueue(req);
    }

    private void setFieldsTomArticleView() {
        mArticleView = new JSONObject();
        try {

            mArticleView.put(COLUMN_TITLE, textTitle.getText().toString());
            mArticleView.put(ARTICLES_COLUMN_DESCRIPTION, textDescription.getText().toString());
            mArticleView.put(ARTICLES_COLUMN_PUBLISHED, true);
            int category_id= (int) spinnerCategory.getSelectedItemId();
            int indexSelectedCategory=listCategory_id.get(category_id).get_id();
            mArticleView.put(ARTICLES_COLUMN_CATEGORY_ID, indexSelectedCategory);
            isChangePhotoEdittable=true;


        } catch (Exception ex) {
            throw  new IllegalArgumentException(ex.getMessage());
        }
    }

    private void sendRequestPhoto(String url) {
        // Retrieves an image specified by the URL, displays it in the UI.
        try {

            ImageRequest request = new ImageRequest(url,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            imagePhoto.setImageBitmap(bitmap);
                        }
                    }, 0, 0, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
            AppControllerMy.getInstance().addToRequestQueue(request);
        }catch (Exception e){
            Log.d("test", e.getMessage());
            e.printStackTrace();
        }
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

    @Override
    public void deleteArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void updateArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void addArticleItem() {

    }

}
