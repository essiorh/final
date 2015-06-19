package com.teach.firstapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.teach.firstapp.javacode.classes.SampleFragment;

public class MessageActivity extends Activity implements LoaderManager.LoaderCallbacks, SampleFragment.IOnAction{

    public static final String MESSAGE_BODY_KEY = "MESSAGE_BODY_KEY";

    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity_layout);

        message = (TextView) findViewById(R.id.message_body);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String msg = extras.getString(MESSAGE_BODY_KEY, "No message");
            message.setText(msg);
        }
        getLoaderManager().initLoader(1, null, this);

        getFragmentManager().beginTransaction()
                .add(R.id.message_body, SampleFragment.getInstance(10), "tag").commit();
    }

    @Override
    public void onAction() {

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id){
            case 1:
                return new AsyncTaskLoader(this) {
                    @Override
                    public Object loadInBackground() {
                        return null;
                    }
                };
            case 2:
                return new AsyncTaskLoader(this) {
                    @Override
                    public Object loadInBackground() {
                        return null;
                    }
                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()){
            case 1:
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
