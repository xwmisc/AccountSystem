package com.xw.GUI;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SegmentEvent;
import org.eclipse.swt.events.SegmentListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.xw.Config;
import com.xw.Log;
import com.xw.Logic;
import com.xw.LogicV1;
import com.xw.Util;
import com.xw.excel.ExcelException;

import CustomDialog.CheckboxBean;
import CustomDialog.DialogFactory;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

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
	private Button checkbox_seeSysTable;
	private Button button_2;
	private Table table_match;
	private DateTime dateTime_match_from;
	private DateTime dateTime_match_to;
	private Text text_match_from;
	private Text text_match_to;
	private Combo combo_match;
	private Label label_type;
	private Button btn_add_match;

	private ArrayList<matchBean> matches;
	private Button btn_del_match;

	class matchBean {
		String column;
		String type;
		Object from;
		Object to;

		public matchBean(String column, String type, Object from, Object to) {
			super();
			this.column = column;
			this.type = type;
			this.from = from;
			this.to = to;
		}

	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PropertyConfigurator.configure("log4j.properties");
			GUI window = new GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			Log.logger().error(e.toString(), e);
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
		matches = new ArrayList<>();

		shell = new Shell();
		shell.setSize(900, 553);
		shell.setText("SWT Application");

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		m_Table = new ASTable(table);
		table.setBounds(279, 10, 593, 486);
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
		combo_tableList.setBounds(10, 10, 188, 28);
		combo_tableList.select(0);

		btn_query = new Button(shell, SWT.NONE);
		btn_query.setBounds(10, 81, 124, 30);
		btn_query.setText("查询表");
		btn_query.addSelectionListener(OnClick(e -> {
			// 检索
			String tableName = combo_tableList.getText();
			m_Table.setCurrentTableName(tableName);
			m_Table.reloadData();
			// 匹配
			fillWithColumnName(combo_match, m_Table.getCurrentTableName());
		}));

		btn_add = new Button(shell, SWT.NONE);
		// btn_add.setEnabled(false);
		btn_add.setText("添加");
		btn_add.setBounds(10, 117, 124, 30);
		btn_add.addSelectionListener(OnClick(e -> {
			new Add(shell, shell.getStyle()).open();
			// new AdjustImage(shell, shell.getStyle()).open();
		}));

		btn_delete = new Button(shell, SWT.NONE);
		// btn_delete.setEnabled(false);
		btn_delete.setText("删除行");
		btn_delete.setBounds(149, 117, 124, 30);
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
		btn_refreshTable.setBounds(10, 45, 263, 30);
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
		brn_dTable.setBounds(149, 81, 124, 30);
		brn_dTable.addSelectionListener(OnClick(e -> {
			try {
				String tableName = combo_tableList.getText();
				ASDataSource.deleteTable(tableName);
				refreshCombo();
			} catch (SQLException e1) {
				e1.printStackTrace();
				Log.logger().error(e1.toString(), e1);
			}
		}));

		button = new Button(shell, SWT.NONE);
		button.setText("比较01");
		button.setBounds(149, 153, 124, 30);
		button.addSelectionListener(OnClick(e -> {
			new Compare01(shell, shell.getStyle()).open();
		}));

		button_1 = new Button(shell, SWT.NONE);
		button_1.setText("导出XSLX");
		button_1.setBounds(10, 153, 124, 30);
		button_1.addSelectionListener(OnClick(e -> {
			boolean result = Logic.exportXLSX(combo_tableList.getText());
			if (result) {
				DialogFactory.showMsg(shell, "导出成功,请在运行目录下查看文件");
			} else {
				DialogFactory.showErr(shell, "导出失败,请检查运行日志:" + Log.getFileLocation());
			}
		}));

		checkbox_seeSysTable = new Button(shell, SWT.CHECK);
		checkbox_seeSysTable.setBounds(204, 9, 69, 28);
		checkbox_seeSysTable.setText("系统表");

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setBounds(10, 189, 263, 307);

		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("记录");

		text_info = new Text(tabFolder, SWT.BORDER | SWT.MULTI);
		tabItem_1.setControl(text_info);

		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("筛选");

		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_1);

		label_type = new Label(composite_1, SWT.NONE);
		label_type.setBounds(135, 144, 120, 20);
		label_type.setText("类型:");

		combo_match = new Combo(composite_1, SWT.NONE);
		combo_match.setBounds(0, 110, 120, 28);
		combo_match.addSegmentListener(new SegmentListener() {
			@Override
			public void getSegments(SegmentEvent arg0) {
				try {
					label_type.setText("类型:");
					String column_name = combo_match.getText();
					String type = ASDataSource.getColumnType(m_Table.getCurrentTableName(), column_name);
					label_type.setText("类型:" + type);
				} catch (IOException | SQLException e) {
				}
			}
		});

		table_match = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table_match.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 8, SWT.BOLD));
		table_match.setBounds(0, 0, 255, 107);
		table_match.setHeaderVisible(true);
		table_match.setLinesVisible(true);
		TableColumn tc = new TableColumn(table_match, SWT.NONE);
		tc.setText("列名");
		tc.setWidth(76);
		tc = new TableColumn(table_match, SWT.NONE);
		tc.setText("规则");
		tc.setWidth(200);

		dateTime_match_from = new DateTime(composite_1, SWT.BORDER);
		dateTime_match_from.setBounds(0, 202, 110, 28);

		dateTime_match_to = new DateTime(composite_1, SWT.BORDER);
		dateTime_match_to.setBounds(145, 202, 110, 28);

		text_match_from = new Text(composite_1, SWT.BORDER);
		text_match_from.setBounds(0, 170, 110, 26);

		text_match_to = new Text(composite_1, SWT.BORDER);
		text_match_to.setBounds(145, 170, 110, 26);

		Button radio_as = new Button(composite_1, SWT.RADIO);
		radio_as.setBounds(10, 144, 60, 20);
		radio_as.setText("等于");
		radio_as.addSelectionListener(OnClick(c -> {
			text_match_to.setEnabled(false);
			dateTime_match_to.setEnabled(false);
		}));

		Button radio_between = new Button(composite_1, SWT.RADIO);
		radio_between.setSelection(true);
		radio_between.setText("介于");
		radio_between.setBounds(71, 144, 60, 20);
		radio_between.addSelectionListener(OnClick(c -> {
			text_match_to.setEnabled(true);
			dateTime_match_to.setEnabled(true);
		}));

		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setBounds(118, 202, 22, 20);
		label_1.setText("至");

		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setText("至");
		label_2.setBounds(118, 173, 22, 20);

		Button btn_match = new Button(composite_1, SWT.NONE);
		btn_match.setBounds(10, 236, 235, 30);
		btn_match.setText("开始筛选");
		btn_match.addSelectionListener(OnClick(c -> {
			try {
				m_Table.applyMatch();
			} catch (IOException | SQLException e1) {
				e1.printStackTrace();
				Log.logger().error(e1.toString(), e1);

			}
		}));

		btn_add_match = new Button(composite_1, SWT.NONE);
		btn_add_match.setText("添加规则");
		btn_add_match.setBounds(126, 108, 72, 30);
		btn_add_match.addSelectionListener(OnClick(c -> {
			try {
				refreshMatch();
				String column_name = combo_match.getText();
				String type = ASDataSource.getColumnType(m_Table.getCurrentTableName(), column_name).toLowerCase();
				boolean isBetween = radio_between.getSelection();
				Object from = null;
				Object to = null;
				if (type.contains("date")) {
					from = new Date(dateTime_match_from.getYear(), dateTime_match_from.getMonth(),
							dateTime_match_from.getDay());
					if (isBetween) {
						to = new Date(dateTime_match_to.getYear(), dateTime_match_to.getMonth(),
								dateTime_match_to.getDay());
					}
				} else if (type.contains("real") || type.contains("int")) {
					from = Double.parseDouble(text_match_from.getText());
					if (isBetween) {
						to = Double.parseDouble(text_match_to.getText());
					}
				} else {
					from = text_match_from.getText();
				}
				matches.add(new matchBean(column_name, type, from, to));
				refreshMatch();
			} catch (IOException | SQLException e) {
				DialogFactory.showErr(shell, e.toString());
			}
		}));

		btn_del_match = new Button(composite_1, SWT.NONE);
		btn_del_match.setText("删除");
		btn_del_match.setBounds(202, 108, 48, 30);
		btn_del_match.addSelectionListener(OnClick(c -> {
			for (TableItem item : table_match.getSelection()) {
				matches.remove(item.getData());
				table_match.remove(table_match.indexOf(item));
				item.dispose();
			}
		}));

		try {
			LogicV1.setup();
			Class.forName(Config.class.getName());
//			Config.loadEMP();

		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
			Log.logger().error(e1.toString(), e1);
			DialogFactory.showErr(shell, "数据库或配置文件错误,请关闭程序再试(请确保本程序仅有一个进程)");

		}
		refreshCombo();

		// AdjustImage a = new AdjustImage(shell, shell.getStyle());
		// a.open();
	}

	public static void fillWithColumnName(Combo combo, String tableName) {
		try {
			if (combo == null)
				return;
			combo.removeAll();
			if (!ASDataSource.existTable(tableName))
				return;
			List<String> columns;
			columns = ASDataSource.getColumns(tableName);
			for (String column : columns)
				combo.add(column);
			if (columns.size() > 0)
				combo.select(0);
		} catch (IOException | SQLException e1) {

			e1.printStackTrace();
			Log.logger().error(e1.toString(), e1);
		}
	}

	private void refreshMatch() {
		table_match.removeAll();
		for (matchBean val : matches) {
			TableItem tableItem = new TableItem(table_match, SWT.NONE);
			tableItem.setText(0, val.column);
			tableItem.setText(1, "" + (val.to == null ? "等于" : "介于") + val.from.toString()
					+ (val.to == null ? "" : "至" + val.to.toString()));
			tableItem.setData(val);
		}
	}

	private void refreshCombo() {

		try {
			String[] tables;
			tables = ASDataSource.getSortTables();
			combo_tableList.removeAll();
			for (int index = 0; index < tables.length; index++) {
				String table = tables[index];
				if (!checkbox_seeSysTable.getSelection() && table.contains("系统_"))
					continue;
				if (table.startsWith("sqlite"))
					continue;
				combo_tableList.add(table);
			}
			if (combo_tableList.getItemCount() > 0)
				combo_tableList.select(0);
		} catch (SQLException e1) {
			e1.printStackTrace();
			Log.logger().error(e1.toString(), e1);
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

	class ASTable {
		Table m_Table;
		private String m_tableName;
		private HashMap<String, Boolean> invisibleBean;

		public ASTable(Table table) {
			m_Table = table;
			m_Table.addMouseListener(new MouseListener() {

				@Override
				public void mouseUp(MouseEvent e) {

				}

				@Override
				public void mouseDown(MouseEvent e) {

				}

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					// 弹出选择项
					ArrayList<CheckboxBean> beans = new ArrayList<>();
					for (final TableColumn column : m_Table.getColumns()) {
						beans.add(new CheckboxBean(column.getText(), column.getWidth() > 0, selected -> {
							if (selected) {
								column.setWidth(evalWidth(column.getText()));
								invisibleBean.put(column.getText(), true);
							} else {
								column.setWidth(0);
								invisibleBean.put(column.getText(), false);
							}
						}));
					}
					DialogFactory.showCustomCheckboxList(shell, beans);
				}
			});
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
				Log.logger().error(e1.toString(), e1);
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
				Log.logger().error(e1.toString(), e1);
			}
		}

		public void setCurrentTableName(String tableName) {
			if (m_tableName==null || !m_tableName.equals(tableName)) {
				m_tableName = tableName;
				invisibleBean = new HashMap<>();
			}
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
				if (invisibleBean.getOrDefault(each, true)) {
					column.setWidth(evalWidth(each));
				} else {
					column.setWidth(0);
				}
				Object type = "";
				try {
					type = ASDataSource.getColumnType(m_tableName, each);
				} catch (IOException | SQLException e) {
					e.printStackTrace();
					Log.logger().error(e.toString(), e);

				}
				column.setData(type);
				column.setText(each);
				column.addSelectionListener(new sortListener(column));
			});
		}

		private int evalWidth(String str) {
			return str.getBytes().length * 100 / 16 < 100 ? 100 : str.getBytes().length * 100 / 16;
		}

		public void addItem(List<HashMap<String, Object>> vals) {
			final SimpleDateFormat fmt = new SimpleDateFormat("MM月dd日 E HH:mm:ss:SS yyyy");

			for (HashMap<String, Object> val : vals) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setData(val);
				for (TableColumn column : m_Table.getColumns()) {
					String attr = column.getText();
					Object something = val.get(attr);
					// boolean isDate = attr.contains("时间") || attr.contains("日期");
					if (something instanceof Date) {
						Date date = (Date) something;
						tableItem.setText(m_Table.indexOf(column), fmt.format(date));
					} else if (something instanceof Number) {
						// if (isDate) {
						// String text = fmt.format(new Date(((Number) something).longValue()));
						// tableItem.setText(m_Table.indexOf(column), text);
						// } else {
						String text = new BigDecimal(((Number) something).doubleValue()).toString();
						tableItem.setText(m_Table.indexOf(column), text);
						// }
					} else {
						String text = Optional.ofNullable(something).orElse("").toString();
						tableItem.setText(m_Table.indexOf(column), text);
					}
				}
			}
		}

		public void applyMatch() throws IOException, SQLException {
			Log.logger().info("applyMatch");
			if (matches.size() == 0)
				return;
			for (matchBean match : matches) {

				// 存在列 且 类型相同
				boolean isExisted = false;
				int cIndex = 0;
				for (TableColumn column : m_Table.getColumns()) {
					Log.logger().info("text:" + column.getText() + " " + match.column);
					if (column.getText().equals(match.column)) {
						String type = ASDataSource.getColumnType(m_tableName, match.column);
						if (type.toLowerCase().contains(match.type.toLowerCase())) {
							Log.logger().info("type:" + type + " " + match.type);
							cIndex = m_Table.indexOf(column);
							isExisted = true;
							break;
						}
					}
				}
				if (!isExisted)
					continue;

				// 删除
				boolean isBetween = (match.to != null);
				for (TableItem item : m_Table.getItems()) {
					HashMap val = (HashMap) item.getData();
					boolean isMatch = true; // 默认不删除
					try {
						if (match.type.contains("date")) {
							Date date = (Date) val.get(match.column);
							Date date_match = (Date) match.from;
							if (!isBetween) {
								isMatch = (date_match.getMonth() == date.getMonth()
										&& date_match.getDate() == date.getDate());
								Log.logger().info("" + date_match.getMonth() + " " + date.getMonth());
								Log.logger().info("" + date_match.getDate() + " " + date.getDate());
							} else {
								Date date_match2 = (Date) match.to;
								date.setYear(date_match.getYear());
								isMatch = (date_match.getTime() <= date.getTime()
										&& date.getTime() <= date_match2.getTime());
								Log.logger().info(
										"" + date_match.getTime() + " " + date.getTime() + " " + date_match2.getTime());
							}
						} else if (match.type.contains("real") || match.type.contains("int")) {
							Object _num = val.get(match.column);
							Double num;
							Double num_match = (Double) match.from;
							if (_num instanceof Integer) {
								num = 0.0 + (Integer) _num;
							} else {
								num = (Double) val.get(match.column);
							}
							if (!isBetween) {
								isMatch = num == num_match;
							} else {
								Double num_match2 = (Double) match.to;
								isMatch = (num_match <= num && num <= num_match2);
							}
						} else {
							String text = (String) val.get(match.column);
							String text_match = (String) match.from;
							isMatch = text.equals(text_match);
						}
					} catch (ClassCastException | NullPointerException e) {
					}
					// 删除项
					if (!isMatch) {
						Log.logger().info("" + isMatch);
						int itemIndex = m_Table.indexOf(item);
						m_Table.remove(itemIndex);
						item.dispose();
					}
				} // 删除结束

			}

		}

		public void removeAllColumn() {
			for (TableColumn column : m_Table.getColumns())
				column.dispose();
		}

		public void removeAllItem() {
			TableItem[] tableItems = m_Table.getItems();
			for(int index = tableItems.length-1;index>=0;index--)
				tableItems[index].dispose();
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
				if (lastCol.equals(colName)) {
					isAscend = !lastIsAscend;
				}
				lastIsAscend = isAscend;
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
