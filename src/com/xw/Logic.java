package com.xw;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.xw.db.DB;
import com.xw.excel.Excel;
import com.xw.excel.Excel.Sheet;
import com.xw.excel.ExcelException;

public class Logic {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void recordFromFile(File file) {
		try {
			System.out.println("file " + file.getName());
			DB db = DB.getInstance();
			String tableName = file.getName();
			tableName = tableName.substring(0, tableName.indexOf("."));
			if (db.existTable(tableName)) {
				System.out.println("file has been recorded " + file.getName());
				return;
			} else {
				db.createEmptyTable(tableName);
				System.out.println("createEmptyTable file " + file.getName());
			}

			Excel excel = new Excel(file);

			Sheet sheet = excel.getSheets().get(0);
			int cols = sheet.getColCount(1);
			int rows = sheet.getRowCount();
			System.out.println("cols: " + cols);
			System.out.println("rows: " + rows);

			HashMap[] vals = new HashMap[rows - 1];

			for (int col = 1; col <= cols; col++) {
				// in each column
				String attr = sheet.readString(1, col, "");
				System.out.println("attr: " + attr);
				if (attr.equals(""))
					continue;
				boolean isNumberCol = isNumberColumn(sheet, col);
				if (isNumberCol)
					db.insertColumn(tableName, attr, DB.SQLITE3_TYPE.TYPE_NUMBER);
				else
					db.insertColumn(tableName, attr, DB.SQLITE3_TYPE.TYPE_TEXT);

				for (int row = 2; row <= rows; row++) {
					// in each row
					int index = row - 2;
					if (vals[index] == null)
						vals[index] = new HashMap<>();
					if (isNumberCol) {
						double num = sheet.readDouble(row, col, 0);
						// System.out.println("num: " + num);
						vals[index].put(attr, num);
					} else {
						String text = sheet.readString(row, col, "");
						// System.out.println("text:" + text);
						vals[index].put(attr, text);
						System.out.println("text:" + vals[index].get(attr));
					}
				}
			}
			db.insert(tableName, vals);
			db.commit();
		} catch (IOException | ExcelException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static boolean isNumberColumn(Sheet sheet, int col) {
		int rows = sheet.getRowCount();
		for (int row = 2; row <= rows; row++) {
			Object sample = sheet.read(row, col);
			if (sample instanceof Double)
				continue;
			else if (sample instanceof String) {
				String text = (String) sample;
				// (-)?[0-9]+(\.[0-9]+)?
				if (text.matches("(-)?[0-9]+(\\.[0-9]+)?"))
					continue;
				else
					return false;
			} else
				return false;
		}
		return true;
	}
}
