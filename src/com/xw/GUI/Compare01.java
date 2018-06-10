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

public class Compare01 extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text text;
	private Text text_1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Compare01(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
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
		shell.setSize(234, 160);
		shell.setText(getText());

		Combo combo_tableList = new Combo(shell, SWT.NONE);
		combo_tableList.setItems(new String[] {});
		combo_tableList.setBounds(10, 10, 98, 28);
		combo_tableList.select(0);
		refreshCombo(combo_tableList);

		Combo combo_tableList2 = new Combo(shell, SWT.NONE);
		combo_tableList2.setItems(new String[] {});
		combo_tableList2.setBounds(114, 10, 98, 28);
		combo_tableList2.select(0);
		refreshCombo(combo_tableList2);
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setBounds(10, 76, 98, 30);
		btnOk.setText("ok");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(10, 44, 98, 26);
		text.setText("支出");
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(114, 44, 98, 26);
		text_1.setText("합계금액");
		
		btnOk.addSelectionListener(GUI.OnClick(e->{
			String table1= combo_tableList.getText();
			String table2 =combo_tableList2.getText();
			boolean result = Logic.compare01(table1, table2, text.getText(),text_1.getText());
			if(result) {
				GUI.showMsgDialog(shell, "成功,请查看表["+table1+"_"+table2+"]");
			}else {
				GUI.showErrDialog(shell, "失败,请检查运行日志:"+Log.getFileLocation());
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
