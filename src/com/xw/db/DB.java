package com.xw.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.xw.Util;

public class DB {
	private static DB mDB;
	private static SqliteDate mSD;
	Connection m_Connection;

	public static enum SQLITE3_TYPE {
		TYPE_TEXT, TYPE_NUMBER, TYPE_DATE
	}

	public SqliteDate getSqliteDate() throws SQLException {
		if (mSD == null) {
			mSD = new SqliteDate();
		}
		return mSD;
	}

	public class SqliteDate {
		static final String DATE_TABLE = "sqlite3_date";
		static final String TB_NAME = "table_name";
		static final String TB_CNAME = "date_column_name";

		private SqliteDate() throws SQLException {
			if (!existTable(DATE_TABLE)) {
				try {
					String sql = "create table " + DATE_TABLE + "(id integer primary key autoincrement)";
					m_Connection.createStatement().executeUpdate(sql);
					commit();
					insertColumn(DATE_TABLE, TB_NAME, SQLITE3_TYPE.TYPE_TEXT);
					insertColumn(DATE_TABLE, TB_CNAME, SQLITE3_TYPE.TYPE_TEXT);
					commit();
				} catch (SQLException e) {
					if (e.getMessage().contains("already exists"))
						return;
					else
						throw e;
				}
			}
		}

		public boolean isDateColumn(String table, String column) throws SQLException {
			List<HashMap<String, Object>> vals = query(DATE_TABLE, new String[] { TB_CNAME },
					Util.PairOf(TB_NAME, table, TB_CNAME, column));
			return vals.size() > 0;
		}

		public void setDateColumn(String table, String[] columns) throws SQLException {
			HashMap[] vals = new HashMap[columns.length];
			for (int i = 0; i < columns.length; i++)
				vals[i] = Util.PairOf(TB_NAME, table, TB_CNAME, columns[i]);
			insert(DATE_TABLE, vals);
		}

