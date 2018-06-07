package com.xw;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.xw.db.DB;
import com.xw.excel.Excel;
import com.xw.excel.Excel.Sheet;
import com.xw.excel.ExcelException;

public class Logic {
	public static enum CellType {
		TEXT, NUMBER, DATE
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void recordFromFile(File file) {
		try {
			final boolean replaceTable = true;

			System.out.println("file " + file.getName());

			// 初始化DB
			DB db = DB.getInstance();
			String tableName = file.getName();
			tableName = tableName.substring(0, tableName.indexOf("."));
			// 建表
			if (db.existTable(tableName)) {
				System.out.println("file has been recorded " + file.getName());
				if (replaceTable) {
					db.deleteTable(tableName);
					db.commit();
					db.createEmptyTable(tableName);
					db.commit();
					System.out.println("createEmptyTable " + tableName);
				}else
					return;
			} else {
				db.createEmptyTable(tableName);
				db.commit();
				System.out.println("createEmptyTable " + tableName);
			}

			// 初始化excel
			Excel excel = new Excel(file);

			Sheet sheet = excel.getSheets().get(0);
			int cols = sheet.getColCount(1);
			int rows = sheet.getRowCount();
			System.out.println("cols: " + cols);
			System.out.println("rows: " + rows);

			HashMap[] vals = new HashMap[rows - 1];// 欲添加数据源

			for (int col = 1; col <= cols; col++) {
				// in each column
				String attr = sheet.readString(1, col, "");
				System.out.println("attr: " + attr);
				if (attr.equals(""))
					continue;
				CellType type = getCellType(sheet, col);
				switch (type) {
				case DATE:
					db.insertColumn(tableName, attr, DB.SQLITE3_TYPE.TYPE_DATE);
					break;
				case NUMBER:
					db.insertColumn(tableName, attr, DB.SQLITE3_TYPE.TYPE_NUMBER);
					break;
				case TEXT:
					db.insertColumn(tableName, attr, DB.SQLITE3_TYPE.TYPE_TEXT);
					break;
				}
				for (int row = 2; row <= rows; row++) {
					// in each row
					int index = row - 2;
					if (vals[index] == null)
						vals[index] = new HashMap<>();
					switch (type) {
					case DATE:
						Date date = (Date) sheet.read(row, col);
						vals[index].put(attr, date);
						System.out.println("date:" + vals[index].get(attr));
						break;
					case NUMBER:
						double num = sheet.readDouble(row, col, 0);
						vals[index].put(attr, num);
						break;
					case TEXT:
						String text = sheet.readString(row, col, "");
						vals[index].put(attr, text);
//						System.out.println("text:" + vals[index].get(attr));
						break;
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

	private static CellType getCellType(Sheet sheet, int col) {
		int rows = sheet.getRowCount();
		for (int row = 2; row <= rows; row++) {
			Object sample = sheet.read(row, col);
			if (sample instanceof Date) {
				if (row == rows)
					return CellType.DATE;
				else
					continue;
			}
		}

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
					return CellType.TEXT;
			} else
				return CellType.TEXT;
		}
		return CellType.NUMBER;
	}
}
