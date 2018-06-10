package com.xw;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.xw.db.DB;
import com.xw.db.DB.SQLITE3_TYPE;
import com.xw.excel.Excel;
import com.xw.excel.Excel.Sheet;
import com.xw.excel.ExcelException;

public class Logic {
	public static enum CellType {
		TEXT, NUMBER, DATE
	}

	public static void main(String[] args) throws IOException, ExcelException {
		// TODO Auto-generated method stub
		// String a = new String("12"+34);
		// String b = new String("1234");
		// System.out.println(a==f(a));

		// test1(new File(
		// "C:\\Users\\acer-pc\\Documents\\WeChat
		// Files\\wxid_qyi4s5vkakv222\\Files\\对账2月3月(1)\\workx.xls"));
	}

	public static void test1(File file) throws IOException, ExcelException {

		Excel excel = new Excel(file);

		Sheet sheet = excel.getSheets().get(0);
		int rows = sheet.getRowCount();
		System.out.println("rows: " + rows);

		for (int row = 1; row <= rows; row++) {
			int cols = sheet.getColCount(row);
			System.out.print("\n" + row + "\t");
			for (int i = 0; i < cols; i++) {
				System.out.print("*");
			}
			for (int i = 0; i < cols; i++) {
				System.out.print("|" + sheet.readString(row, i + 1, ""));
			}
		}

	}

	public static boolean recordFromFile(File file) {
		try {
			final boolean replaceTable = true;
			Log.clean();
			Log.append("file " + file.getName());

			// 初始化DB
			DB db = DB.getInstance();
			String tableName = file.getName();
			tableName = tableName.substring(0, tableName.indexOf("."));
			// 建表
			if (db.existTable(tableName)) {
				Log.append("file has been recorded " + file.getName());
				if (replaceTable) {
					db.deleteTable(tableName);
					db.commit();
					db.createEmptyTable(tableName);
					db.commit();
					Log.append("createEmptyTable " + tableName);
				} else
					return false;
			} else {
				db.createEmptyTable(tableName);
				db.commit();
				Log.append("createEmptyTable " + tableName);
			}

			// 初始化excel
			Excel excel = new Excel(file);

			Sheet sheet = excel.getSheets().get(0);
			int rows = sheet.getRowCount();

			// 寻找起始行
			int startRow = 1;
			for (int row = 1; row <= rows; row++) {
				int cols = sheet.getColCount(row);
				boolean flag = cols >= 1;
				for (int col = 1; col <= cols; col++) {
					String s = sheet.readString(row, col, "");
					if (s.equals("")) {
						flag = false;
						break;
					}
				}
				if (flag) {
					startRow = row;
					break;
				}
			}
			int cols = sheet.getColCount(startRow);
			Log.append("cols: " + cols);
			Log.append("startRow: " + startRow);
			Log.append("rows: " + rows);

			HashMap[] vals = new HashMap[rows - startRow];// 欲添加数据源

			for (int col = 1; col <= cols; col++) {
				// in each column
				String attr = sheet.readString(startRow, col, "");
				// 替换非法符号
				// ((\\)|(/)|(\|)|(!)|(;)|(:)|(~)|(@)|(#)|(\$)|(%)|(\^)|(&)|(\*)|(\-)|(\+)|(\{)|(\})|(\[)|(\])|(\()|(\))|(,)|(\.)|(
				// ))
				attr = attr.replaceAll(
						"((\\\\)|(/)|(\\|)|(!)|(;)|(:)|(~)|(@)|(#)|(\\$)|(%)|(\\^)|(&)|(\\*)|(\\-)|(\\+)|(\\{)|(\\})|(\\[)|(\\])|(\\()|(\\))|(,)|(\\.)|( ))",
						"_");

				System.out.println("attr: " + attr);
				if (attr.equals(""))
					continue;
				CellType type = getSerialCellType(sheet, startRow, rows, col);
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
				for (int row = startRow + 1; row <= rows; row++) {
					// in each row
					int index = row - startRow - 1;
					if (vals[index] == null)
						vals[index] = new HashMap<>();
					switch (type) {
					case DATE:
						Date date = (Date) sheet.read(row, col);
						vals[index].put(attr, date);
						// System.out.println("date:" + vals[index].get(attr));
						break;
					case NUMBER:
						// double num = sheet.readDouble(row, col, Double.MAX_VALUE);
						// if (num != Double.MAX_VALUE) {
						// vals[index].put(attr, num);
						// } else {
						// vals[index].put(attr, null);
						// }
						double num = sheet.readDouble(row, col, 0);
						vals[index].put(attr, num);
						break;
					case TEXT:
						// final String ERRSTRING = "";
						// String text = sheet.readString(row, col, ERRSTRING);
						// if (text != ERRSTRING) {
						// vals[index].put(attr, text);
						// } else {
						// vals[index].put(attr, null);
						// }
						String text = sheet.readString(row, col, "");
						vals[index].put(attr, text);
						// System.out.println("text:" + vals[index].get(attr));
						break;
					}
				}
			}
			vals = Util.DropNull(vals);
			db.insert(tableName, vals);
			db.commit();
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.append(e.toString());
		}
		return false;

	}

