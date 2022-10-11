package it.usna.util;

public interface UsnaEventListener<T, M> {
	public void update(T mesgType, M msgBody);
}