		public void cleanDateColumn(String table) throws SQLException {
			List<HashMap<String, Object>> vals = query(DATE_TABLE, new String[] { "id" }, Util.PairOf(TB_NAME, table));
			int[] ids = new int[vals.size()];
			for (int i = 0; i < vals.size(); i++)
				ids[i] = (int) vals.get(i).get("id");
			delete(table, ids);
		}

	}

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		System.out.println(DB.getInstance().existTable("CMP"));
		DB.getInstance().test("result");
	}

	public static DB getInstance() {
		try {
			if (null == mDB) {
				mDB = new DB("database.db");
			}
			return mDB;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private DB(String FileName) throws IOException {
		try {
			Class.forName("org.sqlite.JDBC");
			m_Connection = DriverManager.getConnection("jdbc:sqlite:" + FileName);
			m_Connection.setAutoCommit(false);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void createEmptyTable(String tableName) throws SQLException {
		if (existTable(tableName))
			return;
		String sql = "create table " + tableName + "(id integer primary key autoincrement)";
		m_Connection.createStatement().executeUpdate(sql);
		//清除日期
		getSqliteDate().cleanDateColumn(tableName);
	}

	public void deleteTable(String tableName) throws SQLException {
		if (!existTable(tableName))
			return;
		String sql = "drop table " + tableName;
		m_Connection.createStatement().executeUpdate(sql);
	}

	public boolean existTable(String tableName) throws SQLException {
		if(tableName.equals("")||tableName.contains(" "))return false;
		DatabaseMetaData meta = m_Connection.getMetaData();
		ResultSet rs = meta.getTables(null, null, tableName, new String[] { "TABLE" });
		boolean flag = rs.next();
		rs.close();
		return flag;
	}

	public void insertColumn(String tableName, String colName, SQLITE3_TYPE type) throws SQLException {
		String sql = "alter table " + tableName + " add column " + colName;
		switch (type) {
		case TYPE_NUMBER:
			sql += " real";
			break;
		case TYPE_TEXT:
			sql += " text";
			break;
		case TYPE_DATE:
			sql += " datetime";
			break;
		}
		m_Connection.createStatement().executeUpdate(sql);
		// 日期
		if (type == SQLITE3_TYPE.TYPE_DATE)
			getSqliteDate().setDateColumn(tableName, new String[] { colName });

	}

	public void insertColumn(String tableName, String colName, String type) throws SQLException {
		String sql = "alter table " + tableName + " add column " + colName + " " + type;
		m_Connection.createStatement().executeUpdate(sql);
		// 日期
		if (type.toLowerCase().contains("date"))
			getSqliteDate().setDateColumn(tableName, new String[] { colName });
	}

	public boolean hasColumn(String tableName, String colName) throws SQLException {

		ResultSet rSet = m_Connection.createStatement().executeQuery("select * from " + tableName);
		ResultSetMetaData rsmd = rSet.getMetaData();

		int colCount = rsmd.getColumnCount();
		if (colCount == 0)
			return false;

		for (int i = 1; i <= colCount; i++) {
			String name = rsmd.getColumnName(i);
			if (name.equals(colName))
				return true;
		}

		rSet.close();
		return false;
	}

	public String[] getColumns(String tableName) throws SQLException {

		ResultSet rSet = m_Connection.createStatement().executeQuery("select * from " + tableName);
		ResultSetMetaData rsmd = rSet.getMetaData();

		int colCount = rsmd.getColumnCount();
		if (colCount == 0)
			return new String[0];

		String[] cols = new String[colCount];
		for (int i = 1; i <= colCount; i++) {
			cols[i - 1] = rsmd.getColumnName(i);
		}

		rSet.close();
		return cols;
	}

	public String getColumnType(String tableName, String columnName) throws SQLException {

		//日期
		boolean isDate = getSqliteDate().isDateColumn(tableName, columnName);
		if(isDate) {
			return "DATE";
		}
			
		ResultSet rSet = m_Connection.createStatement().executeQuery("select * from " + tableName);
		ResultSetMetaData rsmd = rSet.getMetaData();

		int colCount = rsmd.getColumnCount();
		int c = 0;
		for (int i = 1; i <= colCount; i++) {
			if (rsmd.getColumnName(i).equals(columnName)) {
				c = i;
				break;
			}
		}
		String type = rsmd.getColumnTypeName(c);
		rSet.close();
		return type;
	}

	public String[] getTables() throws SQLException {

		DatabaseMetaData meta = m_Connection.getMetaData();
		ResultSet rs = meta.getTables(null, null, null, new String[] { "TABLE" });
		HashSet<String> tables = new HashSet<>();
		while (rs.next())
			tables.add((String) rs.getObject("TABLE_NAME"));
		rs.close();
		return tables.toArray(new String[0]);
	}

	public void insert(String tableName, HashMap vals) throws SQLException {
		Set keyset = vals.keySet();
		String[] keys = new String[keyset.size()];
		int k = 0;
		for (Object key : keyset)
			keys[k++] = (String) key;

		String sql = "insert into " + tableName + "(";
		for (int i = 0; i < keys.length; i++)
			sql += (i == 0 ? "" : ",") + keys[i];
		sql += ") values(";
		for (int i = 0; i < keys.length; i++)
			sql += (i == 0 ? "" : ",") + "?";
		sql += ")";
		PreparedStatement pre_stmt = m_Connection.prepareStatement(sql);

		for (int i = 0; i < keys.length; i++)
			pre_stmt.setObject(1 + i, vals.get(keys[i]));

		pre_stmt.executeUpdate();
	}

	public void insert(String tableName, HashMap[] vals) throws SQLException {
		if (vals.length == 0)
			return;
		Set keyset = vals[0].keySet();
		String[] keys = new String[keyset.size()];
		int k = 0;
		for (Object key : keyset)
			keys[k++] = (String) key;
		// System.out.println(keys.length);

		String sql = "insert into " + tableName + "(";
		for (int i = 0; i < keys.length; i++)
			sql += (i == 0 ? "" : ",") + keys[i];
		sql += ") values(";
		for (int i = 0; i < keys.length; i++)
			sql += (i == 0 ? "" : ",") + "?";
		sql += ")";
		PreparedStatement pre_stmt = m_Connection.prepareStatement(sql);

		for (int i = 0; i < vals.length; i++) {
			for (int j = 0; j < keys.length; j++) {
				// System.out.println(keys[j]+": " + vals[i].get(keys[j]).toString());
				pre_stmt.setObject(j + 1, vals[i].get(keys[j]));
			}
			pre_stmt.addBatch();
		}
		pre_stmt.executeBatch();
		pre_stmt.close();
	}

	public void insert(String tableName, List vals) throws SQLException {
		HashMap[] arr = List2HashMapArr(vals);
		insert(tableName, arr);
	}

	public static List<HashMap<String, Object>> HashMapArr2List(HashMap[] hm) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		for (int i = 0; i < hm.length; i++) {
			list.add(hm[i]);
		}
		return list;
	}

	public static HashMap[] List2HashMapArr(List list) {
		HashMap[] hm = new HashMap[list.size()];
		for (int i = 0; i < list.size(); i++) {
			hm[i] = (HashMap) list.get(i);
		}
		return hm;
	}

	public void delete(String tableName, int[] id) throws SQLException {
		String sql = "delete from " + tableName + " where id=?";
		PreparedStatement pre_stmt = m_Connection.prepareStatement(sql);
		for (int each_id : id) {
			pre_stmt.setInt(1, each_id);
			pre_stmt.addBatch();
			;
		}
		pre_stmt.executeBatch();
		pre_stmt.close();
	}

	@SuppressWarnings({ "unchecked" })
	public List<HashMap<String, Object>> query(String tableName, String[] columns, HashMap where) throws SQLException {
		String sql_column = "";

		if (columns == null || columns.length == 0)
			throw new SQLException("query error occur! no column");
		for (int i = 0; i < columns.length; i++)
			sql_column += (i == 0 ? "" : ",") + columns[i];

		String sql = "select " + sql_column + " from " + tableName + " ";

		ArrayList where_values = null;
		if (where != null) {
			String sql_where = "";
			where_values = new ArrayList<>();
			for (Object key : where.keySet()) {
				if (sql_where.equals(""))
					sql_where += " where " + (String) key + "=?";
				else
					sql_where += " and " + (String) key + "=?";
				where_values.add(where.get(key));
			}
			sql += sql_where;
		}

		PreparedStatement pre_stmt = m_Connection.prepareStatement(sql);

		if (where != null) {
			for (int i = 1; i <= where_values.size(); i++) {
				Object value = where_values.get(i - 1);
				pre_stmt.setObject(i, value);
			}
		}
		ResultSet rSet = pre_stmt.executeQuery();

		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		while (rSet.next()) {
			HashMap<String, Object> row = new HashMap<>();
			for (String column : columns) {
				if (getSqliteDate().isDateColumn(tableName, column)) {
					row.put(column, rSet.getDate(column));// 日期
				} else {
					row.put(column, rSet.getObject(column));
				}
			}
			list.add(row);
		}
		rSet.close();
		pre_stmt.close();
		return list;

	}

	public void modify(String tableName, int[] id, HashMap vals) throws SQLException {
		String sql = "update " + tableName + " set ?=? where id=?";
		PreparedStatement pre_stmt = m_Connection.prepareStatement(sql);

		for (Object key : vals.keySet()) {
			for (int each_id : id) {
				pre_stmt.setString(1, (String) key);
				Object value = vals.get(key);
				pre_stmt.setObject(2, value);
				pre_stmt.setInt(3, each_id);
				pre_stmt.addBatch();
			}
		}
		pre_stmt.executeBatch();

	}

	public void commit() throws SQLException {
		m_Connection.commit();
	}

	public void close() throws SQLException {
		m_Connection.close();
	}

	public void test(String tableName) {
		System.out.println("=======" + tableName + "=======");
		try {
			ResultSet rSet;
			rSet = m_Connection.createStatement().executeQuery("select * from " + tableName);
			ResultSetMetaData rsmd = rSet.getMetaData();

			int colCount = rsmd.getColumnCount();
			if (colCount == 0) {
				System.out.println("no data");
				return;
			}

			ArrayList<String> cols = new ArrayList<>();
			for (int i = 1; i <= colCount; i++) {
				cols.add(rsmd.getColumnName(i));
				System.out.print(rsmd.getColumnName(i) + "|");
			}
			while (rSet.next()) {
				System.out.println("");
				for (int j = 0; j < colCount; j++) {
					System.out.print(rSet.getString(cols.get(j)) + "|");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("\n=======end=======");

	}
}
