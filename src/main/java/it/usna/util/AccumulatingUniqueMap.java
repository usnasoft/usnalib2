package it.usna.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AccumulatingUniqueMap<K, V> extends HashMap<K, Set<V>> {
	private static final long serialVersionUID = 1L;

//	@Override
//	public List<V> put(K key, List<V> value) {
//		return super.put(key, value);
//	}

	public Set<V> putVal(K key, V value) {
		Set<V> val = get(key);
		if(val == null) {
			val = new HashSet<V>();
			val.add(value);
			put(key, val);
		} else {
			val.add(value);
		}
		remove(key, null);
		return val;
	}
	
	public boolean addVal(K key, V value) {
		Set<V> val = get(key);
		if(val == null) {
			val = new HashSet<V>();
			val.add(value);
			put(key, val);
			return false;
		} else {
			return val.add(value);
		}
	}
	
	public Set<V> putNull(K key) {
		return putVal(key, (V)null);
	}
	
	public Set<V> putKey(K key) {
		Set<V> val = get(key);
		if(val == null) {
			return put(key, new HashSet<V>());
		} else {
			return val;
		}
	}
	
	public boolean removeValue(K key, V value) {
		Set<V> set = get(key);
		return set != null && set.remove(value);
	}
}
