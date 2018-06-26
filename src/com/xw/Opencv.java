package com.xw;

public class Opencv {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static native byte[] cut(long imgAddr, int x, int y, int w, int h);
	public static native byte[] cut(long imgAddr);

}
