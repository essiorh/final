package com.example.ilia.final_exercise.interfaces;

import android.net.Uri;

import com.example.ilia.final_exercise.database.ArticleItem;

import java.net.URI;

/**
 * Created by ilia on 16.06.15.
 */
public interface IClickListener {
    void getArticleToAnotherFragment(Uri articleItem);
}
