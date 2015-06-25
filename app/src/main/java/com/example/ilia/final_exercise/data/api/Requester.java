package com.example.ilia.final_exercise.data.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.ilia.final_exercise.AppController;
import com.example.ilia.final_exercise.data.api.request.DataRequest;
import com.example.ilia.final_exercise.data.api.request.DeleteDataRequest;
import com.example.ilia.final_exercise.data.api.response.DataResponse;
import com.example.ilia.final_exercise.data.containers.Article;
import com.example.ilia.final_exercise.data.containers.Category;
import com.example.ilia.final_exercise.ui.framework.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;

import static com.example.ilia.final_exercise.data.model.AppContentProvider.*;
import static com.example.ilia.final_exercise.data.model.OpenDBHelper.*;

/**
 * Created by ilia on 23.06.15.
 * @author ilia
 */
public class Requester {

	private static final String SERVER = "http://editors.yozhik.sibext.ru/";

	public Requester() {
	}

	public DataResponse getCategories() {

		RestClient restClient = new RestClient();
		String url = getCategoriesUrl();
		ApiResponse response = restClient.doGet(url);
		Gson gson = new Gson();

		CategoriesContainer responseContainer = deserialize(gson
				, response
				, CategoriesContainer.class);

		if (responseContainer != null) {

			ArrayList<Long> ids = new ArrayList<>();
			Cursor cursor = AppController.getAppContext().getContentResolver()
					.query(CONTENT_URI_CATEGORIES
							, new String[]{COLUMN_ID}
							, null
							, null
							, null);
			while (cursor.moveToNext()) {
				ids.add(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
			}
			cursor.close();

			for (Category category : responseContainer.categories) {
				ids.remove(category.getId());

				AppController.getAppContext().getContentResolver()
						.insert(CONTENT_URI_CATEGORIES, category.buildContentValues());
			}

			if (ids.size() > 0) {
				AppController.getAppContext().getContentResolver()
						.delete(CONTENT_URI_CATEGORIES
								, formatArrayCondition(COLUMN_ID, ids), null);
			}
		}
		return new DataResponse();
	}


	public DataResponse getArticles() {

		RestClient restClient = new RestClient();
		String url = getArticlesUrl();
		ApiResponse response = restClient.doGet(url);
		Gson gson = getGson();
		ArticlesContainer responseContainer = deserialize(gson, response, ArticlesContainer.class);

		if (responseContainer != null && responseContainer.articles != null) {
			ArrayList<Long> ids = new ArrayList<>();
			Cursor cursor = AppController.getAppContext().getContentResolver()
					.query(CONTENT_URI_ARTICLES
							, new String[]{COLUMN_ID}, null, null, null);

			while (cursor.moveToNext()) {
				ids.add(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
			}

			cursor.close();

			for (Article article : responseContainer.articles) {
				ids.remove(article.getId());

				AppController.getAppContext().getContentResolver()
						.insert(CONTENT_URI_ARTICLES,
								article.buildContentValues());
			}

			if (ids.size() > 0) {
				AppController.getAppContext().getContentResolver()
						.delete(getArticlesUri()
								, formatArrayCondition(COLUMN_ID, ids), null);
			}
		}
		return new DataResponse();
	}

	public DataResponse addArticle(DataRequest request) {
		long id = -1;
		RestClient restClient = new RestClient();
		String url = putArticleUrl();
		Gson gsonSerializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.setPrettyPrinting().create();
		String jsonContent = gsonSerializer.toJson(request.getArticle());
		Gson gsonDeserializer = getGson();
		ApiResponse response = restClient.doPost(url, null, jsonContent);
		ArticleContainer responseContainer =
				deserialize(gsonDeserializer, response, ArticleContainer.class);

		if (responseContainer != null && responseContainer.article != null) {
			id = responseContainer.article.getId();
			AppController.getAppContext().getContentResolver()
					.insert(CONTENT_URI_ARTICLES
							, responseContainer.article.buildContentValues());

			if (!TextUtils.isEmpty(request.getAdditionalData())) {
				addPhotoToArticle(id, request.getAdditionalData());
			}
		}
		return new DataResponse(id);
	}

	public DataResponse deleteArticle(DeleteDataRequest articleId) {

		long id = articleId.getId();
		String url = deleteArticleUrl(id);
		RestClient restClient = new RestClient();
		ApiResponse response = restClient.doDelete(url);

		if (response.getStatus() == 200) {
			AppController.getAppContext().getContentResolver()
					.delete(getArticlesUri(id), null, null);
		} else {
			return new DataResponse(-1);
		}
		return new DataResponse(id);
	}

	public DataResponse editArticle(DataRequest request) {

		if (request.getArticle() == null) {
			return new DataResponse(-1);
		}

		long id = request.getArticle().getId();
		String url = editArticleUrl(id);
		Gson gsonSerializer = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String jsonContent = gsonSerializer.toJson(request.getArticle());
		RestClient restClient = new RestClient();
		Gson gson = getGson();

		ApiResponse response = restClient.doPut(url, jsonContent);
		ArticleContainer responseContainer = deserialize(gson, response, ArticleContainer.class);

		if (responseContainer != null && responseContainer.article != null) {
			id = responseContainer.article.getId();
			AppController.getAppContext().getContentResolver()
					.update(getArticlesUri(id)
							, responseContainer.article.buildContentValues(), null, null);
			if (!TextUtils.isEmpty(request.getAdditionalData())) {
				addPhotoToArticle(id, request.getAdditionalData());
			}
		} else {
			new DataResponse(-1);
		}
		return new DataResponse(id);
	}

	private void addPhotoToArticle(long id, String imagePath) {

		RestClient restClient = new RestClient();
		String url = addImageUrl(id);
		Gson gson = new Gson();
		Uri uri = Uri.parse(imagePath);
		String utils = Utils.getPath(AppController.getAppContext(), uri);
		if (utils == null) return;
		File file = new File(utils);
		String response = restClient.doUploadFile(url, file, "photo[image]");
		PhotoContainer responseContainer = deserialize(gson, response, PhotoContainer.class);

		if (responseContainer != null && responseContainer.photo != null) {
			ContentValues values = new ContentValues();
			values.put(ARTICLES_PHOTO_URL, responseContainer.photo.url);
			AppController.getAppContext().getContentResolver()
					.update(getArticlesUri(id)
							, values, null, null);
		}
	}

	@NonNull
	private Gson getGson() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss Z").create();
	}

	private String getCategoriesUrl() {
		return SERVER + "categories.json";
	}

	private String getArticlesUrl() {
		return SERVER + "articles.json";
	}

	private String putArticleUrl() {
		return SERVER + "articles.json";
	}

	private String editArticleUrl(long id) {
		return SERVER + String.format("articles/%d.json", id);
	}

	private String deleteArticleUrl(long id) {
		return SERVER + String.format("articles/%d.json", id);
	}

	private String addImageUrl(long id) {
		return SERVER + String.format("articles/%d/photos.json", id);
	}

	private String formatArrayCondition(String field, ArrayList<Long> ids) {
		StringBuilder result = new StringBuilder();

		result.append(field);
		result.append(" in (");

		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) {
				result.append(", ");
			}
			result.append(ids.get(i).toString());
		}

		result.append(" ) ");

		return result.toString();
	}

	private static class CategoriesContainer {
		public Category[] categories;
	}

	private static class ArticlesContainer {
		public Article[] articles;
	}

	private static class ArticleContainer {
		public Article article;
	}

	private static class PhotoContainer {
		class Photo {
			public long id;
			public String url;
		}

		public Photo photo;
	}

	private <T> T deserialize(Gson gson, ApiResponse response, Class<T> classOfT) {

		if (response != null) {
			try {
				return gson.fromJson(response.getInputStreamReader(), classOfT);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	private <T> T deserialize(Gson gson, String response, Class<T> classOfT) {
		if (response != null) {
			try {
				return gson.fromJson(response, classOfT);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}

