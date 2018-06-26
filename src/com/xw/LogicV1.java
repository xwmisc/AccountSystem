package com.xw;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.xw.db.DB;
import com.xw.db.DB.SQLITE3_TYPE;
import com.xw.excel.Excel;
import com.xw.excel.ExcelException;
import com.xw.excel.Excel.Sheet;

public class LogicV1 {

	final static String table1 = "系统_折扣表";
	final static String table2 = "系统_对账表";
	final static String table3 = "系统_返点表";
	final static String table4 = "生成_对账结果";
	final static String table5 = "生成_应收检查";
	final static String table6 = "生成_返点检查";

	public final static String EMP = "系统_EMP";

	static void setup() throws SQLException {

		DB db = DB.getInstance();
		if (db.existTable(table1))
			db.deleteTable(table1);
		if (db.existTable(table2))
			db.deleteTable(table2);
		if (db.existTable(table3))
			db.deleteTable(table3);
		if (db.existTable(table4))
			db.deleteTable(table4);
		if (db.existTable(table5))
			db.deleteTable(table5);
		if (db.existTable(table6))
			db.deleteTable(table6);

		db.createEmptyTable(table1);
		db.insertColumn(table1, "姓名", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table1, "日期", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table1, "款项类型", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table1, "应收增加", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table1, "应收减少", SQLITE3_TYPE.TYPE_NUMBER);

		db.createEmptyTable(table2);
		db.insertColumn(table2, "姓名", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table2, "日期", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table2, "款项类型", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table2, "单据编号", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table2, "应收增加", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table2, "应收减少", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table2, "备注", SQLITE3_TYPE.TYPE_TEXT);

		db.createEmptyTable(table3);
		db.insertColumn(table3, "日期", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table3, "当日总刷货返点", SQLITE3_TYPE.TYPE_NUMBER);

		db.createEmptyTable(table4);
		db.insertColumn(table4, "姓名", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table4, "日期", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table4, "款项类型", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table4, "应收增加差额", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table4, "应收减少差额", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table4, "折扣表应收增加", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table4, "折扣表应收减少", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table4, "对账表应收增加", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table4, "对账表应收减少", SQLITE3_TYPE.TYPE_NUMBER);

		db.createEmptyTable(table5);
		db.insertColumn(table5, "姓名", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table5, "日期", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table5, "应收增加合计", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table5, "应收减少合计", SQLITE3_TYPE.TYPE_NUMBER);

		db.createEmptyTable(table6);
		db.insertColumn(table6, "日期", SQLITE3_TYPE.TYPE_TEXT);
		db.insertColumn(table6, "返点差额", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table6, "返点表总额", SQLITE3_TYPE.TYPE_NUMBER);
		db.insertColumn(table6, "折扣表总额", SQLITE3_TYPE.TYPE_NUMBER);

		db.commit();// 提交事务
	}

