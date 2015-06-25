package com.example.ilia.final_exercise.data.api.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ilia on 23.06.15.
 *
 * @author ilia
 */
public class DataResponse implements Parcelable {
    public static final Creator<DataResponse> CREATOR
            = new Creator<DataResponse>() {
        public DataResponse createFromParcel(Parcel in) {
            return new DataResponse(in);
        }

        public DataResponse[] newArray(int size) {
            return new DataResponse[size];
        }
    };
    private long id;

    public DataResponse() {
        id = 0;
    }

    public DataResponse(long id) {
        this.id = id;
    }

    public DataResponse(Parcel in) {
        id = in.readLong();
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
    }
}