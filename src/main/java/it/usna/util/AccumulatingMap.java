package it.usna.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccumulatingMap<K, V> extends HashMap<K, List<V>> {
	private static final long serialVersionUID = 1L;

//	@Override
//	public List<V> put(K key, List<V> value) {
//		return super.put(key, value);
//	}

	public List<V> putVal(K key, V value) {
		List<V> val = get(key);
		if(val == null) {
			val = new ArrayList<V>();
			val.add(value);
			put(key, val);
		} else {
			val.add(value);
		}
		return val;
	}
	
	public List<V> putNull(K key) {
		return putVal(key, (V)null);
	}
	
	public List<V> putKey(K key) {
		List<V> val = get(key);
		if(val == null) {
			return put(key, new ArrayList<V>());
		} else {
			return val;
		}
	}
	
	public V getFirst(K key) {
		return get(key).get(0);
	}
	
	public boolean removeValue(K key, V value) {
		List<V> list = get(key);
		return list != null && list.remove(value);
	}

//	public static void main(String ...strings ) {
//		AccumulatingMap<String, Object> map = new AccumulatingMap<>();
//		map.putVal("test", 1);
//		map.putVal("test", 2);
//		map.putVal("test_", 3);
//		System.out.println(map);
//	}
}
