package com.example.ilia.final_exercise.data.containers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ilia on 23.06.15.
 * @author ilia
 */
public class PhotoContainer implements Parcelable {

	private String url;

	public PhotoContainer(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}


	public PhotoContainer(Parcel in) {
		url = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
	}

	public static final Creator<PhotoContainer> CREATOR = new Creator<PhotoContainer>() {

		public PhotoContainer createFromParcel(Parcel in) {
			return new PhotoContainer(in);
		}

		public PhotoContainer[] newArray(int size) {
			return new PhotoContainer[size];
		}
	};
}