	public static boolean account(File account_folder) {
		try {
			System.out.println(account_folder.getPath());

			final String s1_name = "对账结果";
			final String s2_name = "应收检查";
			final String s3_name = "返点检查";

			DB db = DB.getInstance();
			setup();

			for (File file : account_folder.listFiles()) {
				if (file.getName().matches("[0-9]{4}折扣表\\.xlsx?$")) {
					System.out.println("折扣表" + file.getAbsolutePath());
					addRecord1(file);
				}
				if (file.getName().matches("往来对账数据.*\\.xlsx?$")) {
					System.out.println("对账表" + file.getAbsolutePath());
					addRecord2(file);
				}
				if (file.getName().matches("返点收入余额表.*\\.xlsx?$")) {
					System.out.println("返点收入余额表" + file.getAbsolutePath());
					addRecord3(file);
				}
			}
			db.commit();// 提交事务

			// db.test(table1);
			// db.test(table2);
			// db.test(table3);

			String[] type = new String[] { "刷货开工费", "刷货差额", "刷货退回", "刷货返点", "刷货费用", "刷货入库" };
			String[] item = new String[] { "姓名", "款项类型", "日期", "应收增加", "应收减少" };

			HashMap<String, Double> count1 = new HashMap<>();
			HashMap<String, Double> count2 = new HashMap<>();

			// 集合 折扣表1中所有日期
			HashSet<String> day_set = new HashSet<>();
			for (HashMap day : db.query(table1, new String[] { "日期" }, null))
				day_set.add((String) day.get("日期"));

			// 集合 所有员工
			List<HashMap<String, Object>> emps = db.query(EMP, new String[] { "name" }, null);
			HashSet<String> staff_set = new HashSet<>();
			for (HashMap emp : emps)
				staff_set.add((String) emp.get("name"));

			// 数据
			List vals1 = new ArrayList();
			List vals2 = new ArrayList();
			List vals3 = new ArrayList();

			// 开始
			for (String staff : staff_set) {
				for (String each_day : day_set) {

					double receivable_incr = 0;
					double receivable_decr = 0;

					for (String each_type : type) {
						// excel_format = "";
						// excel_format = excel_format + staff + Sheet.DIV;
						// excel_format = excel_format + each_day + Sheet.DIV;
						// excel_format = excel_format + each_type + Sheet.DIV;

						// 初始化应收统计
						count1.put("应收增加", 0.0);
						count1.put("应收减少", 0.0);
						count2.put("应收增加", 0.0);
						count2.put("应收减少", 0.0);

						HashMap condition = Util.PairOf("姓名", staff, "日期", each_day, "款项类型", each_type);
						// 按照以上条件查询table1
						List<HashMap<String, Object>> data1 = db.query(table1, new String[] { "应收增加", "应收减少" },
								condition);
						// 按照以上条件查询table2
						List<HashMap<String, Object>> data2 = db.query(table2, new String[] { "应收增加", "应收减少" },
								condition);

						// 检查应收
						for (HashMap val : data1) {
							double num = (double) val.get("应收增加");
							receivable_incr += num;
							count1.put("应收增加", count1.get("应收增加") + num);
							num = (double) val.get("应收减少");
							receivable_decr += num;
							count1.put("应收减少", count1.get("应收减少") + num);
						}
						for (HashMap val : data2) {
							double num = (double) val.get("应收增加");
							receivable_incr += num;
							count2.put("应收增加", count1.get("应收增加") + num);
							num = (double) val.get("应收减少");
							receivable_decr += num;
							count2.put("应收减少", count1.get("应收减少") + num);
						}

						// 匹配应收
						double sum = count1.get("应收增加") - count1.get("应收减少")
								- (count2.get("应收增加") - count2.get("应收减少"));
						if (sum != 0) {
							// 发现问题
							HashMap val = new HashMap<>();
							val.put("姓名", staff);
							val.put("日期", each_day);
							val.put("款项类型", each_type);
							val.put("应收增加差额", count1.get("应收增加") - count2.get("应收增加"));
							val.put("应收减少差额", count1.get("应收减少") - count2.get("应收减少"));
							val.put("折扣表应收增加", count1.get("应收增加"));
							val.put("折扣表应收减少", count1.get("应收减少"));
							val.put("对账表应收增加", count2.get("应收增加"));
							val.put("对账表应收减少", count2.get("应收减少"));
							vals1.add(val);
						}
					}

					// 匹配同一人同一天所有应收
					if (receivable_incr != receivable_decr) {
						HashMap val = new HashMap<>();
						val.put("姓名", staff);
						val.put("日期", each_day);
						val.put("应收增加合计", receivable_incr);
						val.put("应收减少合计", receivable_decr);
						vals2.add(val);
					}

				}
			}

			// 返点检查
			// 集合 所有折扣表和返点表日期
			day_set = new HashSet<>();
			for (HashMap day : db.query(table1, new String[] { "日期" }, null))
				day_set.add((String) day.get("日期"));
			for (HashMap day : db.query(table3, new String[] { "日期" }, null))
				day_set.add((String) day.get("日期"));

			HashMap<String, String> point_condition = new HashMap<>();
			for (String each_day : day_set) {
				point_condition.clear();
				point_condition.put("日期", each_day);
				List<HashMap<String, Object>> record_point = null;
				List<HashMap<String, Object>> account_point = null;
				try {
					record_point = db.query(table3, new String[] { "当日总刷货返点" }, point_condition);
					point_condition.put("款项类型", "刷货返点");
					account_point = db.query(table1, new String[] { "应收减少" }, point_condition);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				double point1 = record_point.size() == 0 ? 0.0
						: Optional.ofNullable((double) record_point.get(0).get("当日总刷货返点")).orElse(0.0);
				double point2 = account_point.stream().mapToDouble(point -> (double) (((HashMap) point).get("应收减少")))
						.sum();
				if (point1 != point2) {
					HashMap val = new HashMap<>();
					val.put("日期", each_day);
					val.put("返点差额", point2 - point1);
					val.put("返点表总额", point1);
					val.put("折扣表总额", point2);
					vals3.add(val);
				}
			}

			db.insert(table4, vals1);
			db.insert(table5, vals2);
			db.insert(table6, vals3);
			db.commit();
			System.out.println("录入完成!");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 折扣表
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void addRecord1(File file) throws Exception {
		// System.out.println("addRecord1");
		DB db = DB.getInstance();

		Excel excel = new Excel(file);

		for (Sheet sheet : excel.getSheets()) {
			// 遍历每一个表

			// 跳过不存在员工
			String emp_name = sheet.getName();
			List emps = db.query(EMP, new String[] { "name" }, Util.PairOf("name", emp_name));
			if (emps.size() <= 0)
				continue;

			int base_row = 1;
			int base_column = 1;

			String emp_name2 = ((String) sheet.readString(base_row, base_column + 1, "")).trim();
			if (!emp_name.contains(emp_name2))
				continue;

			// 跳过不正确日期
			String date = sheet.read(base_row + 1, base_column + 1).toString().trim();
			if (!file.getName().contains(date))
				continue;

			// 欲提交数据
			List vals = new ArrayList();

			// 遍历款项类型
			for (int i = 0; i < 6; i++) {
				// 读取数据
				HashMap<String, Object> val = new HashMap<>();
				val.put("姓名", emp_name);
				val.put("日期", date);
				String type = ((String) sheet.read(base_row + 3 + i, base_column)).trim();
				val.put("款项类型", type);

				// 分为应收增加/应收减少
				if (type.equals("刷货差额")) {
					// 若为刷货差额,则调整差额正负
					// TODO num正负
					double num = sheet.readDouble(base_row + 3 + i, base_column + 1, 0);
					if (num < 0) {
						val.put("应收增加", 0.0);
						val.put("应收减少", num);
					} else {
						val.put("应收增加", num);
						val.put("应收减少", 0.0);
					}
				} else {
					// 正常插入
					double num1 = sheet.readDouble(base_row + 3 + i, base_column + 1, 0);
					val.put("应收增加", num1);
					double num2 = sheet.readDouble(base_row + 3 + i, base_column + 2, 0);
					val.put("应收减少", num2);
				}
				// 添加到数据
				vals.add(val);
			}

			// 提交
			db.insert(table1, vals);
		}
		excel.close();
	}

	/**
	 * 对账单
	 * 
	 * @param dbm
	 * @param filePath
	 * @throws ExcelException
	 * @throws IOException
	 * @throws SQLException
	 * @throws DBException
	 */
	public static void addRecord2(File file) throws ExcelException, IOException, SQLException {
		// System.out.println("addRecord2");

		Excel excel = new Excel(file);
		Sheet sheet = excel.getSheets().get(0);

		DB db = DB.getInstance();
		List<HashMap<String, Object>> emps = db.query(EMP, new String[] { "name" }, null);

		List vals = new ArrayList();

		// 遍历所有数据行
		for (int i = 0; i < sheet.getRowCount() - 10; i++) {

			int row = 10 + i;

			// 跳过无行号的行
			if ((Double) sheet.readDouble(row, 1, 0) == 0)
				continue;

			// 备注合法性验证
			String remark = "";
			try {
				String _remark = sheet.readString(row, 11, "");
				remark = FJ.convert((_remark).trim(), 0);
			} catch (Exception e) {
				remark = "";
			}

			// 姓名合法性验证
			String staff_name = null;
			for (HashMap emp : emps) {
				String name = (String) emp.get("name");
				if (remark.matches("[0-9]{4} (刷货).{2,4} (" + name + ").*")) {
					staff_name = name;
					break;
				}
			}

			// 数据
			HashMap<String, Object> val = new HashMap<>();

			if (staff_name != null) {
				String sp_remark[] = remark.split(" ");
				val.put("日期", sp_remark[0]);
				val.put("款项类型", sp_remark[1]);
				val.put("姓名", staff_name);
				val.put("单据编号", sheet.readString(row, 3, ""));

				double num = sheet.readDouble(row, 5, 0);
				val.put("应收增加", num);
				num = sheet.readDouble(row, 6, 0);
				val.put("应收减少", num);
				val.put("备注", remark);

				vals.add(val);
				// System.out.println("ValidData:|" + "i:" + i + "|" + sheet.read(row, 1) + "|"
				// + remark);
			} else {
				// System.out.println("IgnoreError|" + "i:" + i + "|" + sheet.read(row, 1) + "|"
				// + remark);
			}
		}

		db.insert(table2, vals);

		excel.close();
	}

	public static void addRecord3(File file) throws ExcelException, IOException, SQLException {
		System.out.println("addRecord3");

		Excel excel = new Excel(file);
		Sheet sheet = excel.getSheets().get(0);

		int base_row = 5;
		int row_add = 0;

		DB db = DB.getInstance();
		List vals = new ArrayList();

		Date date = null;
		while (true) {
			try {
				Object obj = sheet.read(row_add + base_row, 1);
				if (obj instanceof Double)
					date = new Date((long) (((Double) obj - 25568 - 1) * 1000l * 60 * 60 * 24));
				else
					date = (Date) obj;
				if (null == date) {
					System.out.println("inValidData:|" + date);
					break;
				}
			} catch (ClassCastException e) {
				break;
			}
			// System.out.println("ValidData:|" + date);

			// 数据
			HashMap val = new HashMap<>();

			String date_text = ((date.getMonth() + 1 < 10) ? "0" : "") + (date.getMonth() + 1)
					+ ((date.getDate() < 10) ? "0" : "") + date.getDate();
			System.out.println("ValidData:|" + date_text);
			val.put("日期", date_text);

			double num = sheet.readDouble(row_add + base_row, 2, 0);
			val.put("当日总刷货返点", num);

			vals.add(val);
			row_add++;
		}

		db.insert(table3, vals);
		excel.close();
	}
}