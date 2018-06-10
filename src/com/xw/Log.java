package com.xw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
	static File logfile;
	static {
		logfile = new File("log.txt");
		if (!logfile.exists())
			try {
				logfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void clean() {

		try (FileOutputStream testfile = new FileOutputStream(logfile);) {
			testfile.write("".getBytes());
			testfile.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void append(String s) {
		System.out.println(s);
		try (FileWriter writer = new FileWriter(logfile, true);) {
			writer.write("\r\n" + s);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFileLocation() {
		try {
			return logfile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "log.txt";
	}

}
