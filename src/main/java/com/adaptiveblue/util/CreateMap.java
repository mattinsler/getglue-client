package com.adaptiveblue.util;

import java.util.HashMap;
import java.util.Map;

public final class CreateMap {
	public static <K, V> Map<K, V> of() {
		return new HashMap<K, V>();
	}
	
	public static <K, V> Map<K, V> of(K k1, V v1) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(k1, v1);
		return map;
	}
	
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(k1, v1);
		map.put(k2, v2);
		return map;
	}
	
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		return map;
	}
	
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(k1, v1);
		map.put(k2, v2);
		map.put(k3, v3);
		map.put(k4, v4);
		return map;
	}
}
