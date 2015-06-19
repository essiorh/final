package com.teach.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;

import com.teach.firstapp.android.api.ApiService;
import com.teach.firstapp.android.api.ApiServiceHelper;
import com.teach.firstapp.android.api.reponse.DataResponse;
import com.teach.firstapp.android.api.request.DataRequest;

public class SampleActivity extends Activity implements View.OnClickListener{

    Button toFirstButton;
    Button toSecondButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_activity_layout);

        toFirstButton = (Button) findViewById(R.id.sample_first);
        toSecondButton = (Button) findViewById(R.id.sample_second);

        toFirstButton .setOnClickListener(this);
        toSecondButton.setOnClickListener(this);

        ApiServiceHelper.getInstance().getData(new DataRequest(), new ResultReceiver(new Handler()){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if(resultData.containsKey(ApiService.ERROR_KEY)){

                } else {
                    DataResponse response = (DataResponse)resultData.getSerializable(ApiService.RESPONSE_OBJECT_KEY);

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sample_first:
                openMessage("First message");
                break;
            case R.id.sample_second:
                openMessage("Second message");
                break;
        }
    }

    private void openMessage(String msg){
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.MESSAGE_BODY_KEY, msg);
        startActivity(intent);
    }
}
