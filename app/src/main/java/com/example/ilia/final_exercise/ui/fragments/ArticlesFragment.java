package com.example.ilia.final_exercise.ui.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.data.containers.Article;
import com.example.ilia.final_exercise.data.containers.Category;
import com.example.ilia.final_exercise.data.model.OpenDBHelper;
import com.example.ilia.final_exercise.ui.interfaces.IActivityArticleInteractionListener;
import com.example.ilia.final_exercise.ui.interfaces.IArticleFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.ilia.final_exercise.data.model.AppContentProvider.*;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ilia on 16.06.15.
 * @author ilia
 */
public class ArticlesFragment extends BaseFragment implements IActivityArticleInteractionListener
		, LoaderManager.LoaderCallbacks<Cursor> {

	private static final int GET_CURRENT_ARTICLE_LOADER = 1;
	private static final int GET_CATEGORIES_LOADER = 2;

	public static final int GET_IMAGE_REQUEST_CODE = 1;

	private static final String PARAM_ARTICLE_ID = "paramArticleId";
	private static final String STATE_ARTICLE_ID = "stateArticleId";
	private static final String STATE_ARTICLE_TITLE = "stateArticleTitle";
	private static final String STATE_ARTICLE_DESCRIPTION = "stateArticleDescription";
	private static final String STATE_ARTICLE_CATEGORY = "stateArticleCategory";
	private static final String STATE_CATEGORIES = "stateCategories";

	private IArticleFragmentInteractionListener mListener;
	private long mArticleId;
	private String mImagePath;
	private ArrayList<Category> mCategories;
	String[] mArticlesProjection;
	String[] mCategoriesProjection;


	private EditText mTitleEdit;
	private EditText mDescriptionEdit;
	private Button mViewButton;
	private Button mEditButton;
	private Button mSaveButton;
	private ImageButton mAddImageButton;

	private Spinner mSpinner;
	private Switch mIsPublishedSwitch;
	private ImageView mImage;
	private boolean mImageChanged;

	public static ArticlesFragment newInstance(long articleId) {
		ArticlesFragment fragment = new ArticlesFragment();
		Bundle args = new Bundle();
		args.putLong(PARAM_ARTICLE_ID, articleId);
		fragment.setArguments(args);
		return fragment;
	}

	public ArticlesFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof IArticleFragmentInteractionListener) {
			mListener = (IArticleFragmentInteractionListener) activity;
			mListener.onRegister(this);
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mArticleId = getArguments().getLong(PARAM_ARTICLE_ID);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_articles, container, false);
		mCategories = new ArrayList<>();
		mTitleEdit = (EditText) view.findViewById(R.id.fragment_article_edit_title);
		mDescriptionEdit = (EditText) view.findViewById(R.id.fragment_article_edit_description);
		mImage = (ImageView) view.findViewById(R.id.fragment_articles_image);
		mSpinner = (Spinner) view.findViewById(R.id.fragment_article_category_spinner);
		mSpinner.setEnabled(false);

		mIsPublishedSwitch = (Switch) view.findViewById(R.id.fragment_article_publish_switch);

		mViewButton = (Button) view.findViewById(R.id.fragment_article_view_button);
		mViewButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onViewModeButtonClicked();
			}
		});

		mEditButton = (Button) view.findViewById(R.id.fragment_article_edit_button);
		mEditButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onEditModeButtonClicked();
			}
		});

		if (savedInstanceState != null) {
			mCategories = savedInstanceState.getParcelableArrayList(STATE_CATEGORIES);
			onCategoriesReceived(savedInstanceState.getInt(STATE_ARTICLE_CATEGORY));
			mArticleId = savedInstanceState.getLong(STATE_ARTICLE_ID);
			mTitleEdit.setText(savedInstanceState.getString(STATE_ARTICLE_TITLE));
			mDescriptionEdit.setText(savedInstanceState.getString(STATE_ARTICLE_DESCRIPTION));

		} else {
			getCategories();
		}

		mSaveButton = (Button) view.findViewById(R.id.fragment_article_save_button);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSavePressed();
			}
		});

		mAddImageButton = (ImageButton) view.findViewById(R.id.fragment_article_image_change_button);
		mAddImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onChangeImage();
			}
		});
		mArticlesProjection = new String[]{OpenDBHelper.COLUMN_ID
				, OpenDBHelper.ARTICLES_CATEGORY_ID
				, OpenDBHelper.ARTICLES_TITLE
				, OpenDBHelper.ARTICLES_DESCRIPTION
				, OpenDBHelper.ARTICLES_PHOTO_URL
				, OpenDBHelper.ARTICLES_PUBLISHED
				, OpenDBHelper.ARTICLES_CREATED
				, OpenDBHelper.ARTICLES_UPDATED
				, OpenDBHelper.ARTICLES_OWN
		};

		mCategoriesProjection = new String[]{OpenDBHelper.COLUMN_ID
				, OpenDBHelper.CATEGORIES_TITLE
		};

		return view;
	}

	private void getCategoriesFromDb() {
		getActivity().getLoaderManager().restartLoader(GET_CATEGORIES_LOADER, null, this);
	}

	private void getCategories() {

		getActivity().getLoaderManager().initLoader(GET_CATEGORIES_LOADER, null, this);

		getCategoriesRequest(new IResponseListener() {
			@Override
			public void onResponse(long id) {
				getCategoriesFromDb();
			}
		}, null);
	}

	private void addCategoriesFromCursor(Cursor cursor) {

		mCategories = new ArrayList<>();
		while (cursor.moveToNext()) {
			Category category = Category.fromCursor(cursor);
			mCategories.add(category);
		}
		cursor.close();
		onCategoriesReceived(0);
	}

	private void onCategoriesReceived(int initCategoryIndex) {
		ArrayAdapter<Category> adapter = new ArrayAdapter<>(getActivity()
				, android.R.layout.simple_spinner_item
				, mCategories.toArray(new Category[mCategories.size()]));
		mSpinner.setAdapter(adapter);
		mSpinner.setSelection(initCategoryIndex);
	}

	private void onViewModeButtonClicked() {
		disableUiFields();
		mViewButton.setEnabled(false);
		mEditButton.setEnabled(true);
	}

	private void disableUiFields() {
		mTitleEdit.setEnabled(false);
		mDescriptionEdit.setEnabled(false);
		mSaveButton.setVisibility(View.INVISIBLE);
		mIsPublishedSwitch.setEnabled(false);
		mSpinner.setEnabled(false);
		mAddImageButton.setEnabled(false);
	}

	private void onEditModeButtonClicked() {
		mTitleEdit.setEnabled(true);
		mDescriptionEdit.setEnabled(true);
		mSaveButton.setVisibility(View.VISIBLE);
		mIsPublishedSwitch.setEnabled(true);
		mEditButton.setEnabled(false);
		mViewButton.setEnabled(true);
		mAddImageButton.setEnabled(true);
		mSpinner.setEnabled(true);
	}

	private void onChangeImage() {
		Intent intent = new Intent();
		intent.setType(getString(R.string.fragment_action_image_type));
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, getString(R.string.fragment_action_select_image_text))
				, GET_IMAGE_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GET_IMAGE_REQUEST_CODE && resultCode == RESULT_OK
				&& data != null && data.getData() != null) {

			Uri uri = data.getData();

			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
				mImage.setImageBitmap(bitmap);
				mImagePath = uri.toString();
				mImageChanged = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void onSavePressed() {
		String title = mTitleEdit.getText().toString();
		String description = mDescriptionEdit.getText().toString();
		long categoryId = mCategories.get(mSpinner.getSelectedItemPosition()).getId();

		if (description.length() == 0 || title.length() == 0) {
			return;
		}

		Article article = new Article(mArticleId, title, description, "", true, categoryId
				, 0, 0, true);

		if (mArticleId < 0) {
			addArticleRequest(article, mImageChanged ? mImagePath : null, new IResponseListener() {
				@Override
				public void onResponse(long id) {
					mArticleId = id;
				}
			}, null);

		} else {
			editArticleRequest(article, mImageChanged ? mImagePath : null, null, null);
		}
	}

	@Override
	public void onCreateNewArticle() {
		mArticleId = -1;
		clearFields();
		onEditModeButtonClicked();
	}

	@Override
	public void onOpenArticle(long id) {
		Bundle args = new Bundle();
		args.putLong(PARAM_ARTICLE_ID, id);
		getLoaderManager().restartLoader(GET_CURRENT_ARTICLE_LOADER, args, this);
	}

	@Override
	public void onDeleteArticle(long id) {
		if (mArticleId == id) {
			// clear fields
			clearFields();
			disableUiFields();
		}
	}

	private void clearFields() {
		mImagePath = null;
		mImageChanged = false;
		mTitleEdit.setText("");
		mDescriptionEdit.setText("");
		mSpinner.setSelection(0);
		mIsPublishedSwitch.setChecked(false);
		Picasso.with(getActivity()).load(R.drawable.default_photo).into(mImage);
	}

	private void fillUiWithData(Cursor cursor) {
		cursor.moveToFirst();
		Article currentArticle = Article.fromCursor(cursor);
		mArticleId = currentArticle.getId();
		mTitleEdit.setText(currentArticle.getTitle());
		mDescriptionEdit.setText(currentArticle.getDescription());
		mIsPublishedSwitch.setChecked(currentArticle.isPublished());

		int spinnerSelection = getCategoryIndexById(currentArticle.getCategoryId());
		if (spinnerSelection >= 0) {
			mSpinner.setSelection(spinnerSelection);
		}

		if (!TextUtils.isEmpty(currentArticle.getPhotoUrl())) {
			Picasso.with(getActivity()).load(currentArticle.getPhotoUrl()).into(mImage);
		} else {
			Picasso.with(getActivity()).load(R.drawable.default_photo).into(mImage);
		}

		mEditButton.setEnabled(currentArticle.getIsMine());
		mViewButton.setEnabled(false);
		mAddImageButton.setEnabled(false);
		cursor.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id) {
			case GET_CURRENT_ARTICLE_LOADER:
				long articleId = args.getLong(PARAM_ARTICLE_ID);
				return new CursorLoader(
						getActivity(),
						getArticlesUri(articleId),
						mArticlesProjection, null, null, null
				);
			case GET_CATEGORIES_LOADER:
				return new CursorLoader(
						getActivity(),
						CONTENT_URI_CATEGORIES,
						mCategoriesProjection, null, null, null
				);
			default:
				return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {

			case GET_CURRENT_ARTICLE_LOADER:
				if (data != null) {
					fillUiWithData(data);
				}
				break;

			case GET_CATEGORIES_LOADER:
				if (data != null) {
					addCategoriesFromCursor(data);
				}
				break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(STATE_CATEGORIES, mCategories);
		outState.putString(STATE_ARTICLE_TITLE, mTitleEdit.getText().toString());
		outState.putString(STATE_ARTICLE_DESCRIPTION, mDescriptionEdit.getText().toString());
		outState.putInt(STATE_ARTICLE_CATEGORY, mSpinner.getSelectedItemPosition());
	}

	private int getCategoryIndexById(long id) {
		for (int i = 0; i < mCategories.size(); i++) {
			if (mCategories.get(i).getId() == id) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener.onUnregister(this);
		mListener = null;
	}

}