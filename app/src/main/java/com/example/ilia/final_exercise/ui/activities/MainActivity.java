package com.example.ilia.final_exercise.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.ilia.final_exercise.R;
import com.example.ilia.final_exercise.ui.fragments.ArticlesFragment;
import com.example.ilia.final_exercise.ui.fragments.TopicListFragment;
import com.example.ilia.final_exercise.ui.interfaces.IActivityArticleInteractionListener;
import com.example.ilia.final_exercise.ui.interfaces.IActivityTopicListInteractionListener;
import com.example.ilia.final_exercise.ui.interfaces.IArticleFragmentInteractionListener;
import com.example.ilia.final_exercise.ui.interfaces.ITopicListFragmentInteraction;

/**
 * Created by ilia on 16.06.15.
 *
 * @author ilia
 */
public class MainActivity extends Activity implements IArticleFragmentInteractionListener,
        ITopicListFragmentInteraction {

    public static final String TAG = "MY LOG";

    private IActivityArticleInteractionListener mArticleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            Log.d(TAG, "savedInstanceState is not null");
        } else {
            getFragmentManager().beginTransaction()
                    .add(R.id.topic_list_panel, TopicListFragment.newInstance())
                    .add(R.id.article_panel, ArticlesFragment.newInstance(-1))
                    .commit();
        }
    }

    @Override
    public void onItemClicked(long id) {
        if (mArticleFragment != null) {
            mArticleFragment.onOpenArticle(id);
        }
    }

    @Override
    public void onCreateNewArticle() {
        if (mArticleFragment != null) {
            mArticleFragment.onCreateNewArticle();
        }
    }

    @Override
    public void onDeleteArticle(long id) {
        if (mArticleFragment != null) {
            mArticleFragment.onDeleteArticle(id);
        }
    }

    @Override
    public void onRegister(IActivityArticleInteractionListener fragment) {
        mArticleFragment = fragment;
    }

    @Override
    public void onUnregister(IActivityArticleInteractionListener fragment) {
        mArticleFragment = null;
    }

    @Override
    public void onRegister(IActivityTopicListInteractionListener fragment) {
    }

    @Override
    public void onUnregister(IActivityTopicListInteractionListener fragment) {
    }

}
