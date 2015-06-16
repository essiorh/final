package com.example.ilia.final_exercise.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.models.ArticleItem;

/**
 * Created by ilia on 16.06.15.
 */
public class ArticleFragment extends Fragment implements IClickListener {
    private TextView textTitle;
    private TextView textDescription;
    private Spinner spinnerCategory;
    private ImageView imagePhoto;
    private Switch switchPublished;
    private Button buttonView;
    private Button buttonEdit;
    private Button buttonSave;

    public ArticleFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout inflateView = (LinearLayout) inflater.inflate(R.layout.article_view, container, false);
        textTitle = (TextView) inflateView.findViewById(R.id.editText);
        textDescription = (TextView) inflateView.findViewById(R.id.editText2);
        spinnerCategory = (Spinner) inflateView.findViewById(R.id.spinner);
        imagePhoto = (ImageView) inflateView.findViewById(R.id.imageView);
        switchPublished = (Switch) inflateView.findViewById(R.id.switch1);
        buttonView = (Button) inflateView.findViewById(R.id.button);
        buttonEdit = (Button) inflateView.findViewById(R.id.button2);
        buttonSave = (Button) inflateView.findViewById(R.id.button3);



        return inflateView;
    }

    @Override
    public void getArticleToAnotherFragment(ArticleItem articleItem) {
        textTitle.setText(articleItem.getmTitle());
        textDescription.setText(articleItem.getmDescription());
        switchPublished.setSelected(articleItem.ismPublished());


    }
}
