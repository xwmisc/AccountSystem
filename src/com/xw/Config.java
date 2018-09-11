package com.xw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;
import com.xw.db.DB;
import com.xw.excel.Excel;
import com.xw.excel.Excel.Sheet;
import com.xw.excel.ExcelException;

public class Config {
	public static Properties prop = null;
	static {
		if (prop == null) {
			prop = new Properties();
			try {
				File conf = new File("config.txt");
				if (!conf.exists()) {
					conf.createNewFile();
					saveEMP();
					store();
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(conf)));
				prop.load(in);
				in.close();
				loadEMP();
			} catch (IOException | SQLException | ExcelException  e) {
				e.printStackTrace();
				Log.logger().error(e.getMessage(), e);
			}
		}

	}

	public static void store() {
		FileWriter out;
		try {
			out = new FileWriter("config.txt");
			prop.store(out,
					"This file is a configuration file. Please note that the both sides of '=' do not write spaces character.");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			store();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 数据库初始化之后才能调用
	 * 
	 * @throws IOException
	 * @throws ExcelException
	 * @throws SQLException
	 */
	public static void loadEMP() throws IOException, ExcelException, SQLException {
		DB db = DB.getInstance();

		String emplist = prop.getProperty("employees", "");
		System.out.println(emplist);
		String[] emps = emplist.split(",");
		// 查找有效员工
		ArrayList<String> emps_excel = new ArrayList<>();
		emps_excel.addAll(Arrays.asList(emps));
		for (int index = emps_excel.size() - 1; index >= 0; index--) {
			String emp = emps_excel.get(index);
			if (emp.equals("")) {
				emps_excel.remove(index);
			}
		}

		// 检查
		ArrayList<Integer> emps_none = new ArrayList<>();
		List<HashMap<String, Object>> db_emps = db.query(LogicV1.EMP, new String[] { "id", "name" }, null);
		for (HashMap val : db_emps) {
			String db_emp = (String) val.get("name");
			boolean emp_exist = false;
			for (String emp : emps_excel) {
				if (db_emp.equals(emp)) {
					emp_exist = true;
					break;
				}
			}
			if (emp_exist) {
				emps_excel.remove(db_emp);
			} else {
				emps_none.add((Integer) val.get("id"));
			}
		}
		// 删除
		if (emps_none.size() > 0) {
			int[] ids = new int[emps_none.size()];
			for (int index = 0; index < emps_none.size(); index++)
				ids[index] = emps_none.get(index);
			db.delete(LogicV1.EMP, ids);
		}
		// 录入
		if (emps_excel.size() > 0) {
			ArrayList<HashMap> vals = new ArrayList<>();
			for (String emp : emps_excel) {
				HashMap val = new HashMap<>();
				val.put("name", emp);
				vals.add(val);
			}
			db.insert(LogicV1.EMP, vals);
			db.commit();
		}

		db.test(LogicV1.EMP);
	}

	/**
	 * 数据库初始化之后才能调用
	 * 
	 * @throws IOException
	 * @throws ExcelException
	 * @throws SQLException
	 */
	public static void loadSetting() throws IOException, ExcelException, SQLException {
		DB db = DB.getInstance();
		File file = new File("config.xlsx");
		Log.logger().info(file.getAbsolutePath());
		if (!file.exists())
			saveSetting();

		Excel excel = new Excel(file);
		Sheet sheet = excel.getSheet("config");
		for (int col = 1; col <= sheet.getColCount(1); col++) {
			String title = sheet.readString(1, col, "");
			if (title.equals(""))
				continue;
			if (title.equals("员工")) {
				// 查找有效员工
				ArrayList<String> emps_excel = new ArrayList<>();
				for (int row = 2; row <= sheet.getRowCount(); row++) {
					String emp = sheet.readString(row, col, "").trim();
					if (!emp.equals("")) {
						emps_excel.add(emp);
					}
				}
				// 检查
				ArrayList<Integer> emps_none = new ArrayList<>();
				List<HashMap<String, Object>> db_emps = db.query(LogicV1.EMP, new String[] { "id", "name" }, null);
				for (HashMap val : db_emps) {
					String db_emp = (String) val.get("name");
					boolean emp_exist = false;
					for (String emp : emps_excel) {
						if (db_emp.equals(emp)) {
							emp_exist = true;
							break;
						}
					}
					if (emp_exist) {
						emps_excel.remove(db_emp);
					} else {
						emps_none.add((Integer) val.get("id"));
					}
				}
				// 删除
				if (emps_none.size() > 0) {
					int[] ids = new int[emps_none.size()];
					for (int index = 0; index < emps_none.size(); index++)
						ids[index] = emps_none.get(index);
					db.delete(LogicV1.EMP, ids);
				}
				// 录入
				if (emps_excel.size() > 0) {
					ArrayList<HashMap> vals = new ArrayList<>();
					for (String emp : emps_excel) {
						HashMap val = new HashMap<>();
						val.put("name", emp);
						vals.add(val);
					}
					db.insert(LogicV1.EMP, vals);
				}
			}

		}
		db.test(LogicV1.EMP);
	}

	public static void saveEMP() throws SQLException, IOException, ExcelException {
		DB db = DB.getInstance();
		// 查询录入
		List<HashMap<String, Object>> db_emps = db.query(LogicV1.EMP, new String[] { "id", "name" }, null);
		String emplist = "";
		for (HashMap val : db_emps) {
			String db_emp = (String) val.get("name");
			emplist += db_emp + ",";
		}
		prop.setProperty("employees", emplist);
	}

	public static void saveSetting() throws SQLException, IOException, ExcelException {
		DB db = DB.getInstance();
		File file = new File("config.xlsx");
		Log.logger().info(file.getAbsolutePath());
		file.mkdirs();
		// 初始化excel
		Excel excel = Excel.createExcel(file.getAbsolutePath(), true);
		Sheet sheet = excel.createSheet("config");
		// 查询录入
		sheet.write(1, 1, "EMPS");
		List<HashMap<String, Object>> db_emps = db.query(LogicV1.EMP, new String[] { "id", "name" }, null);
		int row = 2;
		for (HashMap val : db_emps) {
			String db_emp = (String) val.get("name");
			sheet.write(row, 1, db_emp);
		}
		// 保存
		excel.closeWithSave();
	}

}
