package com.example.ilia.final_exercise.data.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.data.api.request.DataRequest;
import com.example.ilia.final_exercise.data.api.request.DeleteDataRequest;
import com.example.ilia.final_exercise.data.api.response.DataResponse;
import com.example.ilia.final_exercise.data.containers.Article;
import com.example.ilia.final_exercise.data.model.AppContentProviderGrisha;
import com.example.ilia.final_exercise.data.containers.Category;
import com.example.ilia.final_exercise.data.model.DbHelper;
import com.example.ilia.final_exercise.ui.framework.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by grigoriy on 16.06.15.
 */
public class Requester {
	private static final String TAG = "Requester";
	private static final String SERVER = "http://editors.yozhik.sibext.ru/";

	public Requester() {
	}

	public DataResponse getCategories() {

		RestClient restClient 	= new RestClient();
		String url 				= getCategoriesUrl();
		ApiResponse response 	= restClient.doGet(url);
		Gson gson = new Gson();

		CategoriesContainer responseContainer = deSerealize(gson
				, response
				, CategoriesContainer.class);

		if(responseContainer != null){
			// synchronize categories with DB

			ArrayList<Long> ids = new ArrayList<>();
			// delete categories, that not exists in server's categories list
			// 1) retrive categories ID  from DB
			Cursor cursor	= AppController.getAppContext().getContentResolver()
					.query(AppContentProviderGrisha.CONTENT_URI_CATEGORIES
							, new String[]{DbHelper.COLUMN_ID}
							, null
							, null
							, null);
			// 2) remove IDs from server's elements
			while (cursor.moveToNext()){
				ids.add(cursor.getLong( cursor.getColumnIndex(DbHelper.COLUMN_ID)) );
			}
			cursor.close();

			// 3) delete excess elements from db
			for (Category category: responseContainer.categories){
				ids.remove(category.getId());

				AppController.getAppContext().getContentResolver()
						.insert(AppContentProviderGrisha.CONTENT_URI_CATEGORIES, category.buildContentValues());
			}

			// delete categories, than not exists in server
			if(ids.size() > 0) {
				AppController.getAppContext().getContentResolver()
						.delete(AppContentProviderGrisha.CONTENT_URI_CATEGORIES
								, formatArrayCondition(DbHelper.COLUMN_ID, ids), null);
			}
		}
		return new DataResponse();
	}


	public DataResponse getArticles() {

		RestClient restClient 	= new RestClient();
		String url 				= getArticlesUrl();
		ApiResponse response 	= restClient.doGet(url);
		Gson gson 				= getGson();
		ArticlesContainer responseContainer = deSerealize(gson, response, ArticlesContainer.class);

		// store to base
		if(responseContainer != null && responseContainer.articles != null){
			ArrayList<Long> ids = new ArrayList<>();
			// delete articles, that not exists in server's articles list
			Cursor cursor	= AppController.getAppContext().getContentResolver()
					.query(AppContentProviderGrisha.CONTENT_URI_ARTICLES
							, new String[]{DbHelper.COLUMN_ID}
							, null
							, null
							, null);
			while (cursor.moveToNext()){
				ids.add(cursor.getLong( cursor.getColumnIndex(DbHelper.COLUMN_ID)) );
			}

			cursor.close();

			for (Article article: responseContainer.articles){
				ids.remove(article.getId());

				AppController.getAppContext().getContentResolver()
						.insert(AppContentProviderGrisha.CONTENT_URI_ARTICLES, article.buildContentValues());
			}

			// delete articles, than not exists in server
			if(ids.size() > 0) {
				AppController.getAppContext().getContentResolver()
						.delete(AppContentProviderGrisha.getArticlesUri()
								, formatArrayCondition(DbHelper.COLUMN_ID, ids), null);
			}
		}
		return new DataResponse();
	}

	public DataResponse addArticle(DataRequest request) {
		long id	= -1;
		RestClient restClient	= new RestClient();
		String url				= putArticleUrl();
		Gson gsonSerializer		= new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.setPrettyPrinting().create();
		String jsonContent 		= gsonSerializer.toJson(request.getArticle());
		Gson gsonDeserialazer	= getGson();
		ApiResponse response	= restClient.doPost(url, null, jsonContent);
		ArticleContainer responseContainer = deSerealize(gsonDeserialazer, response, ArticleContainer.class);

		if(responseContainer != null && responseContainer.article != null){
			//insert into db
			id	= responseContainer.article.getId();
			AppController.getAppContext().getContentResolver()
					.insert(AppContentProviderGrisha.CONTENT_URI_ARTICLES
							, responseContainer.article.buildContentValues());

			if(!TextUtils.isEmpty(request.getAdditionalData())){
				addPhotoToArticle(id, request.getAdditionalData());
			}

			Log.d(TAG, "article succesful sended");
		}

		return new DataResponse(id);
	}

