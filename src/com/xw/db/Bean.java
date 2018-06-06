package com.xw.db;

public class Bean {
	String id;
	Object val;
	String type;

	public static Bean[] PairOf(boolean autoSetType, Object... obj) {
		try {
			if (autoSetType) {
				if (obj.length % 2 != 0)
					throw new IllegalArgumentException("Illegal Argument Length:" + obj.length);

				int arrL = obj.length / 2;
				Bean[] set = new Bean[arrL];
				for (int i = 0; i < arrL; i++) {
					set[i].id = (String) obj[i * 2 + 0];
					set[i].val = obj[i * 2 + 1];
				}
				return set;
			} else {
				if (obj.length % 3 != 0)
					throw new IllegalArgumentException("Illegal Argument Length:" + obj.length);

				int arrL = obj.length / 3;
				Bean[] set = new Bean[arrL];
				for (int i = 0; i < arrL; i++) {
					set[i].id = (String) obj[i * 3 + 0];
					set[i].val = obj[i * 3 + 1];
					set[i].type = (String) obj[i * 3 + 2];
				}
				return set;
			}
		} catch (IllegalArgumentException | ClassCastException e) {
			e.printStackTrace();
		}
		return null;
	}
}
