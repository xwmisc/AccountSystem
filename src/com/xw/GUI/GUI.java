package com.xw.GUI;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Text;

import com.xw.DBManager;
import com.xw.Log;
import com.xw.Logic;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Canvas;

public class GUI {

	protected Shell shell;
	private Table table;
	private Button btn_add;
	private Button btn_delete;
	private Button btn_query;
	private Text text_info;
	private ASTable m_Table;
	private Combo combo_tableList;
	private Button btn_refreshTable;
	private Button brn_dTable;
	private Button button;
	private Button button_1;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI window = new GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(672, 508);
		shell.setText("SWT Application");

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		m_Table = new ASTable(table);
		table.setBounds(219, 10, 435, 441);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		shell.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Rectangle rect_table = table.getBounds();
				Rectangle rect_shell = shell.getBounds();
				rect_table.width = rect_shell.width - (rect_table.x + 26);
				rect_table.height = rect_shell.height - (rect_table.y + 56);
				table.setBounds(rect_table);
			}
		});

		combo_tableList = new Combo(shell, SWT.NONE);
		combo_tableList.setItems(new String[] {});
		combo_tableList.setBounds(10, 10, 98, 28);
		combo_tableList.select(0);

		text_info = new Text(shell, SWT.BORDER | SWT.MULTI);
		text_info.setBounds(10, 155, 202, 296);

		btn_query = new Button(shell, SWT.NONE);
		btn_query.setBounds(10, 47, 98, 30);
		btn_query.setText("查询表");
		btn_query.addSelectionListener(OnClick(e -> {
			// 检索
			String tableName = combo_tableList.getText();
			m_Table.setCurrentTableName(tableName);
			m_Table.reloadData();
		}));

		btn_add = new Button(shell, SWT.NONE);
		// btn_add.setEnabled(false);
		btn_add.setText("添加");
		btn_add.setBounds(10, 83, 98, 30);
		btn_add.addSelectionListener(OnClick(e -> {
			new Add(shell, shell.getStyle()).open();
			// new AdjustImage(shell, shell.getStyle()).open();
		}));

		btn_delete = new Button(shell, SWT.NONE);
		// btn_delete.setEnabled(false);
		btn_delete.setText("删除行");
		btn_delete.setBounds(114, 83, 98, 30);
		btn_delete.addSelectionListener(OnClick(e -> {
			// 删除
			m_Table.deleteSelectData();
		}));

		// 选中显示内容
		table.addSelectionListener(OnClick(e -> {
			TableItem[] items = table.getSelection();
			if (items.length > 0) {
				String info = "";
				for (int i = 0; i < table.getColumnCount(); i++)
					info += (info.equals("") ? "" : ",\n") + table.getColumn(i).getText() + "=" + items[0].getText(i);
				text_info.setText(info);
			}
		}));

		btn_refreshTable = new Button(shell, SWT.NONE);
		btn_refreshTable.setText("刷新");
		btn_refreshTable.setBounds(114, 10, 98, 30);
		btn_refreshTable.addSelectionListener(OnClick(e -> {
			refreshCombo();
		}));

		brn_dTable = new Button(shell, SWT.NONE);
		brn_dTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		brn_dTable.setText("删除表");
		brn_dTable.setBounds(114, 47, 98, 30);
		brn_dTable.addSelectionListener(OnClick(e -> {
			try {
				String tableName = combo_tableList.getText();
				ASDataSource.deleteTable(tableName);
				refreshCombo();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}));
		
		button = new Button(shell, SWT.NONE);
		button.setText("比较01");
		button.setBounds(115, 119, 98, 30);
		button.addSelectionListener(OnClick(e -> {
			new Compare01(shell, shell.getStyle()).open();
		}));
		
		button_1 = new Button(shell, SWT.NONE);
		button_1.setText("导出XSLX");
		button_1.setBounds(10, 119, 98, 30);
		button_1.addSelectionListener(OnClick(e -> {
			boolean result = Logic.exportXLSX(combo_tableList.getText());
			if(result) {
				GUI.showMsgDialog(shell, "导出成功,请在运行目录下查看文件");
			}else {
				GUI.showErrDialog(shell, "导出失败,请检查运行日志:"+Log.getFileLocation());
			}
		}));

		refreshCombo();
	}

	private void refreshCombo() {

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

	public void runOnUIThread(final Runnable run) {
		shell.getDisplay().syncExec(run);
	}

	public static SelectionListener OnClick(Consumer<SelectionEvent> c) {
		SelectionListener listener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				c.accept(arg0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		};
		return listener;
	}

	public static void showErrDialog(Shell shell, String err) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				int style = SWT.APPLICATION_MODAL | SWT.ERROR;
				MessageBox messageBox = new MessageBox(shell, style);
				messageBox.setText("Error Occurred!");
				messageBox.setMessage(err);
				messageBox.open();
			}
		});

	}

	public static void showMsgDialog(Shell shell, String message) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				int style = SWT.APPLICATION_MODAL | SWT.YES;
				MessageBox messageBox = new MessageBox(shell, style);
				messageBox.setText("Tip");
				messageBox.setMessage(message);
				messageBox.open();
			}
		});
	}

	class ASTable {
		Table m_Table;
		private String m_tableName;

		public ASTable(Table table) {
			m_Table = table;
			m_Table.addSelectionListener(new SelectionAdapter() {
				boolean sortType = true;

				public void widgetSelected(SelectionEvent e) {
					sortType = !sortType;
				}
			});
		}

		public void reloadData() {
			final List data;
			final List<String> columnNames;
			try {
				columnNames = ASDataSource.getColumns(m_tableName);
				data = ASDataSource.get(m_tableName);
				runOnUIThread(() -> {
					removeAllItem();
					removeAllColumn();
					addColumn(columnNames);
					addItem(data);
				});
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		public void deleteSelectData() {
			// 获得选中的id
			ArrayList<Integer> list_id = new ArrayList<>();
			for (TableItem item : table.getSelection()) {
				HashMap<String, Object> data = (HashMap<String, Object>) item.getData();
				list_id.add((Integer) data.get("id"));// id列
			}
			int[] arr_id = new int[list_id.size()];
			for (int i = 0; i < list_id.size(); i++)
				arr_id[i] = list_id.get(i);
			try {
				// 删除
				ASDataSource.delete(m_tableName, arr_id);
				// 更新
				reloadData();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		public void setCurrentTableName(String tableName) {
			m_tableName = tableName;
		}

		public String getCurrentTableName() {
			return m_tableName;
		}

		public void addColumn(List<String> cloumnsName) {
			cloumnsName.stream().filter(s -> {
				for (TableColumn col : m_Table.getColumns())
					if (col.getText().equals(s))
						return false;
				return true;
			}).forEach(each -> {
				final TableColumn column = new TableColumn(m_Table, SWT.NONE);
				column.setWidth(each.getBytes().length * 100 / 16 < 100 ? 100 : each.getBytes().length * 100 / 16);
				column.setText(each);
				column.addSelectionListener(new sortListener(column));
			});
		}

		public void addItem(List<HashMap<String, Object>> item_list) {
			final SimpleDateFormat fmt = new SimpleDateFormat("MM月dd日 E HH:mm:ss:SS yyyy");

			for (HashMap<String, Object> item : item_list) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setData(item);
				for (TableColumn column : m_Table.getColumns()) {
					String attr = column.getText();
					Object something = item.get(attr);
					boolean isDate = attr.contains("时间") || attr.contains("日期");
					if (something instanceof Date) {
						Date date = (Date) something;
						tableItem.setText(m_Table.indexOf(column), fmt.format(date));
					} else if (something instanceof Number) {
						if (isDate) {
							String text = fmt.format(new Date(((Number) something).longValue()));
							tableItem.setText(m_Table.indexOf(column), text);
						} else {
							String text = new BigDecimal(((Number) something).doubleValue()).toString();
							tableItem.setText(m_Table.indexOf(column), text);
						}
					} else {
						String text = Optional.ofNullable(something).orElse("").toString();
						tableItem.setText(m_Table.indexOf(column), text);
					}
				}
			}
		}

		public void removeAllColumn() {
			for (TableColumn column : m_Table.getColumns())
				column.dispose();
		}

		public void removeAllItem() {
			for (TableItem item : m_Table.getItems())
				item.dispose();
		}

		class sortListener implements SelectionListener {
			TableColumn column;
			String lastCol = "";
			boolean lastIsAscend = false;

			public sortListener(TableColumn column) {
				this.column = column;
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				System.out.println("!" + column.getText());
				String colName = column.getText();
				boolean isAscend = true; // 按照升序排序
				if(lastCol.equals(colName) ) {
					isAscend = !lastIsAscend;
				}
				lastIsAscend = isAscend ;
				lastCol = colName;

				Collator comparator = Collator.getInstance(Locale.getDefault());
				int columnIndex = table.indexOf(column);
				TableItem[] items = table.getItems();
				// 使用冒泡法进行排序
				for (int i = 1; i < items.length; i++) {
					String str2value = items[i].getText(columnIndex);
					if (str2value.equalsIgnoreCase("")) {
						// 当遇到表格中的空项目时，就停止往下检索排序项目
						break;
					}
					for (int j = 0; j < i; j++) {
						String str1value = items[j].getText(columnIndex);
						boolean isLessThan = comparator.compare(str2value, str1value) < 0;
						if ((isAscend && isLessThan) || (!isAscend && !isLessThan)) {
							String[] values = getTableItemText(table, items[i]);
							Object obj = items[i].getData();
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							item.setData(obj);
							items = table.getItems();
							break;
						}
					}
				}
				table.setSortColumn(column);
				table.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
				isAscend = !isAscend;
			}

			public String[] getTableItemText(Table table, TableItem item) {
				int count = table.getColumnCount();
				String[] strs = new String[count];
				for (int i = 0; i < count; i++) {
					strs[i] = item.getText(i);
				}
				return strs;
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		}

	}
}
