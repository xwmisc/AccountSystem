package com.xw;

import java.util.Date;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DBManager {
	private static DBManager m_DBManager;
	Connection m_Connection;

	PreparedStatement stmt_insert_emp;
	PreparedStatement stmt_insert_dfs;
	PreparedStatement stmt_insert_cmp;
	PreparedStatement stmt_insert_working;

	public static final String EMP = "EMP";
	public static final String EMP_ID = "id";
	public static final String EMP_NAME = "name";

	public static final String DFS = "DFS";
	public static final String DFS_ID = "id";
	public static final String DFS_NAME = "name";

	public static final String CMP = "CMP";
	public static final String CMP_ID = "id";
	public static final String CMP_NAME = "name";

	public static final String WORKING = "Working";
	public static final String WORKING_ID = "id";
	public static final String WORKING_WORKINGDATE = "workingDate";
	public static final String WORKING_EMPID = "empId";
	public static final String WORKING_DFSID = "dfsId";
	public static final String WORKING_CMPID = "cmpId";
	public static final String WORKING_刷货开工费现金 = "刷货开工费现金";
	public static final String WORKING_刷货开工费卡 = "刷货开工费卡";
	public static final String WORKING_刷货使用现金 = "刷货使用现金";
	public static final String WORKING_刷货使用卡 = "刷货使用卡";
	public static final String WORKING_刷货入库金额 = "刷货入库金额";
	public static final String WORKING_刷货费用 = "刷货费用";
	public static final String WORKING_刷货损失 = "刷货损失";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			Date timestamp = new Date();

			DBManager db = DBManager.getInstance();
			
			
			System.out.println("1 " + (new Date().getTime() - timestamp.getTime()));

			db.insert(EMP, Util.PairOf(EMP_NAME, "姜渊"));
			db.insert(CMP, Util.PairOf(CMP_NAME, "首尔林"));
			db.insert(DFS, Util.PairOf(DFS_NAME, "乐天"));

			db.commit();
			System.out.println("2 " + (new Date().getTime() - timestamp.getTime()));

			HashMap<String, Object> working = new HashMap<>();
			int id = 0;
			id = (int) ((HashMap) db.query(EMP, Util.SetOf(EMP_ID), Util.PairOf(EMP_NAME, "姜渊")).get(0)).get(EMP_ID);
			working.put(WORKING_EMPID, id);
			id = (int) ((HashMap) db.query(CMP, Util.SetOf(CMP_ID), Util.PairOf(CMP_NAME, "首尔林")).get(0)).get(CMP_ID);
			working.put(WORKING_CMPID, id);
			id = (int) ((HashMap) db.query(DFS, Util.SetOf(DFS_ID), Util.PairOf(DFS_NAME, "乐天")).get(0)).get(DFS_ID);
			working.put(WORKING_DFSID, id);
			working.put(WORKING_WORKINGDATE, new java.sql.Date(new Date().getTime()));
			working.put(WORKING_刷货开工费现金, 10000d);
			working.put(WORKING_刷货开工费卡, 20000d);
			working.put(WORKING_刷货使用现金, 3000d);
			working.put(WORKING_刷货使用卡, 4000d);
			working.put(WORKING_刷货入库金额, 500d);
			working.put(WORKING_刷货费用, 60d);
			working.put(WORKING_刷货损失, 7d);
			db.insert(WORKING, working);
			System.out.println("3 " + (new Date().getTime() - timestamp.getTime()));

			db.commit();
			System.out.println("4 " + (new Date().getTime() - timestamp.getTime()));

			db.test(EMP);
			db.test(CMP);
			db.test(DFS);
			db.test(WORKING);

			db.close();
			System.out.println("5 " + (new Date().getTime() - timestamp.getTime()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DBManager getInstance() throws IOException, SQLException {
		if (null == m_DBManager) {
			m_DBManager = new DBManager("database.db");
		}
		return m_DBManager;
	}

	private DBManager(String FileName) throws IOException {
		try {
			Class.forName("org.sqlite.JDBC");
			m_Connection = DriverManager.getConnection("jdbc:sqlite:" + FileName);
			m_Connection.setAutoCommit(false);

			createTable();
			stmt_insert_emp = m_Connection.prepareStatement("insert into EMP(name) values (?)");
			stmt_insert_dfs = m_Connection.prepareStatement("insert into DFS(name) values (?)");
			stmt_insert_cmp = m_Connection.prepareStatement("insert into CMP(name) values (?)");
			stmt_insert_working = m_Connection.prepareStatement("insert into "
					+ "Working(workingDate,empId,dfsId,cmpId,刷货开工费现金,刷货开工费卡,刷货使用现金,刷货使用卡,刷货入库金额,刷货费用,刷货损失) "
					+ "values (?,?,?,?,?,?,?,?,?,?,?)");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insert(String tableName, HashMap values) throws SQLException {
		switch (tableName) {
		case EMP:
			stmt_insert_emp.setString(1, (String) values.get(EMP_NAME));
			stmt_insert_emp.executeUpdate();
			break;
		case DFS:
			stmt_insert_dfs.setString(1, (String) values.get(DFS_NAME));
			stmt_insert_dfs.executeUpdate();
			break;
		case CMP:
			stmt_insert_cmp.setString(1, (String) values.get(CMP_NAME));
			stmt_insert_cmp.executeUpdate();
			break;
		case WORKING:
			stmt_insert_working.setDate(1, (java.sql.Date) values.get(WORKING_WORKINGDATE));
			stmt_insert_working.setInt(2, (int) values.get(WORKING_EMPID));
			stmt_insert_working.setInt(3, (int) values.get(WORKING_DFSID));
			stmt_insert_working.setInt(4, (int) values.get(WORKING_CMPID));
			stmt_insert_working.setDouble(5, (double) values.get(WORKING_刷货开工费现金));
			stmt_insert_working.setDouble(6, (double) values.get(WORKING_刷货开工费卡));
			stmt_insert_working.setDouble(7, (double) values.get(WORKING_刷货使用现金));
			stmt_insert_working.setDouble(8, (double) values.get(WORKING_刷货使用卡));
			stmt_insert_working.setDouble(9, (double) values.get(WORKING_刷货入库金额));
			stmt_insert_working.setDouble(10, (double) values.get(WORKING_刷货费用));
			stmt_insert_working.setDouble(11, (double) values.get(WORKING_刷货损失));
			stmt_insert_working.executeUpdate();
			break;
		default:
			throw new SQLException("no such table \"" + tableName + "\"");
		}
	}

	public void delete(String tableName, int[] id) throws SQLException {
		String sql = "delete from " + tableName + " where id=?";
		PreparedStatement pre_stmt = m_Connection.prepareStatement(sql);
		for (int each_id : id) {
			pre_stmt.setInt(1, each_id);
			pre_stmt.executeUpdate();
		}
	}

	@SuppressWarnings({ "unchecked" })
	public List<HashMap<String, Object>> query(String tableName, Set<String> columns, HashMap where)
			throws SQLException {
		String sql_column = "";

		if (columns == null)
			throw new SQLException("query error occur! no column");
		for (String column : columns) {
			if (!sql_column.equals(""))
				sql_column += ",";
			sql_column += column;
		}

		String sql = "select " + sql_column + " from " + tableName + " ";

		ArrayList where_values = null;
		if (where != null) {
			String sql_where = "where ";
			where_values = new ArrayList<>();
			for (Object key : where.keySet()) {
				if (!sql_where.equals(""))
					sql_where += " " + (String) key + "=?";
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
				if (value instanceof Integer)
					pre_stmt.setInt(i, (int) value);
				else if (value instanceof Double)
					pre_stmt.setDouble(i, (double) value);
				else if (value instanceof Date)
					pre_stmt.setDate(i, (java.sql.Date) value);
				else if (value instanceof String)
					pre_stmt.setString(i, (String) value);
				else
					throw new SQLException("query error occur! unknow type " + value.getClass().getName());
			}
		}
		ResultSet rSet = pre_stmt.executeQuery();

		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		while (rSet.next()) {
			HashMap<String, Object> row = new HashMap<>();
			for (String column : columns) {
				row.put(column, rSet.getObject(column));
			}
			list.add(row);
		}
		rSet.close();
		return list;

	}

	public void modify(String tableName, int[] id, HashMap values) throws SQLException {
		String sql = "update " + tableName + " set ?=? where id=?";
		PreparedStatement pre_stmt = m_Connection.prepareStatement(sql);

		for (Object key : values.keySet()) {
			for (int each_id : id) {
				pre_stmt.setString(1, (String) key);
				Object value = values.get(key);
				if (value instanceof Integer)
					pre_stmt.setInt(2, (int) value);
				else if (value instanceof Double)
					pre_stmt.setDouble(2, (double) value);
				else if (value instanceof Date)
					pre_stmt.setDate(2, (java.sql.Date) value);
				else if (value instanceof String)
					pre_stmt.setString(2, (String) value);
				else
					throw new SQLException("modify error occur! unknow type " + value.getClass().getName());
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

	private void createTable() throws SQLException {
		String sql = "create table if not exists DFS(\r\n" + "	id integer primary key autoincrement,\r\n"
				+ "	name text not null unique\r\n" + ");\r\n" + "create table if not exists EMP(\r\n"
				+ "	id integer primary key autoincrement,\r\n" + "	name text not null unique\r\n" + ");\r\n"
				+ "create table if not exists CMP(\r\n" + "	id integer primary key autoincrement,\r\n"
				+ "	name text not null unique\r\n" + ");\r\n" + "create table if not exists Working(\r\n"
				+ "	id integer primary key autoincrement,\r\n" + "	workingDate datetime not null,\r\n"
				+ "	empId integer not null,\r\n" + "	dfsId integer not null,\r\n" + "	cmpId integer not null,\r\n"
				+ "	刷货开工费现金 double not null,\r\n" + "	刷货开工费卡 double not null,\r\n" + "	刷货使用现金 double not null,\r\n"
				+ "	刷货使用卡 double not null,\r\n" + "	刷货入库金额 double not null,\r\n" + "	刷货费用 double not null,\r\n"
				+ "	刷货损失 double not null,\r\n" + "	foreign key (empId) references EMP(id),\r\n"
				+ "	foreign key (dfsId) references DFS(id),\r\n" + "	foreign key (cmpId) references CMP(id)\r\n"
				+ ")";
		// m_Connection.createStatement().execute("drop table if exists DFS");
		// m_Connection.createStatement().execute("drop table if exists EMP");
		// m_Connection.createStatement().execute("drop table if exists CMP");
		// m_Connection.createStatement().execute("drop table if exists Working");
		m_Connection.commit();
		m_Connection.createStatement().executeUpdate(sql);
		m_Connection.commit();
	}

	public static List<String> getColumnsName(String tableName) {
		List<String> list = null;
		switch (tableName) {
		case EMP:
			list = Arrays.asList(EMP_ID, EMP_NAME);
			break;
		case DFS:
			list = Arrays.asList(DFS_ID, DFS_NAME);
			break;
		case CMP:
			list = Arrays.asList(CMP_ID, CMP_NAME);
			break;
		case WORKING:
			list = Arrays.asList(WORKING_ID, WORKING_EMPID, WORKING_DFSID, WORKING_CMPID, WORKING_WORKINGDATE,
					WORKING_刷货开工费现金, WORKING_刷货开工费卡, WORKING_刷货使用现金, WORKING_刷货使用卡, WORKING_刷货入库金额, WORKING_刷货费用,
					WORKING_刷货损失);
			break;
		}
		return list;
	}

	public static int findIdByName(String tableName, String name) throws SQLException, IOException {
		DBManager db = DBManager.getInstance();
		switch (tableName) {
		case EMP:
			return (int) ((HashMap) db.query(tableName, Util.SetOf(EMP_ID), Util.PairOf(EMP_NAME, name)).get(0))
					.get(EMP_ID);
		case DFS:
			return (int) ((HashMap) db.query(tableName, Util.SetOf(DFS_ID), Util.PairOf(DFS_NAME, name)).get(0))
					.get(DFS_ID);
		case CMP:
			return (int) ((HashMap) db.query(tableName, Util.SetOf(CMP_ID), Util.PairOf(CMP_NAME, name)).get(0))
					.get(CMP_ID);
		}
		try {
			return (int) ((HashMap) db.query(tableName, Util.SetOf(EMP_ID), Util.PairOf(EMP_NAME, name)).get(0))
					.get(EMP_ID);
		} catch (Exception e) {
		}
		try {
			return (int) ((HashMap) db.query(tableName, Util.SetOf(DFS_ID), Util.PairOf(DFS_NAME, name)).get(0))
					.get(DFS_ID);
		} catch (Exception e) {
		}
		try {
			return (int) ((HashMap) db.query(tableName, Util.SetOf(CMP_ID), Util.PairOf(CMP_NAME, name)).get(0))
					.get(CMP_ID);
		} catch (Exception e) {
		}
		return -1;
	}

	public void test(String tableName) throws SQLException {
		System.out.println("=======" + tableName + "=======");
		ResultSet rSet;
		rSet = m_Connection.createStatement().executeQuery("select * from " + tableName);
		int i = 1;
		try {
			while (true) {
				rSet.getString(i);
				i += 1;
			}
		} catch (SQLException e) {
		}
		if (i == 1) {
			System.out.println("no data");
			return;
		}
		while (rSet.next()) {
			for (int j = 1; j < i; j++) {
				System.out.print(rSet.getString(j) + "|");
			}
			System.out.println("");
		}

		System.out.println("=======end=======");

	}
}
