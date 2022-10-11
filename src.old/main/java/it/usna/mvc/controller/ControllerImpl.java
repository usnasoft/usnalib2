package it.usna.mvc.controller;

import java.util.ArrayList;
import java.util.List;

import it.usna.mvc.model.Model;
import it.usna.mvc.view.View;

/**
 * Controller base class (MVC)
 * @author Antonio Flaccomio
 * @param <M>
 */
public class ControllerImpl<M extends Model> implements Controller {
	
	protected List<ModelViews<M>> modelsViews = new ArrayList<>();
	//protected CommandMap<? extends Enum<?>> registeredCommands = null;
	protected ModelViews<M> currentActive;
	
	/**
	 * Signal from view: override this method if useful
	 * @param theView
	 */
	public void viewIconized(final View theView) {
	}
	
	/**
	 * Signal from view: override this method if useful
	 * @param theView
	 */
	public void viewDeiconized(final View theView) {
	}
	
	/**
	 * Signal from view:
	 * New active model is the owner of the view;
	 * override this method to change this behaviour.
	 * @param theView
	 */
	public void viewGainedFocus(final View theView) {
		final ModelViews<M> mv = find(theView);
		if(mv != null) {  // maybe the model has been closed
			mv.views.remove(theView); // move view to the "active" position
			mv.views.add(theView);
			setActiveModel(mv);
		}
	}
	
	/**
	 * Signal from view: override this method if useful
	 * @param theView
	 */
	public void viewLoosedFocus(final View theView) {
	}
	
	/**
	 * Signal from view.
	 * If a model has no more views it is also closed;
	 * override this method to change this behaviour.
	 * @param theView
	 */
	public void viewClosed(final View theView) {
		final ModelViews<M> mv = find(theView);
		if(mv != null) {  // maybe the model has been closed
			mv.views.remove(theView);
			if(mv.views.size() == 0) {
				mv.model.close();
				modelsViews.remove(mv);
				setActiveModel();
			}
		}
	}
	
	/**
	 * Add a Model and a related View (model becomes active)
	 * @param model
	 * @param view if null no view is associated to the model
	 */
	protected ModelViews<M> addModel(final M model, final View view) {
		final ModelViews<M> mv = new ModelViews<M>(model, view);
		model.setViewsList(mv.getViews());
		setActiveModel(mv);
		model.initialize();
		return mv;
	}
	
	/**
	 * Set the active model; models without views are supported (can became active).
	 * Any action changing active model must invoke this method.
	 * @param mv, null if no model can be activated (e.g. after closing
	 * of the last referred model).
	 */
	public void setActiveModel(final ModelViews<M> mv) {
		if(mv != currentActive) {
			if(currentActive != null) {
				currentActive.model.deactivate();
			}
			if(mv != null) {
				modelsViews.remove(mv); // sposta il modello alla fine della lista
				modelsViews.add(mv);    // (modello attivo)
				mv.model.activate();
				// Activate topmost view of new model (if model has no view
				// deactivate previously active view; no view will be active)
				View currentView = mv.getCurrentView();
				if(currentView != null) {
					if(!currentView.isSelected()) {
						currentView.setSelected(true);
					}
				} else if (currentActive != null && (currentView = currentActive.getCurrentView()) != null) {
					if(currentView.isSelected()) {
						currentView.setSelected(false);
					}
				}
			}
			currentActive = mv;
		}
	}
	
	/**
	 * Set the active model; models without views are supported (can became active).
	 * @param mod
	 */
	public void setActiveModel(final Model mod) {
		setActiveModel(find(mod));
	}
	
	/**
	 * Set first available model as active;
	 * typically called after a model is closed;
	 * Active model is null if models is list is empty.
	 * @return the new active ModelViews<M>
	 */
	protected ModelViews<M> setActiveModel() {
		final int mvSize = modelsViews.size();
		final ModelViews<M> newMv = (mvSize == 0) ? null : modelsViews.get(mvSize - 1);
		setActiveModel(newMv);
		return newMv;
	}
	
	/**
	 * Close current active Model; next model become active (see: setActiveModel())
	 * @return true: the model has been closed
	 */
	public boolean closeCurrentModel() {
		// Non posso iterare sulla lista perche' il close normalmente manda un segnale
		// di chiusura che altera il contenuto della lista delle fineste.
		final ModelViews<M> ca = currentActive;
		final List<View> lv = ca.views;
		// Use reverse order in case the close event delete the view from list before end of cicle
		for(int i = lv.size() - 1; i >= 0; i--) {
			final boolean canClose = lv.get(i).close();
			if(!canClose) {
				return false;
			}
		}
		// Se ho chiuso tutte le fineste probabilmente si e' gia' chiuso il modello
		// in questo caso ca!=currentActive, ma potrebbe essere stato definito un
		// comportamento diverso.
		if(ca == currentActive) {
			ca.model.close();
			modelsViews.remove(ca);
			setActiveModel();
		}
		return true;
	}
	
	/**
	 * Add a View to a Model
	 * @param model
	 * @param view
	 */
	protected void addView(final M model, final View view) {
		final ModelViews<M> mv = find(model);
		mv.views.add(view);
	}
	
	/**
	 * Add a View to the current Model
	 * @param model
	 * @param view
	 */
	protected void addView(final View view) {
		currentActive.views.add(view);
	}
	
	/**
	 * Close current active View
	 * @param view
	 */
	public void closeCurrentView() {
		currentActive.getCurrentView().close();
		// view close will generate viewClosed() event
	}
	
	/**
	 * Close all models and views
	 */
	public boolean closeApp() {
		while(currentActive != null) {
			if(closeCurrentModel() == false) {
				return false;
			}
		}
		System.exit(0);
		return true;
	}
	
	/**
	 * return a ModelViews object associated to a View
	 * @param view
	 * @return
	 */
	protected ModelViews<M> find(final View view) {
		for(ModelViews<M> item: modelsViews) {
			if(item.views.contains(view)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * return a ModelViews object associated to a Model
	 * @param view
	 * @return
	 */
	protected ModelViews<M> find(final Model model) {
		for(final ModelViews<M> item: modelsViews) {
			if(item.model.equals(model)) {
				return item;
			}
		}
		return null;
	}
	
	/**
	 * For internal library use only
	 * @return
	 */
	public List<ModelViews<M>> getModelsViews() {
		return modelsViews;
	}
	
	/**
	 * Enable/Disable a registered Action
	 * @param command
	 * @param enable
	 */
	/*public void enableCommand(final Enum<?> command, final boolean enable) {
		registeredCommands.get(command).setEnabled(enable);
	}*/
	
	/**
	 * Get a registered Action
	 * @param command
	 * @return
	 */
	/*public Action getCommand(final Enum<?> command) {
		return registeredCommands.get(command);
	}*/

	/**
	 * Send a message to the active view
	 * @param msg
	 */
	public void signalTopmostView(final Object msg) {
		currentActive.getCurrentView().update(this, msg);
	}
	
	/**
	 * Send a message to all views
	 * @param msg
	 */
	public void signalViews(final Object msg) {
		for(final ModelViews<M> item: modelsViews) {
			for(final View v: item.getViews()) {
				v.update(this, msg);
			}
		}
	}
}