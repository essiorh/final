package com.example.ilia.final_exercise.ui.interfaces;

import com.example.ilia.final_exercise.data.containers.ArticleItem;

/**
 * Created by ilia on 16.06.15.
 */
public interface IStateItemChange {
    void deleteArticleItem(ArticleItem articleItem);
    void updateArticleItem(ArticleItem articleItem);
    void addArticleItem();
}
