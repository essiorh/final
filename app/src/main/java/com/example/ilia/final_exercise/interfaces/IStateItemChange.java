package com.example.ilia.final_exercise.interfaces;

import com.example.ilia.final_exercise.database.ArticleItem;

/**
 * Created by ilia on 16.06.15.
 */
public interface IStateItemChange {
    void deleteArticleItem(ArticleItem articleItem);
    void updateArticleItem(ArticleItem articleItem);
    void addArticleItem(ArticleItem articleItem);
}
