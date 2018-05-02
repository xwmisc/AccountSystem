package com.xw.GUI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public 	interface ASModel {

	List<HashMap<String, String>> get(String tableName) throws IOException, SQLException;

	List<HashMap<String, String>> get(String tableName, HashMap<String, Object> where)
			throws IOException, SQLException;

	List<String> getColumns(String tableName) throws IOException, SQLException;

	void delete(String tableName, int[] id) throws IOException, SQLException;
	
	void insert(String tableName,HashMap<String, Object> values) throws IOException, SQLException;
}