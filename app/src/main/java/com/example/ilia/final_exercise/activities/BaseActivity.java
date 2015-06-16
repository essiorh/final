package com.example.ilia.final_exercise.activities;

import android.support.v7.app.AppCompatActivity;

import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.interfaces.IStateItemChange;
import com.example.ilia.final_exercise.models.ArticleItem;

/**
 * Created by ilia on 16.06.15.
 */
public class BaseActivity extends AppCompatActivity implements IClickListener,IStateItemChange {
    @Override
    public void getArticleToAnotherFragment(ArticleItem articleItem) {

    }

    @Override
    public void deleteArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void updateArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void addArticleItem(ArticleItem articleItem) {

    }
}
