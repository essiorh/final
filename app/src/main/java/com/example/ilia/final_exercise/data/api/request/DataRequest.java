package com.example.ilia.final_exercise.data.api.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.ilia.final_exercise.data.containers.Article;

/**
 * Created by ilia on 23.06.15.
 * @author ilia
 */
public class DataRequest implements Parcelable {
	private Article article;
	private String uri;

	public DataRequest(Article article, String uri) {
		this.article = article;
		this.uri = uri;
	}

	public Article getArticle() {
		return article;
	}

	public String getAdditionalData() {
		return uri;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(article, flags);
		dest.writeString(uri);
	}

	public static final Creator<DataRequest> CREATOR
			= new Creator<DataRequest>() {
		public DataRequest createFromParcel(Parcel in) {
			return new DataRequest(in);
		}

		public DataRequest[] newArray(int size) {
			return new DataRequest[size];
		}
	};

	public DataRequest(Parcel in) {
		article = in.readParcelable(Article.class.getClassLoader());
		uri = in.readString();
	}
}
