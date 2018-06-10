package com.xw.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	Workbook m_Workbook;
	File m_File;

	public static void main(String[] arg) throws IOException, ExcelException {
		Excel excel = new Excel(new File("C:\\Users\\acer-pc\\Do" + "cuments\\WeChat Files\\wxid_qyi4s5v"
				+ "kakv222\\Files\\对账2月3月(1)\\result.xls"));
		System.out.println(excel.getSheets().get(1).getColCount(1));
		System.out.println(excel.getSheets().get(1).getName());
	}

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

		public String getName() {
			return m_Sheet.getSheetName();
		}

		public void write(int row, int col, Object value) throws ExcelException {
			write(new int[] { row - 1, col - 1 }, value);
		}

		public void write(int[] location, Object value) throws ExcelException {
			Row _row = getRow(location[0]);
			Cell cell = _row.createCell(location[1]);

			if (value instanceof String) {
				cell.setCellValue((String) value);
			} else if (value instanceof Date) {
				cell.setCellValue((Date) value);
	            CellStyle cellStyle = m_Workbook.createCellStyle();
	            DataFormat format= m_Workbook.createDataFormat();
	            cellStyle.setDataFormat(format.getFormat("yyyy年m月d日"));
	            cell.setCellStyle(cellStyle);
	            
			} else if (value instanceof Boolean) {
				cell.setCellValue((Boolean) value);
			} else if (value instanceof Double) {
				cell.setCellValue((Double) value);
			} else if (value instanceof Long) {
				cell.setCellValue((Long) value);
			} else if (value instanceof Float) {
				cell.setCellValue((Float) value + 0.0);
			} else if (value instanceof Integer) {
				cell.setCellValue((Integer) value + 0.0);
			} else if (value == null) {
				;
			} else
				throw new ExcelException("Cant write with " + value.toString());
		}

		public void writeFormat(int row, int col, String text) throws ExcelException {
			writeFormat(new int[] { row - 1, col - 1 }, text);
		}

		public void writeFormat(int[] location, String text) throws ExcelException {
			int row = location[0];
			int col = location[1];
			String[] row_list = text.split(NEW_LINE);
			for (String each_row : row_list) {
				String[] cell_list = each_row.split("\\" + DIV);
				int col_add = 0;
				Row _row = getRow(row);
				for (String each_text : cell_list) {
					_row.createCell(col + col_add).setCellValue(each_text);
					col_add++;
				}
				row++;
			}
		}

		public Object read(int row, int col) {
			return read(new int[] { row - 1, col - 1 });
		}

		public double readDouble(int row, int col, double def) {

			Object something = read(new int[] { row - 1, col - 1 });
			if (something instanceof Double)
				return (double) something;
			else if (something instanceof String) {
				String text = (String) something;
				// (-)?[0-9]+(\.[0-9]+)?
				if (text.matches("(-)?[0-9]+(\\.[0-9]+)?"))
					return Double.parseDouble(text);
			}
			return def;
		}

		public Date readDate(int row, int col, Date def) {

			Object something = read(new int[] { row - 1, col - 1 });
			if (something instanceof Date)
				return (Date) something;
			else
				return def;
		}

		public String readString(int row, int col, String def) {

			Object something = read(new int[] { row - 1, col - 1 });
			if (something instanceof String)
				return (String) something;
			else
				return def;
		}

		public Object read(int[] location) {
			if (m_Sheet.getLastRowNum() < location[0])
				return null;
			Row _row = getRow(location[0]);
			if (_row.getLastCellNum() < location[1])
				return null;
			Cell cell = _row.getCell(location[1]);
			if (cell == null)
				return null;

			switch (cell.getCellTypeEnum()) {
			case FORMULA:
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell))
					return cell.getDateCellValue();
				else
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

		public int getRowCount() {
			return m_Sheet.getLastRowNum() + 1;
		}

		public int getColCount(int row) {
			Row _row = getRow(row - 1);
			int rowCount = _row.getLastCellNum();
			return rowCount;
		}
	}

	public Excel(File file) throws IOException, ExcelException {

		m_File = file;
		String filePath = file.getAbsolutePath();
		if (!m_File.exists())
			throw new ExcelException("File existed");
		if (filePath.endsWith("xls"))
			m_Workbook = new HSSFWorkbook(new FileInputStream(m_File));
		else if (filePath.endsWith("xlsx"))
			m_Workbook = new XSSFWorkbook(new FileInputStream(m_File));
		else
			throw new ExcelException("InValid File " + filePath);

	}

	public static Excel createExcel(String path, boolean overWrite) throws IOException, ExcelException {
		File file = new File(path);
		if (file.exists() && !overWrite)
			throw new ExcelException("File existed");
		if (file.exists())
			file.delete();
		if (!file.createNewFile())
			return null;

		Workbook workbook;
		String filePath = file.getAbsolutePath();
		if (filePath.endsWith("xls"))
			workbook = new HSSFWorkbook();
		else
			workbook = new XSSFWorkbook();

		FileOutputStream out = new FileOutputStream(file);
		workbook.write(out);
		out.close();
		workbook.close();

		return new Excel(file);
	}

	public Sheet createSheet(String name) {
		return new Sheet(m_Workbook.createSheet(name));
	}

	public Sheet getSheet(String name) {
		return new Sheet(m_Workbook.getSheet(name));
	}

	public List<Sheet> getSheets() {
		ArrayList<Sheet> list = new ArrayList<>();
		for (int i = 0; i < m_Workbook.getNumberOfSheets(); i++)
			list.add(new Sheet(m_Workbook.getSheetAt(i)));
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

}
