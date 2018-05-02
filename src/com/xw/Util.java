package com.xw;

import java.util.HashMap;
import java.util.HashSet;

public class Util {
	@SuppressWarnings("unchecked")
	public static HashSet SetOf(Object... obj) {
		HashSet set = new HashSet<>();
		for (Object each : obj)
			set.add(each);
		return set;
	}

	@SuppressWarnings("unchecked")
	public static HashMap PairOf(Object... obj) {
		if (obj.length % 2 != 0)
			throw new IllegalArgumentException("argument cant be paired!");
		HashMap map = new HashMap<>();
		for (int i = 0; i + 1 < obj.length; i += 2) {
			map.put(obj[i], obj[i + 1]);
		}
		return map;
	}
}
