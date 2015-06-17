package com.example.ilia.final_exercise.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.database.AppContentProvider;
import com.example.ilia.final_exercise.database.AppSQLiteOpenHelper;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.interfaces.IStateItemChange;
import com.example.ilia.final_exercise.database.ArticleItem;

import java.util.Date;

import static com.example.ilia.final_exercise.database.AppSQLiteOpenHelper.*;

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

    @Override
    public void getArticleToAnotherFragment(ArticleItem articleItem) {
        mArticleItem = articleItem;
        textTitle.setText(articleItem.getmTitle());
        textDescription.setText(articleItem.getmDescription());
        switchPublished.setSelected(articleItem.ismPublished());

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
                    String title		=  textTitle.getText().toString();
                    String description	= textDescription.getText().toString();
                    long updated		= (new Date()).getTime();
                    //long categoryId		= mCategories.get(mSpinner.getSelectedItemPosition()).getId();
                    //if (mCreatedDate == 0) mCreatedDate = updated;
                    boolean isPublished	= switchPublished.isChecked();
                    // only save if title or description
                    // is available

                    if (description.length() == 0 || title.length() == 0) {
                        return;
                    }

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TITLE, 		title);
                    values.put(ARTICLES_COLUMN_DESCRIPTION, 	description);
                    values.put(ARTICLES_COLUMN_CATEGORY_ID, 	0);
                    values.put(ARTICLES_COLUMN_CREATE_AT, 		updated);
                    values.put(ARTICLES_COLUMN_UPDATE_AT, 		updated);
                    values.put(ARTICLES_COLUMN_PUBLISHED, 	isPublished);


                    /*if (mArticleUri == null) {
                        // New todo
                        mArticleUri = AppController.getAppContext().getContentResolver()
                                .insert(AppContentProvider.CONTENT_URI_ARTICLES, values);
                    } else {
                        // Update todo
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
