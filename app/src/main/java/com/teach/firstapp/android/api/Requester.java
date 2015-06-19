package com.teach.firstapp.android.api;

import com.google.gson.Gson;
import com.teach.firstapp.android.api.reponse.DataResponse;
import com.teach.firstapp.android.api.request.DataRequest;

public class Requester {
    private static final String TAG = "Requester";

    private static final String SERVER = "http://test.com/";

    public Requester() {
    }

    public DataResponse getData(DataRequest request){
        RestClient restClient = new RestClient();
        String url = getDataUrl();
        ApiResponse response = restClient.doGet(url);

        Gson gson = new Gson();

//        Response loginResponse = null;
//        try {
//            loginResponse = gson.fromJson(response.getInputStreamReader(), Response.class);
//        } catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//        if(loginResponse == null){
//            return false;
//        }
//
//        TestApplication.getAppContext().getContentResolver().insert(...);

        return new DataResponse();
    }

    private String getDataUrl(){
        return SERVER + "data.json";
    }
}
