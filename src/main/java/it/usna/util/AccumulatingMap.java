package it.usna.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccumulatingMap<K, V> extends HashMap<K, List<V>> {
	private static final long serialVersionUID = 1L;

	@Override
	public List<V> put(K key, List<V> value) {
		return super.put(key, value);
	}

	public List<V> put(K key, V value) {
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

//	public static void main(String ...strings ) {
//		AccumulatingMap<String, Integer> map = new AccumulatingMap<>();
//		map.put("test", 1);
//		map.put("test", 2);
//		map.put("test_", 3);
//		System.out.println(map);
//	}
	
}
