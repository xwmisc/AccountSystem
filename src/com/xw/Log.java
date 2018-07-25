package com.xw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import sun.reflect.Reflection;

public class Log {
	static File logfile;
	private static Logger logger;
	static {
		// PropertyConfigurator.configure("log4j.properties");

		logfile = new File("log.txt");
		if (!logfile.exists())
			try {
				logfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static Logger logger() {
		if (logger == null)
			logger = Logger.getLogger("Log4j");
		return logger;
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
		System.out.print(s);
		try (FileWriter writer = new FileWriter(logfile, true);) {
			writer.write(s);
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
