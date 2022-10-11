package it.usna.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import it.usna.mvc.model.Model;
import it.usna.mvc.view.View;

/**
 * One model and its associated views (utility class)
 * @author Antonio Flaccomio
 * @param <M extends Model>
 */
public class ModelViews<M extends Model> {
	final List<View> views = new ArrayList<>();
	final M model;
	
	ModelViews(final M model) {
		this.model = model;
	}
	
	ModelViews(final M model, final View view) {
		this.model = model;
		if(view != null) {
			views.add(view);
		}
	}
	
	public final M getModel() {
		return model;
	}
	
	public final List<View> getViews() {
		return views;
	}
		
	public View getCurrentView() {
		final int numViews = views.size();
		return (numViews > 0) ? views.get(numViews - 1) : null;
	}
}
