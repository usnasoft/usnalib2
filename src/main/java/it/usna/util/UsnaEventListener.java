package it.usna.util;

/**
 * @see it.usna.util.UsnaObservable<T, M>
 * @param <T> message
 * @param <M> message details
 */
public interface UsnaEventListener<T, M> {
	public void update(T mesgType, M msgBody);
}
