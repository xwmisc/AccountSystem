package com.xw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelAPI {
	Workbook m_Workbook;
	File m_File;

	public static void main(String[] arg) {
		try {
			ExcelAPI excel = new ExcelAPI("E:\\360Download\\record.xls");
			System.out.println("=====SheetList");
			for (String i : excel.getSheetNames())
				System.out.println(i);
			System.out.println("=====openSheet");
			Sheet sheet = excel.getSheet(excel.getSheetNames().get(0));
			System.out.println("=====Read");
			System.out.println("3x1|" + sheet.read(3, 1));
			System.out.println("=====ReadDate");
			try {
				// System.out.println("0x0|" + excel.readDate(0, 0));
			} catch (ClassCastException e) {
				// System.out.println("=====ReadDateFix"
				// + new Date((long) ((excel.readNumber(0, 0) - 25568 - 1) * 1000l * 60 * 60 *
				// 24)).toGMTString());
				// Matcher m = Pattern.compile("([0-9]+)月([0-9]+)日").matcher(excel.read(0, 0));
				// System.out.println("=====ReadDateFix"+excel.read(0, 0));
				// System.out.println(m.group(1)+"月"+m.group(2)+"日");

			}
			System.out.println("=====ReadNumber");

				System.out.println("3x1|" + sheet.read(new int[] { 3, 1 }));

			System.out.println("=====Write");
			sheet.writeFormat(1, 1,
					"ben|0226|刷货开工费|-10000000|0|刷货差额|刷货退回|0|-4577150|刷货返点|刷货费用|刷货入库|0|-965360|0303|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0205|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0227|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0202|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0224|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0301|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0203|刷货开工费|-19900000|0|刷货差额|刷货退回|0|-887250|刷货返点|刷货费用|刷货入库|0|-3555744|0225|刷货开工费|-50000000|0|刷货差额|刷货退回|0|-2710600|刷货返点|刷货费用|");
			// System.out.println("1x1|" + excel.read(1, 1));
			// System.out.println("1x3|" + excel.read(1, 3));
			// excel.save();
			// System.out.println("1x4|" + excel.readNumber(1, 4));
			System.out.println("=====save");
			excel.save();
			System.out.println("=====close");
			excel.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @author acer-pc
	 *
	 */
	public class Sheet {
		org.apache.poi.ss.usermodel.Sheet m_Sheet = m_Workbook.getSheetAt(0);
		public static final String DIV = "|";
		public static final String NEW_LINE = "newline";

		public Sheet(org.apache.poi.ss.usermodel.Sheet m_Sheet) {
			this.m_Sheet = m_Sheet;
		}

		private Row getRow(int row) {
			Row _row = m_Sheet.getRow(row);
			if (_row == null)
				_row = m_Sheet.createRow(row);
			return _row;
		}

		public void write(int row, int col, String text) throws ExcelException {
			Row _row = getRow(row);
			Cell cell = _row.createCell(col);
			cell.setCellValue(text);
		}

		public void writeFormat(int base_row, int base_col, String text) throws ExcelException {
			// System.out.println("writeFormat " + text);
			int row = 0;
			int col = 0;
			String[] row_list = text.split(NEW_LINE);
			for (String each_row : row_list) {
				String[] cell_list = each_row.split("\\" + DIV);
				col = 0;
				Row _row = getRow(row);
				for (String each_text : cell_list) {
					_row.createCell(base_col + col).setCellValue(each_text);
					col++;
				}
				row++;
			}
		}

		public Object read(int row, int col) {
			return read(new int[] { row - 1, col - 1 });
		}

		public Object read(int[] location) {
			if (m_Sheet.getLastRowNum() < location[0])
				return null;
			Row _row = getRow(location[0]);
			if (_row.getLastCellNum() < location[1])
				return null;
			Cell cell = _row.getCell(location[1]);

			switch (cell.getCellTypeEnum()) {
			case FORMULA:
			case NUMERIC:
				return cell.getNumericCellValue();
			case STRING:
				return cell.getStringCellValue();
			case BOOLEAN:
				return cell.getBooleanCellValue();
			case BLANK:
			case _NONE:
			case ERROR:
			default:
				return null;
			}
		}

		public Date toDate(double num) {
			Date date = HSSFDateUtil.getJavaDate(num);
			return date;
		}

	}

	public ExcelAPI(String filePath, String[] newSheetName) throws IOException, ExcelException {

		if (null == filePath)
			throw new IllegalArgumentException("filename is null");
		m_File = new File(filePath);

		if (m_File.exists()) {
			if (filePath.endsWith("xls"))
				m_Workbook = new HSSFWorkbook(new FileInputStream(m_File));
			else if (filePath.endsWith("xlsx"))
				m_Workbook = new XSSFWorkbook(new FileInputStream(m_File));
			else
				throw new ExcelException("InValid File " + filePath);
		} else {
			m_Workbook = new XSSFWorkbook();
			for (String name : newSheetName)
				m_Workbook.createSheet(name);
			save();
		}

	}

	public ExcelAPI(String filePath) throws IOException, ExcelException {
		this(filePath, new String[] { "sheet1" });
	}

	public Sheet getSheet(String name) {
		return new Sheet(m_Workbook.getSheet(name));
	}

	public List<String> getSheetNames() {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < m_Workbook.getNumberOfSheets(); i++)
			list.add(m_Workbook.getSheetName(i));
		return list;
	}

	public void save() throws IOException {
		FileOutputStream out = new FileOutputStream(m_File);
		m_Workbook.write(out);
		out.close();
	}

	public void close() throws IOException {
		m_Workbook.close();
	}

	public void closeWithSave() throws IOException {
		save();
		m_Workbook.close();
	}

	public class ExcelException extends Exception {

		public ExcelException(String string) {
			super(string);
		}

	}
}
