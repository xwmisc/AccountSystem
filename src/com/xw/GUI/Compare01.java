package com.xw.GUI;

import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.xw.Log;
import com.xw.Logic;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;

public class Compare01 extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text c1_t1key;
	private Text c1_t2key;
	private Text c2_t1date;
	private Text c2_t1key;
	private Text c2_t2date;
	private Text c2_t2key;
	private Combo table2;
	private Combo table1;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public Compare01(Shell parent, int style) {
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
		shell.setSize(372, 379);
		shell.setText(getText());

		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setBounds(10, 96, 338, 70);

		c1_t1key = new Text(composite, SWT.BORDER);
		c1_t1key.setBounds(10, 32, 98, 26);
		c1_t1key.setText("支出");

		c1_t2key = new Text(composite, SWT.BORDER);
		c1_t2key.setBounds(114, 32, 98, 26);
		c1_t2key.setText("합계금액");

		Button c1_start = new Button(composite, SWT.NONE);
		c1_start.setBounds(230, 30, 98, 30);
		c1_start.setText("数值比对");
		c1_start.addSelectionListener(GUI.OnClick(e -> {
			String t1 = table1.getText();
			String t2 = table2.getText();
			boolean result = Logic.compare01(t1, t2, c1_t1key.getText(), c1_t2key.getText());
			if (result) {
				GUI.showMsgDialog(shell, "成功,请查看表[" + t1 + "_" + t2 + "]");
			} else {
				GUI.showErrDialog(shell, "失败,请检查运行日志:" + Log.getFileLocation());
			}
		}));

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setText("表1关键词：");
		label_2.setBounds(10, 8, 98, 20);

		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setText("表2关键词：");
		label_3.setBounds(114, 8, 98, 20);

		Composite composite_1 = new Composite(shell, SWT.BORDER);
		composite_1.setBounds(10, 10, 338, 80);

		Button refresh_table = new Button(composite_1, SWT.NONE);
		refresh_table.setBounds(229, 34, 98, 30);
		refresh_table.setText("刷新表");
		refresh_table.addSelectionListener(GUI.OnClick(e -> {
			refreshCombo(table1);
			refreshCombo(table2);
		}));

		table2 = new Combo(composite_1, SWT.NONE);
		table2.setBounds(114, 36, 98, 28);
		table2.setItems(new String[] {});
		table2.select(0);
		refreshCombo(table2);

		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setBounds(114, 10, 76, 20);
		label_1.setText("表2：");

		table1 = new Combo(composite_1, SWT.NONE);
		table1.setBounds(10, 36, 98, 28);
		table1.setItems(new String[] {});
		table1.select(0);
		refreshCombo(table1);

		Label label = new Label(composite_1, SWT.NONE);
		label.setBounds(10, 10, 76, 20);
		label.setText("表1：");

		Composite composite_2 = new Composite(shell, SWT.BORDER);
		composite_2.setBounds(10, 172, 338, 135);

		c2_t1date = new Text(composite_2, SWT.BORDER);
		c2_t1date.setText("日期");
		c2_t1date.setBounds(10, 37, 98, 26);

		c2_t1key = new Text(composite_2, SWT.BORDER);
		c2_t1key.setText("합계금액");
		c2_t1key.setBounds(10, 95, 98, 26);

		Button c2_start = new Button(composite_2, SWT.NONE);
		c2_start.setText("仅求和表1");
		c2_start.setBounds(226, 55, 98, 30);
		c2_start.addSelectionListener(GUI.OnClick(e -> {
			String t1 = table1.getText();
			String t2 = table2.getText();
			String t1date = c2_t1date.getText();
			String t2date = c2_t2date.getText();
			String t1key = c2_t1key.getText();
			String t2key = c2_t2key.getText();
			boolean result = Logic.compare02(t1, t2, t1date, t1key, t2date, t2key,true);
			if (result) {
				GUI.showMsgDialog(shell, "成功,请查看表[" + t1 + "_" + t2 + "]");
			} else {
				GUI.showErrDialog(shell, "失败,请检查运行日志:" + Log.getFileLocation());
			}
		}));

		Label label_4 = new Label(composite_2, SWT.NONE);
		label_4.setText("表1求和列名：");
		label_4.setBounds(10, 69, 98, 20);

		Label label_5 = new Label(composite_2, SWT.NONE);
		label_5.setText("表1日期列名：");
		label_5.setBounds(10, 11, 98, 20);

		Label label_6 = new Label(composite_2, SWT.NONE);
		label_6.setText("表2日期列名：");
		label_6.setBounds(114, 11, 98, 20);

		c2_t2date = new Text(composite_2, SWT.BORDER);
		c2_t2date.setText("日期");
		c2_t2date.setBounds(114, 37, 98, 26);

		c2_t2key = new Text(composite_2, SWT.BORDER);
		c2_t2key.setText("当日总刷货返点");
		c2_t2key.setBounds(114, 95, 98, 26);

		Label label_7 = new Label(composite_2, SWT.NONE);
		label_7.setText("表2数值列名：");
		label_7.setBounds(114, 69, 98, 20);

		Label label_8 = new Label(composite_2, SWT.WRAP);
		label_8.setText("日期统计");
		label_8.setBounds(242, 11, 65, 26);
		
		Button c2_start2 = new Button(composite_2, SWT.NONE);
		c2_start2.setText("分别求和");
		c2_start2.setBounds(226, 91, 98, 30);
		c2_start2.addSelectionListener(GUI.OnClick(e -> {
			String t1 = table1.getText();
			String t2 = table2.getText();
			String t1date = c2_t1date.getText();
			String t2date = c2_t2date.getText();
			String t1key = c2_t1key.getText();
			String t2key = c2_t2key.getText();
			boolean result = Logic.compare02(t1, t2, t1date, t1key, t2date, t2key,false);
			if (result) {
				GUI.showMsgDialog(shell, "成功,请查看表[" + t1 + "_" + t2 + "]");
			} else {
				GUI.showErrDialog(shell, "失败,请检查运行日志:" + Log.getFileLocation());
			}
		}));

	}

	private void refreshCombo(Combo combo_tableList) {

		try {
			String[] tables;
			tables = ASDataSource.getTables();
			combo_tableList.removeAll();
			for (String table : tables)
				combo_tableList.add(table);
			if (combo_tableList.getItemCount() > 0)
				combo_tableList.select(0);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
}
