package it.usna.util;

import java.util.ArrayList;

public class UsnaObservable<T, M> {
	private ArrayList<UsnaEventListener<T, M>> listeners = new ArrayList<>();
	
	public void addListener(UsnaEventListener<T, M> l) {
		listeners.add(l);
	}
	
	public void fireEvent(T type, M msg) {
		listeners.forEach(l -> l.update(type, msg));
	}
	
	public void fireEvent(T type) {
		listeners.forEach(l -> l.update(type, null));
	}
	
	public void removeListeners() {
		listeners.clear();
	}
}
