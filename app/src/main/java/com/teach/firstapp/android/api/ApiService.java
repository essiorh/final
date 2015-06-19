package com.teach.firstapp.android.api;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.teach.firstapp.android.api.reponse.DataResponse;
import com.teach.firstapp.android.api.request.DataRequest;

import java.io.Serializable;

public class ApiService extends IntentService {

    private static final String TAG = "ApiService";

    public static final String CALLBACK_KEY = "CALLBACK_KEY";
    public static final String ACTION_KEY = "ACTION_KEY";
    public static final String ERROR_KEY = "ERROR_KEY";
    public static final String REQUEST_OBJECT_KEY = "REQUEST_OBJECT_KEY";
    public static final String RESPONSE_OBJECT_KEY = "RESPONSE_OBJECT_KEY";

    public static final int ACTION_GET_DATA = 1;

    private boolean destroyed;
    private ResultReceiver receiver;

    public ApiService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra(CALLBACK_KEY);
        int action = intent.getIntExtra(ACTION_KEY, -1);
        Bundle data = processIntent(intent, action);
        sentMessage(action, data);
    }

    private Bundle processIntent(Intent intent, int  action){
        switch (action){
            case ACTION_GET_DATA:
                return getData((DataRequest) getRequestObject(intent));
        }
        return null;
    }



    private Bundle getData(DataRequest request){
        Requester requester = new Requester();
        Bundle bundle = new Bundle();
        DataResponse response = requester.getData(request);
        if(response == null){
            bundle.putBoolean(ERROR_KEY, true);
        } else {
            bundle.putSerializable(RESPONSE_OBJECT_KEY, response);
        }
        return bundle;
    }



    private Serializable getRequestObject(Intent intent){
        return intent.getSerializableExtra(REQUEST_OBJECT_KEY);
    }

    private void sentMessage(int code, Bundle data){
        if(!destroyed && receiver != null){
            receiver.send(code, data);
        }
    }

    @Override
    public void onCreate() {
        destroyed = false;
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        receiver = null;
        Log.d(TAG, "ResourceLoadService: onDestroy");
        super.onDestroy();
    }
}