	public DataResponse deleteArticle(DeleteDataRequest articleId) {

		long id					= articleId.getId();
		String url				= deleteArticleUrl(id);
		RestClient restClient	= new RestClient();
		ApiResponse response	= restClient.doDelete(url);

		if(response.status == 200){
			//delete in db
			AppController.getAppContext().getContentResolver()
					.delete(AppContentProviderGrisha.getArticlesUri(id), null, null);
			Log.d(TAG, "article succesful edited");
		} else {
			return new DataResponse(-1);
		}

		return new DataResponse(id);
	}

	public DataResponse editArticle(DataRequest request) {

		if(request.getArticle() == null){
			// bad request object
			return new DataResponse(-1);
		}

		long 		id				= request.getArticle().getId();
		String 		url				= editArticleUrl(id);
		Gson 		gsonSerializer	= new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String 		jsonContent 	= gsonSerializer.toJson(request.getArticle());
		RestClient	restClient		= new RestClient();
		Gson 		gson			= getGson();

		ApiResponse response		= restClient.doPut(url, jsonContent);
		ArticleContainer responseContainer = deSerealize(gson, response, ArticleContainer.class);

		if(responseContainer != null && responseContainer.article != null){
			//update in db
			id	= responseContainer.article.getId();
			AppController.getAppContext().getContentResolver()
					.update(AppContentProviderGrisha.getArticlesUri(id)
							, responseContainer.article.buildContentValues(), null, null);
			if(!TextUtils.isEmpty(request.getAdditionalData())){
				addPhotoToArticle(id, request.getAdditionalData());
			}
			Log.d(TAG, "article succesful edited");
		} else {
			new DataResponse(-1);
		}

		return new DataResponse(id);
	}

	private void addPhotoToArticle(long id, String imagePath) {

		RestClient 		restClient			= new RestClient();
		String 			url					= addImageUrl(id);
		Gson 			gson				= new Gson();
		Uri 			uri					= Uri.parse(imagePath);
		File 			file				= new File(Utils.getPath(AppController.getAppContext()
												,uri));
		String 			response			= restClient.doUploadFile(url, file, "photo[image]");
		PhotoContainer 	responseContainer	= deSerealize(gson, response, PhotoContainer.class);

		if(responseContainer != null && responseContainer.photo != null) {
			//update in db
			ContentValues values	= new ContentValues();
			values.put(DbHelper.ARTICLES_PHOTO_URL,responseContainer.photo.url);
			AppController.getAppContext().getContentResolver()
					.update(AppContentProviderGrisha.getArticlesUri(id)
							, values, null, null);
			Log.d(TAG, "photo added succesfuly");
		}
	}

	@NonNull
	private Gson getGson() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
	}

	private String getCategoriesUrl(){
		return SERVER + "categories.json";
	}

	private String getArticlesUrl(){
		return SERVER + "articles.json";
	}

	private String putArticleUrl(){
		return SERVER + "articles.json";
	}

	private String editArticleUrl(long id){
		return SERVER + String.format("articles/%d.json",id);
	}

	private String deleteArticleUrl(long id){
		return SERVER + String.format("articles/%d.json",id);
	}

	private String addImageUrl(long id){
		return SERVER + String.format("articles/%d/photos.json",id);
	}

	private String formatArrayCondition(String field, ArrayList<Long> ids){
		StringBuilder result = new StringBuilder();

		result.append(field);
		result.append(" in (");

		for (int i = 0; i< ids.size(); i++){
			if(i > 0) {
				result.append(", ");
			}
			result.append(ids.get(i).toString());
		}

		result.append(" ) ");

		return result.toString();
	}

	private static  class CategoriesContainer{
		public Category[] categories;
	}

	private static  class ArticlesContainer{
		public Article[] articles;
	}

	private static  class ArticleContainer{
		public Article article;
	}

	private static  class PhotoContainer{
		class Photo{
			public long id;
			public String url;
		}
		public Photo photo;
	}

	private  <T> T deSerealize(Gson gson, ApiResponse response, Class<T> classOfT){

		if(response != null){
			try {
				return gson.fromJson(response.getInputStreamReader(), classOfT);
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	private  <T> T deSerealize(Gson gson, String response, Class<T> classOfT){
		if(response != null){
			try {
				return gson.fromJson(response, classOfT);
			} catch (Exception e){
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}

