package com.xw.GUI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.xw.DBManager;
import com.xw.db.DB;

public class ASDataSource {

	public static List<HashMap<String, String>> get(String tableName) throws IOException, SQLException {
		DB db = DB.getInstance();
		List result = db.query(tableName, db.getColumns(tableName), null);
		return result;
	}

	public static List<HashMap<String, String>> get(String tableName, HashMap<String, Object> where)
			throws IOException, SQLException {
		DB db = DB.getInstance();
		List result = db.query(tableName, db.getColumns(tableName), where);
		return result;
	}

	public static List<String> getColumns(String tableName) throws IOException, SQLException {
		DB db = DB.getInstance();
		return Arrays.asList(db.getColumns(tableName));
	}
	public static String getColumnType(String tableName,String columnName) throws IOException, SQLException {
		DB db = DB.getInstance();
		return db.getColumnType(tableName, columnName);
	}

	public static String[] getTables() throws SQLException {
		DB db = DB.getInstance();
		return db.getTables();
	}
	
	public static String[] getSortTables() throws SQLException {
		DB db = DB.getInstance();
		return db.getTablesSortByCreationTime();
	}
	
	
	
	public static boolean existTable(String tableName) throws SQLException {
		DB db = DB.getInstance();
		return db.existTable(tableName);
	}

	public static void insert(String tableName, HashMap<String, Object> values) throws IOException, SQLException {
		DB db = DB.getInstance();
		db.insert(tableName, values);
		db.commit();
	}

	public static void delete(String tableName, int[] id) throws IOException, SQLException {
		DB db = DB.getInstance();
		db.delete(tableName, id);
		db.commit();
	}

	public static void deleteTable(String tableName) throws SQLException {
		DB db = DB.getInstance();
		db.deleteTable(tableName);
		db.commit();
	}

}