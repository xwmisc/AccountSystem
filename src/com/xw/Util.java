package com.xw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;

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
	

	public static byte[] swtImg2PngByte(org.eclipse.swt.graphics.Image image) {
		org.eclipse.swt.graphics.ImageLoader imageLoader = new org.eclipse.swt.graphics.ImageLoader();
		imageLoader.data = new org.eclipse.swt.graphics.ImageData[] { image.getImageData() };
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		imageLoader.save(result, SWT.IMAGE_PNG);
		return result.toByteArray();
	}

	public static ImageData swtPngByte2Img(byte[] img) {
		org.eclipse.swt.graphics.ImageLoader imageLoader = new org.eclipse.swt.graphics.ImageLoader();
		ByteArrayInputStream in = new ByteArrayInputStream(img);
		imageLoader.load(in);
		return imageLoader.data[0];
	}
}
