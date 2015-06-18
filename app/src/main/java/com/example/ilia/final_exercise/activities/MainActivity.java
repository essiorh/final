package com.example.ilia.final_exercise.activities;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.interfaces.IStateItemChange;
import com.example.ilia.final_exercise.database.ArticleItem;

import java.net.URI;


public class MainActivity extends BaseActivity {
    private Fragment fragmentList;
    private Fragment fragmentArticle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fragmentList=getSupportFragmentManager().findFragmentById(R.id.frg_list);
        fragmentArticle=getSupportFragmentManager().findFragmentById(R.id.frg_article);

        //getSupportFragmentManager().beginTransaction().add(R.id.frg_list, new ListFragment(), LIST_FRAGMENT).commit();
        //getSupportFragmentManager().beginTransaction().add(R.id.frg_article, new ArticleFragment(), TITLE_FRAGMENT).commit();

    }

    @Override
    public void getArticleToAnotherFragment(Uri uri) {
        IClickListener iClickListener=(IClickListener)fragmentArticle;
        iClickListener.getArticleToAnotherFragment(uri);
    }

    @Override
    public void deleteArticleItem(ArticleItem articleItem) {
    }

    @Override
    public void updateArticleItem(ArticleItem articleItem) {
        IStateItemChange iClickListener=(IStateItemChange)fragmentList;
        iClickListener.updateArticleItem(articleItem);
    }

    @Override
    public void addArticleItem(ArticleItem articleItem) {
    }


}
