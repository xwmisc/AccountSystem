package com.xw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

	public static void DropNull(List list) {
		for (int i = 0; i < list.size(); i++) {
			HashMap hm = (HashMap) list.get(i);
			boolean flag_AllNull = true;
			for (Object something : hm.entrySet()) {
				if (something != null) {
					flag_AllNull = false;
					break;
				}
			}
			if (flag_AllNull)
				list.remove(i--);
		}
	}

	public static HashMap[] DropNull(HashMap[] hm) {
		ArrayList<HashMap> list = new ArrayList<>();
		for (int i = 0; i < hm.length; i++) {
			boolean flag_AllNull = true;
			for (Object something : hm[i].entrySet()) {
				if (something != null) {
					flag_AllNull = false;
					break;
				}
			}
			if (flag_AllNull)
				list.add(hm[i]);
		}
		int length = hm.length - list.size();
		HashMap[] hm2 = new HashMap[length];
		int index = 0;
		for (HashMap each : hm) {
			if (!list.contains(each))
				hm2[index++] = each;
		}
		return hm2;
	}

}
