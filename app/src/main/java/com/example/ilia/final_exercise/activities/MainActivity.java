package com.example.ilia.final_exercise.activities;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.fragments.ArticleFragment;
import com.example.ilia.final_exercise.fragments.ListFragment;
import com.example.ilia.final_exercise.fragments.PageFragment;
import com.example.ilia.final_exercise.models.ArticleItem;
import com.example.ilia.final_exercise.models.GroupItem;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    public static final String LIST_FRAGMENT = "list_fragment";
    public static final String TITLE_FRAGMENT = "title_fragment";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getSupportFragmentManager().beginTransaction().add(R.id.frg_list, new ListFragment(), LIST_FRAGMENT).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frg_article, new ArticleFragment(), TITLE_FRAGMENT).commit();
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
