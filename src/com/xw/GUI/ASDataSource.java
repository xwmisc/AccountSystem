package com.xw.GUI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.xw.DBManager;

public class ASDataSource {

	public static List<HashMap<String, String>> get(String tableName) throws IOException, SQLException {
		DBManager db = DBManager.getInstance();
		List result = db.query(tableName, new HashSet<String>(db.getColumnsName(tableName)), null);
		return result;
	}

	public static List<HashMap<String, String>> get(String tableName, HashMap<String, Object> where)
			throws IOException, SQLException {
		DBManager db = DBManager.getInstance();
		List result = db.query(tableName, new HashSet<String>(db.getColumnsName(tableName)), where);
		return result;
	}

	public static void delete(String tableName, int[] id) throws IOException, SQLException {
		DBManager db = DBManager.getInstance();
		db.delete(tableName, id);
	}

	public static List<String> getColumns(String tableName) throws IOException, SQLException {
		DBManager db = DBManager.getInstance();
		return db.getColumnsName(tableName);
	}

	public static void insert(String tableName, HashMap<String, Object> values) throws IOException, SQLException {
		DBManager db = DBManager.getInstance();
		db.insert(tableName, values);
		db.commit();
	}

}