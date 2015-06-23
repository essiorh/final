package com.example.ilia.final_exercise.data.api;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.ResultReceiver;

import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.data.api.request.DataRequest;
import com.example.ilia.final_exercise.data.api.request.DeleteDataRequest;

/**
 * Created by grigoriy on 16.06.15.
 */
public class ApiServiceHelper {
	private static final String TAG = "ApiServiceHelper";

	private static ApiServiceHelper instance;

	public static ApiServiceHelper getInstance() {
		if (instance == null) {
			instance = new ApiServiceHelper();
		}
		return instance;
	}

	private ApiServiceHelper() {
		super();
	}

	public void getCategories( ResultReceiver onServiceResult){
		startService(null, ApiService.ACTION_GET_CATEGORIES, onServiceResult);
	}

	public void getArticles( ResultReceiver onServiceResult){
		startService(null, ApiService.ACTION_GET_ARTICLES, onServiceResult);
	}

	public void addArticle(DataRequest data, ResultReceiver onServiceResult){
		startService(data, ApiService.ACTION_ADD_ARTICLE, onServiceResult);
	}

	public void editArticle(DataRequest data, ResultReceiver onServiceResult){
		startService(data, ApiService.ACTION_EDIT_ARTICLE, onServiceResult);
	}

	public void deleteArticle(DeleteDataRequest data, ResultReceiver onServiceResult){
		startService(data, ApiService.ACTION_DELETE_ARTICLE, onServiceResult);
	}

	 /*
    utils
     */

	private void startService(Parcelable data, int action, ResultReceiver onServiceResult) {
		Intent intent = getIntent(action, onServiceResult);
		if(data != null){
			intent.putExtra(ApiService.REQUEST_OBJECT_KEY, data);
		}
		getContext().startService(intent);
	}

	private Intent getIntent(int action, ResultReceiver onServiceResult) {
		final Intent i = new Intent(getContext(), ApiService.class);
		i.putExtra(ApiService.ACTION_KEY, action);
		i.putExtra(ApiService.CALLBACK_KEY, onServiceResult);
		return i;
	}

	private Context getContext(){
		return AppController.getAppContext();
	}
}
