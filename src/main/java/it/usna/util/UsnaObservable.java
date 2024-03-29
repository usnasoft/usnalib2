package it.usna.util;

import java.util.ArrayList;

/**
 * @see it.usna.util.UsnaEventListener<T, M>
 * @param <T> message
 * @param <M> message details
 */
public class UsnaObservable<T, M> {
	private ArrayList<UsnaEventListener<T, M>> listeners = new ArrayList<>();
	
	public synchronized void addListener(UsnaEventListener<T, M> l) {
		listeners.add(l);
	}
	
	public synchronized void addUniqueListener(UsnaEventListener<T, M> l) {
		if(listeners.contains(l) == false) {
			listeners.add(l);
		}
	}

	public synchronized void fireEvent(T type, M msg) {
		listeners.forEach(l -> {
			//try {
				l.update(type, msg);
			//} catch (RuntimeException e) {}
		});
	}
	
	public synchronized void fireEvent(T type) {
		listeners.forEach(l -> {
			//try {
				l.update(type, null);
			//} catch (RuntimeException e) {}
		});
	}
	
	public synchronized void removeListeners() {
		listeners.clear();
	}
	
	public synchronized void removeListener(UsnaEventListener<T, M> l) {
		listeners.remove(l);
	}
}