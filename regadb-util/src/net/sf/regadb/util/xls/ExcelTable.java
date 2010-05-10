package net.sf.regadb.util.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * Excel file r/w class.
 * 
 * @author plibin0
 */
public class ExcelTable {
	private HSSFWorkbook workBook;
	private HSSFSheet sheet;
	
	private int rows, columns;
	private SimpleDateFormat fullSimpleDateFormat;
	
	/**
	 * Create a new excel table, ready for reading or writing.
	 * If you want to read dates, you need to specify a date format.
	 * 
	 * @param fullDateFormat
	 */
	public ExcelTable(String fullDateFormat) {
		fullSimpleDateFormat = fullDateFormat != null ? new SimpleDateFormat(fullDateFormat) : null;
	}

	public void loadFile(File f) throws IOException {
		InputStream excelFIS = new FileInputStream(f);
		workBook = new HSSFWorkbook(excelFIS);
		sheet = workBook.getSheetAt(0);

		for (int r = 0; r < sheet.getPhysicalNumberOfRows(); r++) {
			HSSFRow hssfRow = sheet.getRow(r);
			if (hssfRow != null) {
				rows++;
				int tmpCellCount = 0;
				for (int c = 0; c < hssfRow.getPhysicalNumberOfCells(); c++) {
					HSSFCell cell = hssfRow.getCell(c);
					if (cell != null) {
						tmpCellCount++;
					}
				}
				columns = Math.max(columns, tmpCellCount);
			}
		}
	}

	public void create() {
		workBook = new HSSFWorkbook();
		sheet = workBook.createSheet();
	}

	public void writeAndFlush(File f) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(f);
		workBook.write(fileOut);
		fileOut.close();
	}
	
	public void writeAndFlush(OutputStream os) throws IOException {
		workBook.write(os);
		os.close();
	}

	public String getCell(int row, int col) {
		HSSFCell cell = sheet.getRow(row).getCell(col);
		
		if (cell == null) {
			return "";
		}

		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			return cell.getRichStringCellValue().getString();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell))
				return fullSimpleDateFormat.format(cell.getDateCellValue());
			else {
				double value = cell.getNumericCellValue();
				long iValue = (long)value;
				value = (double)iValue;
				if (value == cell.getNumericCellValue())
					return iValue + "";
				else
					return value + "";
			}
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue()+"";
		} else {
			return "";
		}
	}

	public void setCell(int row, int col, String value) {
		if (value == null)
			return;
		
		if (sheet.getRow(row) == null) {
			sheet.createRow(row);
			sheet.getRow(row).createCell(col).setCellValue(new HSSFRichTextString(value));
			rows++;
		} else {
			sheet.getRow(row).createCell(col).setCellValue(new HSSFRichTextString(value));
		}
	}

	public void setCell(int row, int col, Integer value) {
		if (value == null)
			return;
		
		if (sheet.getRow(row) == null) {
			sheet.createRow(row);
			sheet.getRow(row).createCell(col).setCellValue(value);
			rows++;
		} else {
			sheet.getRow(row).createCell(col).setCellValue(value);
		}
	}

	public void setCell(int row, int col, Object value) {
		if (value instanceof Integer)
			setCell(row, col, (Integer) value);
		else
			setCell(row, col, (String) value);
	}

	public int columnCount() {
		return columns;
	}

	public int rowCount() {
		return rows;
	}
}
