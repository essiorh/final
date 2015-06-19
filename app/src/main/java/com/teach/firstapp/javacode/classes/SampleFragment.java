package com.teach.firstapp.javacode.classes;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SampleFragment extends Fragment implements LoaderManager.LoaderCallbacks{

    public interface IOnAction{
        void onAction();
    }

    private static final String ID_KEY = "ID_KEY";

    public static SampleFragment getInstance(int id){
        SampleFragment fragment = new SampleFragment();
        Bundle args = new Bundle();
        args.putInt(ID_KEY, id);
        fragment.setArguments(args);
        return fragment;
    }

    IProgress iProgress;
    IOnAction onAction;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        iProgress = (IProgress)activity;
        onAction = (IOnAction)activity;
    }

    @Override
    public void onDetach() {
        iProgress = null;
        onAction = null;

        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        int id = args.getInt(ID_KEY);

        iProgress.showProgress();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        onAction.onAction();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
