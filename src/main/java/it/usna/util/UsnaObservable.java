package it.usna.util;

import java.util.ArrayList;

public class UsnaObservable<T, M> {
	private ArrayList<UsnaEventListener<T, M>> listeners = new ArrayList<>();
	
	public synchronized void addListener(UsnaEventListener<T, M> l) {
		listeners.add(l);
	}
	
	public synchronized void fireEvent(T type, M msg) {
		listeners.forEach(l -> l.update(type, msg));
	}
	
	public synchronized void fireEvent(T type) {
		listeners.forEach(l -> l.update(type, null));
	}
	
	public synchronized void removeListeners() {
		listeners.clear();
	}
	
	public synchronized void removeListener(UsnaEventListener<T, M> l) {
		listeners.remove(l);
	}
}