	private static CellType getSerialCellType(Sheet sheet, int startRow, int endRow, int col) {

		int date = 0;
		int blank = 0;
		int num = 0;
		for (int row = startRow + 1; row <= endRow; row++) {
			Object sample = sheet.read(row, col);

			if (sample instanceof Date) {
				date++;
				continue;
			}

			if (sample instanceof Double) {
				num++;
				continue;
			} else if (sample instanceof String) {
				String text = (String) sample;
				// (-)?[0-9]+(\.[0-9]+)?
				if (text.matches("(-)?[0-9]+(\\.[0-9]+)?")) {
					num++;
					continue;
				} else {
					return CellType.TEXT;
				}
			} else if (sample == null) {
				blank++;
			}
		}
		if (date > 0 && num == 0) {
			return CellType.DATE;
		} else if (date == 0 && num > 0) {
			return CellType.NUMBER;
		} else {
			return CellType.TEXT;
		}
	}

	public static boolean compare01(String table1, String table2, String keyWord, String keyWord2) {

		try {
			Log.clean();
			// 初始化DB
			DB db = DB.getInstance();
			if (!db.existTable(table1) || !db.existTable(table1))
				return false;

			List<HashMap<String, Object>> list1 = db.query(table1, db.getColumns(table1), null);
			List<HashMap<String, Object>> list2 = db.query(table2, db.getColumns(table2), null);

			for (int i = 0; i < list1.size(); i++) {
				HashMap<String, Object> record = list1.get(i);
				double word = (double) record.get(keyWord);
				for (int j = 0; j < list2.size(); j++) {
					HashMap<String, Object> record2 = list2.get(j);
					double word2 = (double) record2.get(keyWord2);
					if (word == word2) {
						Log.append("-" + word);
						list1.remove(i--);
						list2.remove(j--);
						break;
					}
				}
			}


			// 建表
			final boolean replaceTable = true;
			String tableName = table1 + "_" + table2;
			if (db.existTable(tableName)) {
				Log.append(tableName + " existed");
				if (replaceTable) {
					db.deleteTable(tableName);
					db.commit();
					db.createEmptyTable(tableName);
					db.commit();
					Log.append("createEmptyTable " + tableName);
				} else
					return false;
			} else {
				db.createEmptyTable(tableName);
				db.commit();
				Log.append("createEmptyTable " + tableName);
			}

			//id分类
			for (HashMap<String, Object> record : list1) {
				int id = (int) record.get("id");
				record.put(table1 + "_id", id);
				record.remove("id");
			}
			for (HashMap<String, Object> record : list2) {
				int id = (int) record.get("id");
				record.put(table2 + "_id", id);
				record.remove("id");
			}
			HashSet<String> added = new HashSet<>();
			for (String column : db.getColumns(table1)) {
				if (column.equals("id") || !added.add(column))
					continue;
				db.insertColumn(tableName, column, db.getColumnType(table1, column));
			}
			for (String column : db.getColumns(table2)) {
				if (column.equals("id") || !added.add(column))
					continue;
				db.insertColumn(tableName, column, db.getColumnType(table2, column));
			}
			db.insertColumn(tableName, table1 + "_id", SQLITE3_TYPE.TYPE_NUMBER);
			db.insertColumn(tableName, table2 + "_id", SQLITE3_TYPE.TYPE_NUMBER);

			// List<HashMap> list3 = new ArrayList<>();
			// list3.addAll(list1);
			// list3.addAll(list2);
			// Util.DropNull(list3);
			// db.insert(tableName, list3);
			db.insert(tableName, list1.toArray(new HashMap[0]));
			db.insert(tableName, list2.toArray(new HashMap[0]));
			db.commit();

			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.append(e.toString());
		}
		return false;
	}

}
