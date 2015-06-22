package com.example.ilia.final_exercise.activities;

import android.net.Uri;
import android.os.Bundle;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.database.ArticleItem;
import com.example.ilia.final_exercise.fragments.ArticleFragment;
import com.example.ilia.final_exercise.fragments.ListFragment;
import com.example.ilia.final_exercise.interfaces.IClickListener;
import com.example.ilia.final_exercise.interfaces.IStateItemChange;


public class MainActivity extends BaseActivity {
    public static final String LIST_FRAGMENT = "list_fragment";
    public static final String ARTICLE_FRAGMENT = "article_fragment";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frg_list, new ListFragment(), LIST_FRAGMENT)
                    .add(R.id.frg_article, new ArticleFragment(), ARTICLE_FRAGMENT).commit();
        }
    }

    @Override
    public void getArticleToAnotherFragment(Uri uri) {
        IClickListener iClickListener=(IClickListener)getSupportFragmentManager().findFragmentByTag(ARTICLE_FRAGMENT);
        iClickListener.getArticleToAnotherFragment(uri);
    }

    @Override
    public void deleteArticleItem(ArticleItem articleItem) {
    }

    @Override
    public void updateArticleItem(ArticleItem articleItem) {

    }

    @Override
    public void addArticleItem() {
        IStateItemChange iClickListener=(IStateItemChange)getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT);
        iClickListener.addArticleItem();
    }


}
