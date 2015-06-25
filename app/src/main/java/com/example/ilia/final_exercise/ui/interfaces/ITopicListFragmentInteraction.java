package com.example.ilia.final_exercise.ui.interfaces;

/**
 * Created by ilia on 16.06.15.
 * @author ilia
 */
public interface ITopicListFragmentInteraction {
	void onRegister(IActivityTopicListInteractionListener fragment);

	void onUnregister(IActivityTopicListInteractionListener fragment);

	void onItemClicked(long id);

	void onCreateNewArticle();

	void onDeleteArticle(long id);
}
