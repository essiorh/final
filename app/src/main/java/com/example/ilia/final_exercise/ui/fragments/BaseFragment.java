package com.example.ilia.final_exercise.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.example.ilia.final_exercise.data.api.request.DataRequest;
import com.example.ilia.final_exercise.data.api.request.DeleteDataRequest;
import com.example.ilia.final_exercise.data.api.response.DataResponse;
import com.example.ilia.final_exercise.data.containers.Article;

import static com.example.ilia.final_exercise.data.api.ApiService.ERROR_KEY;
import static com.example.ilia.final_exercise.data.api.ApiService.RESPONSE_OBJECT_KEY;
import static com.example.ilia.final_exercise.data.api.ApiServiceHelper.addArticle;
import static com.example.ilia.final_exercise.data.api.ApiServiceHelper.deleteArticle;
import static com.example.ilia.final_exercise.data.api.ApiServiceHelper.editArticle;
import static com.example.ilia.final_exercise.data.api.ApiServiceHelper.getArticles;
import static com.example.ilia.final_exercise.data.api.ApiServiceHelper.getCategories;

/**
 * Created by ilia on 16.06.15.
 *
 * @author ilia
 */
public abstract class BaseFragment extends Fragment {

    public void getArticlesRequest(final IResponseListener responseListener
            , final IErrorListener errorListener) {

        getArticles(new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData.containsKey(ERROR_KEY)) {
                    if (errorListener != null) {
                        errorListener.onError();
                    }
                } else {
                    if (responseListener != null) {
                        responseListener.onResponse(0L);
                    }
                }
            }
        });
    }

    public void addArticleRequest(Article article, String imagePath
            , final IResponseListener responseListener
            , final IErrorListener errorListener) {

        addArticle(new DataRequest(article, imagePath)
                , new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData.containsKey(ERROR_KEY)) {
                    if (errorListener != null) {
                        errorListener.onError();
                    }
                } else {

                    DataResponse response = resultData
                            .getParcelable(RESPONSE_OBJECT_KEY);
                    if (responseListener != null) {
                        responseListener.onResponse(response.getId());
                    }
                }
            }
        });
    }

    public void editArticleRequest(Article article, String imagePath
            , final IResponseListener responseListener
            , final IErrorListener errorListener) {

        editArticle(new DataRequest(article, imagePath), new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData.containsKey(ERROR_KEY)) {
                    if (errorListener != null) {
                        errorListener.onError();
                    }
                } else {
                    DataResponse response = resultData
                            .getParcelable(RESPONSE_OBJECT_KEY);
                    if (responseListener != null) {
                        responseListener.onResponse(response.getId());
                    }
                }
            }
        });
    }

    public void deleteArticleRequest(long id
            , final IResponseListener responseListener
            , final IErrorListener errorListener) {

        deleteArticle(new DeleteDataRequest(id)
                , new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData.containsKey(ERROR_KEY)) {
                    if (errorListener != null) {
                        errorListener.onError();
                    }
                } else {
                    if (responseListener != null) {
                        responseListener.onResponse(0L);
                    }
                }
            }
        });
    }

    public void getCategoriesRequest(final IResponseListener responseListener
            , final IErrorListener errorListener) {

        getCategories(new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData.containsKey(ERROR_KEY)) {
                    if (errorListener != null) {
                        errorListener.onError();
                    }
                } else {
                    if (responseListener != null) {
                        responseListener.onResponse(0L);
                    }
                }
            }
        });
    }

    protected interface IErrorListener {
        void onError();
    }

    protected interface IResponseListener {
        void onResponse(long id);
    }
}
