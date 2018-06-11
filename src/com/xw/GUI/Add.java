package com.xw.GUI;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.xw.DBManager;
import com.xw.ExcelAPI;
import com.xw.ExcelAPI.ExcelException;
import com.xw.ExcelAPI.Sheet;
import com.xw.Log;
import com.xw.Logic;
import com.xw.Util;
import com.xw.excel.Excel;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.DND;

public class Add extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text text_刷货开工费现金;
	private Text text_刷货开工费卡;
	private Text text_刷货使用现金;
	private Text text_刷货使用卡;
	private Text text_刷货损失;
	private Text text_刷货费用;
	private Text text_刷货入库金额;
	private Text text_emp_name;
	private Text text_dfs_name;
	private Text text_cmp_name;
	private Combo combo_EMP;
	private Combo combo_DFS;
	private Combo combo_CMP;
	private DateTime dateTime;
	private Text text_file;
	private Text text_表头行号;
	private Text text_结束行号;
	private Text text_开始行号;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public Add(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(564, 632);
		shell.setText(getText());

		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setBounds(22, 149, 513, 289);

		combo_EMP = new Combo(composite, SWT.READ_ONLY);
		combo_EMP.setBounds(10, 101, 92, 28);

		Label label = new Label(composite, SWT.NONE);
		label.setText("员工");
		label.setBounds(10, 77, 76, 20);

		dateTime = new DateTime(composite, SWT.BORDER);
		dateTime.setBounds(10, 36, 115, 28);

		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setText("日期");
		label_1.setBounds(10, 10, 45, 20);

		Label lblDfs = new Label(composite, SWT.NONE);
		lblDfs.setText("DFS");
		lblDfs.setBounds(10, 147, 76, 20);

		combo_DFS = new Combo(composite, SWT.READ_ONLY);
		combo_DFS.setBounds(10, 171, 92, 28);

		combo_CMP = new Combo(composite, SWT.READ_ONLY);
		combo_CMP.setBounds(10, 240, 92, 28);

		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setText("返点公司");
		label_3.setBounds(10, 216, 76, 20);

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setText("刷货开工费现金");
		label_2.setBounds(150, 10, 115, 20);

		text_刷货开工费现金 = new Text(composite, SWT.BORDER);
		text_刷货开工费现金.setBounds(150, 38, 163, 26);

		text_刷货开工费卡 = new Text(composite, SWT.BORDER);
		text_刷货开工费卡.setBounds(150, 105, 163, 26);

		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setText("刷货开工费卡");
		label_4.setBounds(150, 77, 115, 20);

		text_刷货使用现金 = new Text(composite, SWT.BORDER);
		text_刷货使用现金.setBounds(150, 175, 163, 26);

		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setText("刷货使用现金");
		label_5.setBounds(150, 147, 115, 20);

		text_刷货使用卡 = new Text(composite, SWT.BORDER);
		text_刷货使用卡.setBounds(150, 244, 163, 26);

		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setText("刷货使用卡");
		label_6.setBounds(150, 216, 115, 20);

		text_刷货损失 = new Text(composite, SWT.BORDER);
		text_刷货损失.setBounds(342, 175, 163, 26);

		Label label_7 = new Label(composite, SWT.NONE);
		label_7.setText("刷货损失");
		label_7.setBounds(342, 147, 115, 20);

		text_刷货费用 = new Text(composite, SWT.BORDER);
		text_刷货费用.setBounds(342, 105, 163, 26);

		Label label_8 = new Label(composite, SWT.NONE);
		label_8.setText("刷货费用");
		label_8.setBounds(342, 77, 115, 20);

		text_刷货入库金额 = new Text(composite, SWT.BORDER);
		text_刷货入库金额.setBounds(342, 38, 163, 26);

		Label label_9 = new Label(composite, SWT.NONE);
		label_9.setText("刷货入库金额");
		label_9.setBounds(342, 10, 115, 20);

		Button button = new Button(composite, SWT.NONE);
		button.setBounds(342, 238, 98, 30);
		button.setText("添加记录");
		button.addSelectionListener(GUI.OnClick(e -> {
			addRecord();
		}));

		Composite composite_1 = new Composite(shell, SWT.BORDER);
		composite_1.setBounds(22, 10, 133, 133);

		Label label_10 = new Label(composite_1, SWT.NONE);
		label_10.setText("员工");
		label_10.setBounds(10, 21, 76, 20);

		text_emp_name = new Text(composite_1, SWT.BORDER);
		text_emp_name.setBounds(10, 47, 88, 26);

		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.setText("添加员工");
		button_1.setBounds(10, 93, 98, 30);
		button_1.addSelectionListener(GUI.OnClick(e -> {
			addEMP();
		}));

		Composite composite_2 = new Composite(shell, SWT.BORDER);
		composite_2.setBounds(161, 10, 182, 133);

		Label lblDfs_1 = new Label(composite_2, SWT.NONE);
		lblDfs_1.setText("DFS");
		lblDfs_1.setBounds(10, 21, 76, 20);

		text_dfs_name = new Text(composite_2, SWT.BORDER);
		text_dfs_name.setBounds(10, 47, 147, 26);

		Button btndfs = new Button(composite_2, SWT.NONE);
		btndfs.setText("添加DFS");
		btndfs.setBounds(10, 93, 147, 30);
		btndfs.addSelectionListener(GUI.OnClick(e -> {
			addDFS();
		}));

		Composite composite_3 = new Composite(shell, SWT.BORDER);
		composite_3.setBounds(349, 10, 186, 133);

		Label label_12 = new Label(composite_3, SWT.NONE);
		label_12.setText("返点公司");
		label_12.setBounds(10, 21, 76, 20);

		text_cmp_name = new Text(composite_3, SWT.BORDER);
		text_cmp_name.setBounds(10, 47, 154, 26);

		Button button_3 = new Button(composite_3, SWT.NONE);
		button_3.setText("添加返点公司");
		button_3.setBounds(10, 93, 154, 30);
		button_3.addSelectionListener(GUI.OnClick(e -> {
			addCMP();
		}));

		Composite composite_4 = new Composite(shell, SWT.BORDER);
		composite_4.setBounds(22, 444, 513, 144);

		Label label_11 = new Label(composite_4, SWT.NONE);
		label_11.setText("文件拖拽");
		label_11.setBounds(10, 13, 61, 20);

		text_file = new Text(composite_4, SWT.BORDER);
		text_file.setBounds(77, 10, 426, 26);

		DropTarget dropTarget = new DropTarget(text_file, DND.DROP_MOVE);
		Transfer[] transfer = new Transfer[] { FileTransfer.getInstance() };
		dropTarget.setTransfer(transfer);

		Composite composite_5 = new Composite(composite_4, SWT.BORDER);
		composite_5.setBounds(7, 48, 496, 77);

		Button check_autosetting = new Button(composite_5, SWT.CHECK);
		check_autosetting.setText("手动配置");
		check_autosetting.setBounds(10, 10, 100, 20);

		Label label_13 = new Label(composite_5, SWT.NONE);
		label_13.setBounds(151, 10, 76, 20);
		label_13.setText("表头行号：");

		Label label_14 = new Label(composite_5, SWT.NONE);
		label_14.setText("结束行号：");
		label_14.setBounds(375, 10, 76, 20);

		text_表头行号 = new Text(composite_5, SWT.BORDER);
		text_表头行号.setText("1");
		text_表头行号.setBounds(151, 36, 76, 26);

		text_结束行号 = new Text(composite_5, SWT.BORDER);
		text_结束行号.setText("100");
		text_结束行号.setBounds(375, 36, 76, 26);

		text_开始行号 = new Text(composite_5, SWT.BORDER);
		text_开始行号.setText("2");
		text_开始行号.setBounds(264, 36, 76, 26);

		Label label_15 = new Label(composite_5, SWT.NONE);
		label_15.setText("开始行号：");
		label_15.setBounds(264, 10, 76, 20);
		dropTarget.addDropListener(new DropTargetListener() {

			@Override
			public void dragEnter(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragLeave(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOperationChanged(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOver(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drop(DropTargetEvent arg0) {
				// TODO Auto-generated method stub
				String[] files = (String[]) arg0.data;
				for (String path : files) {
					boolean result = false;
					if (check_autosetting.getSelection()) {
						int title = Integer.parseInt(text_表头行号.getText());
						int start = Integer.parseInt(text_开始行号.getText());
						int end = Integer.parseInt(text_结束行号.getText());
						result = Logic.recordFromFile(new File(path), title, start, end);
					} else {
						result = Logic.recordFromFile(new File(path));
					}
					if (result) {
						text_file.setText("添加成功:" + files[0]);
						GUI.showMsgDialog(shell, "添加成功:" + files[0]);
					} else {
						text_file.setText("添加失败:" + files[0]);
						GUI.showErrDialog(shell, "添加失败,请检查运行日志:" + Log.getFileLocation());
					}
				}
			}

			@Override
			public void dropAccept(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		refreshData();
	}

	// public static void addRecord1(String fileName) throws Exception {
	// // System.out.println("addRecord1");
	// DBManager db = DBManager.getInstance();
	// ExcelAPI excel = new ExcelAPI(fileName);
	//
	// List<HashMap<String, Object>> list_emp = db.query(DBManager.EMP,
	// Util.SetOf(DBManager.EMP_ID,DBManager.EMP_NAME),null);
	// HashSet emp_set = new HashSet<>();
	// list_emp.stream().forEach(emp->{emp_set.add(emp.get(DBManager.EMP_NAME));});
	//
	// for (String sheetName : excel.getSheetNames()) {
	// if (!emp_set.contains(sheetName))
	// continue;
	// Sheet sheet = excel.getSheet(sheetName);
	// //验证名字
	// String emp_name = (String) sheet.read(1, 2);
	// if (!emp_set.contains(emp_name))
	// continue;
	// //添加数据
	// HashMap<String, Object> kv = new HashMap<>();
	// //emp
	// int id = DBManager.findIdByName(DBManager.EMP, emp_name);
	// kv.put(DBManager.WORKING_EMPID, id);
	// //date 2018!!!
	// String dateString = (String) sheet.read(2, 2);
	// Date date = new Date(2018, Integer.parseInt(dateString.substring(0, 2)),
	// Integer.parseInt(dateString.substring(2, 4)));
	// kv.put(DBManager.WORKING_WORKINGDATE, date);
	// //date
	// String dateString = (String) sheet.read(2, 2);
	// Date date = new Date(2018, Integer.parseInt(dateString.substring(0, 2)),
	// Integer.parseInt(dateString.substring(2, 4)));
	// kv.put(DBManager.WORKING_WORKINGDATE, date);
	//
	//
	//
	// for (int i = 0; i < 6; i++) {
	// String text;
	// Date date = excel.read(base_row + 1, base_column + 1);
	// kv.put(DBManager.WORKING_WORKINGDATE, );
	// text = excel.read(base_row + 3 + i, base_column).trim();
	// kv.put("款项类型", text);
	// if (text.equals("刷货差额")) {
	// try {
	// text = dFormat.format(excel.readNumber(base_row + 3 + i, base_column + 1));
	// } catch (ClassCastException e) {
	// text = "0";
	// }
	//
	// if (text.startsWith("-")) {
	// kv.put("应收增加", "0");
	// kv.put("应收减少", text.substring(1));
	// } else {
	// kv.put("应收增加", text);
	// kv.put("应收减少", "0");
	// }
	// } else {
	// try {
	// text = dFormat.format(excel.readNumber(base_row + 3 + i, base_column + 1));
	// } catch (ClassCastException e) {
	// text = "0";
	// }
	// kv.put("应收增加", text.equals("") ? "0" : text);
	// try {
	// text = dFormat.format(excel.readNumber(base_row + 3 + i, base_column + 2));
	// } catch (ClassCastException e) {
	// text = "0";
	// }
	// kv.put("应收减少", text.equals("") ? "0" : text);
	// }
	// db.put(Config.TABLE_1, kv);
	// }
	// }
	// excel.close();
	// }

	public void addEMP() {
		try {
			String name = text_emp_name.getText().trim();
			if (name.equals(""))
				throw new Exception("名字不能为空");
			ASDataSource.insert(DBManager.EMP, Util.PairOf(DBManager.EMP_NAME, name));
			GUI.showMsgDialog(shell, "添加成功");
			refreshData();
		} catch (Exception e) {
			GUI.showErrDialog(shell, e.toString());
			e.printStackTrace();
		}
	}

	public void addCMP() {
		try {
			String name = text_cmp_name.getText().trim();
			if (name.equals(""))
				throw new Exception("名字不能为空");
			ASDataSource.insert(DBManager.CMP, Util.PairOf(DBManager.CMP_NAME, name));
			GUI.showMsgDialog(shell, "添加成功");
			refreshData();
		} catch (Exception e) {
			GUI.showErrDialog(shell, e.toString());
			e.printStackTrace();
		}
	}

	public void addDFS() {
		try {
			String name = text_dfs_name.getText().trim();
			if (name.equals(""))
				throw new Exception("名字不能为空");
			ASDataSource.insert(DBManager.DFS, Util.PairOf(DBManager.DFS_NAME, name));
			GUI.showMsgDialog(shell, "添加成功");
			refreshData();
		} catch (Exception e) {
			GUI.showErrDialog(shell, e.toString());
			e.printStackTrace();
		}
	}

	public void addRecord() {
		try {
			DBManager db = DBManager.getInstance();
			HashMap<String, Object> working = new HashMap<>();
			int id = 0;
			id = (int) ((HashMap) db.query(DBManager.EMP, Util.SetOf(DBManager.EMP_ID),
					Util.PairOf(DBManager.EMP_NAME, combo_EMP.getText())).get(0)).get(DBManager.EMP_ID);
			working.put(DBManager.WORKING_EMPID, id);
			id = (int) ((HashMap) db.query(DBManager.CMP, Util.SetOf(DBManager.CMP_ID),
					Util.PairOf(DBManager.CMP_NAME, combo_CMP.getText())).get(0)).get(DBManager.CMP_ID);
			working.put(DBManager.WORKING_CMPID, id);
			id = (int) ((HashMap) db.query(DBManager.DFS, Util.SetOf(DBManager.DFS_ID),
					Util.PairOf(DBManager.DFS_NAME, combo_DFS.getText())).get(0)).get(DBManager.DFS_ID);
			working.put(DBManager.WORKING_DFSID, id);
			working.put(DBManager.WORKING_WORKINGDATE,
					new java.sql.Date(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay()));
			working.put(DBManager.WORKING_刷货开工费现金, Double.valueOf(text_刷货开工费现金.getText()));
			working.put(DBManager.WORKING_刷货开工费卡, Double.valueOf(text_刷货开工费卡.getText()));
			working.put(DBManager.WORKING_刷货使用现金, Double.valueOf(text_刷货使用现金.getText()));
			working.put(DBManager.WORKING_刷货使用卡, Double.valueOf(text_刷货使用卡.getText()));
			working.put(DBManager.WORKING_刷货入库金额, Double.valueOf(text_刷货入库金额.getText()));
			working.put(DBManager.WORKING_刷货费用, Double.valueOf(text_刷货费用.getText()));
			working.put(DBManager.WORKING_刷货损失, Double.valueOf(text_刷货损失.getText()));
			db.insert(DBManager.WORKING, working);
			db.commit();
			GUI.showMsgDialog(shell, "添加成功");

		} catch (Exception e) {
			GUI.showErrDialog(shell, e.toString());
			e.printStackTrace();
		}
	}

	public void refreshData() {
		try {
			combo_CMP.removeAll();
			ASDataSource.get(DBManager.CMP).forEach(e -> {
				combo_CMP.add(e.get(DBManager.CMP_NAME));
			});
			combo_EMP.removeAll();
			ASDataSource.get(DBManager.EMP).forEach(e -> {
				combo_EMP.add(e.get(DBManager.EMP_NAME));
			});
			combo_DFS.removeAll();
			ASDataSource.get(DBManager.DFS).forEach(e -> {
				combo_DFS.add(e.get(DBManager.CMP_NAME));
			});
			if (combo_CMP.getItemCount() > 0)
				combo_CMP.select(0);
			if (combo_EMP.getItemCount() > 0)
				combo_EMP.select(0);
			if (combo_DFS.getItemCount() > 0)
				combo_DFS.select(0);

		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
}
